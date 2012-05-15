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
import android.support.v4.app.FragmentActivity;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;




public class MainActivity extends FragmentActivity
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
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);

	
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
		
		/*if(this.boardInfo == null || this.boardInfo.getCategoryCount() == 0 || boardInfo.get(0, 0) == null )
		{
			Editor editor = share.edit();
			this.boardInfo = this.resetBoard();
			String infoString = JSON.toJSONString(boardInfo);
			editor.putString(BOARDS, infoString);
			editor.commit();
		}*/
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
		/*ActionBar.DISPLAY_SHOW_HOME;//2
		flags |= ActionBar.DISPLAY_USE_LOGO;//1
		flags |= ActionBar.DISPLAY_SHOW_TITLE;//8
		flags |= ActionBar.DISPLAY_HOME_AS_UP;//4
		*/

		int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
		int i = 0;
		for(i = 0;i< menu.size();i++){
			ReflectionUtil.setShowAsAction(
					menu.getItem(i), actionNum);
		}
		
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		view.setBackgroundResource(
				ThemeManager.getInstance().getBackgroundColor());
		GridView gv = (GridView) findViewById(R.id.gride);
		if(gv != null)
			((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();

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
		default:
		//case android.R.id.home: //this is a system id
			this.finish();
			break;

		}
		return true;
	}

	private void initView() {

		setTitle("源于一个简单的想法");
		
		ThemeManager.SetContextTheme(this);
		view = LayoutInflater.from(this).inflate(R.layout.viewpager_main, null);
		view.setBackgroundResource(
			ThemeManager.getInstance().getBackgroundColor());
		setContentView(view);

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		//this.getSupportFragmentManager();
		pager.setAdapter(
				new BoardPagerAdapter( getSupportFragmentManager(),boardInfo) );

		/*GridView gv = (GridView) findViewById(R.id.gride);
		ImageGridList adapter = new ImageGridList(this);	
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new EnterToplistLintener());
		MainActivity.this.registerForContextMenu(gv);*/
		
		if(newVersion){
			new AlertDialog.Builder(this).setTitle("提示")
			.setMessage(StringUtil.getTips())
			.setPositiveButton("知道了", null).show();
			
			newVersion = false;
			
		}
		

	}
	
	
	/*
	//AdapterContextMenuInfo lastMenuInfo = null;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//GridView gv; gv.getChildAt(1);
		//TextView clicked = (TextView)((AdapterContextMenuInfo)menuInfo).targetView;
		menu.add(0,ADD_BOARD_INDEX,0, "加新板块");
		menu.add(0,ADD_BOARD_INDEX+ 1,0, "删除板块");
		menu.add(0,ADD_BOARD_INDEX+ 2,0, "重置所有板块");
		//lastMenuInfo = new AdapterContextMenuInfo(v, 1, 1);
		
	}



	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		

		switch (item.getItemId()) {
		case ADD_BOARD_INDEX:
			handleAddBoard();
			break;
		case ADD_BOARD_INDEX + 1: //del
			
			int position = info.position;

				String fid = this.boardInfo.get(category,position).getUrl();
				if(removeCustomBoard(fid))
				{
					GridView gv = (GridView) findViewById(R.id.gride);
					((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();
				}				
			break;
		case ADD_BOARD_INDEX + 2: //del
			this.boardInfo = this.resetBoard();
			this.flushBoardInfo();
			GridView gv = (GridView) findViewById(R.id.gride);
			((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();
		break;
			
		}
		
		return super.onContextItemSelected(item);
	}




	private void handleAddBoard() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        final EditText input = new EditText(this);  
        alert.setView(input);  
        alert.setTitle("输入板块名  板块id");
        alert.setMessage("类似: 议事厅(空格)7");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                String value = input.getText().toString().trim(); 
                final  String values[] = value.split(" ");
                
                if(values.length <2){
                	Toast.makeText(MainActivity.this, "输入非法",  
                        Toast.LENGTH_SHORT).show();
                }else if(addCustomBoard(values[1], values[0]))
                {
                	final GridView gv = (GridView) findViewById(R.id.gride);
                	((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();
                	
                	final String uri = "http://img4.ngacn.cc/ngabbs/nga_classic/f/"
        					+values[1]+".png";
        				final String fileName = HttpUtil.PATH_ICON+"/"+values[1]+".png";
        				
        				new Thread() {
        					public void run() {
        						HttpUtil.downImage(uri,fileName+".bak");
        						File f = new File(fileName+".bak");
        						if(f.exists()){
        							f.renameTo(new File(fileName));
        							//((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();
        						}
        					};
        				}.start();
                }
                
            }  
        });  
  
        alert.setNegativeButton("Cancel",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        dialog.cancel();  
                    }  
                });  
        alert.show(); 
		
	}*/



	static final int ADD_BOARD_INDEX = 1;


	//String error = "";
	//int error_level = 0;

	private void initDate() {
		app = ((MyApp) getApplication());
		//prepareGridData();


		new Thread() {
			public void run() {


				File file = new File(HttpUtil.PATH);
				if (!file.exists()) {
					delay("创建新的缓存目录");
					file.mkdirs();
				} else {
					delay("缓存目录正常");
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
		
		boards.add(new Board(i, "7", "艾泽拉斯议事厅", R.drawable.p7));
		boards.add(new Board(i, "323", "台服讨论区", R.drawable.p323));
		boards.add(new Board(i, "-7", "大漩涡", R.drawable.p354));
		boards.add(new Board(i, "10", "银色黎明裁判所", R.drawable.p10));
		boards.add(new Board(i, "230", "艾泽拉斯风纪委员会", R.drawable.p230));
		boards.add(new Board(i, "387", "潘大力亚之迷雾", R.drawable.p387));
		boards.addCategoryName(i, "综合讨论");
		i++;
		
		boards.add(new Board(i, "390", "五晨寺", R.drawable.p390));
		boards.add(new Board(i, "320", "黑锋要塞", R.drawable.p320));
		boards.add(new Board(i, "181", "铁血沙场", R.drawable.p181));
		boards.add(new Board(i, "182", "魔法圣堂", R.drawable.p182));
		boards.add(new Board(i, "183", "信仰神殿", R.drawable.p183));
		boards.add(new Board(i, "185", "风暴祭坛", R.drawable.p185));
		boards.add(new Board(i, "186", "翡翠梦境", R.drawable.p186));
		boards.add(new Board(i, "187", "猎手大厅", R.drawable.p187));
		boards.add(new Board(i, "184", "圣光之力", R.drawable.p184));
		boards.add(new Board(i, "188", "恶魔深渊", R.drawable.p188));
		boards.add(new Board(i, "189", "暗影裂口", R.drawable.p189));
		boards.addCategoryName(i, "职业讨论区");
		i++;
		
		boards.add(new Board(i, "310", "前瞻资讯", R.drawable.p310));
		boards.add(new Board(i, "190", "任务讨论", R.drawable.p190));
		boards.add(new Board(i, "213", "战争档案", R.drawable.p213));
		boards.add(new Board(i, "218", "副本专区", R.drawable.p218));
		boards.add(new Board(i, "258", "战场讨论", R.drawable.p258));
		boards.add(new Board(i, "272", "竞技场", R.drawable.p272));
		boards.add(new Board(i, "191", "地精商会", R.drawable.p191));
		boards.add(new Board(i, "200", "插件研究", R.drawable.p200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.p240));
		boards.add(new Board(i, "274", "插件发布", R.drawable.p274));
		boards.add(new Board(i, "315", "战斗统计", R.drawable.p315));
		boards.add(new Board(i, "333", "DKP系统", R.drawable.p333));
		boards.add(new Board(i, "327", "成就讨论", R.drawable.p327));
		boards.add(new Board(i, "388", "幻化讨论", R.drawable.p388));
		boards.add(new Board(i, "255", "公会管理", R.drawable.p10));
		boards.add(new Board(i, "306", "人员招募", R.drawable.p10));
		boards.addCategoryName(i, "心得讨论");
		i++;
		
		boards.add(new Board(i, "264", "卡拉赞剧院", R.drawable.p264));
		boards.add(new Board(i, "8", "大图书馆", R.drawable.p8));
		boards.add(new Board(i, "102", "作家协会", R.drawable.p102));
		boards.add(new Board(i, "124", "壁画洞窟", R.drawable.pdefault));
		boards.add(new Board(i, "254", "镶金玫瑰", R.drawable.p254));
		boards.add(new Board(i, "355", "龟岩兄弟会", R.drawable.p355));
		boards.add(new Board(i, "116", "奇迹之泉", R.drawable.p116));
		boards.addCategoryName(i, "周边讨论");
		i++;
		
		
		boards.add(new Board(i, "173", "帐号安全", R.drawable.p193));
		boards.add(new Board(i, "201", "系统问题", R.drawable.p201));
		boards.add(new Board(i, "334", "硬件配置", R.drawable.p334));
		boards.add(new Board(i, "335", "网站开发", R.drawable.p335));
		boards.addCategoryName(i, "系统软硬件讨论");
		i++;
		
		
		boards.add(new Board(i, "318", "Diablo III", R.drawable.p318));
		boards.add(new Board(i, "-46468", "坦克世界", R.drawable.p46468));
		boards.add(new Board(i, "332", "战锤40K", R.drawable.p332));
		boards.add(new Board(i, "321", "DotA", R.drawable.p321));
		boards.add(new Board(i, "353", "纽沃斯英雄传", R.drawable.pdefault));
		boards.addCategoryName(i, "其他游戏");
		i++;
		
		boards.add(new Board(i, "-152678", "英雄联盟 Let's Gank", R.drawable.p152678));
		boards.add(new Board(i, "-1068355", "晴风村", R.drawable.pdefault));
		boards.add(new Board(i, "-447601", " 二次元国家地理 - NG2", R.drawable.pdefault));
		boards.add(new Board(i, "-152678", "英雄联盟 Let's Gank", R.drawable.pdefault));
		boards.add(new Board(i, "-343809", "寂寞的车俱乐部", R.drawable.pdefault));
		boards.add(new Board(i, "-131429", "红茶馆——小说馆", R.drawable.pdefault));
		boards.add(new Board(i, "-46468", " 洛拉斯的坦克世界", R.drawable.pdefault));
		boards.add(new Board(i, "-2371813", "NGA驻吉他海四办公室", R.drawable.pdefault));
		boards.add(new Board(i, "-124119", "菠萝方舟·神圣避难所 ", R.drawable.pdefault));
		boards.add(new Board(i, "-84", " 模玩之魂", R.drawable.pdefault));
		boards.add(new Board(i, "-187579", " 大旋涡历史博物馆", R.drawable.pdefault));
		boards.add(new Board(i, "-308670", "血库的个人空间", R.drawable.pdefault));
		boards.add(new Board(i, "-112905", "八圣祠", R.drawable.pdefault));
		boards.addCategoryName(i, "个人版面");
		i++;
		
		
		return boards;
	}


	private BoardHolder boardInfo;
	/*final private int category = 0;
	private BoardHolder getOptionBoard() {
		return  boardInfo;
	}
	
	private void setOptionBoard(BoardHolder info){	
		this.boardInfo = info;
	}
	
	private void flushBoardInfo(){
		String infoString = JSON.toJSONString(boardInfo);
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(BOARDS, infoString);
		editor.commit();
		
	}*/
	/*
	private void loadBoardInfo(){
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		String infoString = share.getString(BOARDS, "");
		if(boardInfo==null)
			boardInfo = new BoardHolder();
		if(!infoString.equals(""))
		{
			try{
			boardInfo  =  JSON.parseObject(infoString, BoardHolder.class);
			//boardInfo.convertChildren();
			}catch(Exception e){
				Log.e("", Log.getStackTraceString(e));
			}
			
		}

			
		
	}*/
	
	/*private boolean addCustomBoard(String fid,String name){
		BoardHolder boards = getOptionBoard();
		boards.add(new Board(fid, name));
		synchronized(this){
			setOptionBoard(boards);
			flushBoardInfo();
			
		}
		//refresh
		//this.prepareGridData();
		return true;
	}
	
	private boolean removeCustomBoard(String fid){
		BoardHolder boards = getOptionBoard();


		
		boards.remove(category,fid);
		

		//urlList.remove(index);
		//nameList.remove(index);
		
		synchronized (this) {
			//setOptionBoard(boards);
			flushBoardInfo();

		}
		
		//refresh
		//this.prepareGridData();
		return true;
	}*/





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
				activityUtil.noticeError( "没有找到可用网络",MainActivity.this);
			} else if(rssUtil.getErrorCode() == RSSUtil.DOCUMENT_ERROR){
				activityUtil.noticeError( "请重新登录",MainActivity.this);
			}else {
				activityUtil.noticeError( "未知错误",MainActivity.this);
			}
			
		}
	}


	/*class ImageGridList extends BaseAdapter {
		Activity activity;
		
		private Map<String,Drawable> iconMap;
		public ImageGridList(Activity a) {
			activity = a;
			iconMap = new HashMap<String,Drawable>();
		}

		public int getCount() {
			return boardInfo.size(category);
		}

		public Object getItem(int position) {
			return boardInfo.get(category,position).getUrl();//urls[position];
		}

		public long getItemId(int position) {
			return position;
		}
		
		class ViewHolder{
			ImageView img;
			TextView text;
		};
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.board_icon,
						null);

				ImageView iconView = (ImageView) convertView
						.findViewById(R.id.board_imgicon);
				TextView tv = (TextView) convertView
						.findViewById(R.id.board_name_view);
				holder.img = iconView;
				holder.text = tv;
				convertView.setTag(holder);
				//iconView.setGravity(Gravity.CENTER_HORIZONTAL);
				ReflectionUtil.view_setGravity(convertView, Gravity.CENTER_HORIZONTAL);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			
			Drawable draw = getDrable(convertView, position);
			holder.img.setImageDrawable(draw);
			holder.text.setText(boardInfo.get(category,position).getName());
			return convertView;
			//return iv;
		}


		private Drawable getDrable(View convertView, int position) {
			Drawable d = null;
			final String url = boardInfo.get(category,position).getUrl();
			int resId = boardInfo.get(category,position).getIcon();
			if (resId != 0) {// default board
				d = getResources().getDrawable(resId);
			} else {// optional board
				d = iconMap.get(url);
				if (d == null) {
					final String iconFolder = HttpUtil.PATH_ICON;
					String iconPath = iconFolder + "/" + url + ".png";

					// def = getResources().getDrawable(R.drawable.pdefault);

					try {
						Bitmap bmp = BitmapFactory
								.decodeStream(new FileInputStream(iconPath));
						d = new BitmapDrawable(bmp);
					} catch (FileNotFoundException e) {
						d = getResources().getDrawable(R.drawable.pdefault);

					}

					iconMap.put(url, d);
				}
			}

			return d;
		}

	}

	*/

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
				String tip = fidString + "不是合法的板块id";
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
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
				.setMessage("个人板块要登录了才能进去")
				.setPositiveButton("知道了", null).show();
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


