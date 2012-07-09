package gov.pianzong.androidnga.activity;

import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import gov.pianzong.androidnga.R;
import android.annotation.TargetApi;
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
	//private final String IPHONE_UA = "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3";
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
	
	@TargetApi(8)
	private void load(){
		final String uri = getIntent().getStringExtra("path");
		final WebSettings  settings = wv.getSettings(); 
		/*String data = "����̽��7��2�ձ��� �����ϵ�һ��ת������������ˡ���������������һϵ�е�ʵ��֮����ʮ�������ı��������ˡ���һ��Ϣ����¶�������������ļ�����������������Ӥ���е������������ԣ��ѱ�ȷ�ϸ���ӵ������������Դ��<br/><br/>��Ϊ��������ʥ���ð���ֳҽѧ�Ϳ�ѧ�о�����һ��ʵ����Ŀ�Ľ����ʮ��������ڹ�ȥ�������ڵ�������Щ�������Լ�λ���е�ĸ�ס�����Ů�Ծ����ߵĶ������ע�뵽��Щĸ��δ�ܾ��������У�ʹ�����ǻ�û��е����������е�����һ��Ӥ����������ָ�Ʋ��ԣ�ȷ�������Ǹ����Ŵ��������������˵�DNA��������������ĸ��һ�����ס�<br/><br/>����Ļ�����Щ���Ӽ̳в��������ǵ���ֳϸ��ϵ��ζ�ţ������ܹ�����Щ���򴫸��Լ��ĺ����<br/><br/>Ϊѧ���ڿ���������ֳ������ģ�������ѧ����Jacques CohenΪ�׵��о��߳ƣ������Ǹ���������ֳϸ���󣬲�������������ͯ���׸�����������Ҳ����Υ�����Եĵ�·�ϼ�ðʧ�����˵��ǵ�һ������<br/><br/>�ı��������ֳϸ����������ֻ������������Ļ���𶫲���������һ��Ϊȫ������������ѧ�����ܻ�ļ������Ŵ�ѧ�ҵ�����һ�죬���ַ��������ڴ���һЩ������������ģ�������ж���������������������֡�<br/><br/>һЩר���������������ʵ�顣�����׶�����HammersmithҽԺ��Winstonѫ�����������BBC����û��֤�ݱ������Ʋ��б����õ�����������ֽ׶�ʹ�����ּ������Ҿ��ȣ�����Ӣ�����Բ��ᱻ������<br/><br/>δ������ͯ����Э���ȫ���ܼ�John Smeaton˵�������ǵ�Ȼ�����ܲ���֮��ķ򸾱���ǿ�ҵ�ͬ�顣��������ʵ��ֻ�ܽ�һ��˵������Ϊһ������Ӥ�����ֶΣ��Թ��ھ����������̽�Ӥ���ﻯ�ˡ�<br/><br/>����Ӣ��������ֳ�����������ܾ�����̥�����(HFEA)�ķ����˳ƣ����ǲ�������ʹ�����������Ϊ�ü����漰��ֳϸ���ĸ��졣<br/><br/>����Cohen��ͬ����Ͻ����ǣ���ЩŮ�Բ��У�����Ϊ���ǵ���ϸ����΢С�Ľṹȱ�ݡ����������ȱ�ݡ�<br/><br/>���ǻ�þ����ߵ����ӣ����ü�ϸ���룬ȡ�������ڲ����С�����������������ʣ���ע�벻�и�Ů�������С�����������������򣬸��Ʒ��µ��������Ŵ�������Ů�˵�DNA����Щ�������ͨ����ĸϵ��ֳϸ���������ݡ�<br/><br/>Jacques Cohen����Ϊ�ƶ�������ֳ������չ�ģ��Ի͵���������Ŀ�ѧ�ҡ��������ļ�����ֱ����ʵ�����е�����ע�侫�ӵ�DNA��ʹ�ò������Կ���ӵ���Լ��ĺ��ӡ��ڴ�֮ǰ��ֻ�в���Ů���ܹ�ʹ���Թ�Ӥ�����С��ƶ�����ȥ����˵�������ļ�����ʹ�ÿ�¡��ͯ��Ϊ���ܣ����ⱻ������ѧ����Ϊ�ǿֲ������ס�<br/><br/>������ҵ�ѧ�����ԣ�����Ҫһ��������ܸ㶨��������������������������ҵ���ϣ������һ����¡��ͯ�������ܾ������ǵ�����<br/>[url]http://discovery.163.com/12/0702/09/85D8PQ9A000125LI.html[/url]";
		if(ActivityUtil.isGreaterThan_4_0()){
			wv.loadDataWithBaseURL(null,data, "text/html", "utf-8",null);
			return ;
		}*/
		
		if(uri.endsWith(".swf")
				&& ActivityUtil.isGreaterThan_2_1() )//android 2.2
		{
			//final String html = "<embed src='"
			//		+uri+
			//	"' allowFullScreen='true' quality='high'  align='middle' allowScriptAccess='always' type='application/x-shockwave-flash'></embed>";
			//settings.setJavaScriptEnabled(true);
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
