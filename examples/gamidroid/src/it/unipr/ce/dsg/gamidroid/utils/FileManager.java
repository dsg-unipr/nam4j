package it.unipr.ce.dsg.gamidroid.utils;

import it.unipr.ce.dsg.gamidroid.activities.NAM4JAndroidActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import android.os.Environment;
import android.util.Log;

/**
 * <p>
 * This class creates the Sip2Peer configuration files for the peers and/or the
 * bootstrap and stores them on the device's SD card.
 * </p>
 * 
 * <p>
 * Copyright (c) 2013, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */
public class FileManager {

	private static final String TAG = "FileManager";

	private String cfg_path;
	private String bs_cfg_path;

	private File sd;

	private static FileManager fileManager;

	private FileManager() {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			sd = new File(Environment.getExternalStorageDirectory()
					+ Constants.CONFIGURATION_FILES_PATH);

			cfg_path = Constants.PEER_CONFIGURATION_FILE_NAME;
			bs_cfg_path = Constants.BOOTSTRAP_CONFIGURATION_FILE_NAME;

			if (!sd.exists()) {
				sd.mkdirs();
			}
		}

	}

	public void createFiles() {

		File file;
		file = new File(sd, cfg_path);

		if (!file.exists()) {
			createPeerConfigFile(file);
			Log.d(NAM4JAndroidActivity.TAG, "Creating configuration file");
		}

		file = new File(sd, bs_cfg_path);

		if (!file.exists()) {
			createBsConfigFile(file);
			Log.d(NAM4JAndroidActivity.TAG,
					"Creating bootstrap configuration file");
		}
	}

	public void createPeerConfigFile(File file) {

		BufferedWriter writer;

		if (!file.exists()) {
			try {
				file.createNewFile();
				writer = new BufferedWriter(new FileWriter(file, true));

				/* Generating a random port and name */
				Random ran = new Random();
				int port = 1024 + ran.nextInt(9999 - 1024);

				String peerName = Utils.generateRandomString();
				
				writer.write("via_addr=AUTO-CONFIGURATION\n"
						+ "host_port=" + port + "\n"
						+ "peer_name=" + peerName + "\n"
						+ "#format_message=text\n"
						+ "test_address_reachability=no\n"
						+ "#list_path=list/\n"
						+ "bootstrap_peer=bootstrap@" + Constants.DEFAULT_BOOTSTRAP_ADDRESS + ":" + Constants.DEFAULT_BOOTSTRAP_PORT + "\n"
						+ "req_npeer=10\n"
						+ "keepalive_time=5000\n"
						+ "debug_level=0");

				writer.close();

			} catch (FileNotFoundException e) {
				Log.e(NAM4JAndroidActivity.TAG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(NAM4JAndroidActivity.TAG, e.getMessage());
				e.printStackTrace();
			}
		}

	}

	/**
	 * Method to update the bootstrap's contact information in the configuration
	 * file.
	 * 
	 * @param ip
	 *            The new contact address in the form IP:PORT of the bootstrap
	 */
	public void updatePeerConfigFile(String ip) {

		BufferedWriter writer;
		Log.d(FileManager.TAG, "Peer path: " + sd.getPath());
		File file = new File(sd, cfg_path);

		file.delete();
		try {
			file.createNewFile();
			Log.d(NAM4JAndroidActivity.TAG, "Updating peer config file: "
					+ file.getPath());
		} catch (IOException e1) {
			Log.e(NAM4JAndroidActivity.TAG, e1.getMessage());
			e1.printStackTrace();
		}
		try {
			writer = new BufferedWriter(new FileWriter(file, true));

			/*
			 * Generating a random port different than the bootstrap's one, and
			 * a random name
			 */
			int port;
			do {
				Random ran = new Random();
				port = 1024 + ran.nextInt(9999 - 1024);
			} while (port != Integer.parseInt(ip.split(":")[1]));

			String peerName = Utils.generateRandomString();

			writer.write("via_addr=AUTO-CONFIGURATION\n"
					+ "host_port=" + port + "\n"
					+ "peer_name=" + peerName + "\n"
					+ "#format_message=text\n"
					+ "test_address_reachability=no\n"
					+ "#list_path=list/\n"
					+ "bootstrap_peer=" + ip + "\n"
					+ "req_npeer=10\n"
					+ "keepalive_time=5000\n"
					+ "debug_level=0");

			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			Log.e(NAM4JAndroidActivity.TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(NAM4JAndroidActivity.TAG, e.getMessage());
			e.printStackTrace();
		}

	}

	public void createBsConfigFile(File file) {

		BufferedWriter writer;

		if (!file.exists()) {
			try {
				file.createNewFile();
				writer = new BufferedWriter(new FileWriter(file, true));
				writer.write("via_addr=AUTO-CONFIGURATION\n"
						+ "host_port=5080\n"
						+ "peer_name=bootstrap\n"
						+ "#format_message=text\n"
						+ "test_address_reachability=no\n"
						+ "#list_path=list/\n"
						+ "#log_path=log/\n"
						+ "keepalive_time=5000\n"
						+ "debug_level=0");
				writer.close();

			} catch (FileNotFoundException e) {
				Log.e(NAM4JAndroidActivity.TAG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(NAM4JAndroidActivity.TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static FileManager getFileManager() {

		if (fileManager == null) {
			fileManager = new FileManager();
		}
		return fileManager;
	}

}
