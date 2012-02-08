package sp.phone.activity;

import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class SettingsActivity extends Activity{

	private View view;
	private CheckBox checkBoxDownimgNowifi;
	private CheckBox nightMode;
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
		try{
		view = getLayoutInflater().inflate(R.layout.settings, null);
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
		nightMode.setOnCheckedChangeListener(new OnCheckedChangeListener(){

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
			
		});
	
		
		//TextView textv =(TextView)findViewById(R.id.fling_text);
		//textv.setOnTouchListener(this.gestureListener);
		updateThemeUI();
	}
	
	private void updateThemeUI(){
		checkBoxDownimgNowifi.setTextColor(
				this.getResources().getColor(
						ThemeManager.getInstance().getForegroundColor()));
		nightMode.setTextColor(
				this.getResources().getColor(
						ThemeManager.getInstance().getForegroundColor()));
		
		view.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
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
	
}
