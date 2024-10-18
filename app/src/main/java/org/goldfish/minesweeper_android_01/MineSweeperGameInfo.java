package org.goldfish.minesweeper_android_01;

import java.sql.Timestamp;

/**
 * {@code MineSweeperGameInfo}接口定义了游戏的基本信息<br/>
 * 包括游戏的高度、宽度、雷数、游戏时间、难度描述、获胜者、经度和纬度<br/>
 * 通过实现这个接口，可以将游戏信息写入数据库<br/>
 * {@code getHeight()}返回雷区的高度<br/>
 * {@code getWidth()}返回雷区的宽度<br/>
 * {@code getMineCount()}返回雷区的雷数<br/>
 * {@code getTime()}返回游戏时间<br/>
 * {@code getDifficultyDescription()}返回游戏的难度描述<br/>
 * {@code getWinner()}返回获胜者<br/>
 *
 *
 */
public interface MineSweeperGameInfo {
    int getHeight();
    int getWidth();
    int getMineCount();
    String getDifficultyDescription();
    long getTime();
}
