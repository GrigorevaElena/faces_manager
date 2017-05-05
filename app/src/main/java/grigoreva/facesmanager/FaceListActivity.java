package grigoreva.facesmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import grigoreva.facesmanager.fragment.FaceListFragment;

/**
 * Created by админ2 on 04.04.2017.
 */
public class FaceListActivity extends AppCompatActivity {
    private FaceListFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_list);
        if (savedInstanceState == null) {
            mFragment = new FaceListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.faces_fragment_container,
                    mFragment, FaceListFragment.class.getCanonicalName()).commit();
        } else {
            mFragment = (FaceListFragment) getSupportFragmentManager()
                    .findFragmentByTag(FaceListFragment.class.getCanonicalName());
        }
    }
}
