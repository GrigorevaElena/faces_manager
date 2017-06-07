package grigoreva.facesmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Лена on 27.05.2017.
 */
public class LoadFaceActivity extends AppCompatActivity {
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 200;
    private static final int REQUEST = 1;
    private ImageView mImageView;
    private Bitmap mImage;
    private Button mButton;
    private ProcessingState mProcessingState = ProcessingState.START;

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
        //...
    }

    private void addFaceInDB() {
        // Открыть окно ввода персональных данных, прокинуть туда объект фото
        // к текущему экрану можно вернуться только по "Отмена"
        //...
    }

    private void findFaceWithLandmarks() {
        // работа с face detector
        //...
    }

    private void normalizeIfNeed() {
        //...
    }

    private void findAvgColor() {
        //...
        getAvgColorBackground();
        //...
    }

    private void getAvgColorBackground() {
        // вычислить тон по RGB цвету
        //...
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
}
