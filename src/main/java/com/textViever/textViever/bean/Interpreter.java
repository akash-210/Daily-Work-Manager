package com.textViever.textViever.bean;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {

	private String text;
	private String fileName;
	private String save;
	private List<String> shortTopic = new ArrayList<>();

	public Interpreter() {
		super();

	}

	public String getTEXT() {
		return text;
	}

	public void setTEXT(String tEXT) {
		text = tEXT;
	}

	public String getFILENAME() {
		return fileName;
	}

	public void setFILENAME(String fILENAME) {
		fileName = fILENAME;
	}

	public String getSAVE() {
		return save;
	}

	public List<String> getShortTopic() {
		return shortTopic;
	}

	public void setShortTopic(String shortTopic) {
		this.shortTopic.add(shortTopic);
	}

	public void setSAVE(String sAVE) {
		save = sAVE;
	}

	@Override
	public String toString() {
		return "Interpreter [text=" + text + ", fileName=" + fileName + ", save=" + save + ", shortTopic=" + shortTopic
				+ "]";
	}

}
