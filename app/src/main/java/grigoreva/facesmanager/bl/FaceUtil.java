package grigoreva.facesmanager.bl;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grigoreva.facesmanager.data.greendao.Photo;

/**
 * Created by Лена on 27.05.2017.
 */
public class FaceUtil {

    public enum LandmarkType {
        BOTTOM_MOUTH(0),
        LEFT_CHEEK(1),
        LEFT_EAR_TIP(2),
        LEFT_EAR(3),
        LEFT_EYE(4),
        LEFT_MOUTH(5),
        NOSE_BASE(6),
        RIGHT_CHEEK(7),
        RIGHT_EAR_TIP(8),
        RIGHT_EAR(9),
        RIGHT_EYE(10),
        RIGHT_MOUTH(11);

        private int mValue;

        LandmarkType(int value) {
            mValue = value;
        }

        @Nullable
        public static LandmarkType fromValue(int value) {
            for (LandmarkType type : LandmarkType.values()) {
                if (value == type.mValue) {
                    return type;
                }
            }
            return null;
        }
    }

    public static List<Photo> parseFacesModelList(@Nullable SparseArray<Face> faces) {
        if (faces == null || faces.size() == 0) {
            return new ArrayList<>();
        }
        List<Photo> persons = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            Photo person = new Photo();
            persons.add(person);
        }
        return persons;
    }

    private static void parseMainFacePoints(@NonNull Face face, @NonNull Photo person) {
        if (face == null) {
            return;
        }
        List<Landmark> points = face.getLandmarks();
        Map<String, PointF> pointsMap = new HashMap<>();
        for (Landmark mark : points) {
            pointsMap.put(String.valueOf(LandmarkType.fromValue(mark.getType())), mark.getPosition());
        }
        //todo person.setPointsMap();
    }
}
