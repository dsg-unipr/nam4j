package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;
import it.unipr.ce.dsg.gamidroid.ontology.FloorStruct;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2pchord.resource.ResourceDescriptor;
import it.unipr.ce.dsg.s2pchord.resource.ResourceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * <p>
 * This class represents an Activity that allows the search for building
 * notifications in the network.
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
public class BuildingLookupActivity extends ListActivity implements ResourceListener, IEventListener {
	public static String TAG = "BuildingLookupActivity";
	Context mContext;
	private List<FloorStruct> floors;
	private String address;
	TextView tv;
	private static String buildingMessageType = "BuildingNotification";
	Context context;
	TextView titleTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.building_lookup);
		mContext = this;
		
		overridePendingTransition(R.anim.animate_left_in, R.anim.animate_left_out);
		Button backButton = (Button) findViewById(R.id.backButtonLookup);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GamiNode.getAndroidGamiNode(mContext).getRfm().stopBuildingNotificationLookup();
				onBackPressed();
			}
		});

		Bundle b = this.getIntent().getExtras();
		address = b.getString("AddressBuilding");

		Log.d(BuildingLookupActivity.TAG, "Address: " + address);

		titleTv = (TextView) findViewById(R.id.BuildingText);
		
		String addressToShow = address;
		if (addressToShow.length() > 28) addressToShow = addressToShow.substring(0, 28) + "...";
		
		titleTv.setText(address);
		titleTv.setTextSize(18);

		GamiNode.getAndroidGamiNode(mContext).getRfm().startBuildingNotificationLookup(address);
		
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK, "");

		if (currentNetwork.equalsIgnoreCase(Constants.CHORD)) {
			GamiNode.addChordResourceListener(this);
		} else if (currentNetwork.equalsIgnoreCase(Constants.MESH)) {
			GamiNode.addMeshResourceListener(this);
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Gson gson = new Gson();
		String rooms = gson.toJson(floors.get(position).getRooms());

		Intent i = new Intent(this, FloorActivity.class);
		i.putExtra("Rooms", rooms);
		i.putExtra("Building", address);
		i.putExtra("Floor", floors.get(position).getName());

		startActivity(i);
	}

	/**
	 * When back button is pressed to get back to the map, stop listening for
	 * the updates related to the building
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GamiNode.getAndroidGamiNode(mContext).getRfm().stopBuildingNotificationLookup();
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Method to process the JSon descriptor of the resource the node looked for, when it is received.
	 */
	private void processSearchedResource(String jsonDescriptor) {
		try {
			JSONObject obj = new JSONObject(jsonDescriptor);
			String name = obj.getString("name");
			JSONObject subjectObj = obj.getJSONObject("subject");

			if (name.equalsIgnoreCase(buildingMessageType)) {
				String subjectValue = subjectObj.getString("value");
				Gson gson = new Gson();
				floors = gson.fromJson(subjectValue, new TypeToken<List<FloorStruct>>() {}.getType());

				runOnUiThread(new Runnable() {
					public void run() {
						List<String> listString = new ArrayList<String>();
						for (int i = 0; i < floors.size(); i++) {
							listString.add(context.getString(R.string.floor) + " " + floors.get(i).getName());
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item_floor, R.id.textItem, listString);
						setListAdapter(adapter);
					}
				});
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method executed every time a searched building descriptor is received on
	 * the Chord network. It also allows the runtime management of the
	 * descriptor's changes which represent changes in the building structure.
	 */
	@Override
	public void onReceivedResource(ResourceDescriptor rd, String reason) {
		String attachment = rd.getAttachment();
		processSearchedResource(attachment);
	}
	
	/**
	 * Method executed every time a searched building descriptor is received on
	 * the Mesh network. It also allows the runtime management of the
	 * descriptor's changes which represent changes in the building structure.
	 */
	@Override
	public void onFoundSearchedResource(Resource resource) {
		System.out.println("I am BuildingLookupActivity and I received the building information I looked for");
		
		// Building descriptor will just include a single element, so the
		// following cycle will be executed just once
		Set<String> keySet = resource.getKeySet();
		for(String k : keySet) {
			processSearchedResource(resource.getValue(k));
		}
	}

	@Override
	public void onReceivedResourceToBeResponsible(Resource resource) {
		System.out.println("I am BuildingLookupActivity and I received a building descriptor to be its responsible");
	}
	
	@Override
	public void onReceivedMessage(String message) {}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.animate_left_in, R.anim.animate_right_out);
	}

}
