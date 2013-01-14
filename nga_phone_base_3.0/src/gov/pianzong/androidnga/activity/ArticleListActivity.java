package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;
import sp.phone.adapter.TabsAdapter;
import sp.phone.adapter.ThreadFragmentAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.fragment.ArticleListFragment;
import sp.phone.fragment.GotoDialogFragment;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.task.BookmarkTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;

import com.example.android.actionbarcompat.ActionBarActivity;

public class ArticleListActivity extends ActionBarActivity
implements PagerOwnner,ResetableArticle,OnThreadPageLoadFinishedListener,
PerferenceConstant{
	TabHost tabhost;
	ViewPager  mViewPager;
	ThreadFragmentAdapter mTabsAdapter;
    int tid;
    int pid;
    int authorid;
	private static final String TAG= "ArticleListActivity";
	private static final String GOTO_TAG = "goto";
	private int fid = 0;
	
	protected int getViewId(){
		return R.layout.pagerview_article_list;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getViewId());
		
		if(PhoneConfiguration.getInstance().uploadLocation
				&& PhoneConfiguration.getInstance().location == null
				)
		{
			ActivityUtil.reflushLocation(this);
		}
		
		
		mViewPager = (ViewPager)findViewById(R.id.pager);
		if (ActivityUtil.isNotLessThan_4_0()) {			
			setNfcCallBack();
		}

		tid = 7;
		int pageFromUrl = 0;
		String url = this.getIntent().getDataString();
		if(null != url){
			tid = this.getUrlParameter(url, "tid");		
			pid = this.getUrlParameter(url, "pid");		
			authorid = this.getUrlParameter(url, "authorid");
			pageFromUrl = this.getUrlParameter(url, "page");
		}else
		{
		tid = this.getIntent().getIntExtra("tid", 0);		
		pid = this.getIntent().getIntExtra("pid", 0);
		authorid = this.getIntent().getIntExtra("authorid", 0);
		}
	
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		
		if(tabhost != null)
		{
			tabhost.setup();
			mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,ArticleListFragment.class);	
		}else{
			mTabsAdapter = new ThreadFragmentAdapter(this,getSupportFragmentManager(),
							mViewPager,ArticleListFragment.class);
		}
		
		mTabsAdapter.setArgument("id", tid);
		mTabsAdapter.setArgument("pid", pid);
		mTabsAdapter.setArgument("authorid", authorid);
		ActivityUtil.getInstance().noticeSaying(this);

        if (savedInstanceState != null) {
        	int pageCount = savedInstanceState.getInt("pageCount");
        	if(pageCount!=0)
        	{
        		mTabsAdapter.setCount(pageCount);
        		mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
        	}
        	
        }else if( 0 != getUrlParameter(url, "page"))
        {
        	
        	mTabsAdapter.setCount(pageFromUrl+1);
        	mViewPager.setCurrentItem(pageFromUrl);
        }
		
	}
	
	private int getUrlParameter(String url, String paraName){
		if(StringUtil.isEmpty(url))
		{
			return 0;
		}
		final String pattern = paraName+"=" ;
		int start = url.indexOf(pattern);
		if(start == -1)
			return 0;
		start +=pattern.length();
		int end = url.indexOf("&",start);
		if(end == -1)
			end = url.length();
		String value = url.substring(start,end);
		int ret = 0;
		try{
			ret = Integer.parseInt(value);
		}catch(Exception e){
			Log.e(TAG, "invalid url:" + url);
		}
		
		return ret;
	}
	
	@TargetApi(14)
	private void setNfcCallBack(){
		NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
		CreateNdefMessageCallback callback = new CreateNdefMessageCallback(){

			@Override
			public NdefMessage createNdefMessage(NfcEvent event) {
				final String url = getUrl();
				NdefMessage msg = new NdefMessage(
		                new NdefRecord[]{NdefRecord.createUri(url)}
		                );
				return msg;
			}
			
		};
		if (adapter != null) {
			adapter.setNdefPushMessageCallback(callback, this);

		}
		
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
        super.onSaveInstanceState(outState);
        outState.putInt("pageCount",mTabsAdapter.getCount());
        outState.putInt("tab",mViewPager.getCurrentItem());
     //   outState.putInt("tid",tid);
        
    }
    
    
    
    
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.articlelist_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;

		MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			lock.setTitle(R.string.unlock_orientation);
			lock.setIcon(R.drawable.ic_menu_always_landscape_portrait);
			
		}
		
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
				
				intent.setClass(this, PostActivity.class);
				startActivity(intent);
				if(PhoneConfiguration.getInstance().showAnimation)
					overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
				break;
			case R.id.article_menuitem_refresh:
				int current = mViewPager.getCurrentItem();
				ActivityUtil.getInstance().noticeSaying(this);
				mViewPager.setAdapter(mTabsAdapter);
				mViewPager.setCurrentItem(current);
				
				
				break;
			case R.id.article_menuitem_addbookmark:				
				BookmarkTask bt = new BookmarkTask(this);
				bt.execute(String.valueOf(this.tid));
				break;
			case R.id.article_menuitem_lock:
				
				handleLockOrientation(item);
				break;
			case R.id.goto_floor:
				createGotoDialog();
				break;
			case R.id.article_menuitem_back:
			default:
				if(0 == fid)
				{
					finish();
				}else
				{
					Intent intent2 = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.putExtra("fid", fid);
                    startActivity(intent2);
					
				}
				break;
		}
		return true;

	}
	
	private void handleLockOrientation(MenuItem item){
		int preOrentation = ThemeManager.getInstance().screenOrentation;
		int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		ImageButton compat_item = getActionItem(R.id.actionbar_compat_item_lock);
		
		if(preOrentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				preOrentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			//restore
			//int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
			ThemeManager.getInstance().screenOrentation = newOrientation;
			
			setRequestedOrientation(newOrientation);
			item.setTitle(R.string.lock_orientation);
			item.setIcon(R.drawable.ic_lock_screen);
			if(compat_item !=null)
				compat_item.setImageResource(R.drawable.ic_lock_screen);
			
		}else{
			newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			Display dis = getWindowManager().getDefaultDisplay();
			//Point p = new Point();
			//dis.getSize(p);
			if(dis.getWidth() < dis.getHeight()){
				newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			
			ThemeManager.getInstance().screenOrentation = newOrientation;
			setRequestedOrientation(newOrientation);			
			item.setTitle(R.string.unlock_orientation);
			item.setIcon(R.drawable.ic_menu_always_landscape_portrait);
			if(compat_item !=null)
				compat_item.setImageResource(R.drawable.ic_menu_always_landscape_portrait);
		}
		
		
		
		SharedPreferences share = getSharedPreferences(PERFERENCE,
				Activity.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(SCREEN_ORENTATION, newOrientation);
		editor.commit();
		
	}
	
	private ImageButton getActionItem(int id){
		View actionbar_compat = findViewById(R.id.actionbar_compat);
		View ret = null;
		if(actionbar_compat != null)
		{
			ret = actionbar_compat.findViewById(id);
		}
		return (ImageButton) ret;
	}

	
	private void createGotoDialog(){

		int count = mTabsAdapter.getCount();
		Bundle args = new Bundle();
		args.putInt("count", count);
		
		DialogFragment df = new GotoDialogFragment();
		df.setArguments(args);
		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment prev = fm.findFragmentByTag(GOTO_TAG);
		if(prev != null){
			fm.beginTransaction().remove(prev).commit();
		}
		df.show(fm, GOTO_TAG);
		
	}

	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		super.onResume();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		return super.onContextItemSelected(item);
	}

	/*public ThreadFragmentAdapter getmTabsAdapter() {
		return mTabsAdapter;
	}*/

	@Override
	public int getCurrentPage() {
		
		return mViewPager.getCurrentItem() + 1;
	}
	
	@Override
	public void setCurrentItem(int index){
		mViewPager.setCurrentItem(index);
	}


	@Override
	protected void onDestroy() {
		//ActivityUtil.getInstance().dismiss();
		super.onDestroy();
	}

	@Override
	public void reset(int pid, int authorid,int floor) {
		this.pid = pid;
		this.authorid = authorid;
		mTabsAdapter.setArgument("pid", pid);
		mTabsAdapter.setArgument("authorid", authorid);
		if(tabhost!=null)
			tabhost.getTabWidget().removeAllViews();
		int page = floor / 20;
		mTabsAdapter.setCount(page+1);
		mViewPager.setAdapter(mTabsAdapter);
		mViewPager.setCurrentItem(page);
		
		
	}


	public String getUrl(){
		final String scheme = getResources().getString(R.string.myscheme);
		final StringBuilder sb = new StringBuilder(scheme);
		sb.append("://bbs.ngacn.cc/read.php?");
		if(tid!=0){
			sb.append("tid=");
			sb.append(tid);
			sb.append('&');
		}
		if(authorid !=0){
			sb.append("authorid=");
			sb.append(authorid);
			sb.append('&');
		}
		if(pid != 0){
			sb.append("pid=");
			sb.append(pid);
			sb.append('&');
		}
		if(this.mViewPager.getCurrentItem() != 0){
			sb.append("page=");
			sb.append(mViewPager.getCurrentItem());
			sb.append('&');
		}

		return sb.toString();
	}

	@Override
	public void finishLoad(ThreadData data) {
		int exactCount = 1 + data.getThreadInfo().getReplies()/20;
		if(mTabsAdapter.getCount() != exactCount
				&&this.authorid == 0){
			mTabsAdapter.setCount(exactCount);
		}
		fid = data.getThreadInfo().getFid();
		setTitle(StringUtil.unEscapeHtml(data.getThreadInfo().getSubject()));
		
		
	}



	
    

}
