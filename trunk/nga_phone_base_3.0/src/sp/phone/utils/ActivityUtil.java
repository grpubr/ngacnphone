package sp.phone.utils;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class ActivityUtil {


	static ActivityUtil instance;
	static final String TAG = ActivityUtil.class.getSimpleName();
	static Object lock= new Object();
	public static ActivityUtil getInstance(){
		if(instance == null){
			instance = new ActivityUtil();
		}
		return instance;//instance;
		
	}
	private ActivityUtil(){
		
	}
	private ProgressDialog proDialog;
	private DialogFragment df = null;

	/*private Context context;

	public ActivityUtil(Context context) {
		this.context = context;
	}*/
	public void noticeSaying(Context context){
		
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			/*notice("加速模式", str.split(";")[0]
					+ "-----" + str.split(";")[1]);*/
			notice("",str.replace(";", "-----"),context);
		} else {
			notice("", str,context);
		}
	}
	
	public void noticeError(String error,Context context){
		
		notice("错误", error,context);
	}

	private void notice(String title, String content,Context c) {
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("content", content);
		message.obj = c;
		message.setData(b);
		handler.sendMessage(message);
	}

	private Handler handler = new Handler() {
		public void handleMessage (final Message msg) {
			Log.d(TAG, "handle Message");
			Context context = (Context) msg.obj;
			Bundle b = msg.getData();
			if (b != null) {
				String title = b.getString("title");
				String content = b.getString("content");
				synchronized( lock){

					try{
						final String tag = "saying"; 
						DialogFragment df = new SayingDialogFragment(); 
						df.setArguments(b);
						
						FragmentActivity fa = (FragmentActivity)context;
						FragmentManager fm = fa.getSupportFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();

				        Fragment prev = fm.findFragmentByTag(tag);
				        if (prev != null) {
				            ft.remove(prev);
				        }
				        ft.addToBackStack(null);
						df.show(fa.getSupportFragmentManager(), tag);
						ActivityUtil.this.df = df;
						/*
					proDialog = ProgressDialog.show(context, title, content);
					proDialog.setCanceledOnTouchOutside(true);*/
					}catch(Exception e){
						Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
					}

				}//sync
			}
		};
	};

	public void dismiss() {
		synchronized (lock) {
			if (proDialog != null) {
				Log.d(TAG, "dissmiss dialog");
				try{
				proDialog.dismiss();
				}catch(Exception e){
					
				}
				proDialog = null;
			}
			if(df !=null){
				df.dismiss();
				df= null;
			}
		}
	}
	
	class SayingDialogFragment extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
		    //
			Bundle b = getArguments();
			if (b != null) {
				String title = b.getString("title");
				String content = b.getString("content");
				dialog.setTitle(title);
				dialog.setMessage(content);
			}
		    
		    dialog.setCanceledOnTouchOutside(true);
		    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    dialog.setIndeterminate(true);
		    dialog.setCancelable(true);
		    // etc...
		    this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
		    return dialog;
		}
		
	}

}
