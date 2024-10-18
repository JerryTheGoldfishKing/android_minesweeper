package org.goldfish.minesweeper_android_01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


enum DBMode {
	READ, WRITE
}

public class DBManager extends SQLiteOpenHelper {
	/*

	 */
	final static int DB_VERSION = 1;
	final static String DB_NAME = "minesweeper.db";
	private static DBManager manager = null;
	final String db_path;
	final String GAME_INFO_TABLE_NAME = "game_info";
	final String ENTRY_RECORD_TABLE_NAME = "entry_record";
	private final Map<String, String> SQLMetaData;

	private DBManager(@Nullable Context context, @NotNull String db_path) {
		super(context, db_path + '/' + DB_NAME, null, DB_VERSION);
		this.db_path = db_path;
		SQLMetaData = Map.of("height", "INTEGER", "width", "INTEGER", "mines", "INTEGER", "difficulty_description", "TEXT", "end_time", "INTEGER");
	}

	public static DBManager getInstance() {
		if (manager == null) throw new NullPointerException("DBManager is not initialized");
		return manager;
	}

	public static DBManager getInstance(@Nullable Context context, String dbPath) {
		if (manager == null) manager = new DBManager(context, dbPath);
		return manager;
	}

	public void onWinWrite(MineSweeperGameInfo info) {
		SQLiteDatabase db = getWritableDatabase();
		Object[] bindArgs = {info.getHeight(), info.getWidth(), info.getMineCount(), info.getDifficultyDescription(), info.getTime()};
		String insertSQL = "INSERT INTO `" + GAME_INFO_TABLE_NAME + "` (`height`,`width`,`mines`,`difficulty_description`,`end_time`) VALUES (?,?,?,?,?);";
		db.execSQL(insertSQL, bindArgs);
	}

	public List<MineSweeperGameInfo> onRecordsGet() {
		List<MineSweeperGameInfo> records = new LinkedList<>();
		SQLiteDatabase db = getReadableDatabase();
		String selectSQL = "SELECT * FROM `" + GAME_INFO_TABLE_NAME + "`;";
		var cursor = db.rawQuery(selectSQL, null);
		while (cursor.moveToNext()) {
			int heightIndex = cursor.getColumnIndex("height");
			if (heightIndex == -1) throw new NullPointerException("height column not found");
			int height = cursor.getInt(heightIndex);
			int widthIndex = cursor.getColumnIndex("width");
			if (widthIndex == -1) throw new NullPointerException("width column not found");
			int width = cursor.getInt(widthIndex);
			int minesIndex = cursor.getColumnIndex("mines");
			if (minesIndex == -1) throw new NullPointerException("mines column not found");
			int mines = cursor.getInt(minesIndex);
			int difficultyDescriptionIndex = cursor.getColumnIndex("difficulty_description");
			if (difficultyDescriptionIndex == -1)
				throw new NullPointerException("difficulty_description column not found");
			String difficultyDescription = cursor.getString(difficultyDescriptionIndex);
			int endTimeIndex = cursor.getColumnIndex("end_time");
			if (endTimeIndex == -1) throw new NullPointerException("end_time column not found");
			long endTime = cursor.getLong(endTimeIndex);
			Result result = new Result(difficultyDescription, mines, height, width, endTime);
			records.add(result);
		}
		return records;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String createGameInfoSQL = " CREATE TABLE IF NOT EXISTS " + GAME_INFO_TABLE_NAME + """
				(
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					height INTEGER,
					width INTEGER,
					mines INTEGER,
					difficulty_description TEXT,
					end_time TIMESTAMP
				)""";
		db.execSQL(createGameInfoSQL);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String dropSQL = "DROP TABLE IF EXISTS " + GAME_INFO_TABLE_NAME;
		db.execSQL(dropSQL);
		onCreate(db);
	}

}

