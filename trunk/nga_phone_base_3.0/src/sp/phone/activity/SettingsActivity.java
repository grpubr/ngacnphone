package sp.phone.activity;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;


public class SettingsActivity extends Activity{

	private LinearLayout view;
	private CheckBox checkBoxDownimgNowifi;
	private CheckBox nightMode;
	private SeekBar fontSizeBar;
	private float defaultFontSize;
	private TextView fontTextView;
	private int defaultWebSize;
	private SeekBar webSizebar;
	private WebView websizeView;
	//private MyGestureListener gestureListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//gestureListener = new MyGestureListener(this);
		
		initView();
		
	}
	void initView(){
		MyApp app = (MyApp) getApplication();
		//checkbox
		ThemeManager.SetContextTheme(this);
		try{
		 view = (LinearLayout) getLayoutInflater().inflate(R.layout.settings, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.setContentView(view);
		
		
		checkBoxDownimgNowifi = (CheckBox) findViewById(R.id.checkBox_down_img_no_wifi);
		checkBoxDownimgNowifi.setChecked(app.isDownImgWithoutWifi());
		CheckBoxDownimgNowifiChangedListener listener = new CheckBoxDownimgNowifiChangedListener();
		checkBoxDownimgNowifi.setOnCheckedChangeListener(listener);

		nightMode = (CheckBox) findViewById(R.id.checkBox_night_mode);
		nightMode.setChecked(ThemeManager.getInstance().getMode() ==ThemeManager.MODE_NIGHT);
		nightMode.setOnCheckedChangeListener(new NightModeListener());
		
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
		websizeView.loadDataWithBaseURL(null,"帖子内字体大小", "text/html", "utf-8","");
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


	class NightModeListener implements OnCheckedChangeListener{
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			SharedPreferences  share = 
				getSharedPreferences("perference", MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean("nightmode", arg1);
			editor.commit();
			int mode = ThemeManager.MODE_NORMAL;
			if(arg1)
				mode =ThemeManager.MODE_NIGHT;
			ThemeManager.getInstance().setMode(mode);
			updateThemeUI();
		}
		
	}

	class CheckBoxDownimgNowifiChangedListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			MyApp app = (MyApp) getApplication();
			app.setDownImgWithoutWifi(arg1);
			SharedPreferences  share = 
				getSharedPreferences("perference", MODE_PRIVATE);

			Editor editor = share.edit();
			editor.putBoolean("down_load_without_wifi", arg1);
			editor.commit();
			
		}
		
	}
	
	class FontSizeListener implements SeekBar.OnSeekBarChangeListener{

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
					getSharedPreferences("perference", MODE_PRIVATE);

				Editor editor = share.edit();
				editor.putFloat("textsize", textSize);
				editor.commit();
			PhoneConfiguration.getInstance().setTextSize(textSize);
		}
		
		
	}
	
	class WebSizeListener implements SeekBar.OnSeekBarChangeListener{

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
					getSharedPreferences("perference", MODE_PRIVATE);

				Editor editor = share.edit();
				
				editor.putInt("websize", webSize);
				editor.commit();

			PhoneConfiguration.getInstance().setWebSize(webSize);
			
		}
		
	}

}
