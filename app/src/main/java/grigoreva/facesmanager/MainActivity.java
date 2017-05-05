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

        Button importFaces = (Button) findViewById(R.id.import_faces_btn);
        Button makePhotoBtn = (Button) findViewById(R.id.make_photo_btn);

        assert importFaces != null;
        importFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FaceListActivity.class);
                startActivity(intent);
            }
        });

        assert makePhotoBtn != null;
        makePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewFaceActivity.class);
                startActivity(intent);
            }
        });
    }
}
