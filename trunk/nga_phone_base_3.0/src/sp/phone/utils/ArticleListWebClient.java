package sp.phone.utils;

import sp.phone.task.TudouVideoLoadTask;
import gov.pianzong.androidnga.activity.ArticleListActivity;
import gov.pianzong.androidnga.activity.ImageViewerActivity;
import gov.pianzong.androidnga.activity.TopicListActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleListWebClient extends WebViewClient {
	static private final String NGACN_BOARD_PREFIX ="http://bbs.ngacn.cc/thread.php?"; 
	static private final String NGA178_BOARD_PREFIX ="http://nga.178.com/thread.php?"; 
	static private final String NGACN_THREAD_PREFIX ="http://bbs.ngacn.cc/read.php?"; 
	static private final String NGA178_THREAD_PREFIX ="http://nga.178.com/read.php?"; 
	static private final String YOUKU_END= "/v.swf";
	static private final String YOUKU_START = "http://player.youku.com/player.php/sid/";
	static private final String TUDOU_END= "/&resourceId=";
	static private final String TUDOU_START = "http://www.tudou.com/v/";
	//http://www.tudou.com/a/YRxj-HoTxT0/&resourceId=0_04_05_99&iid=146525460/v.swf
	//http://www.tudou.com/v/Qw74nyAg1wU/&resourceId=0_04_05_99/v.swf
	private final FragmentActivity fa ;
	
	public ArticleListWebClient(FragmentActivity fa) {
		super();
		this.fa = fa;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String origurl) {
		final String url = origurl.toLowerCase();
		if(url.startsWith(NGACN_BOARD_PREFIX)
				|| url.startsWith(NGA178_BOARD_PREFIX ) ){
			Intent intent = new Intent();
			intent.setData(Uri.parse(origurl));
			intent.setClass(view.getContext(), TopicListActivity.class);
			view.getContext().startActivity(intent);

		}else if(url.startsWith(NGACN_THREAD_PREFIX)
				|| url.startsWith(NGA178_THREAD_PREFIX ) ){
			Intent intent = new Intent();
			intent.setData(Uri.parse(origurl));
			intent.setClass(view.getContext(), ArticleListActivity.class);
			view.getContext().startActivity(intent);

			
		}else if(url.endsWith(".gif")||url.endsWith(".jpg")||
				url.endsWith(".png")||url.endsWith(".jpeg")||
				url.endsWith(".bmp")
				){
			Intent intent = new Intent();
			intent.putExtra("path", origurl);
			intent.setClass(view.getContext(), ImageViewerActivity.class);
			view.getContext().startActivity(intent);

		}else if(url.startsWith(YOUKU_START)){
			String id = StringUtil.getStringBetween(origurl, 0, YOUKU_START, YOUKU_END).result;
			String htmlUrl = "http://v.youku.com/player/getRealM3U8/vid/"
					+id +
					"/type/mp4/video.m3u8";
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(htmlUrl));
			//intent.setType("application/x-mpegURL");
			view.getContext().startActivity(intent);
		}else if(url.startsWith(TUDOU_START)){
			String id = StringUtil.getStringBetween(origurl, 0, TUDOU_START, TUDOU_END).result;
			
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutor(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			view.getContext().startActivity(intent);
			//return false;
		}
		return true;
	}
	
	@TargetApi(11)
	private void runOnExcutor(TudouVideoLoadTask loader, String id){
		loader.executeOnExecutor(TudouVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}



}
