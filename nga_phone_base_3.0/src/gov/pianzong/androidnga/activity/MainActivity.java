package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PerferenceConstant;
import sp.phone.interfaces.PageCategoryOwnner;
import sp.phone.task.AppUpdateCheckTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
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
	implements PerferenceConstant,OnItemClickListener,PageCategoryOwnner{
	static final String TAG = MainActivity.class.getSimpleName();
	static final String RECENT = "�������";
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	private MyApp app;
	ViewPager pager;
	View view;
	AppUpdateCheckTask task = null;
	OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setTheme(R.style.AppTheme);
		Intent intent = getIntent();
		app = ((MyApp) getApplication());
		loadConfig(intent);
		initDate();
		initView();

		task = new AppUpdateCheckTask(this);
		task.execute("");
		
	}


	private void loadConfig(Intent intent) {
		//initUserInfo(intent);
		this.boardInfo = this.loadDefaultBoard();
		
			
		
	}

	
	@Override
	protected void onStop() {
		if(task != null){
			Log.d(TAG,"cancel update check task");
			task.cancel(true);
			task= null;
		}
		super.onStop();
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
			if( getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
				setRequestedOrientation(orentation);
		}else{
			if(getRequestedOrientation() ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
					getRequestedOrientation() ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		
		int width = getResources().getInteger(R.integer.page_category_width);
		pager.setAdapter(
				new BoardPagerAdapter( getSupportFragmentManager(),this,width) );	
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
	
	void jumpToNearby(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, NearbyUserActivity.class);
		
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
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
			//case android.R.id.home: //this is a system id
			//this.finish();
			jumpToNearby();
			break;
		default:
			/*Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.addCategory(Intent.CATEGORY_HOME);
			startActivity(MyIntent);*/
			finish();
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


				
				
				
				File filebase = new File(HttpUtil.PATH);
				if (!filebase.exists()) {
					delay("�����µĻ���Ŀ¼");
					filebase.mkdirs();
				} 
				if(ActivityUtil.isGreaterThan_2_1())
				{
					File f = new File(HttpUtil.PATH_AVATAR_OLD);
					if(f.exists()){
						f.renameTo(new File(HttpUtil.PATH_AVATAR));
						delay("�ƶ�ͷ����λ��");
					}
				}

				
				File file = new File(HttpUtil.PATH_NOMEDIA);
				if (!file.exists()) {
					Log.i(getClass().getSimpleName(),"create .nomedia");
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
				
				
			}
		}.start();
	
	}
	
	/*public BoardCategory getCategory(int page){
		if(this.boardInfo == null)
			return null;
		return boardInfo.getCategory(page);
	}*/
	private BoardHolder loadDefaultBoard(){
		
		BoardHolder boards = new BoardHolder();
		
		int i= 0;
		
		SharedPreferences share = getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		String recentStr = share.getString(RECENT_BOARD, "");
		List<Board> recentList = null;
		if(!StringUtil.isEmpty(recentStr)){
			recentList = JSON.parseArray(recentStr, Board.class);
			if(recentList != null){
				for(int j = 0;j< recentList.size();j++){
					boards.add(recentList.get(j));
				}
			}
		}
		if(recentList != null)
		{
		boards.addCategoryName(i, RECENT);
		i++;
		}
		
		boards.add(new Board(i, "7", "������", R.drawable.p7));
		boards.add(new Board(i, "323", "̨��������", R.drawable.p323));
		boards.add(new Board(i, "-7", "������", R.drawable.p354));
		boards.add(new Board(i, "10", "��ɫ����", R.drawable.p10));
		boards.add(new Board(i, "230", "������˹���ίԱ��", R.drawable.p230));
		boards.add(new Board(i, "387", "�˴�����֮����", R.drawable.p387));
		boards.add(new Board(i, "414", "��Ϸ�ۺ�����", R.drawable.p414));
		boards.add(new Board(i, "305", "305Ȩ����", R.drawable.pdefault));
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
		boards.add(new Board(i, "411", "��������", R.drawable.p411));
		boards.add(new Board(i, "255", "�������", R.drawable.p10));
		boards.add(new Board(i, "306", "��Ա��ļ", R.drawable.p10));
		boards.addCategoryName(i, "ð���ĵ�");
		i++;
		
		boards.add(new Board(i, "264", "�����޾�Ժ", R.drawable.p264));
		boards.add(new Board(i, "8", "��ͼ���", R.drawable.p8));
		boards.add(new Board(i, "102", "����Э��", R.drawable.p102));
		boards.add(new Board(i, "124", "�ڻ�����", R.drawable.pdefault));
		boards.add(new Board(i, "254", "���õ��", R.drawable.p254));
		boards.add(new Board(i, "355", "�����ֵܻ�", R.drawable.p355));
		boards.add(new Board(i, "116", "�漣֮Ȫ", R.drawable.p116));
		boards.addCategoryName(i, "�����֮��");
		i++;
		
		
		boards.add(new Board(i, "173", "�ʺŰ�ȫ", R.drawable.p193));
		boards.add(new Board(i, "201", "ϵͳ����", R.drawable.p201));
		boards.add(new Board(i, "334", "Ӳ������", R.drawable.p334));
		boards.add(new Board(i, "335", "��վ����", R.drawable.p335));
		boards.addCategoryName(i, "ϵͳ��Ӳ������");
		i++;
		
		boards.add(new Board(i, "414", "��Ϸ�ۺ�����", R.drawable.p414));
		boards.add(new Board(i, "412", " ��ʦ֮ŭ", R.drawable.p412));
		boards.add(new Board(i, "318", "Diablo III", R.drawable.p318));
		boards.add(new Board(i, "-46468", "̹������", R.drawable.p46468));
		boards.add(new Board(i, "332", "ս��40K", R.drawable.p332));
		boards.add(new Board(i, "321", "DotA", R.drawable.p321));
		boards.add(new Board(i, "353", "Ŧ��˹Ӣ�۴�", R.drawable.pdefault));
		boards.add(new Board(i, "-2371813", "EVE", R.drawable.p2371813));
		boards.add(new Board(i, "-793427", "��ս��", R.drawable.pdefault));
		boards.add(new Board(i, "416", "���֮��2", R.drawable.pdefault));
		boards.add(new Board(i, "406", "�Ǽ�����2", R.drawable.pdefault));
		boards.addCategoryName(i, "������Ϸ");
		i++;
		
		boards.add(new Board(i, "318", "�����ƻ���3", R.drawable.p318));
		boards.add(new Board(i, "409", "HC������", R.drawable.p403));
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
		
		boards.add(new Board(i, "-522474", "�ۺ�����������", R.drawable.pdefault));
		boards.add(new Board(i, "-152678", "Ӣ������", R.drawable.p152678));
		boards.add(new Board(i, "-1068355", "����", R.drawable.pdefault));
		boards.add(new Board(i, "-447601", " ����Ԫ���ҵ���", R.drawable.houzi));
		boards.add(new Board(i, "-343809", "��į�ĳ����ֲ�", R.drawable.pdefault));
		boards.add(new Board(i, "-131429", "���ݡ���С˵��", R.drawable.pdefault));
		boards.add(new Board(i, "-46468", " ̹������", R.drawable.pdefault));
		boards.add(new Board(i, "-2371813", "NGAפ�������İ칫��", R.drawable.pdefault));
		boards.add(new Board(i, "-124119", "���ܷ��� ", R.drawable.pdefault));
		boards.add(new Board(i, "-84", " ģ��֮��", R.drawable.pdefault));
		boards.add(new Board(i, "-187579", " ��������ʷ�����", R.drawable.pdefault));
		boards.add(new Board(i, "-308670", "Ѫ��ĸ��˿ռ�", R.drawable.pdefault));
		boards.add(new Board(i, "-112905", "��ʥ��", R.drawable.pdefault));
		boards.add(new Board(i, "-8725919", "С���ӽ�", R.drawable.pdefault));
		boards.add(new Board(i, "-608808", "Ѫ�ȳ���", R.drawable.pdefault));
		boards.add(new Board(i, "-469608", "Ӱ������", R.drawable.pdefault));
		boards.add(new Board(i, "-55912", "��������", R.drawable.pdefault));
		boards.add(new Board(i, "-353371", "ɵ������С����", R.drawable.pdefault));
		boards.add(new Board(i, "-538800", "��Ů�����Ԫ", R.drawable.pdefault));
		boards.add(new Board(i, "-522679", "Battlefield 3", R.drawable.pdefault));
		boards.add(new Board(i, "-7678526", "�齫��ѧԺ", R.drawable.pdefault));
		boards.add(new Board(i, "-202020", "һֻIT������������", R.drawable.pdefault));
		boards.add(new Board(i, "-444012", "���ǵ��Ｃ", R.drawable.pdefault));
		boards.add(new Board(i, "-47218", " û�е�������", R.drawable.pdefault));
		boards.add(new Board(i, "-349066", "���Ĳ�԰", R.drawable.pdefault));
		boards.add(new Board(i, "-314508", "���羡ͷ�İٻ���˾", R.drawable.pdefault));		
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
				intent.setClass(MainActivity.this, FlexibleTopicListActivity.class);
				//intent.setClass(MainActivity.this, TopicListActivity.class);
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
		
		private void saveRecent(List<Board> boardList){
			String rescentStr = JSON.toJSONString(boardList);
			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);
			Editor editor = share.edit();
			editor.putString(RECENT_BOARD, rescentStr);
			editor.commit();
			
		}
		
		private void addToRecent() {
			
			boolean recentAlreadExist = boardInfo.getCategoryName(0).equals(RECENT);
			
			BoardCategory recent = boardInfo.getCategory(0);
			if(recent != null && recentAlreadExist)
				recent.remove(fidString);
			//int i = 0;
			for (int i = 0; i < boardInfo.getCategoryCount(); i++) {
				BoardCategory curr = boardInfo.getCategory(i);
				for (int j = 0; j < curr.size(); j++) {
					Board b = curr.get(j);
					if (b.getUrl().equals(fidString)) {
						Board b1 =new Board(0, b.getUrl(), b.getName(), b
								.getIcon());

						if(!recentAlreadExist){
							List<Board> boardList = new ArrayList<Board>();
							boardList.add(b1);
							saveRecent(boardList);
							boardInfo = loadDefaultBoard();
							return;
						}else{
							recent.addFront(b1);
						}
						recent = boardInfo.getCategory(0);
						this.saveRecent(recent.getBoardList());

						return;
					}//if
				}//for j
				
			}//for i
			
		}
	}




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.onItemClickListenerlistener.onItemClick(parent, view, position, id);
		
	}


	@Override
	public int getCategoryCount() {
		if(boardInfo == null)
			return 0;
		return boardInfo.getCategoryCount();
	}


	@Override
	public String getCategoryName(int position) {
		if(boardInfo == null)
			return "";
		return boardInfo.getCategoryName(position);
	}


	@Override
	public BoardCategory getCategory(int category) {
		if(boardInfo == null)
			return null;
		return boardInfo.getCategory(category);
	}

}


