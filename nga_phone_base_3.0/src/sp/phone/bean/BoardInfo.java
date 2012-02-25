package sp.phone.bean;

public class BoardInfo {
	private String url;
	private String name;
	private int icon;
	public BoardInfo(){
		
	}
	
	
	
	public BoardInfo(String url, String name, int icon) {
		super();
		this.url = url;
		this.name = name;
		this.icon = icon;
	}



	public BoardInfo(String url, String name) {
		this(url,name, 0);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	

}
