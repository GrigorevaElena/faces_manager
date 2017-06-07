package grigoreva.facesmanager.data.command;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import grigoreva.facesmanager.data.greendao.DatabaseDelegate;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;

/**
 * Created by админ2 on 06.06.2017.
 */
public class SaveNewPersonCommand extends AsyncTask<Void, Void, Void>{
    private Context mContext;
    private Person mPerson;
    private PersonPhoto mPhoto;

    public SaveNewPersonCommand(@NonNull Context context, @NonNull Person person, @NonNull PersonPhoto photo) {
        mContext = context;
        mPerson = person;
        mPhoto = photo;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DatabaseDelegate databaseDelegate = DatabaseDelegate.getInstance(mContext);
        long personId = databaseDelegate.savePerson(mPerson);
        mPhoto.setPersonId(personId);
        databaseDelegate.savePhoto(mPhoto);
        return null;
    }
}
