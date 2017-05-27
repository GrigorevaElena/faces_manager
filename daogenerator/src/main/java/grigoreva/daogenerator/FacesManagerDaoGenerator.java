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
        Entity photoEntity = createPhotoEntity(schema);

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

    private static Entity createPhotoEntity(Schema schema) {
        Entity photoEntity = schema.addEntity("Photo");

        photoEntity.addIdProperty().primaryKey().autoincrement();
        photoEntity.addStringProperty("photoUrl");
        photoEntity.addFloatProperty("noiseX");
        photoEntity.addFloatProperty("noiseY");
        photoEntity.addFloatProperty("leftEyesX");
        photoEntity.addFloatProperty("leftEyesY");
        photoEntity.addFloatProperty("rightEyesX");
        photoEntity.addFloatProperty("rightEyesY");
        //TODO + левая щека, правая щека, левый уголок губ, правый уголок губ, низ губы
        //TODO параметры прямоугольника, в которое вписываем лицо?

        //TODO + средний цвет лица (яркость или цвет? в каком виде?)
        //TODO + средний цвет глаз?
        //TODO + яркость или цвет в контрольных точках?
        return photoEntity;
    }

    //TODO + связующая таблица один ко многим контакт, фото
}
