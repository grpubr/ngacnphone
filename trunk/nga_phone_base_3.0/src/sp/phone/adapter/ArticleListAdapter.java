package sp.phone.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import sp.phone.activity.R;
import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArticleListAdapter extends BaseAdapter {
	private ThreadData data;
	private Context activity;
	
	
	
	public ArticleListAdapter(Context activity) {
		super();
		this.activity = activity;
	}



	@Override
	public int getCount() {
		if(null == data)
			return 0;
		return data.getRowNum();
	}

	
	
	public void setData(ThreadData data) {
		this.data = data;
	}

	

	public ThreadData getData() {
		return data;
	}



	@Override
	public Object getItem(int position) {
		if(null == data)
			return null;
		
		return data.getRowList().get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
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
	
	private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {

		final int lou = row.getLou();
		final String floor = String.valueOf(lou);
		avatarIV.setTag(floor);// 设置 tag 为楼层
		final String avatarUrl = row.getJs_escap_avatar();// 头像
		final String userId = String.valueOf(row.getAuthorid());
		if (!StringUtil.isEmpty(avatarUrl)) {
			final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists()) {
					try {
						InputStream is = new FileInputStream(avatarPath);
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						avatarIV.setImageBitmap(bitmap);
						is.close();
					} catch (Exception e) {

					}

				} else {
					final boolean downImg = isInWifi()
							|| PhoneConfiguration.getInstance()
									.isDownAvatarNoWifi();

					avatarIV.setImageResource(R.drawable.default_avatar);
					new AvatarLoadTask(avatarIV, null, downImg).execute(
							avatarUrl, avatarPath, userId);

				}
			}
		}

	}
	
	public View getView(int position, View view, ViewGroup parent) {
		if(position ==0){
			start = System.currentTimeMillis();
		}
		//View rowView = m.get(position);
		ViewHolder holder = null;
		if(view== null){
			view = LayoutInflater.from(activity).inflate(R.layout.relative_aritclelist, null);
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
			if(holder.contentTV.getHeight() > 300){
				view = LayoutInflater.from(activity).inflate(R.layout.relative_aritclelist, null);
				holder = new ViewHolder();
				holder.nickNameTV =(TextView) view.findViewById(R.id.nickName);
				holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
				holder.contentTV = (WebView) view.findViewById(R.id.content);
				holder.floorTV = (TextView) view.findViewById(R.id.floor);
				holder.postTimeTV = (TextView)view.findViewById(R.id.postTime);
				holder.titleTV = (TextView) view.findViewById(R.id.floor_title);
				view.setTag(holder);
				
			}
			
		}
		
			int colorId = ThemeManager.getInstance().getBackgroundColor();
			view.setBackgroundResource(colorId);
			
			ThreadRowInfo row = data.getRowList().get(position);
			
			handleAvatar(holder.avatarIV, row);
			

			// 其他处理
			int fgColorId = ThemeManager.getInstance().getForegroundColor();
			int fgColor = parent.getContext().getResources().getColor(fgColorId);
			
			
			TextView nickNameTV = holder.nickNameTV;
			nickNameTV.setText(row.getAuthor());
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
			if(row.getContent()== null){
				row.setContent(row.getSubject());
				row.setSubject(null);
			}
			
			String ngaHtml = StringUtil.decodeForumTag(row.getContent());
			ngaHtml = ngaHtml + buildAttachment(row);
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
			//contentTV.loadData(ngaHtml, "text/html; charset=UTF-8", null);
			contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);
			//contentTV.setOnTouchListener(gestureListener);
			contentTV.getSettings().setDefaultFontSize(
					PhoneConfiguration.getInstance().getWebSize());
			
			final int lou =  row.getLou();
			final String floor = String.valueOf(lou);
			TextView floorTV = holder.floorTV;
			floorTV.setText("[" + floor + " 楼]");

			TextView postTimeTV = holder.postTimeTV;
			postTimeTV.setText(row.getPostdate());

			TextView titleTV = holder.titleTV;
			if (!StringUtil.isEmpty(row.getSubject()) && position!=0) {
				titleTV.setText(row.getSubject());
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

	private String buildAttachment(ThreadRowInfo row){
		
		if(row ==null || row.getAttachs() == null || row.getAttachs().size() == 0){
			return "";
		}
		StringBuilder  ret = new StringBuilder();
		ret.append("<br/>附件<hr/><br/>");
		//ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;margin:0px 0px 10px 30px;padding:10px;color:#6b2d25;max-width:100%;'>");
		ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;padding:10px;color:#6b2d25;'>");
		ret.append("<tbody>");
		Iterator<Entry<String, Attachment>> it = row.getAttachs().entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Attachment> entry = it.next(); 
			//String url  = "http://img.ngacn.cc/attachments/" + entry.getValue().getAttachurl();
			ret.append("<tr><td><a href='http://img.ngacn.cc/attachments/");
			ret.append(entry.getValue().getAttachurl());
			ret.append("'>");
			
			ret.append("<img src='http://img.ngacn.cc/attachments/");
			ret.append(entry.getValue().getAttachurl());
			if(entry.getValue().getThumb() == 1)
			{
				ret.append(".thumb.jpg");
				//ret.append(entry.getValue().getExt());
			}
			
			ret.append("' style= 'max-width:70%;'></a>");
			
			ret.append("</td></tr></tbody>");
			
		}
		
		return ret.toString();
	}


}
