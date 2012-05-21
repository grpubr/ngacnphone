package sp.phone.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArticleListAdapter  extends ArrayAdapter<HashMap<String, String>> 
{

	//ZipFile zf;

	//SparseArray<View> m = new SparseArray<View>();
	ListView listView;
	private LayoutInflater inflater;
	private Activity activity;
	OnTouchListener gestureListener;

	public ArticleListAdapter(Activity activity,OnTouchListener gestureListener,
			List<HashMap<String, String>> lMap, ListView listView) {
		super(activity, 0, lMap);
		this.activity = activity;
		this.listView = listView;
		//this.zf = zf;
		this.gestureListener  = gestureListener;
		inflater = LayoutInflater.from(activity);
		


	}
	private boolean isInWifi(){
		ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return wifi == State.CONNECTED;
	}
	long start;
	long end;
	class ViewHolder{
		TextView nickNameTV;
		ImageView avatarIV;
		WebView contentTV;
		TextView floorTV;
		TextView postTimeTV;
		TextView titleTV;
		
	}
	public View getView(int position, View view, ViewGroup parent) {
		if(position ==0){
			start = System.currentTimeMillis();
		}
		//View rowView = m.get(position);
		ViewHolder holder = null;
		if(view== null){
			view = inflater.inflate(R.layout.relative_aritclelist, null);
			holder = new ViewHolder();
			holder.nickNameTV =(TextView) view.findViewById(R.id.nickName);
			holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
			holder.contentTV = (WebView) view.findViewById(R.id.content);
			holder.floorTV = (TextView) view.findViewById(R.id.floor);
			holder.postTimeTV = (TextView)view.findViewById(R.id.postTime);
			holder.titleTV = (TextView) view.findViewById(R.id.floor_title);
			view.setTag(holder);
			
			
		}else{
			holder = (ViewHolder) view.getTag();
		}
		/*if (rowView != null && m.size() > 1) {
			TextView nickNameTV = (TextView) rowView
					.findViewById(R.id.nickName);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nickNameTV.getLayoutParams();		
			final int configWidth = PhoneConfiguration.getInstance().nikeWidth;
			if(params.width != configWidth){
				params.width =configWidth; 
				nickNameTV.setLayoutParams(params);
			}
			return rowView;
		} else {*/

			//rowView = inflater.inflate(R.layout.relative_aritclelist, null);
			int colorId = ThemeManager.getInstance().getBackgroundColor();
			view.setBackgroundResource(colorId);
			
			HashMap<String, String> map = getItem(position);
			final String floor = map.get("floor");// 楼层
			// 头像处理
			//final ImageView avatarIV = holder.avatarIV;
			//avatarIV.setImageDrawable(null);

			holder.avatarIV.setTag(floor);// 设置 tag 为楼层
			final String avatarUrl = map.get("avatarImage");// 头像
			final String userId = map.get("userId");
			if (!StringUtil.isEmpty(avatarUrl)) {
				final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
				final boolean downImg = isInWifi()||PhoneConfiguration.getInstance().isDownAvatarNoWifi();
				new AvatarLoadTask(holder.avatarIV, null, downImg).execute(avatarUrl, avatarPath, userId);
				
			}

			// 其他处理
			int fgColorId = ThemeManager.getInstance().getForegroundColor();
			int fgColor = parent.getContext().getResources().getColor(fgColorId);
			
			
			TextView nickNameTV = holder.nickNameTV;
			nickNameTV.setText(map.get("nickName"));
			nickNameTV.setTextColor(fgColor);
			TextPaint tp = holder.nickNameTV.getPaint();
            tp.setFakeBoldText(true);//bold for Chinese character
             
             
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nickNameTV.getLayoutParams();
			params.width = PhoneConfiguration.getInstance().nikeWidth;
			//nickNameTV.setLayoutParams(params);//其他组件是根据这个来定位的

			int bgColor = parent.getContext().getResources().getColor(colorId);
			WebView contentTV = holder.contentTV;//(WebView) rowView.findViewById(R.id.content);
			contentTV.setBackgroundColor(0);
			contentTV.setBackgroundResource(colorId);
			contentTV.setFocusable(false);
			
			bgColor = bgColor & 0xffffff;
			String bgcolorStr = String.format("%06x",bgColor);
			
			int htmlfgColor = fgColor & 0xffffff;
			String fgColorStr = String.format("%06x",htmlfgColor);
			
			String ngaHtml = StringUtil.decodeForumTag(map.get("content"));
			ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">" 
				+ "<body bgcolor= '#"+ bgcolorStr +"'>"
				+ "<font color='#"+ fgColorStr + "' size='2'>"
				+ ngaHtml + 
				"</font></div></body>";

			

				
			
			WebSettings setting = contentTV.getSettings();
			if(!PhoneConfiguration.getInstance().isDownImgNoWifi() && !isInWifi() )
				setting.setBlockNetworkImage(true);
			else
				setting.setBlockNetworkImage(false);
			contentTV.loadData(ngaHtml, "text/html; charset=UTF-8", null);
			//contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);
			contentTV.setOnTouchListener(gestureListener);
			contentTV.getSettings().setDefaultFontSize(
					PhoneConfiguration.getInstance().getWebSize());
			
			TextView floorTV = holder.floorTV;
			floorTV.setText("[" + floor + " 楼]");

			TextView postTimeTV = holder.postTimeTV;
			postTimeTV.setText(map.get("postTime"));

			TextView titleTV = holder.titleTV;
			if (!StringUtil.isEmpty(map.get("title")) && position!=0) {
				titleTV.setText(map.get("title"));
				titleTV.setTextColor(fgColor);
				tp = titleTV.getPaint();
	            tp.setFakeBoldText(true);//bold for Chinese character
			} else {
				titleTV.setVisibility(View.GONE);
			}


			if(position == this.getCount()-1){
				end = System.currentTimeMillis();
				Log.i(getClass().getSimpleName(),"render cost:" +(end-start));
			}
	
		
	
		
		return view;
	}

	


}
