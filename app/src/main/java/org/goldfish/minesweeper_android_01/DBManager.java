package org.goldfish.minesweeper_android_01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;


public class DBManager extends SQLiteOpenHelper {
	final static int DB_VERSION = 1;

	private static DBManager manager = null;
	final String GAME_INFO_TABLE_NAME = "game_info";
	String tableName;

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

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createEntryInfoSQL = "CREATE TABLE IF NOT EXISTS " + tableName + """
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
		String dropSQL = "DROP TABLE IF EXISTS " + tableName;
		db.execSQL(dropSQL);
		onCreate(db);
	}

	enum DBMode {
		READ, WRITE
	}
}
