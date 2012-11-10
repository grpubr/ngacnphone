package sp.phone.fragment;

import gov.pianzong.androidnga.R;
import sp.phone.adapter.TabsAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.CheckReplyNotificationTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.StringUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class TopiclistContainer extends Fragment
implements OnTopListLoadFinishedListener{
	final String TAG = TopiclistContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;
	TabHost tabhost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter=null;
	
	private CheckReplyNotificationTask asynTask;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pagerview_article_list,container,false);
		tabhost = (TabHost) v.findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		
		
		fid = 0;
		authorid = 0;
		int pageInUrl = 0;
		String url = getActivity().getIntent().getDataString();
		
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
			fid =  getActivity().getIntent().getIntExtra("fid", 0);
			authorid =  getActivity().getIntent().getIntExtra("authorid", 0);
			searchpost =  getActivity().getIntent().getIntExtra("searchpost", 0);
			favor =  getActivity().getIntent().getIntExtra("favor", 0);
			key =  getActivity().getIntent().getStringExtra("key");
		}
		
		if (null != savedInstanceState)
			fid = savedInstanceState.getInt("fid");

		mTabsAdapter = new TabsAdapter(this.getActivity(), tabhost, mViewPager,
				TopicListFragment.class);
		mTabsAdapter.setArgument("id", fid);
		mTabsAdapter.setArgument("authorid", authorid);
		mTabsAdapter.setArgument("searchpost", searchpost);
		mTabsAdapter.setArgument("favor", favor);
		mTabsAdapter.setArgument("key", key);
		//mTabsAdapter.setCount(100);
		
		if(favor != 0){
			this.getActivity().setTitle(R.string.bookmark_title);
		}
		
		if(!StringUtil.isEmpty(key))
		{
			final String title = this.getResources().getString(android.R.string.search_go)
					+ ":"+key;
			this.getActivity().setTitle(title);
		}

		ActivityUtil.getInstance().noticeSaying(this.getActivity());

		if (savedInstanceState != null) {
			int currentPageInex = savedInstanceState.getInt("tab");
			mTabsAdapter.setCount(currentPageInex + 1);
			mViewPager.setCurrentItem(currentPageInex);
		}else if(pageInUrl !=0){
			mViewPager.setCurrentItem(pageInUrl -1);
		}
		
		
		return v;
	}
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId= R.menu.threadlist_menu;
		inflater.inflate(menuId, menu);

		
		super.onCreateOptionsMenu(menu,inflater);
	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("tab", mViewPager.getCurrentItem());
		outState.putInt("fid", fid);
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
		
		if( mTabsAdapter.getCount() != pageCount)
			mTabsAdapter.setCount(pageCount);
		
	}


}
