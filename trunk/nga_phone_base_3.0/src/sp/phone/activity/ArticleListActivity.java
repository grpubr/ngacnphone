package sp.phone.activity;

import sp.phone.adapter.TabsAdapter;
import sp.phone.fragment.ArticleListFragment;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class ArticleListActivity extends FragmentActivity
implements PagerOwnner,ResetableArticle {
	TabHost tabhost;
	ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    int tid;
    int pid;
    int authorid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = this.getLayoutInflater().inflate(R.layout.pagerview_article_list, null);
		//setContentView(R.layout.pagerview_article_list);
		int bg = this.getResources().getColor(
				ThemeManager.getInstance().getBackgroundColor());
		v.setBackgroundColor(bg);
		setContentView(v);
		
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager)findViewById(R.id.pager);


		tid = 7;
		
		tid = this.getIntent().getIntExtra("tid", 7);
		//if(null != savedInstanceState)
		//	tid = savedInstanceState.getInt("tid");
		
		pid = this.getIntent().getIntExtra("pid", 0);
		if(0 != pid){
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	        nm.cancel(R.layout.message_article);
		}
		
		authorid = this.getIntent().getIntExtra("authorid", 0);
		//if(null != savedInstanceState)
		//	authorid = savedInstanceState.getInt("authorid");
		
		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,tid,ArticleListFragment.class);
		mTabsAdapter.setAuthorid(authorid);
		mTabsAdapter.setPid(pid);
		
		TextView tv = null;

		//for(int i = 1; i < 6; i++){
		tv = new TextView(this);
		tv.setTextSize(20);
		tv.setText("1");
		tv.setGravity(Gravity.CENTER);
		mTabsAdapter.addTab(tabhost.newTabSpec("1").setIndicator(tv));

		ActivityUtil.getInstance().noticeSaying(this);
		
		
		
		
        if (savedInstanceState != null) {
        	int pageCount = savedInstanceState.getInt("pageCount");
        	mTabsAdapter.setCount(pageCount);
        	mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
        	
        }
		
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
        super.onSaveInstanceState(outState);
        outState.putInt("pageCount",mViewPager.getChildCount());
        outState.putInt("tab",mViewPager.getCurrentItem());
     //   outState.putInt("tid",tid);
        
    }
    
    
    
    
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.articlelist_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;

		MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			lock.setTitle(R.string.unlock_orientation);
			lock.setIcon(android.R.drawable.ic_menu_always_landscape_portrait);
			
		}
		
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
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
		super.onResume();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		return super.onContextItemSelected(item);
	}

	public TabsAdapter getmTabsAdapter() {
		return mTabsAdapter;
	}

	@Override
	public int getCurrentPage() {
		
		return mViewPager.getCurrentItem() + 1;
	}


	@Override
	protected void onDestroy() {
		ActivityUtil.getInstance().dismiss();
		super.onDestroy();
	}

	@Override
	public void reset(int pid, int authorid) {
		this.pid = pid;
		this.authorid = authorid;
		mTabsAdapter.setAuthorid(authorid);
		mTabsAdapter.setPid(pid);
		tabhost.getTabWidget().removeAllViews();
		mTabsAdapter.setCount(1);
		mTabsAdapter.notifyDataSetChanged();
		mViewPager.setAdapter(mTabsAdapter);
		
		
	}



    

}
