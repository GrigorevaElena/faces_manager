package grigoreva.facesmanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import grigoreva.facesmanager.R;
import grigoreva.facesmanager.adapter.FaceListAdapter;
import grigoreva.facesmanager.data.loader.PersonListLoader;
import grigoreva.facesmanager.model.PersonViewModel;

/**
 * Created by админ2 on 04.04.2017.
 */
public class FaceListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<PersonViewModel>>{

    private FaceListAdapter mAdapter;
    private Loader<List<PersonViewModel>> mLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_list, container, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.face_list);
        mAdapter = new FaceListAdapter();
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(mAdapter);
        //fillInitData();
        initDataLoader();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.forceLoad();
    }

    private void initDataLoader() {
        mLoader = getLoaderManager().initLoader(R.id.person_list_loader, null, this);
    }

    private void fillInitData() {
        List<PersonViewModel> initPersonList = new ArrayList<>();
        initPersonList.add(new PersonViewModel("path", "Иванов", "Иван", true));
        initPersonList.add(new PersonViewModel("path", "Петров", "Петр", false));
        initPersonList.add(new PersonViewModel("path", "Михайлов", "Михаил", true));
        initPersonList.add(new PersonViewModel("path", "Викторов", "Виктор", true));
        initPersonList.add(new PersonViewModel("path", "Алекандрова", "Александра", false));
        mAdapter.setData(initPersonList);
    }

    @Override
    public Loader<List<PersonViewModel>> onCreateLoader(int id, Bundle args) {
        if (id == R.id.person_list_loader) {
            mLoader = new PersonListLoader(getContext());
        }
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<PersonViewModel>> loader, List<PersonViewModel> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<PersonViewModel>> loader) {

    }
}
