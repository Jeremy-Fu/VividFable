package cmu.jspd.vividfable.asynctask;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import cmu.jspd.vividfable.config.Environment;

public class FetchFableTask extends AsyncTask<String, Void, String> {

	private static final String TAG = "FetchFableTask";
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:21.0.0) Gecko/20121011 Firefox/21.0.0";

	private ProgressDialog progressDialog;
	private TextView titleView;
	private TextView txtView;
	private Activity ctxt;
	private TextToSpeech textToSpeech;
	private RatingBar ratingBar;

	public FetchFableTask(Activity c, TextView ttl, TextView t, TextToSpeech tts, RatingBar rating) {
		ctxt = c;
		titleView = ttl;
		txtView = t;
		textToSpeech = tts;
		ratingBar = rating;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(ctxt, "Please wait",
				"Fetching content ...", true);
	}

	@Override
	protected String doInBackground(String... urls) {
		
		if (ctxt.getResources().getConfiguration().locale.getLanguage().equals(Environment.CHINESE)) {
			Document doc;
			try {
				doc = Jsoup.connect(urls[0]).userAgent(USER_AGENT)
						.timeout(5000).get();
			} catch (IOException e) {
				return "";
			}
			
			StringBuilder sb = new StringBuilder();
			for(Element para : doc.select(".g-content").select("p")) {
				para.select("*").remove();
				sb.append(para.html().replaceAll("&nbsp;", " ") + "\n\n");
			}
			
			return sb.toString();
			
		}
		try {
			if (urls.length > 0) {
				Document doc = Jsoup.connect(urls[0]).userAgent(USER_AGENT)
						.timeout(5000).get();
				return doc.select("pre").text();
			}
		} catch (Exception ex) {
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		
		progressDialog.dismiss();
		
		//Didn't fetch the content
		if (result == null || result.length() == 0) {
			AlertDialog aDialog = new AlertDialog.Builder(ctxt)
			.setMessage("Unable to fetch content").setTitle("Error")
			.setCancelable(false)
			.setNegativeButton("Close", new OnClickListener() {
				public void onClick(final DialogInterface dialog,
						final int which) {
					ctxt.finish();
				}
			}).create();
			aDialog.show();
			return;
		}
		
		if (ctxt.getResources().getConfiguration().locale.getLanguage().equals(Environment.DEFAULT_LANGUAGE)) {
			showEnFable(result);
		} else {
			showZhFable(result);
		}
		ratingBar.setRating(0);
	}
	
	private void showEnFable(String result) {
		if (result != null) {
			String text = result;
			while (text.contains("  ")) {
				text = text.replace("  ", " ");
			}
			while (text.contains("\n\n")) {
				text = text.replace("\n\n", "\n");
			}
			int start = 0;
			int end = 0;
			boolean opened = false;
			boolean closed = false;
			for (int i = 0; i < text.length() && !closed; i++) {
				char ch = text.charAt(i);
				if (!opened) {
					if ((((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')))) {
						start = i;
						opened = true;
					}
				} else if (!closed && (ch == '\n')) {
					end = i + 1;
					closed = true;
				}
			}
			if (opened && closed) {
				String title = text.substring(start, end);
				titleView.setText(title);
				text = text.substring(end).trim();
			}
			txtView.setText(text);
			textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		} else {
			
		}
	}
	
	private void showZhFable(String result) {
		txtView.setText(result);
		
		if (TextToSpeech.ERROR == textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, null)) {
			Log.e(TAG, "Falied to speak");
		} else {
			Log.i(TAG, "speak!");
		}
	}
}
