package org.goldfish.minesweeper_android_01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import java.io.File;


public class EntranceActivity extends AppCompatActivity implements Resources, LifecycleEventObserver {

	final static String TAG = "EntranceActivity_Lifecycle";
	Button easyButton, mediumButton, hardButton,
		recordButton;
	TextView privacyButton;
	SQLiteDatabase db;

	public EntranceActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_main);

		AMAPRequestSender.getInstance(this);


		initializeDatabase();

		View.OnClickListener location_waiting_listener =
			v -> Toast.makeText(this, "正在获取位置信息",
				Toast.LENGTH_SHORT).show();


		easyButton = findViewById(R.id.easy_mode_button);
		mediumButton =
			findViewById(R.id.medium_mode_button);
		hardButton = findViewById(R.id.hard_mode_button);
		recordButton = findViewById(R.id.record_button);

		easyButton.setOnClickListener(location_waiting_listener);
		mediumButton.setOnClickListener(location_waiting_listener);
		hardButton.setOnClickListener(location_waiting_listener);
		recordButton.setOnClickListener(location_waiting_listener);

		privacyButton =
			findViewById(R.id.privacy_collection_entrance);
		privacyButton.setOnClickListener(location_waiting_listener);


		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars =
				insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top,
				systemBars.right, systemBars.bottom);
			return insets;
		});

		if (ContextCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//			new Thread(() -> ActivityCompat
//			.requestPermissions(this,
//				new String[]{Manifest.permission
//				.ACCESS_FINE_LOCATION},
//				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)).start();
			// 如果没有权限，则请求权限
			ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
		} else {
			updateListeners();
		}

		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
		// 检查是否已经有权限
		// 检查是否已经有权限

		EntranceRecorder.getInstance().onEntryRecord();


	}


	public void updateListeners() {
		AMAPRequestSender.getInstance(this).requestLocation();
		//check whether AMap client has access to location
		//if not, request permission

		easyButton.setOnClickListener(new ModeSelectListener(this, Mode.EASY));
		mediumButton.setOnClickListener(new ModeSelectListener(this, Mode.MEDIUM));
		hardButton.setOnClickListener(new ModeSelectListener(this, Mode.HARD));

		recordButton.setOnClickListener(v -> startActivity(new Intent(this, RecordActivity.class)));
		recordButton.setOnLongClickListener(v -> {
			AlertDialog.Builder builder =
				new AlertDialog.Builder(this);
			builder.setTitle("警告");
			builder.setMessage("重置所有储存数据?");
			builder.setPositiveButton("Yes", (dialog,
			                                  which) -> {
				DBManager.getInstance().onUpgrade(db, 1,
					2);
				Toast.makeText(this, "数据已重置",
					Toast.LENGTH_SHORT).show();
			});
			builder.setNegativeButton("No", (dialog,
			                                 which) -> {
			});
			builder.create().show();
			return true;

		});

		privacyButton.setOnClickListener(v -> startActivity(new Intent(this, PrivacyCollectionActivity.class)));

	}


	@Override
	public void onRequestPermissionsResult(int requestCode
		, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode,
			permissions, grantResults);
		if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
			if (grantResults.length == 0) {
				Toast.makeText(this, "未授权",
					Toast.LENGTH_SHORT).show();
				return;
			}
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "未授权",
					Toast.LENGTH_SHORT).show();
				return;
			}
			updateListeners();
		}
	}

	private void initializeDatabase() {
		String dbName="minesweeper.db";
		File dbFile = new File(getFilesDir(), dbName);
		Log.i(TAG,
			"initializeDatabase: writing to " + dbFile.getAbsolutePath());
		DBManager.getInstance(this,
			dbFile.getAbsolutePath());
		db = DBManager.getInstance().getWritableDatabase();
	}


	@Override
	public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
		if (event == Lifecycle.Event.ON_START) {
			EntranceRecorder.getInstance().onEntryRecord();
		} else if (event == Lifecycle.Event.ON_STOP) {
			try {
				EntranceRecorder.getInstance().onExitRecord();
			} catch (Exception ignored) {

			}
		}
	}
}