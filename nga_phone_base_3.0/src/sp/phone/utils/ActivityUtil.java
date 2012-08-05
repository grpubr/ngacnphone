package sp.phone.utils;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class ActivityUtil {

	public static boolean isGreaterThan_3_0(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB;
	}
	public static boolean isGreaterThan_2_2(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.FROYO;
	}
	public static boolean isGreaterThan_2_1(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR;
	}
	public static boolean isGreaterThan_1_6(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT;
	}
	public static boolean isGreaterThan_2_3(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD;
	}
	public static boolean isGreaterThan_2_3_3(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
	}
	public static boolean isGreaterThan_4_0(){
		return android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
	public static boolean islessThan_4_1(){
		return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN;
	}
	public static boolean isMeizu(){
		return "Meizu".equalsIgnoreCase(android.os.Build.MANUFACTURER);
	}
	
	static ActivityUtil instance;
	static final String TAG = ActivityUtil.class.getSimpleName();
	static final String dialogTag = "saying"; 
	static Object lock= new Object();
	public static ActivityUtil getInstance(){
		if(instance == null){
			instance = new ActivityUtil();
		}
		return instance;//instance;
		
	}
	private ActivityUtil(){
		
	}

	private DialogFragment df = null;

	public void noticeSaying(Context context){
		
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			notice("",str.replace(";", "-----"),context);
		} else {
			notice("", str,context);
		}
	}
	
	static public String getSaying(){
		String str = StringUtil.getSaying();
		if (str.indexOf(";") != -1) {
			str = str.replace(";", "-----");
		} 
		
		return str;
		
	}
	
	public void noticeError(String error,Context context){
		HttpUtil.switchServer();
		notice("´íÎó", error,context);
	}

	private void notice(String title, String content,Context c) {

		if(c == null)
			return;
		Log.d(TAG, "saying dialog");
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("content", content);
		synchronized (lock) {
			try{
			
			DialogFragment df = new SayingDialogFragment(); 
			df.setArguments(b);
			
			FragmentActivity fa = (FragmentActivity)c;
			FragmentManager fm = fa.getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

	       	Fragment prev = fm.findFragmentByTag(dialogTag);
	        if (prev != null) {
	            ft.remove(prev);
	        }

	        ft.commit();
			df.show(fm, dialogTag);
			this.df = df;
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));

			}
			
		}

	}

	
	public void clear(){
		synchronized (lock) {
			this.df = null;
		}
	}
	public void dismiss() {

		synchronized (lock) {
			Log.d(TAG, "trying dissmiss dialog");


			if (df != null && df.getActivity() != null) {
				Log.d(TAG, "dissmiss dialog");
				
				try{
				FragmentActivity fa = (FragmentActivity)(df.getActivity());
				FragmentManager fm = fa.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();

		        Fragment prev = fm.findFragmentByTag(dialogTag);
		        if (prev != null) {
		            ft.remove(prev);
		            
		        }

		        ft.commit();
				}catch(Exception e){
					Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
				}
		       
		        df = null;
				

			} else {
				df = null;
			}

		}
	}

	
	public static class SayingDialogFragment extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
		    //
			Bundle b = getArguments();
			if (b != null) {
				String title = b.getString("title");
				String content = b.getString("content");
				dialog.setTitle(title);
				if(StringUtil.isEmpty(content))
					content = ActivityUtil.getSaying();
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
