package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sp.phone.bean.Board;
import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class MyApp extends Application implements PerferenceConstant {
	final private static String TAG = MyApp.class.getSimpleName();
	public final static int version = 371;
	private PhoneConfiguration config = null;
	boolean newVersion = false;
	
	
	@Override
	public void onCreate() {
		Log.w(TAG,"app nga androind start");
		//CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(getApplicationContext());
		if(config == null)
			config = PhoneConfiguration.getInstance();
		initUserInfo();
		loadConfig();
		if(ActivityUtil.isGreaterThan_2_1())
			initPath();

		super.onCreate();
	}
	
	@TargetApi(8)
	private void initPath(){
		File baseDir = getExternalCacheDir();
		HttpUtil.PATH = baseDir.getAbsolutePath();
		HttpUtil.PATH_AVATAR = HttpUtil.PATH +
				 "/nga_cache";
		HttpUtil.PATH_NOMEDIA = HttpUtil.PATH + "/.nomedia";
	}

	private void initUserInfo() {
		

		
		PhoneConfiguration config = PhoneConfiguration.getInstance();

		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);

		
			final String uid = share.getString(UID, "");
			final String cid = share.getString(CID, "");
			if (!StringUtil.isEmpty(uid) && !StringUtil.isEmpty(cid) ) {
				config.setUid(uid);
				config.setCid(cid);
				String userListString = share.getString(USER_LIST, "");
				if(StringUtil.isEmpty(userListString)){
					final String name = share.getString(USER_NAME, "");
					addToUserList(uid,cid,name);
	
				}
			}

			boolean downImgWithoutWifi = share.getBoolean(DOWNLOAD_IMG_NO_WIFI, false);
			config.setDownImgNoWifi(downImgWithoutWifi);
			boolean downAvatarNoWifi = share.getBoolean(DOWNLOAD_AVATAR_NO_WIFI, false);
			config.setDownAvatarNoWifi(downAvatarNoWifi);

	}

	public void addToUserList(String uid, String cid, String name){
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		
		String userListString = share.getString(USER_LIST, "");
		
		List<User> userList = null;
		//new ArrayList<User>();
		if(StringUtil.isEmpty(userListString)){
			userList = new ArrayList<User>();
		}else
		{
			userList = JSON.parseArray(userListString, User.class);
			for( User u : userList){
				if(u.getUserId().equals(uid)){
					userList.remove(u);
					break;
				}
					
			}
		}
		
		User user = new User();
		user.setCid(cid);
		user.setUserId(uid);
		user.setNickName(name);
		userList.add(0,user);
		
		userListString = JSON.toJSONString(userList);
		
		share.edit().putString(USER_LIST, userListString).commit();
		
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
			
			String recentStr = share.getString(RECENT_BOARD, "");
			List<Board> recentList = null;
			if(!StringUtil.isEmpty(recentStr)){
				recentList = JSON.parseArray(recentStr, Board.class);
				if(recentList != null){
					for(int j = 0;j< recentList.size();j++){
						recentList.get(j).setIcon(R.drawable.pdefault);
					}
					recentStr = JSON.toJSONString(recentList);
					editor.putString(RECENT_BOARD,recentStr);
				}
			}
			
			editor.commit();
			
		}
		

		//refresh
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		config.setRefreshAfterPost(
				share.getBoolean(REFRESH_AFTER_POST,false));
		config.setRefreshAfterPost(false);
		
		config.showAnimation = share.getBoolean(SHOW_ANIMATION, true);
		config.useViewCache = share.getBoolean(USE_VIEW_CACHE, true);
		config.showSignature = share.getBoolean(SHOW_SIGNATURE, false);
		
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

	

	

	public PhoneConfiguration getConfig() {
		return config;
	}




}
