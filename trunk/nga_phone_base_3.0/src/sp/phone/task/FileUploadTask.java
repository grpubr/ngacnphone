package sp.phone.task;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

import sp.phone.utils.ActivityUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.UploadCookieCollector;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class FileUploadTask extends
AsyncTask<String, Integer, String> {
	private static final String TAG = FileUploadTask.class.getSimpleName();
	private static final String BOUNDARY =
					"-----------------------------7db1c5232222b";
	private static final String ATTACHMENT_SERVER = "http://upload.ngacn.cc:8080/attach.php?";
	private static final String LOG_TAG = FileUploadTask.class.getSimpleName();
	private InputStream is;
	private long filesize;
	private Context context;
	private onFileUploaded notifier;
	
	final private String filename ;
	final private String utfFilename;
	final private String contentType ;
	
	static final private String attachmentsStartFlag = "namedItem('attachments').value+='";
	static final private String attachmentsCheckStartFlag = "namedItem('attachments_check').value+='";
	static final private String attachmentsEndFlag = "\\t";
	
	static final private String picUrlStartTag = "addUploadedAttach('";
	static final private String picUrlEndTag = "'";
	
	public FileUploadTask(InputStream is, long filesize, Context context, onFileUploaded notifier, String contentType) {
		super();
		this.is = is;
		this.filesize = filesize;
		this.context = context;
		this.notifier = notifier;
		this.contentType = contentType;
		this.filename = contentType.replace('/', '.');
		this.utfFilename = filename.substring(1);
	}

	@Override
	protected void onPreExecute() {
		ActivityUtil.getInstance().noticeSaying(context);
		super.onPreExecute();
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

	@Override
	protected void onCancelled(String result) {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled(result);
	}

	@Override
	protected void onPostExecute(String result) {
		do
		{
			if(StringUtil.isEmpty(result))
				break;
			int start = result.indexOf(attachmentsStartFlag);
			if(start == -1)
				break;
			start = start + attachmentsStartFlag.length();
			int end = result.indexOf(attachmentsEndFlag, start);
			if(end == -1)
				break;
			String attachments = result.substring(start, end);
			try {
				attachments = URLEncoder.encode(attachments + "\t","utf-8");
			} catch (UnsupportedEncodingException e1) {
				Log.e(TAG, "invalid attachments string" + attachments);
			}
			
			start = result.indexOf(attachmentsCheckStartFlag);
			if(start == -1)
				break;
			start = start + attachmentsCheckStartFlag.length();
			end = result.indexOf(attachmentsEndFlag, start);
			if(end == -1)
				break;
			String attachmentsCheck = result.substring(start, end);
			try {
				attachmentsCheck = URLEncoder.encode(attachmentsCheck + "\t","utf-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "invalid attachmentsCheck string" + attachmentsCheck);
				break;
			}
			
			start = result.indexOf(picUrlStartTag);
			if(start == -1)
				break;
			start = start + picUrlStartTag.length();
			end = result.indexOf(picUrlEndTag, start);
			if(end == -1)
				break;
			String picUrl = result.substring(start, end);
			notifier.finishUpload(attachments, attachmentsCheck, picUrl);
		}while(false);
		ActivityUtil.getInstance().dismiss();
		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {
		final byte header[] = buildHeader().getBytes();
		final byte tail[] = buildTail().getBytes();
		
		final String cookie = new UploadCookieCollector().StartCollect().toString();
		String html = null;
		URL url;
		try {
			url = new URL(ATTACHMENT_SERVER);
			Log.d(LOG_TAG, "cookie:" + cookie);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		conn.setRequestProperty("Content-Length",
				String.valueOf(header.length + filesize + tail.length));
		conn.setRequestProperty("Accept-Charset", "GBK");
		conn.setRequestProperty("Cookie", cookie);
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		
		byte[] buf = new byte[1024];  
	    int len;  
	    out.write(header); 
	    while ((len = is.read(buf)) != -1)  
	        out.write(buf, 0, len);  
	  
	    out.write(tail);  
	  
	    is.close(); 
	    InputStream httpInputStream = conn.getInputStream();
		for (int i = 1; (conn.getHeaderFieldKey(i)) != null; i++) {
			Log.d(LOG_TAG, conn.getHeaderFieldKey(i) + ":"
					+ conn.getHeaderField(i));

		}
	    html = IOUtils.toString(httpInputStream, "gbk");
	    Log.d(LOG_TAG, "get response" + html);
	    out.close();
	    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return html;
	}
	
	private String buildHeader(){
		StringBuilder sb = new StringBuilder();
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"attachment_file1\"; filename=\"");
		sb.append(filename);
		sb.append("\"\r\nContent-Type: ");
		sb.append(contentType);
		sb.append("\r\n\r\n");
		
		return sb.toString();
		
	}
	
	private String buildTail(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_watermark\"\r\n\r\n\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_thumb\"\r\n\r\n\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_dscp\"\r\n\r\n\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_url_utf8_name\"\r\n\r\n");
		sb.append(utfFilename + "\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"func\"\r\n\r\nupload\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"fid\"\r\n\r\n-7\r\n");
		
		sb.append("--" + BOUNDARY + "--\r\n");
		
		return sb.toString();
	}
	
	public interface onFileUploaded{
		int finishUpload(String attachments, String attachmentsCheck, String picUrl );
	}

}
