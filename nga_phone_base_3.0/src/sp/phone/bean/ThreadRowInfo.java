package sp.phone.bean;

import java.util.List;
import java.util.Map;

public class ThreadRowInfo {
	
	//private int tid;
	//private String fid;
	// private int quote_from;
	 //private String quote_to;
	// private String icon;
	// private String titlefont;
	 private String author;
	 private int authorid;
	 private String subject;
	// private int type;
	// private int type_2;
	private String postdate;
	// private int lastpost;
	// private String lastposter;
	//private int replies;
	// private int locked;
	// private int digest;
	// private int ifupload;
	// private int lastmodify;
	// private int recommend;
	private int pid;
	private String alterinfo;
	private String content;
	private int lou;
	//private int postdatetimestamp;
	//private int content_length;
	private Map<String,Attachment>attachs;
	//private int credit;
	//private String reputation;
	//private int groupid;
	//private String lpic;
	private String level;
	//private int gp_lesser;
	//private int yz;
	//private String js_escap_site;
	//private String js_escap_honor;
	private String js_escap_avatar;
	//private int regdate;
	//private String mute_time;
	private int postnum;
	private int aurvrc;
	//private int money;
	//private int thisvisit;
	private String signature;
	//private String nickname;
	private List<ThreadRowInfo> comments;
	
	
	
	
	public Map<String, Attachment> getAttachs() {
		return attachs;
	}
	public void setAttachs(Map<String, Attachment> attachs) {
		this.attachs = attachs;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getJs_escap_avatar() {
		return js_escap_avatar;
	}
	public void setJs_escap_avatar(String js_escap_avatar) {
		this.js_escap_avatar = js_escap_avatar;
	}
	public String getAlterinfo() {
		return alterinfo;
	}
	public void setAlterinfo(String alterinfo) {
		this.alterinfo = alterinfo;
	}
	public int getLou() {
		return lou;
	}
	public void setLou(int lou) {
		this.lou = lou;
	}
	public int getAuthorid() {
		return authorid;
	}
	public void setAuthorid(int authorid) {
		this.authorid = authorid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPostdate() {
		return postdate;
	}
	public void setPostdate(String postdate) {
		this.postdate = postdate;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<ThreadRowInfo> getComments() {
		return comments;
	}
	public void setComments(List<ThreadRowInfo> comments) {
		this.comments = comments;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getPostnum() {
		return postnum;
	}
	public void setPostnum(int postnum) {
		this.postnum = postnum;
	}
	public int getAurvrc() {
		return aurvrc;
	}
	public void setAurvrc(int aurvrc) {
		this.aurvrc = aurvrc;
	}


	



	
	
}
