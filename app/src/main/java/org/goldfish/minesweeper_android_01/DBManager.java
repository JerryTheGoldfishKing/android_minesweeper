package org.goldfish.minesweeper_android_01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Locale;


public class DBManager extends SQLiteOpenHelper {
	final static int DB_VERSION = 1;

	private static DBManager manager = null;
	final String ENTER_AND_EXIT_TABLE_NAME = "enter_and_exit";
	final String GAME_INFO_TABLE_NAME = "game_info";
	String tableName;
	private SQLiteDatabase database;

	private DBManager(@Nullable Context context, @Nullable String DBName, @NotNull String tableName) {
		super(context, DBName, null, DB_VERSION);
	}

	public static DBManager getInstance() {
		if (manager == null) throw new NullPointerException("DBManager is not initialized");
		return manager;
	}

	public static DBManager getInstance(@Nullable Context context, @Nullable String name, @NotNull String tableName) {
		if (manager == null) manager = new DBManager(context, name, tableName);
		return manager;
	}

	public void onExitWrite(Timestamp start, Timestamp end, Double latitude, Double longitude) {
		Object[] bindArgs = {start, end, latitude, longitude};
		String sql = String.format(Locale.CHINA, "INSERT INTO %s (start, end, latitude, longitude) VALUES (?, ?, ?, ?)", tableName);
		database.execSQL(sql, bindArgs);
	}

	public void onGameEndWrite(MineSweeperGameInfo gameInfo) {
		Object[] bindArgs= {gameInfo.getHeight(), gameInfo.getWidth(), gameInfo.getMineCount(), gameInfo.getDifficultyDescription(), gameInfo.getWinner(), EntranceRecorder.getInstance().latitude, EntranceRecorder.getInstance().longtitude, EntranceRecorder.getInstance().start, new Timestamp(System.currentTimeMillis())};
		String sql = String.format(Locale.CHINA, "INSERT INTO %s (height, width, mines, difficulty_description, winner, latitude, longitude, start, end_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", GAME_INFO_TABLE_NAME);
		database.execSQL(sql, bindArgs);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createEntryInfoSQL = "CREATE TABLE IF NOT EXISTS " + ENTER_AND_EXIT_TABLE_NAME + """
				(
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					start TIMESTAMP,
				 	end TIMESTAMP,
				  	latitude DOUBLE,
				   	longitude DOUBLE
				)
""";
		db.execSQL(createEntryInfoSQL);
		String createGameInfoSQL =" CREATE TABLE IF NOT EXISTS " + GAME_INFO_TABLE_NAME + """
				(
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					height INTEGER,
					width INTEGER,
					mines INTEGER,
					difficulty_description TEXT,
					winner TEXT,
					longitude DOUBLE,
					latitude DOUBLE,
					start TIMESTAMP,
					end_time TIMESTAMP
				)""";
		db.execSQL(createGameInfoSQL);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String dropSQL = "DROP TABLE IF EXISTS " + ENTER_AND_EXIT_TABLE_NAME+";";
		db.execSQL(dropSQL);
		db.execSQL("DROP TABLE IF EXISTS "+GAME_INFO_TABLE_NAME+';');
		onCreate(db);
	}

	enum DBMode {
		READ, WRITE
	}
}
