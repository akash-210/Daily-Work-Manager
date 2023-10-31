package com.textViever.textViever;

public class SaveObject {

	private String savingDirectory;
	private String parentFolderName;
	private String subFolderName;
	private String fileName;
	private String textData;
	private String save;

	public SaveObject() {
	}

	public String getSavingDirectory() {
		return savingDirectory;
	}

	public void setSavingDirectory(String savingDirectory) {
		this.savingDirectory = savingDirectory;
	}

	public String getParentFolderName() {
		return parentFolderName;
	}

	public void setParentFolderName(String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	public String getSubFolderName() {
		return subFolderName;
	}

	public void setSubFolderName(String subFolderName) {
		this.subFolderName = subFolderName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSave() {
		return save;
	}
	

	public String getTextData() {
		return textData;
	}

	public void setTextData(String textData) {
		this.textData = textData;
	}

	public void setSave(String save) {
		this.save = save;
	}

	@Override
	public String toString() {
		return "SaveObject [savingDirectory=" + savingDirectory + ", parentFolderName=" + parentFolderName
				+ ", subFolderName=" + subFolderName + ", fileName=" + fileName + ", textData=" + textData + ", save="
				+ save + "]";
	}


	
	

}
