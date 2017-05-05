package grigoreva.facesmanager.bl;

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
}
