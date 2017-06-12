package grigoreva.facesmanager.data.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;

/**
 * Created by админ2 on 16.05.2017.
 */
public class DatabaseDelegate {
    private static DatabaseDelegate mDelegate;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    private static PersonDao mPersonDao;
    private static PersonPhotoDao mPhotoDao;
    private static PhotoLandmarkDao mLandmarkDao;

    public static DatabaseDelegate getInstance(@NonNull Context context) {
        if (mDelegate != null) {
            return mDelegate;
        } else {
            mDelegate = init(context);
            return mDelegate;
        }
    }

    private static DatabaseDelegate init(@NonNull Context context) {
        DatabaseDelegate databaseDelegate = new DatabaseDelegate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

        mPersonDao = mDaoSession.getPersonDao();
        mPhotoDao = mDaoSession.getPersonPhotoDao();
        mLandmarkDao = mDaoSession.getPhotoLandmarkDao();

        return databaseDelegate;
    }

    public List<Person> getAllPersonList(){
        return mPersonDao.loadAll();
    }

    @Nullable
    public PersonPhoto getMainPersonPhoto(long id) {
        List<PersonPhoto> photoList = mPhotoDao.queryBuilder()
                .where(PersonPhotoDao.Properties.PersonId.eq(id))
                .list();
        if (photoList != null && photoList.size() > 0) {
            return photoList.get(0);
        }
        return null;
    }

    public Person createNewPerson(String surname, String name, boolean isContact) {
        Person person = new Person();
        person.setSurname(surname);
        person.setName(name);
        person.setIsContact(isContact);
        return person;
    }

    public Person getOrCreatePerson(String surname, String name, boolean isContact) {
        if (surname == null || name == null) {
            return createNewPerson(surname, name, isContact);
        }
        List<Person> personList = mPersonDao.queryBuilder().where(PersonDao.Properties.Surname.eq(surname),
                PersonDao.Properties.Name.eq(name)).list();
        if (personList != null && !personList.isEmpty()) {
            return personList.get(0);
        } else {
            return createNewPerson(surname, name, isContact);
        }
    }

    public long savePerson(Person person) {
        return mPersonDao.insertOrReplace(person);
    }

    public long savePhoto(PersonPhoto photo) {
        long photoId = mPhotoDao.insertOrReplace(photo);
        for (PhotoLandmark landmark : photo.getLandmarkList().values()) {
            landmark.setPhotoId(photoId);
        }
        savePhotoLandmarkList(photo.getLandmarkList().values());
        return photoId;
    }

    private void savePhotoLandmarkList(Collection<PhotoLandmark> landmarkList) {
        if (landmarkList == null || landmarkList.isEmpty()) {
            return;
        }
        mLandmarkDao.insertOrReplaceInTx(landmarkList);
    }

    public Person getPersonById(long id) {
        return mPersonDao.load(id);
    }

    public List<PersonPhoto> getAllPhotos() {
        return mPhotoDao.loadAll();
    }

    public Map<LandmarkType, PhotoLandmark> getLandmarksForPhoto(Long id) {
        List<PhotoLandmark> landmarks = mLandmarkDao.queryBuilder().where(PhotoLandmarkDao.Properties.PhotoId.eq(id)).list();
        Map<LandmarkType, PhotoLandmark> result = new HashMap<>();
        for (PhotoLandmark landmark : landmarks) {
            result.put(landmark.getLandmarkType(), landmark);
        }
        return result;
    }

    public List<PersonPhoto> getPhotoByPersonId(Long id) {
        List<PersonPhoto> photos = mPhotoDao.queryBuilder().where(PersonPhotoDao.Properties.PersonId.eq(id)).list();
        for (PersonPhoto photo : photos) {
            photo.setLandmarkList(getLandmarksForPhoto(photo.getId()));
        }
        return photos;
    }
}
