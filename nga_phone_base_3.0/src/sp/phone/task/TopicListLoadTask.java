package sp.phone.task;

import sp.phone.bean.RSSFeed;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.RSSUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TopicListLoadTask extends AsyncTask<String, Integer, RSSUtil> {
	final static String TAG = TopicListLoadTask.class.getSimpleName();
	private final Context context;
	final private OnTopListLoadFinishedListener notifier;

	
	public TopicListLoadTask(Context context,
			OnTopListLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}


	@Override
	protected void onPreExecute() {
		//ActivityUtil.getInstance().noticeSaying(context);
		super.onPreExecute();
	}


	@Override
	protected RSSUtil doInBackground(String... params) {
		if(params.length == 0)
			return null;
		Log.d(TAG, "start to load " + params[0]);
		String url = params[0];
		RSSUtil rssUtil = new RSSUtil();
		rssUtil.parseXml(url);
		return rssUtil;
	}


	@Override
	protected void onPostExecute(RSSUtil result) {
		
		
		if(result == null){
			Log.e(TAG, "erorr in get rssutil");
		}
		
		RSSFeed rssFeed = result.getFeed();
		ActivityUtil.getInstance().dismiss();
		if(rssFeed == null){
			Log.e(TAG, "erorr in get rssutil");
			ActivityUtil.getInstance().noticeError
			(result.getErrorString(), context);
		}
		else if (rssFeed.getItems().size() == 0)
		{
			Log.e(TAG, "erorr in load rss feed:" + result.getErrorString());
			ActivityUtil.getInstance().noticeError
				(result.getErrorString(), context);
		}else{
			if(this.notifier != null)
				notifier.finishLoad(rssFeed);
		}
		
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}
	
	

}
