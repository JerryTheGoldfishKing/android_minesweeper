package org.goldfish.minesweeper_android_01;

import static org.goldfish.minesweeper_android_01.Controller.thrower;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntranceActivity extends AppCompatActivity implements Resources {

    Button easyButton, mediumButton, hardButton, recordButton;
    SQLiteDatabase db;

    public EntranceActivity() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        easyButton = findViewById(R.id.easy_mode_button);
        mediumButton = findViewById(R.id.medium_mode_button);
        hardButton = findViewById(R.id.hard_mode_button);
        recordButton = findViewById(R.id.record_button);
        View.OnClickListener waitingListener= v -> Toast.makeText(this, "等待定位信息", Toast.LENGTH_SHORT).show();
        easyButton.setOnClickListener(waitingListener);
        mediumButton.setOnClickListener(waitingListener);
        hardButton.setOnClickListener(waitingListener);

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
        }
    }

}
