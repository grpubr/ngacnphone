package sp.phone.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.Board;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.RSSFeed;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.RSSUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.android.actionbarcompat.ActionBarActivity;



public class MainActivity extends ActionBarActivity
	implements PerferenceConstant{
	final static int version = 139;
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	private MyApp app;
	View view;
	boolean newVersion = false;
	OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();
	
	public OnItemClickListener getOnItemClickListener(){
		return onItemClickListenerlistener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		


	
		Intent intent = getIntent();
		app = ((MyApp) getApplication());
		loadConfig(intent);
		initDate();
		initView();

	}


	private void loadConfig(Intent intent) {
		initUserInfo(intent);
		this.boardInfo = this.loadDefaultBoard();;//this.loadBoardInfo();
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		if(share.getBoolean(NIGHT_MODE, false))
			ThemeManager.getInstance().setMode(1);
		
		ThemeManager.getInstance().screenOrentation = 
				share.getInt(SCREEN_ORENTATION,ActivityInfo.SCREEN_ORIENTATION_USER);
		
		
		int version_in_config = share.getInt(VERSION, 0);
		if(version_in_config < version){
			newVersion = true;
			Editor editor = share.edit();
			editor.putInt(VERSION, version);
			editor.putBoolean(REFRESH_AFTER_POST, true);

			editor.commit();
			
		}
		

		//refresh
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		config.setRefreshAfterPost(
				share.getBoolean(REFRESH_AFTER_POST,true));
		//font
		final float defTextSize = 21.0f;//new TextView(this).getTextSize();
		final int defWebSize = 16;//new WebView(this).getSettings().getDefaultFontSize();
		
		final float textSize = share.getFloat(TEXT_SIZE, defTextSize);
		final int webSize = share.getInt(WEB_SIZE, defWebSize);
		config.setTextSize(textSize);
		config.setWebSize(webSize);
		
		boolean notification = share.getBoolean(ENABLE_NOTIFIACTION, true);
		boolean notificationSound = share.getBoolean(NOTIFIACTION_SOUND, true);
		config.notification = notification;
		config.notificationSound = notificationSound;
		
		//bookmarks
		String bookmarkJson = share.getString(BOOKMARKS, "");
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		try{
		if(!bookmarkJson.equals(""))
			bookmarks=JSON.parseArray(bookmarkJson, Bookmark.class);
		}catch(Exception e){
			Log.e("JSON_error",Log.getStackTraceString(e));
		}
		PhoneConfiguration.getInstance().setBookmarks(bookmarks);
		
		
		
			
		
	}

	private void initUserInfo(Intent intent) {
		
		String uid = null;
		String cid = null;
		
		PhoneConfiguration config = PhoneConfiguration.getInstance();

		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);

		
			uid = share.getString(UID, "");
			cid = share.getString(CID, "");
			if (uid != null && cid != null && uid != "" && cid != "") {
				config.setUid(uid);
				config.setCid(cid);
			}
			boolean downImgWithoutWifi = share.getBoolean(DOWNLOAD_IMG_NO_WIFI, false);
			config.setDownImgNoWifi(downImgWithoutWifi);
			boolean downAvatarNoWifi = share.getBoolean(DOWNLOAD_AVATAR_NO_WIFI, false);
			config.setDownAvatarNoWifi(downAvatarNoWifi);

	}


	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		/*
		int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
		int i = 0;
		for(i = 0;i< menu.size();i++){
			ReflectionUtil.setShowAsAction(
					menu.getItem(i), actionNum);
		}
		*/
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		
		return super.onCreateOptionsMenu(menu);
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		/*view.setBackgroundResource(
				ThemeManager.getInstance().getBackgroundColor());
		GridView gv = (GridView) view;// (GridView) findViewById(R.id.gride);
		if(gv != null)
			((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();*/

		super.onResume();
	}

	private void jumpToLogin() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		try {
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			// MainActivity.this.finish();
		} catch (Exception e) {

			// /System.out.print("123");
		}

	}

	private void jumpToSetting() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		try {
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			// MainActivity.this.finish();
		} catch (Exception e) {

			// /System.out.print("123");
		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_login:
			this.jumpToLogin();
			break;
		case R.id.mainmenu_setting:
			this.jumpToSetting();
			break;
		case R.id.mainmenu_exit:
			Intent intent = new Intent();
			intent.putExtra("tab", "1");
			intent.setClass(MainActivity.this, TopicListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			break;
		default:
		//case android.R.id.home: //this is a system id
			this.finish();
			break;

		}
		return true;
	}

	private void initView() {

		setTitle("Դ��һ���򵥵��뷨");
		
		ThemeManager.SetContextTheme(this);
		view = LayoutInflater.from(this).inflate(R.layout.viewpager_main, null);
		view.setBackgroundResource(
			ThemeManager.getInstance().getBackgroundColor());
		setContentView(view);

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(
				new BoardPagerAdapter( getSupportFragmentManager(),boardInfo) );


		
		if(newVersion){
			new AlertDialog.Builder(this).setTitle("��ʾ")
			.setMessage(StringUtil.getTips())
			.setPositiveButton("֪����", null).show();
			
			newVersion = false;
			
		}
		

	}
	
	
	
	private void initDate() {



		new Thread() {
			public void run() {


				File file = new File(HttpUtil.PATH);
				if (!file.exists()) {
					delay("�����µĻ���Ŀ¼");
					file.mkdirs();
				} 
				
				file = new File(HttpUtil.PATH_WEB_CACHE);
				if (!file.exists()) {
					Log.i(getClass().getSimpleName(),"create webcache directory");
					file.mkdirs();
				} 
				
				file = new File(HttpUtil.PATH_NOMEDIA);
				if (!file.exists()) {
					Log.i(getClass().getSimpleName(),"create .nomedia");
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				
				
			}
		}.start();
	
	}
	private BoardHolder loadDefaultBoard(){
		
		BoardHolder boards = new BoardHolder();
		
		int i= 0;
		
		boards.add(new Board(i, "7", "������˹������", R.drawable.p7));
		boards.add(new Board(i, "323", "̨��������", R.drawable.p323));
		boards.add(new Board(i, "-7", "������", R.drawable.p354));
		boards.add(new Board(i, "10", "��ɫ����������", R.drawable.p10));
		boards.add(new Board(i, "230", "������˹���ίԱ��", R.drawable.p230));
		boards.add(new Board(i, "387", "�˴�����֮����", R.drawable.p387));
		boards.addCategoryName(i, "�ۺ�����");
		i++;
		
		boards.add(new Board(i, "390", "�峿��", R.drawable.p390));
		boards.add(new Board(i, "320", "�ڷ�Ҫ��", R.drawable.p320));
		boards.add(new Board(i, "181", "��Ѫɳ��", R.drawable.p181));
		boards.add(new Board(i, "182", "ħ��ʥ��", R.drawable.p182));
		boards.add(new Board(i, "183", "�������", R.drawable.p183));
		boards.add(new Board(i, "185", "�籩��̳", R.drawable.p185));
		boards.add(new Board(i, "186", "����ξ�", R.drawable.p186));
		boards.add(new Board(i, "187", "���ִ���", R.drawable.p187));
		boards.add(new Board(i, "184", "ʥ��֮��", R.drawable.p184));
		boards.add(new Board(i, "188", "��ħ��Ԩ", R.drawable.p188));
		boards.add(new Board(i, "189", "��Ӱ�ѿ�", R.drawable.p189));
		boards.addCategoryName(i, "ְҵ������");
		i++;
		
		boards.add(new Board(i, "310", "ǰհ��Ѷ", R.drawable.p310));
		boards.add(new Board(i, "190", "��������", R.drawable.p190));
		boards.add(new Board(i, "213", "ս������", R.drawable.p213));
		boards.add(new Board(i, "218", "����ר��", R.drawable.p218));
		boards.add(new Board(i, "258", "ս������", R.drawable.p258));
		boards.add(new Board(i, "272", "������", R.drawable.p272));
		boards.add(new Board(i, "191", "�ؾ��̻�", R.drawable.p191));
		boards.add(new Board(i, "200", "����о�", R.drawable.p200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.p240));
		boards.add(new Board(i, "274", "�������", R.drawable.p274));
		boards.add(new Board(i, "315", "ս��ͳ��", R.drawable.p315));
		boards.add(new Board(i, "333", "DKPϵͳ", R.drawable.p333));
		boards.add(new Board(i, "327", "�ɾ�����", R.drawable.p327));
		boards.add(new Board(i, "388", "�û�����", R.drawable.p388));
		boards.add(new Board(i, "255", "�������", R.drawable.p10));
		boards.add(new Board(i, "306", "��Ա��ļ", R.drawable.p10));
		boards.addCategoryName(i, "�ĵ�����");
		i++;
		
		boards.add(new Board(i, "264", "�����޾�Ժ", R.drawable.p264));
		boards.add(new Board(i, "8", "��ͼ���", R.drawable.p8));
		boards.add(new Board(i, "102", "����Э��", R.drawable.p102));
		boards.add(new Board(i, "124", "�ڻ�����", R.drawable.pdefault));
		boards.add(new Board(i, "254", "���õ��", R.drawable.p254));
		boards.add(new Board(i, "355", "�����ֵܻ�", R.drawable.p355));
		boards.add(new Board(i, "116", "�漣֮Ȫ", R.drawable.p116));
		boards.addCategoryName(i, "�ܱ�����");
		i++;
		
		
		boards.add(new Board(i, "173", "�ʺŰ�ȫ", R.drawable.p193));
		boards.add(new Board(i, "201", "ϵͳ����", R.drawable.p201));
		boards.add(new Board(i, "334", "Ӳ������", R.drawable.p334));
		boards.add(new Board(i, "335", "��վ����", R.drawable.p335));
		boards.addCategoryName(i, "ϵͳ��Ӳ������");
		i++;
		
		
		boards.add(new Board(i, "318", "Diablo III", R.drawable.p318));
		boards.add(new Board(i, "-46468", "̹������", R.drawable.p46468));
		boards.add(new Board(i, "332", "ս��40K", R.drawable.p332));
		boards.add(new Board(i, "321", "DotA", R.drawable.p321));
		boards.add(new Board(i, "353", "Ŧ��˹Ӣ�۴�", R.drawable.pdefault));
		boards.addCategoryName(i, "������Ϸ");
		i++;
		
		boards.add(new Board(i, "-152678", "Ӣ������ Let's Gank", R.drawable.p152678));
		boards.add(new Board(i, "-1068355", "����", R.drawable.pdefault));
		boards.add(new Board(i, "-447601", " ����Ԫ���ҵ��� - NG2", R.drawable.pdefault));
		boards.add(new Board(i, "-152678", "Ӣ������ Let's Gank", R.drawable.pdefault));
		boards.add(new Board(i, "-343809", "��į�ĳ����ֲ�", R.drawable.pdefault));
		boards.add(new Board(i, "-131429", "���ݡ���С˵��", R.drawable.pdefault));
		boards.add(new Board(i, "-46468", " ����˹��̹������", R.drawable.pdefault));
		boards.add(new Board(i, "-2371813", "NGAפ�������İ칫��", R.drawable.pdefault));
		boards.add(new Board(i, "-124119", "���ܷ��ۡ���ʥ������ ", R.drawable.pdefault));
		boards.add(new Board(i, "-84", " ģ��֮��", R.drawable.pdefault));
		boards.add(new Board(i, "-187579", " ��������ʷ�����", R.drawable.pdefault));
		boards.add(new Board(i, "-308670", "Ѫ��ĸ��˿ռ�", R.drawable.pdefault));
		boards.add(new Board(i, "-112905", "��ʥ��", R.drawable.pdefault));
		boards.addCategoryName(i, "���˰���");
		i++;
		
		
		return boards;
	}


	private BoardHolder boardInfo;
	





	private void delay(String text) {
		final String msg = text;
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
			
		});
	}



	

	private void getData(final String url) {
		activityUtil.noticeSaying(MainActivity.this);
		new GetToplistRssThread(url).start();
	}

	class GetToplistRssThread extends Thread {
		final String url;

		public GetToplistRssThread(String url) {
			super();
			this.url = url;
		}

		@Override
		public void run() {
			RSSUtil rssUtil = new RSSUtil();
			rssUtil.parseXml(url);
			RSSFeed rssFeed = rssUtil.getFeed();

			if (rssFeed != null && rssFeed.getItems().size() != 0) {
				app.setRssFeed(rssFeed);

				activityUtil.dismiss();
				runOnUiThread(new Runnable() {
					public void run() {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								TopicListActivity1.class);
						startActivity(intent);
						overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
					}

				});

			} else if(rssUtil.getErrorCode() == RSSUtil.NETWORK_ERROR){
				activityUtil.noticeError( "û���ҵ���������",MainActivity.this);
			} else if(rssUtil.getErrorCode() == RSSUtil.DOCUMENT_ERROR){
				activityUtil.noticeError( "�����µ�¼",MainActivity.this);
			}else {
				activityUtil.noticeError( "δ֪����",MainActivity.this);
			}
			
		}
	}


	

	class EnterToplistLintener implements OnItemClickListener , OnClickListener {
		int position;
		String fidString;

		public EnterToplistLintener(int position, String fidString) {
			super();
			this.position = position;
			this.fidString = fidString;
		}
		public EnterToplistLintener(){//constructoer	
		}

		public void onClick(View v) {

			if (position != 0 && !HttpUtil.HOST_PORT.equals("")) {
				HttpUtil.HOST = HttpUtil.HOST_PORT + HttpUtil.Servlet_timer;
			}
			int fid = 0;
			try {
				fid = Integer.parseInt(fidString);
			} catch (Exception e) {
				final String tag = this.getClass().getSimpleName();
				Log.e(tag, Log.getStackTraceString(e));
				Log.e(tag, "invalid fid " + fidString);
			}
			if (fid == 0) {
				String tip = fidString + "���ǺϷ��İ��id";
				Toast.makeText(app, tip, Toast.LENGTH_LONG);
				return;
			}

			Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
					+ "&rss=1";
			PhoneConfiguration config = PhoneConfiguration.getInstance();
			if ( !StringUtil.isEmpty(config.getCookie())) {

				url = url + "&" + config.getCookie().replace("; ", "&");
			}else if(fid<0){
				new AlertDialog.Builder(MainActivity.this).setTitle("��ʾ")
				.setMessage("���˰��Ҫ��¼�˲��ܽ�ȥ")
				.setPositiveButton("֪����", null).show();
				return;
			}

			if (!StringUtil.isEmpty(url)) {
				getData(url);
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			position = arg2;
			fidString=(String) arg0.getItemAtPosition(position);
			onClick(arg1);
			
		}
	}

}


