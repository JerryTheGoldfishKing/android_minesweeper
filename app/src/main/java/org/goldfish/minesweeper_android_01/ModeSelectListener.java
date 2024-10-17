package org.goldfish.minesweeper_android_01;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

class ModeSelectListener  implements View.OnClickListener {
    AppCompatActivity activity;
    Mode mode;
    AlertDialog.Builder builder;

    public ModeSelectListener(AppCompatActivity activity, Mode mode) {
        this.activity = activity;
        this.mode = mode;
        this.builder = new AlertDialog.Builder(activity);
    }

    @Override
    public void onClick(View v) {

        //提示框初始化
        builder.setTitle("确认你的难度");
        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtras(mode);

        //提示内容
        String message = String.format(Locale.CHINA, "模式:%s\n雷区高度: %d\n雷区宽度: %d\n雷个数: %d", mode.difficulty_description, mode.height, mode.width, mode.mines);
        builder.setMessage(message);

        //提示框反应
        builder.setPositiveButton("确认",(dialog,which)-> activity.startActivity(intent));
        builder.setNegativeButton("取消",((dialog, which) -> Controller.promptAndExit(activity)));

        //提示框生效
        builder.create().show();

    }
}
