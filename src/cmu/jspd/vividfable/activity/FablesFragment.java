package cmu.jspd.vividfable.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cmu.jspd.vividfable.adapter.FableIconAdapter;
import cmu.jspd.vividfable.datamodel.Fable;
import cmu.jspd.vividfable.datamodel.Fable.FableType;

public class FablesFragment extends Fragment {

	private GridView recentFables;
	private GridView favoriteFables;
	
	public FablesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard,
				container, false);

		recentFables = (GridView) rootView
				.findViewById(R.id.recentFables);
		

		recentFables.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Fable fable = (Fable) parent.getItemAtPosition(position);
				openFable(fable);
			}
		});

		favoriteFables = (GridView) rootView
				.findViewById(R.id.favoriteFables);
		

		favoriteFables.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Fable fable = (Fable) parent.getItemAtPosition(position);
				openFable(fable);
			}
		});
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		recentFables.setAdapter(new FableIconAdapter(getActivity(), 
				getActivity().getResources().getConfiguration().locale.getLanguage(),	//Internationalization 
				FableIconAdapter.LIST_FABLES_RECENT, R.layout.grid_single));
		
		favoriteFables.setAdapter(new FableIconAdapter(getActivity(), 
				getActivity().getResources().getConfiguration().locale.getLanguage(),	//Internationalization
				FableIconAdapter.LIST_FABLES_FAVORITE, R.layout.grid_single));
		
		
	}
	
	private void openFable(Fable fable) {
		Intent intent = null;
		if (fable.getType() == FableType.TEXT) {
			intent = new Intent(getActivity(), TextFableActivity.class);
			intent.putExtra(TextFableActivity.LOCAL_FABLE, true);
			intent.putExtra(TextFableActivity.FABLE_TITLE, fable.getTitle());
			intent.putExtra(TextFableActivity.FABLE_CONTENT, fable.getContent());
			intent.putExtra(TextFableActivity.FABLE_ID, fable.getId());
			
		}
		startActivity(intent);
	}
}