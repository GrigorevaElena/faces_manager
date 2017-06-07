package grigoreva.facesmanager.data.greendao.customtype;

import android.support.annotation.Nullable;

/**
 * Created by админ2 on 06.06.2017.
 */
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

    protected int mValue;

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
