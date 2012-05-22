package sp.phone.fragment;
import sp.phone.activity.BookmarkActivity;
import sp.phone.activity.MainActivity;
import sp.phone.activity.PostActivity;
import sp.phone.activity.R;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.forumoperation.FloorOpener;
import sp.phone.task.TopicListLoadTask;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class TopicListFragment extends Fragment{
	static final String TAG = TopicListFragment.class.getSimpleName();
	ListView listview=null;
	TopicListAdapter adapter=null;
	TopicListLoadTask task=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
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
		listview.setOnItemClickListener(new ItemClicked(getActivity()));
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
		//Log.d(TAG,"onStart" + (1+getArguments().getInt("index")) );
		this.loadPage();
		super.onStart();
	}
	
	

	@Override
	public void onResume() {
		this.setHasOptionsMenu(true);
		Log.d(TAG,"onResume" + (1+getArguments().getInt("index")) );
		
		super.onResume();
	}
	
	private void loadPage(){
		if(task == null)
		{
			task= new TopicListLoadTask(this.getActivity(),adapter);
			String fidString = String.valueOf(getArguments().getInt("fid"));
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
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		task.cancel(false);
		super.onSaveInstanceState(outState);
		outState.putInt("index", this.getArguments().getInt("index"));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.threadlist_menu, menu);
		
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		 ReflectionUtil.actionBar_setDisplayOption(this.getActivity(), flags);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			case R.id.threadlist_menu_newthread :
				handlePostThread(item);
				break;
			case R.id.threadlist_menu_item2 :
				if(task != null){
					task.cancel(false);
					task = null;
				}
				loadPage();
				break;
			case R.id.goto_bookmark_item:
				Intent intent_bookmark = new Intent(this.getActivity(), BookmarkActivity.class);
				startActivity(intent_bookmark);
				break;
			case R.id.threadlist_menu_item3 :
			default:
				//case android.R.id.home:
				Intent intent = new Intent(this.getActivity(), MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean handlePostThread(MenuItem item){
		
	
		String fid = String.valueOf(getArguments().getInt("fid"));
		
		int intFid=0;
		try{
		intFid = Integer.parseInt(fid);
		}catch(Exception e){

			return false;
		}
		Intent intent = new Intent();
		//intent.putExtra("prefix",postPrefix.toString());
		intent.putExtra("fid", intFid);
		intent.putExtra("action", "new");
		
		intent.setClass(this.getActivity(), PostActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.zoom_enter,
				R.anim.zoom_exit);
		return true;
	}
	
	
	class ItemClicked extends FloorOpener
	implements OnItemClickListener{

		public ItemClicked(Activity activity) {
			super(activity);
			
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String guid = (String) parent.getItemAtPosition(position);
			 String url;
			if (guid.indexOf("\n") != -1) {
				url = guid.substring(0, guid.length() - 1);
			} else {
				url = guid;
			}
			handleFloor(url);
			
		}
		
		
	}
	
	
	
	
	
	
}
