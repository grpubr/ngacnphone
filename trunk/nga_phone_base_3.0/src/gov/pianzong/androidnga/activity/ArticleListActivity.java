package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;
import sp.phone.adapter.TabsAdapter;
import sp.phone.fragment.ArticleListFragment;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;

public class ArticleListActivity extends FragmentActivity
implements PagerOwnner,ResetableArticle {
	TabHost tabhost;
	ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    int tid;
    int pid;
    int authorid;
	private static final String TAG= ArticleListActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = this.getLayoutInflater().inflate(R.layout.pagerview_article_list, null);
		//setContentView(R.layout.pagerview_article_list);
		int bg = this.getResources().getColor(
				ThemeManager.getInstance().getBackgroundColor());
		v.setBackgroundColor(bg);
		setContentView(v);
		
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager)findViewById(R.id.pager);
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {			
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
	
		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,ArticleListFragment.class);
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
			lock.setIcon(android.R.drawable.ic_menu_always_landscape_portrait);
			
		}
		
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.goto_floor){
			createGotoDialog();
			return true;
		}else
		{
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void createGotoDialog(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);  
		alert.setTitle(R.string.goto_floor);
		alert.setMessage(R.string.goto_floor_description);
		
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) { 
            	if(null == mTabsAdapter)
            		dialog.dismiss();
                String value = input.getText().toString().trim(); 
                try{
                	int floor = Integer.valueOf(value);
                	if(floor > mTabsAdapter.getCount() || floor <1)
                		floor = mTabsAdapter.getCount();
                	mViewPager.setCurrentItem(floor-1);
                }catch(Exception e){
                	dialog.dismiss();
                }
            }
		});
		
		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                dialog.dismiss();
            }
		});
		
		alert.show();
		
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

	public TabsAdapter getmTabsAdapter() {
		return mTabsAdapter;
	}

	@Override
	public int getCurrentPage() {
		
		return mViewPager.getCurrentItem() + 1;
	}


	@Override
	protected void onDestroy() {
		//ActivityUtil.getInstance().dismiss();
		super.onDestroy();
	}

	@Override
	public void reset(int pid, int authorid) {
		this.pid = pid;
		this.authorid = authorid;
		mTabsAdapter.setArgument("pid", pid);
		mTabsAdapter.setArgument("authorid", authorid);
		tabhost.getTabWidget().removeAllViews();
		mTabsAdapter.setCount(1);
		//mTabsAdapter.notifyDataSetChanged();
		mViewPager.setAdapter(mTabsAdapter);
		
		
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



	
    

}
