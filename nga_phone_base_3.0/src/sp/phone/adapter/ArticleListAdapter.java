package sp.phone.adapter;

import gov.pianzong.androidnga.R;
import gov.pianzong.androidnga.activity.ArticleListActivity;
import gov.pianzong.androidnga.activity.ImageViewerActivity;
import gov.pianzong.androidnga.activity.TopicListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ArticleListAdapter extends BaseAdapter implements OnLongClickListener {
	private static final  String TAG = ArticleListAdapter.class.getSimpleName();
	private ThreadData data;
	final private Context activity;
	private final SparseArray<View> viewCache;
	
	
	
	public ArticleListAdapter(Context activity) {
		super();
		this.activity = activity;
		this.viewCache = new SparseArray<View>();
		
		 client = new ArticleListWebClient((FragmentActivity) activity);

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
	
	static class ViewHolder{
		TextView nickNameTV;
		ImageView avatarIV;
		View contentTV;
		TextView floorTV;
		TextView postTimeTV;
		TextView titleTV;
		int position=-1;
		
	}

	
	static class WebViewTag{
		public ListView lv;
		public View holder;
	}
	
	private void handleContentTV(WebView contentTV,ThreadRowInfo row,int bgColorId,int bgColor,int fgColor){
		
		contentTV.setBackgroundColor(0);
		//if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		//	contentTV.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		
		contentTV.setFocusable(false);
		//contentTV.setClickable(false);
		contentTV.setLongClickable(false);
		contentTV.setFocusableInTouchMode(false);
		
		bgColor = bgColor & 0xffffff;
		String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		String fgColorStr = String.format("%06x",htmlfgColor);
		if(row.getContent()== null){
			row.setContent(row.getSubject());
			row.setSubject(null);
		}
		
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
		String ngaHtml = StringUtil.decodeForumTag(row.getContent(),showImage);
		if(StringUtil.isEmpty(ngaHtml))
			ngaHtml = row.getAlterinfo();
		ngaHtml = ngaHtml + buildComment(row,fgColorStr) + buildAttachment(row,showImage)
				+ buildSignature(row,showImage);
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">" 
			+ "<body bgcolor= '#"+ bgcolorStr +"'>"
			+ "<font color='#"+ fgColorStr + "' size='2'>"
			+ ngaHtml + 
			"</font></body>";

		

			
		
		WebSettings setting = contentTV.getSettings();
		if(!PhoneConfiguration.getInstance().isDownImgNoWifi() && !isInWifi() )
			setting.setBlockNetworkImage(true);
		else
			setting.setBlockNetworkImage(false);
		setting.setDefaultFontSize(
				PhoneConfiguration.getInstance().getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(this.client);
		//contentTV.setOnLongClickListener(this);
		contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);
		//contentTV.loadData(ngaHtml, "text/html; charset=utf-8", "UTF-8");
		
		
	}
	
	private final WebViewClient client;
	

	private Bitmap defaultAvatar = null;
	private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {

		final int lou = row.getLou();
		final String floor = String.valueOf(lou);
		avatarIV.setTag(floor);// ���� tag Ϊ¥��
		final String avatarUrl = parseAvatarUrl(row.getJs_escap_avatar());// ͷ��
		final String userId = String.valueOf(row.getAuthorid());
		if(PhoneConfiguration.getInstance().nikeWidth < 3){
			avatarIV.setImageBitmap(null);
			return;
		}
		if(defaultAvatar == null || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth){
			Drawable defaultAvatarDrawable = avatarIV.getContext().getResources().getDrawable(R.drawable.default_avatar);
			this.defaultAvatar = ImageUtil.zoomImageByWidth(defaultAvatarDrawable, 
					PhoneConfiguration.getInstance().nikeWidth);
		}
		

		avatarIV.setImageBitmap(defaultAvatar);
		
		if (!StringUtil.isEmpty(avatarUrl)) {
			final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists()) {
					
						Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
			            if(bitmap!=null)
			            	avatarIV.setImageBitmap(bitmap);
			            else
			            	f.delete();
	

				} else {
					final boolean downImg = isInWifi()
							|| PhoneConfiguration.getInstance()
									.isDownAvatarNoWifi();
					
					new AvatarLoadTask(avatarIV, null, downImg).execute(
							avatarUrl, avatarPath, userId);

				}
			}
		}

	}
	
	private ViewHolder initHolder(View view){
		ViewHolder holder = new ViewHolder();
		holder.nickNameTV =(TextView) view.findViewById(R.id.nickName);
		holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
		holder.contentTV = view.findViewById(R.id.content);
		holder.floorTV = (TextView) view.findViewById(R.id.floor);
		holder.postTimeTV = (TextView)view.findViewById(R.id.postTime);
		holder.titleTV = (TextView) view.findViewById(R.id.floor_title);
		return holder;
	}
	
	private boolean isLight(ThreadRowInfo row){
		if(row == null)
			return false;
		String content = row.getContent();
		
		if(content != null){
			if(content.indexOf("[img]") >=0 ||
					content.indexOf("[IMG]") >=0 ||
					content.indexOf("[flash]") >=0 ||
					content.indexOf("[quote]") >=0 ||
					content.indexOf("[s:") >=0
				){
				return false;
			}
		}
		
		if(row.getAttachs() != null)
			return false;
		
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if(config.showSignature){
			content = row.getSignature();
			if(content.indexOf("[img]") >=0 ||
					content.indexOf("[IMG]") >=0
				){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isLight(View view){
		if(view == null)
			return false;
		ViewHolder holder = (ViewHolder) view.getTag();
		return holder.contentTV instanceof TextView;
	}
	private View createView(int position, View view, ViewGroup parent){
		ThreadRowInfo row = data.getRowList().get(position);
		ViewHolder holder = null;
		View ret = null;
		if (viewCache.get(position) != null) {
			Log.d(TAG, "get view from cache ,floor " + position);
			return viewCache.get(position);
		}
		if(view == null){
			
			if(isLight(row))
				ret = LayoutInflater.from(activity).inflate(
						R.layout.simple_articlelist, parent,false);
			else
				ret = LayoutInflater.from(activity).inflate(
						R.layout.relative_aritclelist, parent,false);
			holder = initHolder(ret);
			ret.setTag(holder);
			
			return ret;
		}
		
		holder = (ViewHolder) view.getTag();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if (isLight(row)) {
			
			if(isLight(view))
				return view;
			
			if(config.useViewCache)
				this.viewCache.put(holder.position, view);
			ret = LayoutInflater.from(activity).inflate(
					R.layout.simple_articlelist, parent,false);
			holder = initHolder(ret);
			ret.setTag(holder);
			
			return ret;
			
		} else {
			if (!isLight(view)) {
				if (!config.useViewCache && holder.contentTV.getHeight() < 300) {
					return view;
				}

			}
			if (config.useViewCache)
				viewCache.put(holder.position, view);
			
			ret = LayoutInflater.from(activity).inflate(
					R.layout.relative_aritclelist, parent, false);
			holder = initHolder(ret);
			ret.setTag(holder);
			return ret;

		}
			


		
		
	}
	public View getView(int position, View view, ViewGroup parent) {
		
		ThreadRowInfo row = data.getRowList().get(position);

		
		//PhoneConfiguration config = PhoneConfiguration.getInstance();
		/*if (viewCache.get(position) != null) {
			Log.d(TAG, "get view from cache ,floor " + position);
			return viewCache.get(position);
		} else {
			if (view == null || config.useViewCache) {
				Log.d(TAG, "inflater new view ,floor " + position);
				view = LayoutInflater.from(activity).inflate(
						R.layout.relative_aritclelist, parent,false);
				holder = initHolder(view);
				view.setTag(holder);
				if (config.useViewCache)
					viewCache.put(position, view);
			} else {
				holder = (ViewHolder) view.getTag();
				if (holder.position == position) {
					return view;
				}
				//holder.contentTV.stopLoading();
				if (holder.contentTV.getHeight() > 300) {
					Log.d(TAG, "skip and store a tall view ,floor " + position);
					
					viewCache.put(holder.position, view);
					
					view = LayoutInflater.from(activity).inflate(
							R.layout.relative_aritclelist,  parent,false);
					holder = initHolder(view);
					view.setTag(holder);

				}

			}

		}*/
		
		View ret = createView(position, view, parent);
		
		ViewHolder holder = (ViewHolder) ret.getTag();	
		if(holder.position == position){
			return ret;
		}


		holder.position = position;
		
		ThemeManager theme = ThemeManager.getInstance();
		int colorId = theme.getBackgroundColor();
		ret.setBackgroundResource(colorId);

		
		
		if(row == null){
			holder.titleTV.setText("����¥��");
			return ret;
		}

		handleAvatar(holder.avatarIV, row);

		

		
		// ��������
		int fgColorId = ThemeManager.getInstance().getForegroundColor();
		int fgColor = parent.getContext().getResources().getColor(fgColorId);
		
		TextView nickNameTV = holder.nickNameTV;
		nickNameTV.setText(row.getAuthor());
		nickNameTV.setTextColor(fgColor);
		TextPaint tp = holder.nickNameTV.getPaint();
		tp.setFakeBoldText(true);// bold for Chinese character

		TextView titleTV = holder.titleTV;
		if (!StringUtil.isEmpty(row.getSubject()) && position != 0) {
			titleTV.setText(row.getSubject());
			titleTV.setTextColor(fgColor);

		} 

		int bgColor = parent.getContext().getResources().getColor(colorId);

		if(!isLight(ret)){
			WebView contentTV = (WebView) holder.contentTV;
			handleContentTV(contentTV, row, colorId, bgColor, fgColor);
		}else{
			TextView contentText = (TextView) holder.contentTV;
			handleContentTV(contentText, row, colorId, bgColor, fgColor);
		}

		final int lou = row.getLou();
		final String floor = String.valueOf(lou);
		TextView floorTV = holder.floorTV;
		floorTV.setText("[" + floor + " ¥]");
		floorTV.setTextColor(fgColor);

		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(row.getPostdate());
		postTimeTV.setTextColor(fgColor);




		return ret;
	}

	private void handleContentTV(TextView contentText, ThreadRowInfo row,
			int colorId, int bgColor, int fgColor) {


		
		contentText.setBackgroundColor(bgColor);
		
		bgColor = bgColor & 0xffffff;
		//String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		String fgColorStr = String.format("%06x",htmlfgColor);
		if(row.getContent()== null){
			row.setContent(row.getSubject());
			row.setSubject(null);
		}
		
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
		String ngaHtml = StringUtil.decodeForumTag(row.getContent(),showImage);
		if(StringUtil.isEmpty(ngaHtml))
			ngaHtml = row.getAlterinfo();
		ngaHtml = ngaHtml + buildComment(row,fgColorStr) + buildAttachment(row,showImage)
				+ buildSignature(row,showImage);
		ngaHtml = "<font color='#"+ fgColorStr + "' size='2'>"
			+ ngaHtml + 
			"</font>";

		

			
		
		contentText.setText(Html.fromHtml(ngaHtml));
		
		
	}



	private String buildAttachment(ThreadRowInfo row,boolean showImage){
		
		if(row ==null || row.getAttachs() == null || row.getAttachs().size() == 0){
			return "";
		}
		StringBuilder  ret = new StringBuilder();
		ret.append("<br/><br/>����<hr/><br/>");
		//ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;margin:0px 0px 10px 30px;padding:10px;color:#6b2d25;max-width:100%;'>");
		ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:2'>");
		ret.append("<tbody>");
		Iterator<Entry<String, Attachment>> it = row.getAttachs().entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Attachment> entry = it.next(); 
			//String url  = "http://img.ngacn.cc/attachments/" + entry.getValue().getAttachurl();
			ret.append("<tr><td><a href='http://img.ngacn.cc/attachments/");
			ret.append(entry.getValue().getAttachurl());
			ret.append("'>");
			if (showImage) {
				ret.append("<img src='http://img.ngacn.cc/attachments/");
				ret.append(entry.getValue().getAttachurl());
				if (entry.getValue().getThumb() == 1) {
					ret.append(".thumb.jpg");
					// ret.append(entry.getValue().getExt());
				}
			} else {
				ret.append("<img src='file:///android_asset/ic_offline_image.png");
			}

			ret.append("' style= 'max-width:70%;'></a>");
			
			ret.append("</td></tr>");
			
		}
		ret.append("</tbody></table>");
		
		return ret.toString();
	}

	private String parseAvatarUrl(String js_escap_avatar){
		//"js_escap_avatar":"{ \"t\":1,\"l\":2,\"0\":{ \"0\":\"http://pic2.178.com/53/533387/month_1109/93ba4788cc8c7d6c75453fa8a74f3da6.jpg\",\"cX\":0.47,\"cY\":0.78},\"1\":{ \"0\":\"http://pic2.178.com/53/533387/month_1108/8851abc8674af3adc622a8edff731213.jpg\",\"cX\":0.49,\"cY\":0.68}}"
		if(null == js_escap_avatar)
			return null;
		
		int start = js_escap_avatar.indexOf("http");
		if(start == 0|| start == -1)
			return js_escap_avatar;
		int end = js_escap_avatar.indexOf("\"",start);//
		if(end == -1)
			end = js_escap_avatar.length();
		String ret= null;
		try{
			ret = js_escap_avatar.substring(start, end);
		}catch(Exception e){
			Log.e(TAG, "cann't handle avatar url "+ js_escap_avatar);
		}
		return ret;
	}
	
	private String buildComment(ThreadRowInfo row, String fgColor){
		if(row ==null || row.getComments() == null || row.getComments().size() == 0){
			return "";
		}
		
		StringBuilder  ret = new StringBuilder();
		ret.append("<br/></br>����<hr/><br/>");
		ret.append("<table  border='1px' cellspacing='0px' style='border-collapse:collapse;");
		ret.append("color:");
		ret.append(fgColor);
		ret.append("'>");
		
		ret.append("<tbody>");
		
		Iterator<ThreadRowInfo> it = row.getComments().iterator();
		while(it.hasNext()){
			ThreadRowInfo comment = it.next();
			ret.append("<tr><td>");
			ret.append("<span style='font-weight:bold' >");
			ret.append(comment.getAuthor());
			ret.append("</span><br/>");
			ret.append("<img src='");
			String avatarUrl = parseAvatarUrl(comment.getJs_escap_avatar());
			ret.append(avatarUrl);
			ret.append("' style= 'max-width:32;'>");
			
			ret.append("</td><td>");
			ret.append(comment.getContent());
			ret.append("</td></tr>");
			
		}
		ret.append("</tbody></table>");
		return ret.toString();
	}

	private String buildSignature(ThreadRowInfo row, boolean showImage){
		if(row ==null || row.getSignature() == null 
				|| row.getSignature().length() == 0
				|| !PhoneConfiguration.getInstance().showSignature){
			return "";
		}
		return "<br/></br>ǩ��<hr/><br/>"
				+StringUtil.decodeForumTag(row.getSignature(),showImage);
	}




	@Override
	public void notifyDataSetChanged() {
		this.viewCache.clear();
		super.notifyDataSetChanged();
	}



	@Override
	public boolean onLongClick(View v) {
		if(v instanceof WebView){
			WebViewTag tag =  (WebViewTag) v.getTag();
			tag.lv.showContextMenuForChild(tag.holder);
			return true;
		}
		return false;
	}
	
	
	

}
