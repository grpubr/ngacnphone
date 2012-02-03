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
import sp.phone.utils.StringUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ArticleListAdapter extends ArrayAdapter<HashMap<String, String>> {

	ZipFile zf;

	HashMap<Integer, View> m = new HashMap<Integer, View>();
	ListView listView;
	private LayoutInflater inflater;
	private Activity activity;

	public ArticleListAdapter(Activity activity,
			List<HashMap<String, String>> lMap, ListView listView, ZipFile zf) {
		super(activity, 0, lMap);
		this.activity = activity;
		this.listView = listView;
		this.zf = zf;
		inflater = LayoutInflater.from(activity);
		


	}

	public View getView(int position, View view, ViewGroup parent) {

		View rowView = m.get(position);
		if (rowView != null) {
			return rowView;
		} else {
			rowView = inflater.inflate(R.layout.article_list_2, null);
			HashMap<String, String> map = getItem(position);
			final String floor = map.get("floor");// 楼层
			// 头像处理
			final ImageView avatarIV = (ImageView) rowView
					.findViewById(R.id.avatarImage);
			avatarIV.setImageDrawable(null);

			avatarIV.setTag(floor);// 设置 tag 为楼层
			final String avatarImage = map.get("avatarImage");// 头像
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
							if (file.exists()) { // 开始检查缓存文件夹
								Bitmap bitmap = BitmapFactory
										.decodeFile(newImage);
								if (bitmap != null) {
									System.out.println("from file" + floor);
									Message message = handler2.obtainMessage(0,
											bitmap);
									handler2.sendMessage(message);
								}
							} else {
								InputStream is = null;
								if (zf != null) { // 开始检查缓存ZIP

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
									System.out.println("from net" + floor);
									// 下载
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

			// 其他处理

			TextView nickNameTV = (TextView) rowView
					.findViewById(R.id.nickName);
			nickNameTV.setText(map.get("nickName"));

			TextView contentTV = (TextView) rowView.findViewById(R.id.content);
			String ngaHtml = StringUtil.parseHTML3(map.get("content"));
			ImageGetter imgGetter = new ImageGetter() {
				public Drawable getDrawable(String source) {
					return ImageUtil.reSetDrawable(activity, source);
				}
			};
			Spanned html = Html.fromHtml(ngaHtml, imgGetter, null);
			contentTV.setText(html);

			TextView floorTV = (TextView) rowView.findViewById(R.id.floor);

			floorTV.setText("[" + floor + " 楼]");

			TextView postTimeTV = (TextView) rowView
					.findViewById(R.id.postTime);
			postTimeTV.setText(map.get("postTime"));

			TextView titleTV = (TextView) rowView.findViewById(R.id.title);
			if (!StringUtil.isEmpty(map.get("title"))) {
				titleTV.setText(map.get("title"));
			} else {
				titleTV.setVisibility(View.GONE);
			}
			//rowView.setOnLongClickListener(new FloorLongClickListen() );
			//rowView.setOnCreateContextMenuListener( new FloorCreateContextMenuListener() );
			//rowView.set
			m.put(position, rowView);
		}
		
		
		return rowView;
	}
	

		
	/*class FloorLongClickListen implements OnLongClickListener{
		private final String uid;
		private final String content;
		private final String name;
		private final String dateTime;
		public FloorLongClickListen(String uid,String content,String name,String dateTime){
			this.uid = uid;
			this.content = content;
			this.name = name;
			this.dateTime = "1970-01-01 00:00";
		}
		@Override
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub
			//arg0.getContext()
			//arg0.get
			return false;
		}
		
	}*/
}
