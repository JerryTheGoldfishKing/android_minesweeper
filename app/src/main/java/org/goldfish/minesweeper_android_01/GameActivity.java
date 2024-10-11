package org.goldfish.minesweeper_android_01;
//GameActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private Controller controller;


    private TextView minePrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int height = intent.getIntExtra("height", 10);
        int width = intent.getIntExtra("width", 10);
        int mines = intent.getIntExtra("mines", 10);
        String difficulty_description = intent.getStringExtra("difficulty_description");

        controller = new Controller(height, width, mines);
        controller.setActivity(this);

        Toast.makeText(this, String.format(Locale.CHINA, "模式: %s 高度: %d, 宽度: %d, 雷数: %d", difficulty_description, height, width, mines), Toast.LENGTH_SHORT).show();

        TextView titleTextView = findViewById(R.id.game_title);
        if (titleTextView == null) {
            Toast.makeText(this, "标题显示失败", Toast.LENGTH_SHORT).show();
            System.exit(0);
            return;
        }
        titleTextView.setText(difficulty_description);

        Chronometer chronometer = findViewById(R.id.goldfish_chronometer);
        if (chronometer == null) {
            Toast.makeText(this, "计时器定位失败", Toast.LENGTH_SHORT).show();
            System.exit(0);
            return;
        }
        chronometer.setBase(0);
        controller.setChronometer(chronometer);

        minePrompt = findViewById(R.id.mine_counter);
        if (minePrompt == null) {
            Toast.makeText(this, "扫雷计数器定位失败", Toast.LENGTH_SHORT).show();
            System.exit(0);
            return;
        }
        minePrompt.setText(String.valueOf(mines));

        GridLayout layout = findViewById(R.id.grids_field);
        if (layout == null) {
            Toast.makeText(this, "雷区初始化定位失败", Toast.LENGTH_SHORT).show();
            System.exit(0);
            return;
        }
        layout.setColumnCount(width);
        layout.setRowCount(height);

        for (int num = 0; num < width * height; num++) {
            Grid button = new Grid(this, num / width, num % width);
            controller.add(button);
            layout.addView(button);
        }
        controller.findSurroundings();
    }

    void addExitButton() {
        LinearLayout layout = findViewById(R.id.main_layout);
        AppCompatButton
                exitButton = new AppCompatButton(this),
                restartButton = new AppCompatButton(this);
        if (layout == null) {
            Toast.makeText(this,
                    "无法创建退出按钮",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        exitButton.setOnClickListener(
                v -> controller.promptAndExit()
        );
        restartButton.setOnClickListener(
                v -> startActivity(new Intent(this,EntranceActivity.class))
        );

        layout.addView(exitButton);
        layout.addView(restartButton);
    }

    Controller getController() {
        return controller;
    }
    public TextView getMinePrompt() {
        return minePrompt;
    }


}