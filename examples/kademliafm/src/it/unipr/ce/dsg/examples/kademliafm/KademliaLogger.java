package it.unipr.ce.dsg.examples.kademliafm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class KademliaLogger {
	
	private String logFolderPath = null;
	private FileWriter fstream = null;
	private BufferedWriter out = null;
	
	public KademliaLogger(String logFolderPath) {
		this.logFolderPath = logFolderPath;
		try {
			this.fstream = new FileWriter(this.logFolderPath + "KademliaLogs.txt");
			this.out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void log(Object logMessage) {
		Gson gson = new Gson();	
		try {			
			String json = gson.toJson(logMessage);
			this.out.append(json + "\n");
			this.out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeLogFile() {
		try {
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
