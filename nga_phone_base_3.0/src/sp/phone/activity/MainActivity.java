package sp.phone.activity;

import android.app.Activity;
import android.os.Bundle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipFile;

import sp.phone.bean.RSSFeed;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.RSSUtil;
import sp.phone.utils.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


public class MainActivity extends Activity {

	TextView tv_pre;
	TextView tv_now;
	TextView tv_error;
	ActivityUtil activityUtil = new ActivityUtil(this);
	private MyApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);

		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);
		activityUtil.setBG();
		View view = LayoutInflater.from(this).inflate(R.layout.main, null);
		// setContentView(R.layout.main);
		view.setBackgroundResource(ActivityUtil.bg);
		Intent intent = getIntent();
		setContentView(view);
		initUserInfo(intent);
		initDate();
		initView();

	}
	private void  loadConfig(Intent intent){
		
		initUserInfo(intent);
	}
	private void initUserInfo(Intent intent) {
		app = ((MyApp) getApplication());
		String uid = null;// intent.getStringExtra("uid");
		String cid = null;//intent.getStringExtra("cid");
		String userName = null;//intent.getStringExtra("User");
		
		SharedPreferences  share = 
			this.getSharedPreferences("perference", MODE_PRIVATE);
		
		if( uid != null && cid !=null && uid != "" && cid != ""){
			app.setUid(uid);
			app.setCid(cid);

			Editor editor = share.edit();
			editor.putString("uid", uid);
			editor.putString("cid", cid);
			editor.putString("username", userName);
			editor.commit();
		}else{
			uid = share.getString("uid","");
			cid = share.getString("cid","");
			if( uid != null && cid !=null && uid != "" && cid != ""){
				app.setUid(uid);
				app.setCid(cid);
			}
			boolean downImgWithoutWifi = share.getBoolean("down_load_without_wifi", true);
			app.setDownImgWithoutWifi(downImgWithoutWifi);
		}
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	private void jumpToLogin(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		try{
			startActivity(intent);
			//MainActivity.this.finish();
		}catch(Exception e){
			
			///System.out.print("123");
		}
		
	}
	
	private void jumpToSetting()
	{
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		try{
			startActivity(intent);
			//MainActivity.this.finish();
		}catch(Exception e){
			
			///System.out.print("123");
		}
		
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch( item.getItemId())
		{
			case R.id.mainmenu_login :
				this.jumpToLogin();
				break;
			case R.id.mainmenu_setting :
				this.jumpToSetting();
				break;
			case R.id.mainmenu_exit :
				this.finish();
				break;
		}
		return true;
	}

	private void initView() {
		// TextView titleTV = (TextView) findViewById(R.id.title);
		// titleTV.setText("Դ��һ���򵥵��뷨");
		setTitle("Դ��һ���򵥵��뷨");

		tv_pre = (TextView) findViewById(R.id.tv_pre);
		tv_now = (TextView) findViewById(R.id.tv_now);
		tv_error = (TextView) findViewById(R.id.tv_error);
		GridView gv = (GridView) findViewById(R.id.gride);
		ImageList adapter = new ImageList(this);
		gv.setAdapter(adapter);
	}

	String error = "";
	int error_level = 0;

	private void initDate() {
		app = ((MyApp) getApplication());

		new Thread() {
			public void run() {

				delay("��鱾������...");

				boolean f = check_net();
				if (f) {
					delay("ѡ����������");

					delay("��Ѱ���ٷ�����...");
					boolean status = HttpUtil.selectServer();
					// boolean status = false;
					if (!status) {
						delay("����:��");
						error += "����:��";
					} else {
						delay("���ٷ�������ͨ");
					}

					delay("��黺��Ŀ¼...");
					// File file_sd = new File(HttpUtil.PATH_SD);

					// String[] nga_cache = file_sd.list(new FilenameFilter() {
					// @Override
					// public boolean accept(File dir, String filename) {
					// if ("nga_cache.zip".equals(filename)) {
					// return true;
					// } else {
					// return false;
					// }
					// }
					// });
					// if (nga_cache.length == 1) {
					// HttpUtil.PATH_ZIP = HttpUtil.PATH_SD + nga_cache[0];
					// }

					System.out.println("zip is");

					File file = new File(HttpUtil.PATH);
					if (!file.exists()) {
						delay("�����µĻ���Ŀ¼");
						file.mkdirs();
					} else {
						delay("����Ŀ¼����");
					}
					HttpUtil.PATH_ZIP = HttpUtil.PATH_SD
							+ "nga_cache/nga_cache.zip";
					File file_zip = new File(HttpUtil.PATH_ZIP);
					System.out.println("zip:" + HttpUtil.PATH_ZIP);
					if (file_zip.exists()) {
						try {
							ZipFile zf = new ZipFile(HttpUtil.PATH_ZIP);
							app.setZf(zf);
							System.out.println("exists.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				} else {
					error_level = 1;
					delay("û���ҵ����õ�����");
					error += "û���ҵ����õ�����,";
				}

				if (error.equals("")) {
					delay("��ʼ�����.");
				} else {
					delay_error(error);
				}
			};
		}.start();
	}

	private void delay_error(String error) {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("error", error);
		message.setData(b);
		handler.sendMessage(message);
	}

	private void delay(String text) {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("text_now", text);
		message.setData(b);
		handler.sendMessage(message);
	}

	private String text_pre;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			if (b != null) {
				tv_pre.setText(text_pre);

				String error = b.getString("error");
				if (error != null) {
					tv_now.setVisibility(View.GONE);
					tv_error.setVisibility(View.VISIBLE);
					tv_error.setText(error);
					text_pre = error;
				} else {
					String text_now = b.getString("text_now");
					if (text_now != null) {
						tv_now.setVisibility(View.VISIBLE);
						tv_error.setVisibility(View.GONE);
						tv_now.setText(text_now);

					}
					text_pre = text_now;
				}

			}

		}
	};

	private boolean check_net() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	private void getData(final String url) {
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			activityUtil.notice("����ģʽ", str.split(";")[0] + "-----"
					+ str.split(";")[1]);

		} else {
			activityUtil.notice("����ģʽ", str);
		}
		new Thread() {
			@Override
			public void run() {
				RSSUtil rssUtil = new RSSUtil();
				rssUtil.parseXml(url);
				RSSFeed rssFeed = rssUtil.getFeed();

				if (rssFeed != null && rssFeed.getItems().size() != 0) {
					// MyApp app = ((MyApp) getApplicationContext());
					app.setRssFeed(rssFeed);

					HashMap<Object, RSSFeed> map = new HashMap<Object, RSSFeed>();
					if(false)
						map.put(StringUtil.getNowPageNum(rssFeed.getLink()),
							rssFeed);
					app.setMap(map);
					Intent intent = new Intent();
					intent
							.setClass(MainActivity.this,
									TopicListActivity1.class);
					startActivity(intent);

				} else {
					activityUtil.notice("ERROR", "û���ҵ���������");
				}
				activityUtil.dismiss();
			}
		}.start();
	}

	int[] image = { R.drawable.p7,R.drawable.p354, /*R.drawable.tf,*/ R.drawable.p320,
			R.drawable.p181, R.drawable.p187, R.drawable.p185, R.drawable.p189,
			R.drawable.p182, R.drawable.p186, R.drawable.p184, R.drawable.p183,
			R.drawable.p188 };
	String[] urls = { "7","-7", /*"323",*/ "320", "181", "187", "185", "189", "182",
			"186", "184", "183", "188" };
	String[] names = { "������", "������", /*"̨��������",*/ "�ڷ�Ҫ��", "��Ѫɳ��", "���ִ���", "�籩��̳",
			"��Ӱ�ѿ�", "ħ��ʥ��", "����ξ�", "ʥ��֮��", "�������", "��ħ��Ԩ" };

	class ImageList extends BaseAdapter {
		Activity activity;

		public ImageList(Activity a) {
			activity = a;
		}

		public int getCount() {
			return image.length;
		}

		public Object getItem(int position) {
			return image[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			final TextView iv = new TextView(activity);
			Drawable draw = getResources().getDrawable(image[position]);
			iv.setCompoundDrawablesWithIntrinsicBounds(null, draw, null, null);
			iv.setText(names[position]);
			iv.setGravity(Gravity.CENTER_HORIZONTAL);
			iv.setTextColor(android.graphics.Color.BLACK);
			if (error_level == 0) {
				iv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						if (position != 0 && !HttpUtil.HOST_PORT.equals("")) {
							HttpUtil.HOST = HttpUtil.HOST_PORT
									+ HttpUtil.Servlet_timer;
						}

						System.out.println("set host:" + HttpUtil.HOST);

						String url = HttpUtil.Server + "/thread.php?fid="
								+ urls[position] + "&rss=1";
						int fid = 0;
						fid = Integer.parseInt( urls[position]);
						if(fid < 0 && app.getUid() != null && app.getCid() != null){
							
							url = url + "&ngaPassportUid=" + app.getUid()
								+ "&ngaPassportCid=" + app.getCid();
						}
							
						if (!StringUtil.isEmpty(url)) {
							getData(url);
						}
					}
				});
			}
			return iv;
		}
	}

}
