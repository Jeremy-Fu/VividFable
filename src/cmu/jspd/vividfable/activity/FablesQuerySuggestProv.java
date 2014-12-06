package cmu.jspd.vividfable.activity;

import android.content.SearchRecentSuggestionsProvider;

public class FablesQuerySuggestProv extends SearchRecentSuggestionsProvider {

	public static final String AUTHORITY = FablesQuerySuggestProv.class
			.getName();

	public static final int MODE = DATABASE_MODE_QUERIES;

	public FablesQuerySuggestProv() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
