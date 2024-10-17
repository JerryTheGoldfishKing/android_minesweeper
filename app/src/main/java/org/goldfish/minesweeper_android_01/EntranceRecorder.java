package org.goldfish.minesweeper_android_01;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Timestamp;

public class EntranceRecorder {
	private static EntranceRecorder instance;
	AppCompatActivity activity;
	Timestamp start, end;
	Double longtitude, latitude;

	private EntranceRecorder(AppCompatActivity activity) {
		this.activity = activity;
		start = new Timestamp(System.currentTimeMillis());
	}

	public static EntranceRecorder getInstance() {
		if (instance == null) throw new NullPointerException("EntranceRecorder is not initialized");
		return instance;
	}

	public static EntranceRecorder getInstance(AppCompatActivity activity) {
		if (instance == null) return instance = new EntranceRecorder(activity);
		instance.activity = activity;
		return instance;
	}

	public void updateLocation(double latitude, double longtitude) {
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	public void onExitRequest() {
		DBManager.getInstance().onExitWrite(start, end, longtitude, latitude);
		Controller.promptAndExit(activity);
	}
}
