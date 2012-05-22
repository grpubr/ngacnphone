package sp.phone.adapter;

import java.util.ArrayList;

import sp.phone.activity.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class TabsAdapter extends FragmentStatePagerAdapter implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	static final String TAG = TabsAdapter.class.getSimpleName();
	private final Context mContext;
	private final TabHost mTabHost;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private int offset = 0;
	private final int fid;
	
	static final class TabInfo {
		private final String tag;
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(String _tag, Class<?> _class, Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	public TabsAdapter(FragmentActivity activity, TabHost tabHost,
			ViewPager pager, int fid) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mTabHost = tabHost;
		mViewPager = pager;
		mTabHost.setOnTabChangedListener(this);
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
		this.fid = fid;
	}

	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		tabSpec.setContent(new DummyTabFactory(mContext));
		String tag = tabSpec.getTag();

		TabInfo info = new TabInfo(tag, clss, args);
		mTabs.add(info);
		mTabHost.addTab(tabSpec);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return 100;//mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		Log.d(TAG, "get framgent:" + position);
		int tab_count = mTabHost.getTabWidget().getChildCount();
		offset = position/ tab_count * tab_count;
		TabInfo info = mTabs.get(position-offset);
		info.args.putInt("index", position);
		info.args.putInt("fid", fid);
		Fragment f = Fragment.instantiate(mContext, info.clss.getName(), info.args);
		
		return f;
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		//mTabHost.get
		TextView v = (TextView) mTabHost.getCurrentTabView();
		int defaultColor = v.getCurrentTextColor();
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			v = (TextView)mTabHost.getTabWidget().getChildAt(i);
			v.setText(String.valueOf(i+offset+1));
			if (mTabHost.getCurrentTab() == i) {
				v.setTextColor(R.color.black);
			} else {
				v.setTextColor(defaultColor);
			}
		}
		mViewPager.setCurrentItem(position+offset);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		// Unfortunately when TabHost changes the current tab, it kindly
		// also takes care of putting focus on it when not in touch mode.
		// The jerk.
		// This hack tries to prevent this from pulling focus out of our
		// ViewPager.
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		int tab_count = mTabHost.getTabWidget().getChildCount();
		offset = position/ tab_count * tab_count;// & ~(mTabHost.getTabWidget().getChildCount() -1);
		mTabHost.setCurrentTab(position-offset);
		widget.setDescendantFocusability(oldFocusability);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}