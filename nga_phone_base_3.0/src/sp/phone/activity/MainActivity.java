package sp.phone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
	final static int version = 112;
	TextView tv_pre;
	TextView tv_now;
	TextView tv_error;
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	private MyApp app;
	View view;

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
		//firstRun = share.getBoolean("firstRun", true);
		int version_in_config = share.getInt("version", 0);
		if(version_in_config < version){
			Editor editor = share.edit();
			editor.putInt("version", version);
			editor.putBoolean("refreshAfterPost", true);
			this.boardInfo = this.resetBoard();
			String infoString = JSON.toJSONString(boardInfo);
			editor.putString("boards", infoString);
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
		// titleTV.setText("Դ��һ���򵥵��뷨");
		setTitle("Դ��һ���򵥵��뷨");
		
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
		
		/*if(firstRun){
			new AlertDialog.Builder(this).setTitle("��ʾ")
			.setMessage(StringUtil.getTips())
			.setPositiveButton("֪����", null).show();
			
			firstRun = false;
			
		}*/
		

	}
	
	
	
	//AdapterContextMenuInfo lastMenuInfo = null;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//GridView gv; gv.getChildAt(1);
		//TextView clicked = (TextView)((AdapterContextMenuInfo)menuInfo).targetView;
		menu.add(0,ADD_BOARD_INDEX,0, "���°��");
		menu.add(0,ADD_BOARD_INDEX+ 1,0, "ɾ�����"/*+clicked.getText().toString()*/);
		menu.add(0,ADD_BOARD_INDEX+ 2,0, "�������а��");
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

				String fid = this.boardInfo.get(position).getUrl();
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
        alert.setTitle("��������  ���id");
        alert.setMessage("����: ������(�ո�)7");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                String value = input.getText().toString().trim(); 
                final  String values[] = value.split(" ");
                
                if(values.length <2){
                	Toast.makeText(MainActivity.this, "����Ƿ�",  
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

					System.out.println("zip is");

					File file = new File(HttpUtil.PATH);
					if (!file.exists()) {
						delay("�����µĻ���Ŀ¼");
						file.mkdirs();
					} else {
						delay("����Ŀ¼����");
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
	private List<BoardInfo> resetBoard(){
		
		List<BoardInfo> boards = new ArrayList<BoardInfo>();
		boards.add(new BoardInfo("7", "������˹������", R.drawable.p7));
		boards.add(new BoardInfo("323", "̨��������", R.drawable.p323));
		boards.add(new BoardInfo("-7", "������", R.drawable.p354));
		boards.add(new BoardInfo("10", "��ɫ����������", R.drawable.p10));
		boards.add(new BoardInfo("230", "������˹���ίԱ��", R.drawable.p230));
		boards.add(new BoardInfo("387", "�˴�����֮����", R.drawable.p387));
		
		boards.add(new BoardInfo("320", "�ڷ�Ҫ��", R.drawable.p320));
		boards.add(new BoardInfo("181", "��Ѫɳ��", R.drawable.p181));
		boards.add(new BoardInfo("182", "ħ��ʥ��", R.drawable.p182));
		boards.add(new BoardInfo("183", "�������", R.drawable.p183));
		boards.add(new BoardInfo("185", "�籩��̳", R.drawable.p185));
		boards.add(new BoardInfo("186", "����ξ�", R.drawable.p186));
		boards.add(new BoardInfo("187", "���ִ���", R.drawable.p187));
		boards.add(new BoardInfo("184", "ʥ��֮��", R.drawable.p184));
		boards.add(new BoardInfo("188", "��ħ��Ԩ", R.drawable.p188));
		boards.add(new BoardInfo("189", "��Ӱ�ѿ�", R.drawable.p189));
		
		boards.add(new BoardInfo("310", "ǰհ��Ѷ", R.drawable.p310));
		boards.add(new BoardInfo("190", "��������", R.drawable.p190));
		boards.add(new BoardInfo("213", "ս������", R.drawable.p213));
		boards.add(new BoardInfo("218", "����ר��", R.drawable.p218));
		boards.add(new BoardInfo("258", "ս������", R.drawable.p258));
		boards.add(new BoardInfo("272", "������", R.drawable.p272));
		boards.add(new BoardInfo("191", "�ؾ��̻�", R.drawable.p191));
		boards.add(new BoardInfo("200", "����о�", R.drawable.p200));
		boards.add(new BoardInfo("240", "BigFoot", R.drawable.p240));
		boards.add(new BoardInfo("274", "�������", R.drawable.p274));
		boards.add(new BoardInfo("315", "ս��ͳ��", R.drawable.p315));
		boards.add(new BoardInfo("333", "DKPϵͳ", R.drawable.p333));
		boards.add(new BoardInfo("327", "�ɾ�����", R.drawable.p327));
		boards.add(new BoardInfo("388", "�û�����", R.drawable.p388));
		boards.add(new BoardInfo("255", "�������", R.drawable.p10));
		boards.add(new BoardInfo("306", "��Ա��ļ", R.drawable.p10));
		
		boards.add(new BoardInfo("264", "�����޾�Ժ", R.drawable.p264));
		boards.add(new BoardInfo("8", "��ͼ���", R.drawable.p8));
		boards.add(new BoardInfo("102", "����Э��", R.drawable.p102));
		boards.add(new BoardInfo("124", "�ڻ�����", R.drawable.pdefault));
		boards.add(new BoardInfo("254", "���õ��", R.drawable.p254));
		boards.add(new BoardInfo("355", "�����ֵܻ�", R.drawable.p355));
		boards.add(new BoardInfo("116", "�漣֮Ȫ", R.drawable.p116));
		
		
		boards.add(new BoardInfo("173", "�ʺŰ�ȫ", R.drawable.p193));
		boards.add(new BoardInfo("201", "ϵͳ����", R.drawable.p201));
		boards.add(new BoardInfo("334", "Ӳ������", R.drawable.p334));
		boards.add(new BoardInfo("335", "��վ����", R.drawable.p335));
		
		
		boards.add(new BoardInfo("318", "Diablo III", R.drawable.p318));
		boards.add(new BoardInfo("332", "ս��40K", R.drawable.p332));
		boards.add(new BoardInfo("321", "DotA", R.drawable.p321));
		boards.add(new BoardInfo("353", "Ŧ��˹Ӣ�۴�", R.drawable.pdefault));
		
		boards.add(new BoardInfo("-1068355", "����", R.drawable.pdefault));
		boards.add(new BoardInfo("-447601", " ����Ԫ���ҵ��� - NG2", R.drawable.pdefault));
		boards.add(new BoardInfo("-152678", "Ӣ������ Let's Gank", R.drawable.pdefault));
		boards.add(new BoardInfo("-343809", "��į�ĳ����ֲ�", R.drawable.pdefault));
		boards.add(new BoardInfo("-131429", "���ݡ���С˵��", R.drawable.pdefault));
		boards.add(new BoardInfo("-46468", " ����˹��̹������", R.drawable.pdefault));
		boards.add(new BoardInfo("-2371813", "NGAפ�������İ칫��", R.drawable.pdefault));
		boards.add(new BoardInfo("-124119", "���ܷ��ۡ���ʥ������ ", R.drawable.pdefault));
		boards.add(new BoardInfo("-84", " ģ��֮��", R.drawable.pdefault));
		boards.add(new BoardInfo("-187579", " ��������ʷ�����", R.drawable.pdefault));
		boards.add(new BoardInfo("-308670", "Ѫ��ĸ��˿ռ�", R.drawable.pdefault));
		boards.add(new BoardInfo("-112905", "��ʥ��", R.drawable.pdefault));
		
		
		return boards;
	}

	private void prepareGridData() {
		List<BoardInfo> boards = getOptionBoard();


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
				activityUtil.noticeError( "û���ҵ���������",MainActivity.this);
			}
			activityUtil.dismiss();
		}
	}

	/*int[] image = { R.drawable.p7, R.drawable.p354, 
			R.drawable.p320, R.drawable.p181, R.drawable.p187, R.drawable.p185,
			R.drawable.p189, R.drawable.p182, R.drawable.p186, R.drawable.p184,
			R.drawable.p183, R.drawable.p188 };
	String[] urls = null;
	String[] names = null;
	
	String[] defaults_urls ={ "7", "-7", "320", "181", "187", "185", "189",
			"182", "186", "184", "183", "188" };
	String[] defaults_names = { "������", "������", "�ڷ�Ҫ��", "��Ѫɳ��", "���ִ���",
			"�籩��̳", "��Ӱ�ѿ�", "ħ��ʥ��", "����ξ�", "ʥ��֮��", "�������", "��ħ��Ԩ" };
*/
	class ImageGridList extends BaseAdapter {
		Activity activity;
		
		private Map<String,Drawable> iconMap;
		public ImageGridList(Activity a) {
			activity = a;
			iconMap = new HashMap<String,Drawable>();
		}

		public int getCount() {
			return boardInfo.size();
		}

		public Object getItem(int position) {
			return boardInfo.get(position).getUrl();//urls[position];
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
			holder.text.setText(boardInfo.get(position).getName());
			return convertView;
			//return iv;
		}


		private Drawable getDrable(View convertView, int position) {
			Drawable d = null;
			final String url = boardInfo.get(position).getUrl();
			int resId = boardInfo.get(position).getIcon();
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

	private String text_urls[] = { "321", "-1068355","-447601" };
	private String text_names[] = { "DotA","����","����Ԫ���ҵ��� - NG2" };

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
				String tip = fidString + "���ǺϷ��İ��id";
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
			// TODO Auto-generated method stub
			position = arg2;
			fidString=(String) arg0.getItemAtPosition(position);
			onClick(arg1);
			
		}
	}

}


