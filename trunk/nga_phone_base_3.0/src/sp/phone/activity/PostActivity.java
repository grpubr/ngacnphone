package sp.phone.activity;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.ThreadPostAction;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostActivity extends Activity {

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
	private MyApp app;
	private String REPLY_URL="http://bbs.ngacn.cc/post.php?";
	private String sig ="\n[url=http://code.google.com/p/ngacnphone/downloads/list]"
		+"----sent from my " + android.os.Build.MANUFACTURER
		+ " " + android.os.Build.MODEL + ",android "
		+ android.os.Build.VERSION.RELEASE + "[/url]\n";


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

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
		if(tid == null)
			tid = "";

		app = (MyApp)getApplication();
		
		act = new ThreadPostAction(tid, "", "");
		act.setAction_(action);
		act.setFid_(fid);
		
		titleText = (EditText) findViewById(R.id.reply_titile_edittext);
		titleText.setSelected(true);
		bodyText = (EditText) findViewById(R.id.reply_body_edittext);
		bodyText.setText(prefix);
		if(prefix != null)
			bodyText.setSelection(prefix.length());

		
		button_commit = (Button)findViewById(R.id.reply_commit_button);
		button_cancel = (Button)findViewById(R.id.reply_cancel_button);
		
		button_commit.setOnClickListener(new ButtonCommitListener(REPLY_URL));
		button_cancel.setOnClickListener( new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PostActivity.this.finish();
		 	}
			}
		);
		
	}
	class ButtonCommitListener implements OnClickListener{

		private final String url;
		ButtonCommitListener(String url){
			this.url = url;
		}
		@Override
		public void onClick(View v) {
			if(action.equals("reply")){
				handleReply(v);
			}else if(action.equals("new")){
				handleNewThread(v);
			}
		}
		public void handleNewThread(View v){
			handleReply(v);
			
		}
		
		public void handleReply(View v) {


			act.setPost_subject_(titleText.getText().toString());
			act.setPost_content_(bodyText.getText().toString()+ sig);
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
		protected String doInBackground(String... params) {
			if(params.length<2)
				return "parameter error";
			String ret = "�������";
			String url = params[0];
			String body = params[1];
			
			HttpPostClient c =  new HttpPostClient(url);
			String cookie = "ngaPassportUid="+ app.getUid()+
			"; ngaPassportCid=" + app.getCid();
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
			PostActivity.this.finish();
			super.onPostExecute(result);
		}
		
		
	}

}