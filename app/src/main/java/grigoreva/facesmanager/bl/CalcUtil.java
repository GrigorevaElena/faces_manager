package grigoreva.facesmanager.bl;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import grigoreva.facesmanager.data.greendao.PhotoLandmark;
import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;

/**
 * Created by Лена on 12.06.2017.
 */
public class CalcUtil {

    public static PointF calcMahalanobis(@NonNull Map<LandmarkType, PhotoLandmark> landmarkVect1,
                                        @NonNull Map<LandmarkType, PhotoLandmark> landmarkVect2) {
        if (landmarkVect2.isEmpty() || landmarkVect1.isEmpty()) {
            return new PointF(1000000, 1000000);
        }

        PointF[][] covMatr = calcCovMatrix(landmarkVect1);
        PointF[] vect = getVect(landmarkVect2.values());
        return scalVectMult(vect, miltiplyVectMatrix(vect, covMatr));
    }

    private static PointF[] getVect(@NonNull Collection<PhotoLandmark> values) {
        PointF[] vect = new PointF[values.size()];
        List<PointF> points = new ArrayList<>();
        for (PhotoLandmark landmark : values) {
            points.add(new PointF(landmark.getNormPointX(), landmark.getNormPointY()));
        }
        points.toArray(vect);
        return vect;
    }

    /**
     * Преобразование в список векторов
     *
     * @param landmarkListVect - список локальных моделей, модель состоит из списка точек
     * @return инвертированный список (элемент списка есть набор возможных значений точки - тоже список)
     */
    public static List<List<PointF>> convertToVectList(@Nullable List<Map<LandmarkType, PhotoLandmark>> landmarkListVect) {
        if (landmarkListVect == null || landmarkListVect.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<PointF>> mList = new ArrayList<>(8);
        mList.add(getLandmarkListByType(LandmarkType.BOTTOM_MOUTH, landmarkListVect));
        mList.add(getLandmarkListByType(LandmarkType.LEFT_MOUTH, landmarkListVect));
        mList.add(getLandmarkListByType(LandmarkType.RIGHT_MOUTH, landmarkListVect));

        mList.add(getLandmarkListByType(LandmarkType.NOSE_BASE, landmarkListVect));

        mList.add(getLandmarkListByType(LandmarkType.LEFT_CHEEK, landmarkListVect));
        mList.add(getLandmarkListByType(LandmarkType.RIGHT_CHEEK, landmarkListVect));

        mList.add(getLandmarkListByType(LandmarkType.LEFT_EYE, landmarkListVect));
        mList.add(getLandmarkListByType(LandmarkType.RIGHT_EYE, landmarkListVect));
        return mList;
    }

    private static List<PointF> getLandmarkListByType(@NonNull LandmarkType type,
                                                      List<Map<LandmarkType, PhotoLandmark>> landmarkVectList) {
        List<PointF> list = new ArrayList<>();
        for (Map<LandmarkType, PhotoLandmark> map : landmarkVectList) {
            PhotoLandmark landmark = map.get(type);
            if (landmark != null) {
                list.add(new PointF(landmark.getNormPointX(), landmark.getNormPointY()));
            }
        }
        return list;
    }

    /**
     * Скалярное произведение векторов
     * @param vect1
     * @param vect2
     * @return
     */
    private static PointF scalVectMult(PointF[] vect1, PointF[] vect2) {
        PointF pointF = new PointF(0, 0);

        for (int i = 0; i < 8; i++) {
            pointF.x += vect1[i].x * vect2[i].x;
            pointF.y += vect1[i].y * vect2[i].y;
        }
        return new PointF((float) Math.pow(pointF.x, 0.5), (float) Math.pow(pointF.y, 0.5));
    }

    /**
     * Умножение вектора на матрицу
     * @param vect
     * @param matrix
     * @return
     */
    private static PointF[] miltiplyVectMatrix(PointF[] vect, PointF[][] matrix) {
        PointF[] result = new PointF[8];

        for (int i = 0; i < 8; i++) {
            PointF pointF = new PointF(0, 0);
            for (int j = 0; j < 8; j++) {
                pointF.x += (vect[j].x * matrix[i][j].x);
                pointF.y += (vect[j].y * matrix[i][j].y);
            }
            result[i] = pointF;
        }
        return result;
    }

    /**
     * Построение матрицы ковариации
     * @param landmarkVectList
     * @return
     */
    private static PointF[][] calcCovMatrix(@Nullable Map<LandmarkType, PhotoLandmark> landmarkVectList) {
        PointF[][] covMatrix = new PointF[8][8];
        if (landmarkVectList == null || landmarkVectList.isEmpty()) {
            return covMatrix;
        }
        PointF[] matList = new PointF[landmarkVectList.values().size()];
        Collection<PhotoLandmark> landmarks = landmarkVectList.values();
        List<PointF> pointFs = new ArrayList<>();
        for (PhotoLandmark landmark : landmarks) {
            pointFs.add(new PointF(landmark.getNormPointX(), landmark.getNormPointY()));
        }
        pointFs.toArray(matList);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == j) {
                    covMatrix[i][j] = cacSinglDisp(matList);
                } else {
                    covMatrix[i][j] = calcCov(matList, matList, matList[i], matList[j]);
                }
            }
        }
        return covMatrix;
    }

    /**
     * @param var1 список значений точки 1
     * @param var2 список значений точки 2
     * @param m1   матожидание точки 1
     * @param m2   матожидание точки 2
     * @return ковариация точек 1 и 2
     */
    public static PointF calcCov(PointF[] var1, PointF[] var2, PointF m1, PointF m2) {
        float covX = 0.0f;
        float covY = 0.0f;
        for (int i = 0; i < var1.length; i++) {
            covX += (var1[i].x - m1.x) * (var2[i].x - m2.x);
            covY += (var1[i].y - m1.y) * (var2[i].y - m2.y);
        }

        return new PointF(covX / var1.length, covY / var1.length);
    }

    /**
     * Дисперсия
     *
     * @param points - все возможные значения точки в обучающей выборке
     * @return дисперсия точки
     */
    public static PointF cacSinglDisp(PointF[] points) {
        PointF avgPoint = getAvgValue(points);
        PointF avgSquarePoint = getAvgSquareValue(points);
        return new PointF(avgSquarePoint.x - avgPoint.x, avgSquarePoint.y - avgPoint.y);
    }

    /**
     * Вычисление средней точки (ее матожидание)
     *
     * @param points - все возможные значения точки в обучающей выборке
     * @return
     */
    public static PointF getAvgValue(PointF[] points) {
        if (points == null || points.length == 0) {
            return new PointF(0, 0);
        }
        float sumX = 0.0f;
        float sumY = 0.0f;

        for (PointF point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        return new PointF(sumX / points.length, sumY / points.length);
    }

    /**
     * Вычисление квадрата матожидания точки
     *
     * @param points - все возможные значения точки в обучающей выборке
     * @return квадрат матожидания точки вектора признаков
     */
    private static PointF getAvgSquareValue(PointF[] points) {
        if (points == null || points.length == 0) {
            return new PointF(0, 0);
        }
        float sumX = 0.0f;
        float sumY = 0.0f;

        for (PointF point : points) {
            sumX += point.x * point.x;
            sumY += point.y * point.y;
        }
        return new PointF(sumX / points.length, sumY / points.length);
    }
}
