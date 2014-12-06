package cmu.jspd.vividfable.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;
import cmu.jspd.vividfable.datamodel.Fable;
import cmu.jspd.vividfable.datamodel.Fable.FableType;

public class Utility {

	private static final String TAG = "Utility";

	/**
	 * Convert a string to FableType representation.
	 * 
	 * @param fableTypeString
	 * @return
	 */
	public static FableType convertFableType(String fableTypeString) {
		if (fableTypeString.equals(FableType.TEXT.toString())) {
			return FableType.TEXT;
		} else if (fableTypeString.equals(FableType.VIDEO.toString())) {
			return FableType.VIDEO;
		} else {
			Log.e(TAG, "Not a valid FableType string (" + fableTypeString + ")");
			throw new RuntimeException("Not a valid FableType string ("
					+ fableTypeString + ")");

		}
	}

	public static Fable parseRawFableFile(Context context, int Rid)
			throws IOException {

		InputStream in = context.getResources().openRawResource(Rid);
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader fin = new BufferedReader(reader);

		String line;

		line = fin.readLine();
		String title = line.split(":")[1];
		Log.d(TAG, "title: " + title);
		
		line = fin.readLine();
		String language = line.split(":")[1];
		Log.d(TAG, "language: " + language);
		
		line = fin.readLine();
		FableType type;
		if (line.split(":")[1].equals(FableType.TEXT.toString())) {
			type = FableType.TEXT;
		} else if (line.split(":")[1].equals(FableType.VIDEO.toString())) {
			type = FableType.VIDEO;
		} else {
			throw new RuntimeException(String.format("Invalid fable type %s", line.split(":")[1]));
		}

		StringBuilder sb = new StringBuilder();
		fin.readLine();
		while((line = fin.readLine()) != null) {
			sb.append(line + "\n");
		}
		

		return new Fable(Long.MAX_VALUE, title, language, type, sb.toString());
	}

}
