package sp.phone.utils;

import java.lang.reflect.Method;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

public class ReflectionUtil {
	public static boolean actionBar_setDisplayOption(Activity activity,
			int flags){
		boolean ret = true;
		 Method setDisplayMethod;
		 Method getActionBarMethod;
		try {//
			getActionBarMethod = activity.getClass().
			 	getMethod("getActionBar");
			Object actionBar = getActionBarMethod.invoke(activity);
			
			//setDisplayMethod= Class.forName("android.app.ActionBar")
			//		.getMethod("setDisplayOptions", int.class);
			setDisplayMethod = actionBar.getClass().getMethod("setDisplayOptions", int.class);
			 setDisplayMethod.invoke(actionBar, flags);
		} catch (Exception e){
			Log.i(activity.getClass().getSimpleName(),"fail to set actionBar");
			ret = false;
		}
		return ret;
		
	}
	
	public static boolean view_setGravity(View v,
			int flags){
		boolean ret = true;
		String methodName = "setGravity";
		 Method setMethod;
		try {//
			setMethod = v.getClass().getMethod(methodName,int.class);
			 setMethod.invoke(v, flags);
		} catch (Exception e){
			Log.i(v.getClass().getSimpleName(),"fail to set gravity");
		}
		return ret;
	}

}
