package sp.phone.activity;

import sp.phone.bean.PerferenceConstant;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;


public class SettingsActivity extends Activity{

	private LinearLayout view;
	private CompoundButton checkBoxDownimgNowifi;
	private CompoundButton checkBoxDownAvatarNowifi;
	private CompoundButton nightMode;
	private CompoundButton notification;
	private CompoundButton notificationSound;
	private SeekBar fontSizeBar;
	private float defaultFontSize;
	private TextView fontTextView;
	private int defaultWebSize;
	private SeekBar webSizebar;
	private WebView websizeView;
	//private MyGestureListener gestureListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//gestureListener = new MyGestureListener(this);
		
		initView();
		
	}
	void initView(){

		//checkbox
		ThemeManager.SetContextTheme(this);
		try{
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				view = (LinearLayout) getLayoutInflater().inflate(R.layout.switch_settings, null);
			else
				view = (LinearLayout) getLayoutInflater().inflate(R.layout.settings, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.setContentView(view);
		
		
		checkBoxDownimgNowifi = (CompoundButton) findViewById(R.id.checkBox_down_img_no_wifi);
		checkBoxDownimgNowifi.setChecked(PhoneConfiguration.getInstance().downImgNoWifi);
		DownImgNoWifiChangedListener listener = new DownImgNoWifiChangedListener();
		checkBoxDownimgNowifi.setOnCheckedChangeListener(listener);
		
		checkBoxDownAvatarNowifi = (CompoundButton) findViewById(R.id.checkBox_download_avatar_no_wifi);
		checkBoxDownAvatarNowifi.setChecked(PhoneConfiguration.getInstance().downImgNoWifi);
		DownAvatarNowifiChangedListener AvatarListener = new DownAvatarNowifiChangedListener();
		checkBoxDownAvatarNowifi.setOnCheckedChangeListener(AvatarListener);

		nightMode = (CompoundButton) findViewById(R.id.checkBox_night_mode);
		nightMode.setChecked(ThemeManager.getInstance().getMode() ==ThemeManager.MODE_NIGHT);
		nightMode.setOnCheckedChangeListener(new NightModeListener());
		

		
		notificationSound = (CompoundButton) findViewById(R.id.checkBox_notification_sound);
		notificationSound.setOnCheckedChangeListener(new NotificationSoundChangedListener());
		notificationSound.setChecked(PhoneConfiguration.getInstance().notificationSound);
		notificationSound.setEnabled(PhoneConfiguration.getInstance().notification);
		
		
		notification = (CompoundButton) findViewById(R.id.checkBox_notification);
		notification.setOnCheckedChangeListener(
				new NotificationChangedListener(notificationSound));
		notification.setChecked(PhoneConfiguration.getInstance().notification);
		
		
		fontTextView = (TextView)findViewById(R.id.textView_font_size);
		defaultFontSize = fontTextView.getTextSize();
		
		fontSizeBar = (SeekBar) findViewById(R.id.fontsize_seekBar);
		fontSizeBar.setMax(300);
		final float textSize =  PhoneConfiguration.getInstance().getTextSize();
		int progress = (int) (100.0f*textSize /defaultFontSize);
		fontSizeBar.setProgress(progress);
		fontSizeBar.setOnSeekBarChangeListener(new FontSizeListener());
		fontTextView.setTextSize(textSize);

		websizeView = (WebView) findViewById(R.id.websize_view);
		defaultWebSize = websizeView.getSettings().getDefaultFontSize();
		webSizebar = (SeekBar) findViewById(R.id.webszie_bar);
		webSizebar.setMax(300);
		final int webSize = PhoneConfiguration.getInstance().getWebSize();
		progress = 100* webSize /defaultWebSize;
		webSizebar.setProgress(progress);
		websizeView.getSettings().setDefaultFontSize(webSize);
		websizeView.loadDataWithBaseURL(null,"�����������С", "text/html", "utf-8","");
		webSizebar.setOnSeekBarChangeListener(
				new WebSizeListener());
		

		
		
		//Switch s_wifi = new Switch(this);
		//view.addView(s_wifi, checkBoxDownimgNowifi.getLayoutParams());
		
	
		
		//TextView textv =(TextView)findViewById(R.id.fling_text);
		//textv.setOnTouchListener(this.gestureListener);
		updateThemeUI();
	}
	
	private void updateThemeUI(){
		int fgColor =  getResources().getColor(
						ThemeManager.getInstance().getForegroundColor());
		checkBoxDownimgNowifi.setTextColor(fgColor);
		nightMode.setTextColor(fgColor);
		notification.setTextColor(fgColor);
		notificationSound.setTextColor(fgColor);
		fontTextView.setTextColor(fgColor);
		
		view.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int flags =15;
		/*ActionBar.DISPLAY_SHOW_HOME;
		flags |= ActionBar.DISPLAY_USE_LOGO;
		flags |= ActionBar.DISPLAY_SHOW_TITLE;
		flags |= ActionBar.DISPLAY_HOME_AS_UP;
		flags |= ActionBar.DISPLAY_SHOW_CUSTOM;*/
		//final ActionBar bar = getActionBar();
		//bar.setDisplayOptions(flags);
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			default:
			//case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	class NightModeListener implements OnCheckedChangeListener, PerferenceConstant{
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			SharedPreferences  share = 
				getSharedPreferences(PERFERENCE, MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean(NIGHT_MODE, arg1);
			editor.commit();
			int mode = ThemeManager.MODE_NORMAL;
			if(arg1)
				mode =ThemeManager.MODE_NIGHT;
			ThemeManager.getInstance().setMode(mode);
			updateThemeUI();
		}
		
	}

	class DownImgNoWifiChangedListener 
		implements OnCheckedChangeListener, PerferenceConstant{

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

			PhoneConfiguration.getInstance().downImgNoWifi = arg1;
			SharedPreferences  share = 
				getSharedPreferences(PERFERENCE, MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean(DOWNLOAD_IMG_NO_WIFI, arg1);
			editor.commit();
			
		}
		
	}
	
	class DownAvatarNowifiChangedListener 
		implements OnCheckedChangeListener, PerferenceConstant{

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

			PhoneConfiguration.getInstance().downAvatarNoWifi = arg1;
			SharedPreferences  share = 
				getSharedPreferences(PERFERENCE, MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean(DOWNLOAD_AVATAR_NO_WIFI, arg1);
			editor.commit();
			
		}
		
	}
	
	class NotificationChangedListener 
		implements OnCheckedChangeListener, PerferenceConstant{
		final CompoundButton child;
		public NotificationChangedListener(CompoundButton child){
			this.child = child;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			
			PhoneConfiguration.getInstance().notification = isChecked;
			child.setEnabled(isChecked);
			SharedPreferences  share = 
				getSharedPreferences(PERFERENCE, MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean(ENABLE_NOTIFIACTION, isChecked);
			editor.commit();
			
		}
			
		
		
	}
	
	class NotificationSoundChangedListener 
		implements OnCheckedChangeListener, PerferenceConstant{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			
			PhoneConfiguration.getInstance().notificationSound= isChecked;

			SharedPreferences  share = 
				getSharedPreferences(PERFERENCE, MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean(NOTIFIACTION_SOUND, isChecked);
			editor.commit();
			
		}
		
	}
	
	class FontSizeListener implements SeekBar.OnSeekBarChangeListener
		, PerferenceConstant{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(progress !=0)
				fontTextView.setTextSize(defaultFontSize*progress/100.0f);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			float textSize = defaultFontSize*seekBar.getProgress()/100.0f;
			SharedPreferences  share = 
					getSharedPreferences(PERFERENCE, MODE_PRIVATE);

				Editor editor = share.edit();
				editor.putFloat(TEXT_SIZE, textSize);
				editor.commit();
			PhoneConfiguration.getInstance().setTextSize(textSize);
		}
		
		
	}
	
	class WebSizeListener implements SeekBar.OnSeekBarChangeListener
		, PerferenceConstant{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(progress !=0)
				websizeView.getSettings().setDefaultFontSize(
					(int) (defaultWebSize*progress/100.0f));
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			//int textSize = (int) (defaultWebSize*seekBar.getProgress()/100.0f);

			int webSize = (int) (defaultWebSize*seekBar.getProgress()/100.0f);
			SharedPreferences  share = 
					getSharedPreferences(PERFERENCE, MODE_PRIVATE);

				Editor editor = share.edit();
				
				editor.putInt(WEB_SIZE, webSize);
				editor.commit();

			PhoneConfiguration.getInstance().setWebSize(webSize);
			
		}
		
	}

}
