package sp.phone.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class ThreadFragmentAdapter extends FragmentStatePagerAdapter {

	private int pageCount=0;
	private Bundle arguments = new Bundle();
	private final Context mContext;
	private final Class<?> clss;
	
	public ThreadFragmentAdapter(FragmentActivity activity,
			ViewPager pager,
			Class<?> FragmentClass) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		this.clss = FragmentClass;
		pager.setAdapter(this);
	}

	@Override
	public Fragment getItem(int position) {
		Bundle args = new Bundle(arguments);
		args.putInt("page", position);
		Fragment f = Fragment.instantiate(mContext, clss.getName(), args);
		
		return f;
	}

	@Override
	public int getCount() {

		return pageCount;
	}
	
	public void setCount(int pageCount){
		this.pageCount = pageCount;
		this.notifyDataSetChanged();
	}
	
	public void setArgument(String key, int value){
		arguments.putInt(key, value);
	}
	
	public void setArgument(String key, String value){
		arguments.putString(key, value);
	}

	@Override
	public float getPageWidth(int position) {
		if(pageCount == 1){
			return 1.0f;
		}
		return 0.9f;
	}

}
