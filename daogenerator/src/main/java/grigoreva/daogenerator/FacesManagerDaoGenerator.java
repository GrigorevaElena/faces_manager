package grigoreva.daogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by админ2 on 16.05.2017.
 */
public class FacesManagerDaoGenerator {
    private static final int VERSION = 1;
    private static final String PACKAGE_DESTINATION = "grigoreva.facesmanager.data.greendao";
    private static final String DESTINATION_FOLDER = "./app/src/main/java";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(VERSION, PACKAGE_DESTINATION);

        Entity personEntity = createPersonEntity(schema);
        Entity photoEntity = createPersonPhotoEntity(schema);
        Entity landmarkEntity = createPhotoLandmarkEntity(schema);
        schema.enableKeepSectionsByDefault();

        new DaoGenerator().generateAll(schema, DESTINATION_FOLDER);
    }

    private static Entity createPersonEntity(Schema schema) {
        Entity personEntity = schema.addEntity("Person");

        personEntity.addIdProperty().primaryKey().autoincrement();
        personEntity.addStringProperty("name");
        personEntity.addStringProperty("surname");
        personEntity.addBooleanProperty("isContact").notNull();
        //TODO + возраст, пол, контакты - в отдельной таблице
        return personEntity;
    }

    private static Entity createPersonPhotoEntity(Schema schema) {
        Entity personPhotoEntity = schema.addEntity("PersonPhoto");

        personPhotoEntity.addIdProperty().primaryKey().autoincrement();
        personPhotoEntity.addStringProperty("photoUrl");
        personPhotoEntity.addLongProperty("personId");
        //параметры прямоугольника, в которое вписываем лицо
        personPhotoEntity.addFloatProperty("faceWidth");
        personPhotoEntity.addFloatProperty("faceHeight");
        personPhotoEntity.addFloatProperty("normalFaceWidth");
        personPhotoEntity.addFloatProperty("normalFaceHeight");
        personPhotoEntity.addIntProperty("avgFaceColor");//средний цвет лица (яркость или цвет? в каком виде?)
        //TODO + угол наклона?
        //TODO + средний цвет глаз?
        //TODO + яркость или цвет в контрольных точках?
        return personPhotoEntity;
    }

    private static Entity createPhotoLandmarkEntity(Schema schema) {
        Entity photoLandmarkEntity = schema.addEntity("PhotoLandmark");

        photoLandmarkEntity.addIdProperty().primaryKey().autoincrement();
        photoLandmarkEntity.addIntProperty("landmarkType")
                .customType(PACKAGE_DESTINATION + ".customtype.LandmarkType",
                        PACKAGE_DESTINATION + ".customtype.LandmarkTypeConverter");
        photoLandmarkEntity.addLongProperty("photoId");//связь по первичному ключу
        photoLandmarkEntity.addFloatProperty("pointX");
        photoLandmarkEntity.addFloatProperty("pointY");
        photoLandmarkEntity.addFloatProperty("normPointX");
        photoLandmarkEntity.addFloatProperty("normPointY");
        //координаты
        return photoLandmarkEntity;
    }

    //TODO + связующая таблица один ко многим контакт, фото
}
