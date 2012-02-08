package sp.phone.activity;

import java.util.HashMap;

import sp.phone.bean.ArticlePage;
import sp.phone.bean.RSSFeed;
import sp.phone.bean.RSSItem;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.RSSUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import sp.phone.activity.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class TopicListActivity1 extends Activity {

	ActivityUtil activityUtil = new ActivityUtil(this);

	static TabHost tabHost;
	private static final String TABID_PRE = "tab_f";
	private static final String TABID_NEXT = "tab_n";
	private RSSFeed rssFeed;
	private int page;
	private HashMap<Object, RSSFeed> map;
	private int max_num = 5;
	private MyApp app;
	private HashMap<Object, ArticlePage> map_article;
	private TopicFlingListener flingListener;
	protected ListView currentListview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);

		initDate();
		initView();
		setListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.threadlist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			case R.id.threadlist_menu_newthread :
				handlePostThread(item);
				break;
			case R.id.threadlist_menu_item2 :
				new BoardPageNumChangeListener().onTabChanged(
						tabHost.getCurrentTabTag());
				break;
			case R.id.threadlist_menu_item3 :
				this.finish();
				break;
		}
		return true;
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return	flingListener.getDetector().onTouchEvent(event);
	}

	private boolean handlePostThread(MenuItem item){
		final String fid_start_tag = "fid=";
		final String fid_end_tag = "&";
		String fid = rssFeed.getLink();
		int start = fid.indexOf(fid_start_tag);
		if(start == -1)
		{
			Toast.makeText(this, R.string.error,
					Toast.LENGTH_LONG).show();
			return false;
		}
		start += fid_start_tag.length();
		int end= fid.indexOf(fid_end_tag, start) ;
		if(end ==-1)
			end = fid.length();
		fid = fid.substring(start,end);
		
		
		Intent intent = new Intent();
		//intent.putExtra("prefix",postPrefix.toString());
		intent.putExtra("fid", fid);
		intent.putExtra("action", "new");
		
		intent.setClass(this, PostActivity.class);
		startActivity(intent);
		return true;
	}

	private void initDate() {
		app = ((MyApp) getApplication());
		rssFeed = app.getRssFeed();
		map = app.getMap();
		map_article = app.getMap_article();
		flingListener = new TopicFlingListener(this);

	}

	//SoundPool soundPool = null;
	//private int hitOkSfx;

	private void initView() {

		//soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// 载入音频流
		//hitOkSfx = soundPool.load(this, R.raw.tweet, 0);

		setTitle(rssFeed.getTitle());
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setBackgroundResource(
			ThemeManager.getInstance().getBackgroundColor());
		// 首页
		TabSpec ts_first = tabHost.newTabSpec(TABID_PRE);
		TextView tv2 = new TextView(TopicListActivity1.this);
		tv2.setBackgroundResource(R.drawable.page_first);
		ts_first.setIndicator(tv2);
		ts_first.setContent(new tabFactory2());
		tabHost.addTab(ts_first);
		// 页码数
		page = StringUtil.getNowPageNum(rssFeed.getLink());
		for (int i = max_num - 4; i <= max_num; i++) {
			if (i == page) {
				TabSpec spec = tabHost.newTabSpec("tab_" + i);
				TextView tv = new TextView(TopicListActivity1.this);
				tv.setText(i + "");
				tv.setGravity(Gravity.CENTER);
				tv.setTextColor(R.color.white);
				tv.setTextSize(20);
				spec.setIndicator(tv);
				spec.setContent(new tabFactory());
				tabHost.addTab(spec);
				tabHost.setCurrentTabByTag("tab_" + i);
			} else {
				TabSpec spec = tabHost.newTabSpec("tab_" + i);
				TextView tv = new TextView(TopicListActivity1.this);
				tv.setText(i + "");
				tv.setTextSize(20);
				tv.setGravity(Gravity.CENTER);
				spec.setIndicator(tv);
				spec.setContent(new tabFactory2());
				tabHost.addTab(spec);
			}
		}

		TabSpec ts_next = tabHost.newTabSpec(TABID_NEXT);
		TextView tv3 = new TextView(TopicListActivity1.this);
		tv3.setBackgroundResource(R.drawable.page_next);
		ts_next.setIndicator(tv3);
		ts_next.setContent(new tabFactory2());
		tabHost.addTab(ts_next);

	}

	private void setListener() {
		tabHost.setOnTabChangedListener(new BoardPageNumChangeListener());
	}

	class BoardPageNumChangeListener implements OnTabChangeListener{
		public void onTabChanged(final String tabId) {

			//soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);

			String link = rssFeed.getLink();
			final int num;
			if (tabId.equals(TABID_PRE)) {
				if(page ==1)
					TopicListActivity1.this.finish();
				
				num = page > 1? page -1:1;
				max_num = max_num>5?max_num-1:5;
			} else if (tabId.equals(TABID_NEXT)) {
				num = page + 1;
				if (num <= 5) {
					max_num = 5;
				} else {
					max_num = num;
				}
			} else {
				num = Integer.parseInt(tabId.split("_")[1]);
				if (num <= 5) {
					max_num = 5;
				} else {
					max_num = num;
				}
			}
			RSSFeed rssFeed2 = null;
			if (!tabId.equals(TABID_PRE)) {
				rssFeed2 = map.get(num);
			}
			if (rssFeed2 == null) {
				link = link.substring(0, link.length() - 1);
				final String newURL;
				if (link.indexOf("page") == -1) {
					newURL = link + "&page=" + num + "&rss=1";
				} else {
					newURL = link.replaceAll("page=\\d", "page=" + num
							+ "&rss=1");
				}
				new Thread() {
					@Override
					public void run() {
						String str = StringUtil.getSaying();
						if (str.indexOf(";") != -1) {
							activityUtil.notice("加速模式", str.split(";")[0]
									+ "-----" + str.split(";")[1]);
						} else {
							activityUtil.notice("加速模式", str);
						}
						RSSUtil rssUtil = new RSSUtil();
						rssUtil.parseXml(newURL);
						rssFeed = rssUtil.getFeed();
						//map.put(num, rssFeed);
						Message message = new Message();
						handler_rebuild.sendMessage(message);
					}
				}.start();
			} else {
				rssFeed = rssFeed2;
				reBuild();
			}
		}
		
	}
	
	private Handler handler_rebuild = new Handler() {
		public void handleMessage(Message msg) {
			if (rssFeed != null && rssFeed.getItems().size() != 0) {
				reBuild();
				activityUtil.dismiss();
			}
		}
	};

	private void reBuild() {
		tabHost.setOnTabChangedListener(null);
		tabHost.setCurrentTab(0);
		tabHost.clearAllTabs();
		initView();
		setListener();
	}

	//for other tabs
	class tabFactory2 implements TabContentFactory {
		public View createTabContent(String tag) {
			TextView view = new TextView(TopicListActivity1.this);
			return view;
		}
	}
	
	//factory for current tab;
	class tabFactory implements TabContentFactory {
		public View createTabContent(String tag) {

			ListView listView = new ListView(TopicListActivity1.this);
			// listView.setBackgroundResource(R.drawable.bodybg);
			listView.setCacheColorHint(0);
			// listView.setDivider(null);
			listView.setVerticalScrollBarEnabled(false);
			MyAdapter adapter = new MyAdapter(TopicListActivity1.this);
			listView.setAdapter(adapter);
			listView.setOnTouchListener(flingListener);
			listView.setOnItemClickListener(new ArticlelistItemClickListener());
			//listView.indexOfChild(child)
			currentListview = listView;
			return listView;
		}
		
		
	}
	

	class FloorOpener {
		
		void handleFloor(String floorUrl) {
			final String url = floorUrl;
			ArticlePage ap = map_article.get(url + "&page=1");
			if (ap != null) {
				app.setArticlePage(ap);
				Intent intent = new Intent();
				intent.setClass(TopicListActivity1.this,
						ArticleListActivity1.class);
				startActivity(intent);
			} else {
				new Thread() {
					@Override
					public void run() {
						ArticlePage articlePage = null;

						System.out.println("host:" + HttpUtil.HOST);
						if (!HttpUtil.HOST_PORT.equals("")) {

							String str = StringUtil.getSaying();
							if (str.indexOf(";") != -1) {
								activityUtil.notice("加速模式", str.split(";")[0]
										+ "-----" + str.split(";")[1]);
							} else {
								activityUtil.notice("加速模式", str);
							}
							articlePage = HttpUtil
									.getArticlePageByJson(HttpUtil.HOST
											+ "?uri=" + url + "@page=1");
						}
						if (articlePage == null) {
							// activityUtil.notice("INFO",
							// "连接策略:P-N,这种策略将模拟浏览器显示方式");

							String str = StringUtil.getSaying();
							if (str.indexOf(";") != -1) {
								activityUtil.notice("普通模式", str.split(";")[0]
										+ "-----" + str.split(";")[1]);
							} else {
								activityUtil.notice("普通模式", str);
							}
							String cookie = "";
							if (TopicListActivity1.this.app.getUid() != null
									&& TopicListActivity1.this.app.getUid() != "")
								cookie = "ngaPassportUid=" + app.getUid()
										+ "; ngaPassportCid=" + app.getCid();
							articlePage = HttpUtil.getArticlePage(url
									+ "&page=1", cookie);
						}
						if (articlePage != null) {
							app.setArticlePage(articlePage);// 设置当前page
							map_article.put(url + "&page=1", articlePage);// 添加新的数据
							app.setMap_article(map_article);
							Intent intent = new Intent();
							intent.setClass(TopicListActivity1.this,
									ArticleListActivity1.class);
							startActivity(intent);
						} else {
							activityUtil.notice("ERROR", "可能遇到了一个广告或者帖子被删除");
						}
						activityUtil.dismiss();
					}
				}.start();
			}
		}

	}

	
	class ArticlelistItemClickListener extends FloorOpener
		implements OnItemClickListener{
			
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			RSSItem item = rssFeed.getItems().get(arg2);
			String guid = item.getGuid();
			 String url;
			if (guid.indexOf("\n") != -1) {
				url = guid.substring(0, guid.length() - 1);
			} else {
				url = guid;
			}
			handleFloor(url);
		}
			
	}
	

	class MyAdapter extends BaseAdapter {
		HashMap<Integer, View> m = new HashMap<Integer, View>();
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public int getCount() {
			return rssFeed.getItems().size();
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View view, ViewGroup parent) {

			View convertView = m.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.topic_list, null);
				TextView num = (TextView) convertView.findViewById(R.id.num);
				TextView title = (TextView) convertView
						.findViewById(R.id.title);
				TextView nickName = (TextView) convertView
						.findViewById(R.id.nickName);
				TextView replies = (TextView) convertView
						.findViewById(R.id.replies);
				num.setText("" + (position + 1));
				RSSItem item = rssFeed.getItems().get(position);

				String description = item.getDescription();
				String[] arr = description.split("\n");
				
				nickName.setText(item.getAuthor());
				int last_index = arr.length -1;
				String reply_count = "0";
				int count_in_desc = arr[last_index].indexOf("个");
				if( count_in_desc !=-1)
					reply_count = arr[last_index].substring(0, arr[last_index].indexOf("个"));
					replies.setText("[" + reply_count + " RE]");
				try{
				title.setTextColor(parent.getResources().getColor(
							ThemeManager.getInstance().getForegroundColor()));
				}catch(Exception e){
					Log.e(getClass().getSimpleName(),Log.getStackTraceString(e));
				}
				title.setText(arr[0]);
				

				/*String guid = item.getGuid();
				final String url;
				if (guid.indexOf("\n") != -1) {
					url = guid.substring(0, guid.length() - 1);
				} else {
					url = guid;
				}*/
				
				
				//if(false)// no cache
				m.put(position, convertView);
			}
			return convertView;
		}
		

		


	}

	class TopicFlingListener extends SimpleOnGestureListener implements
			OnTouchListener {
		Context context;
		GestureDetector gDetector;
		final float FLING_MIN_DISTANCE = 100;
		/*public ArticleFlingListener() {
			super();
		}*/

		public TopicFlingListener(Context context) {
			this(context, null);
		}

		public TopicFlingListener(Context context, GestureDetector gDetector) {

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
			if(e1.getX()-e2.getX() > FLING_MIN_DISTANCE&& 
					Math.abs(velocityX) >1.73* Math.abs(velocityY)){
				//left
				
				new BoardPageNumChangeListener().onTabChanged(TABID_NEXT);
				 return true;
			}
			
			if(e2.getX()-e1.getX() > FLING_MIN_DISTANCE&&
					Math.abs(velocityX) >1.73* Math.abs(velocityY) ){//3/3^0.5
				//right
				new BoardPageNumChangeListener().onTabChanged(TABID_PRE);
				 return true;
				
			}
			return false;//super.onFling(e1, e2, velocityX, velocityY);
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

			 return gDetector.onTouchEvent(event);
			 
		}

		public GestureDetector getDetector() {
			return gDetector;
		}
	}
	
}

