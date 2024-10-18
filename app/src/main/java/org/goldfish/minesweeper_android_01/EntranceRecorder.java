package org.goldfish.minesweeper_android_01;

import static org.goldfish.minesweeper_android_01.Controller.thrower;

import android.util.Log;

import com.amap.api.location.AMapLocation;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class EntranceRecorder {
	private static EntranceRecorder instance =
		new EntranceRecorder();
	Long enterTime, exitTime;
	AMapLocation location;

	private EntranceRecorder() {
		enterTime = null;
		exitTime = null;
		location = null;
	}

	public static EntranceRecorder getInstance() {
		if (instance == null) {
			instance = new EntranceRecorder();
		}
		return instance;
	}

	public synchronized void onEntryRecord() {
		Log.i(thrower, "onEntryRecord: trying");
		if (enterTime != null) return;
		enterTime = System.currentTimeMillis();
		Log.i(thrower, "onEntryRecord: " + enterTime);
		AMAPRequestSender.getInstance().requestLocation();
	}

	public void updateLocation(@NotNull AMapLocation location) {
		this.location = location;
	}

	/**
	 * 用户退出时 调用<br/>
	 * 将用户此次登录写入数据库<br/>
	 *
	 * @throws NullPointerException 如果进入数据不完整
	 *                              开始时间先调用{@code
	 *                              EntranceRecorder
	 *                              .getInstance()
	 *                              .onEntryRecord()}<br/>
	 *                              经纬度调用{@code
	 *                              EntranceRecorder
	 *                              .getInstance()
	 *                              .updateLocation
	 *                              (double, double)}<br/>
	 */

	public synchronized void onExitRecord() throws Exception {
		Log.i(EntranceActivity.TAG, "onExitRecord: ");
		if (enterTime == null) {
			throw new Exception("onEntryRecord() is not " + "called");
		}
		if (location == null) {
			throw new Exception("updateLocation() is not " + "called");
		}
		exitTime = System.currentTimeMillis();
		Log.w(thrower, "onExitRecord: writing...");
		//submit record
		PrivacyRecord record =
			new PrivacyRecord(new Timestamp(enterTime),
				new Timestamp(exitTime), location);
		//clear cache
		enterTime = null;
		exitTime = null;
		location = null;
		DBManager.getInstance().onExitWrite(record);
	}
}
