package sp.phone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import sp.phone.bean.Bookmark;
import sp.phone.bean.RSSFeed;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.RSSUtil;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import sp.phone.bean.BoardInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.alibaba.fastjson.JSON;

public class MainActivity extends Activity {

	TextView tv_pre;
	TextView tv_now;
	TextView tv_error;
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	private MyApp app;
	View view;
	boolean firstRun;
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
		this.loadBoardInfo();
		SharedPreferences share = this.getSharedPreferences("perference",
				MODE_PRIVATE);
		if(share.getBoolean("nightmode", false))
			ThemeManager.getInstance().setMode(1);
		firstRun = share.getBoolean("firstRun", true);
		if(firstRun){
			Editor editor = share.edit();
			editor.putBoolean("firstRun", false);
			editor.putBoolean("refreshAfterPost", true);
			editor.commit();
		}
		//refresh
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		config.setRefreshAfterPost(
				share.getBoolean("refreshAfterPost",true));
		//font
		final float defTextSize = 21.0f;//new TextView(this).getTextSize();
		final int defWebSize = 16;//new WebView(this).getSettings().getDefaultFontSize();
		
		final float textSize = share.getFloat("textsize", defTextSize);
		final int webSize = share.getInt("websize", defWebSize);
		config.setTextSize(textSize);
		config.setWebSize(webSize);
		
		boolean notification = share.getBoolean("enableNotification", true);
		boolean notificationSound = share.getBoolean("notificationSound", true);
		config.notification = notification;
		config.notificationSound = notificationSound;
		
		//bookmarks
		String bookmarkJson = share.getString("bookmarks", "");
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
		
		String uid = null;// intent.getStringExtra("uid");
		String cid = null;// intent.getStringExtra("cid");
		//String userName = null;// intent.getStringExtra("User");

		SharedPreferences share = this.getSharedPreferences("perference",
				MODE_PRIVATE);

		
			uid = share.getString("uid", "");
			cid = share.getString("cid", "");
			if (uid != null && cid != null && uid != "" && cid != "") {
				app.setUid(uid);
				app.setCid(cid);
			}
			boolean downImgWithoutWifi = share.getBoolean(
					"down_load_without_wifi", true);
			app.setDownImgWithoutWifi(downImgWithoutWifi);
		

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
		// TextView titleTV = (TextView) findViewById(R.id.title);
		// titleTV.setText("源于一个简单的想法");
		setTitle("源于一个简单的想法");
		
		ThemeManager.SetContextTheme(this);
		view = LayoutInflater.from(this).inflate(R.layout.main, null);
		// setContentView(R.layout.main);
		view.setBackgroundResource(
			ThemeManager.getInstance().getBackgroundColor());
		setContentView(view);

		tv_pre = new TextView(this);//(TextView) findViewById(R.id.tv_pre);
		tv_now = new TextView(this);//(TextView) findViewById(R.id.tv_now);
		tv_error = new TextView(this);//(TextView) findViewById(R.id.tv_error);

		GridView gv = (GridView) findViewById(R.id.gride);
		ImageGridList adapter = new ImageGridList(this);	
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new EnterToplistLintener());
		MainActivity.this.registerForContextMenu(gv);
		
		if(firstRun){
			new AlertDialog.Builder(this).setTitle("提示")
			.setMessage(StringUtil.getTips())
			.setPositiveButton("知道了", null).show();
			
			firstRun = false;
			
		}
		

	}
	
	
	
	//AdapterContextMenuInfo lastMenuInfo = null;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		//GridView gv; gv.getChildAt(1);
		//TextView clicked = (TextView)((AdapterContextMenuInfo)menuInfo).targetView;
		menu.add(0,ADD_BOARD_INDEX,0, "加新板块");
		menu.add(0,ADD_BOARD_INDEX+ 1,0, "删除板块"/*+clicked.getText().toString()*/);
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
			if(position < image.length)
				new AlertDialog.Builder(this).setTitle("错误")
				.setMessage("这个删不了")
				.setPositiveButton("不确定", null).show();
		
			else{
				String fid = urls[position];
				if(removeCustomBoard(fid))
				{
					GridView gv = (GridView) findViewById(R.id.gride);
					((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();
				}
				
				
			}
				
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
		
	}



	static final int ADD_BOARD_INDEX = 1;


	String error = "";
	int error_level = 0;

	private void initDate() {
		app = ((MyApp) getApplication());
		prepareGridData();


		new Thread() {
			public void run() {

				delay("检查本地网络...");

				boolean f = check_net();
				if (f) {
					delay("选定可用网络");

					delay("搜寻加速服务器...");
					boolean status = HttpUtil.selectServer();
					// boolean status = false;
					if (!status) {
						delay("加速:否");
						error += "加速:否";
					} else {
						delay("加速服务器畅通");
					}

					delay("检查缓存目录...");

					System.out.println("zip is");

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
					delay("没有找到可用的网络");
					error += "没有找到可用的网络,";
				}

				if (error.equals("")) {
					delay("初始化完毕.");
				} else {
					delay_error(error);
				}
			};
		}.start();
	
	}

	private void prepareGridData() {
		List<BoardInfo> boards = getOptionBoard();
		List<String> urlList = new ArrayList<String>(Arrays.asList(defaults_urls));
		List<String> nameList = new ArrayList<String>(Arrays.asList(defaults_names));

		for (BoardInfo b : boards) {
			urlList.add(b.getUrl());
			nameList.add(b.getName());

		}
		synchronized(this){
			urls = urlList.toArray(new String[urlList.size()]);
			names = nameList.toArray(new String[nameList.size()]);
			
		}

	}
	private List<BoardInfo> boardInfo;
	private List<BoardInfo> getOptionBoard() {
		return  boardInfo;
	}
	
	private void setOptionBoard(List<BoardInfo> info){	
		this.boardInfo = info;
	}
	
	private void flushBoardInfo(){
		String infoString = JSON.toJSONString(boardInfo);
		SharedPreferences share = this.getSharedPreferences("perference",
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("boards", infoString);
		editor.commit();
		
	}
	private void loadBoardInfo(){
		SharedPreferences share = this.getSharedPreferences("perference",
				MODE_PRIVATE);
		String infoString = share.getString("boards", "");
		if(boardInfo==null)
			boardInfo = new ArrayList<BoardInfo>();
		if(!infoString.equals(""))
		{
			try{
			boardInfo  =  JSON.parseArray(infoString, BoardInfo.class);
			}catch(Exception e){
				Log.e("", Log.getStackTraceString(e));
			}
			
		}

			
		
	}
	
	private boolean addCustomBoard(String fid,String name){
		List<BoardInfo> boards = getOptionBoard();
		boards.add(new BoardInfo(fid, name));
		synchronized(this){
			setOptionBoard(boards);
			flushBoardInfo();
			
		}
		//refresh
		this.prepareGridData();
		return true;
	}
	
	private boolean removeCustomBoard(String fid){
		List<BoardInfo> boards = getOptionBoard();


		for (BoardInfo b : boards) {
			if(b.getUrl().equals(fid)){
				
				boards.remove(b);
				break;
			}

		}

		//urlList.remove(index);
		//nameList.remove(index);
		
		synchronized (this) {
			setOptionBoard(boards);
			flushBoardInfo();

		}
		
		//refresh
		this.prepareGridData();
		return true;
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
			tv_now.setVisibility(View.GONE);
			tv_error.setVisibility(View.GONE);
			tv_pre.setVisibility(View.GONE);

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
				// MyApp app = ((MyApp) MainActivity.this);
				app.setRssFeed(rssFeed);

				HashMap<Object, RSSFeed> map = new HashMap<Object, RSSFeed>();
				if (false)
					map.put(StringUtil.getNowPageNum(rssFeed.getLink()),
							rssFeed);
				app.setMap(map);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, TopicListActivity1.class);
				startActivity(intent);

			} else {
				activityUtil.noticeError( "没有找到可用网络",MainActivity.this);
			}
			activityUtil.dismiss();
		}
	}

	int[] image = { R.drawable.p7, R.drawable.p354, /* R.drawable.tf, */
			R.drawable.p320, R.drawable.p181, R.drawable.p187, R.drawable.p185,
			R.drawable.p189, R.drawable.p182, R.drawable.p186, R.drawable.p184,
			R.drawable.p183, R.drawable.p188 };
	String[] urls = null;
	String[] names = null;
	
	String[] defaults_urls = { "7", "-7", /* "323", */"320", "181", "187", "185", "189",
			"182", "186", "184", "183", "188" };
	String[] defaults_names = { "议事厅", "大漩涡", /* "台服讨论区", */"黑锋要塞", "铁血沙场", "猎手大厅",
			"风暴祭坛", "暗影裂口", "魔法圣堂", "翡翠梦境", "圣光之力", "信仰神殿", "恶魔深渊" };

	class ImageGridList extends BaseAdapter {
		Activity activity;
		
		private Map<String,Drawable> iconMap;
		public ImageGridList(Activity a) {
			activity = a;
			iconMap = new HashMap<String,Drawable>();
		}

		public int getCount() {
			return names.length;
		}

		public Object getItem(int position) {
			return urls[position];
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
			holder.text.setText(names[position]);
			return convertView;
			//return iv;
		}


		private Drawable getDrable(View convertView, int position) {
			Drawable d = null;
			final String url = urls[position];
			if (position < image.length) {// default board
				d = getResources().getDrawable(image[position]);
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

					iconMap.put(urls[position], d);
				}
			}

			return d;
		}

	}

	private String text_urls[] = { "321", "-1068355","-447601" };
	private String text_names[] = { "DotA","晴风村","二次元国家地理 - NG2" };

	class TextListAdapter extends BaseAdapter {
		final Activity activity;

		public TextListAdapter(Activity activity) {
			super();
			this.activity = activity;
		}

		@Override
		public int getCount() {
			return text_urls.length;

		}

		@Override
		public Object getItem(int position) {
			if (position < text_urls.length)
				return text_urls[position];
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final TextView iv = new TextView(activity);
			iv.setText(text_names[position]);
			iv.setTextColor(android.graphics.Color.BLACK);
			Drawable draw = getResources().getDrawable(R.drawable.pdefault);
			iv.setCompoundDrawablesWithIntrinsicBounds(draw, null, null, null);
			if (error_level == 0) {
				String fidString = (String) getItem(position);
				iv.setOnClickListener(new EnterToplistLintener(position,
						fidString));

			}
			return iv;
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
				String tip = fidString + "不是合法的板块id";
				Toast.makeText(app, tip, Toast.LENGTH_LONG);
				return;
			}

			Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
					+ "&rss=1";

			if ( app.getUid() != null && app.getCid() != null) {

				url = url + "&ngaPassportUid=" + app.getUid()
						+ "&ngaPassportCid=" + app.getCid();
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
			// TODO Auto-generated method stub
			position = arg2;
			fidString=(String) arg0.getItemAtPosition(position);
			onClick(arg1);
			
		}
	}

}


