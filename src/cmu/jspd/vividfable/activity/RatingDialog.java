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
import android.widget.ImageView;

public class RatingDialog extends Activity implements
		OnGesturePerformedListener {
	private GestureLibrary gestureLib;
	private ImageView rateView;
	public static final String FABLE_TITLE = "FABLE_TITLE";

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
		rateView = (ImageView) rating.findViewById(R.id.rateImage);
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
		if (max > 1.0) {
			if ("happy".equals(match)) {
				rateView.setImageResource(R.drawable.happy_512);
			} else if ("none".equals(match)) {
				rateView.setImageResource(R.drawable.none_512);
			} else if ("sad".equals(match)) {
				rateView.setImageResource(R.drawable.sad_512);

			}
		}
	}
}
