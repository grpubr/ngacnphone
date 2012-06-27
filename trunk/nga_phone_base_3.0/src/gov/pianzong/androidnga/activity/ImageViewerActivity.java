package gov.pianzong.androidnga.activity;

import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import gov.pianzong.androidnga.R;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class ImageViewerActivity extends FragmentActivity {
	private WebView wv;
	@Override
	protected void onCreate(Bundle arg0) {

		this.setContentView(R.layout.webview_layout);
		wv = (WebView) findViewById(R.id.webview);
		
		
		load();
		
		super.onCreate(arg0);
	}

	@Override
	protected void onResume() {
		
		super.onResume();
	}
	
	private void load(){
		final String uri = getIntent().getStringExtra("path");
		final WebSettings  settings = wv.getSettings(); 
		if(uri.endsWith(".swf")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO )//android 2.2
		{
			//final String html = "<embed src='"
			//		+uri+
			//	"' allowFullScreen='true' quality='high'  align='middle' allowScriptAccess='always' type='application/x-shockwave-flash'></embed>";
			settings.setJavaScriptEnabled(true);
			wv.setWebChromeClient(new WebChromeClient());
			settings.setPluginState(PluginState.ON);
			wv.loadUrl(uri);

		}else{//images

			settings.setSupportZoom(true);
			settings.setBuiltInZoomControls(true);	
			settings.setUseWideViewPort(true); 
			settings.setLoadWithOverviewMode(true);
			wv.loadUrl(uri);
		}
		
	}
	
	

	@Override
	protected void onPause() {
		wv.loadUrl("about:blank");
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.imageview_option_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_refresh :
			load();
			break;
		default:
			Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.setClass(this, ArticleListActivity.class);
			MyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(MyIntent);
				
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	
}
