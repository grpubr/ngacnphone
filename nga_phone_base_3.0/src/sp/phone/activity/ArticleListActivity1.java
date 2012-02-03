package sp.phone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

import sp.phone.bean.Article;
import sp.phone.bean.ArticlePage;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class ArticleListActivity1 extends Activity {

	ActivityUtil activityUtil = new ActivityUtil(this);

	private TabHost tabHost;
	private ArticlePage articlePage;
	private HashMap<Object, ArticlePage> map_article;

	private MyApp app;
	final int REPLY_POST_ORDER = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);

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

	SoundPool soundPool = null;
	private int hitOkSfx;

	private void initView() {

		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// ������Ƶ��
		hitOkSfx = soundPool.load(this, R.raw.shake, 0);

		TextView titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText(articlePage.getNow().get("title"));

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setBackgroundResource(ActivityUtil.bg);
		HashMap<String, String> page = articlePage.getPage();
		if (page == null || page.size() == 0) {
			TabSpec spec = tabHost.newTabSpec("tab" + 1);
			TextView tv = new TextView(ArticleListActivity1.this);
			tv.setText("��ҳ");
			tv.setTextSize(20);
			spec.setIndicator(tv);
			spec.setContent(new tabFactory());
			tabHost.addTab(spec);
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ListView currentView = (ListView) tabHost.getCurrentView();
		ArticleListAdapter currentAdapter = (ArticleListAdapter) currentView.getAdapter();
		StringBuffer postPrefix = new StringBuffer();
		String tid = null;
		tid = articlePage.getNow().get("link");
		tid = tid.substring(tid.indexOf("tid=")+4);
		int end = tid.indexOf("&");
		if(end == -1)
			end = tid.length();
		tid = tid.substring(0,end);
		if( REPLY_POST_ORDER ==item.getItemId())
		{

			HashMap<String, String> map = currentAdapter.getItem(info.position);
			
			final String name = map.get("nickName");
			final String postTime = map.get("postTime");
			final String url = map.get("url");
			
			/*tid = currentAdapter.getItem(0).get("url");
			tid = tid.substring(url.indexOf("tid=")+4);
			int end = tid.indexOf("&");
			if(end == -1)
				end = tid.length();
			tid = tid.substring(0,end);*/
			
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

	private void setListener() {
		tabHost.setOnTabChangedListener(changeListener);
		
	}

	OnTabChangeListener changeListener = new OnTabChangeListener() {
		public void onTabChanged(String tabId) {

			//soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);

			// ���¼��� ����
			HashMap<String, String> page = articlePage.getPage();
			String urls = HttpUtil.Server;
			if (tabId.equals("tab_first")) {
				urls += page.get("first");
			} else if (tabId.equals("tab_next")) {
				urls += page.get("next");
			} else if (tabId.equals("tab_last")) {
				urls += page.get("last");
			} else {
				String num = tabId.split("_")[1];
				for (HashMap<String, String> hashMap : articlePage.getList()) {
					if (num.equals(hashMap.get("num"))) {
						urls += hashMap.get("link");
						break;
					}
				}
			}

			if (urls.indexOf("\n") != -1) {
				urls = urls.substring(0, urls.length() - 1);
			}

			final String s = urls;

			ArticlePage ap2 = map_article.get(s);
			if (ap2 != null) {
				articlePage = ap2;
				// Message message = new Message();
				// handler_rebuild.sendMessage(message);
				System.gc();
				reBuild();
			} else {
				new Thread() {
					@Override
					public void run() {
						ArticlePage ap = null;
						if (!HttpUtil.HOST_PORT.equals("")) {
							String str = StringUtil.getSaying();
							if (str.indexOf(";") != -1) {
								activityUtil.notice("����ģʽ", str.split(";")[0]
										+ "-----" + str.split(";")[1]);
							} else {
								activityUtil.notice("����ģʽ", str);
							}
							String a = s.replace("&", "@");
							ap = HttpUtil.getArticlePageByJson(HttpUtil.HOST
									+ "?uri=" + a);

						}
						if (ap == null) {
							String str = StringUtil.getSaying();
							if (str.indexOf(";") != -1) {
								activityUtil.notice("��ͨģʽ", str.split(";")[0]
										+ "-----" + str.split(";")[1]);
							} else {
								activityUtil.notice("��ͨģʽ", str);
							}

							// activityUtil.notice("INFO",
							// "���Ӳ���:P-N,��ģ���������ʾ��ʽ");
							
							String cookie = "ngaPassportUid=" + ArticleListActivity1.this.app.getUid()
								+"; ngaPassportCid=" + ArticleListActivity1.this.app.getCid();
							ap = HttpUtil.getArticlePage(s,cookie);
						}
						if (ap != null) {
							app.setArticlePage(ap);// ���õ�ǰpage
							map_article.put(s, ap);// ����µ�����
							app.setMap_article(map_article);
							articlePage = ap;
							Message message = new Message();
							handler_rebuild.sendMessage(message);
						} else {
							activityUtil.notice("ERROR", "����������һ�����������ӱ�ɾ��");
						}
						activityUtil.dismiss();

					}
				}.start();
			}

		}
	};

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
						ArticleListActivity1.this, mData, listView, zf);
				listView.setAdapter(adapter);
				// listView.setBackgroundResource(R.drawable.bodybg);
				listView.setCacheColorHint(0);
				// listView.setDivider(null);
				listView.setVerticalScrollBarEnabled(false);
				listView.setOnCreateContextMenuListener(new FloorCreateContextMenuListener());
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

}
