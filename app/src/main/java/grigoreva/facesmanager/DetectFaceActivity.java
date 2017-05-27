package grigoreva.facesmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;
import java.util.List;

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
    private int mImageCount = 21;
    private int mCurrentImagePosition;
    private Paint mPointPainter;

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
    protected void onResume() {
        super.onResume();
        createPainter();
        createOptions();
        //TODO нет отслеживания, т.к. работаем с готовым изоражением
        mFaceDetector = new FaceDetector
                .Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        createFaceBorder();
    }

    private void setTestImage(@DrawableRes int resId) {
        mBitmap = null;
        mBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                resId,
                mOptions);
        mImageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
    }

    private void createOptions() {
        mOptions = new BitmapFactory.Options();
        mOptions.outHeight=200;
        mOptions.outWidth=200;
        mOptions.inMutable=true;
    }

    private void createFaceBorder() {
        Bitmap tempBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.RGB_565);
        mCanvas = new Canvas(tempBitmap);
        mCanvas.drawBitmap(mBitmap, 0, 0, null);

        if (mFaceDetector == null || !mFaceDetector.isOperational()) {
            Toast.makeText(getApplicationContext(), "Could not set up the face detector!", Toast.LENGTH_SHORT).show();
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(mBitmap).build();
        SparseArray<Face> faces = mFaceDetector.detect(frame);
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

    private void showMainFacePoints(Face face) {
        if (face == null) {
            return;
        }
        List<Landmark> points = face.getLandmarks();
        for (Landmark mark : points) {
            mCanvas.drawPoint(mark.getPosition().x, mark.getPosition().y, mPointPainter);
        }
    }

    private void createPainter() {
        mRectPaint = new Paint();
        mRectPaint.setStrokeWidth(5);
        mRectPaint.setColor(Color.GREEN);
        mRectPaint.setStyle(Paint.Style.STROKE);

        mPointPainter = new Paint();
        mPointPainter.setStrokeWidth(8);
        mPointPainter.setColor(Color.BLUE);
        mPointPainter.setStyle(Paint.Style.FILL);
    }
}
