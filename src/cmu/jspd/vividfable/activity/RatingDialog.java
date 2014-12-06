package cmu.jspd.vividfable.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class RatingDialog extends Activity implements
		OnGesturePerformedListener {
	
	public static final String RATING_RESULT = "RATING_RESULT";
	
	public static final String FABLE_TITLE = "FABLE_TITLE";
	
	private GestureLibrary gestureLib;
	private ImageView rateResultView;
	private Button confirm_Button;
	
	private int rating = 1;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent intent = getIntent();
		String title = intent.getStringExtra(FABLE_TITLE);
		setTitle(title);
		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
		View rating = getLayoutInflater().inflate(R.layout.rate_view, null);
		gestureOverlayView.addView(rating);
		gestureOverlayView.addOnGesturePerformedListener(this);
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gestureLib.load()) {
			finish();
		}
		setContentView(gestureOverlayView);
		rateResultView = (ImageView) rating.findViewById(R.id.rateImage);
		confirm_Button = (Button)rating.findViewById(R.id.rate_confirm);
		confirm_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent result = new Intent();
				result.putExtra(RATING_RESULT, RatingDialog.this.rating);
				
				setResult(Activity.RESULT_OK, result);
				finish();
			}
			
		});
		
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		String match = "";
		double max = -1.0;
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if (prediction.score > max) {
					max = prediction.score;
					match = prediction.name;
				}
			}
		}
		
		rating = 1;
		if (max > 1.0) {
			if ("happy".equals(match)) {
				rateResultView.setImageResource(R.drawable.happy_512);
				rating = 3;
			} else if ("none".equals(match)) {
				rateResultView.setImageResource(R.drawable.none_512);
				rating = 2;
			} else if ("sad".equals(match)) {
				rateResultView.setImageResource(R.drawable.sad_512);
				rating = 1;
			}
		}
		
		
		
		
	}
}
