package sp.phone.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings);
		CheckBox checkBoxDownimgNowifi = (CheckBox) findViewById(R.id.checkBox_down_img_no_wifi);
		MyApp app = (MyApp) getApplication();
		checkBoxDownimgNowifi.setChecked(app.isDownImgWithoutWifi());
		CheckBoxDownimgNowifiChangedListener listener = new CheckBoxDownimgNowifiChangedListener();
		checkBoxDownimgNowifi.setOnCheckedChangeListener(listener);
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
