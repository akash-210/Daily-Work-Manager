package com.textViever.textViever;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.textViever.textViever.service.Console;

@Controller("/")
public class HomeController {

	private String loc = "";
	private String currentlocation;
	/*private boolean createtxtPage = false;*/
	private File globalFile = null;
	
	@Autowired
	private Console console;

	@RequestMapping(value = "home")
	public String homepage(Model model) throws IOException {
		File file = new File("task.txt");
		FileReader reader = new FileReader(file);
		BufferedReader buffreader = new BufferedReader(reader);
		String value;
		while ((value = buffreader.readLine()) != null) {
			String[] splitvalue=value.split("<->");
			if (splitvalue[0].equals("")) {
				continue;
			}
			switch (splitvalue[0].trim().toUpperCase()) {
			case "SAVINGDIRECTORY":
				if(splitvalue.length > 1) {
					loc =splitvalue[1];
				}
				else {
					loc=console.currlocationFromConsole;
				}
				break;
			}
		}
		buffreader.close();
		globalFile = new File(loc);
		currentlocation = loc;
		String[] folders = globalFile.list();
		model.addAttribute("loc", loc);
		model.addAttribute("folder", folders);
		return "Home";
	}

	@RequestMapping(value = "inside/{name}")
	public String allFilesBySelection(@PathVariable("name") String locationselected, Model model) {
		currentlocation = currentlocation + "\\" + locationselected;
		globalFile = new File(currentlocation);
		String[] folders = globalFile.list();
		model.addAttribute("folder", folders);
		return "NavBar::load";
	}

	@ResponseBody
	@RequestMapping(value = "feedtext/{filename}")
	public String feedtext(@PathVariable("filename") String filename, Model model) throws IOException {
		String decodedURL = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());
		String location = currentlocation + "\\" + decodedURL;
		File file = new File(location);
		FileReader reader = new FileReader(file);
		BufferedReader buffreader = new BufferedReader(reader);
		String value;
		StringBuilder hj = new StringBuilder();
		while ((value = buffreader.readLine()) != null) {
			if (value.equals("")) {
				continue;
			}
			hj.append(value);
			hj.append(System.lineSeparator());
		}
		System.out.println(hj.toString());
		buffreader.close();
		return hj.toString();
	}

	@RequestMapping("back")
	public String back(Model model) {
		String storeaddress;
		if (!currentlocation.equals((storeaddress = loc))) {
			storeaddress = globalFile.getParent();
			globalFile = new File(storeaddress);
		}
		currentlocation = storeaddress;
		String[] folders = globalFile.list();
		model.addAttribute("folder", folders);
		return "NavBar::load";
	}

	@ResponseBody
	@RequestMapping("create")
	public String create(Model model) throws IOException {
		File file = new File("task.txt");
		FileReader reader = new FileReader(file);
		BufferedReader buffreader = new BufferedReader(reader);
		String value;
		StringBuilder builder = new StringBuilder();
		boolean pass = false;
		while ((value = buffreader.readLine()) != null) {

			if (!pass && value.equals("")) {
				continue;
			}
			switch (value.trim().toLowerCase()) {
			case "text*":
				pass = true;
				continue;
			case "stop*":
				pass = false;
				continue;
			}
			if (pass) {
				builder.append(value);
				builder.append(System.lineSeparator());
			}

		}
		buffreader.close();
/*		createtxtPage = true;*/
		return builder.toString();
	}

	@ResponseBody
	@RequestMapping("save")
	public String save(Model model, @RequestBody SaveObject data) throws IOException {
		String textdata = formatParagraph(data.getTextData());
		data.setTextData(textdata);
		String finaltxt = getTextDataFromDataObject(data);
		FileWriter writer = new FileWriter("task.txt");
		writer.write(finaltxt);
		writer.close();
		System.out.println(data);
/*		createtxtPage = true;*/
		return textdata;
	}

	private String getTextDataFromDataObject(SaveObject data) {
		StringBuilder builder = new StringBuilder();
		String nextline = System.lineSeparator();
		builder.append("SAVINGDIRECTORY<->" + data.getSavingDirectory()).append(nextline)
				.append("PARENTFOLDERNAME<->" + data.getParentFolderName()).append(nextline)
				.append("SUBFOLDERNAME<->" + data.getSubFolderName()).append(nextline).append("START*").append(nextline)
				.append("FILENAME<->" + data.getFileName()).append(nextline).append("SAVE<->" + data.getSave())
				.append(nextline).append("TEXT*").append(nextline).append(data.getTextData()).append(nextline)
				.append("STOP*").append(nextline);
		return builder.toString();
	}

	private String formatParagraph(String paragraph) {
		StringBuilder buffer = new StringBuilder();
		String[] newline = paragraph.split("\\n");
		Pattern pattern = Pattern.compile("^(\\s+)");
		String allSpaceBeforeFirstCharacter = null;
		int totalength = newline.length;
		for (String oneSentence : newline) {
			allSpaceBeforeFirstCharacter = "";
			Matcher matcher = pattern.matcher(oneSentence);
			if (matcher.find()) {
				allSpaceBeforeFirstCharacter = matcher.group(1); // Group 1 contains the leading spaces
			}
			String formateLine = getFormateLine(oneSentence.replaceAll("\\s+", " ").trim());
			buffer.append(allSpaceBeforeFirstCharacter).append(formateLine);
			if (totalength-- > 1) {
				buffer.append(System.lineSeparator());
			}
		}
		return buffer.toString();
	}

	private String getFormateLine(String line) {
		StringBuffer result=new StringBuffer();
		String[] splitbyDot=line.replaceAll("\\.", ".~").split("~");
		for (String word : splitbyDot) {
			word=word.trim();
			if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1)).append(" ");
                }else {
                	result.append(" ");
                }
            }
		} 
		return result.toString().trim();		
	}
}
