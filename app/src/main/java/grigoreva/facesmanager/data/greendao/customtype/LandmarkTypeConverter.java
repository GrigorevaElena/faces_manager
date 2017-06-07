package grigoreva.facesmanager.data.greendao.customtype;

import de.greenrobot.dao.converter.PropertyConverter;

/**
 * Created by админ2 on 06.06.2017.
 */
public class LandmarkTypeConverter implements PropertyConverter<LandmarkType, Integer> {
    @Override
    public LandmarkType convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return LandmarkType.fromValue(databaseValue);
    }

    @Override
    public Integer convertToDatabaseValue(LandmarkType entityProperty) {
        return entityProperty == null ? null : entityProperty.mValue;
    }
}