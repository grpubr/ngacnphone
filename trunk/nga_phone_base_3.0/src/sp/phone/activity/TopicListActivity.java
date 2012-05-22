package sp.phone.activity;

import sp.phone.adapter.TabsAdapter;
import sp.phone.fragment.TopicListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.TextView;

public class TopicListActivity extends FragmentActivity{

	TabHost tabhost;
	ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pagerview_article_list);
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager);
		
		TextView tv = null;
		/*TextView tv = new TextView(this);
		tv.setBackgroundResource(R.drawable.page_per);
		mTabsAdapter.addTab(tabhost.newTabSpec("pre").setIndicator(tv),
				ArticleListFragment.class, null);*/
		for(int i = 1; i < 6; i++){
		tv = new TextView(this);
		tv.setTextSize(20);
		tv.setText(String.valueOf(i));
		tv.setGravity(Gravity.CENTER);
		Bundle args = new Bundle();
		args.putInt("index", i);
		mTabsAdapter.addTab(tabhost.newTabSpec(String.valueOf(i)).setIndicator(tv),
				TopicListFragment.class, args);
		}
		
		
		
		
        if (savedInstanceState != null) {
        	tabhost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
		
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabhost.getCurrentTabTag());
        
    }



	

}
