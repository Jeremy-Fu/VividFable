package cmu.jspd.vividfable.datamodel;

import android.content.ContentValues;

public class Fable {
	
	private static final String DEFAULT_TITLE = "default_title";
	private static final String DEFAULT_LANGUAGE = "en";
	private static final FableType DAFUALT_TYPE = FableType.TEXT;
	private static final String DEFAULT_CONTENT = "story strats from here.";
	
	private final long id;
	private final String title;
	private final String language;
	private final FableType type;
	private final String content;
	private int rating = 0;

	public Fable(long id) {
		this(id, DEFAULT_TITLE, DEFAULT_LANGUAGE, DAFUALT_TYPE, DEFAULT_CONTENT);
	}
	
	public Fable(long id, String title, String language, FableType type) {
		this(id, title, language, type, DEFAULT_CONTENT);
	}
	
	public Fable(long id, String title, String language, FableType type, String content) {
		super();
		this.id = id;
		this.title = title;
		this.language = language;
		this.type = type;
		this.content = content;
	}
	
	public Fable(Fable fable) {
		this(fable.id, fable.title, fable.language, fable.type, fable.content);
	}

	public long getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

	public String getLanguage() {
		return language;
	}

	public FableType getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		if (rating >= 5) {
			this.rating = 5;
		} else if (rating <= 0) {
			this.rating = 0;
		} else {
			this.rating = rating;
		}
	}

	public enum FableType {
		TEXT, VIDEO;
	}

	public ContentValues getFullContentValues() {
		
		ContentValues values = new ContentValues();
		
		
		values.put(FableDBHelper.COLUMN_TITLE, title);
		values.put(FableDBHelper.COLUMN_LANGUAGE, language);
		values.put(FableDBHelper.COLUMN_TYPE, type.toString());
		values.put(FableDBHelper.COLUMN_CONTENT, content);
		values.put(FableDBHelper.COLUMN_RATING, rating);
		
		return values;
	}
	
//	public ContentValues getStatsContentValues() {
//		
//		ContentValues values = new ContentValues();
//		
//		values.put(FableDBHelper.COLUMN_TITLE, title);
//		values.put(FableDBHelper.COLUMN_LANGUAGE, language);
//		values.put(FableDBHelper.COLUMN_TYPE, type.toString());
//		values.put(FableDBHelper.COLUMN_CONTENT, content);
//		values.put(FableDBHelper.COLUMN_RATING, rating);
//		
//		return values;
//	}

	
	

}
