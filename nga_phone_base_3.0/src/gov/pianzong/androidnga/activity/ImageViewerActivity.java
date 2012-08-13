package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;
import sp.phone.task.DownloadImageTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.example.android.actionbarcompat.ActionBarActivity;

public class ImageViewerActivity extends  ActionBarActivity {
	private WebView wv;
	//private final String IPHONE_UA = "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3";
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.webview_layout);
		wv = (WebView) findViewById(R.id.webview);
		
		
		
		
		
	}

	@Override
	protected void onResume() {
		load();
		super.onResume();
	}
	
	private String getPath(){
		return getIntent().getStringExtra("path");
	}
	
	@TargetApi(8)
	private void load(){
		final String uri = getPath();
		final WebSettings  settings = wv.getSettings(); 

		
		if(uri.endsWith(".swf")
				&& ActivityUtil.isGreaterThan_2_1() )//android 2.2
		{
			wv.setWebChromeClient(new WebChromeClient());
			settings.setPluginState(PluginState.ON);
			wv.loadUrl(uri);

		}else{//images

			settings.setSupportZoom(true);
			settings.setBuiltInZoomControls(true);	
			settings.setUseWideViewPort(true); 
			if(ActivityUtil.isGreaterThan_2_1())
				settings.setLoadWithOverviewMode(true);
			//settings.setUserAgentString(IPHONE_UA);
			wv.loadUrl(uri);
		}
		
	}
	
	

	@Override
	protected void onPause() {
		wv.stopLoading();
		wv.loadUrl("about:blank");
		super.onPause();
	}
	
	

	@Override
	protected void onStop() {
		wv.stopLoading();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.imageview_option_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	@TargetApi(11)
	private void runOnExecutor(DownloadImageTask task,String path){
		task.executeOnExecutor(DownloadImageTask.THREAD_POOL_EXECUTOR, path);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_refresh :
			load();
			break;
		case R.id.save_image:
			final String path = getPath();
			DownloadImageTask task = new DownloadImageTask(this);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExecutor(task,path);
			}else{
				task.execute(path);
			}
			break;
		default:
			/*Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.setClass(this, ArticleListActivity.class);
			MyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(MyIntent);*/
			this.finish();
				
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//super.onSaveInstanceState(outState);
	}
	
	

	
}
