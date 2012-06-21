package sp.phone.task;

import gov.pianzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class JsonTopicListLoadTask extends AsyncTask<String, Integer, TopicListInfo> {
	private final static String TAG = JsonTopicListLoadTask.class.getSimpleName();
	private final Context context;
	final private OnTopListLoadFinishedListener notifier;
	private String error;
	
	
	public JsonTopicListLoadTask(Context context,
			OnTopListLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}
	@Override
	protected TopicListInfo doInBackground(String... params) {
		if(params.length == 0)
			return null;
		Log.d(TAG, "start to load " + params[0]);
		String uri = params[0];
		String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
		if(js == null){
			error = context.getResources().getString(R.string.network_error);
			return null;
		}
		js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
		JSONObject o = null;
		try{
				o = (JSONObject) JSON.parseObject(js).get("data");
		}catch(Exception e){
			Log.e(TAG, "can not parse :\n" +js );
		}
		if(o == null){
			error = "�����µ�¼";
			return null;
		}
		
		TopicListInfo ret = new TopicListInfo();;
		
		Object rows = o.get("__ROWS");
		if(rows instanceof Integer)
		{
			ret.set__ROWS((Integer)o.get("__ROWS"));
		}else{
			ret.set__ROWS(10000);
		}
		
		ret.set__T__ROWS((Integer)o.get("__T__ROWS"));
		Integer forum = (Integer)o.get("__SELECTED_FORUM");
		if(forum !=null)
			ret.set__SELECTED_FORUM(forum);
		else
			ret.set__SELECTED_FORUM(0);
		
		JSONObject o1 = (JSONObject) o.get("__T");
		
		if(ret == null || o1 == null)
		{
			error = "�����µ�¼";
			return null;
		}
			
			
		List<ThreadPageInfo> articleEntryList = new  ArrayList<ThreadPageInfo>();
		for(int i = 0; i <ret.get__T__ROWS(); i++){
			JSONObject rowObj  = (JSONObject) o1.get(String.valueOf(i));
			try{
			ThreadPageInfo entry = JSONObject.toJavaObject(rowObj,ThreadPageInfo.class);
			articleEntryList.add(entry);
			}catch(Exception e){
				ThreadPageInfo entry = new ThreadPageInfo();
				String error = rowObj.getString("error");
				entry.setSubject(error);
				entry.setAuthor("");
				entry.setLastposter("");
				articleEntryList.add(entry);
			}
		}
		
		ret.setArticleEntryList(articleEntryList);
		
		return ret;
	}
	@Override
	protected void onPostExecute(TopicListInfo result) {
		ActivityUtil.getInstance().dismiss();
		if(result == null)
		{
			ActivityUtil.getInstance().noticeError
			(error, context);
			return;
		}
		if(null != notifier)
			notifier.jsonfinishLoad(result);
		super.onPostExecute(result);
	}
	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}
	
	

}
