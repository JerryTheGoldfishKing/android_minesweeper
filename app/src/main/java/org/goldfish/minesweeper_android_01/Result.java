package org.goldfish.minesweeper_android_01;

/**
 * {@code Result} 游戏结果信息 <br/>
 *
 */

public class Result implements MineSweeperGameInfo {
	/**
	 * 游戏时间
	 */
	final long time;
	/**
	 * 雷区高度
	 */
	final int height;
	final int width;
	final int mineCount;
	String difficulty_description;

	/**
	 *
	 * @param mineSweeperGameInfo 游戏信息
	 * @param time 游戏时间
	 */
	public Result(MineSweeperGameInfo mineSweeperGameInfo, long time) {
		this(mineSweeperGameInfo.getDifficultyDescription(), mineSweeperGameInfo.getMineCount(), mineSweeperGameInfo.getWidth(), mineSweeperGameInfo.getHeight(), time);
	}

	/**
	 *
	 * @param difficulty_description 难度描述
	 * @param mineCount 雷数
	 * @param width 雷区宽度
	 * @param height 雷区高度
	 * @param time 游戏时间
	 */
	public Result(String difficulty_description, int mineCount, int width, int height, long time) {
		this.difficulty_description = difficulty_description;
		this.mineCount = mineCount;
		this.width = width;
		this.height = height;
		this.time = time;
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
	public long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "Result{" +
			"time=" + time +
			", height=" + height +
			", width=" + width +
			", mineCount=" + mineCount +
			", difficulty_description='" + difficulty_description + '\'' +
			'}';
	}
}
