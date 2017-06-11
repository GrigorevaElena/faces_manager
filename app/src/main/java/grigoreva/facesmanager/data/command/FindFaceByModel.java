package grigoreva.facesmanager.data.command;

import android.content.Context;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import grigoreva.facesmanager.bl.FaceUtil;
import grigoreva.facesmanager.data.greendao.DatabaseDelegate;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.event.FaceWasFoundEvent;
import grigoreva.facesmanager.event.SavePersonEvent;

/**
 * Created by Лена on 10.06.2017.
 */
public class FindFaceByModel implements Runnable {
    private Context mContext;
    private PersonPhoto mPhoto;

    public FindFaceByModel(@NonNull Context context, @NonNull PersonPhoto photo) {
        mContext = context;
        mPhoto = photo;
    }

    @Override
    public void run() {
        DatabaseDelegate databaseDelegate = DatabaseDelegate.getInstance(mContext);
        List<PersonPhoto> personPhotos = databaseDelegate.getAllPhotos();
        for (PersonPhoto personPhoto : personPhotos) {
            personPhoto.setLandmarkList(databaseDelegate.getLandmarksForPhoto(personPhoto.getId()));
        }
        if (personPhotos == null || personPhotos.isEmpty()) {
            EventBus.getDefault().post(new FaceWasFoundEvent(null));
        } else {
            PersonPhoto photo = FaceUtil.findFace(personPhotos, mPhoto);
            if (photo != null) {
                EventBus.getDefault().post(new FaceWasFoundEvent(databaseDelegate.getPersonById(photo.getPersonId())));
            } else {
                EventBus.getDefault().post(new FaceWasFoundEvent(null));
            }
        }
    }
}
