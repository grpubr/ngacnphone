package sp.phone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.alibaba.fastjson.JSON;

import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.fragment.BoardPagerFragment;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
import sp.phone.interfaces.PageCategoryOwnner;

public class BoardPagerAdapter extends FragmentStatePagerAdapter {
	
	private PageCategoryOwnner pageCategoryOwnner;
	public BoardPagerAdapter(FragmentManager fm, PageCategoryOwnner pageCategoryOwnner) {
		super(fm);
		this.pageCategoryOwnner =  pageCategoryOwnner;
	

	}
	@Override
	public Fragment getItem(int index) {
		//BoardCategory category = boardInfo.getCategory(arg0);
		return BoardPagerFragment.newInstance(index);
	}
	@Override
	public int getCount() {

		return pageCategoryOwnner.getCategoryCount();
	}
	@Override
	public CharSequence getPageTitle(int position) {
		
		return pageCategoryOwnner.getCategoryName(position);
	}
	
	




}
