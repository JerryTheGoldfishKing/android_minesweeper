package org.goldfish.minesweeper_android_01;
//GameActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class GameActivity extends AppCompatActivity implements Resources{

    private Controller controller;

    private Mode mode;

    private TextView minePrompt;
    private Button exitButton;
    private Button restartButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        GridLayout layout;
        if(intent instanceof Mode) {
            mode = (Mode) intent;
        }
        int height = intent.getIntExtra("height", 10);
        int width = intent.getIntExtra("width", 10);
        int mines = intent.getIntExtra("mines", 10);
        String difficulty_description = intent.getStringExtra("difficulty_description");

        controller = new Controller(height, width, mines);
        controller.setActivity(this);

        Toast.makeText(this, String.format(Locale.CHINA, "模式: %s 高度: %d, 宽度: %d, 雷数: %d", difficulty_description, height, width, mines), Toast.LENGTH_SHORT).show();
        try {
            TextView titleTextView = findViewById(R.id.game_title);
            titleTextView.setText(difficulty_description);

            Chronometer chronometer = findViewById(R.id.goldfish_chronometer);
            chronometer.setBase(0);
            controller.setChronometer(chronometer);

            minePrompt = findViewById(R.id.mine_counter);
            minePrompt.setText(String.valueOf(mines));

            layout = findViewById(R.id.grids_field);
            layout.setColumnCount(width);
            layout.setRowCount(height);

            exitButton = findViewById(R.id.exit_button);
            exitButton.setOnClickListener(v->{
                Controller.promptAndExit(this);
            });
            restartButton = findViewById(R.id.restart_button);
            restartButton.setOnClickListener(v->{
                startActivity(new Intent(this, EntranceActivity.class));
            });



        } catch (NullPointerException nullPointerException) {
            Toast.makeText(this, nullPointerException.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "有组件无法定位", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int num = 0; num < width * height; num++) {
            Grid button = new Grid(this, num / width, num % width);
            controller.add(button);
            layout.addView(button);
        }
        controller.findSurroundings();
    }

    void addExitButton(boolean finished) {
        exitButton.setOnClickListener(
                v -> Controller.promptAndExit(this)
        );
        restartButton.setOnClickListener(
                v -> {
                    startActivity(new Intent(this, EntranceActivity.class));
                }
        );
    }

    Controller getController() {
        return controller;
    }

    public TextView getMinePrompt() {
        return minePrompt;
    }

    public Button getExitButton() {
        return exitButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }
}