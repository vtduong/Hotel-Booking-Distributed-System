
package aspackage.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FileLogger {
	String requestType;
	Map<String, String> requestParameters;
	String requestStatus;
	String serverResonse;
	String input;
	String clientID;

	public FileLogger(String input, String requestType, Map<String, String> requestParameters, String requestStatus,
			String serverResonse) {
		this.input = input;
		this.requestParameters = requestParameters;
		this.requestType = requestType;
		this.requestStatus = requestStatus;
		this.serverResonse = serverResonse;

	}
	
	public FileLogger(String input, String requestType, String clientID, Map<String, String> requestParameters, String serverResponse) {
		this.input = input;
		this.requestType = requestType;
		this.clientID = clientID;
		this.serverResonse = serverResponse;
		this.requestParameters = requestParameters;
	}

	public void writeFiles() {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(input, true));
				out.newLine();
				out.write("=============New Session==============");
				out.newLine();
				out.write("Request Type:"+requestType);
				out.newLine();
				out.write("Request Status:"+requestStatus);
				out.newLine();
				out.write("Server Resposnse:"+serverResonse);
				out.newLine();
				out.write("requestParameters:"+requestParameters.toString());
				out.newLine();
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				out.write("Request time:"+now);
				out.newLine();
				out.write("======================================");
				out.newLine();
				out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("exception occoured" + e);
		}
	}
	
	public void writeFilesForClient() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(input, true));
			out.newLine();
			out.write("=============New Session==============");
			out.newLine();
			out.write("Request Type:"+requestType);
			out.newLine();
			out.write("Client ID:"+clientID);
			out.newLine();
			out.write("requestParameters:"+requestParameters.toString());
			out.newLine();
			out.write("Server Resposnse:"+serverResonse);
			out.newLine();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			out.write("Request time:"+now);
			out.newLine();
			out.write("======================================");
			out.newLine();
			out.flush();
		out.close();
	} catch (IOException e) {
		System.out.println("exception occoured" + e);
	}
	}

}
