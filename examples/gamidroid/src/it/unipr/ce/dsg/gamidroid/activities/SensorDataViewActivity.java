package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;
import it.unipr.ce.dsg.gamidroid.ontology.Building;
import it.unipr.ce.dsg.gamidroid.ontology.Floor;
import it.unipr.ce.dsg.gamidroid.ontology.Location;
import it.unipr.ce.dsg.gamidroid.ontology.Room;
import it.unipr.ce.dsg.gamidroid.ontology.Sensor;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2pchord.resource.ResourceDescriptor;
import it.unipr.ce.dsg.s2pchord.resource.ResourceListener;

import java.util.Date;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

/**
 * <p>
 * This class represents an activity that receives data for a sensor.
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

public class SensorDataViewActivity extends Activity implements
		ResourceListener, IEventListener {

	public static String TAG = "SettingsActivity";
	private Context mContext;
	private Location location;
	private String sensorName;
	private static String temperatureMessageType = "TemperatureNotification";
	private volatile boolean stopThread = false;
	private LinearLayout layout;
	private GraphView graphView;
	private GraphViewSeries graphViewSeries;
	private String[] hLabels;
	private double currentX = 0;
	private int MAX_DATA_COUNT = 3600;

	TextView titleTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_data);
		
		mContext = this;
		
		overridePendingTransition(R.anim.animate_left_in, R.anim.animate_left_out);

		Button backButton = (Button) findViewById(R.id.backButtonSensor);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				stopThread();

				GamiNode.getAndroidGamiNode(mContext).getRfm()
						.stopTemperatureNotificationLookup();

				onBackPressed();
			}
		});

		stopThread = false;

		Bundle b = getIntent().getExtras();

		location = new Location();
		location.setBuilding(new Building(b.getString("Building")));
		location.setFloor(new Floor(b.getString("Floor")));
		location.setRoom(new Room(b.getString("Room")));
		location.setSensor(new Sensor(b.getString("Sensor")));

		sensorName = b.getString("Sensor");

		String buildingToShow = b.getString("Building");
		if (buildingToShow.length() > 28)
			buildingToShow = buildingToShow.substring(0, 28) + "...";
		String floorToShow = getResources().getString(R.string.floor)
				+ b.getString("Floor");
		if (floorToShow.length() > 28)
			floorToShow = floorToShow.substring(0, 28) + "...";
		String roomToShow = b.getString("Room");
		if (roomToShow.length() > 28)
			roomToShow = roomToShow.substring(0, 28) + "...";

		titleTv = (TextView) findViewById(R.id.SensorResumeText);
		titleTv.setText(buildingToShow + "\n" + floorToShow + "\n" + roomToShow);
		titleTv.setTextSize(15);

		GamiNode
				.getAndroidGamiNode(mContext)
				.getRfm()
				.startTemperatureNotificationLookup(
						location.getBuilding().getValue(),
						location.getFloor().getValue(),
						location.getRoom().getValue(),
						location.getSensor().getValue());

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK, "");

		if (currentNetwork.equalsIgnoreCase(Constants.CHORD)) {
			GamiNode.addChordResourceListener(this);
		} else if (currentNetwork.equalsIgnoreCase(Constants.MESH)) {
			GamiNode.addMeshResourceListener(this);
		}
		
		// Initialize sensor data graph
		graphViewSeries = new GraphViewSeries(new GraphViewData[] {});
		graphView = new LineGraphView(this, "Sensor Readings");
		graphView.setPadding(3, 0, 0, 0);
		graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setTextSize(16);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(40); // width of the vertical labels
	  
	    Date d = new Date();
	    String s  = (String) DateFormat.format("H:m:s ", d.getTime());
	    
	    System.out.println(s);
		
		// Array of strings that are the horizontal labels
		hLabels=new String[] {s};
		graphView.setHorizontalLabels(hLabels);
		
		// graphView.getGraphViewStyle().setVerticalLabelsWidth(200);
		
		graphView.addSeries(graphViewSeries); // data
		graphView.setViewPort(0, 4);
		graphView.setScalable(false);
		graphView.setScrollable(true);
		graphView.setManualYAxisBounds(45, 0);
		// graphView.getGraphViewStyle().setNumVerticalLabels(45 / 5);
		graphView.getGraphViewStyle().setNumHorizontalLabels(10);
		
		layout = (LinearLayout) findViewById(R.id.graph);
		layout.addView(graphView);
	}
	
	/**
	 * Method to process the JSon descriptor of the resource the node looked
	 * for, when it is received.
	 */
	private void processSearchedResource(String jsonDescriptor) {
		System.out.println("\n--- Processing the sensor's data...\n");
		
		try {
			JSONObject obj = new JSONObject(jsonDescriptor);

			String name = obj.getString("name");
			// JSONObject subjectObj = obj.getJSONObject("subject");
			JSONObject locationObj = obj.getJSONObject("location");

			if (name.equalsIgnoreCase(temperatureMessageType)) {

				// final String subjectValue = subjectObj.getString("value");

				JSONObject locationValue = new JSONObject(
						locationObj.getString("value"));

				JSONObject building = locationValue
						.getJSONObject("building");
				final String buildingValue = building.getString("value");

				JSONObject room = locationValue.getJSONObject("room");
				final String roomValue = room.getString("value");

				JSONObject floor = locationValue.getJSONObject("floor");
				final String floorValue = floor.getString("value");

				JSONObject sensor = locationValue.getJSONObject("sensor");
				final String sensorValue = sensor.getString("value");

				if (sensorValue.equalsIgnoreCase(sensorName)) {

					runOnUiThread(new Runnable() {
						public void run() {
							if (!stopThread) {
								TextView textBuilding = (TextView) findViewById(R.id.textAddress);
								TextView textFloor = (TextView) findViewById(R.id.textFloor);
								TextView textRoom = (TextView) findViewById(R.id.textRoom);
								TextView textSensor = (TextView) findViewById(R.id.textSensor);
								TextView textTemperature = (TextView) findViewById(R.id.textTemperature);

								textBuilding.setText("Address: " + buildingValue);
								textFloor.setText("Floor: " + floorValue);
								textRoom.setText("Room: " + roomValue);
								textSensor.setText("Sensor: " + sensorValue);
								// textTemperature.setText("Temperature: " + subjectValue);
								
								// graphViewSeries.appendData(new GraphViewData(currentX++, Double.parseDouble(subjectValue)), true, MAX_DATA_COUNT);
								
								int min = 26;
								int max = 30;
								double randomValue = min + (int)(Math.random() * ((max - min) + 1));
								String randomValueString = "" + randomValue;
								textTemperature.setText("Temperature: " + randomValueString);

								int length=hLabels.length;
								length++;

								String[] localLabels=new String[length];

								for (int i=0;i<length-1;i++)
								{
									localLabels[i]=hLabels[i];
								}
								
							    Date d = new Date();
							    String s  = (String) DateFormat.format("H:m:s ", d.getTime());
							    System.out.println(s);
							    localLabels[length-1]=s;
							    hLabels=localLabels;
								//hLabels[length]=s;
								graphView.setHorizontalLabels(hLabels);
								
								graphViewSeries.appendData(new GraphViewData(currentX++, randomValue), false, MAX_DATA_COUNT);
							}
						}
					});
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceivedResource(ResourceDescriptor rd, String reason) {
		String attachment = rd.getAttachment();

		if (attachment != null) {
			processSearchedResource(attachment);
		} else {
			Toast.makeText(this, "No data received for the sensor", Toast.LENGTH_LONG).show();
			Log.e(SensorDataViewActivity.TAG, "The attachement of the resource is null\n");
		}
	}
	
	@Override
	public void onFoundSearchedResource(Resource resource) {
		// Sensor descriptor will just include a single element, so the
		// following cycle will be executed just once
		Set<String> keySet = resource.getKeySet();
		for(String k : keySet) {
			processSearchedResource(resource.getValue(k));
		}
	}

	@Override
	public void onReceivedResourceToBeResponsible(Resource resource) {}
	
	@Override
	public void onReceivedMessage(String message) {
		System.out.println("I am NamCentralizedPeer and I have received a message: " + message);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stopThread();
			GamiNode.getAndroidGamiNode(mContext).getRfm().stopTemperatureNotificationLookup();
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void stopThread() {
		stopThread = true;
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.animate_right_in, R.anim.animate_right_out);
	}

}
