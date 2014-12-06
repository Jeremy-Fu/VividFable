package cmu.jspd.vividfable.datamodel;

import cmu.jspd.vividfable.util.Utility;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FableDBHelper extends SQLiteOpenHelper {

	protected static final String FABLES_TABLE_NAME = "fables";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_LANGUAGE = "language";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_CREATE_AT = "created_at";
	public static final String COLUMN_RATING = "rating";

	
	protected static String[] ALL_FIELDS = { COLUMN_ID, COLUMN_TITLE, COLUMN_LANGUAGE, COLUMN_TYPE, COLUMN_CONTENT, COLUMN_CREATE_AT, COLUMN_RATING};

	private static final int DATABASE_VERSION = 1;
	
	private static final String FABLES_TABLE_CREATE = "CREATE TABLE "
			+ FABLES_TABLE_NAME + " (" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL,"
			+ COLUMN_TITLE + " TEXT NOT NULL, "
			+ COLUMN_TYPE +" TEXT NOT NULL, "
			+ COLUMN_LANGUAGE + " TEXT NOT NULL, "
			+ COLUMN_CONTENT + " TEXT NOT NULL, "
			+ COLUMN_CREATE_AT + " INTEGER, "
			+ COLUMN_RATING + " INTEGER);";

	public FableDBHelper(Context context) {
		super(context, FABLES_TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FABLES_TABLE_CREATE);
		Log.i("FableDBHelper", FABLES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + FABLES_TABLE_NAME);
		onCreate(db);
	}

}
