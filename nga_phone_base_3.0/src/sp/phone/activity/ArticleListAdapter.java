package sp.phone.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

public class ArticleListAdapter extends ArrayAdapter<HashMap<String, String>> {

	ZipFile zf;

	HashMap<Integer, View> m = new HashMap<Integer, View>();
	ListView listView;
	private LayoutInflater inflater;
	private Activity activity;
	OnTouchListener gestureListener;

	public ArticleListAdapter(Activity activity,OnTouchListener gestureListener,
			List<HashMap<String, String>> lMap, ListView listView, ZipFile zf) {
		super(activity, 0, lMap);
		this.activity = activity;
		this.listView = listView;
		this.zf = zf;
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
	public View getView(int position, View view, ViewGroup parent) {
		if(position ==0){
			start = System.currentTimeMillis();
		}
		View rowView = m.get(position);
		if (rowView != null && m.size() > 1) {
			TextView nickNameTV = (TextView) rowView
					.findViewById(R.id.nickName);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nickNameTV.getLayoutParams();		
			final int configWidth = PhoneConfiguration.getInstance().nikeWidth;
			if(params.width != configWidth){
				params.width =configWidth; 
				nickNameTV.setLayoutParams(params);
			}
			return rowView;
		} else {
			final MyApp app = (MyApp) activity.getApplication();
			//rowView = inflater.inflate(R.layout.article_list_2, null);
			rowView = inflater.inflate(R.layout.relative_aritclelist, null);
			int colorId = ThemeManager.getInstance().getBackgroundColor();
			rowView.setBackgroundResource(colorId);
			
			HashMap<String, String> map = getItem(position);
			final String floor = map.get("floor");// ¥��
			// ͷ����
			final ImageView avatarIV = (ImageView) rowView
					.findViewById(R.id.avatarImage);
			avatarIV.setImageDrawable(null);

			avatarIV.setTag(floor);// ���� tag Ϊ¥��
			final String avatarImage = map.get("avatarImage");// ͷ��
			final String userId = map.get("userId");
			if (!StringUtil.isEmpty(avatarImage)) {
				final String newImage = ImageUtil.newImage(avatarImage, userId);
				if (newImage != null) {
					final Handler handler2 = new Handler() {
						public synchronized void handleMessage(Message message) {
							if (avatarIV != null) {
								avatarIV.setImageBitmap((Bitmap) message.obj);
							}
						}
					};

					new Thread() {
						@Override
						public void run() {

							File file = new File(newImage);
							if (file.exists()) { // ��ʼ��黺���ļ���
								Bitmap bitmap = BitmapFactory
										.decodeFile(newImage);
								if (bitmap != null) {
									//System.out.println("from file" + floor);
									Message message = handler2.obtainMessage(0,
											bitmap);
									handler2.sendMessage(message);
								}
							} else {
								InputStream is = null;
								if (zf != null) { // ��ʼ��黺��ZIP

									String extension = ImageUtil
											.getImageType(avatarImage);
									ZipEntry entry = zf.getEntry("avatarImage/"
											+ userId + "." + extension);
									if (entry != null) {
										try {
											is = zf.getInputStream(entry);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
								if (is == null) {
									//System.out.println("from net" + floor);
									// ����
									if(!app.isDownImgWithoutWifi() && !isInWifi() ){
										Bitmap bitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.default_avatar);
										Message message = handler2.obtainMessage(0, bitmap);
										handler2.sendMessage(message);
									}else
									{
										HttpUtil.downImage(avatarImage, newImage);
										if (file.exists()) {
											Bitmap bitmap = BitmapFactory
													.decodeFile(newImage);
											if (bitmap != null) {
												Message message = handler2
														.obtainMessage(0, bitmap);
												handler2.sendMessage(message);
											} else {
												System.out
														.println("decodeStream fall"
																+ floor);
											}
										}
									}
								} else {
									System.out.println("from zip" + floor);
									Bitmap bitmap = BitmapFactory
											.decodeStream(is);
									if (bitmap != null) {
										Message message = handler2
												.obtainMessage(0, bitmap);
										handler2.sendMessage(message);
									}
								}

							}

						}
					}.start();
				}
			}

			// ��������

			TextView nickNameTV = (TextView) rowView
					.findViewById(R.id.nickName);
			nickNameTV.setText(map.get("nickName"));
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nickNameTV.getLayoutParams();
			params.width = PhoneConfiguration.getInstance().nikeWidth;
			//nickNameTV.setLayoutParams(params);//��������Ǹ����������λ��

			WebView contentTV = (WebView) rowView.findViewById(R.id.content);
			contentTV.setBackgroundColor(0);
			contentTV.setFocusable(false);
			int bgColor = parent.getContext().getResources().getColor(colorId);
			bgColor = bgColor & 0xffffff;
			String bgcolorStr = String.format("%06x",bgColor);
			
			int fgColorId = ThemeManager.getInstance().getForegroundColor();
			int fgColor = parent.getContext().getResources().getColor(fgColorId);
			fgColor = fgColor & 0xffffff;
			String fgColorStr = String.format("%06x",fgColor);
			
			String ngaHtml = StringUtil.decodeForumTag(map.get("content"));
			ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=UTF-8 \">" 
				+ "<body bgcolor= '#"+ bgcolorStr +"'>"
				+ "<font color='#"+ fgColorStr + "' size='2'>"
				+ ngaHtml + 
				"</font></div></body>";
				
			
			WebSettings setting = contentTV.getSettings();
			if(!app.isDownImgWithoutWifi() && !isInWifi() )
				setting.setBlockNetworkImage(true);
			else
				setting.setBlockNetworkImage(false);

			contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);
			contentTV.setOnTouchListener(gestureListener);
			contentTV.getSettings().setDefaultFontSize(
					PhoneConfiguration.getInstance().getWebSize());
			
			TextView floorTV = (TextView) rowView.findViewById(R.id.floor);
			floorTV.setText("[" + floor + " ¥]");

			TextView postTimeTV = (TextView) rowView
					.findViewById(R.id.postTime);
			postTimeTV.setText(map.get("postTime"));

			TextView titleTV = (TextView) rowView.findViewById(R.id.floor_title);
			if (!StringUtil.isEmpty(map.get("title"))) {
				titleTV.setText(map.get("title"));
			} else {
				titleTV.setVisibility(View.GONE);
			}
			//rowView.setOnLongClickListener(new FloorLongClickListen() );
			//rowView.setOnCreateContextMenuListener( new FloorCreateContextMenuListener() );
			//rowView.set
			if(position == this.getCount()-1){
				end = System.currentTimeMillis();
				Log.i(getClass().getSimpleName(),"render cost:" +(end-start));
			}
			m.put(position, rowView);
		}
		

		
		return rowView;
	}
	


}
