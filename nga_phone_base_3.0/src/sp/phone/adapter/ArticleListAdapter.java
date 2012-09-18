package sp.phone.adapter;

import gov.pianzong.androidnga.R;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import sp.phone.bean.Attachment;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.task.AvatarLoadTask;
import sp.phone.task.ForumTagDecodTask;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.support.v4.app.FragmentActivity;
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
	private final SparseArray<SoftReference <View>> viewCache;
	private final Object lock = new Object();
	private final HashSet<String> urlSet = new HashSet<String>();
	
	
	
	public ArticleListAdapter(Context activity) {
		super();
		this.activity = activity;
		this.viewCache = new SparseArray<SoftReference <View>>();
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
	
	public static String convertToHtmlText(final ThreadRowInfo row,boolean showImage,final String fgColorStr,final String bgcolorStr){
		String ngaHtml = StringUtil.decodeForumTag(row.getContent(),showImage);
		if(StringUtil.isEmpty(ngaHtml)){
			ngaHtml = row.getAlterinfo();
		}
		if(StringUtil.isEmpty(ngaHtml)){
			
			ngaHtml= "<font color='red'>[Òþ²Ø]</font>";
		}
		ngaHtml = ngaHtml + buildComment(row,fgColorStr) + buildAttachment(row,showImage)
				+ buildSignature(row,showImage);
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">" 
			+ "<body bgcolor= '#"+ bgcolorStr +"'>"
			+ "<font color='#"+ fgColorStr + "' size='2'>"
			+ ngaHtml + 
			"</font></body>";

		
		return ngaHtml;
	}
	
	private void handleContentTV(final WebView contentTV,final ThreadRowInfo row,int bgColorId,int bgColor,int fgColor){
		
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
		final String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x",htmlfgColor);
		if(row.getContent()== null){
			row.setContent(row.getSubject());
			row.setSubject(null);
		}
		
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
		showImage = showImage || ActivityUtil.isGreaterThan_2_3_3();
		//String ngaHtml = convertToHtmlText(row, showImage, fgColorStr, bgcolorStr);
		
		/*StringUtil.decodeForumTag(row.getContent(),showImage);
		if(StringUtil.isEmpty(ngaHtml)){
			ngaHtml = row.getAlterinfo();
		}
		if(StringUtil.isEmpty(ngaHtml)){
			
			ngaHtml= "<font color='red'>[Òþ²Ø]</font>";
		}
		ngaHtml = ngaHtml + buildComment(row,fgColorStr) + buildAttachment(row,showImage)
				+ buildSignature(row,showImage);
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">" 
			+ "<body bgcolor= '#"+ bgcolorStr +"'>"
			+ "<font color='#"+ fgColorStr + "' size='2'>"
			+ ngaHtml + 
			"</font></body>";*/

		

			
		
		WebSettings setting = contentTV.getSettings();
		if(!showImage )
			setting.setBlockNetworkImage(true);
		else
			setting.setBlockNetworkImage(false);
		setting.setDefaultFontSize(
				PhoneConfiguration.getInstance().getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		//final String htmlData = ngaHtml;
		final int lou = row.getLou();
		//int delay = 100 + lou%20 * 20;
		Log.d(TAG, "post content for "+ lou);
		
		contentTV.setTag(row.getLou());
		contentTV.loadDataWithBaseURL(null,"loading", "text/html", "utf-8",null);

		ForumTagDecodTask task= new ForumTagDecodTask(row, showImage, fgColorStr, bgcolorStr);
		if(ActivityUtil.isGreaterThan_2_3_3()){
			excuteOnExcutor(task,contentTV);
		}else{
			task.execute(contentTV);
		}
		//boolean postResult = true;
		
		/*postResult = contentTV.postDelayed(new Runnable(){

			@Override
			public void run() {
				Log.d(TAG, "load content for "+ lou);
				//contentTV.loadDataWithBaseURL(null,htmlData, "text/html", "utf-8",null);
				contentTV.in
			}
			
		}, delay);*/
		//contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);

		
	}
	
	@TargetApi(11)
	private void excuteOnExcutor(ForumTagDecodTask task, WebView contentTV){
		task.executeOnExecutor(ForumTagDecodTask.THREAD_POOL_EXECUTOR, contentTV);
	}
	
	
	private final WebViewClient client ; 
	
	private Bitmap defaultAvatar = null;
	private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {

		final int lou = row.getLou();
		//final String floor = String.valueOf(lou);
		//avatarIV.setTag(floor);// ÉèÖÃ tag ÎªÂ¥²ã
		final String avatarUrl = parseAvatarUrl(row.getJs_escap_avatar());// Í·Ïñ
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
		
		Object tagObj =  avatarIV.getTag();
		if(tagObj instanceof AvatarTag){
			AvatarTag origTag = (AvatarTag)tagObj;
			if(origTag.isDefault == false){
				ImageUtil.recycleImageView(avatarIV);
				Log.d(TAG, "recycle avatar:" + origTag.lou);
			}else{
				Log.d(TAG, "default avatar, skip recycle");
			}
		}

		
		AvatarTag tag = new AvatarTag(lou, true);
		avatarIV.setImageBitmap(defaultAvatar);
		avatarIV.setTag(tag);
		if (!StringUtil.isEmpty(avatarUrl)) {
			final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists() && ! isPending(avatarUrl)) {
					
						Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
			            if(bitmap!=null){
			            	avatarIV.setImageBitmap(bitmap);
			            	tag.isDefault = false;
			            }
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

		ThreadRowInfo row = data.getRowList().get(position);
		
		int lou = -1;
		if(row != null)
			lou = row.getLou();
		ViewHolder holder = null;
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		
		SoftReference <View> ref = viewCache.get(position);
		View cachedView = null;
		if (ref!= null)
		{
			cachedView = ref.get();
		}
		if (cachedView != null) {
			Log.d(TAG, "get view from cache ,floor " + lou);
			return cachedView;
		} else {
			if(ref != null)
				Log.i(TAG, "cached view recycle by system:" + lou);
			if (view == null || config.useViewCache) {
				Log.d(TAG, "inflater new view ,floor " + lou);
				view = LayoutInflater.from(activity).inflate(
						R.layout.relative_aritclelist, parent,false);
				holder = initHolder(view);
				view.setTag(holder);
				if (config.useViewCache)
					viewCache.put(position, new SoftReference <View>(view));
			} else {
				holder = (ViewHolder) view.getTag();
				if (holder.position == position) {
					return view;
				}
				holder.contentTV.stopLoading();
				if (holder.contentTV.getHeight() > 300) {
					Log.d(TAG, "skip and store a tall view ,floor " + lou);
					// if (config.useViewCache)
					viewCache.put(holder.position,new SoftReference<View>(view));
					
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

		
		
		if(row == null){
			holder.titleTV.setText("´íÎóÂ¥²ã");
			return view;
		}

		handleAvatar(holder.avatarIV, row);

		

		
		// ÆäËû´¦Àí
		int fgColorId = ThemeManager.getInstance().getForegroundColor();
		int fgColor = parent.getContext().getResources().getColor(fgColorId);
		
		TextView nickNameTV = holder.nickNameTV;
		nickNameTV.setText(row.getAuthor());
		nickNameTV.setTextColor(fgColor);
		TextPaint tp = holder.nickNameTV.getPaint();
		tp.setFakeBoldText(true);// bold for Chinese character

		TextView titleTV = holder.titleTV;
		if (!StringUtil.isEmpty(row.getSubject()) /*&& position != 0*/) {
			titleTV.setText(row.getSubject());
			titleTV.setTextColor(fgColor);

		} 

		int bgColor = parent.getContext().getResources().getColor(colorId);

		WebView contentTV = holder.contentTV;
		handleContentTV(contentTV, row, colorId, bgColor, fgColor);


		final String floor = String.valueOf(lou);
		TextView floorTV = holder.floorTV;
		floorTV.setText("[" + floor + " Â¥]");
		floorTV.setTextColor(fgColor);

		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(row.getPostdate());
		postTimeTV.setTextColor(fgColor);




		return view;
	}

	private static String buildAttachment(ThreadRowInfo row,boolean showImage){
		
		if(row ==null || row.getAttachs() == null || row.getAttachs().size() == 0){
			return "";
		}
		StringBuilder  ret = new StringBuilder();
		ret.append("<br/><br/>¸½¼þ<hr/><br/>");
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

	private static String parseAvatarUrl(String js_escap_avatar){
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
	
	private static String buildComment(ThreadRowInfo row, String fgColor){
		if(row ==null || row.getComments() == null || row.getComments().size() == 0){
			return "";
		}
		
		StringBuilder  ret = new StringBuilder();
		ret.append("<br/></br>ÆÀÂÛ<hr/><br/>");
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

	private static String buildSignature(ThreadRowInfo row, boolean showImage){
		if(row ==null || row.getSignature() == null 
				|| row.getSignature().length() == 0
				|| !PhoneConfiguration.getInstance().showSignature){
			return "";
		}
		return "<br/></br>Ç©Ãû<hr/><br/>"
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
			this.urlSet.remove(url);
		}
		
	}
	

}
