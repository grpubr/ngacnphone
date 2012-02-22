package sp.phone.forumoperation;

import java.util.HashMap;

import sp.phone.activity.ArticleListActivity1;
import sp.phone.activity.MyApp;
import sp.phone.bean.ArticlePage;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ActivityUtil;
import android.app.Activity;
import android.content.Intent;

public 	class FloorOpener {
	private MyApp app;
	private Activity activity;
	HashMap<Object, ArticlePage> map_article;
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	public FloorOpener(Activity activity) {
		super();
		this.activity = activity;
		this.app = (MyApp) activity.getApplication();
		//rssFeed = app.getRssFeed();
		//map = app.getMap();
		map_article = app.getMap_article();
	}
	public void handleFloor(String floorUrl) {
		final String url = floorUrl;
		final String  completeURL = url + "&page=1";
		ArticlePage ap = map_article.get(completeURL);
		String url_last=null;
		if (ap != null &&ap.getPage() != null ) 
			url_last=ap.getPage().get("last");
		
		if (ap != null &&url_last !=null && !url_last.equals(completeURL)  ) {
			
			app.setArticlePage(ap);
			Intent intent = new Intent();
			intent.setClass(activity,
					ArticleListActivity1.class);
			activity.startActivity(intent);
		} else {
			new Thread() {
				@Override
				public void run() {
					ArticlePage articlePage = null;

					System.out.println("host:" + HttpUtil.HOST);
					if (!HttpUtil.HOST_PORT.equals("")) {
						activityUtil.noticeSaying(activity);
						/*String str = StringUtil.getSaying();
						if (str.indexOf(";") != -1) {
							activityUtil.notice("����ģʽ", str.split(";")[0]
									+ "-----" + str.split(";")[1]);
						} else {
							activityUtil.notice("����ģʽ", str);
						}*/
						articlePage = HttpUtil
								.getArticlePageByJson(HttpUtil.HOST
										+ "?uri=" + url + "@page=1");
					}
					if (articlePage == null) {
						// activityUtil.notice("INFO",
						// "���Ӳ���:P-N,���ֲ��Խ�ģ���������ʾ��ʽ");
						activityUtil.noticeSaying(activity);
						String threadUrl = url;
						if(!url.startsWith("http://")){
							threadUrl = "http://bbs.ngacn.cc" + url;
						}
						String cookie = "";
						if (app.getUid() != null && app.getUid() != "")
							cookie = "ngaPassportUid=" + app.getUid()
									+ "; ngaPassportCid=" + app.getCid();
						articlePage = HttpUtil.getArticlePage(threadUrl
								+ "&page=1", cookie);
					}
					activityUtil.dismiss();
					if (articlePage != null) {
						//TODO implement FIFO map
						if(map_article.size()>4)
							map_article.clear();
						
						app.setArticlePage(articlePage);// ���õ�ǰpage
						map_article.put(url + "&page=1", articlePage);// ����µ�����
						app.setMap_article(map_article);
						Intent intent = new Intent(activity,
								ArticleListActivity1.class);
					
						activity.startActivity(intent);
					} else {
						activityUtil.noticeError( "����������һ�����������ӱ�ɾ��"
								,activity);
					}
					
				}
			}.start();
		}
	}

}

