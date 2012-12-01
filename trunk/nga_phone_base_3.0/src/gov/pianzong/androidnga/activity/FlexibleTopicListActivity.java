package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.TopicListInfo;
import sp.phone.fragment.ArticleContainerFragment;
import sp.phone.fragment.TopiclistContainer;
import sp.phone.interfaces.EnterJsonArticle;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FlexibleTopicListActivity extends FragmentActivity 
implements OnTopListLoadFinishedListener,OnItemClickListener
,OnThreadPageLoadFinishedListener{

	private String TAG = FlexibleTopicListActivity.class.getSimpleName() ;
	boolean dualScreen = true;
	


	@Override
	protected void onCreate(Bundle arg0) {
		this.setContentView(R.layout.toplist_activity_two_panel);
		super.onCreate(arg0);
		Fragment f1 = new TopiclistContainer();
		Bundle args = new Bundle(getIntent().getExtras());
		args.putString("url", getIntent().getDataString());
		f1.setArguments(args);
		
		//Fragment f = ArticleContainerFragment.create(5769306, 0, 0);
		if(null == findViewById(R.id.item_detail_container))
			dualScreen = false;
		FragmentTransaction ft = getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.item_list, f1);
			//.add(R.id.item_detail_container, f);
		ft.commit();
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		Fragment topicContainer = getSupportFragmentManager()	
									.findFragmentById(R.id.item_list);
									
		OnTopListLoadFinishedListener listener = null;
		try{
			listener = (OnTopListLoadFinishedListener)topicContainer;
			if(listener != null)
				listener.jsonfinishLoad(result);
		}catch(ClassCastException e){
			Log.e(TAG , "topicContainer should implements " + OnTopListLoadFinishedListener.class.getCanonicalName());
		}
	}

	private OnItemClickListener onItemClickNewActivity = null;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		

		
		if(!dualScreen){
			if(null == onItemClickNewActivity){
				onItemClickNewActivity = new EnterJsonArticle(this);
			}
			onItemClickNewActivity.onItemClick(parent, view, position, id);
			
		}else
		{
			String guid = (String) parent.getItemAtPosition(position);
			if(StringUtil.isEmpty(guid))
				return;
			
			guid = guid.trim();
	
			int pid = StringUtil.getUrlParameter(guid, "pid");
			int tid = StringUtil.getUrlParameter(guid, "tid");
			int authorid = StringUtil.getUrlParameter(guid, "authorid");
			ArticleContainerFragment f = ArticleContainerFragment.create(tid, pid, authorid);
			//Fragment f = new TopiclistContainer();
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			
			ft.replace(R.id.item_detail_container, f);
				
			ft.commit();
		}
		
	}

	@Override
	public void finishLoad(ThreadData data) {
		/*int exactCount = 1 + data.getThreadInfo().getReplies()/20;
		if(father.getmTabsAdapter().getCount() != exactCount
				&&this.authorid == 0){
			father.getmTabsAdapter().setCount(exactCount);
		}
		father.setTitle(StringUtil.unEscapeHtml(data.getThreadInfo().getSubject()));
		*/
		
		Fragment articleContainer = getSupportFragmentManager()	
				.findFragmentById(R.id.item_detail_container);
				
		OnThreadPageLoadFinishedListener listener = null;
		try{
			listener = (OnThreadPageLoadFinishedListener)articleContainer;
			if(listener != null){
				listener.finishLoad(data);
				setTitle(StringUtil.unEscapeHtml(
						data.getThreadInfo().getSubject())
						);
			}
		}catch(ClassCastException e){
			Log.e(TAG , "detailContainer should implements OnThreadPageLoadFinishedListener");
		}
		
		
	}

}
