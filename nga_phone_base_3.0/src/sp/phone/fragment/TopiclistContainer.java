package sp.phone.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gov.pianzong.androidnga.R;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TopiclistContainer extends Fragment
implements OnTopListLoadFinishedListener{
	final String TAG = TopiclistContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	private PullToRefreshListView mPullRefreshListView;
	AppendableTopicAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mPullRefreshListView = new PullToRefreshListView(getActivity());
		adapter = new AppendableTopicAdapter(this.getActivity());
		mPullRefreshListView.setAdapter(adapter);
		try{
			OnItemClickListener listener = (OnItemClickListener) getActivity();
			mPullRefreshListView.setOnItemClickListener(listener);
		}catch(ClassCastException e){
			Log.e(TAG, "father activity should implenent OnItemClickListener");
		}
		
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullRefreshListView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				// Do work to refresh the list here.
				//new GetDataTask().execute();
				refresh();
			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				//Toast.makeText(PullToRefreshListActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
				JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),adapter);
				task.execute(getUrl(adapter.getNextPage()));
			}
		});
		
		

		
		fid = 0;
		authorid = 0;
		int pageInUrl = 0;
		String url = getArguments().getString("url");
		
		if(url != null){
			
			fid = getUrlParameter(url,"fid");
			pageInUrl =getUrlParameter(url,"page");
			authorid = getUrlParameter(url,"authorid");
			searchpost = getUrlParameter(url,"searchpost");
			favor = getUrlParameter(url,"favor");
			key = StringUtil.getStringBetween(url, 0, "key=", "&").result;
		}
		else
		{
			fid =  getArguments().getInt("fid", 0);
			authorid =  getArguments().getInt("authorid", 0);
			searchpost =  getArguments().getInt("searchpost", 0);
			favor =  getArguments().getInt("favor", 0);
			key =  getArguments().getString("key");
		}
		
		if (null != savedInstanceState)
			fid = savedInstanceState.getInt("fid");


		
		if(favor != 0){
			this.getActivity().setTitle(R.string.bookmark_title);
		}
		
		if(!StringUtil.isEmpty(key))
		{
			final String title = this.getResources().getString(android.R.string.search_go)
					+ ":"+key;
			this.getActivity().setTitle(title);
		}
		
		//JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),this);
		//task.execute(getUrl(1));
		
		this.refresh();
		


		
		
		return mPullRefreshListView;
	}
	
	void refresh(){
		JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),this);
		ActivityUtil.getInstance().noticeSaying(this.getActivity());
		task.execute(getUrl(1));
	}
	
	String getUrl(int page){
		
		String jsonUri = HttpUtil.Server + "/thread.php?";
		if( 0 != fid)
			jsonUri +="fid=" + fid + "&";
		if(0!= authorid)
			jsonUri +="authorid=" + authorid + "&";
		if(searchpost !=0)
			jsonUri +="searchpost=" + searchpost + "&";
		if(favor !=0)
			jsonUri +="favor=" + favor + "&";
		if(!StringUtil.isEmpty(key)){
			try {
				jsonUri += "key=" + URLEncoder.encode(key, "GBK") + "&";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		jsonUri += "page="+ page + "&lite=js&noprefix";
		
		return jsonUri;
	}
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId= R.menu.threadlist_menu;
		inflater.inflate(menuId, menu);

		
		super.onCreateOptionsMenu(menu,inflater);
	}



	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}



	private int getUrlParameter(String url, String paraName){
		if(StringUtil.isEmpty(url))
		{
			return 0;
		}
		final String pattern = paraName+"=" ;
		int start = url.indexOf(pattern);
		if(start == -1)
			return 0;
		start +=pattern.length();
		int end = url.indexOf("&",start);
		if(end == -1)
			end = url.length();
		String value = url.substring(start,end);
		int ret = 0;
		try{
			ret = Integer.parseInt(value);
		}catch(Exception e){
			Log.e(TAG, "invalid url:" + url);
		}
		
		return ret;
	}



	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		int lines = 35;
		if(authorid !=0)
			lines = 20;
		int pageCount = result.get__ROWS() / lines ;
		if( pageCount * lines < result.get__ROWS() )
			pageCount++;
		
		if(searchpost !=0)//can not get exact row counts
		{
			int page = result.get__ROWS();
			pageCount = page;
			if(result.get__T__ROWS() == lines)
				pageCount++;
		}

		adapter.clear();
		adapter.jsonfinishLoad(result);
		mPullRefreshListView.onRefreshComplete();
		//mPullRefreshListView.setAdapter(adapter);
		//adapter.notifyDataSetChanged();
		//mPullRefreshListView.setAdapter(adapter);
		
	}


}
