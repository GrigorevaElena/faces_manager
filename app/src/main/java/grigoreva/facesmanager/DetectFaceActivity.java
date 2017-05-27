package grigoreva.facesmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grigoreva.facesmanager.data.greendao.Photo;

/**
 * Created by админ2 on 04.05.2017.
 */
public class DetectFaceActivity extends AppCompatActivity {
    private ImageView mImageView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Bitmap mBitmap;
    private FaceDetector mFaceDetector;
    private Canvas mCanvas;
    private Paint mRectPaint;
    private BitmapFactory.Options mOptions;
    private int mCurrentImagePosition;
    private Paint mPointPainter;

    private SparseArray<Face> mFaces;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_face);
        mImageView = (ImageView) findViewById(R.id.face_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateTestImage();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        initDetector();
        createPainters();
        //createOptions();

        setTestImage(R.drawable.test1);
    }

    private void updateTestImage() {
        mCurrentImagePosition++;
        @DrawableRes
        int resId;
        switch (mCurrentImagePosition) {
            case 1:
                resId = R.drawable.test1;
                break;
            case 2:
                resId = R.drawable.test2;
                break;
            case 3:
                resId = R.drawable.test3;
                break;
            case 4:
                resId = R.drawable.test4;
                break;
            case 5:
                resId = R.drawable.test5;
                break;
            case 6:
                resId = R.drawable.test6;
                break;
            case 7:
                resId = R.drawable.test7;
                break;
            case 8:
                resId = R.drawable.test8;
                break;
            case 9:
                resId = R.drawable.test9;
                break;
            case 10:
                resId = R.drawable.test10;
                break;
            case 11:
                resId = R.drawable.test11;
                break;
            case 12:
                resId = R.drawable.test12;
                break;
            case 13:
                resId = R.drawable.test13;
                break;
            case 14:
                resId = R.drawable.test14;
                break;
            case 15:
                resId = R.drawable.test15;
                break;
            case 16:
                resId = R.drawable.test16;
                break;
            case 17:
                resId = R.drawable.test17;
                break;
            case 18:
                resId = R.drawable.test18;
                break;
            case 19:
                resId = R.drawable.test19;
                break;
            case 20:
                resId = R.drawable.test20;
                break;
            case 21:
                resId = R.drawable.test21;
                break;
            case 22:
                resId = R.drawable.test22;
                break;
            case 23:
                mCurrentImagePosition = 1;
            default:
                resId = R.drawable.test1;
                break;
        }
        setTestImage(resId);
        createFaceBorder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFaceDetector != null) {
            mFaceDetector.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createFaceBorder();
    }

    private void initDetector() {
        //TODO нет отслеживания, т.к. работаем с готовым изоражением
        mFaceDetector = new FaceDetector
                .Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
    }

    private void setTestImage(@DrawableRes int resId) {
        mBitmap = null;
        //http://misseva.ru/uploads/images/lyudi-ochen-raznye.jpg
        //http://www.cablook.com/wp-content/uploads/2014/10/139.jpg
       /* URL url = null;
        try {
            url = new URL("http://misseva.ru/uploads/images/lyudi-ochen-raznye.jpg");
            mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }*/

        /*mBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                resId,
                mOptions);*/
        mImageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
    }

/*    private void createOptions() {
        mOptions = new BitmapFactory.Options();
        mOptions.inMutable = true;
    }*/

    private void createFaceBorder() {
        if (mFaceDetector == null || !mFaceDetector.isOperational()) {
            Toast.makeText(getApplicationContext(), "Could not set up the face detector!", Toast.LENGTH_SHORT).show();
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(mBitmap).build();
        mFaces = mFaceDetector.detect(frame);
    }

    private void showOriginalLandmarks(@NonNull SparseArray<Face> faces) {
        Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.RGB_565);
        mCanvas = new Canvas(tempBitmap);
        mCanvas.drawBitmap(mBitmap, 0, 0, null);

        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            mCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, mRectPaint);
            showMainFacePoints(thisFace);
        }
        mImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    enum PointsName {
        BOTTOM_MOUTH(0), LEFT_CHEEK(1), LEFT_EAR_TIP(2),
        LEFT_EAR(3), LEFT_EYE(4), LEFT_MOUTH(5),
        NOSE_BASE(6), RIGHT_CHEEK(7), RIGHT_EAR_TIP(8),
        RIGHT_EAR(9), RIGHT_EYE(10), RIGHT_MOUTH(11);

        int value;

        PointsName(int i) {
            value = i;
        }

        @Nullable
        public static PointsName fromValue(int val) {
            for (PointsName name : PointsName.values()) {
                if (val == name.value) {
                    return name;
                }
            }
            return null;
        }
    }

    @NonNull
    private Photo getNormalizeLandmarks(@NonNull Face face) {
        Photo photo = new Photo();

        Map<PointsName, PointF> pointsMap = getLandmarkPointsMap(face);

        float height = face.getHeight();
        float width = face.getWidth();
        Log.d("FaceDetector", "Face height: " + height + " and width: " + width);
        if (height != 150) {
            float k = calcK(Math.max(width, height), 150);
            PointF normalPoint = normalization(width, height, k);
            Log.d("FaceDetector", "Normal face height: " + height + " and width: " + width);
            //TODO для нормализации остальных точек вычитаем из них левую нижнюю координату
            normalPoint = pointsMap.get(PointsName.LEFT_EYE);
            if (normalPoint != null) {
                normalPoint = normalization(normalPoint.x - width, normalPoint.y - height, k);
                photo.setLeftEyesX(normalPoint.x);
                photo.setLeftEyesY(normalPoint.y);
            }
            normalPoint = pointsMap.get(PointsName.RIGHT_EYE);
            if (normalPoint != null) {
                normalPoint = normalization(normalPoint.x - width, normalPoint.y - height, k);
                photo.setRightEyesX(normalPoint.x);
                photo.setRightEyesY(normalPoint.y);
            }
            normalPoint = pointsMap.get(PointsName.NOSE_BASE);
            if (normalPoint != null) {
                normalPoint = normalization(normalPoint.x - width, normalPoint.y - height, k);
                photo.setNoiseX(normalPoint.x);
                photo.setNoiseY(normalPoint.y);
            }
            //TODO и тд
        } else {
            // TODO просто заносим координаты в модель
            PointF normalPoint = pointsMap.get(PointsName.LEFT_EYE);
            if (normalPoint != null) {
                photo.setLeftEyesX(normalPoint.x);
                photo.setLeftEyesY(normalPoint.y);
            }
            normalPoint = pointsMap.get(PointsName.RIGHT_EYE);
            if (normalPoint != null) {
                photo.setRightEyesX(normalPoint.x);
                photo.setRightEyesY(normalPoint.y);
            }
            normalPoint = pointsMap.get(PointsName.NOSE_BASE);
            if (normalPoint != null) {
                photo.setNoiseX(normalPoint.x);
                photo.setNoiseY(normalPoint.y);
            }
        }

        return photo;
    }

    //TODO можно нормализовать и выводить без объекта - в мапе, хранить в бд тоже списком точек с кастомным типом
    //хранить с точкой идентификатор лица, в запросе возвращать лица (вложенный, если получится),
    // возвращать с каждым фото список контрольных точек

    private Map<PointsName, PointF> getLandmarkPointsMap(@NonNull Face face) {
        Map<PointsName, PointF> pointsMap = new HashMap<>(face.getLandmarks().size());
        //TODO оптимизировать в 1 проход ВАЖНО!
        //можно определять, что за точка при парсинге типа
        for (Landmark mark : face.getLandmarks()) {
            PointsName name = PointsName.fromValue(mark.getType());
            if (name == null) {
                continue;
            }
            pointsMap.put(name, new PointF(mark.getPosition().x, mark.getPosition().y));
        }
        return pointsMap;
    }

    private void DrawNormalizeLandmarks(@NonNull Photo photo, @NonNull PointF size) {
        float offset = (float) 5.0;
        Bitmap tempBitmap = Bitmap.createBitmap((int) (size.x + 2 * offset),
                (int) (size.y + 2 * offset), Bitmap.Config.RGB_565);
        mCanvas = new Canvas(tempBitmap);
        mCanvas.drawRoundRect(new RectF(offset, offset, size.x + offset, size.y + offset), 2, 2, mRectPaint);

        mCanvas.drawPoint(photo.getLeftEyesX(), photo.getLeftEyesY(), mPointPainter);
        mCanvas.drawPoint(photo.getNoiseX(), photo.getNoiseY(), mPointPainter);
        mCanvas.drawPoint(photo.getRightEyesX(), photo.getRightEyesY(), mPointPainter);

        mImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    private float calcK(float height, float normalHeight) {
        //TODO запретить передавать 0 вторым коэффициентом!
        return height / normalHeight;
    }

    private PointF normalization(float width, float height, float k) {
        return new PointF(width * k, height * k);
        //по сути вектор из начала координат в верхний правый угол или нормализованное окно, в котором находится лицо
    }

    private void showMainFacePoints(Face face) {
        if (face == null) {
            return;
        }
        List<Landmark> points = face.getLandmarks();
        for (Landmark mark : points) {
            mCanvas.drawPoint(mark.getPosition().x, mark.getPosition().y, mPointPainter);
        }
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
}
