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
	private ProgressDialog pd = null;

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
		
		notice("����", error,context);
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

	        //ft.commit();
			df.show(fm, dialogTag);
			this.df = df;
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
				pd = ProgressDialog.show(c, title, content);
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
			if(pd != null)
			{
				try{
				pd.dismiss();
				}catch(Exception e){
					Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));
				}
				pd = null;
				
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
