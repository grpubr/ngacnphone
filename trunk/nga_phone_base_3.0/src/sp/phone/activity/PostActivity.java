package sp.phone.activity;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.ThreadPostAction;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PostActivity extends Activity {

	private String prefix;
	private EditText titleText;
	private EditText bodyText;
	private ThreadPostAction act; 
	private String action;
	private String tid;
	private Button button_commit;
	private Button button_cancel;
	private MyApp app;
	private String REPLY_URL="http://bbs.ngacn.cc/post.php?";
	private String sig ="\n----sent from my android app";
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.reply);
		Intent intent = this.getIntent();
		prefix = intent.getStringExtra("prefix");
		action = intent.getStringExtra("action");
		tid = intent.getStringExtra("tid");
		app = (MyApp)getApplication();
		
		act = new ThreadPostAction(tid, "", "");
		act.setAction_(action);
		
		titleText = (EditText) findViewById(R.id.reply_titile_edittext);
		bodyText = (EditText) findViewById(R.id.reply_body_edittext);
		bodyText.setText(prefix);
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
			// TODO Auto-generated method stub
			HttpPostClient c = 
				new HttpPostClient(url);
			String cookie = "ngaPassportUid="+ app.getUid()+
			"; ngaPassportCid=" + app.getCid();
			c.setCookie(cookie);
			act.setPost_subject_(titleText.getText().toString());
			act.setPost_content_(bodyText.getText().toString()+ sig);
			try {
				InputStream input = c.post_body(act.toString()).getInputStream();
				String html = IOUtils.toString(input, "gbk");
				System.out.print(html);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PostActivity.this.finish();
		}
		
	}

}
