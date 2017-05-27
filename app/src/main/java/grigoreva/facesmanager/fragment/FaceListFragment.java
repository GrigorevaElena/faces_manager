package grigoreva.facesmanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import grigoreva.facesmanager.R;
import grigoreva.facesmanager.adapter.FaceListAdapter;
import grigoreva.facesmanager.model.PersonViewModel;

/**
 * Created by админ2 on 04.04.2017.
 */
public class FaceListFragment extends Fragment {

    private FaceListAdapter mAdapter;

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
        fillInitData();
        return view;
    }

    private void fillInitData() {
        List<PersonViewModel> initPersonList = new ArrayList<>();
        initPersonList.add(new PersonViewModel("path", "Иванов", "Иван"));
        initPersonList.add(new PersonViewModel("path", "Петров", "Петр"));
        initPersonList.add(new PersonViewModel("path", "Михайлов", "Михаил"));
        initPersonList.add(new PersonViewModel("path", "Викторов", "Виктор"));
        initPersonList.add(new PersonViewModel("path", "Алекандрова", "Александра"));
        mAdapter.setData(initPersonList);
    }
}
