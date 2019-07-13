package vspackage.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vspackage.config.Config;

public class Logger {
	
	private String userID;
	private String path;
	
	public static int ERROR = 0;
	public static int DEBUG = 1;
	public static int INFO = 2;
	public static int TRACE = 3;
	public static int WARNING = -1;
	
	
	public Logger(String userID) {
		this.userID = userID;
		try {	
			// user must be a server
			if(userID.contains("MTL")) {
				path = Config.getProperty("montreal_server_log");
			}
				
			else if(userID.contains("OTW")) {
				path = Config.getProperty("ottawa_server_log");
			}
				
			else if(userID.contains("TOR")) {
				path = Config.getProperty("toronto_server_log");
			}
			
		} catch(IOException e) {
			System.out.println("Unable to create a Logger instance");
		}
		
	}
	
	public Logger(String userID, boolean clientSide) {
		this.userID = userID;
		try {	
			
			path = Config.getProperty("clientLogPath") + userID;
			File file = new File(path);
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
		} catch(IOException e) {
			System.out.println("Unable to create a Logger instance");
		}
		
	}
	
	public void log(int code, String eventParam) throws IOException {
		
		String type = "";
		boolean hasToAppend = true;
		
		FileWriter fileWrite = new FileWriter(path, hasToAppend);
		BufferedWriter writer = new BufferedWriter(fileWrite);
		
		
		if(code == 0)
			type = "ERROR";
		
		else if(code == 1)
			type = "DEBUG";
		
		else if(code == 2) 
			type = "INFO";
		
		else if(code == 3)
			type = "TRACE";
		
		else if(code == -1)
			type = "WARNING";
		
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");  
		Date date = new Date(System.currentTimeMillis());  
		
		
		writer.write(formatter.format(date) + " " + type + " " +
				userID + " " + eventParam);
		
		writer.newLine();
		
		writer.flush();
		fileWrite.flush();
		
		writer.close();
		fileWrite.close();
		
	}
}
