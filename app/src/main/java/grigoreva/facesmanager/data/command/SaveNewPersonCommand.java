package grigoreva.facesmanager.data.command;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import grigoreva.facesmanager.data.greendao.DatabaseDelegate;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.event.SavePersonEvent;

/**
 * Created by админ2 on 06.06.2017.
 */
public class SaveNewPersonCommand implements Runnable {
    private Context mContext;
    private Person mPerson;
    private PersonPhoto mPhoto;

    public SaveNewPersonCommand(@NonNull Context context, @NonNull Person person, @NonNull PersonPhoto photo) {
        mContext = context;
        mPerson = person;
        mPhoto = photo;
    }

    @Override
    public void run() {
        DatabaseDelegate databaseDelegate = DatabaseDelegate.getInstance(mContext);
        if (mPerson.getId() == null) {
            mPerson = databaseDelegate.getOrCreatePerson(mPerson.getSurname(), mPerson.getName(), mPerson.getIsContact());
            databaseDelegate.savePerson(mPerson);
        }
        mPhoto.setPersonId(mPerson.getId());
        databaseDelegate.savePhoto(mPhoto);
        //EventBus.getDefault().postSticky(new SavePersonEvent(true));
        EventBus.getDefault().post(new SavePersonEvent(true));
    }
}
