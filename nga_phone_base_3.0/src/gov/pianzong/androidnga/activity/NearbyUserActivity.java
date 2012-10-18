package gov.pianzong.androidnga.activity;

import gov.pianzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import sp.phone.adapter.NearbyUsersAdapter;
import sp.phone.bean.NearbyUser;
import sp.phone.bean.PerferenceConstant;
import sp.phone.interfaces.OnNearbyLoadComplete;
import sp.phone.task.NearbyUserTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

public class NearbyUserActivity extends FragmentActivity
implements PerferenceConstant,OnNearbyLoadComplete{
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.setContentView(R.layout.webview_layout);
		setTheme(R.style.AppTheme);
		lv = new ListView(this);
		this.setContentView(lv);
		initLocation();
	}
	
	void initLocation()
	{
	    ActivityUtil.reflushLocation(this);
	    Location location = PhoneConfiguration.getInstance().location;

	    SharedPreferences share = getSharedPreferences(
				PERFERENCE, MODE_PRIVATE);
		String userName = share.getString(USER_NAME, "");
		try {
			userName = URLEncoder.encode(userName,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(location == null)
		{
			Toast.makeText(this, R.string.fail_to_locate, Toast.LENGTH_SHORT).show();
		}else if(StringUtil.isEmpty(userName))
		{
			Toast.makeText(this, R.string.nearby_no_login, Toast.LENGTH_SHORT).show();
		}else
		{
	    	ActivityUtil.getInstance().noticeSaying(this);
			new NearbyUserTask(location.getLatitude(),location.getLongitude(),
					userName,PhoneConfiguration.getInstance().uid,this).execute();

	    }
	}
	


	@Override
	public void OnComplete(String result) {
		ActivityUtil.getInstance().dismiss();
		if(StringUtil.isEmpty(result))
			return;
		List<NearbyUser> list = null;
		try{
		list = JSON.parseArray(result, NearbyUser.class);
		}catch(Exception e){
			return ;
		}
		if(list != null && list.size() ==0){
			Toast.makeText(this, R.string.nearby_no_user, Toast.LENGTH_SHORT).show();
		}
		
		NearbyUsersAdapter adapter = new NearbyUsersAdapter(list);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NearbyUser u = (NearbyUser) parent.getItemAtPosition(position);
		    	String loc = "http://ditu.google.cn/maps?q="
    			+u.getLatitude() + "," + u.getLongitude()
    			+"(" +u.getNickName()+")";
		    	Uri mapUri = Uri.parse(loc);  
    			Intent i = new Intent(Intent.ACTION_VIEW); 
    			i.setData(mapUri);  
       
        		startActivity(i);
				
			}
			
		});

		lv.setAdapter(adapter);
		
	}

}
