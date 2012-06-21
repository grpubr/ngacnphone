package gov.pianzong.androidnga.activity;

import sp.phone.adapter.TabsAdapter;
import sp.phone.bean.RSSFeed;
import sp.phone.bean.TopicListInfo;
import sp.phone.fragment.TopicListFragment;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.CheckReplyNotificationTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.TabHost;
import gov.pianzong.androidnga.R;

public class TopicListActivity extends FragmentActivity
	implements OnTopListLoadFinishedListener{
	private String TAG = TopicListActivity.class.getSimpleName();
	TabHost tabhost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter=null;
	private CheckReplyNotificationTask asynTask;
	int fid;
	int authorid;
	int searchpost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pagerview_article_list);
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager) findViewById(R.id.pager);

		fid = 0;
		authorid = 0;
		int pageInUrl = 0;
		String url = this.getIntent().getDataString();
		
		if(url != null){
			
			fid = getUrlParameter(url,"fid");
			pageInUrl =getUrlParameter(url,"page");
			authorid = getUrlParameter(url,"authorid");
			searchpost = getUrlParameter(url,"searchpost");
		}
		else
		{
			fid = this.getIntent().getIntExtra("fid", 0);
			authorid = this.getIntent().getIntExtra("authorid", 0);
			searchpost = getUrlParameter(url,"searchpost");
			
		}
		
		if (null != savedInstanceState)
			fid = savedInstanceState.getInt("fid");

		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,
				TopicListFragment.class);
		mTabsAdapter.setArgument("id", fid);
		mTabsAdapter.setArgument("authorid", authorid);
		mTabsAdapter.setArgument("searchpost", searchpost);
		//mTabsAdapter.setCount(100);


		ActivityUtil.getInstance().noticeSaying(this);

		if (savedInstanceState != null) {
			int currentPageInex = savedInstanceState.getInt("tab");
			mTabsAdapter.setCount(currentPageInex + 1);
			mViewPager.setCurrentItem(currentPageInex);
		}else if(pageInUrl !=0){
			mViewPager.setCurrentItem(pageInUrl -1);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.threadlist_menu, menu);
		
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		 ReflectionUtil.actionBar_setDisplayOption(this, flags);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", mViewPager.getCurrentItem());
		outState.putInt("fid", fid);

	}
	
	

	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		
		
		if(asynTask !=null){
			asynTask.cancel(true);
			asynTask = null;
		}
		long now = System.currentTimeMillis();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if(now - config.lastMessageCheck > 60*1000 && config.notification)
		{
			Log.d(TAG, "start to check Reply Notification");
			asynTask = new CheckReplyNotificationTask(this);
			asynTask.execute(config.getCookie());
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//SActivityUtil.getInstance().dismiss();
		super.onDestroy();
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
	public void finishLoad(RSSFeed feed) {

		
	}

	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		int pageCount = result.get__ROWS() / result.get__T__ROWS() +1;
		
		if( mTabsAdapter.getCount() != pageCount)
			mTabsAdapter.setCount(pageCount);
	}
	
	


}
