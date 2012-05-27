package sp.phone.task;

import sp.phone.bean.ThreadData;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.activity.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class JsonThreadLoadTask extends AsyncTask<String, Integer, ThreadData> {
	static final String TAG = JsonThreadLoadTask.class.getSimpleName();
	final private Context context;
	private String errorStr;
	final private OnThreadPageLoadFinishedListener notifier;

	public JsonThreadLoadTask(Context context,
			OnThreadPageLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}


	@Override
	protected ThreadData doInBackground(String... params) {
		if(params.length == 0)
			return null;
		
		final String url = params[0];
		Log.d(TAG, "start to load:" + url);
		String js = HttpUtil.getHtml(url, PhoneConfiguration.getInstance().getCookie());
		if(null == js){
			errorStr = context.getString(R.string.network_error);
			return null;
		}
		
		ThreadData result =ArticleUtil.parseJsonThreadPage(js);
		
		if(null == result){
			errorStr = context.getResources().getString(R.string.thread_load_error);
		}
		
		return result;
	}


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}


	@Override
	protected void onPostExecute(ThreadData result) {
		ActivityUtil.getInstance().dismiss();
		if(result == null){
			ActivityUtil.getInstance().noticeError(errorStr, context);
		}
		notifier.finishLoad(result);
		
		super.onPostExecute(result);
	}


	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

}
