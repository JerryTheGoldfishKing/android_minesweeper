package org.goldfish.minesweeper_android_01;

import android.content.Intent;

public class Mode extends Intent implements MineSweeperGameInfo {
    public static final Mode EASY = new Mode(9, 9, 10, "简单");
    public static final Mode MEDIUM = new Mode(16, 16, 40, "中等");
    public static final Mode HARD = new Mode(30, 16, 99, "困难");
    final int height;
    final int width;
    final int mines;
    final String difficulty_description;

    Mode(int height, int width, int mines, String difficulty_description) {
        this.height = height;
        this.width = width;
        this.mines = mines;
        this.difficulty_description = difficulty_description;
        putExtra("height", height);
        putExtra("width", width);
        putExtra("mines", mines);
        putExtra("difficulty_description", difficulty_description);

    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getMineCount() {
        return mines;
    }


    @Override
    public String getDifficultyDescription() {
        return difficulty_description;
    }

    @Override
    public String getWinner() {
        throw new UnsupportedOperationException("Winner unavailable in mode selecting stage");
    }
}

