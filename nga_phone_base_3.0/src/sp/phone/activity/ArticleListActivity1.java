package sp.phone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

import com.alibaba.fastjson.JSON;

import sp.phone.activity.TopicListActivity1.BoardPageNumChangeListener;
import sp.phone.bean.Article;
import sp.phone.bean.ArticlePage;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class ArticleListActivity1 extends Activity {

	ActivityUtil activityUtil = ActivityUtil.getInstance();

	private TabHost tabHost;
	private ArticlePage articlePage;
	private HashMap<Object, ArticlePage> map_article;

	private MyApp app;
	final int REPLY_POST_ORDER = 0;
	private ArticleFlingListener flingListener;
	private static final String TABID_NEXT = "tab_next";
	private static final String TABID_PRE = "tab_prev";
	@Override
	public void onCreate(Bundle savedInstanceState) {

		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		//		R.layout.title_bar);

		initDate();
		initView();
		setListener();
	}

	ZipFile zf;

	private void initDate() {
		app = ((MyApp) getApplication());
		articlePage = app.getArticlePage();
		map_article = app.getMap_article();
		zf = app.getZf();
	}

	//SoundPool soundPool = null;
	//private int hitOkSfx;

	private void initView() {

		//soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// ������Ƶ��
		//hitOkSfx = soundPool.load(this, R.raw.shake, 0);
		flingListener = new ArticleFlingListener(this);
		//TextView titleTV = (TextView) findViewById(R.id.title);
		//titleTV.setText(articlePage.getNow().get("title"));
		this.setTitle(articlePage.getNow().get("title"));
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
							new LoadArticleThread(url).start();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.articlelist_menu, menu);
		int flags = ActionBar.DISPLAY_SHOW_HOME;
		flags |= ActionBar.DISPLAY_USE_LOGO;
		flags |= ActionBar.DISPLAY_SHOW_TITLE;
		flags |= ActionBar.DISPLAY_HOME_AS_UP;
		flags |= ActionBar.DISPLAY_SHOW_CUSTOM;
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
				break;
			case R.id.article_menuitem_refresh:
				final String url = HttpUtil.Server + articlePage.getNow().get("link");
				new LoadArticleThread(url).start();
				break;
			case R.id.article_menuitem_addbookmark:
				String bookmarkUrl = articlePage.getNow().get("link");
				String title = articlePage.getNow().get("title");
				boolean ret = PhoneConfiguration.getInstance().addBookmark(bookmarkUrl, title);
				if(ret)
					Toast.makeText(this, "�ղسɹ�", Toast.LENGTH_LONG);
				else{
					Toast.makeText(this, "�����Ѿ����ղؼ�����", Toast.LENGTH_LONG);
					break;
				}
				
				SharedPreferences share = this.getSharedPreferences("perference",
						MODE_PRIVATE);
				Editor editor = share.edit();
				String bookmarks = JSON.toJSONString(PhoneConfiguration.getInstance().getBookmarks());
				editor.putString("bookmarks", bookmarks);
				editor.commit();
				break;
			case R.id.article_menuitem_back:
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
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
	
	
	

	
	

	@Override
	protected void onRestart() {
		final String url = HttpUtil.Server + articlePage.getNow().get("link");
		new LoadArticleThread(url).start();
		super.onRestart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ListView currentView = (ListView) tabHost.getCurrentView();
		ArticleListAdapter currentAdapter = (ArticleListAdapter) currentView.getAdapter();
		StringBuffer postPrefix = new StringBuffer();
		String tid = getTid();
		if( REPLY_POST_ORDER ==item.getItemId())
		{

			HashMap<String, String> map = currentAdapter.getItem(info.position);
			
			final String name = map.get("nickName");
			final String postTime = map.get("postTime");
			final String url = map.get("url");
			

			if(url.indexOf("pid=") != -1 )
			{
				String pid = url.substring(url.indexOf("pid=")+4);
				if(pid.indexOf("&") != -1)
					pid = pid.substring(0,pid.indexOf("&"));
				
				
				postPrefix.append("[b]Reply to [pid=");
				postPrefix.append(pid);
				postPrefix.append("]Reply[/pid] Post by ");
				postPrefix.append(name);
				postPrefix.append(" (");
				postPrefix.append(postTime);
				postPrefix.append(")[/b]\n");
			}else if(url.indexOf("tid=")!= -1){
				
				postPrefix.append("[quote][tid=");
				postPrefix.append(tid);
				postPrefix.append("]Topic[/pid] [b]Post by ");
				postPrefix.append(name);
				postPrefix.append(" (");
				postPrefix.append(postTime);
				postPrefix.append("):[/b]\n");
				postPrefix.append(map.get("content"));
				postPrefix.append("[/quote]");
				
				
			}
			postPrefix.append("\n[@");
			postPrefix.append(name);
			postPrefix.append("]\n");
		}
		Intent intent = new Intent();
		intent.putExtra("prefix", StringUtil.removeHtmlTag(postPrefix.toString()) );
		intent.putExtra("tid", tid);
		intent.putExtra("action", "reply");
		
		intent.setClass(ArticleListActivity1.this, PostActivity.class);
		startActivity(intent);
		
		return true;
	}

	
	


	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return flingListener.getDetector().onTouchEvent(event);
	}

	private void setListener() {
		tabHost.setOnTabChangedListener(changeListener);
	}

	OnTabChangeListener changeListener = new ArticlePageChangeListener();
	
	class ArticlePageChangeListener implements OnTabChangeListener{
		public void onTabChanged(String tabId) {

			//soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);

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
				// Message message = new Message();
				// handler_rebuild.sendMessage(message);
				System.gc();
				reBuild();
			} else {
				new LoadArticleThread(s).start();

			}

		}
	}

	class LoadArticleThread extends Thread {
		private final String url;
		public LoadArticleThread(String url) {
			super();
			this.url = url;
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
				
				String cookie = "ngaPassportUid=" + ArticleListActivity1.this.app.getUid()
					+"; ngaPassportCid=" + ArticleListActivity1.this.app.getCid();
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
				activityUtil.noticeError("����������һ�����������ӱ�ɾ��",ArticleListActivity1.this);
			}
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
						mData, listView, zf);
				listView.setAdapter(adapter);
				// listView.setBackgroundResource(R.drawable.bodybg);
				listView.setCacheColorHint(0);
				app.getResources().getColor(R.color.shit1);
				listView.setVerticalScrollBarEnabled(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				
				listView.setOnCreateContextMenuListener(new FloorCreateContextMenuListener());
				listView.setOnTouchListener(flingListener);
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

				arg0.add(0,REPLY_POST_ORDER,0, "��֮");
				arg0.add(0,REPLY_POST_ORDER+ 1,0, "����");
				
				
				
				
			}
			
		}
		
	}

	class ArticleFlingListener extends SimpleOnGestureListener implements
			OnTouchListener {
		Context context;
		GestureDetector gDetector;
		final float FLING_MIN_DISTANCE = 80;

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
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1 == null || e2 == null)
				return false;
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > 1.73*Math.abs(velocityY)) {
				// left

				 new ArticlePageChangeListener().onTabChanged(TABID_NEXT);
				 return true;
			}

			if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > 1.73*Math.abs(velocityY)) {
				// right
				 new ArticlePageChangeListener().onTabChanged(TABID_PRE);
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
