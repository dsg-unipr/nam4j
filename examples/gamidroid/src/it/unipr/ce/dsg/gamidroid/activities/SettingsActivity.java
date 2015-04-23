package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.interfaces.INetworkChosen;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.gamidroid.utils.ExpandableListAdapter;
import it.unipr.ce.dsg.gamidroid.utils.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

/**
 * <p>
 * This class represents an Activity that allows the user to manage the settings
 * of the application.
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

public class SettingsActivity extends Activity implements INetworkChosen {

	public static String TAG = "SettingsActivity";
	
	Context mContext;

	private FileManager fileManager;
	private EditText textIp;

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		
		mContext = this;

		overridePendingTransition(R.anim.animate_left_in, R.anim.animate_right_out);

		fileManager = FileManager.getFileManager();

		// Get the listview
		expListView = (ExpandableListView) findViewById(R.id.networkSelector);

		// Preparing list data
		createList();
		
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

		// Setting list adapter
		expListView.setAdapter(listAdapter);

		Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);

		buttonConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText textIp = (EditText) findViewById(R.id.editIpBoostrap);
				String ip = textIp.getText().toString();
				fileManager.updatePeerConfigFile(ip);
				onBackPressed();
				finish();
			}
		});

		Button backButton = (Button) findViewById(R.id.backButtonSetting);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		textIp = (EditText) findViewById(R.id.editIpBoostrap);

		// Load file containing bootstrap address
		File sd = new File(Environment.getExternalStorageDirectory()
				+ Constants.CONFIGURATION_FILES_PATH);
		File file = new File(sd, Constants.PEER_CONFIGURATION_FILE_NAME);

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file.getPath()));
			String ipBoostrap = properties.getProperty("bootstrap_peer");
			textIp.setText(ipBoostrap);
		} catch (FileNotFoundException e1) {
			Log.e(SettingsActivity.TAG, e1.getMessage()
					+ " (reading file containing bootstrap properties)");
			e1.printStackTrace();
		} catch (IOException e1) {
			Log.e(SettingsActivity.TAG, e1.getMessage()
					+ " (reading file containing bootstrap properties)");
			e1.printStackTrace();
		}

	}

	/*
	 * Preparing the list data
	 */
	private void createList() {
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK, "");
		
		// Adding child data
		listDataHeader.add("Network" + "_" + currentNetwork);
		// listDataHeader.add("Header 2");
		// listDataHeader.add("Header 3");

		// Adding child data
		List<String> networks = new ArrayList<String>();
		networks.add(Constants.MESH);
		networks.add(Constants.CHORD);

		// List<String> secondList = new ArrayList<String>();
		// secondList.add("First element");
		// secondList.add("Second element");

		// List<String> thirdList = new ArrayList<String>();
		// thirdList.add("Second element");
		// thirdList.add("Second element");

		listDataChild.put(listDataHeader.get(0), networks);
		// listDataChild.put(listDataHeader.get(1), secondList);
		// listDataChild.put(listDataHeader.get(2), thirdList);
	}

	@Override
	public void networkChosen(String network, int expandedGroup) {
		
		expListView.collapseGroup(expandedGroup);
		
		SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(Constants.NETWORK, network);
		editor.commit();

		createList();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.animate_left_in, R.anim.animate_right_out);
	}

}
