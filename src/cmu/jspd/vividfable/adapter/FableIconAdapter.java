package cmu.jspd.vividfable.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cmu.jspd.vividfable.activity.R;
import cmu.jspd.vividfable.datamodel.Fable;
import cmu.jspd.vividfable.datamodel.Fable.FableType;
import cmu.jspd.vividfable.datamodel.FableDataManager;

public class FableIconAdapter extends BaseAdapter {

	public static final int LIST_FABLES_RECENT = 0;
	public static final int LIST_FABLES_FAVORITE = 1;
//	public static final int LIST_FABLES_MOST_VIEWED = 2;

	private Context mContext;
	private List<Fable> fables;
	private int resource;

	public FableIconAdapter(Context c, String lang, int listType, int resource) {
		mContext = c;
		this.resource = resource;
		switch (listType) {
		
		case LIST_FABLES_RECENT: {
			fables = FableDataManager.getInstance(mContext)
					.listRecentFablesForAdapter(lang);
			break;
		}
		case LIST_FABLES_FAVORITE: {
			fables = FableDataManager.getInstance(mContext)
					.listFavoriteFablesForAdapter(lang);
			break;
		}
//		case LIST_FABLES_MOST_VIEWED: {
//			fables = FableDataManager.getInstance(mContext)
//					.listMostViewedFablesForAdapter(lang);
//			break;
//		}
		}
	}

	public int getCount() {
		return fables.size();
	}

	public Object getItem(int position) {
		return fables.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
		
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resource, parent, false);
		}
		
		ImageView imageView = (ImageView)convertView.findViewById(R.id.grid_image);
//		imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setPadding(8, 8, 8, 8);
		
		TextView textView = (TextView)convertView.findViewById(R.id.grid_text);
		
		Fable fable = fables.get(position);
		if (fable.getType() == FableType.TEXT) {
			imageView.setImageResource(R.drawable.text);
		} else {
			imageView.setImageResource(R.drawable.video);
		}
		textView.setText(fable.getTitle());
		
		return convertView;
	}

}