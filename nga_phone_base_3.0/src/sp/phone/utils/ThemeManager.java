package sp.phone.utils;
import sp.phone.activity.R;
public class ThemeManager {
	private static ThemeManager instance = null;
	int foregroundColor[]={R.color.black,R.color.white};
	int backgroundColor[]={R.color.shit2,R.color.black};
	int mode = 0;
	static final public int MODE_NORMAL = 0;
	static final public  int MODE_NIGHT = 1;
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

	
	

}
