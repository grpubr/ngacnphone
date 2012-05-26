package sp.phone.fragment;

import sp.phone.activity.ArticleListActivity;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.ThreadData;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ArticleListFragment extends Fragment
	implements OnThreadPageLoadFinishedListener{
	final static private String TAG = ArticleListFragment.class.getSimpleName();
	private ListView listview=null;
	private ArticleListAdapter articleAdpater;
	private JsonThreadLoadTask task;
	private int page=0;
	private int tid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		page = getArguments().getInt("page") + 1;
		tid = getArguments().getInt("id");
		articleAdpater = new ArticleListAdapter(this.getActivity());
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listview = new ListView(this.getActivity());
		
		return listview;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		this.loadPage();
		listview.setAdapter(articleAdpater);
		super.onResume();
	}
	
	private void loadPage(){
		if(null == this.task){
			int index = getArguments().getInt("page");
			Activity activity = getActivity();
			task= new JsonThreadLoadTask(activity,this);
			if(index == 0){
				ActivityUtil.getInstance().noticeSaying(activity);
			}
			

			String url = HttpUtil.Server + 
					"/read.php?tid=" + tid
					+"&page="+page
					+"&lite=js&noprefix";
			task.execute(url);
		}else{
			
		}
		
		
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onOptionsMenuClosed(menu);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	@Override
	public void finishLoad(ThreadData data) {
		Log.d(TAG, "finishLoad");
		if(null != data){
			articleAdpater.setData(data);
			articleAdpater.notifyDataSetChanged();
			ArticleListActivity father = (ArticleListActivity) this.getActivity();
			int exactCount = 1 + data.getThreadInfo().getReplies()/20;
			if(father.getmTabsAdapter().getCount() != exactCount){
				father.getmTabsAdapter().setCount(exactCount);
			}
		}
		
	}
	
	
	

}
