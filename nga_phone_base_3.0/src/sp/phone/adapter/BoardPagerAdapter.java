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

public class BoardPagerAdapter extends FragmentStatePagerAdapter {
	
	private BoardHolder boardInfo;
	public BoardPagerAdapter(FragmentManager fm, BoardHolder boardInfo) {
		super(fm);
		this.boardInfo = boardInfo;
	

	}
	@Override
	public Fragment getItem(int arg0) {
		BoardCategory category = boardInfo.getCategory(arg0);
		return BoardPagerFragment.newInstance(JSON.toJSONString(category));
	}
	@Override
	public int getCount() {

		return boardInfo.getCategoryCount();
	}
	@Override
	public CharSequence getPageTitle(int position) {
		
		return boardInfo.getCategoryName(position);
	}
	
	




}
