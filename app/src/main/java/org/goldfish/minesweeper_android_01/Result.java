package org.goldfish.minesweeper_android_01;


public class Result implements MineSweeperGameInfo {
    final int time;
    final int height;
    final int width;
    final int mineCount;
    String difficulty_description;
    String winner;


    Result(MineSweeperGameInfo mineSweeperGameInfo, int time) {
        this(mineSweeperGameInfo, time, "goldfish");
    }

    Result(MineSweeperGameInfo mineSweeperGameInfo, int time, String winner) {
        width = mineSweeperGameInfo.getWidth();
        height = mineSweeperGameInfo.getHeight();
        mineCount = mineSweeperGameInfo.getMineCount();
        difficulty_description = mineSweeperGameInfo.getDifficultyDescription();
        this.time = time;
        this.winner = winner;
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
        return mineCount;
    }

    @Override
    public String getDifficultyDescription() {
        return String.valueOf(difficulty_description);
    }

    @Override
    public String getWinner() {
        return String.valueOf(winner);
    }


}
