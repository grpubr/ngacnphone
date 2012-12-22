package sp.phone.task;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

import gov.pianzong.androidnga.R;
import gov.pianzong.androidnga.activity.ReplyListActivity;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.StringFindResult;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CheckReplyNotificationTask extends
		AsyncTask<String, Integer, String> 
implements PerferenceConstant{
	final String url = "http://bbs.ngacn.cc/nuke.php?func=noti&__notpl&__nodb&__nolib";
	final Context context;
	
	public CheckReplyNotificationTask(Context context){
		this.context = context;
	}
	@Override
	protected String doInBackground(String... params) {
		
		final String cookie = params[0];
		//final String emptyMessage = "window.script_muti_get_var_store=null";
		String result = null;//emptyMessage;
		

			result =HttpUtil.getHtml(url, cookie, null, 3000);
			//result = "{0:[{0:8,1:553736,2:\"��Ƭ\",3:\"\",4:\"\",5:\"��һ���ͻ���ɱ����\",9:1338737284,6:5222738,7:90295509},{0:8,1:553736,2:\"��Ƭ\",3:\"\",4:\"\",5:\"��һ���ͻ���ɱ����\",9:1338737301,6:5222738,7:90295522}]}";
			PhoneConfiguration.getInstance().lastMessageCheck
				= System.currentTimeMillis();
			Log.i(this.getClass().getSimpleName(), "get message:"+result);
			//result = "{0:[{0:8,1:1831521,2:\"Ƭ��\",3:\"\",4:\"\",5:\"���Լ���ǹר��¥��\",9:1339680840,6:5271811,7:91025529},{0:8,1:1831521,2:\"Ƭ��\",3:\"\",4:\"\",5:\"���Լ���ǹר��¥��\",9:1339680888,6:5271811,7:91025564}]}";
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		/*@sample
		 * window.script_muti_get_var_store={0:[
		 * {0:8,1:1831521,2:"Ƭ��",3:"",4:"",5:"NGA��׿�ͻ���Խ��Խ
			�����ˣ����LZ����Сβ���������°棡�����飡",
			9:1329908664,6:4942187,7:84606246},
			{0:8,1:1831521,2:"Ƭ��",3:"",4:"",
			5:"NGA��׿�ͻ���Խ��Խ�����ˣ����LZ����Сβ���������°棡��
			���飡",
			9:1329908695,6:4942187,7:84606274}]}
		 * 
		 */
		
		//result = "{0:[{0:8,1:1831521,2:\"Ƭ��\",3:\"\",4:\"\",5:\"NGA��׿�ͻ���Խ��Խ�����ˣ����LZ����Сβ���������°棡�����飡\",9:1329908664,6:4942187,7:84606246},{0:8,1:1831521,2:\"Ƭ��\",3:\"\",4:\"\",5:\"NGA��׿�ͻ���Խ��Խ�����ˣ����LZ����Сβ���������°棡�����飡\",9:1329908695,6:4942187,7:84606274}]}";
		
		/*Object o = JSON.parse(result);
		
		if( o!= null){
			JSONObject obj = (JSONObject) o;
			JSONArray array = obj.getJSONArray("0");
		}*/
		try {
			JSONObject o = new JSONObject(result);
			JSONArray array = o.getJSONArray("0");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(StringUtil.isEmpty(result)){
			return;
		}
		
		int start = 0;
		
		while(true)
		{
			/*start = result.indexOf(",2:\"", start)+4;
			int end = result.indexOf("\",3:",start);
			String nickName = result.substring(start, end);
			start = end;*/
			StringFindResult ret = StringUtil.getStringBetween(result,start, ",1:", ",2");
			start = ret.position;
			if(StringUtil.isEmpty(ret.result)||ret.position == -1)
				break;
			String authorId = ret.result;
	
			
			ret = StringUtil.getStringBetween(result,start, ":\"", "\",3:");
			if(StringUtil.isEmpty(ret.result)||ret.position == -1)
				break;
			String nickName = ret.result;
			start = ret.position;
			
			
			/*start = result.indexOf(",5:\"", start)+4;
			end = result.indexOf("\",9:",start);
			String title = result.substring(start, end);
			start = end;*/
			
			ret = StringUtil.getStringBetween(result,start, ",5:\"", "\",9:");
			if(StringUtil.isEmpty(ret.result)||ret.position == -1)
				break;
			
			String title = ret.result;
			start = ret.position;
					
			/*start = result.indexOf(",6:", start)+3;
			end = result.indexOf(",7:",start);
			String tid = result.substring(start, end);
			start = end;*/
			ret = StringUtil.getStringBetween(result,start, ",6:", ",7");
			if(StringUtil.isEmpty(ret.result)||ret.position == -1)
				break;
			
			String tid = ret.result;
			start = ret.position;
			
			
			/*start = result.indexOf(",7:", start)+3;
			end = result.indexOf("}",start);
			String pid = result.substring(start, end);
			start = end;*/
			
			ret = StringUtil.getStringBetween(result,start, ":", "}");
			String pid = ret.result;
			if(!StringUtil.isEmpty(ret.result))
				start = ret.position;
			
			
			
			
			title = StringUtil.unEscapeHtml(title);
			addNotification(authorId,nickName,tid,pid, title);
		}

		if(notificationList.size() == 1){
			NotificationObject o = notificationList.get(0);
			showNotification(o.getNickName(),String.valueOf(o.getTid())
					,String.valueOf(o.getPid()), o.getTitle());
		}
		else if(notificationList.size() >= 1){
			showStackedNotification();
		}
	}

	List<NotificationObject> notificationList = new ArrayList<NotificationObject>();
	void addNotification(String authorid,String nickName, String tid, String pid, String title ){
		if(StringUtil.isEmpty(tid))
		{
			return;
		}
		
		NotificationObject o  = new NotificationObject();
		o.setAuthorId(Integer.parseInt(authorid));
		o.setNickName(nickName);
		o.setTid(Integer.parseInt(tid));
		o.setPid(Integer.parseInt(pid));
		o.setTitle(title);
		notificationList.add(o);
		
		
	}
	
	void showStackedNotification(){
		
		String str = JSON.toJSONString(notificationList);
		SharedPreferences  share = 
				context.getSharedPreferences(PERFERENCE, Context.MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putString(PENDING_REPLYS, str);
			editor.commit();
		
		
		NotificationManager nm = 
				(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		//intent.setFlags(Intent.flag)
		intent.setClass(context, ReplyListActivity.class);
		PendingIntent pending=
				PendingIntent.getActivity(context, 0, intent, 0); 
		 Notification notification = new Notification(); 
		 notification.icon = R.drawable.nga_bg;
		 notification.defaults = Notification.DEFAULT_LIGHTS;
		 AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		 
		 if(PhoneConfiguration.getInstance().notificationSound
				 && audioManager.getRingerMode() ==AudioManager.RINGER_MODE_NORMAL )
			 notification.defaults |=Notification.DEFAULT_SOUND;
		 notification.flags = Notification.FLAG_AUTO_CANCEL;
		 
	     notification.tickerText = "��" + notificationList.size()
	    		 +"����������";
	     notification.when = System.currentTimeMillis();
	     
	     notification.number = notificationList.size();
	    		 
	     NotificationObject o  = notificationList.get(0);
	    notification.setLatestEventInfo(context, o.getNickName(), o.getTitle(), pending);
	    nm.notify(R.layout.topiclist_activity, notification);
	}
	
	void showNotification(String nickName, String tid, String pid, String title){
		
		if(StringUtil.isEmpty(tid))
		{
			return;
		}
		
		Log.i(this.getClass().getSimpleName(), "showNotification: pid="+pid +
				",tid=" + tid);
		
		NotificationManager nm = 
				(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Intent intent = new Intent(context,ArticleListActivity.class); 
		//intent.putExtra("tid", Integer.valueOf(tid).intValue());
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		int pidValue = 0;
		try
		{	if(!StringUtil.isEmpty(pid))
				pidValue = Integer.valueOf(pid).intValue();
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), "invalid pid: " + pid);
		}
		//intent.putExtra("pid", pidValue);
		
		Resources res = context.getResources();//.getString(R.string.myscheme)
		String url = res.getString(R.string.myscheme) + "://"+res.getString(R.string.myhost) +
				"/read.php?tid="+tid;
		if(pidValue!=0){
			url = url + "&pid="+pid;
		}
		intent.setData(Uri.parse(url));
		
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK );
		intent.addFlags(Intent.FILL_IN_DATA);
		
		PendingIntent pending=
				PendingIntent.getActivity(context, 0, intent, 0); 
		
		 String tickerText = nickName + " �ղ�������";


		 Notification notification = new Notification(); 
		 notification.icon = R.drawable.nga_bg;
		// notification.largeIcon = avatar;
		// notification.number = 5;

		 notification.defaults = Notification.DEFAULT_LIGHTS;
		 AudioManager audioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
		 
		 if(PhoneConfiguration.getInstance().notificationSound
				 && audioManager.getRingerMode() ==AudioManager.RINGER_MODE_NORMAL )
			 notification.defaults |=Notification.DEFAULT_SOUND;
		 notification.flags = Notification.FLAG_AUTO_CANCEL;
		
       // Notification notification = new Notification(sp.phone.activity.R.drawable.defult_img,tickerText,
        //        System.currentTimeMillis());
        notification.tickerText = tickerText;
        notification.when = System.currentTimeMillis();
        int id = Integer.valueOf(tid).intValue();
        if(pidValue !=0)
        	id=pidValue ;
        
		 notification.setLatestEventInfo(context, nickName, title, pending);
		 nm.notify(id, notification);
	}
	
	

}
