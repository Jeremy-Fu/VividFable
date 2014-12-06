package cmu.jspd.vividfable.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import cmu.jspd.vividfable.asynctask.FetchFableTask;
import cmu.jspd.vividfable.datamodel.Fable;
import cmu.jspd.vividfable.datamodel.Fable.FableType;
import cmu.jspd.vividfable.datamodel.FableDataManager;

public class TextFableActivity extends Activity {

	private static final String TAG = "TextFableActivity";
	
	public static final String FABLE_LINK = "FABLE_LINK";
	public static final String FABLE_TITLE = "FABLE_TITLE";
	public static final String LOCAL_FABLE = "LOCAL_FABLE";
	public static final String FABLE_CONTENT = "FABLE_CONTENT";

	public static final String ACTION_FETCH_LINK = "ACTION_FETCH_LINK";

	private TextToSpeech tts;
	private TextView titleView;
	private TextView textView;
	private RatingBar fableRatingBar;
	private Button keepBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.app_name);
		getActionBar().setIcon(R.drawable.ic_launcher);
		getActionBar().show();
		setContentView(R.layout.textfable_view);
		
		titleView = (TextView) findViewById(R.id.fableTitle);
		
		fableRatingBar = (RatingBar) findViewById(R.id.ratingBar);
		
		textView = (TextView) findViewById(R.id.textFableView);
		
		keepBtn = (Button)findViewById(R.id.keepBtn);
		keepBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FableType type = FableType.TEXT;
				String content = (String) textView.getText();
				String title = (String) titleView.getText();
				
				Locale locale = getResources().getConfiguration().locale;
				Fable fable = new Fable(title, locale.getLanguage(), type, content);
				FableDataManager.getInstance(getBaseContext()).saveFullFable(fable);
			}
			
		});
		
		Button rateBtn = (Button) findViewById(R.id.rateBtn);
		
		rateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TextFableActivity.this,
						RatingDialog.class);
				intent.putExtra(RatingDialog.FABLE_TITLE, titleView.getText());
				startActivity(intent);
			}
		});
		
		textView.setText("");
		
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			
			private static final String TAG = "TextToSpeech";
			
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					
					int result = getResources().getConfiguration().locale.getLanguage().equals("zh") ? tts.setLanguage(Locale.CHINESE) : tts.setLanguage(Locale.US);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.w(TAG,
								"This Language is not supported for TTS");
					} else {
						Log.d(TAG, "TTS initialization succeeded.");
					}
				} else {
					Log.e(TAG, "TTS Initilization Failed!");
				}
			}
		});
		
		Intent intent = getIntent();
		
		if (ACTION_FETCH_LINK.equals(intent.getAction())) {
			String link = intent.getStringExtra(FABLE_LINK);
			String title = intent.getStringExtra(FABLE_TITLE);
			titleView.setText(title);
			new FetchFableTask(this, titleView, textView, tts).execute(link);
		} else {

			titleView.setText(intent.getStringExtra(FABLE_TITLE));
			textView.setText(intent.getStringExtra(FABLE_CONTENT));
			keepBtn.setEnabled(false);
			
		}
	}

	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
}
