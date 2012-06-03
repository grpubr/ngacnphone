package sp.phone.fragment;
import sp.phone.activity.ArticleListActivity;
import sp.phone.activity.BookmarkActivity;
import sp.phone.activity.MainActivity;
import sp.phone.activity.PostActivity;
import sp.phone.activity.R;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.bean.RSSFeed;
import sp.phone.forumoperation.FloorOpener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.TopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopicListFragment extends Fragment
	implements OnTopListLoadFinishedListener{
	static final String TAG = TopicListFragment.class.getSimpleName();
	ListView listview=null;
	TopicListAdapter adapter=null;
	TopicListLoadTask task=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		if(adapter== null)
			adapter =new TopicListAdapter(this.getActivity());
		Log.d(TAG,"onCreate" + (1+getArguments().getInt("page")) );


		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView" + (1+getArguments().getInt("page")) );
		listview = new ListView(getActivity());
		listview.setOnItemClickListener(new EnterJsonArticle());
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation
					(getActivity(), R.anim.topic_list_anim);
			listview.setLayoutAnimation(anim);
		}
		//this.registerForContextMenu(listview);
		return listview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG,"onActivityCreated" + (1+getArguments().getInt("page")) );
		listview.setAdapter(adapter);
	}
	
	

	@Override
	public void onStart() {
		Log.d(TAG,"onStart" + (1+getArguments().getInt("page")) );
		this.loadPage();
		super.onStart();
	}
	
	

	@Override
	public void onResume() {
		this.setHasOptionsMenu(true);
		Log.d(TAG,"onResume" + (1+getArguments().getInt("page")) );
		
		super.onResume();
	}
	
	private void loadPage(){
		if(task == null)
		{
			int index = getArguments().getInt("page");
			Activity activity = getActivity();
			task= new TopicListLoadTask(activity,this);
			
			String fidString = String.valueOf(getArguments().getInt("id"));
			final String page = String.valueOf(1 + index );
			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
				+ "&page="+ page
				+ "&rss=1";
			PhoneConfiguration config = PhoneConfiguration.getInstance();
			if ( !StringUtil.isEmpty(config.getCookie())) {

				url = url + "&" + config.getCookie().replace("; ", "&");
			}
			task.execute(url);
		}else{
			ActivityUtil.getInstance().dismiss();
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		task.cancel(false);
		super.onSaveInstanceState(outState);
		outState.putInt("page", this.getArguments().getInt("page"));
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected,fid="
				+getArguments().getInt("id")+ 
				",page="+ (1+getArguments().getInt("page")) );
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
				ActivityUtil.getInstance().noticeSaying(getActivity());
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
		
	
		String fid = String.valueOf(getArguments().getInt("id"));
		
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
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			getActivity().overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
		}
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


	class EnterJsonArticle implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String guid = (String) parent.getItemAtPosition(position);
			guid = guid.trim();
			String startTag = "tid=";
			String endTag = "&";
			int start = guid.indexOf(startTag);
			if(start == -1){
				return ;
			}
			start = start + startTag.length();
			
			int end = guid.indexOf(endTag);
			if(end == -1)
				end = guid.length();
			String tidString = guid.substring(start,end);
			Integer tid = Integer.valueOf(tidString);
			
			Intent intent = new Intent();
			intent.putExtra("tab", "1");
			intent.putExtra("tid",tid.intValue() );
			intent.setClass(getActivity(), ArticleListActivity.class);
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
			
		}
		
	}
	
	
	@Override
	public void finishLoad(RSSFeed feed) {
		if(feed !=null && getActivity() !=null )
			getActivity().setTitle(feed.getTitle());
		adapter.finishLoad(feed);
		
	}



	
	
	
	
	
}
