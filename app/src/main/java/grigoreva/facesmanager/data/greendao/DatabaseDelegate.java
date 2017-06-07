package grigoreva.facesmanager.data.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;

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
        List<PersonPhoto> photoList = mPhotoDao.queryRaw(PersonPhotoDao.Properties.PersonId.eq(id).toString());
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

    public long savePerson(Person person) {
        return mPersonDao.insertOrReplace(person);
    }

    public long savePhoto(PersonPhoto photo) {
        savePhotoLandmarkList(photo.getLandmarkList().values());
        return mPhotoDao.insertOrReplace(photo);
    }

    private void savePhotoLandmarkList(Collection<PhotoLandmark> landmarkList) {
        if (landmarkList == null || landmarkList.isEmpty()) {
            return;
        }
        mLandmarkDao.insertOrReplaceInTx(landmarkList);
    }
}
