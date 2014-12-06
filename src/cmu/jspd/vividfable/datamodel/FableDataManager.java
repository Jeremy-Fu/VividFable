package cmu.jspd.vividfable.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cmu.jspd.vividfable.config.Environment;
import cmu.jspd.vividfable.datamodel.Fable.FableType;
import cmu.jspd.vividfable.util.Utility;

public class FableDataManager {
	
	private static final String TAG = "FableDataManager";
	
	private static FableDataManager _i;
	private FableDBHelper dbHelper;
	private SQLiteDatabase database;

	public static FableDataManager getInstance(Context ctxt) {
		if (_i == null) {
			_i = new FableDataManager(ctxt);
			_i.open();
			_i.populateDB(ctxt);
		}
		return _i;
	}

	private FableDataManager(Context ctxt) {
		dbHelper = new FableDBHelper(ctxt);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		if (Environment.DEBUG) {
			//In debug mode, the schema of DB may be changed
			//If changed, drop the obsolete table and create a new one
			dbHelper.onUpgrade(database, 0, 1);
		}
	}

	public void close() {
		dbHelper.close();
	}

	
//	public Fable getLeastFableDetails(Long id) {
//		Cursor c = database.query(FableDBHelper.FABLES_TABLE_NAME,
//				new String[] { FableDBHelper.COLUMN_ID, FableDBHelper.COLUMN_TYPE, FableDBHelper.COLUMN_TITLE, FableDBHelper.COLUMN_LANGUAGE}, "id = " + id,
//				new String[] {}, null, null, null);
//		if (c.moveToFirst()) {
//			Fable fable = new Fable();
//			fable.setId(c.getLong(0));
//			fable.setType(c.getInt(1));
//			fable.setTitle(c.getString(2));
//			fable.setIcon(c.getString(3));
//			return fable;
//		}
//		c.close();
//		return null;
//	}

	public Fable getFullFableDetails(Long id) {
		Cursor c = database.query(FableDBHelper.FABLES_TABLE_NAME,
				FableDBHelper.ALL_FIELDS, "id = " + id, new String[] {}, null,
				null, null);
		if (c.moveToFirst()) {
			return new Fable();
		}
		c.close();
		Log.i(TAG, "Nothing!!!");
		return null;
	}

	public Fable saveFullFable(Fable f) {
		Long id = database.insert(FableDBHelper.FABLES_TABLE_NAME, null,
				f.getFullContentValues());
		return getFullFableDetails(id);
	}

//	public int updateFableStats(Fable f) {
//		return database.update(FableDBHelper.FABLES_TABLE_NAME,
//				f.getStatsContentValues(), "id = " + f.getId(), null);
//	}

	public int countFables() {
		Cursor cursor = database.rawQuery("SELECT  * FROM "
				+ FableDBHelper.FABLES_TABLE_NAME, null);
		int cnt = cursor.getCount();
		cursor.close();
		return cnt;
	}

	public List<Fable> listRecentFablesForAdapter(String lang) {
		
		List<Fable> ret = new ArrayList<Fable>();
		
		String[] gridViewFields = {
			FableDBHelper.COLUMN_TITLE,
			FableDBHelper.COLUMN_TYPE,
			FableDBHelper.COLUMN_CONTENT
		};
		
		String condition = String.format("%s = '%s'", FableDBHelper.COLUMN_LANGUAGE, lang);
		String order = String.format("%s DESC", FableDBHelper.COLUMN_CREATE_AT);
		
		Cursor cursor = database.query(FableDBHelper.FABLES_TABLE_NAME,
				gridViewFields, condition, new String[] {}, null, null, order, "10");
		
		cursor.moveToFirst();
		int counter = 0;
		while (!cursor.isAfterLast()) {
			
			FableType type = Utility.convertFableType(cursor.getString(1));
			String title = cursor.getString(0);
			String content = cursor.getString(2);
			Fable fable = new Fable(title, lang, type, content);
			ret.add(fable);
			cursor.moveToNext();
			counter++;
		}
		Log.i(TAG, "total records = " + counter);
		cursor.close();
		return ret;
	}

	public List<Fable> listFavoriteFablesForAdapter(String lang) {
		
		List<Fable> ret = new ArrayList<Fable>();
		
		String[] gridViewFields = {
				FableDBHelper.COLUMN_TITLE,
				FableDBHelper.COLUMN_TYPE,
				FableDBHelper.COLUMN_CONTENT
		};
		
		String condition = String.format("%s = '%s'", FableDBHelper.COLUMN_LANGUAGE, lang);
		String order = String.format("%s DESC", FableDBHelper.COLUMN_RATING);
		
		Cursor cursor = database
				.query(FableDBHelper.FABLES_TABLE_NAME,
						gridViewFields, condition, new String[] {}, null, null,
						order, "10");
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			FableType type = Utility.convertFableType(cursor.getString(1));
			String title = cursor.getString(0);
			String content = cursor.getString(2);
			Fable fable = new Fable(title, lang, type, content);
			ret.add(fable);
			cursor.moveToNext();
		}
		cursor.close();
		return ret;
	}

//	public List<FableAbstract> listMostViewedFablesForAdapter(String lang) {
//		List<FableAbstract> ret = new ArrayList<FableAbstract>();
//		
//		String[] gridViewFields = {
//				FableDBHelper.COLUMN_TITLE,
//				FableDBHelper.COLUMN_TYPE
//		};
//		
//		String condition = String.format("%s = '%s'", FableDBHelper.COLUMN_LANGUAGE, lang);
//		
//		String order = String.format("%s DESC", FableDBHelper.COLUMN_RATING);
//		
//		Cursor c = database
//				.query(FableDBHelper.FABLES_TABLE_NAME, new String[] { "id",
//						"type", "title", "icon" },
//						"localViewCount > 1 AND status = 1 AND language = '"
//								+ lang + "'", new String[] {}, null, null,
//						"localViewCount DESC", "5");
//		c.moveToFirst();
//		while (!c.isAfterLast()) {
//			Fable fable = new Fable();
//			fable.setId(c.getLong(0));
//			fable.setType(c.getInt(1));
//			fable.setTitle(c.getString(2));
//			fable.setIcon(c.getString(3));
//			ret.add(fable);
//			c.moveToNext();
//		}
//		c.close();
//		return ret;
//	}
	
	private void populateDB(Context context) {
		Fable fable = null;
		Log.i(TAG, "populateDB");
		try {
			fable = Utility.parseRawFableFile(context, cmu.jspd.vividfable.activity.R.raw.fable_en1);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return;
		}
		saveFullFable(fable);
	}

}
