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
import android.widget.Toast;
import cmu.jspd.vividfable.asynctask.FetchFableTask;
import cmu.jspd.vividfable.datamodel.Fable;
import cmu.jspd.vividfable.datamodel.Fable.FableType;
import cmu.jspd.vividfable.datamodel.FableDataManager;

public class TextFableActivity extends Activity {

	private static final String TAG = "TextFableActivity";
	
	private static final int RATING_REQUEST_CODE = 1;
	
	public static final String FABLE_ID = "FABLE_ID";
	public static final String FABLE_LINK = "FABLE_LINK";
	public static final String FABLE_TITLE = "FABLE_TITLE";
	public static final String LOCAL_FABLE = "LOCAL_FABLE";
	public static final String FABLE_CONTENT = "FABLE_CONTENT";

	public static final String ACTION_FETCH_LINK = "ACTION_FETCH_LINK";
	
	private TextToSpeech textToSpeech;
	private TextView titleView;
	private TextView textView;
	private RatingBar fableRatingBar;
	private Button keepBtn;
	private long fableId = Long.MAX_VALUE;

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
			public void onClick(View keepBtnView) {
				FableType type = FableType.TEXT;
				String content = (String) textView.getText();
				String title = (String) titleView.getText();
				
				Locale locale = getResources().getConfiguration().locale;
				Fable fable = new Fable(Long.MAX_VALUE, title, locale.getLanguage(), type, content);
				Fable savedFable = FableDataManager.getInstance(getBaseContext()).saveFullFable(fable);
				if (savedFable.getId() != -1L) {
					Toast.makeText(TextFableActivity.this, "Saved the fable id = " + savedFable.getId(), Toast.LENGTH_SHORT).show();
					fableId = savedFable.getId();
					keepBtnView.setEnabled(false);
				}
				
			}
			
		});
		
		Button ratingBtn = (Button) findViewById(R.id.rateBtn);
		
		ratingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TextFableActivity.this,
						RatingDialog.class);
				intent.putExtra(RatingDialog.FABLE_TITLE, titleView.getText());
				TextFableActivity.this.startActivityForResult(intent, RATING_REQUEST_CODE);
			}
		});
		
		textView.setText("");
		
		textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			
			private static final String TAG = "TextToSpeech";
			
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					
					//Default Language: English, Other Language: Chinese
					int result = getResources().getConfiguration().locale.getLanguage().equals("zh") ? 
							textToSpeech.setLanguage(Locale.CHINESE) : textToSpeech.setLanguage(Locale.US);
					
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.w(TAG,
								"This Language is not supported for TTS");
						
					} else {
						Log.d(TAG, "TTS initialization succeeded.");
						
						if(textView.getText().toString().length() > 0 &&
								TextToSpeech.SUCCESS == textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null)) {
							
						} else if (textView.getText().toString().length() == 0){
							//DO nothing
						} else {
							Toast.makeText(TextFableActivity.this, "Failed to load audio, please check your Internet...", Toast.LENGTH_LONG).show();
						}
					}
				} else {
					Log.e(TAG, "TTS Initilization Failed!");
				}
			}
		});
		
		Intent intent = getIntent();
		
		//Fetch the fable
		if (ACTION_FETCH_LINK.equals(intent.getAction())) { //Fetch the fable from Internet
			String link = intent.getStringExtra(FABLE_LINK);
			String title = intent.getStringExtra(FABLE_TITLE);
			titleView.setText(title);
			new FetchFableTask(this, titleView, textView, textToSpeech, fableRatingBar).execute(link);
		} else {											//Fetch the fable locally

			titleView.setText(intent.getStringExtra(FABLE_TITLE));
			textView.setText(intent.getStringExtra(FABLE_CONTENT));
			keepBtn.setEnabled(false);
			
			fableId = intent.getLongExtra(FABLE_ID, Long.MAX_VALUE);
			
		}
	}
	

	@Override
	public void onDestroy() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RATING_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				if (fableId == Long.MAX_VALUE) {
					Toast.makeText(this, "Failed to rate. Keep it first.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Fable updatedFable = FableDataManager.getInstance(getApplicationContext()).updateFableRating(fableId, data.getIntExtra(RatingDialog.RATING_RESULT, -1));
				
				if (updatedFable != null &&
						updatedFable.getRating() == data.getIntExtra(RatingDialog.RATING_RESULT, -1)) {
					fableRatingBar.setRating(updatedFable.getRating());
					Toast.makeText(this, "Successfully rate " + 
						data.getIntExtra(RatingDialog.RATING_RESULT, -1), Toast.LENGTH_SHORT).show();
					
				} else {
					Toast.makeText(this, "Failed to rate.", Toast.LENGTH_SHORT).show();
				}
				
			}
		}
	}
}
