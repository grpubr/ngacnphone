package sp.phone.fragment;

import gov.pianzong.androidnga.R;
import sp.phone.adapter.ThreadFragmentAdapter;
import sp.phone.bean.ThreadData;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ArticleContainerFragment extends Fragment 
implements OnThreadPageLoadFinishedListener{
	public static ArticleContainerFragment create(int tid, int pid, int authorid){
		ArticleContainerFragment f = new ArticleContainerFragment();
		Bundle args = new Bundle ();
		args.putInt("tid", tid);
		args.putInt("pid", pid);
		args.putInt("authorid", authorid);
		f.setArguments(args);
		return f;
	}
	
	public ArticleContainerFragment() {
		super();
	}
	
	//TabHost tabhost;
	ViewPager  mViewPager;
	ThreadFragmentAdapter mTabsAdapter;
    int tid;
    int pid;
    int authorid;
	private static final String TAG= "ArticleContainerFragment";
	private static final String GOTO_TAG = "goto";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View v = inflater.inflate(R.layout.pagerview_article_list, container,false);
		
		
		/*if(PhoneConfiguration.getInstance().uploadLocation
				&& PhoneConfiguration.getInstance().location == null
				)
		{
			ActivityUtil.reflushLocation(getActivity());
		}*/
			
		//tabhost = (TabHost) v.findViewById(android.R.id.tabhost);
		//tabhost.setup();
		View v  = inflater.inflate(R.layout.article_viewpager, container,false);


		mViewPager = (ViewPager) v.findViewById(R.id.pager);

		
		tid = this.getArguments().getInt("tid", 0);		
		pid = this.getArguments().getInt("pid", 0);
		authorid = this.getArguments().getInt("authorid", 0);
		
		
		mTabsAdapter = new ThreadFragmentAdapter(getActivity(), mViewPager,ArticleListFragment.class);
				//new TabsAdapter(getActivity(), tabhost, mViewPager,ArticleListFragment.class);
		mTabsAdapter.setArgument("id", tid);
		mTabsAdapter.setArgument("pid", pid);
		mTabsAdapter.setArgument("authorid", authorid);
		
		//ActivityUtil.getInstance().noticeSaying(getActivity());
		
        if (savedInstanceState != null) {
        	int pageCount = savedInstanceState.getInt("pageCount");
        	if(pageCount!=0)
        	{
        		mTabsAdapter.setCount(pageCount);
        		mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
        	}
        	
        }else{
        	mTabsAdapter.setCount(1);
        }
		
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageCount",mTabsAdapter.getCount());
        outState.putInt("tab",mViewPager.getCurrentItem());
	}

	@Override
	public void finishLoad(ThreadData data) {
		int exactCount = 1 + data.getThreadInfo().getReplies()/20;
		if(mTabsAdapter.getCount() != exactCount
				&&this.authorid == 0){
			mTabsAdapter.setCount(exactCount);
		}
		
		
	}

	
	
}
