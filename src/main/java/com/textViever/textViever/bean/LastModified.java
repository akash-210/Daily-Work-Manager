package com.textViever.textViever.bean;

public class LastModified {

	private String lastModifiedFileName;
	private long lastModifiedfileTime;

	
	
	public LastModified(String lastModifiedFileName, long lastModifiedfileTime) {
		super();
		this.lastModifiedFileName = lastModifiedFileName;
		this.lastModifiedfileTime = lastModifiedfileTime;
	}

	public String getLastModifiedFileName() {
		return lastModifiedFileName;
	}

	public void setLastModifiedFileName(String lastModifiedFileName) {
		this.lastModifiedFileName = lastModifiedFileName;
	}

	public long getLastModifiedfileTime() {
		return lastModifiedfileTime;
	}

	public void setLastModifiedfileTime(long lastModifiedfileTime) {
		this.lastModifiedfileTime = lastModifiedfileTime;
	}

	@Override
	public String toString() {
		return "LastModified [lastModifiedFileName=" + lastModifiedFileName + ", lastModifiedfileTime="
				+ lastModifiedfileTime + "]";
	}

}
