package sp.phone.task;



import gov.pianzong.androidnga.R;
import gov.pianzong.androidnga.activity.ArticleListActivity;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class CheckReplyNotificationTask extends
		AsyncTask<String, Integer, String> {
	final String url = "http://bbs.ngacn.cc/nuke.php?func=noti&__notpl&__nodb&__nolib";
	final Context context;
	
	public CheckReplyNotificationTask(Context context){
		this.context = context;
	}
	@Override
	protected String doInBackground(String... params) {
		
		final String cookie = params[0];
		final String emptyMessage = "window.script_muti_get_var_store=null";
		String result = emptyMessage;
		
		while(emptyMessage.equals(result))
		{
			result =HttpUtil.getHtml(url, cookie, null, 3000);
			//result = "{0:[{0:8,1:553736,2:\"��Ƭ\",3:\"\",4:\"\",5:\"��һ���ͻ���ɱ����\",9:1338737284,6:5222738,7:90295509},{0:8,1:553736,2:\"��Ƭ\",3:\"\",4:\"\",5:\"��һ���ͻ���ɱ����\",9:1338737301,6:5222738,7:90295522}]}";
			PhoneConfiguration.getInstance().lastMessageCheck
				= System.currentTimeMillis();
			Log.i(this.getClass().getSimpleName(), "get message:"+result);
			break;
		}
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
		

		
		int start = 0;
		while(result != null && result.indexOf(",2:\"", start) !=-1)
		{
			start = result.indexOf(",2:\"", start)+4;
			int end = result.indexOf("\",3:",start);
			String nickName = result.substring(start, end);
			start = end;
			
			start = result.indexOf(",5:\"", start)+4;
			end = result.indexOf("\",9:",start);
			String title = result.substring(start, end);
			start = end;
					
			start = result.indexOf(",6:", start)+3;
			end = result.indexOf(",7:",start);
			String tid = result.substring(start, end);
			start = end;
			
			start = result.indexOf(",7:", start)+3;
			end = result.indexOf("}",start);
			String pid = result.substring(start, end);
			start = end;
			
			title = StringUtil.unEscapeHtml(title);
			showNotification(nickName,tid,pid, title);
		}
	}


	void showNotification(String nickName, String tid, String pid, String title){
		NotificationManager nm = 
				(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(context,ArticleListActivity.class); 
		intent.putExtra("tid", Integer.valueOf(tid).intValue());
		if(!StringUtil.isEmpty(pid))
			intent.putExtra("pid", Integer.valueOf(pid).intValue());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK );
		intent.addFlags(Intent.FILL_IN_DATA);
		
		PendingIntent pending=
				PendingIntent.getActivity(context, 0, intent, 0); 
		
		 String tickerText = nickName + " �ղ�������";


		 Notification notification = new Notification(); 
		 notification.icon = R.drawable.p7;
		// notification.largeIcon = avatar;
		// notification.number = 5;

		 notification.defaults = Notification.DEFAULT_LIGHTS;
		 if(PhoneConfiguration.getInstance().notificationSound)
			 notification.defaults |=Notification.DEFAULT_SOUND;
		
       // Notification notification = new Notification(sp.phone.activity.R.drawable.defult_img,tickerText,
        //        System.currentTimeMillis());
        notification.tickerText = tickerText;
        notification.when = System.currentTimeMillis();
        
		 notification.setLatestEventInfo(context, nickName, title, pending);
		 nm.notify(gov.pianzong.androidnga.R.layout.pagerview_article_list, notification);
	}
	
	

}
