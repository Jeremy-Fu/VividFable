package cmu.jspd.vividfable.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

public class YoutubeSearchFableTask extends
		AsyncTask<Void, Void, Map<String, String>> {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:21.0.0) Gecko/20121011 Firefox/21.0.0";

	private ProgressDialog progressDialog;
	private String query;
	private Activity ctxt;

	public YoutubeSearchFableTask(Activity c, String q) {
		ctxt = c;
		query = q;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(ctxt, "Searching...", query, true);
	}

	protected Map<String, String> doInBackground(Void... v) {
		try {
			String searchString = "site:http://www.youtube.com/watch?v= Aesop Fables "
					+ query;
			String request = "https://www.google.com/search?q="
					+ URLEncoder.encode(searchString) + "&num=20";
			Document doc = Jsoup.connect(request).userAgent(USER_AGENT)
					.timeout(5000).get();
			Elements links = doc.select("h3.r a[href]");
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (Element link : links) {
				String lnk = link.attr("href");
				String title = link.text();
				int end = title.indexOf(" ...");
				if (end > 0) {
					title = title.substring(0, end);
				}
				int idx = lnk.indexOf('?');
				if (idx >= 0) {
					lnk = lnk.substring(idx + 1);
				}
				map.put(lnk, title);
			}
			return map;
		} catch (Exception ex) {
		}
		return null;
	}

	protected void onPostExecute(Map<String, String> result) {
		progressDialog.dismiss();
		if (result.size() > 0) {
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
}
