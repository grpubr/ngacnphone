package sp.phone.fragment;

import sp.phone.activity.R;
import sp.phone.adapter.BoardCatagoryAdapter;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
//import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class BoardPagerFragment extends Fragment {
	String category;
	GridView listview;
	BaseAdapter adapter;
	View v;
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
		  v =  inflater.inflate(R.layout.main, container,false);
		 listview = (GridView)v.findViewById(R.id.gride);
		adapter = new BoardCatagoryAdapter(getResources(), inflater, category);
		//View v = new ImageView(inflater.getContext());
		v.setBackgroundResource(
				ThemeManager.getInstance().getBackgroundColor());
		
		return v;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		OnItemClickListener listener = ReflectionUtil.getOnItemClickListener(getActivity());
		listview.setOnItemClickListener(listener);
		listview.setAdapter(adapter);
		
		
	}

	@Override
	public void onResume() {
		v.setBackgroundResource(
				ThemeManager.getInstance().getBackgroundColor());
		super.onResume();
	}
	
	


	
	

}
