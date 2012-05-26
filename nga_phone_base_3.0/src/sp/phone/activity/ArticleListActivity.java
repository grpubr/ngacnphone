package sp.phone.activity;

import sp.phone.adapter.TabsAdapter;
import sp.phone.fragment.ArticleListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.TextView;

public class ArticleListActivity extends FragmentActivity {
	TabHost tabhost;
	ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    int tid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pagerview_article_list);
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager)findViewById(R.id.pager);


		tid = 7;
		
		tid = this.getIntent().getIntExtra("tid", 7);
		if(null != savedInstanceState)
			tid = savedInstanceState.getInt("tid");
		
		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,tid,ArticleListFragment.class);
		
		TextView tv = null;

		//for(int i = 1; i < 6; i++){
		tv = new TextView(this);
		tv.setTextSize(20);
		tv.setText("1");
		tv.setGravity(Gravity.CENTER);
		mTabsAdapter.addTab(tabhost.newTabSpec("1").setIndicator(tv));
		//}
		
		
		
		
        if (savedInstanceState != null) {
        	mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
        }
		
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
        super.onSaveInstanceState(outState);
        outState.putInt("tab",mViewPager.getCurrentItem());
        outState.putInt("tid",tid);
        
    }

	public TabsAdapter getmTabsAdapter() {
		return mTabsAdapter;
	}


    

}
