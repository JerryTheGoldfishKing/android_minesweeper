package org.goldfish.minesweeper_android_01;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class RecordActivity extends AppCompatActivity {
	final static float TEXT_SIZE=30;
TableLayout tableLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_record);
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});
		List<MineSweeperGameInfo> records = DBManager.getInstance().onRecordsGet();
		tableLayout = findViewById(R.id.record_table);
		RecordView recordView = new RecordView(this, records);

		Button backButton = findViewById(R.id.recoord_return_button);
		backButton.setOnClickListener(v->{
			startActivity(new Intent(this, EntranceActivity.class));
		});
	}
	class RecordView{
		public RecordView(Context context, List<MineSweeperGameInfo> records) {
			for(MineSweeperGameInfo record:records){
				RecordRow row = new RecordRow(context,record);
				tableLayout.addView(row);
			}
		}
	}
}

class RecordRow extends TableRow {
	/**
	 * 每行先难度 再高度宽度 再雷数 再时间
	 * @param context
	 * @param record
	 */
    public RecordRow(Context context, MineSweeperGameInfo record) {
        super(context);
        setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        setBackgroundResource(R.drawable.table_row_bg); // Assuming you have a drawable for row background

        RecordCell minesCell = new RecordCell(context, record.getMineCount());
        RecordCell fieldSizeCell = new RecordCell(context, record.getHeight() + "*" + record.getWidth());
		RecordCell timeCell = new RecordCell(context, record.getTime());
        RecordCell difficultyCell = new RecordCell(context, record.getDifficultyDescription());

		addView(difficultyCell);
		addView(fieldSizeCell);
		addView(minesCell);
		addView(timeCell);
    }
}

class RecordCell extends androidx.appcompat.widget.AppCompatTextView {
    public RecordCell(Context context, Object value) {
        super(context);
        setText(value.toString());
		setTextSize(RecordActivity.TEXT_SIZE);

        setPadding(16, 16, 16, 16); // Padding for better readability
        setBackgroundResource(R.drawable.table_cell_bg); // Assuming you have a drawable for cell background
        setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
    }
}

