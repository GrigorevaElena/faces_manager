package grigoreva.facesmanager.data.command;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grigoreva.facesmanager.bl.CalcUtil;
import grigoreva.facesmanager.bl.FaceUtil;
import grigoreva.facesmanager.data.greendao.DatabaseDelegate;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.data.greendao.PhotoLandmark;
import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;
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
        Person person;
        //person = simpleSearch();
        person = mahalanobisSearch();
        EventBus.getDefault().post(new FaceWasFoundEvent(person));

    }

    private Person mahalanobisSearch() {
        DatabaseDelegate databaseDelegate = DatabaseDelegate.getInstance(mContext);
        List<Person> peronList = databaseDelegate.getAllPersonList();
        PointF minMah = null;
        Person minPerson = null;

        List<Map<LandmarkType, PhotoLandmark>> mapVectList = new ArrayList<>();
        mapVectList.add(mPhoto.getLandmarkList());

        PointF curVectMah = CalcUtil.calcMahalanobis(mPhoto.getLandmarkList(), mPhoto.getLandmarkList());

        for (Person person : peronList) {
            // для каждой персоны получаем список фото с точками
            List<PersonPhoto> photos = databaseDelegate.getPhotoByPersonId(person.getId());
            for (PersonPhoto photo : photos) {
                PointF curMah = CalcUtil.calcMahalanobis(photo.getLandmarkList(), mPhoto.getLandmarkList());
                if (minMah == null || (getDiff(minMah, curVectMah) > getDiff(curMah, curVectMah))) {
                    minMah = curMah;
                    minPerson = person;
                    minPerson.setMainPhoto(photo.getPhotoUrl() );
                }
            }
        }
        if (getDiff(minMah, curVectMah) > 900) {
            minPerson = null;
        }
        return minPerson;
    }

    private float getDiff(PointF p1, PointF p2) {
        float maxX = Math.max(p1.x, p2.x);
        float maxY = Math.max(p1.y, p2.y);
        float minX = Math.min(p1.x, p2.x);
        float minY =  Math.min(p1.y, p2.y);
        return (maxX - minX + maxY - minY) / 2;
    }

    private Person simpleSearch() {
        DatabaseDelegate databaseDelegate = DatabaseDelegate.getInstance(mContext);
        List<PersonPhoto> personPhotos = databaseDelegate.getAllPhotos();
        for (PersonPhoto personPhoto : personPhotos) {
            personPhoto.setLandmarkList(databaseDelegate.getLandmarksForPhoto(personPhoto.getId()));
        }
        if (personPhotos.isEmpty()) {
            return null;
        } else {
            PersonPhoto photo = FaceUtil.findFace(personPhotos, mPhoto);
            if (photo != null) {
                Person person = databaseDelegate.getPersonById(photo.getPersonId());
                PersonPhoto mainPhoto = databaseDelegate.getMainPersonPhoto(person.getId());
                person.setMainPhoto(mainPhoto == null ? null : mainPhoto.getPhotoUrl());
                return person;
            }
            return null;
        }
    }
}
