package grigoreva.facesmanager.data.loader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import grigoreva.facesmanager.data.greendao.DatabaseDelegate;
import grigoreva.facesmanager.data.greendao.Person;
import grigoreva.facesmanager.data.greendao.PersonPhoto;
import grigoreva.facesmanager.model.PersonViewModel;

/**
 * Created by админ2 on 06.06.2017.
 */
public class PersonListLoader extends AsyncTaskLoader<List<PersonViewModel>> {
    public PersonListLoader(Context context) {
        super(context);
    }

    @Override
    public List<PersonViewModel> loadInBackground() {
        DatabaseDelegate databaseDelegate = DatabaseDelegate.getInstance(getContext());
        List<Person> personList = databaseDelegate.getAllPersonList();
        if (personList == null || personList.isEmpty()) {
            return null;
        }
        List<PersonViewModel> personViewModelList = new ArrayList<>(personList.size());
        for (Person person : personList) {
            PersonPhoto personPhoto = databaseDelegate.getMainPersonPhoto(person.getId());
            personViewModelList.add(
                    new PersonViewModel(personPhoto == null ? "" : personPhoto.getPhotoUrl(),
                    person.getSurname(), person.getName(), person.getIsContact()));
        }
        return personViewModelList;
    }
}
