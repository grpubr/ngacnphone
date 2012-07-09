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
		/*String data = "网易探索7月2日报道 世界上第一个转基因人类产生了。在美国，经历了一系列的实验之后，三十个健康的宝宝诞生了。这一消息的披露引起了伦理层面的激烈争吵。至今，这批婴儿中的两个经过测试，已被确认各自拥有三个基因来源。<br/><br/>作为新泽西州圣巴拿巴生殖医学和科学研究所的一项实验项目的结果，十五个孩子在过去的三年内诞生。这些宝宝产自几位不孕的母亲。来自女性捐助者的额外基因，注入到这些母亲未受精的卵子中，使得她们获得怀孕的能力。其中的两个一岁婴儿经过基因指纹测试，确认了他们各自遗传了来自三个成人的DNA——他们有两个母亲一个父亲。<br/><br/>额外的基因被这些孩子继承并融入他们的生殖细胞系意味着，他们能够将这些基因传给自己的后代。<br/><br/>为学术期刊《人类生殖》供稿的，以生育学教授Jacques Cohen为首的研究者称，“这是改造人类生殖细胞后，产下正常健康儿童的首个案例。”“也是在违背人性的道路上既冒失又令人担忧的一步。”<br/><br/>改变人类的生殖细胞——尽管只是用人类自身的基因拆东补西——是一项为全世界绝大多数科学家所避讳的技术。遗传学家担心有一天，这种方法被用于创造一些我们梦寐以求的，比如具有额外的力量或智力的新人种。<br/><br/>一些专家严厉批评了这个实验。来自伦敦西部Hammersmith医院的Winston勋爵，昨天告诉BBC：“没有证据表明治疗不孕必须用到这项技术。在现阶段使用这种技术令我惊讶，这在英国绝对不会被允许。”<br/><br/>未出生儿童保护协会的全国总监John Smeaton说：“人们当然对遭受不育之苦的夫妇抱有强烈的同情。但是这项实验只能进一步说明，作为一种孕育婴儿的手段，试管授精的整个过程将婴儿物化了。<br/><br/>控制英国辅助生殖技术的人类受精与胚胎管理局(HFEA)的发言人称，他们不会允许使用这项技术，因为该技术涉及生殖细胞的改造。<br/><br/>教授Cohen和同僚诊断结论是：这些女性不孕，是因为她们的卵细胞有微小的结构缺陷——线粒体的缺陷。<br/><br/>他们获得捐助者的卵子，并用极细的针，取出卵子内部含有“健康”线粒体的物质，再注入不孕妇女的卵子中。由于线粒体包含基因，该疗法下的新生儿遗传了两个女人的DNA。这些基因可以通过沿母系生殖细胞向后代传递。<br/><br/>Jacques Cohen被视为推动辅助生殖技术发展的，辉煌但备受争议的科学家。他开发的技术，直接向实验室中的卵子注射精子的DNA，使得不育男性可以拥有自己的孩子。在此之前，只有不孕女性能够使用试管婴儿受孕。科恩教授去年曾说过，他的技术将使得克隆儿童成为可能，而这被主流科学界认为是恐怖的征兆。<br/><br/>“这对我的学生而言，仅需要一个下午就能搞定。”他补充道，至少有三个人找到他希望创造一个克隆儿童，但他拒绝了他们的请求。<br/>[url]http://discovery.163.com/12/0702/09/85D8PQ9A000125LI.html[/url]";
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
