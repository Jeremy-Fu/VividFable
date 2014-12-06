package cmu.jspd.vividfable.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FableSourceDBHelper extends SQLiteOpenHelper {

	protected static final String FABLE_SOURCE_TABLE_NAME = "FABLE_SOURCE";

	protected static String[] ALL_FIELDS = { "id", "type", "siteName",
			"siteLogo", "url", "language" };

	private static final int DATABASE_VERSION = 1;
	private static final String FABLE_SOURCE_TABLE_CREATE = "CREATE TABLE "
			+ FABLE_SOURCE_TABLE_NAME
			+ " (id INTEGER PRIMARY KEY, type INTEGER, siteName TEXT, siteLogo TEXT, url TEXT, language TEXT);";

	public FableSourceDBHelper(Context context) {
		super(context, FABLE_SOURCE_TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FABLE_SOURCE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + FABLE_SOURCE_TABLE_NAME + " IF EXISTS");
		db.execSQL(FABLE_SOURCE_TABLE_CREATE);
	}

}
