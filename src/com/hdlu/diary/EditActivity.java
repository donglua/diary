package com.hdlu.diary;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hdlu.db.DBHelper;

public class EditActivity extends Activity {
	private EditText editText;
	private Button saveBtn;
	private Button cancalBtn;
	private DBHelper dbHelper;
 	private int id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		editText = (EditText) findViewById(R.id.editText);
		saveBtn = (Button) findViewById(R.id.save);
		cancalBtn = (Button) findViewById(R.id.cancal);
		id = getIntent().getIntExtra("_id", -1);

		System.out.println(id);
		dbHelper = new DBHelper(this);

		if (id != -1) {
			Cursor cursor = dbHelper.query(
					"select name,content,dt from DIARY where _id=?",
					new String[] { String.valueOf(id) });
			cursor.moveToFirst();
			String content = cursor.getString(1);
			editText.setText(content);
			saveBtn.setOnClickListener(updateClickListener);
		}else
		{
			saveBtn.setOnClickListener(saveClickListener);
		}
		
		cancalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	View.OnClickListener updateClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String text = editText.getText().toString();
			ContentValues values = new ContentValues();
			values.put("content", text);
			dbHelper.update(values,String.valueOf(id));
			finish();
		}
	};

	EditText et =null;
	String text;
	View.OnClickListener saveClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			et = new EditText(EditActivity.this);
			text = editText.getText().toString();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = format.format(new Date());
			et.setText(date);
			new AlertDialog.Builder(EditActivity.this)  
			.setTitle("请输入日记名")  
			.setIcon(android.R.drawable.ic_dialog_info)  
			.setView(et)  
			.setPositiveButton("保存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = et.getText().toString();
					ContentValues values = new  ContentValues() ;
					values.put("name", name);
					values.put("content", text);
					dbHelper.insert(values);
					finish();
				}
			})  
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.show();
		}
	};
	
}
