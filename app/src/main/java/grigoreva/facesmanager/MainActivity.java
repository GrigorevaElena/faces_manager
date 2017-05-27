package grigoreva.facesmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button openPersonListBtn = (Button) findViewById(R.id.person_list_btn);
        Button addPhotoBtn = (Button) findViewById(R.id.add_photo_btn);
        Button detectFaceBtn = (Button) findViewById(R.id.detect_face_btn);

        assert openPersonListBtn != null;
        openPersonListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FaceListActivity.class);
                startActivity(intent);
            }
        });

        assert addPhotoBtn != null;
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadFaceActivity.class);
                startActivity(intent);
            }
        });

        assert detectFaceBtn != null;
        detectFaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetectFaceActivity.class);
                startActivity(intent);
            }
        });
    }
}
