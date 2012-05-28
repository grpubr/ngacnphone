package sp.phone.fragment;

import sp.phone.activity.R;
import sp.phone.adapter.BoardCatagoryAdapter;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class BoardPagerFragment extends Fragment {
	private static final String TAG = BoardPagerFragment.class.getSimpleName();
	String category;
	GridView listview;
	BaseAdapter adapter;
	//View v;
	public static Fragment newInstance(String category)
	{
		Fragment f = new BoardPagerFragment();


		
        Bundle args = new Bundle();
       

        args.putString("category", category);
        f.setArguments(args);
        return f;
		
	}
	/*private BoardPagerFragment(BoardCategory category,OnItemClickListener listener){
		this.listener = listener;
		this.category = category;
	}*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		category = getArguments() != null ? 
				getArguments().getString("category") : "";
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listview =  (GridView) inflater.inflate(R.layout.category_grid, container,false);
		adapter = new BoardCatagoryAdapter(getResources(), inflater, category);
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation
				(this.getActivity(), R.anim.grid_wave_scale);
			listview.setLayoutAnimation(anim);
		}
		listview.setBackgroundResource(
				ThemeManager.getInstance().getBackgroundColor());
		
		return listview;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		OnItemClickListener listener = null;
		try{
			listener = (OnItemClickListener) getActivity();
		}catch(ClassCastException e){
			Log.e(TAG, "activty should implements " + OnItemClickListener.class.getSimpleName());
		}
		
		listview.setOnItemClickListener(listener);
		listview.setAdapter(adapter);
		
		
	}

	@Override
	public void onResume() {
		listview.setBackgroundResource(
				ThemeManager.getInstance().getBackgroundColor());
		super.onResume();
	}
	
	


	
	

}
