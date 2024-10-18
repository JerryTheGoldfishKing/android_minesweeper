package org.goldfish.minesweeper_android_01;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class PrivacyCollectionActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_privacy_collection);
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars =
				insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top,
				systemBars.right, systemBars.bottom);
			return insets;
		});

		List<PrivacyRecord> records =
			DBManager.getInstance().onEntryRecordsGet();

		TableLayout tableLayout =
			findViewById(R.id.privacy_record_table);
		PrivacyRecordView recordView =
			new PrivacyRecordView(this, tableLayout,
				records);

		Button backButton =
			findViewById(R.id.entry_privacy_record_return_button);
		backButton.setOnClickListener(v -> {
			startActivity(new Intent(this,
				EntranceActivity.class));
		});
	}

	class PrivacyRecordView {

		public PrivacyRecordView(Context context,
		                         TableLayout layout,
		                         List<PrivacyRecord> records) {
			for (var record : records) {
				for(String[] valueRow: record.showFormat()){
					TableRow row=new TableRow(context);
					for(String value: valueRow){
						TextView textView=new TextView(context);
						textView.setText(value);
						row.addView(textView);
					}
					layout.addView(row);
				}
			}
		}
	}
}


class PrivacyRecordCell extends LinearLayout {
	public PrivacyRecordCell(Context context ,Object[] values) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		setLayoutParams(params);
		for (Object value : values) {
			TextView textView = new TextView(context);
			textView.setText(value.toString());
			addView(textView);
		}
	}
}
