package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.ThreadPostAction;
import sp.phone.fragment.EmotionDialogFragment;
import sp.phone.listener.OnEmotionPickedListener;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PostActivity extends FragmentActivity
	implements FileUploadTask.onFileUploaded,OnEmotionPickedListener{

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
	private ImageButton button_emotion;
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
		if(action.equals("new")){
			this.setTitle(R.string.new_thread);
		}else if(action.equals("reply")){
			setTitle(R.string.reply_thread);
			
		}else if(action.equals("modify")){
			setTitle(R.string.modify_thread);
			
		}
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
		button_emotion = (ImageButton) findViewById(R.id.imageButton_emotion);
		
		button_emotion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		        Fragment prev = getSupportFragmentManager().findFragmentByTag("emotion");
		        if (prev != null) {
		            ft.remove(prev);
		        }
		        ft.addToBackStack(null);

		        // Create and show the dialog.
		        DialogFragment newFragment = new EmotionDialogFragment();
		        newFragment.show(ft, "emotion");
				
			}
			
		}
		);
	}
	
	@Override
	public void onEmotionPicked(String emotion){
		bodyText.setText( bodyText.getText().toString() + emotion);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_CANCELED )
			return;
		switch(requestCode)
		{
		case REQUEST_CODE_SELECT_PIC :
				//Toast.makeText(this, "²âÊÔ½×¶Î£¬Ò»´ÎÒ»ÕÅÍ¼", Toast.LENGTH_SHORT).show();
				Log.i(LOG_TAG, " select file :" + data.getDataString() );
				ContentResolver cr = this.getContentResolver();
				
			try {
				 ParcelFileDescriptor pfd = cr.openFileDescriptor(data.getData(), "r");
				 long filesize = pfd.getStatSize();
				 if(filesize >= 1024*1024)
				 {
					 Toast.makeText(this, "ÏÞ1MÒ»ÏÂµÄÍ¼Æ¬", Toast.LENGTH_SHORT).show();
					 break;
				 }
				 String contentType = cr.getType(data.getData());
				 if(StringUtil.isEmpty(contentType)){
					 Toast.makeText(this, R.string.invalid_img_selected, Toast.LENGTH_LONG).show();
					 return;
				 }
					 
				 Log.d(LOG_TAG, "file size =" + filesize);
				 pfd.close();
				 InputStream is = cr.openInputStream(data.getData());
				 new FileUploadTask(is,filesize,this, this, contentType).execute();
			} catch (FileNotFoundException e) {
				
				Log.e(LOG_TAG, "file not found:" + Log.getStackTraceString(e));
				
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
		private boolean keepActivity = false;
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
			synchronized(button_commit){
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected void onCancelled(String result) {
			synchronized(button_commit){
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled(result);
		}

		@Override
		protected String doInBackground(String... params) {
			if(params.length<2)
				return "parameter error";
			String ret = "ÍøÂç´íÎó";
			String url = params[0];
			String body = params[1];
			
			HttpPostClient c =  new HttpPostClient(url);
			String cookie = PhoneConfiguration.getInstance().getCookie();
			c.setCookie(cookie);
			
			try {
				InputStream input = null;
				HttpURLConnection conn = c.post_body(body);
				if(conn!=null)
					input = conn.getInputStream();
				else
					keepActivity = true;
				
				if(input != null)
				{
				String html = IOUtils.toString(input, "gbk");
				ret = getReplyResult(html);

				}
				else
					keepActivity = true;
			} catch (IOException e) {
				keepActivity = true;
				Log.e(LOG_TAG, Log.getStackTraceString(e));
				
			}
			return ret;
		}
		
		private String getReplyResult(String html){
			int start = html.indexOf(result_start_tag);
			if(start == -1)
				return "·¢ÌûÊ§°Ü";
			start += result_start_tag.length();
			int end = html.indexOf(result_end_tag, start);
			if(start == -1)
				return "·¢ÌûÊ§°Ü";
			return html.substring(start, end);
			
			
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(v.getContext(), result,
					Toast.LENGTH_LONG).show();
			PhoneConfiguration.getInstance().setRefreshAfterPost(true);
			ActivityUtil.getInstance().dismiss();
			if(!keepActivity)
				PostActivity.this.finish();
			synchronized(button_commit){
				loading = false;
			}
				
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
