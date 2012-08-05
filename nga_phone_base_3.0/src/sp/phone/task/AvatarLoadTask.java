package sp.phone.task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class AvatarLoadTask extends AsyncTask<String, Integer, Bitmap> {
	static final String TAG = AvatarLoadTask.class.getSimpleName();
	final ImageView view ;
	final ZipFile zipFile;
	final boolean downImg;
	final int floor;
	
	public AvatarLoadTask(ImageView view,ZipFile zipFile,boolean downImg, int floor) {
		super();
		this.view = view;
		this.zipFile  = zipFile;
		this.downImg = downImg;
		this.floor = floor;
	}


	@Override
	protected Bitmap doInBackground(String... params) {
		
		final String avatarUrl = params[0];
		final String avatarLocalPath = params[1];


		Bitmap bitmap = null;
		InputStream is = null;

		try {
			is = new FileInputStream(avatarLocalPath);
		} catch (FileNotFoundException e) {
			Log.d(TAG,
					"avatar:" + avatarLocalPath + " is not cached" );
		}
		

		
		if (is == null && downImg) {
			HttpUtil.downImage(avatarUrl, avatarLocalPath);
			try {
				is = new FileInputStream(avatarLocalPath);
				Log.d(TAG,
						"download avatar from " + avatarUrl);
			
			} catch (FileNotFoundException e) {
				Log.d(TAG,
						"avatar " + avatarUrl + " is failed to download" );
			}
		}
		
		if(is !=null){
			Log.d(TAG,"load avatar from file: " + avatarLocalPath);
			bitmap = ImageUtil.loadAvatarFromSdcard(avatarLocalPath);
		}
		
		
		
		return bitmap;
	}


	@Override
	protected void onPostExecute(Bitmap result) {
		if(result !=null){
			int floor =(Integer) view.getTag();
			if(floor == this.floor)
				view.setImageBitmap(result);
		}
	}
	
	

}
