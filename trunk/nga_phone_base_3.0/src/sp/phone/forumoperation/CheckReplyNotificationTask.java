package sp.phone.forumoperation;

import sp.phone.utils.HttpUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;

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
			result =HttpUtil.getHtml(url, cookie);
			try {
				Thread.sleep(120*1000);
			} catch (InterruptedException e) {

			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		/*@sample
		 * window.script_muti_get_var_store={0:[
		 * {0:8,1:1831521,2:"片总",3:"",4:"",5:"NGA安卓客户端越来越
			完善了！点击LZ下面小尾巴下载最新版！付建议！",
			9:1329908664,6:4942187,7:84606246},
			{0:8,1:1831521,2:"片总",3:"",4:"",
			5:"NGA安卓客户端越来越完善了！点击LZ下面小尾巴下载最新版！付
			建议！",
			9:1329908695,6:4942187,7:84606274}]}
		 * 
		 */
		
		/*
		 * ,6:4942187,7:84606246}
		 * ,6:(tid),7:(pid)}
		 */
		int start = 0;
		while(result.indexOf(",2:\"", start) !=-1)
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
			
			String msg = nickName + "召唤你到:" + tid +","+pid
					+"," + title;
			showNotification(msg);
		}
	}


	void showNotification(String msg){
		
	}
	
	

}
