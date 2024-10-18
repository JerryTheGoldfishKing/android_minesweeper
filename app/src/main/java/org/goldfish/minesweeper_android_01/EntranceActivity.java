package org.goldfish.minesweeper_android_01;

import static org.goldfish.minesweeper_android_01.Controller.thrower;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntranceActivity extends AppCompatActivity implements Resources {

    Button easyButton, mediumButton, hardButton, recordButton,privacyCollectionButton;
    SQLiteDatabase db;

    public EntranceActivity() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EntranceRecorder.getInstance(this);

        easyButton = findViewById(R.id.easy_mode_button);
        mediumButton = findViewById(R.id.medium_mode_button);
        hardButton = findViewById(R.id.hard_mode_button);

        View.OnClickListener waitingListener= v -> Toast.makeText(this, "等待定位信息", Toast.LENGTH_SHORT).show();
        easyButton.setOnClickListener(waitingListener);
        mediumButton.setOnClickListener(waitingListener);
        hardButton.setOnClickListener(waitingListener);

        recordButton = findViewById(R.id.record_button);

        privacyCollectionButton=findViewById(R.id.privacy_activity_button);
        privacyCollectionButton.setOnClickListener(waitingListener);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        db = DBManager.getInstance(this, null, "MINESWEEPER_RECORDS").getWritableDatabase();
        DBManager.getInstance().onUpgrade(db,0,0);





        var permission = android.Manifest.permission.ACCESS_FINE_LOCATION;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "asking for permission...", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{permission}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        AMAPRequestSender.getInstance(this).requestLocation();
    }

    public void setLocation() {

        easyButton.setOnClickListener(new ModeSelectListener(this, Mode.EASY));
        mediumButton.setOnClickListener(new ModeSelectListener(this, Mode.MEDIUM));
        hardButton.setOnClickListener(new ModeSelectListener(this, Mode.HARD));

        privacyCollectionButton.setOnClickListener(v -> {
            startActivity(new Intent(this, PrivacyCollectionActivity.class));
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CHECK_PERMISSIONS:
        {
            if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
                Log.i(thrower, "onRequestPermissionsResult: requestCode is not LOCATION_PERMISSION_REQUEST_CODE");
                break CHECK_PERMISSIONS;
            }
            if (grantResults.length == 0) {
                Log.i(thrower, "onRequestPermissionsResult: grantResults is empty");
                break CHECK_PERMISSIONS;
            }
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i(thrower, "onRequestPermissionsResult: permission denied");
                break CHECK_PERMISSIONS;
            }
            try{
                AMAPRequestSender.getInstance(this).requestLocation();
            }catch (NullPointerException e){
                Log.w(thrower, "onRequestPermissionsResult: cannot get location", e);
                break CHECK_PERMISSIONS;
            }
            return;
        }
        Toast.makeText(this, "授权位置信息失败", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

}
