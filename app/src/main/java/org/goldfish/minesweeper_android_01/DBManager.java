package org.goldfish.minesweeper_android_01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


enum DBMode {
	READ, WRITE
}

public class DBManager extends SQLiteOpenHelper {
	final static int DB_VERSION = 1;
	final static String DB_NAME = "minesweeper.db";
	private static DBManager manager = null;
	final String db_path;
	final String GAME_INFO_TABLE_NAME = "game_info";
	final String ENTRY_RECORD_TABLE_NAME = "entry_record";
	private final Map<String, String> SQLMetaData;

	private DBManager(@Nullable Context context,
	                  @NotNull String db_path) {
		super(context, db_path + '/' + DB_NAME, null,
			DB_VERSION);
		this.db_path = db_path;
		SQLMetaData = Map.of("height", "INTEGER", "width",
			"INTEGER", "mines", "INTEGER",
			"difficulty_description", "TEXT", "end_time",
			"INTEGER");
	}

	public static DBManager getInstance() {
		if (manager == null)
			throw new NullPointerException("DBManager is " + "not initialized");
		return manager;
	}

	/**
	 * 获取{@code DBManager}实例<br/>
	 *
	 * @param context {@code Context}实例<br/>
	 *                用于创建数据库<br/>
	 * @param dbPath  数据库路径<br/>
	 * @return {@code DBManager}实例<br/>
	 */

	public static DBManager getInstance(@Nullable Context context, String dbPath) {
		if (manager == null)
			manager = new DBManager(context, dbPath);
		return manager;
	}

	/**
	 * 保存获胜数据<br/>
	 *
	 * @param info {@code MineSweeperGameInfo}实例 <br/>
	 *             记录了游戏的高度、宽度、雷数、难度描述、结束时间
	 */
	public void onWinWrite(MineSweeperGameInfo info) {
		SQLiteDatabase db = getWritableDatabase();
		Object[] bindArgs = {info.getHeight(),
			info.getWidth(), info.getMineCount(),
			info.getDifficultyDescription(),
			info.getTime()};
		String insertSQL =
			"INSERT INTO `" + GAME_INFO_TABLE_NAME + "` " + "(`height`,`width`,`mines`," + "`difficulty_description`,`end_time`) " + "VALUES (?,?,?,?,?);";
		db.execSQL(insertSQL, bindArgs);
	}

	/**
	 * 用户退出时 调用<br/>
	 * 将用户此次登录写入数据库<br/>
	 * 进入时间需调用{@code EntranceRecorder.getInstance()
	 * .onEntryRecord()}获取<br/>
	 * 经纬度调用{@code EntranceRecorder.getInstance()
	 * .updateLocation(double, double)}获取<br/>
	 * <br/>
	 * 退出时间则从{@code System.currentTimeMillis()}获取<br/>
	 *
	 * @see AMAPRequestSender#getInstance(EntranceActivity)
	 */

	public void onExitWrite(@NotNull PrivacyRecord record) {
		AMapLocation location = record.location();
		SQLiteDatabase db = getWritableDatabase();
		String insertSQL;
		Object[] bindArgs;
		if (location != null) {
			insertSQL =
				"INSERT INTO `" + ENTRY_RECORD_TABLE_NAME + "` (`latitude`,`longitude`,`entry_time`,`exit_time`,`address`) " + "VALUES (?,?,?,?,?);";
			bindArgs = new Object[]{location.getLatitude()
				, location.getLongitude(),
				record.enterTime().getTime(),
				record.exitTime().getTime(),
				location.getAddress()};
		} else {
			insertSQL =
				"INSERT INTO `" + ENTRY_RECORD_TABLE_NAME + "` (`entry_time`,`exit_time`) VALUES (?,?);";
			bindArgs =
				new Object[]{record.enterTime().getTime(),
					record.exitTime().getTime()};
		}
		db.execSQL(insertSQL, bindArgs);

	}

	/**
	 * 获取获胜记录<br/>
	 *
	 * @return 获胜记录的列表
	 */

	public List<MineSweeperGameInfo> onRecordsGet() {
		List<MineSweeperGameInfo> records =
			new LinkedList<>();
		SQLiteDatabase db = getReadableDatabase();
		String selectSQL =
			"SELECT * FROM `" + GAME_INFO_TABLE_NAME +
				"`;";
		var cursor = db.rawQuery(selectSQL, null);
		while (cursor.moveToNext()) {
			int heightIndex = cursor.getColumnIndex(
				"height");
			if (heightIndex == -1)
				throw new NullPointerException("height " + "column not found");
			int height = cursor.getInt(heightIndex);
			int widthIndex = cursor.getColumnIndex(
				"width");
			if (widthIndex == -1)
				throw new NullPointerException("width " +
					"column not found");
			int width = cursor.getInt(widthIndex);
			int minesIndex = cursor.getColumnIndex(
				"mines");
			if (minesIndex == -1)
				throw new NullPointerException("mines " +
					"column not found");
			int mines = cursor.getInt(minesIndex);
			int difficultyDescriptionIndex =
				cursor.getColumnIndex(
					"difficulty_description");
			if (difficultyDescriptionIndex == -1)
				throw new NullPointerException(
					"difficulty_description column not " + "found");
			String difficultyDescription =
				cursor.getString(difficultyDescriptionIndex);
			int endTimeIndex = cursor.getColumnIndex(
				"end_time");
			if (endTimeIndex == -1)
				throw new NullPointerException("end_time " + "column not found");
			long endTime = cursor.getLong(endTimeIndex);
			Result result =
				new Result(difficultyDescription, mines,
					width, height, endTime);
			records.add(result);
		}
		return records;
	}

	/**
	 * 获取用户进入记录<br/>
	 *
	 * @return 用户进入记录的列表
	 */

	public List<PrivacyRecord> onEntryRecordsGet() {
		List<PrivacyRecord> records = new LinkedList<>();
		SQLiteDatabase db = getReadableDatabase();
		String selectSQL =
			"SELECT * FROM `" + ENTRY_RECORD_TABLE_NAME +
				"`;";
		var cursor = db.rawQuery(selectSQL, null);
		while (cursor.moveToNext()) {
			int latitudeIndex = cursor.getColumnIndex(
				"latitude");
			if (latitudeIndex == -1)
				throw new NullPointerException("latitude " + "column not found");
			double latitude =
				cursor.getDouble(latitudeIndex);
			int longitudeIndex = cursor.getColumnIndex(
				"longitude");
			if (longitudeIndex == -1)
				throw new NullPointerException("longitude "
					+ "column not found");
			double longitude =
				cursor.getDouble(longitudeIndex);
			int entryTimeIndex = cursor.getColumnIndex(
				"entry_time");
			if (entryTimeIndex == -1)
				throw new NullPointerException("entry_time"
					+ " column not found");
			var entryTime = cursor.getLong(entryTimeIndex);
			int exitTimeIndex = cursor.getColumnIndex(
				"exit_time");
			if (exitTimeIndex == -1)
				throw new NullPointerException("exit_time "
					+ "column not found");
			long exitTime = cursor.getLong(exitTimeIndex);
			AMapLocation location = new AMapLocation(
				"DBManager");
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			PrivacyRecord result =
				new PrivacyRecord(new Timestamp(entryTime)
					, new Timestamp(exitTime), location);
			records.add(result);
		}
		return records;
	}

	/**
	 * 初始化数据库<br/>
	 * 在数据库中创建表<br/>
	 *
	 * @param db {@code SQLiteDatabase}实例<br/>
	 *           待初始化的数据库
	 */

	@Override
	public void onCreate(SQLiteDatabase db) {

		String createGameInfoSQL =
			" CREATE TABLE IF NOT " + "EXISTS " + GAME_INFO_TABLE_NAME + """
			(
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				height INTEGER,
				width INTEGER,
				mines INTEGER,
				difficulty_description TEXT,
				end_time TIMESTAMP
			)""";
		db.execSQL(createGameInfoSQL);
		String createEntryRecordSQL =
			" CREATE TABLE IF " + "NOT EXISTS " + ENTRY_RECORD_TABLE_NAME + """
			(
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				latitude REAL NOT NULL,
				longitude REAL NOT NULL,
				address TEXT,
				entry_time TIMESTAMP NOT NULL,
				exit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
			)""";
		db.execSQL(createEntryRecordSQL);
	}

	/**
	 * 更新数据库<br/>
	 * 删除旧表<br/>
	 * 创建新表<br/>
	 *
	 * @param db         {@code SQLiteDatabase}实例<br/>
	 *                   待更新的数据库<br/>
	 * @param oldVersion 暂未使用<br/>
	 *                   可为零<br/>
	 * @param newVersion 暂未使用<br/>
	 *                   可为零<br/>
	 */

	@Override
	public void onUpgrade(SQLiteDatabase db,
	                      int oldVersion, int newVersion) {
		String dropSQL =
			"DROP TABLE IF EXISTS " + GAME_INFO_TABLE_NAME;
		db.execSQL(dropSQL);
		dropSQL =
			"DROP TABLE IF EXISTS " + ENTRY_RECORD_TABLE_NAME;
		db.execSQL(dropSQL);
		onCreate(db);
	}

}

