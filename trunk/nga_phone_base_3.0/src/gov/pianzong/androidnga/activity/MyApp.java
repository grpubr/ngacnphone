package gov.pianzong.androidnga.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sp.phone.bean.ArticlePage;
import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.RSSFeed;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class MyApp extends Application implements PerferenceConstant {
	final private static String TAG = MyApp.class.getSimpleName();
	public final static int version = 284;
	private RSSFeed rssFeed;
	private ArticlePage articlePage;
	private HashMap<Object, ArticlePage> map_article;
	private PhoneConfiguration config;
	boolean newVersion = false;
	
	
	@Override
	public void onCreate() {
		Log.w(TAG,"app nga androind start");
		//CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(getApplicationContext());
		config = PhoneConfiguration.getInstance();
		initUserInfo();
		loadConfig();
		super.onCreate();
	}

	private void initUserInfo() {
		
		String uid = null;
		String cid = null;
		
		PhoneConfiguration config = PhoneConfiguration.getInstance();

		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);

		
			uid = share.getString(UID, "");
			cid = share.getString(CID, "");
			if (uid != null && cid != null && uid != "" && cid != "") {
				config.setUid(uid);
				config.setCid(cid);
			}
			boolean downImgWithoutWifi = share.getBoolean(DOWNLOAD_IMG_NO_WIFI, false);
			config.setDownImgNoWifi(downImgWithoutWifi);
			boolean downAvatarNoWifi = share.getBoolean(DOWNLOAD_AVATAR_NO_WIFI, false);
			config.setDownAvatarNoWifi(downAvatarNoWifi);

	}

	private void loadConfig(){
		
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		if(share.getBoolean(NIGHT_MODE, false))
			ThemeManager.getInstance().setMode(1);
		
		ThemeManager.getInstance().screenOrentation = 
				share.getInt(SCREEN_ORENTATION,ActivityInfo.SCREEN_ORIENTATION_USER);
		
		
		int version_in_config = share.getInt(VERSION, 0);
		if(version_in_config < version){
			newVersion = true;
			Editor editor = share.edit();
			editor.putInt(VERSION, version);
			editor.putBoolean(REFRESH_AFTER_POST, false);
			editor.putString(RECENT_BOARD,"");
			editor.commit();
			
		}
		

		//refresh
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		config.setRefreshAfterPost(
				share.getBoolean(REFRESH_AFTER_POST,false));
		config.setRefreshAfterPost(false);
		
		config.showAnimation = share.getBoolean(SHOW_ANIMATION, true);
		config.useViewCache = share.getBoolean(USE_VIEW_CACHE, false);
		config.showSignature = share.getBoolean(SHOW_SIGNATURE, true);
		
		//font
		final float defTextSize = 21.0f;//new TextView(this).getTextSize();
		final int defWebSize = 16;//new WebView(this).getSettings().getDefaultFontSize();
		
		final float textSize = share.getFloat(TEXT_SIZE, defTextSize);
		final int webSize = share.getInt(WEB_SIZE, defWebSize);
		config.setTextSize(textSize);
		config.setWebSize(webSize);
		
		boolean notification = share.getBoolean(ENABLE_NOTIFIACTION, true);
		boolean notificationSound = share.getBoolean(NOTIFIACTION_SOUND, true);
		config.notification = notification;
		config.notificationSound = notificationSound;
		
		config.nikeWidth = share.getInt(NICK_WIDTH, 100);
		
		//bookmarks
		String bookmarkJson = share.getString(BOOKMARKS, "");
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		try{
		if(!bookmarkJson.equals(""))
			bookmarks=JSON.parseArray(bookmarkJson, Bookmark.class);
		}catch(Exception e){
			Log.e("JSON_error",Log.getStackTraceString(e));
		}
		PhoneConfiguration.getInstance().setBookmarks(bookmarks);
		
		
		
	}
	
	
	
	public boolean isNewVersion() {
		return newVersion;
	}

	public void setNewVersion(boolean newVersion) {
		this.newVersion = newVersion;
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
