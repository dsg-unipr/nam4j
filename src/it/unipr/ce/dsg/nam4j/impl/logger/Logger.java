package it.unipr.ce.dsg.nam4j.impl.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents a logger. Log messages are in JSON format.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class Logger {

	private String logFolderPath = null;
	private String logFileName = null;
	private FileWriter fstream = null;
	private BufferedWriter out = null;

	/**
	 * Constructor.
	 *
	 * @param logFolderPath
	 *            a String representing the path of the log folder
	 * @param logFileName
	 *            a String representing the name of the log file
	 */
	public Logger(String logFolderPath, String logFileName) {
		this.logFolderPath = logFolderPath;
		this.logFileName = logFileName;
		try {
			this.fstream = new FileWriter(this.logFolderPath + this.logFileName);
			this.out = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to log a message to file.
	 *
	 * @param logMessage
	 *            an Object that will JSON-serialized and appended to the log
	 *            file
	 */
	public void log(Object logMessage) {
		Gson gson = new Gson();
		try {
			String gsonString = gson.toJson(logMessage);
			this.out.append(gsonString + "\n");
			this.out.flush();
		} catch (Exception e) {
		}
	}

	/**
	 * Method to close the log file.
	 */
	public void closeLogFile() {
		try {
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
