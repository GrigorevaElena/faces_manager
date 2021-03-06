package grigoreva.facesmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import grigoreva.facesmanager.bl.FaceUtil;
import grigoreva.facesmanager.data.command.FindFaceByModel;
import grigoreva.facesmanager.data.command.SaveNewPersonCommand;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.data.greendao.PhotoLandmark;
import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;
import grigoreva.facesmanager.data.loader.PersonListLoader;
import grigoreva.facesmanager.event.FaceWasFoundEvent;
import grigoreva.facesmanager.event.MessageEvent;
import grigoreva.facesmanager.event.SavePersonEvent;

/**
 * Created by Лена on 27.05.2017.
 */
public class LoadFaceActivity extends AppCompatActivity {
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 200;
    private static final int REQUEST = 1;

    private Button mButton;
    private ImageView mImageView;

    private Paint mRectPaint;
    private Paint mPointPainter;
    private Canvas mCanvas;
    private Canvas mNormCanvas;
    private Bitmap mImage;

    private ProcessingState mProcessingState = ProcessingState.START;
    private ExecutorService mService;
    private FaceDetector mFaceDetector;
    private SparseArray<Face> mFaces;
    private ImageView mNormalImageView;
    private PersonPhoto mPhoto;

    private enum ProcessingState {
        START(0), PHOTO_LOADED(1), LANDMARKS_FOUND(2),
        NORMALISED(3), COLOR_CALCULATED(4), FACE_FOUND(5),
        ADD_NEW_FACE(6);

        private final int mValue;

        ProcessingState(int value) {
            mValue = value;
        }

        static ProcessingState fromValue(int value) {
            for (ProcessingState state : ProcessingState.values()) {
                if (state.mValue == value) {
                    return state;
                }
            }
            return START; //зацикливаем
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO запретить повоторы!
        setContentView(R.layout.activity_load_photo);
        mButton = (Button) findViewById(R.id.photo_action_btn);
        assert mButton != null;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAction();
            }
        });
        mImageView = (ImageView) findViewById(R.id.loaded_photo_view);
        mNormalImageView = (ImageView) findViewById(R.id.norm_image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (mProcessingState == ProcessingState.START ||
                         mProcessingState == ProcessingState.PHOTO_LOADED) {
                     imageClickAction();
                 }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFaceDetector();
        createPainters();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFaceDetector != null) {
            mFaceDetector.release();
        }
    }

    private void performAction() {
        mButton.setEnabled(false); // блокируем переход
        switch (mProcessingState) {
            case START:
                imageClickAction();
                break;
            case PHOTO_LOADED:
                findFaceWithLandmarks();
                break;
            case LANDMARKS_FOUND:
                normalizeIfNeed();
                break;
            case NORMALISED:
                //findAvgColor(); //модификация
                //addFaceInDB();
                findFaceInDB();
                break;
            case COLOR_CALCULATED:
                findFaceInDB();
                break;
            case FACE_FOUND:
                showMoreInfoAboutPerson();
                break;
            case ADD_NEW_FACE:
                addFaceInDB();
                break;
        }
    }

    private void imageClickAction() {
        if (mImage == null) {
            //открыть галерею
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, REQUEST);
        } else {
            rotateImage(mImage);
        }
    }

    private void rotateImage(@NonNull Bitmap image) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90, (float)(image.getWidth()/2), (float)(image.getHeight()/2));
        mImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
        mImageView.setImageBitmap(mImage);
    }

    private void showMoreInfoAboutPerson() {
        // Отобразить после нажатия на кнопку (под ней) вью с подробной информацией о персоне
        //...
    }

    private void findFaceInDB() {
        // Запрос к БД по данным составленной модели, в нем же - вызов методов алгоритма из FaceUtils
        // показать прогресс загрузки
        if (mService == null) {
            mService = Executors.newFixedThreadPool(3);
        }
        mService.execute(new FindFaceByModel(getApplicationContext(), mPhoto));
    }

    private void addFaceInDB() {
        // Открыть окно ввода персональных данных, прокинуть туда объект фото
        // к текущему экрану можно вернуться только по "Отмена"
        Person person = new Person();
        person.setName("Елена");
        person.setSurname("Григорьева");
        person.setIsContact(true);
        if (mService == null) {
            mService = Executors.newFixedThreadPool(3);
        }
        mService.execute(new SaveNewPersonCommand(getApplicationContext(), person, mPhoto));
    }

    private void findFaceWithLandmarks() {
        // работа с face detector
        if (canDetect()) {
            Frame frame = new Frame.Builder().setBitmap(mImage).build();
            mFaces = mFaceDetector.detect(frame);
            showOriginalLandmarks(mFaces);
            actionPerformed(true);
        }
    }

    private void showOriginalLandmarks(@NonNull SparseArray<Face> faces) {
        Bitmap tempBitmap = Bitmap.createBitmap(mImage.getWidth(), mImage.getHeight(), Bitmap.Config.RGB_565);
        mCanvas = new Canvas(tempBitmap);
        mCanvas.drawBitmap(mImage, 0, 0, null);

        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            mCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, mRectPaint);
            showMainFacePoints(thisFace);
            break; //TODO только 1 лицо!
        }
        mImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private void showMainFacePoints(@NonNull Face face) {
        List<Landmark> points = face.getLandmarks();
        for (Landmark mark : points) {
            mCanvas.drawPoint(mark.getPosition().x, mark.getPosition().y, mPointPainter);
        }
    }

    private void normalizeIfNeed() {
        //...
        mPhoto = FaceUtil.getNormalizeLandmarks(mFaces.valueAt(0));
        Bitmap tempBitmap = Bitmap.createBitmap(mPhoto.getNormalFaceWidth().intValue(),
                mPhoto.getNormalFaceHeight().intValue(), Bitmap.Config.RGB_565);
        mNormCanvas = new Canvas(tempBitmap);
        float x1 = 0;
        float y1 = 0;
        float x2 = mPhoto.getNormalFaceWidth();
        float y2 = mPhoto.getNormalFaceHeight();
        mNormCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, mRectPaint);
        for (PhotoLandmark mark : mPhoto.getLandmarkList().values()) {
            mNormCanvas.drawPoint(mark.getNormPointX(), mark.getNormPointY(), mPointPainter);
        }
        mNormalImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        actionPerformed(true);
    }

    private void findAvgColor() {
        //...
        getAvgColorBackground();
        //...
    }

    private void getAvgColorBackground() {
        // вычислить тон по RGB цвету
        //...
        Map<LandmarkType, PhotoLandmark> landmarkMap = mPhoto.getLandmarkList();
        PhotoLandmark leftCheek = landmarkMap.get(LandmarkType.LEFT_CHEEK);
        PhotoLandmark rightCheek = landmarkMap.get(LandmarkType.RIGHT_CHEEK);
        int pixelLeftCenter = mImage.getPixel(leftCheek.getPointX().intValue(), leftCheek.getPointY().intValue());
        int pixelRightCenter = mImage.getPixel(leftCheek.getPointX().intValue(), leftCheek.getPointY().intValue());

        int redValue = Color.red(pixelLeftCenter);
        int blueValue = Color.blue(pixelLeftCenter);
        int greenValue = Color.green(pixelLeftCenter);
        //todo преобразование к hsb
    }

    private void actionPerformed(boolean isSuccess) {
        if (isSuccess) {
            mProcessingState = ProcessingState.fromValue(mProcessingState.mValue + 1);
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка операции", Toast.LENGTH_SHORT).show();
        }
        mButton.setEnabled(true); // разблокируем
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImage = null;
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                mImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImageView.setImageBitmap(mImage);
            if (mImage != null) {
                mButton.setVisibility(View.VISIBLE);
            }
            actionPerformed(mImage != null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createPainters() {
        mRectPaint = new Paint();
        mRectPaint.setStrokeWidth(5);
        mRectPaint.setColor(Color.GREEN);
        mRectPaint.setStyle(Paint.Style.STROKE);

        mPointPainter = new Paint();
        mPointPainter.setStrokeWidth(10);
        mPointPainter.setColor(Color.RED);
        mPointPainter.setStyle(Paint.Style.FILL);
    }

    private void initFaceDetector() {
        mFaceDetector = new FaceDetector
                .Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
    }

    private boolean canDetect() {
        if (mFaceDetector == null || !mFaceDetector.isOperational()) {
            Toast.makeText(getApplicationContext(),
                    "Детектор лиц не доступен",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // регистрация приемника при старте фрагмента
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        // отписываемся от регистрации при закрытии фрагмента
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // В этом методе-колбэке мы получаем наши данные
    // (объект `event` типа класса-модели MessageEvent)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        // извлекаем из модели отправленную строку: event.message = "Hello everyone!"
        if (event instanceof FaceWasFoundEvent) {
            Person person = ((FaceWasFoundEvent) event).getData();
            if (person == null) {
                Toast.makeText(getApplicationContext(), "Сходства не найдены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Персона найдена. Нажмите ДАЛЕЕ чтобы посмотреть подробную информацию",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (event instanceof SavePersonEvent) {
            Toast.makeText(getApplicationContext(), ((SavePersonEvent)event).getData() ?
                    "Данные сохранены" : "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}
