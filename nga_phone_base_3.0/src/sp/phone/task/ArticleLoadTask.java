package sp.phone.task;

import gov.pianzong.androidnga.activity.MyApp;
import sp.phone.bean.ArticlePage;
import sp.phone.bean.OnThreadLoadCompleteLinstener;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.os.AsyncTask;

public class ArticleLoadTask extends AsyncTask<String, Integer, ArticlePage> {

	final Context context;
	final String cookie;
	final MyApp app;
	OnThreadLoadCompleteLinstener completeListener;
	public ArticleLoadTask(Context context, OnThreadLoadCompleteLinstener completeListener) {
		super();
		this.context = context;
		app = (MyApp) context.getApplicationContext();
		cookie = PhoneConfiguration.getInstance().getCookie();
		this.completeListener = completeListener;
	}
	public ArticleLoadTask(Context context){
		this(context, null);
	}
	

	@Override
	protected ArticlePage doInBackground(String... arg0) {
		if(arg0.length ==0)
			return null;
		
		String url = arg0[0];
		if(!url.startsWith("http://")){
			url = HttpUtil.Server + url;
		}

		ArticlePage ret = getCachedThread(url);
		if(ret == null){
			
			ret  = HttpUtil.getArticlePage(url, cookie);
			
		}
		
		
		return ret;
	}
	
	private ArticlePage getCachedThread(String url){
		return app.getMap_article().get(url);
		
	}

	@Override
	protected void onPostExecute(ArticlePage result) {
		if(completeListener != null){
			completeListener.NotifyLoadComplete(result);
		}
	}

	public void setCompleteListener(OnThreadLoadCompleteLinstener completeListener) {
		this.completeListener = completeListener;
	}
	
	
	

	
}



