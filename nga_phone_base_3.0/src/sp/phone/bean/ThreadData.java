package sp.phone.bean;

import java.util.List;
import java.util.Map;

public class ThreadData {
	private List<ThreadRowInfo> rowList;
	private ThreadPageInfo threadInfo;
	private Map<String,String> __F;
	private int rowNum;
	public List<ThreadRowInfo> getRowList() {
		return rowList;
	}
	public void setRowList(List<ThreadRowInfo> rowList) {
		this.rowList = rowList;
	}
	public ThreadPageInfo getThreadInfo() {
		return threadInfo;
	}
	public void setThreadInfo(ThreadPageInfo threadInfo) {
		this.threadInfo = threadInfo;
	}
	public Map<String, String> get__F() {
		return __F;
	}
	public void set__F(Map<String, String> __F) {
		this.__F = __F;
	}
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}


	
	
	
	

}
