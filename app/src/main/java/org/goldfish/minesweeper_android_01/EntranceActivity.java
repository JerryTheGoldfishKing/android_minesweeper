package org.goldfish.minesweeper_android_01;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntranceActivity extends AppCompatActivity {

    Button easyButton, mediumButton, hardButton;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        easyButton.setOnClickListener(new ModeSelectListener(this,Mode.EASY));
        mediumButton.setOnClickListener(new ModeSelectListener(this,Mode.MEDIUM));
        hardButton.setOnClickListener(new ModeSelectListener(this,Mode.HARD));
    }
}
