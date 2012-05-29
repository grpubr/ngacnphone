package sp.phone.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
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
	implements PerferenceConstant,OnItemClickListener{
	
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	private MyApp app;
	ViewPager pager;
	View view;
	//boolean newVersion = false;
	OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTheme(R.style.AppTheme);
		Intent intent = getIntent();
		app = ((MyApp) getApplication());
		loadConfig(intent);
		initDate();
		initView();

	}


	private void loadConfig(Intent intent) {
		//initUserInfo(intent);
		this.boardInfo = this.loadDefaultBoard();;//this.loadBoardInfo();
		
			
		
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
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		pager.setAdapter(
				new BoardPagerAdapter( getSupportFragmentManager(),boardInfo) );	

		super.onResume();
	}

	private void jumpToLogin() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		try {
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
			{
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		} catch (Exception e) {

			// /System.out.print("123");
		}

	}

	private void jumpToSetting() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		try {
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			
		} catch (Exception e) {

			
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
			//break;
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

		pager = (ViewPager) findViewById(R.id.pager);
		


		
		if(app.isNewVersion()){
			new AlertDialog.Builder(this).setTitle("��ʾ")
			.setMessage(StringUtil.getTips())
			.setPositiveButton("֪����", null).show();
			
			app.setNewVersion(false);
			
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
		
		SharedPreferences share = getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		String recentStr = share.getString(RECENT_BOARD, "");
		if(!StringUtil.isEmpty(recentStr)){
			List<Board> recentList = JSON.parseArray(recentStr, Board.class);
			if(recentList != null){
				for(int j = 0;j< recentList.size();j++){
					boards.add(recentList.get(j));
				}
			}
		}
		boards.addCategoryName(i, "�������");
		i++;
		
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
		
		boards.add(new Board(i, "318", "�����ƻ���3", R.drawable.p318));
		boards.add(new Board(i, "403", "����/��װ/����", R.drawable.pdefault));
		boards.add(new Board(i, "351", "װ������", R.drawable.p401));
		boards.add(new Board(i, "393", "����������������Ʒ", R.drawable.p393));
		boards.add(new Board(i, "400", "ְҵ������", R.drawable.pdefault));
		boards.add(new Board(i, "395", "Ұ����", R.drawable.p395));
		boards.add(new Board(i, "396", "��ħ��", R.drawable.p396));
		boards.add(new Board(i, "397", "��ɮ", R.drawable.p397));
		boards.add(new Board(i, "398", "��ҽ", R.drawable.p398));
		boards.add(new Board(i, "399", "ħ��ʦ", R.drawable.p399));
		boards.addCategoryName(i, "�����ƻ���");
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
						if(PhoneConfiguration.getInstance().showAnimation)
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
			
			
			addToRecent();
			if (!StringUtil.isEmpty(url)) {
				Intent intent = new Intent();
				intent.putExtra("tab", "1");
				intent.putExtra("fid", fid);
				intent.setClass(MainActivity.this, TopicListActivity.class);
				startActivity(intent);
				if(PhoneConfiguration.getInstance().showAnimation)
				{
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			position = arg2;
			fidString=(String) arg0.getItemAtPosition(position);
			onClick(arg1);
			
		}
		
		private void addToRecent() {
			BoardCategory recent = boardInfo.getCategory(0);
			if(recent != null)
				recent.remove(fidString);
			for (int i = 1; i < boardInfo.getCategoryCount(); i++) {
				boolean exit = false;
				BoardCategory curr = boardInfo.getCategory(i);
				for (int j = 0; j < curr.size(); j++) {
					Board b = curr.get(j);
					if (b.getUrl().equals(fidString)) {
						Board b1 =new Board(0, b.getUrl(), b.getName(), b
								.getIcon());
						if(recent == null){
							boardInfo.add(b1);
						}else
						{
							recent.addFront(b1);
						}
						
						recent = boardInfo.getCategory(0);
						String rescentStr = JSON.toJSONString(recent
								.getBoardList());
						SharedPreferences share = getSharedPreferences(PERFERENCE,
								MODE_PRIVATE);
						Editor editor = share.edit();
						editor.putString(RECENT_BOARD, rescentStr);
						editor.commit();
						pager.getAdapter().notifyDataSetChanged();
						exit = true;
						break;
					}//if
				}//for j
				if(exit)
				{
					break;
				}
			}//for i
			
		}
	}




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.onItemClickListenerlistener.onItemClick(parent, view, position, id);
		
	}

}


