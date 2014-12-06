package cmu.jspd.vividfable.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
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

	public Fable getFullFable(long id) {
		Cursor c = database.query(FableDBHelper.FABLES_TABLE_NAME,
				FableDBHelper.ALL_FIELDS, String.format("%s = %d", FableDBHelper.COLUMN_ID, id), new String[] {}, null,
				null, null);
		if (c.moveToFirst()) {
			long retrivedId = c.getLong(0);
			String title = c.getString(1);
			String language = c.getString(2);
			FableType type = Utility.convertFableType(c.getString(3));
			String content = c.getString(4);
			int rating = c.getInt(6);
			
			if (id != retrivedId) {
				throw new RuntimeException(String.format("Try to retrieve id = %d. Obtain id = %d", id, retrivedId));
			}
			
			Fable retrivedFable = new Fable(id, title, language, type, content);
			retrivedFable.setRating(rating);
			return retrivedFable;
			
		}
		c.close();
		return null;
	}

	public Fable saveFullFable(Fable f) {
		long id = database.insert(FableDBHelper.FABLES_TABLE_NAME, null,
				f.getFullContentValues());
		Log.d(TAG, String.format("Insert fable (title:%s) with id: %d", f.getTitle(), id));
		return getFullFable(id);
	}
	
	public Fable updateFableRating(long fableId, int newRating) {
		
		ContentValues values = new ContentValues();
		
		values.put(FableDBHelper.COLUMN_RATING, newRating);
		
		int affectedRows = database.update(FableDBHelper.FABLES_TABLE_NAME, values, String.format("%s = %d", FableDBHelper.COLUMN_ID, fableId), null);
		
		if (affectedRows == 1) {
			return getFullFable(fableId);
		} else {
			return null;
		}
		
	}
	
	public Fable updateFableOpenTime(long fableId) {
		
		ContentValues values = new ContentValues();
		
		
		values.put(FableDBHelper.COLUMN_CREATE_AT, new Date().getTime());
		
		int affectedRows = database.update(FableDBHelper.FABLES_TABLE_NAME, values, String.format("%s = %d", FableDBHelper.COLUMN_ID, fableId), null);
		
		if (affectedRows == 1) {
			return getFullFable(fableId);
		} else {
			throw new RuntimeException("Unable to find the fable id = " + fableId);
		}
		
	}
	

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
			FableDBHelper.COLUMN_ID,
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
			
			long id = cursor.getLong(0);
			String title = cursor.getString(1);
			FableType type = Utility.convertFableType(cursor.getString(2));
			String content = cursor.getString(3);
			Fable fable = new Fable(id, title, lang, type, content);
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
				FableDBHelper.COLUMN_ID,
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
			long id = cursor.getLong(0);
			FableType type = Utility.convertFableType(cursor.getString(2));
			String title = cursor.getString(1);
			String content = cursor.getString(3);
			Fable fable = new Fable(id, title, lang, type, content);
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
			saveFullFable(fable);
			fable = Utility.parseRawFableFile(context, cmu.jspd.vividfable.activity.R.raw.fable_en2);
			saveFullFable(fable);
			fable = Utility.parseRawFableFile(context, cmu.jspd.vividfable.activity.R.raw.fable_en3);
			saveFullFable(fable);
			fable = Utility.parseRawFableFile(context, cmu.jspd.vividfable.activity.R.raw.fable_en4);
			saveFullFable(fable);
			fable = Utility.parseRawFableFile(context, cmu.jspd.vividfable.activity.R.raw.fable_en5);
			saveFullFable(fable);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return;
		}
		return;
	}

}
