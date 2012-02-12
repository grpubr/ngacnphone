package sp.phone.activity;

import java.util.HashMap;
import java.util.zip.ZipFile;

import sp.phone.bean.ArticlePage;
import sp.phone.bean.RSSFeed;
import sp.phone.utils.PhoneConfiguration;
import android.app.Application;

public class MyApp extends Application {

	private RSSFeed rssFeed;
	private HashMap<Object, RSSFeed> map;
	private ArticlePage articlePage;
	private HashMap<Object, ArticlePage> map_article;

	private ZipFile zf;
	
	private String uid;
	private String cid;
	private boolean downImgWithoutWifi;
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

	public HashMap<Object, RSSFeed> getMap() {
		return map;
	}

	public void setMap(HashMap<Object, RSSFeed> map) {
		this.map = map;
	}

	public RSSFeed getRssFeed() {
		return rssFeed;
	}

	public void setRssFeed(RSSFeed rssFeed) {
		this.rssFeed = rssFeed;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the cid
	 */
	public String getCid() {
		return cid;
	}

	/**
	 * @param cid the cid to set
	 */
	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setDownImgWithoutWifi(boolean downImgWithoutWifi) {
		this.downImgWithoutWifi = downImgWithoutWifi;
	}

	public boolean isDownImgWithoutWifi() {
		return downImgWithoutWifi;
	}

	public PhoneConfiguration getConfig() {
		return config;
	}




}
