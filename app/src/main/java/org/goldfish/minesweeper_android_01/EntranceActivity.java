package org.goldfish.minesweeper_android_01;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;


public class EntranceActivity extends AppCompatActivity implements Resources {

	private static final int REQUEST_CODE_PERMISSIONS = 1001;
	Button easyButton, mediumButton, hardButton, recordButton;
	SQLiteDatabase db;

	public EntranceActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_main);

		initializeDatabase();

		easyButton = findViewById(R.id.easy_mode_button);
		mediumButton = findViewById(R.id.medium_mode_button);
		hardButton = findViewById(R.id.hard_mode_button);
		recordButton = findViewById(R.id.record_button);

		easyButton.setOnClickListener(new ModeSelectListener(this, Mode.EASY));
		mediumButton.setOnClickListener(new ModeSelectListener(this, Mode.MEDIUM));
		hardButton.setOnClickListener(new ModeSelectListener(this, Mode.HARD));

		recordButton.setOnClickListener(v -> {
			startActivity(new Intent(this, RecordActivity.class));
		});
		recordButton.setOnLongClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("警告");
			builder.setMessage("重置所有储存数据?");
			builder.setPositiveButton("Yes", (dialog, which) -> {
				DBManager.getInstance().onUpgrade(db, 1, 2);
				Toast.makeText(this, "数据已重置", Toast.LENGTH_SHORT).show();
			});
			builder.setNegativeButton("No", (dialog, which) -> {
			});
			builder.create().show();
			return true;

		});

		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});


	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_PERMISSIONS) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				initializeDatabase();
			} else {
				Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void initializeDatabase() {
		File dbFile = new File(getFilesDir(), "minesweeper.db");
		DBManager.getInstance(this, dbFile.getAbsolutePath());
		db = DBManager.getInstance().getWritableDatabase();
	}
}