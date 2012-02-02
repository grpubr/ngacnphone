package sp.phone.forumoperation;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



import android.util.Log;

public class HttpPostClient {
	public HttpPostClient(String urlString) 
	{
		this.urlString = urlString;

	}

	private static final String LOG_TAG = HttpPostClient.class
			.getSimpleName();
	private String urlString;
	
	public HttpURLConnection post_body(String body)
	{
		HttpURLConnection conn;
		try
		{
			URL url = new URL(this.urlString);
			conn = (HttpURLConnection) url.openConnection();
			//conn.setRequestProperty("Cookie", cookies_);
			conn.setInstanceFollowRedirects(false);
			
			conn.setRequestProperty("User-Agent", "3rd_part_android_app");
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setDoOutput(true);
	
			conn.connect();
	
			OutputStreamWriter out = new OutputStreamWriter(conn
					.getOutputStream());
			out.write(body);
			out.flush();
			out.close();
	
			
			Log.i(LOG_TAG, conn.getResponseMessage());

		}catch(Exception e) {
			//sb.append(e.toString());
			conn = null;
			Log.e(LOG_TAG, e.toString());
		}
		return conn;
	}

}
