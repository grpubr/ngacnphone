package sp.phone.activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sp.phone.bean.Article;
import sp.phone.bean.ArticlePage;
import sp.phone.bean.PerferenceConstant;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

public class ArticleListActivity1 extends Activity 
	implements LoadStopable,OnTouchListener, PerferenceConstant {

	ActivityUtil activityUtil = ActivityUtil.getInstance();

	private TabHost tabHost;
	private ArticlePage articlePage;
	private HashMap<Object, ArticlePage> map_article;

	private MyApp app;
	
	final int QUOTE_ORDER = 0;
	final int REPLY_ORDER = 1;
	final int COPY_CLIPBOARD_ORDER = 2;
	final int SHOW_THISONLY_ORDER = 3;
	final int SHOW_MODIFY_ORDER = 4;
	private ArticleFlingListener flingListener;
	private static final String TABID_NEXT = "tab_next";
	private static final String TABID_PRE = "tab_prev";
	//private ScaleGestureDetector scaleDector;
	//private WebWidthChangeListener  webWidthChangeListener;
	private Object  webWidthChangeListener=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		ThemeManager.SetContextTheme(this);

		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);

		
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}
		setContentView(R.layout.tab2);
		//if(android.os.Build.VERSION.SDK_INT  >= android.os.Build.VERSION_CODES.FROYO)
		//webWidthChangeListener = new WebWidthChangeListener(this);
		try {
			Constructor<?> ScaleListenerContructor = Class.forName("sp.phone.activity.WebWidthChangeListener")
					.getConstructor(Context.class);
			//WebWidthChangeListener.class.getConstructor(parameterTypes)
				webWidthChangeListener = ScaleListenerContructor.newInstance(this);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initDate();
		initView();
		setListener();
	}

	//ZipFile zf;

	private void initDate() {
		app = ((MyApp) getApplication());
		articlePage = app.getArticlePage();
		map_article = app.getMap_article();
		//zf = app.getZf();
	}

	private void initView() {

		// overridePendingTransition(android.R.anim.fade_in,
		//		 android.R.anim.fade_out);
		 
		
		flingListener = new ArticleFlingListener(this);
		//TextView titleTV = (TextView) findViewById(R.id.title);
		if(articlePage == null){
			this.d("articlePage == null,abort create");
			this.finish();
			return;
		}
		
		final String title  = articlePage.getNow().get("title");
		this.setTitle(title);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
		HashMap<String, String> page = articlePage.getPage();
		if (page == null || page.size() == 0) {
			TabSpec spec = tabHost.newTabSpec("tab" + 1);
			TextView tv = new TextView(ArticleListActivity1.this);
			tv.setText("��ҳ");
			tv.setTextSize(20);
			
			final String url = HttpUtil.Server + articlePage.getNow().get("link");
			spec.setIndicator(tv);
			spec.setContent(new tabFactory());
			tabHost.addTab(spec);
			tabHost.getTabWidget().getChildAt(0).setOnClickListener(
					new OnClickListener(){

						@Override
						public void onClick(View v) {
							//Log.e(this.getClass().getCanonicalName(), "click?");
							new LoadArticleThread(url,ArticleListActivity1.this).start();
						}
						
						
					}
			);
		} else {
			// ��һҳ
			TabSpec ts_first = tabHost.newTabSpec("tab_first");
			TextView tv2 = new TextView(ArticleListActivity1.this);
			tv2.setBackgroundResource(R.drawable.page_first);
			ts_first.setIndicator(tv2);
			ts_first.setContent(new tabFactory());
			tabHost.addTab(ts_first);

			// �б�
			for (HashMap<String, String> hashMap : articlePage.getList()) {
				String num = hashMap.get("num");
				TabSpec spec = tabHost.newTabSpec("tab_" + num);
				TextView tv = new TextView(ArticleListActivity1.this);
				tv.setTextSize(20);
				String now = page.get("num");
				// ����ǰtab
				if (num.equals(now)) {
					tv.setText(num);
					tv.setTextColor(R.color.white);
					tv.setGravity(Gravity.CENTER);
					spec.setIndicator(tv);
					spec.setContent(new tabFactory());
					tabHost.addTab(spec);
					tabHost.setCurrentTabByTag("tab_" + num);
				} else {
					tv.setText(num);
					tv.setGravity(Gravity.CENTER);
					spec.setIndicator(tv);
					spec.setContent(new tabFactory2());
					tabHost.addTab(spec);
				}
			}

			String last = page.get("last");
			String current = page.get("current");
			if (!current.equals(last)) {
				// ��һҳ
				TextView tv3 = new TextView(ArticleListActivity1.this);
				tv3.setBackgroundResource(R.drawable.page_next);
				TabSpec ts_next = tabHost.newTabSpec("tab_next");
				ts_next.setIndicator(tv3);
				ts_next.setContent(new tabFactory2());
				tabHost.addTab(ts_next);
			}
			// ���һҳ
			TabSpec ts_last = tabHost.newTabSpec("tab_last");
			TextView tv4 = new TextView(ArticleListActivity1.this);
			tv4.setBackgroundResource(R.drawable.page_last);
			ts_last.setIndicator(tv4);
			ts_last.setContent(new tabFactory2());
			tabHost.addTab(ts_last);
		}
	}
	
	private void d(String log){
		Log.i(this.getClass().getSimpleName(), log);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.articlelist_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		/*ActionBar.DISPLAY_SHOW_HOME;//2
		flags |= ActionBar.DISPLAY_USE_LOGO;//1
		flags |= ActionBar.DISPLAY_SHOW_TITLE;//8
		flags |= ActionBar.DISPLAY_HOME_AS_UP;//4
		*/
		MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			lock.setTitle(R.string.unlock_orientation);
			lock.setIcon(android.R.drawable.ic_menu_always_landscape_portrait);
			
		}
		int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
		int i = 0;
		for(i = 0;i< menu.size();i++){
			ReflectionUtil.setShowAsAction(
					menu.getItem(i), actionNum);
		}
		ReflectionUtil.actionBar_setDisplayOption(this, flags);


		//final ActionBar bar = getActionBar();
		//bar.setDisplayOptions(flags);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			case R.id.article_menuitem_reply:
				String tid = getTid();
				Intent intent = new Intent();
				intent.putExtra("prefix", "" );
				intent.putExtra("tid", tid);
				intent.putExtra("action", "reply");
				
				intent.setClass(ArticleListActivity1.this, PostActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
				break;
			case R.id.article_menuitem_refresh:
				//final String url = HttpUtil.Server + articlePage.getPage().get("current");
				String url = HttpUtil.Server + articlePage.getNow().get("link");
				Map<String,String> pages = articlePage.getPage();
				String last = null;
				if(pages!= null){
					last=pages.get("current");
				}
				if(last!= null)
					url = HttpUtil.Server + last;
				new LoadArticleThread(url,this).start();
				break;
			case R.id.article_menuitem_addbookmark:
				String bookmarkUrl = articlePage.getNow().get("link");
				String title = articlePage.getNow().get("title");
				boolean ret = PhoneConfiguration.getInstance().addBookmark(bookmarkUrl, title);
				if(ret)
					Toast.makeText(this, "�ղسɹ�", Toast.LENGTH_LONG).show();
				else{
					Toast.makeText(this, "�����Ѿ����ղؼ�����", Toast.LENGTH_LONG).show();
					break;
				}
				
				SharedPreferences share = this.getSharedPreferences(PERFERENCE,
						MODE_PRIVATE);
				Editor editor = share.edit();
				String bookmarks = JSON.toJSONString(PhoneConfiguration.getInstance().getBookmarks());
				editor.putString(BOOKMARKS, bookmarks);
				editor.commit();
				break;
			case R.id.article_menuitem_lock:
				
				handleLockOrientation(item);
				break;
			case R.id.article_menuitem_back:
			default:
			//case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void handleLockOrientation(MenuItem item){
		int preOrentation = ThemeManager.getInstance().screenOrentation;
		int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
		
		if(preOrentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				preOrentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			//restore
			//int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
			ThemeManager.getInstance().screenOrentation = newOrientation;
			
			setRequestedOrientation(newOrientation);
			item.setTitle(R.string.lock_orientation);
			item.setIcon(android.R.drawable.ic_lock_idle_lock);

			
		}else{
			newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			Display dis = getWindowManager().getDefaultDisplay();
			if(dis.getWidth() < dis.getHeight()){
				newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			
			ThemeManager.getInstance().screenOrentation = newOrientation;
			setRequestedOrientation(newOrientation);			
			item.setTitle(R.string.unlock_orientation);
			item.setIcon(android.R.drawable.ic_menu_always_landscape_portrait);
		}
		
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(SCREEN_ORENTATION, newOrientation);
		editor.commit();
		
	}
	
	private String getTid(){
		String tid = null;
		tid = articlePage.getNow().get("link");
		tid = tid.substring(tid.indexOf("tid=")+4);
		int end = tid.indexOf("&");
		if(end == -1)
			end = tid.length();
		tid = tid.substring(0,end);
		return tid;
	}
	
	
	private String getPid(String url) {
		int start = url.indexOf("pid=");
		if(start == -1)
			return "";
		start +=4;
		int end = url.indexOf("&");
		if(end == -1)
			end = url.length();
		return url.substring(start,end);
	}

	
	

	@Override
	protected void onRestart() {
		String url = HttpUtil.Server + articlePage.getNow().get("link");
		Map<String,String> pages = articlePage.getPage();
		String last = null;
		if(pages!= null){
			last=pages.get("current");
		}
		if(last!= null)
			url = HttpUtil.Server + last;
		new LoadArticleThread(url,this).start();
		super.onRestart();
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ListView currentView = (ListView) tabHost.getCurrentView();
		ArticleListAdapter currentAdapter = (ArticleListAdapter) currentView.getAdapter();
		StringBuffer postPrefix = new StringBuffer();
		String tid = getTid();

		final HashMap<String, String> map = 
				currentAdapter.getItem(info.position);
		String content = map.get("content");
		final String name = map.get("nickName");
		String mention=null;
		switch(item.getItemId())
		//if( REPLY_POST_ORDER ==item.getItemId())
		{
		case QUOTE_ORDER:

			final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
			content = content.replaceAll(quote_regex, "");
			final String postTime = map.get("postTime");
			// final String url = map.get("url");
			boolean endWithUrl = false;
			if(content.endsWith("[/url]"))
				endWithUrl = true;
			if (content.length() > 100) {
				content = content.substring(0, 99) + ".......";
				if(endWithUrl)
					content += "[/url]";
					
			}
			mention = name;
			postPrefix.append("[quote][tid=");
			postPrefix.append(tid);
			postPrefix.append("]Topic[/pid] [b]Post by ");
			postPrefix.append(name);
			postPrefix.append(" (");
			postPrefix.append(postTime);
			postPrefix.append("):[/b]\n");
			postPrefix.append(content);
			postPrefix.append("[/quote]");

			postPrefix.append("\n[@");
			postPrefix.append(name);
			postPrefix.append("]\n");
		case REPLY_ORDER:	
			Intent intent = new Intent();
			if(!StringUtil.isEmpty(mention))
				intent.putExtra("mention", mention);
			intent.putExtra("prefix", StringUtil.removeBrTag(postPrefix.toString()) );
			intent.putExtra("tid", tid);
			intent.putExtra("action", "reply");	
			intent.setClass(ArticleListActivity1.this, PostActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
			break;
		case SHOW_MODIFY_ORDER :
			Intent intentModify = new Intent();
			intentModify.putExtra("prefix", StringUtil.removeBrTag(content) );
			intentModify.putExtra("tid", tid);
			String pid = getPid(map.get("url"));
			intentModify.putExtra("pid", pid);
			intentModify.putExtra("title", map.get("title"));
			intentModify.putExtra("action", "modify");	
			intentModify.setClass(ArticleListActivity1.this, PostActivity.class);
			startActivity(intentModify);
			overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
			break;
		case COPY_CLIPBOARD_ORDER:	
			//if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB )
			//{
				android.text.ClipboardManager  cbm = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cbm.setText(StringUtil.removeBrTag(content));
			//}else{
				//android.content.ClipboardManager  cbm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				//cbm.setPrimaryClip(ClipData.newPlainText("content", content));
			//}
				
			Toast.makeText(this.getApplication(), "�Ѿ����Ƶ����а�", Toast.LENGTH_SHORT).show();
			break;
		case SHOW_THISONLY_ORDER:
			final String authorId = map.get("userId");
			final String tempUrl = HttpUtil.Server +"/read.php?tid="
					+ tid + "&authorid=" + authorId;
			new LoadArticleThread(tempUrl,this).start();
			break;

			
			
		}


		
		return true;
	}


	


	@Override
	public void stopLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(webWidthChangeListener !=null){
			try {
				Method onTouchMethod = webWidthChangeListener.getClass()
						.getMethod("onTouch", event.getClass());
				onTouchMethod.invoke(webWidthChangeListener, event);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return flingListener.getDetector().onTouchEvent(event);
	}

	private void setListener() {
		if(tabHost!= null)
			tabHost.setOnTabChangedListener(changeListener);
	}

	OnTabChangeListener changeListener = new ArticlePageChangeListener();
	

	class ArticlePageChangeListener implements OnTabChangeListener,LoadStopable{
		boolean isloading = false;
		
		public void onTabChanged(String tabId) {

			//soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);
			synchronized(this){
				if(isloading)
					return;
				isloading = true;
			}

			// ���¼��� ����
			HashMap<String, String> page = articlePage.getPage();
			String host = HttpUtil.Server;
			String urls = null;
			String url_last=null;
			if (page != null) {
				url_last = host + page.get("last");
				if (tabId.equals("tab_first")) {
					urls = page.get("first");
				} else if (tabId.equals("tab_next")) {
					urls = page.get("next");
				} else if (tabId.equals("tab_last")) {
					urls = page.get("last");
				} else if (tabId.equals("tab_prev")) {// virtual tab
					urls = page.get("prev");
				} else {
					String num = tabId.split("_")[1];
					for (HashMap<String, String> hashMap : articlePage
							.getList()) {
						if (num.equals(hashMap.get("num"))) {
							urls = hashMap.get("link");
							break;
						}
					}
				}

				if (urls == null){
					if(tabId.equals("tab_prev")){
							activityUtil.dismiss();
							ArticleListActivity1.this.finish();
							return;
					}
							
					urls = page.get("current");
				}
				
			} else {
				if(tabId.equals("tab_prev")){
					activityUtil.dismiss();
					ArticleListActivity1.this.finish();
					return;
				}
						
				urls = articlePage.getNow().get("link");
			}
			
			if (urls.indexOf("\n") != -1) {
				urls = urls.substring(0, urls.length() - 1);
			}
			
			urls = host + urls;
			final String s = urls;

			ArticlePage ap2 = map_article.get(s);
			if (url_last != null &&ap2 != null && !url_last.trim().equals(urls) ) {
				articlePage = ap2;
				this.stopLoading();
				System.gc();
				reBuild();
			} else {
				new LoadArticleThread(s,this).start();

			}

		}

		@Override
		public void stopLoading() {
			synchronized(this){
				this.isloading = false;
			}
			
		}
	}

	class LoadArticleThread extends Thread {
		final LoadStopable stopable;
		private final String url;
		public LoadArticleThread(String url,LoadStopable stopable) {
			super();
			this.url = url;
			this.stopable = stopable;
		}
		@Override
		public void run() {
			ArticlePage ap = null;
			if (!HttpUtil.HOST_PORT.equals("")) {
				activityUtil.noticeSaying(ArticleListActivity1.this);
				String a = url.replace("&", "@");
				ap = HttpUtil.getArticlePageByJson(HttpUtil.HOST
						+ "?uri=" + a);

			}
			if (ap == null) {
				/*String str = StringUtil.getSaying();
				if (str.indexOf(";") != -1) {
					activityUtil.notice("��ͨģʽ", str.split(";")[0]
							+ "-----" + str.split(";")[1]);
				} else {
					activityUtil.notice("��ͨģʽ", str);
				}*/
				activityUtil.noticeSaying(ArticleListActivity1.this);
				// activityUtil.notice("INFO",
				// "���Ӳ���:P-N,��ģ���������ʾ��ʽ");
				
				String cookie = PhoneConfiguration.getInstance().getCookie();
				ap = HttpUtil.getArticlePage(url,cookie);
			}
			if (ap != null) {
				app.setArticlePage(ap);// ���õ�ǰpage
				map_article.put(url, ap);// ����µ�����
				app.setMap_article(map_article);
				articlePage = ap;
				Message message = new Message();
				handler_rebuild.sendMessage(message);
			} else {
				activityUtil.noticeError("����������һ�����������Ӳ�����",ArticleListActivity1.this);
			}
			stopable.stopLoading();
			activityUtil.dismiss();

		}
	}
	
	Handler handler_rebuild = new Handler() {
		public void handleMessage(final Message msg) {
			System.gc();
			reBuild();
			activityUtil.dismiss();
		};
	};

	private void reBuild() {
		tabHost.setOnTabChangedListener(null);
		tabHost.setCurrentTab(0);
		tabHost.clearAllTabs();
		initView();
		setListener();
	}

	class tabFactory2 implements TabContentFactory {
		public View createTabContent(String tag) {
			TextView view = new TextView(ArticleListActivity1.this);
			return view;
		}
	}

	class tabFactory implements TabContentFactory {

		public View createTabContent(String tag) {
			if (!"tab_f".equals(tag)) {
				ListView listView = new ListView(ArticleListActivity1.this);
				mData = getData(tag);
				ArticleListAdapter adapter = new ArticleListAdapter(
						ArticleListActivity1.this,flingListener,
						mData, listView);
				listView.setAdapter(adapter);
				if(webWidthChangeListener!=null){
					try{
					Method setAapterMethod = webWidthChangeListener.getClass()
							.getMethod("setAdapter", BaseAdapter.class);
					setAapterMethod.invoke(webWidthChangeListener, adapter);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				

				// listView.setBackgroundResource(R.drawable.bodybg);
				listView.setCacheColorHint(0);

				listView.setVerticalScrollBarEnabled(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				
				listView.setOnCreateContextMenuListener(new FloorCreateContextMenuListener());
				listView.setOnTouchListener(ArticleListActivity1.this);
				listView.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						((ListView) parent).setItemChecked(position, true);
					}
					
				});
				return listView;
			} else {
				return new TextView(ArticleListActivity1.this);
			}
		}

		private List<HashMap<String, String>> mData;

		private List<HashMap<String, String>> getData(String tag) {
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			for (Article article : articlePage.getListArticle()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("nickName", article.getUser().getNickName());
				map.put("title", article.getTitle());
				map.put("userId", article.getUser().getUserId() + "");
				map.put("content", article.getContent());
				map.put("avatarImage", article.getUser().getAvatarImage());
				map.put("url", article.getUrl());
				map.put("floor", article.getFloor() + "");
				map.put("postTime", article.getLastTime());
				list.add(map);
			}
			return list;
		}
		
		class FloorCreateContextMenuListener implements OnCreateContextMenuListener{

			@Override
			public void onCreateContextMenu(ContextMenu arg0, View arg1,
					ContextMenuInfo arg2) {

				arg0.add(0,QUOTE_ORDER,0, "��֮");
				arg0.add(0,REPLY_ORDER,0, "����");
				arg0.add(0,COPY_CLIPBOARD_ORDER,0, "���Ƶ����а�");
				arg0.add(0,SHOW_THISONLY_ORDER,0, "ֻ������");
				arg0.add(0,SHOW_MODIFY_ORDER,0, "�༭");

				
				
				
				
			}
			
		}
		
	}

	class ArticleFlingListener extends SimpleOnGestureListener implements
			OnTouchListener {
		Context context;
		GestureDetector gDetector;
		final float FLING_MIN_DISTANCE = 80;
		final float FLING_MIN_VELOCITY = 5000;
		ArticlePageChangeListener changeListener = new ArticlePageChangeListener();

		/*
		 * public ArticleFlingListener() { super(); }
		 */

		public ArticleFlingListener(Context context) {
			this(context, null);
		}

		public ArticleFlingListener(Context context, GestureDetector gDetector) {

			if (gDetector == null)
				gDetector = new GestureDetector(context, this);

			this.context = context;
			this.gDetector = gDetector;
		}
		

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if(e1 == null || e2 == null)
				return false;
			

			float deltaX = Math.abs(e1.getX() - e2.getX());
			float deltaY = Math.abs(e1.getY() - e2.getY()); //distanceX is not equal to e1x-e2x
			if(deltaX<FLING_MIN_DISTANCE*2
				||deltaX < 1.73*deltaY
				|| (Math.abs(distanceY) < 1.73* Math.abs(distanceY))
				)
			{
				return false;
			}
			
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE*1.5 )
			{
				// left
				Log.d("test","onScroll will call change to next");
				Log.d("test","1x="+e1.getX()+",e2x="+e2.getX()+",e1y="+e1.getY()
						+ ",e2y="+e2.getY()+ ",dx="+distanceX+",dy="+distanceY);
				
				changeListener.onTabChanged(TABID_NEXT);
				 return true;
			}

			if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE*1.5)
				{
				// right
				Log.d("test","onScroll will call change to pre");
				Log.d("test","1x="+e1.getX()+",e2x="+e2.getX()+",e1y="+e1.getY()
						+ ",e2y="+e2.getY()+ ",dx="+distanceX+",dy="+distanceY);
				changeListener.onTabChanged(TABID_PRE);
				 return true;
			}
			return  false;
			//return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			
			if(e1 == null || e2 == null)
				return false;
			//if(e2.getAction() != MotionEvent.ACTION_UP)
			//	return false;
			
			float deltaX = Math.abs(e1.getX() - e2.getX());
			float deltaY = Math.abs(e1.getY() - e2.getY());
		

			if ( (e1.getX() - e2.getX() > FLING_MIN_DISTANCE)
					&& (deltaX > 1.73*deltaY)
					&& (Math.abs(velocityX) > 3*Math.abs(velocityY)
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY)
				){
				// left
				Log.d("test","onFling will call change to next");
				Log.d("test","1x="+e1.getX()+",e2x="+e2.getX()+",e1y="+e1.getY()
						+ ",e2y="+e2.getY()+ ",vx="+velocityX+",vy="+velocityY);
				
				changeListener.onTabChanged(TABID_NEXT);
				 return true;
			}
			
			if ( (e2.getX() - e1.getX() > FLING_MIN_DISTANCE)
					&& (deltaX > 1.73*deltaY)
					&& (Math.abs(velocityX) > 3*Math.abs(velocityY)
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY)
				) {
				if(e1.getAction() == MotionEvent.ACTION_UP)
					Log.d("test","onFling action = " + e2.getAction());
				// right
				Log.d("test","onFling will call change to pre");
				Log.d("test","1x="+e1.getX()+",e2x="+e2.getX()+",e1y="+e1.getY()
						+ ",e2y="+e2.getY()+ ",vx="+velocityX+",vy="+velocityY);
				Log.d("test", "deltaX="+ deltaX + "deltaY="+ deltaY);
				changeListener.onTabChanged(TABID_PRE);
				 return true;
			}
			return  false;// super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;

		}

		public boolean onTouch(View v, MotionEvent event) {

			// Within the MyGestureListener class you can now manage the
			// event.getAction() codes.

			// Note that we are now calling the gesture Detectors onTouchEvent.
			// And given we've set this class as the GestureDetectors listener
			// the onFling, onSingleTap etc methods will be executed.
			boolean ret = true;
			try{
				ret = gDetector.onTouchEvent(event);
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
			}
			
			return ret;

		}

		public GestureDetector getDetector() {
			return gDetector;
		}
	}




}

class WebWidthChangeListener extends SimpleOnScaleGestureListener{
	private ScaleGestureDetector scaleDector;
	private BaseAdapter adapter=null;
	
	public WebWidthChangeListener(Context context){
		scaleDector = new ScaleGestureDetector(context,this);
	}
	
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}
	
	


	public ScaleGestureDetector getScaleDector() {
		return scaleDector;
	}

	public boolean onTouch(MotionEvent event) {
		return getScaleDector().onTouchEvent(event);
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		int tochange = (int) ((detector.getCurrentSpan()
			- detector.getPreviousSpan())/50);
		final int nextWidth = PhoneConfiguration.getInstance().nikeWidth + tochange;
		if( nextWidth>5 && nextWidth <300)
			PhoneConfiguration.getInstance().nikeWidth = nextWidth ;
				;
		//Log.i("test","current width"+PhoneConfiguration.getInstance().nikeWidth);
		if(adapter !=null){
			//Log.d("test","call notify");
			adapter.notifyDataSetChanged();
		}
		return super.onScale(detector);
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		super.onScaleEnd(detector);
	}
	
	
}
interface LoadStopable{
	void stopLoading();
	
}