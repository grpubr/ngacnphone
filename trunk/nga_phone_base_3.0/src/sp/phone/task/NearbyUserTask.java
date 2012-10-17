package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.interfaces.OnNearbyLoadComplete;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;
import android.os.AsyncTask;

public 	class NearbyUserTask extends AsyncTask<String,Integer,String>{

	

	public NearbyUserTask(double latitude, double longitude, String name,
			String uid, OnNearbyLoadComplete notifier) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.uid = uid;
		this.notifier = notifier;
	}

	private final double latitude;
	private final double longitude;
	private final String name;
	private final String uid;
	private final OnNearbyLoadComplete notifier;
	@Override
	protected String doInBackground(String... params) {

		String ips[] = {"203.208.46.1",
				"203.208.46.2",
				"203.208.46.3",
				"203.208.46.4",
				"203.208.46.5",
				"203.208.46.6",
				"203.208.46.7",
				"203.208.46.8"};
		String host = "ngalocation.appspot.com";
		String ret = null;
		for(int i =0; i<ips.length; ++i)
		{
			StringBuilder sb = new StringBuilder("http://");
			try {
				sb.append(ips[i]).append("/test?nick_name=")
				.append(URLEncoder.encode(name, "utf-8"))
				.append("&user_id=").append(uid)
				.append("&longitude=").append(longitude)
				.append("&latitude=").append(latitude);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				return null;
			}
			ret = HttpUtil.getHtml(sb.toString(), "", host, 0);
			if(!StringUtil.isEmpty(ret))
				break;
		}

		return ret;
	}

	@Override
	protected void onPostExecute(String result) {
		notifier.OnComplete(result);
	}

	@Override
	protected void onCancelled(String result) {
		notifier.OnComplete(null);
	}

	@Override
	protected void onCancelled() {
		notifier.OnComplete(null);
	}
	
	
}
