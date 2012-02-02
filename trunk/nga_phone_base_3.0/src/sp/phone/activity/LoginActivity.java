package sp.phone.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sp.phone.forumoperation.HttpPostClient;

public class LoginActivity extends Activity {
	
	EditText userText;
	EditText passwordText;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		
		Button button_login = (Button)findViewById(R.id.login_button);
		userText = (EditText)findViewById(R.id.login_user_edittext);
		passwordText = (EditText)findViewById(R.id.login_password_edittext);
		
		String postUrl = "http://account.178.com/q_account.php?_act=login";
		
		SharedPreferences  share = 
			LoginActivity.this.getSharedPreferences("perference", MODE_PRIVATE);
		String userName = share.getString("username", "");
		if(userName != "")
			userText.setText(userName);
		
		LoginButtonListener listener = 
				new LoginButtonListener(postUrl);
		button_login.setOnClickListener(listener);
	}
	
	
	class LoginButtonListener implements OnClickListener{
		private HttpPostClient c ;
		private String uid;
		private String cid;
		private  final String LOG_TAG = LoginButtonListener.class
		.getSimpleName();
		public LoginButtonListener(String loginUrl){
			c = new HttpPostClient(loginUrl);

			uid = null;
			cid = null;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			StringBuffer bodyBuffer = new StringBuffer();
			bodyBuffer.append("type=username&email=");
			bodyBuffer.append(URLEncoder.encode(userText.getText().toString()));
			bodyBuffer.append("&password=");
			bodyBuffer.append(URLEncoder.encode(passwordText.getText().toString()));


			HttpURLConnection conn = c.post_body(bodyBuffer.toString());
			if(! validate(conn)){

				Toast.makeText(v.getContext(),R.string.login_failed, Toast.LENGTH_LONG).show();
			}else
			{
				Toast.makeText(v.getContext(), R.string.login_successfully, Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				/*intent.putExtra("uid", uid);
				intent.putExtra("cid", cid);
				intent.putExtra("username", userText.getText().toString());*/
				intent.setClass(v.getContext(), MainActivity.class);
				SharedPreferences  share = 
					LoginActivity.this.getSharedPreferences("perference", MODE_PRIVATE);
				Editor editor = share.edit();
				editor.putString("uid", uid);
				editor.putString("cid", cid);
				editor.putString("username", userText.getText().toString());
				editor.commit();

				startActivity(intent);
				//LoginActivity.this.finish();
			}
			
		}
		
		private boolean validate(HttpURLConnection conn){
			if( conn == null)
				return false;
			

			
			String cookieVal = null;
			String key = null;
			
			String uid = "";
			String cid = "";
			String location = "";
			for (int i = 0; (key = conn.getHeaderFieldKey(i)) != null; i++) {
				//Log.i(LOG_TAG, conn.getHeaderFieldKey(i) + ":" + conn.getHeaderField(i));
				if (key.equalsIgnoreCase("set-cookie")) {
					cookieVal = conn.getHeaderField(i);
					cookieVal = cookieVal.substring(0, cookieVal.indexOf(';'));
					if(cookieVal.indexOf("_sid=") ==0)
						cid=cookieVal.substring(5);
					if(cookieVal.indexOf("_178c=") ==0)
						uid=cookieVal.substring(6,cookieVal.indexOf('%'));
					
				}
				if (key.equalsIgnoreCase("Location")) 
				{
					location = conn.getHeaderField(i);
					
				}
			}
			if( cid != "" &&uid != "" && location.indexOf("login_success&error=0") != -1)
			{
				this.uid = uid;
				this.cid = cid;
				Log.i(LOG_TAG, "uid =" + uid + ",csid=" + cid);
				return true;
			}
			
			return false;
		}
		
	}

}


