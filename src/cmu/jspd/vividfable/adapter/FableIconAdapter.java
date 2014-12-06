package cmu.jspd.vividfable.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cmu.jspd.vividfable.activity.R;
import cmu.jspd.vividfable.activity.R.drawable;
import cmu.jspd.vividfable.datamodel.Fable;
import cmu.jspd.vividfable.datamodel.Fable.FableType;
import cmu.jspd.vividfable.datamodel.FableDataManager;

public class FableIconAdapter extends BaseAdapter {

	public static final int LIST_FABLES_RECENT = 0;
	public static final int LIST_FABLES_FAVORITE = 1;
//	public static final int LIST_FABLES_MOST_VIEWED = 2;

	private Context mContext;
	private List<Fable> fables;

	public FableIconAdapter(Context c, String lang, int listType) {
		mContext = c;
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
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		if (fables.get(position).getType() == FableType.TEXT) {
			imageView.setImageResource(R.drawable.text);
		} else {
			imageView.setImageResource(R.drawable.video);
		}
		return imageView;
	}

}