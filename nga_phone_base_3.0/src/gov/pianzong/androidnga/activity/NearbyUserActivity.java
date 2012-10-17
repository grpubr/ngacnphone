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
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

public class NearbyUserActivity extends Activity
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
	    Criteria criteria = new Criteria(); 
	    criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 设置精度
	    criteria.setAltitudeRequired(false); // 设置是否需要提供海拔信息
	    criteria.setBearingRequired(false); // 是否需要方向信息
	    criteria.setCostAllowed(false); // 设置找到的 Provider 是否允许产生费用
	    criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置耗电
	    
	    LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE); 
	    String provider=locationManager.getBestProvider(criteria, true); 
	    Location location = null;
	    if(provider != null) { 
        	location = locationManager.getLastKnownLocation(provider); 
        } 
	    SharedPreferences share = getSharedPreferences(
				PERFERENCE, MODE_PRIVATE);
		String userName = share.getString(USER_NAME, "");
		try {
			userName = URLEncoder.encode(userName,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(location != null && !StringUtil.isEmpty(userName)){
			new NearbyUserTask(location.getLatitude(),location.getLongitude(),
					userName,PhoneConfiguration.getInstance().uid,this).execute();

	    }
	}
	


	@Override
	public void OnComplete(String result) {
		if(StringUtil.isEmpty(result))
			return;
		List<NearbyUser> list = null;
		try{
		list = JSON.parseArray(result, NearbyUser.class);
		}catch(Exception e){
			return ;
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
