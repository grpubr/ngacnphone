package sp.phone.fragment;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.task.TopicListLoadTask;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TopicListFragment extends Fragment{
	static final String TAG = TopicListFragment.class.getSimpleName();
	ListView listview=null;
	TopicListAdapter adapter=null;
	TopicListLoadTask task=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//if(listview == null)
		//	listview = new ListView(getActivity());
		if(adapter== null)
			adapter =new TopicListAdapter(this.getActivity());
		Log.d(TAG,"onCreate" + (1+getArguments().getInt("index")) );


		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView" + (1+getArguments().getInt("index")) );
		listview = new ListView(getActivity());
		return listview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG,"onActivityCreated" + (1+getArguments().getInt("index")) );
		listview.setAdapter(adapter);
	}
	
	

	@Override
	public void onStart() {
		Log.d(TAG,"onStart" + (1+getArguments().getInt("index")) );
		if(task == null)
		{
			task= new TopicListLoadTask(this.getActivity(),adapter);
			String fidString = "-7";
			final String page = String.valueOf(1 + getArguments().getInt("index") );
			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
				+ "&page="+ page
				+ "&rss=1";
			PhoneConfiguration config = PhoneConfiguration.getInstance();
			if ( !StringUtil.isEmpty(config.getCookie())) {

				url = url + "&" + config.getCookie().replace("; ", "&");
			}
			task.execute(url);
		}
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		task.cancel(false);
		super.onSaveInstanceState(outState);
		outState.putInt("index", this.getArguments().getInt("index"));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	
	
}
