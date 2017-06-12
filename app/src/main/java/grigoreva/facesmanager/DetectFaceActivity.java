package grigoreva.facesmanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import grigoreva.facesmanager.bl.FaceUtil;
import grigoreva.facesmanager.bl.FileUtils;
import grigoreva.facesmanager.data.command.FindFaceByModel;
import grigoreva.facesmanager.data.command.SaveNewPersonCommand;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.data.greendao.PhotoLandmark;
import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;
import grigoreva.facesmanager.event.FaceWasFoundEvent;
import grigoreva.facesmanager.event.MessageEvent;
import grigoreva.facesmanager.event.SavePersonEvent;
import grigoreva.facesmanager.fragment.dialog.AddPersonDialogFragment;

/**
 * Created by Лена on 27.05.2017.
 */
public class DetectFaceActivity extends AppCompatActivity implements
        AddPersonDialogFragment.AddPersonInterface {
    public static final String ADD_NEW = DetectFaceActivity.class.getCanonicalName() + ".add_mew_person_mode";
    private static final int REQUEST = 1;

    private Button mButton;
    private ImageView mImageView;

    private Paint mRectPaint;
    private Paint mPointPainter;
    private Canvas mCanvas;
    private Canvas mNormCanvas;
    private Bitmap mImage;

    private ProcessingState mProcessingState = ProcessingState.START;
    private boolean mIsAddNewPersonMode;

    private ExecutorService mService;
    private FaceDetector mFaceDetector;
    private SparseArray<Face> mFaces;
    private ImageView mNormalImageView;
    private RelativeLayout mFoundFaceView;

    private PersonPhoto mPhoto;
    private Person mPerson;
    private Uri mSelectedImage;
    private TextView mFoundPersonName;
    private TextView mFoundPersonIsContact;
    private ImageView mFoundPersonPhoto;

    private enum ProcessingState {
        START(0), PHOTO_LOADED(1), LANDMARKS_FOUND(2),
        NORMALISED(3), COLOR_CALCULATED(4), FACE_FOUND(5),
        ADD_NEW_FACE(6), END(7);

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
        setContentView(R.layout.activity_detect_face);
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
        mFoundFaceView = (RelativeLayout) findViewById(R.id.face_data_container);
        mFoundPersonName = (TextView) findViewById(R.id.name);
        mFoundPersonIsContact = (TextView) findViewById(R.id.status);
        mFoundPersonPhoto = (ImageView) findViewById(R.id.main_photo);
        assert mFoundFaceView != null;
        mFoundFaceView.setVisibility(View.GONE);
        mIsAddNewPersonMode = getIntent().getBooleanExtra(ADD_NEW, false);
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

    @Override
    public void savePerson(String surname, String name, boolean isContact) {
        Person person = new Person();
        person.setName(name);
        person.setSurname(surname);
        person.setIsContact(isContact);
        if (mService == null) {
            mService = Executors.newFixedThreadPool(3);
        }
        mService.execute(new SaveNewPersonCommand(getApplicationContext(), person, mPhoto));
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
                findAvgColor(); //модификация
                break;
            case COLOR_CALCULATED:
                if (mIsAddNewPersonMode) {
                    addFaceInDB();
                } else {
                    findFaceInDB();
                }
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
        matrix.postRotate(90, (float) (image.getWidth() / 2), (float) (image.getHeight() / 2));
        mImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
        mImageView.setImageBitmap(mImage);
    }

    private void showMoreInfoAboutPerson() {
        // Отобразить после нажатия на кнопку (под ней) вью с подробной информацией о персоне
        //...
        mFoundPersonName.setText(String.format("%s", mPerson.getName()));
        mFoundPersonIsContact.setText(mPerson.getIsContact() ? "Из списка контактов" : "Из приложения");
        mFoundPersonPhoto.setImageBitmap(FileUtils.getBitmapByPath(mPerson.getMainPhoto()));
        mFoundFaceView.setVisibility(View.VISIBLE);
    }

    private void findFaceInDB() {
        // Запрос к БД по данным составленной модели, в нем же - вызов методов алгоритма из FaceUtils
        // показать прогресс загрузки
        if (mPhoto.getLandmarkList().values().size() < 8) {
            Toast.makeText(getApplicationContext(), "Недостаточно точек для поиска", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mService == null) {
            mService = Executors.newFixedThreadPool(3);
        }
        mService.execute(new FindFaceByModel(getApplicationContext(), mPhoto));
    }

    private void addFaceInDB() {
        if (mPhoto.getLandmarkList().values().size() < 8) {
            Toast.makeText(getApplicationContext(), "Недостаточно точек для формирования модели", Toast.LENGTH_SHORT).show();
            return;
        }
        AddPersonDialogFragment dialogFragment = new AddPersonDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), AddPersonDialogFragment.class.getCanonicalName());
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
        //todo
        mPhoto.setPhotoUrl(getRealPathFromURI(mSelectedImage));
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
        //mNormalImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        actionPerformed(true);
    }

    private void findAvgColor() {
        mPhoto.setAvgFaceColor(getAvgColorBackground());
        actionPerformed(true);
    }

    private int getAvgColorBackground() {
        // вычислить тон по RGB цвету
        Map<LandmarkType, PhotoLandmark> landmarkMap = mPhoto.getLandmarkList();
        PhotoLandmark leftCheek = landmarkMap.get(LandmarkType.LEFT_CHEEK);
        PhotoLandmark rightCheek = landmarkMap.get(LandmarkType.RIGHT_CHEEK);
        int leftX = leftCheek.getPointX().intValue();
        int leftY = leftCheek.getPointY().intValue();
        int rightX = rightCheek.getPointX().intValue();
        int rightY = rightCheek.getPointY().intValue();

        int avgH = 0;

        //todo преобразование к hsb
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                avgH += getHForPixel(mImage.getPixel(leftX + i - 1, leftY + j - 1));
                avgH += getHForPixel(mImage.getPixel(rightX + i - 1, rightY + j - 1));
            }
        }
        Toast.makeText(getApplicationContext(), "Значение среднего цвета: " + avgH/18, Toast.LENGTH_SHORT).show();
        return avgH / 18;
    }

    private int getHForPixel(int pixel) {
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);

        int max = Math.max(Math.max(redValue, blueValue), greenValue);
        int min = Math.min(Math.min(redValue, blueValue), greenValue);

        if (max == min) {
            return 0;
        }

        if (max == redValue && greenValue >= blueValue) {
            return 60 * (greenValue - blueValue) / (max - min);
        }
        else if (max == redValue && greenValue < blueValue) {
            return 60 * (greenValue - blueValue) / (max - min) + 360;
        }
        else if (max == greenValue) {
            return 60 * (blueValue - redValue) / (max - min) + 120;
        }
       else if (max == blueValue) {
            return 60 * (redValue - greenValue) / (max - min) + 240;
        }

        return 0;
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
            mSelectedImage = data.getData();
            try {
                mImage = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedImage);
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

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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
    public void onEvent(MessageEvent event) {
        // извлекаем из модели отправленную строку: event.message = "Hello everyone!"
        if (event instanceof FaceWasFoundEvent) {
            mPerson = ((FaceWasFoundEvent) event).getData();
            if (mPerson == null) {
                Toast.makeText(getApplicationContext(), "Сходства не найдены", Toast.LENGTH_SHORT).show();
                mProcessingState = ProcessingState.END;
            } else {
                Toast.makeText(getApplicationContext(),
                        "Персона найдена. Нажмите ДАЛЕЕ чтобы посмотреть подробную информацию",
                        Toast.LENGTH_SHORT).show();
                actionPerformed(true);
                mProcessingState = ProcessingState.FACE_FOUND;
            }
        }
        if (event instanceof SavePersonEvent) {
            boolean isSuccess = ((SavePersonEvent) event).getData();
            Toast.makeText(getApplicationContext(), isSuccess ?
                    "Данные сохранены" : "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            if (isSuccess) {
                finish();
            }
            mProcessingState = ProcessingState.END;
        }
    }
}
