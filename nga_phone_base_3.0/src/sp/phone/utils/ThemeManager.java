package sp.phone.utils;
import android.content.Context;
import sp.phone.activity.R;
public class ThemeManager {
	private static ThemeManager instance = null;
	int foregroundColor[]={R.color.black,R.color.white};
	int backgroundColor[]={R.color.shit2,R.color.black};
	int mode = 0;
	static final public int MODE_NORMAL = 0;
	static final public  int MODE_NIGHT = 1;
	static final public int ACTION_BAR_FLAG = 31;
	
	/*ActionBar.DISPLAY_SHOW_HOME;//2
	flags |= ActionBar.DISPLAY_USE_LOGO;//1
	flags |= ActionBar.DISPLAY_SHOW_TITLE;//8
	flags |= ActionBar.DISPLAY_HOME_AS_UP;//4*/
	
	static final public int ACTION_IF_ROOM = 1;//SHOW_AS_ACTION_IF_ROOM
	static final public int Theme_Holo = 16973931;//android.R$style.class
	public static final int Theme = 16973829;//android.R$style.class
	
	public static ThemeManager getInstance(){
		if(instance == null){
			instance = new ThemeManager();
			
		}
		return instance;
	}
	public int getForegroundColor() {
		return foregroundColor[mode];
	}

	public int getBackgroundColor() {
		return backgroundColor[mode];
	}
	public static void setInstance(ThemeManager instance) {
		ThemeManager.instance = instance;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getMode() {
		return mode;
	}

	public static void SetContextTheme(Context context){
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			context.setTheme(android.R.style.Theme);
		else
			context.setTheme(android.R.style.Theme_Holo);
	}
	
	

}
