package com.textViever.textViever.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.stereotype.Service;
import com.textViever.textViever.bean.Interpreter;
import com.textViever.textViever.bean.LastModified;

@Service
public class Console {
   public static String currlocationFromConsole=null;
	public static void startClass() throws IOException {

		TimerTask task = new TimerTask() {
			private com.textViever.textViever.bean.LastModified LastModified;
			private long lastFileModifiedTIME;
			private String lastFilesaveName;

			@Override
			public void run() {
				try {
					if (lastFileModifiedTIME == 0L) {
						LastModified = runSaving(lastFilesaveName);
						lastFileModifiedTIME = LastModified.getLastModifiedfileTime();
						lastFilesaveName = LastModified.getLastModifiedFileName();
					}
					checkIfFileModified();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private void checkIfFileModified() throws IOException {
				long newFileModifiedTIME = 0 ;
				boolean pass=true;
				while(pass) {
					if((newFileModifiedTIME = new File("task.txt").lastModified()) != 0) {
						pass=false;
					}
				}
				
				if (this.lastFileModifiedTIME != newFileModifiedTIME) {
					LastModified = runSaving(lastFilesaveName);
					this.lastFilesaveName = LastModified.getLastModifiedFileName();
					this.lastFileModifiedTIME = newFileModifiedTIME;
				}
			}
		};

		// repeat the check every second
		Timer timer = new Timer();
		timer.schedule(task, new Date(), 1000);
	}

	private static LastModified runSaving(String lastFilesaveName) throws IOException {
		List<Interpreter> arraylist = new ArrayList<Interpreter>();
		Map<String, String> textSavingDetails = new HashMap<String, String>();
		FileReader file = new FileReader("task.txt");
		BufferedReader buffer = new BufferedReader(file);
		Interpreter object = null;

		String value;
		boolean handler = false;
		while ((value = buffer.readLine()) != null) {
			if (value.equals("") && !handler) {
				continue;
			}
			String ansere = value;
			String[] arrStr = ansere.split("<->");

			switch (arrStr.length) {
			case 1:
				switch (arrStr[0].trim().toLowerCase()) {

				case "start*":
					object = new Interpreter();
					continue;
				case "stop*":
					arraylist.add(object);
					object = new Interpreter();
					handler = false;
					continue;
				}
			}

			if (arrStr.length > 1) {
				switch (arrStr[0].trim().toLowerCase()) {
				case "filename":
					object.setFILENAME(arrStr[1]);
					continue;
				case "topic":
					object.setShortTopic(arrStr[1].trim());
					continue;
				case "save":
					object.setSAVE(arrStr[1]);
					continue;
				case "savingdirectory":
				case "parentfoldername":
				case "subfoldername":
					textSavingDetails.put(arrStr[0].toLowerCase(), arrStr[1]);
					continue;
				}
			}
			if (arrStr[0].trim().equalsIgnoreCase("text*") || handler) {
				if (handler) {
					String newstring = object.getTEXT();
					newstring += " " + arrStr[0] + System.lineSeparator();
					object.setTEXT(newstring);
					continue;
				} else if (arrStr[0].trim().equalsIgnoreCase("text*")) {
					object.setTEXT("");
					handler = true;
					continue;
				}
			}
		}
		buffer.close();//latest added line aug 12
		String l = getdefaulLocation(textSavingDetails);
		String lastModifiedFileName = saveLocationModification(l, arraylist, textSavingDetails, lastFilesaveName);
		long lastModifiedfileTime = new File("task.txt").lastModified();
		
		return new LastModified(lastModifiedFileName, lastModifiedfileTime);

	}

	public static String getdefaulLocation(Map<String, String> textSavingDetails) {
		String l;
		String currentuser = System.getProperty("user.name");
		if (textSavingDetails.get("savingdirectory")!=null && !textSavingDetails.get("savingdirectory").isEmpty()) {
			l = textSavingDetails.get("savingdirectory");
		} else {
			String defaultLocation = "C:\\Users\\***\\Documents\\Tasks\\ end";
			l = defaultLocation.replace("***", currentuser).replace(" end", "");
		}
		File file=checkFileLocationStrict(l);
		currlocationFromConsole=file.getAbsolutePath();
		return file.getAbsolutePath();
	}

	public static String saveLocationModification(String location, List<Interpreter> arraylist,
			Map<String, String> textSavingDetails, String lastFilesaveName) throws IOException {
		System.out.println(location);
		String finalsavinglocation = location;
		int j = 0;
		for (String keys : textSavingDetails.keySet()) {
			if (keys.equals("savingdirectory")) {
				continue;
			}
			String[] prenames = { "parentfoldername", "subfoldername" };
			finalsavinglocation = finalsavinglocation + "\\"
					+ getComputedName(textSavingDetails.get(prenames[j]), null);
			
			j++;
		}
		File filelocation = new File(finalsavinglocation);
		if (!filelocation.isDirectory()) {
			filelocation.mkdirs();
			System.out.println("The default directory has now been created." + filelocation.getAbsolutePath() + "\n");
		}
		// from this file Saving with data starts
		for (int i = 0; i < arraylist.size(); i++) {
			if (!arraylist.get(i).getSAVE().equalsIgnoreCase("@YES")) {
				continue;
			}
			String txtName = getComputedName(arraylist.get(i).getFILENAME(), arraylist.get(i).getShortTopic());
			String textname = finalsavinglocation + "\\" + txtName + ".txt";
			if (arraylist.get(i).getFILENAME().equalsIgnoreCase("@TODAY")) {
				if (lastFilesaveName == null) {
					lastFilesaveName = textname;
				} else {
					if (!lastFilesaveName.equals(textname)) {
						String[] first = lastFilesaveName.split(" ");
						String[] second = textname.split(" ");
						if (first[1].equals(second[1])) {
							File file = new File(lastFilesaveName);
							try {
								file.delete();
							} catch (Exception e) {
								System.out.println(e + "line no 189");
							}
							lastFilesaveName = textname;
						}else {
							//change when next day comes.
							lastFilesaveName = textname;
						}
						
					}

				}
			}

			FileWriter writer = new FileWriter(textname);
			writer.write(arraylist.get(i).getTEXT());
			writer.close();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa");
			Date date = new Date();
			System.out.println("The file location for saving is " + finalsavinglocation + "\\" + txtName + ".txt");
			System.out.println("The file has been successfully modified. TIME = " + dateFormat.format(date)
			+ System.lineSeparator());
		}
		return lastFilesaveName;

	}

	private static File checkFileLocationStrict(String finalSavingLocation) {
		 File fileLocation = new File(finalSavingLocation);
		    Scanner scanner = new Scanner(System.in);
		    boolean message;
		    while (true) {
		        if ((message=fileLocation.exists()) || fileLocation.mkdirs()) {
		            // directory will exists or has been created
		            System.out.println("The directory has been "+((message)?"Present":"Created")+" at: " + fileLocation.getAbsolutePath() + "\n");
		            break; // esits the while loop when a valid location is founded.
		        } else {
		        	System.out.println("");
		            System.out.println("Error: Could not create a new folder at the given location. There might be a permission issue.");
		            System.err.print("Please enter a new valid location here for the directory: ");
		            String newDirectory = scanner.nextLine();
		            fileLocation = new File(newDirectory);
		        }
		    }
		    scanner.close();
		    return fileLocation;
	}

	private static String getComputedName(String filename, List<String> list) {
		int c;
		StringBuilder sb = new StringBuilder(filename);
		String[] dateArray = String.valueOf(new Date()).split(" ");
		String lowerCaseFilename = filename.trim().toLowerCase();

		switch (lowerCaseFilename) {
		case "@today":
			sb.setLength(0);
			sb.append(dateArray[2]).append(" ").append(dateArray[1]).append(" ").append(dateArray[0]);
	    /*    sb.append(" ( ");*/
			sb.append(" ");
	        c = 0;
	        for (String string : list) {
	          if (string.contains("*")) {
	            if (c == 0) {
	              sb.append(getFirstLetterCapital(string).replace("*", ""));
	              c++;
	              continue;
	            } 
	            if (c > 0)
	              sb.append(" , ").append(getFirstLetterCapital(string).replace("*", "")); 
	          } 
	        } 
	     /*   sb.append(" )");*/
			break;
		case "@tsft":
			sb.setLength(0);
			sb.append(dateArray[2]).append(" ").append(dateArray[1]).append("_Tasks scheduled for tomorrow");
			break;
		case "@thisyear":
			sb.setLength(0);
			sb.append("Year ").append(dateArray[5]);
			break;
		case "@thismonth":
			sb.setLength(0);
			sb.append(dateArray[1]);
			break;
		}
		return sb.toString();
	}

	private static String getFirstLetterCapital(String string) {
		String a = string.toLowerCase();
		return a.toUpperCase().charAt(0) + a.substring(1, a.length());
	}
}
