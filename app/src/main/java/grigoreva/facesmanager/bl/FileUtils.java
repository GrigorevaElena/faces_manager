package grigoreva.facesmanager.bl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by админ2 on 19.03.2017.
 */
public class FileUtils {
    public static File saveImage(byte[] bytes, String name)throws IOException{
        File file = new File(name + "_" + System.currentTimeMillis() + ".jpg");
        //TODO сохранение файла
        return file;
    }


    public static Bitmap getBitmapByPath(String photoPath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 300,
                300);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
