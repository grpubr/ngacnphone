package sp.phone.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import sp.phone.activity.MyApp;
import sp.phone.activity.R;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class AppUpdateCheckTask extends AsyncTask<String, Integer, String> {
	
	public AppUpdateCheckTask(Context context) {
		super();
		this.context = context;
	}
	static final String TAG = AppUpdateCheckTask.class.getSimpleName();
	static final String url = "http://code.google.com/feeds/p/ngacnphone/downloads/basic";
	static final String entryStartTag = "<entry>";
	static final String updateStartTag = "<updated>";
	static final String updateEndtTag = "</updated>";
	static final String idStartTag = "<id>";
	static final String idEndtTag = "</id>";
	final private Context context;
	
	
	
	
	@Override
	protected String doInBackground(String... params) {
		String rssString = HttpUtil.getHtml(url,"");
		String apkUrl = null;
		String apkId = null;
		do
		{
			if(StringUtil.isEmpty(rssString))
				break;
			int start = 0; 
			int end = 0;
			
			start = rssString.indexOf(entryStartTag);
			if(start == -1)
				break;
			
			start = rssString.indexOf(updateStartTag,start);
			if(start == -1)
				break;
			start += updateStartTag.length();
			end = rssString.indexOf(updateEndtTag,start);
			if(end == -1)
				break;
			String date = rssString.substring(start,end);//2012-05-29T17:55:08Z
			date = date.replace('T', ' ');
			date = date.replace("Z", "");
			try {
				SimpleDateFormat sdf  =   new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ); 
				Date d = sdf.parse(date);
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				c.setTime(d);
				long gap = System.currentTimeMillis() - c.getTimeInMillis();//utc
				long hour = gap/(1000*3600);
				if(hour < (8+2))
					break;
			
			} catch (ParseException e) {
				Log.e(TAG, "invalid date:" + date);
				break;
			}
			
			
			
			start = rssString.indexOf(idStartTag,end);
			if(start == -1)
				break;
			start += idStartTag.length();
			end = rssString.indexOf(idEndtTag,start);
			if(end == -1)
				break;
			
			apkUrl = rssString.substring(start,end);
			//id -->http://code.google.com/feeds/p/ngacnphone/downloads/basic/nga_phone200.apk
			//url http://ngacnphone.googlecode.com/files/nga_phone200.apk
			apkId = apkUrl.replace("http://code.google.com/feeds/p/ngacnphone/downloads/basic/nga_phone", 
					"");
			apkId = apkId.replace(".apk", "");
			
		}while(false);
		
		return apkId;
	}
	@Override
	protected void onPostExecute(String result) {
		if(result == null)
			return;
		int id =0;
		id = Integer.parseInt(result);

		if(id <= MyApp.version){
			return;
		}

		NotificationManager nm = 
				(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(result));
		
		intent.addFlags(Intent.FILL_IN_DATA);
		
		PendingIntent pending=
				PendingIntent.getActivity(context, 0, intent, 0); 
		
		 String tickerText = "有新版本";


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
        
		 notification.setLatestEventInfo(context, "新版本", "", pending);
		 nm.notify(sp.phone.activity.R.layout.message_article, notification);
		super.onPostExecute(result);
	}
	
	

}
