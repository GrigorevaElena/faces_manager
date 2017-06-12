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

import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.data.greendao.PhotoLandmark;
import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;

/**
 * Created by Лена on 27.05.2017.
 */
public class FaceUtil {

    private static final float MAX_DIFF_VALUE = 5.0f;
    private static final float MIN_DIFF_X = 2.0f;
    private static final float MIN_DIFF_Y = MIN_DIFF_X;
    public static final int NORMAL_HEIGHT = 300;

    public static List<PersonPhoto> parseFacesModelList(@Nullable SparseArray<Face> faces) {
        if (faces == null || faces.size() == 0) {
            return new ArrayList<>();
        }
        List<PersonPhoto> persons = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            PersonPhoto person = new PersonPhoto();
            persons.add(person);
        }
        return persons;
    }

    private static void parseMainFacePoints(@NonNull Face face, @NonNull PersonPhoto person) {
        if (face == null) {
            return;
        }
        List<Landmark> points = face.getLandmarks();
        Map<String, PointF> pointsMap = new HashMap<>();
        for (Landmark mark : points) {
            pointsMap.put(String.valueOf(LandmarkType.fromValue(mark.getType())), mark.getPosition());
        }
        //person.setLandmarkList(pointsMap);
    }

    public static SparseArray<Face> getTestFaceList(float photoWidth, float photoHeight) {
        SparseArray<Face> faces = new SparseArray<>(1);
        Face face = getTestFace(photoWidth, photoHeight);
        faces.append(0, face);
        return faces;
    }

    public static boolean testComparison() {
        Face face1 = getTestFace(500, 600);
        Face face2 = getTestFace(510, 610);
        Face face3 = getTestFace(500, 600);//TODO
        Face face4 = getTestFace(300, 500);

        SparseArray<Face> faces = new SparseArray<>(1);
        faces.append(0, face1);
        faces.append(1, face2);
        faces.append(2, face3);
        //TODO! провести сравнение
        return false;
    }

    @NonNull
    private static Face getTestFace(float photoWidth, float photoHeight) {
        float yawDegrees = 0;// отклонение, наклон
        float rollDegrees = 0;// вращение, поворот
        Landmark[] landmarks = new Landmark[10];
        //губы?
        landmarks[0] = new Landmark(new PointF(photoWidth/2, photoHeight/2 + 10), Landmark.BOTTOM_MOUTH);
        landmarks[1] = new Landmark(new PointF(photoWidth/2 - 30, photoHeight/2), Landmark.LEFT_MOUTH);
        landmarks[2] = new Landmark(new PointF(photoWidth/2 + 30, photoHeight/2), Landmark.RIGHT_MOUTH);
        //нос
        landmarks[3] = new Landmark(new PointF(photoWidth/2, photoHeight/2 - 40), Landmark.NOSE_BASE);
        //глаза
        landmarks[4] = new Landmark(new PointF(photoWidth/2 - 35, photoHeight/2 - 90), Landmark.LEFT_EYE);
        landmarks[5] = new Landmark(new PointF(photoWidth/2 + 35, photoHeight/2 - 90), Landmark.RIGHT_EYE);
        //уши
        landmarks[6] = new Landmark(new PointF(photoWidth/2 - 70, photoHeight/2 - 60), Landmark.LEFT_EAR);
        landmarks[7] = new Landmark(new PointF(photoWidth/2 + 70, photoHeight/2 - 60), Landmark.RIGHT_EAR);
        //щеки
        landmarks[8] = new Landmark(new PointF(photoWidth/2 - 40, photoHeight/2 - 55), Landmark.LEFT_CHEEK);
        landmarks[9] = new Landmark(new PointF(photoWidth/2 + 40, photoHeight/2 - 55), Landmark.RIGHT_CHEEK);

        return new Face(0,
                new PointF(photoWidth/2, photoHeight/2),
                200,
                250,
                yawDegrees,
                rollDegrees,
                landmarks,
                20,
                40,
                1);
    }

    @NonNull
    public static PersonPhoto getNormalizeLandmarks(@NonNull Face face) {
        PersonPhoto photo = new PersonPhoto();

        float height = face.getHeight();
        float width = face.getWidth();

        photo.setFaceWidth(width);
        photo.setFaceHeight(height);

        Map<LandmarkType, PhotoLandmark> landmarks = new HashMap<>();

        if (height != NORMAL_HEIGHT) {
            float k = calcK(Math.max(width, height), NORMAL_HEIGHT);
            photo.setNormalFaceWidth(width / k);
            photo.setNormalFaceHeight(height / k);
            //TODO для нормализации остальных точек вычитаем из них левую нижнюю координату
            for (Landmark mark : face.getLandmarks()) {
                LandmarkType name = LandmarkType.fromValue(mark.getType());
                if (name == null) {
                    continue;
                }
                landmarks.put(name, getPoint(name, mark.getPosition(), face.getPosition().x, face.getPosition().y, k));
            }
        } else {
            photo.setNormalFaceWidth(width);
            photo.setNormalFaceHeight(height);
            // TODO просто заносим координаты в модель
            for (Landmark mark : face.getLandmarks()) {
                LandmarkType name = LandmarkType.fromValue(mark.getType());
                if (name == null) {
                    continue;
                }
                landmarks.put(name, getPoint(name, mark.getPosition()));
            }
        }
        photo.setLandmarkList(landmarks);

        return photo;
    }

    private static float calcK(float height, float normalHeight) {
        if (normalHeight == 0) {
            return height;
        }
        return height / normalHeight;
    }

    private static PointF normalization(float width, float height, float k) {
        return new PointF(width / k, height / k);
        //по сути вектор из начала координат в верхний правый угол или нормализованное окно, в котором находится лицо
    }

    private static PhotoLandmark getPoint(@NonNull LandmarkType type, @NonNull PointF normalPoint, float normalWidth, float normalHeight, float k) {
        PhotoLandmark landmark = new PhotoLandmark();
        landmark.setLandmarkType(type);
        landmark.setPointX(normalPoint.x);
        landmark.setPointY(normalPoint.y);
        normalPoint = normalization(normalPoint.x - normalWidth, normalPoint.y - normalHeight, k);
        landmark.setNormPointX(normalPoint.x);
        landmark.setNormPointY(normalPoint.y);
        return landmark;
    }

    private static PhotoLandmark getPoint(@NonNull LandmarkType type, @NonNull PointF normalPoint) {
        PhotoLandmark landmark = new PhotoLandmark();
        landmark.setLandmarkType(type);
        landmark.setPointX(normalPoint.x);
        landmark.setPointY(normalPoint.y);
        return landmark;
    }

    public static boolean compareModels(@NonNull PersonPhoto newPhoto, @NonNull PersonPhoto originalPhoto) {
        if (newPhoto.getLandmarkList() == null || newPhoto.getLandmarkList().isEmpty() ||
                originalPhoto.getLandmarkList() == null || originalPhoto.getLandmarkList().isEmpty()) {
            return newPhoto == originalPhoto;
        }
        return comparePointList(newPhoto.getLandmarkList(), originalPhoto.getLandmarkList());
    }

    private static boolean comparePointList(@NonNull Map<LandmarkType, PhotoLandmark> landmarkList,
                                            @NonNull Map<LandmarkType, PhotoLandmark> landmarkList1) {
        float diffValueX = 0;
        float diffValueY = 0;
        PhotoLandmark p1 = landmarkList.get(LandmarkType.BOTTOM_MOUTH);
        PhotoLandmark p2 = landmarkList1.get(LandmarkType.BOTTOM_MOUTH);
        if (!comparePoint(p1, p2)) {
            diffValueX += p1.getNormPointX() - p2.getNormPointX();
            diffValueY += p1.getNormPointY() - p2.getNormPointY();
        }
        p1 = landmarkList.get(LandmarkType.LEFT_EYE);
        p2 = landmarkList1.get(LandmarkType.LEFT_EYE);
        if (!comparePoint(p1, p2)) {
            diffValueX += p1.getNormPointX() - p2.getNormPointX();
            diffValueY += p1.getNormPointY() - p2.getNormPointY();
        }
        p1 = landmarkList.get(LandmarkType.RIGHT_EYE);
        p2 = landmarkList1.get(LandmarkType.RIGHT_EYE);
        if (!comparePoint(p1, p2)) {
            diffValueX += p1.getNormPointX() - p2.getNormPointX();
            diffValueY += p1.getNormPointY() - p2.getNormPointY();
        }
        p1 = landmarkList.get(LandmarkType.LEFT_CHEEK);
        p2 = landmarkList1.get(LandmarkType.LEFT_CHEEK);
        if (!comparePoint(p1, p2)) {
            diffValueX += p1.getNormPointX() - p2.getNormPointX();
            diffValueY += p1.getNormPointY() - p2.getNormPointY();
        }
        p1 = landmarkList.get(LandmarkType.RIGHT_CHEEK);
        p2 = landmarkList1.get(LandmarkType.RIGHT_CHEEK);
        if (!comparePoint(p1, p2)) {
            diffValueX += p1.getNormPointX() - p2.getNormPointX();
            diffValueY += p1.getNormPointY() - p2.getNormPointY();
        }
        p1 = landmarkList.get(LandmarkType.NOSE_BASE);
        p2 = landmarkList1.get(LandmarkType.NOSE_BASE);
        if (!comparePoint(p1, p2)) {
            diffValueX += p1.getNormPointX() - p2.getNormPointX();
            diffValueY += p1.getNormPointY() - p2.getNormPointY();
        }
        return diffValueX < MIN_DIFF_X && diffValueY < MIN_DIFF_Y;
    }

    private static boolean comparePoint(@NonNull PhotoLandmark photoLandmark, @NonNull PhotoLandmark photoLandmark1) {
        return comparePoint(photoLandmark.getNormPointX(), photoLandmark1.getNormPointX(),
                photoLandmark.getNormPointY(), photoLandmark1.getNormPointY());
    }

    private static boolean comparePoint(float x1, float x2, float y1, float y2) {
        if (x1 == x2 && y1 == y2) {
            return true;
        }
        if (Math.abs(x2 - x1) < MAX_DIFF_VALUE && Math.abs(y2 - y1) < MAX_DIFF_VALUE) {
            return true;
        }
        return false;
    }

    public static PersonPhoto findFace(List<PersonPhoto> personPhotos, PersonPhoto photo) {
        for(PersonPhoto personPhoto : personPhotos) {
            if (compareModels(photo, personPhoto)) {
                return personPhoto;
            }
        }
        return null;
    }
}
