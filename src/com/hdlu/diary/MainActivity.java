package com.hdlu.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.hdlu.db.DBHelper;

public class MainActivity extends Activity {
	private Cursor cursor;
	private SimpleCursorAdapter adapter;
	private DBHelper dbHelper;
	private ListView listView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dbHelper = new DBHelper(this);
		cursor = dbHelper.queryAll();
		startManagingCursor(cursor);
		adapter = new SimpleCursorAdapter(this,
				R.layout.item, cursor, new String[] { "_id", "NAME", "DT" },
				new int[] { 0, R.id.item_name, R.id.item_date });
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				goToEdit(position);
			}
		});

		listView.setOnItemLongClickListener(itemLongClickListener);
		dbHelper.close();
		
		findViewById(R.id.imageBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,EditActivity.class));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		menu.add("新建日记").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(MainActivity.this,EditActivity.class));
				return true;
			}
		});
		menu.add("退出").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				finish();
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	};
	
	@Override
	protected void onResume() {
		onCreate(state);
		super.onResume();
	}
	
	Bundle state;
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		this.state = outState;
		super.onSaveInstanceState(outState);
		adapter.notifyDataSetChanged();
	}
	
	OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, final long id) {
			cursor.moveToPosition(position);
			final int _id = cursor.getInt(cursor.getColumnIndex("_id"));
			new AlertDialog.Builder(MainActivity.this)  
			.setTitle("选择")
			.setItems(new String[] {"编辑","重命名","删除","新建日记"},new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(0 == which)
					{
						goToEdit(position);
					}
					if(1 == which){
						
						Cursor cur = dbHelper.query(
								"select name from DIARY where _id=?",
								new String[] { String.valueOf(_id) });
						cur.moveToFirst();
						String oldName = cur.getString(0);
						rename(oldName, String.valueOf(_id));
					}
					if(2 == which){
						dbHelper.delete(String.valueOf(_id));
						adapter.notifyDataSetChanged();
						onResume();
					}
					if (3 == which) {
						startActivity(new Intent(MainActivity.this,EditActivity.class));
					}
				}
			})  
			.setNegativeButton("确定", null)  
			.show();
			return false;
		}
	};
	
	public void goToEdit(int position){
		cursor.moveToPosition(position);
		int _id = cursor.getInt(cursor.getColumnIndex("_id"));
		Intent intent = new Intent(MainActivity.this,EditActivity.class);
		intent.putExtra("_id", _id);
		startActivity(intent);
	}
	
	
	EditText et =null;
	String text;
	public void rename(String oldName,final String id){
		et = new EditText(MainActivity.this);
		et.setText(oldName);
		new AlertDialog.Builder(MainActivity.this)  
		.setTitle("请输入 新日记名")  
		.setIcon(android.R.drawable.ic_dialog_info)  
		.setView(et)
		.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = et.getText().toString();
				ContentValues values = new ContentValues() ;
				values.put("name", name);
				dbHelper.update(values,id);
				adapter.notifyDataSetChanged();
				onResume();
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
}
