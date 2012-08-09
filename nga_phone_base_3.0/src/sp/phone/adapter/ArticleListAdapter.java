package sp.phone.adapter;

import gov.pianzong.androidnga.R;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
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

public class ArticleListAdapter extends BaseAdapter implements OnLongClickListener
,AvatarLoadCompleteCallBack{
	private static final  String TAG = ArticleListAdapter.class.getSimpleName();
	private ThreadData data;
	private Context activity;
	private final SparseArray<View> viewCache;
	private final Object lock = new Object();
	private final HashSet<String> urlSet = new HashSet<String>();
	
	
	
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
		WebView contentTV;
		TextView floorTV;
		TextView postTimeTV;
		TextView titleTV;
		int position=-1;
		
	}
	
	static class WebViewTag{
		public ListView lv;
		public View holder;
	}
	
	@TargetApi(11)
	void setLayerType(WebView contentTV){
			contentTV.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);		
	}
	private void handleContentTV(WebView contentTV,ThreadRowInfo row,int bgColorId,int bgColor,int fgColor){
		
		contentTV.setBackgroundColor(0);
		if(ActivityUtil.isGreaterThan_2_3_3() &&
				ActivityUtil.islessThan_4_1())
			setLayerType(contentTV);
		
		
		
		//contentTV.setClickable(false);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if(ActivityUtil.isGreaterThan_2_2())
		{
			
			contentTV.setLongClickable(false);
		}
		
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
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);

		
	}
	
	private final WebViewClient client ; 
	
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
			Resources res = avatarIV.getContext().getResources();
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
		}
		
		
		avatarIV.setImageBitmap(defaultAvatar);
		avatarIV.setTag(lou);
		if (!StringUtil.isEmpty(avatarUrl)) {
			final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists() && ! isPending(avatarUrl)) {
					
						Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
			            if(bitmap!=null)
			            	avatarIV.setImageBitmap(bitmap);
			            else
			            	f.delete();
	

				} else {
					final boolean downImg = isInWifi()
							|| PhoneConfiguration.getInstance()
									.isDownAvatarNoWifi();
					
					new AvatarLoadTask(avatarIV, null, downImg,lou,this).execute(
							avatarUrl, avatarPath, userId);

				}
			}
		}

	}
	
	private ViewHolder initHolder(View view){
		ViewHolder holder = new ViewHolder();
		holder.nickNameTV =(TextView) view.findViewById(R.id.nickName);
		holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
		holder.contentTV = (WebView) view.findViewById(R.id.content);
		holder.floorTV = (TextView) view.findViewById(R.id.floor);
		holder.postTimeTV = (TextView)view.findViewById(R.id.postTime);
		holder.titleTV = (TextView) view.findViewById(R.id.floor_title);
		return holder;
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		if (position == 0) {
			start = System.currentTimeMillis();
		}

		ViewHolder holder = null;
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if (viewCache.get(position) != null) {
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
				holder.contentTV.stopLoading();
				if (holder.contentTV.getHeight() > 300) {
					Log.d(TAG, "skip and store a tall view ,floor " + position);
					// if (config.useViewCache)
					viewCache.put(holder.position, view);
					
					view = LayoutInflater.from(activity).inflate(
							R.layout.relative_aritclelist,  parent,false);
					holder = initHolder(view);
					view.setTag(holder);

				}

			}

		}



		holder.position = position;
		ThemeManager theme = ThemeManager.getInstance();
		int colorId = theme.getBackgroundColor();
		view.setBackgroundResource(colorId);

		ThreadRowInfo row = data.getRowList().get(position);
		
		if(row == null){
			holder.titleTV.setText("����¥��");
			return view;
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

		WebView contentTV = holder.contentTV;
		handleContentTV(contentTV, row, colorId, bgColor, fgColor);

		final int lou = row.getLou();
		final String floor = String.valueOf(lou);
		TextView floorTV = holder.floorTV;
		floorTV.setText("[" + floor + " ¥]");
		floorTV.setTextColor(fgColor);

		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(row.getPostdate());
		postTimeTV.setTextColor(fgColor);

		if (position == this.getCount() - 1) {
			end = System.currentTimeMillis();
			Log.i(getClass().getSimpleName(), "render cost:" + (end - start));
		}


		return view;
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
	
	
	private boolean isPending(String url){
		boolean ret = false;
		synchronized(lock){
			ret = urlSet.contains(url);
		}
		return ret;
	}

	@Override
	public void OnAvatarLoadStart(String url) {
		synchronized(lock){
			this.urlSet.add(url);
		}
		
	}



	@Override
	public void OnAvatarLoadComplete(String url) {
		synchronized(lock){
			this.urlSet.add(url);
		}
		
	}
	

}
