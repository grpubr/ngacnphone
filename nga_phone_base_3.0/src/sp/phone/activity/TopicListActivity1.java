package sp.phone.activity;

import java.util.HashMap;

import sp.phone.bean.ArticlePage;
import sp.phone.bean.RSSFeed;
import sp.phone.bean.RSSItem;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.RSSUtil;
import sp.phone.utils.StringUtil;
import sp.phone.activity.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class TopicListActivity1 extends Activity {

	ActivityUtil activityUtil = new ActivityUtil(this);

	static TabHost tabHost;
	private RSSFeed rssFeed;
	private int page;
	private HashMap<Object, RSSFeed> map;
	private int max_num = 5;
	private MyApp app;
	private HashMap<Object, ArticlePage> map_article;

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
				Toast.makeText(this, "������",
						Toast.LENGTH_LONG).show();
				break;
			case R.id.threadlist_menu_item3 :
				Toast.makeText(this, "�����û��",
						Toast.LENGTH_LONG).show();
				break;
		}
		return true;
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

	}

	SoundPool soundPool = null;
	private int hitOkSfx;

	private void initView() {

		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// ������Ƶ��
		hitOkSfx = soundPool.load(this, R.raw.tweet, 0);

		setTitle(rssFeed.getTitle());
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setBackgroundResource(ActivityUtil.bg);
		// ��ҳ
		TabSpec ts_first = tabHost.newTabSpec("tab_f");
		TextView tv2 = new TextView(TopicListActivity1.this);
		tv2.setBackgroundResource(R.drawable.page_first);
		ts_first.setIndicator(tv2);
		ts_first.setContent(new tabFactory2());
		tabHost.addTab(ts_first);
		// ҳ����
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

		TabSpec ts_next = tabHost.newTabSpec("tab_n");
		TextView tv3 = new TextView(TopicListActivity1.this);
		tv3.setBackgroundResource(R.drawable.page_next);
		ts_next.setIndicator(tv3);
		ts_next.setContent(new tabFactory2());
		tabHost.addTab(ts_next);

	}

	private void setListener() {
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(final String tabId) {

				soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);

				String link = rssFeed.getLink();
				final int num;
				if (tabId.equals("tab_f")) {
					num = 1;
					max_num = 5;
				} else if (tabId.equals("tab_n")) {
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
				if (!tabId.equals("tab_f")) {
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
								activityUtil.notice("����ģʽ", str.split(";")[0]
										+ "-----" + str.split(";")[1]);
							} else {
								activityUtil.notice("����ģʽ", str);
							}
							RSSUtil rssUtil = new RSSUtil();
							rssUtil.parseXml(newURL);
							rssFeed = rssUtil.getFeed();
							map.put(num, rssFeed);
							Message message = new Message();
							handler_rebuild.sendMessage(message);
						}
					}.start();
				} else {
					rssFeed = rssFeed2;
					reBuild();
				}
			}
		});
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

	class tabFactory implements TabContentFactory {
		public View createTabContent(String tag) {

			ListView listView = new ListView(TopicListActivity1.this);
			// listView.setBackgroundResource(R.drawable.bodybg);
			listView.setCacheColorHint(0);
			// listView.setDivider(null);
			listView.setVerticalScrollBarEnabled(false);
			MyAdapter adapter = new MyAdapter(TopicListActivity1.this);
			listView.setAdapter(adapter);

			return listView;
		}
	}

	class tabFactory2 implements TabContentFactory {
		public View createTabContent(String tag) {
			TextView view = new TextView(TopicListActivity1.this);
			return view;
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
			if (convertView != null) {
				return convertView;
			} else {
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
				replies.setText("[" + arr[1].substring(0, arr[1].indexOf("��"))
						+ " RE]");

				title.setText(Html.fromHtml(arr[0]));

				String guid = item.getGuid();
				final String url;
				if (guid.indexOf("\n") != -1) {
					url = guid.substring(0, guid.length() - 1);
				} else {
					url = guid;
				}

				title.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
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
											activityUtil.notice("����ģʽ", str
													.split(";")[0]
													+ "-----"
													+ str.split(";")[1]);
										} else {
											activityUtil.notice("����ģʽ", str);
										}
										articlePage = HttpUtil
												.getArticlePageByJson(HttpUtil.HOST
														+ "?uri="
														+ url
														+ "@page=1");
									}
									if (articlePage == null) {
										// activityUtil.notice("INFO",
										// "���Ӳ���:P-N,���ֲ��Խ�ģ���������ʾ��ʽ");

										String str = StringUtil.getSaying();
										if (str.indexOf(";") != -1) {
											activityUtil.notice("��ͨģʽ", str
													.split(";")[0]
													+ "-----"
													+ str.split(";")[1]);
										} else {
											activityUtil.notice("��ͨģʽ", str);
										}
										String cookie = "";
										if(TopicListActivity1.this.app.getUid() != null && 
												TopicListActivity1.this.app.getUid()!= "")
										cookie= "ngaPassportUid=" + TopicListActivity1.this.app.getUid()
										+"; ngaPassportCid=" + TopicListActivity1.this.app.getCid();
										articlePage = HttpUtil
												.getArticlePage(url + "&page=1",cookie);
									}
									if (articlePage != null) {
										app.setArticlePage(articlePage);// ���õ�ǰpage
										map_article.put(url + "&page=1",
												articlePage);// ����µ�����
										app.setMap_article(map_article);
										Intent intent = new Intent();
										intent.setClass(
												TopicListActivity1.this,
												ArticleListActivity1.class);
										startActivity(intent);
									} else {
										activityUtil.notice("ERROR",
												"����������һ�����������ӱ�ɾ��");
									}
									activityUtil.dismiss();
								}
							}.start();
						}
					}
				});
				m.put(position, convertView);
			}
			return convertView;
		}
	}
}