package sp.phone.bean;

public class Board {
	private int category;
	private String url;
	private String name;
	private int icon;
	public Board(){
		
	}
	
	
	
	public Board(int category, String url, String name, int icon) {
	
		this(url, name, icon);
		this.category = category;
	}




	public Board(String url, String name, int icon) {
		super();
		this.url = url;
		this.name = name;
		this.icon = icon;
	}



	public Board(String url, String name) {
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



	/**
	 * @return the category
	 */
	public int getCategory() {
		return category;
	}



	/**
	 * @param category the category to set
	 */
	public void setCategory(int category) {
		this.category = category;
	}
	
	
	
	

}
