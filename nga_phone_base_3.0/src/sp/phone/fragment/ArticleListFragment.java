package sp.phone.fragment;

import gov.pianzong.androidnga.R;
import gov.pianzong.androidnga.activity.ArticleListActivity;
import gov.pianzong.androidnga.activity.PostActivity;
import gov.pianzong.androidnga.activity.TopicListActivity;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

public class ArticleListFragment extends Fragment
	implements OnThreadPageLoadFinishedListener,PerferenceConstant{
	final static private String TAG = ArticleListFragment.class.getSimpleName();
	static final int QUOTE_ORDER = 0;
	static final int REPLY_ORDER = 1;
	static final int COPY_CLIPBOARD_ORDER = 2;
	static final int SHOW_THISONLY_ORDER = 3;
	static final int SHOW_MODIFY_ORDER = 4;
	static final int SHOW_ALL = 5;
	static final int POST_COMMENT = 6;
	private ListView listview=null;
	private ArticleListAdapter articleAdpater;
	private JsonThreadLoadTask task;
	private int page=0;
	private int tid;
	private int pid;
	private int authorid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		page = getArguments().getInt("page") + 1;
		tid = getArguments().getInt("id");
		pid = getArguments().getInt("pid", 0);
		authorid = getArguments().getInt("authorid", 0);
		articleAdpater = new ArticleListAdapter(this.getActivity());
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listview = new ListView(this.getActivity());
		
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation
					(getActivity(), R.anim.article_list_anim);
			listview.setLayoutAnimation(anim);
		}
		this.registerForContextMenu(listview);
		return listview;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		listview.setAdapter(articleAdpater);
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume pid="+pid+"&page="+page);
		this.setHasOptionsMenu(true);
		if (PhoneConfiguration.getInstance().isRefreshAfterPost()) {
			
			PagerOwnner father = null;
			try{
				 father = (PagerOwnner) getActivity();
			}catch(ClassCastException e){
				Log.e(TAG,"father activity does not implements interface " 
						+ PagerOwnner.class.getName());
				return ;
			}
			if(father.getCurrentPage() == page){
				PhoneConfiguration.getInstance().setRefreshAfterPost(false);
				this.task = null;
			}
			
		}
		this.loadPage();
		super.onResume();
	}
	
	
	
	private void loadPage(){
		if(null == this.task){

			Activity activity = getActivity();
			task= new JsonThreadLoadTask(activity,this);
			String url = HttpUtil.Server + 
					"/read.php?"
					+"&page="+page
					+"&lite=js&noprefix";
			if(tid !=0)
				url = url + "&tid="+ tid;
			if(pid !=0){
				url = url + "&pid="+ pid;
			}
			
			if(authorid !=0){
				url = url + "&authorid="+ authorid;
			}
			task.execute(url);
		}else{
			ActivityUtil.getInstance().dismiss();
		}
		
		
	}
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.d(TAG, "onOptionsItemSelected,tid="
				+tid+ ",page="+page );
		
		ResetableArticle restNotifier = null;
		try{
			
			 restNotifier= (ResetableArticle)getActivity();
		}catch(ClassCastException e){
			Log.e(TAG,"father activity does not implements interface " 
					+ ResetableArticle.class.getName());
			return true;
		}
		switch( item.getItemId())
		{
			case R.id.article_menuitem_reply:
				//if(articleAdpater.getData() == null)
				//	return false;
				String tid = String.valueOf(this.tid);
				Intent intent = new Intent();
				intent.putExtra("prefix", "" );
				intent.putExtra("tid", tid);
				intent.putExtra("action", "reply");
				
				intent.setClass(getActivity(), PostActivity.class);
				startActivity(intent);
				if(PhoneConfiguration.getInstance().showAnimation)
					getActivity().overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
				break;
			case R.id.article_menuitem_refresh:
				this.task = null;
				ActivityUtil.getInstance().noticeSaying(getActivity());
				this.loadPage();/*
				if(this.pid == 0 && this.authorid ==0)
				{
					this.task = null;
					ActivityUtil.getInstance().noticeSaying(getActivity());
					this.loadPage();
				}else{
					restNotifier.reset(0, 0);
					ActivityUtil.getInstance().noticeSaying(getActivity());
				}*/
				break;
			case R.id.article_menuitem_addbookmark:
				ThreadPageInfo info = articleAdpater.getData().getThreadInfo();
				String bookmarkUrl = "http://bbs.ngacn.cc/read.php?tid="
						+info.getTid();
				
				String title = info.getSubject();
				boolean ret = PhoneConfiguration.getInstance().addBookmark(bookmarkUrl, title);
				if(ret){
					Toast.makeText(getActivity(), R.string.book_mark_successfully, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getActivity(), R.string.already_bookmarked, Toast.LENGTH_LONG).show();
					break;
				}
				
				SharedPreferences share = getActivity().getSharedPreferences(PERFERENCE,
						Activity.MODE_PRIVATE);
				Editor editor = share.edit();
				String bookmarks = JSON.toJSONString(PhoneConfiguration.getInstance().getBookmarks());
				editor.putString(BOOKMARKS, bookmarks);
				editor.commit();
				break;
			case R.id.article_menuitem_lock:
				
				handleLockOrientation(item);
				break;
			case R.id.article_menuitem_back:
			default:
				Intent intent2 = new Intent(this.getActivity(), TopicListActivity.class);
	            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            if(articleAdpater.getData() !=null){
	            	int fid = articleAdpater.getData().getThreadInfo().getFid();
		            intent2.putExtra("fid", fid);
	            }
	            
	            startActivity(intent2);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void handleLockOrientation(MenuItem item){
		int preOrentation = ThemeManager.getInstance().screenOrentation;
		int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		
		if(preOrentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				preOrentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			//restore
			//int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
			ThemeManager.getInstance().screenOrentation = newOrientation;
			
			getActivity().setRequestedOrientation(newOrientation);
			item.setTitle(R.string.lock_orientation);
			item.setIcon(android.R.drawable.ic_lock_idle_lock);

			
		}else{
			newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			Display dis = getActivity().getWindowManager().getDefaultDisplay();
			//Point p = new Point();
			//dis.getSize(p);
			if(dis.getWidth() < dis.getHeight()){
				newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			
			ThemeManager.getInstance().screenOrentation = newOrientation;
			getActivity().setRequestedOrientation(newOrientation);			
			item.setTitle(R.string.unlock_orientation);
			item.setIcon(android.R.drawable.ic_menu_always_landscape_portrait);
		}
		
		SharedPreferences share = getActivity().getSharedPreferences(PERFERENCE,
				Activity.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(SCREEN_ORENTATION, newOrientation);
		editor.commit();
		
	}
	
	
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0,QUOTE_ORDER,0, R.string.quote_subject);
		//if(articleAdpater.getData() !=null){
			ThreadPageInfo info = articleAdpater.getData().getThreadInfo();
			
			//if(info!= null && String.valueOf(info.getAuthorid()).equals(PhoneConfiguration.getInstance().getUid()))
			//{
				menu.add(0,POST_COMMENT,0, R.string.post_comment);	
			//}
		//}
		if(this.pid == 0){
		menu.add(0,REPLY_ORDER,0, R.string.reply_thread);
		menu.add(0,COPY_CLIPBOARD_ORDER,0, R.string.copy_to_clipboard);
		menu.add(0,SHOW_THISONLY_ORDER,0, R.string.show_this_person_only);
		menu.add(0,SHOW_MODIFY_ORDER,0, R.string.edit);
		}else{
			menu.add(0,SHOW_ALL,0, R.string.show_whole_thread);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		Log.d(TAG, "onContextItemSelected,tid="
				+tid+ ",page="+page );
		PagerOwnner father = null;
		ResetableArticle restNotifier = null;
		try{
			 father = (PagerOwnner) getActivity();
			 restNotifier= (ResetableArticle)getActivity();
		}catch(ClassCastException e){
			Log.e(TAG,"father activity does not implements interface " 
					+ PagerOwnner.class.getName());
			return true;
		}
		if(father.getCurrentPage() != page){
			return false;
		}
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		StringBuffer postPrefix = new StringBuffer();
		String tidStr = String.valueOf(this.tid);

		
		ThreadRowInfo row = (ThreadRowInfo) listview.getItemAtPosition(info.position);
		String content = row.getContent();
		final String name = row.getAuthor();
		String mention=null;
		switch(item.getItemId())
		//if( REPLY_POST_ORDER ==item.getItemId())
		{
		case QUOTE_ORDER:

			final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
			content = content.replaceAll(quote_regex, "");
			final String postTime = row.getPostdate();
			// final String url = map.get("url");
			
			if(!content.trim().endsWith("]"))
			{
				if (content.length() > 100) 
					content = content.substring(0, 99) + ".......";
				
					
			}
			mention = name;
			postPrefix.append("[quote][tid=");
			postPrefix.append(tidStr);
			postPrefix.append("]Topic[/pid] [b]Post by ");
			postPrefix.append(name);
			postPrefix.append(" (");
			postPrefix.append(postTime);
			postPrefix.append("):[/b]\n");
			postPrefix.append(content);
			postPrefix.append("[/quote]");

			postPrefix.append("\n[@");
			postPrefix.append(name);
			postPrefix.append("]\n");
		case REPLY_ORDER:	
			Intent intent = new Intent();
			if(!StringUtil.isEmpty(mention))
				intent.putExtra("mention", mention);
			intent.putExtra("prefix", StringUtil.removeBrTag(postPrefix.toString()) );
			intent.putExtra("tid", tidStr);
			intent.putExtra("action", "reply");	
			intent.setClass(getActivity(), PostActivity.class);
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			break;
		case SHOW_MODIFY_ORDER :
			Intent intentModify = new Intent();
			intentModify.putExtra("prefix", StringUtil.removeBrTag(content) );
			intentModify.putExtra("tid", tidStr);
			String pid = String.valueOf(row.getPid());//getPid(map.get("url"));
			intentModify.putExtra("pid", pid);
			intentModify.putExtra("title",row.getSubject());
			intentModify.putExtra("action", "modify");	
			intentModify.setClass(getActivity(), PostActivity.class);
			startActivity(intentModify);
			getActivity().overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
			break;
		case COPY_CLIPBOARD_ORDER:	
			//if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB )
			//{
				android.text.ClipboardManager  cbm = (android.text.ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
				cbm.setText(StringUtil.removeBrTag(content));
			//}else{
				//android.content.ClipboardManager  cbm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				//cbm.setPrimaryClip(ClipData.newPlainText("content", content));
			//}

			Toast.makeText(getActivity(), "�Ѿ����Ƶ����а�", Toast.LENGTH_SHORT).show();
			break;
		case SHOW_THISONLY_ORDER:
			Intent intentThis = new Intent();
			intentThis.putExtra("tab", "1");
			intentThis.putExtra("tid",tid );
			intentThis.putExtra("authorid",row.getAuthorid() );
			
			intentThis.setClass(getActivity(), ArticleListActivity.class);
			startActivity(intentThis);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
			//restNotifier.reset(0, row.getAuthorid());
			//ActivityUtil.getInstance().noticeSaying(getActivity());

			break;
		case SHOW_ALL:
			restNotifier.reset(0, 0);
			ActivityUtil.getInstance().noticeSaying(getActivity());
			break;
		case POST_COMMENT:
			final String dialog_tag = "post comment";
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(dialog_tag);
	        if (prev != null) {
	            ft.remove(prev);
	        }
			DialogFragment df = new PostCommentDialogFragment();
			Bundle b = new Bundle();
			b.putInt("pid",	row.getPid());
			b.putInt("tid", this.tid);
			df.setArguments(b);
			df.show(ft, dialog_tag);
			
			break;
			

			
			
		}
		return super.onContextItemSelected(item);
	}
	@Override
	public void finishLoad(ThreadData data) {
		Log.d(TAG, "finishLoad");
		ArticleListActivity father = (ArticleListActivity) this.getActivity();
		if(null != data && father != null){
			articleAdpater.setData(data);
			articleAdpater.notifyDataSetChanged();
			
			int exactCount = 1 + data.getThreadInfo().getReplies()/20;
			if(father.getmTabsAdapter().getCount() != exactCount
					&&this.authorid == 0){
				father.getmTabsAdapter().setCount(exactCount);
			}
			father.setTitle(data.getThreadInfo().getSubject());
			this.authorid = 0;
		}
		
	}
	
	
	

}
