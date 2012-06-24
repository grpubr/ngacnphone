package gov.pianzong.androidnga.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class ImageViewerActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		WebView wv = new WebView(this);
		this.setContentView(wv);
		final WebSettings  settings = wv.getSettings(); 
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		//settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		
		final String uri = getIntent().getStringExtra("path");
		if(uri.endsWith(".swf")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO )//android 2.2
		{
			final String html = 
			"<html><head></head><body><embed src='"
					+ uri +
					"' allowFullScreen='true' type='application/x-shockwave-flash'></embed></body></html>";
			//settings.setJavaScriptEnabled(true);
			//wv.setWebChromeClient(new WebChromeClient());
			settings.setPluginState(PluginState.ON);
			wv.loadData(html, "text/html", "utf-8");
		}else{
			settings.setUseWideViewPort(true); 
			settings.setLoadWithOverviewMode(true);
			wv.loadUrl(uri);
		}
		
		
		super.onCreate(arg0);
	}

	
}
