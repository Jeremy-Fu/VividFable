package cmu.jspd.vividfable.activity;

import java.util.ArrayList;

import cmu.jspd.vividfable.activity.R;
import cmu.jspd.vividfable.activity.R.id;
import cmu.jspd.vividfable.activity.R.layout;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchResultsDialog extends ListActivity {

	public static final String SEARCH_RESULTS_LINKS = "SEARCH_RESULTS_LINKS";
	public static final String SEARCH_RESULTS_TITLES = "SEARCH_RESULTS_TITLES";

	private ArrayList<String> links;
	private ArrayList<String> titles;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		links = intent.getStringArrayListExtra(SEARCH_RESULTS_LINKS);
		titles = intent.getStringArrayListExtra(SEARCH_RESULTS_TITLES);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.search_item,
				R.id.search_label, titles));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Intent intent = new Intent(this, TextFableActivity.class);
		intent.setAction(TextFableActivity.ACTION_FETCH_LINK);
		intent.putExtra(TextFableActivity.FABLE_LINK, links.get(position));
		intent.putExtra(TextFableActivity.FABLE_TITLE, titles.get(position));
		startActivity(intent);
		finish();
	}
}
