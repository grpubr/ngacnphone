package sp.phone.activity;

import java.util.HashMap;
import java.util.zip.ZipFile;

import sp.phone.bean.ArticlePage;
import sp.phone.bean.RSSFeed;
import sp.phone.utils.PhoneConfiguration;
import android.app.Application;

public class MyApp extends Application {

	private RSSFeed rssFeed;
	private ArticlePage articlePage;
	private HashMap<Object, ArticlePage> map_article;

	private ZipFile zf;
	

	private PhoneConfiguration config;
	
	
	
	@Override
	public void onCreate() {
		config = PhoneConfiguration.getInstance();
		super.onCreate();
	}

	public ZipFile getZf() {
		return zf;
	}

	public void setZf(ZipFile zf) {
		this.zf = zf;
	}

	public HashMap<Object, ArticlePage> getMap_article() {
		if (map_article == null) {
			return new HashMap<Object, ArticlePage>();
		}
		return map_article;
	}

	public void setMap_article(HashMap<Object, ArticlePage> mapArticle) {
		map_article = mapArticle;
	}

	public ArticlePage getArticlePage() {
		return articlePage;
	}

	public void setArticlePage(ArticlePage articlePage) {
		this.articlePage = articlePage;
	}



	public RSSFeed getRssFeed() {
		return rssFeed;
	}

	public void setRssFeed(RSSFeed rssFeed) {
		this.rssFeed = rssFeed;
	}

	

	public PhoneConfiguration getConfig() {
		return config;
	}




}
