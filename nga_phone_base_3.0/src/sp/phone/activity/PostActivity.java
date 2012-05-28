package sp.phone.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.ThreadPostAction;
import sp.phone.task.FileUploadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PostActivity extends Activity
	implements FileUploadTask.onFileUploaded{

	private final String LOG_TAG = Activity.class.getSimpleName();
	private String prefix;
	private EditText titleText;
	private EditText bodyText;
	private ThreadPostAction act; 
	private String action;
	private String tid;
	private int fid;
	private Button button_commit;
	private Button button_cancel;
	private ImageButton button_upload;
	private String REPLY_URL="http://bbs.ngacn.cc/post.php?";
	final int REQUEST_CODE_SELECT_PIC = 1;
	private String sig ="\n[url=http://code.google.com/p/ngacnphone/downloads/list]"
		+"----sent from my " + android.os.Build.MANUFACTURER
		+ " " + android.os.Build.MODEL + ",Android "
		+ android.os.Build.VERSION.RELEASE + "[/url]\n";
	private boolean loading;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		ThemeManager.SetContextTheme(this);
		super.onCreate(savedInstanceState);
		View v = this.getLayoutInflater().inflate(R.layout.reply, null);
		v.setBackgroundColor(getResources()
				.getColor(
					ThemeManager.getInstance().getBackgroundColor()
				));
		this.setContentView(v);
		

		
		Intent intent = this.getIntent();
		prefix = intent.getStringExtra("prefix");
		action = intent.getStringExtra("action");
		tid = intent.getStringExtra("tid");
		fid = intent.getIntExtra("fid", -7);
		String title = intent.getStringExtra("title");
		String pid = intent.getStringExtra("pid");
		String mention = intent.getStringExtra("mention");
		if(tid == null)
			tid = "";


		
		act = new ThreadPostAction(tid, "", "");
		act.setAction_(action);
		act.setFid_(fid);
		if(!StringUtil.isEmpty(mention))
			act.setMention_(mention);
		if(pid !=null)
			act.setPid_(pid);
		loading = false;
		
		titleText = (EditText) findViewById(R.id.reply_titile_edittext);
		if(title!=null)
			titleText.setText(title);
		titleText.setSelected(true);
		bodyText = (EditText) findViewById(R.id.reply_body_edittext);
		bodyText.setText(prefix);
		if(prefix != null)
			bodyText.setSelection(prefix.length());

		
		button_commit = (Button)findViewById(R.id.reply_commit_button);
		button_cancel = (Button)findViewById(R.id.reply_cancel_button);
		button_upload = (ImageButton) findViewById(R.id.imageButton_upload);
		
		button_commit.setOnClickListener(new ButtonCommitListener(REPLY_URL));
		button_cancel.setOnClickListener( new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PostActivity.this.finish();
		 	}
			}
		);
		button_upload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
                Intent intent = new Intent();  
                intent.setType("image/*");  
                intent.setAction(Intent.ACTION_GET_CONTENT);   
                startActivityForResult(intent,  REQUEST_CODE_SELECT_PIC);  
				
			}
			
		}
		);
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 0 )
			return;
		switch(requestCode)
		{
		case REQUEST_CODE_SELECT_PIC :
				//Toast.makeText(this, "���Խ׶Σ�һ��һ��ͼ", Toast.LENGTH_SHORT).show();
				Log.i(LOG_TAG, " select file :" + data.getDataString() );
				ContentResolver cr = this.getContentResolver();
				
			try {
				 ParcelFileDescriptor pfd = cr.openFileDescriptor(data.getData(), "r");
				 long filesize = pfd.getStatSize();
				 if(filesize >= 1024*1024)
				 {
					 Toast.makeText(this, "��1Mһ�µ�ͼƬ", Toast.LENGTH_SHORT).show();
					 break;
				 }
				 String contentType = cr.getType(data.getData());
				 Log.d(LOG_TAG, "file size =" + filesize);
				 pfd.close();
				 InputStream is = cr.openInputStream(data.getData());
				 new FileUploadTask(is,filesize,this, this, contentType).execute();
			} catch (FileNotFoundException e) {
				
				Log.wtf(LOG_TAG, "file not found", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
		default:
				;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	class ButtonCommitListener implements OnClickListener{

		private final String url;		
		ButtonCommitListener(String url){
			this.url = url;
		}
		@Override
		public void onClick(View v) {
			synchronized(button_commit){
				if(loading == true){
					String avoidWindfury = PostActivity.this.getString(R.string.avoidWindfury);
					Toast.makeText(PostActivity.this, avoidWindfury, Toast.LENGTH_SHORT).show();
					return ;
				}
				loading = true;
			}
			
			if(action.equals("reply")){
				handleReply(v);
			}else if(action.equals("new")){
				handleNewThread(v);
			}else if(action.equals("modify")){
				handleNewThread(v);
			}
		}
		public void handleNewThread(View v){
			handleReply(v);
			
		}
		
		public void handleReply(View v) {


			act.setPost_subject_(titleText.getText().toString());
			if(!act.getAction_().equals("modify"))
				act.setPost_content_(bodyText.getText().toString()+ sig);
			else
				act.setPost_content_(bodyText.getText().toString());	
			new ArticlePostTask(v).execute(url,act.toString());

			
			
		}

		
	}
	
	private class ArticlePostTask extends AsyncTask<String, Integer, String>{

		final View v;
		private final String result_start_tag = "<span style='color:#aaa'>&gt;</span>";
		private final String result_end_tag = "<br/>";
		
		public ArticlePostTask(View v) {
			super();
			this.v = v;
		}
		
		@Override
		protected void onPreExecute() {
			ActivityUtil.getInstance().noticeSaying(v.getContext());
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
		protected String doInBackground(String... params) {
			if(params.length<2)
				return "parameter error";
			String ret = "�������";
			String url = params[0];
			String body = params[1];
			
			HttpPostClient c =  new HttpPostClient(url);
			String cookie = PhoneConfiguration.getInstance().getCookie();
			c.setCookie(cookie);
			
			try {
				InputStream input = null;
				input = c.post_body(body).getInputStream();
				if(input != null)
				{
				String html = IOUtils.toString(input, "gbk");
				ret = getReplyResult(html);

				}
			} catch (IOException e) {
				Log.e(LOG_TAG, Log.getStackTraceString(e));
				
			}
			return ret;
		}
		
		private String getReplyResult(String html){
			int start = html.indexOf(result_start_tag);
			if(start == -1)
				return "����ʧ��";
			start += result_start_tag.length();
			int end = html.indexOf(result_end_tag, start);
			if(start == -1)
				return "����ʧ��";
			return html.substring(start, end);
			
			
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(v.getContext(), result,
					Toast.LENGTH_LONG).show();
			PhoneConfiguration.getInstance().setRefreshAfterPost(true);
			ActivityUtil.getInstance().dismiss();
			PostActivity.this.finish();
			super.onPostExecute(result);
		}
		
		
	}

	@Override
	public int finishUpload(String attachments, String attachmentsCheck,
			String picUrl) {
		this.act.appendAttachments_(attachments);
		act.appendAttachments_check_(attachmentsCheck);
		bodyText.setText( bodyText.getText().toString() + "\n[img]" +picUrl + "[/img]");
		return 0;
	}

}
