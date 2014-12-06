package cmu.jspd.vividfable.asynctask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cmu.jspd.vividfable.activity.FablesQuerySuggestProv;
import cmu.jspd.vividfable.activity.SearchResultsDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.SearchRecentSuggestions;
import android.util.Log;

public class SearchFableTask extends AsyncTask<Void, Void, Map<String, String>> {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:21.0.0) Gecko/20121011 Firefox/21.0.0";

	private static final String TAG = "SearchFableTask";
	
	private ProgressDialog progressDialog;
	private String query;
	private Activity ctxt;

	public SearchFableTask(Activity c, String q) {
		ctxt = c;
		query = q;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(ctxt, "Searching...", query, true);
	}

	protected Map<String, String> doInBackground(Void... v) {
		
		if (ctxt.getResources().getConfiguration().locale.getLanguage().equals("en")) {

			return searchEnFable();
			
		} else if (ctxt.getResources().getConfiguration().locale.getLanguage().equals("zh")) {
			
			return searchZhFable();

		} else {
			Log.w(TAG, "Unknown locale.");
			return null;
		}
	}

	protected void onPostExecute(Map<String, String> result) {
		progressDialog.dismiss();
		if (result.size() > 0) {
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
					ctxt, FablesQuerySuggestProv.AUTHORITY,
					FablesQuerySuggestProv.MODE);
			suggestions.saveRecentQuery(query, null);
			ArrayList<String> links = new ArrayList<String>();
			links.addAll(result.keySet());
			ArrayList<String> titles = new ArrayList<String>();
			titles.addAll(result.values());
			Intent intent = new Intent(ctxt, SearchResultsDialog.class);
			intent.putExtra(SearchResultsDialog.SEARCH_RESULTS_LINKS, links);
			intent.putExtra(SearchResultsDialog.SEARCH_RESULTS_TITLES, titles);
			ctxt.startActivity(intent);
		}
	}
	
	
	private Map<String, String> searchEnFable() {
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		String searchString = "site:http://www.aesopfables.com/cgi/aesop1.cgi "
				+ query;
		String siteTitlePrefix = "AesopFables.com - ";
		String request;
		request = "https://www.google.com/search?q="
				+ URLEncoder.encode(searchString) + "&num=20";
		
		Document doc;
		try {
			doc = Jsoup.connect(request).userAgent(USER_AGENT)
					.timeout(5000).get();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return map;
		}
		Elements links = doc.select("h3.r a[href]");
		
		for (Element link : links) {
			String lnk = link.attr("href");
			String title = link.text().substring(siteTitlePrefix.length());
			int end = title.indexOf(" -");
			if (end > 0) {
				title = title.substring(0, end);
			} else {
				end = title.indexOf(" ...");
				if (end > 0) {
					title = title.substring(0, end);
				}
			}
			while (title.contains("  ")) {
				title = title.replace("  ", " ");
			}
			while (title.contains("\n\n")) {
				title = title.replace("\n\n", "\n");
			}
			map.put(lnk, title);
		}
		return map;
	}
	
	/**
	 * Crawl Chinese fables from {@link www.yuyangushi.com}. 
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> searchZhFable() {
		
		Map<String, String>ret = new HashMap<String, String>();
		
		String request;
		try {
			request = "http://www.yuyangushi.com/plus/search.php?kwtype=0&keyword=" + URLEncoder.encode(query, "GBK");
		} catch (UnsupportedEncodingException e1) {
			Log.e(TAG, e1.toString());
			return ret;
		}
		Document doc = null;
		try {
			doc = Jsoup.connect(request).userAgent(USER_AGENT)
					.timeout(5000).get();
		} catch (IOException e) {
			Log.w(TAG, "IOException occured at searching Chinese fables.");
			return ret;
		}
		//Select the result unsorted list
		Elements form = doc.select(".g-list1");
		
		//Select each item in the list
		Elements items = form.select("li");
		for (Element item : items) {
			//Select the link
			String link = item.select("a[href]").attr("href").toString();
			//Select the title
			String title = item.select("a[href]").html().
					replaceAll("<font color=\"red\">", "").
					replaceAll("</font>", "");
			ret.put(link, title);
		}
		return ret;
	}
}
