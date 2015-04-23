package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * <p>
 * This class represents an activity that publishes data for a sensor.
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

public class SensorPublishActivity extends Activity {

	public static String TAG = "SensorPublishActivity";

	Context context;

	String address, currentLocationAddress;
	LatLng currentLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_publish);

		overridePendingTransition(R.anim.animate_left_in,
				R.anim.animate_right_out);

		context = this;

		Bundle b = this.getIntent().getExtras();
		currentLocationAddress = b.getString("Address");

		Bundle bundleCurrentLocation = this.getIntent().getParcelableExtra(
				"Bundle");

		currentLocation = bundleCurrentLocation
				.getParcelable("CurrentLocation");

		if (currentLocationAddress != null
				&& !("".equals(currentLocationAddress))) {
			EditText tvAddr = (EditText) findViewById(R.id.editSensorAddress);
			tvAddr.setText(currentLocationAddress);
		}
		if (currentLocation != null) {
			double lat = currentLocation.latitude;
			double lng = currentLocation.longitude;
			EditText tvAddr = (EditText) findViewById(R.id.editLatlng);
			tvAddr.setText(lat + "," + lng);
		}

		Button backButton = (Button) findViewById(R.id.backButtonSensor);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		Button publishButton = (Button) findViewById(R.id.buttonOkSensor);

		publishButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText editTextAddress = (EditText) findViewById(R.id.editSensorAddress);
				address = editTextAddress.getText().toString();

				EditText editTextLatLng = (EditText) findViewById(R.id.editLatlng);
				String instertedLatLng = editTextLatLng.getText().toString()
						.replaceAll("\\s+", "");

				String[] splitArray = instertedLatLng.split(",");

				if (splitArray.length == 2) {

					String latStr = instertedLatLng.split(",")[0];
					String lngStr = instertedLatLng.split(",")[1];

					if (!"".equals(address) && address != null) {

						EditText editTextName = (EditText) findViewById(R.id.editSensorName);
						String name = editTextName.getText().toString();

						EditText editTextFloor = (EditText) findViewById(R.id.editSensorFloor);
						String floor = editTextFloor.getText().toString();

						EditText editTextRoom = (EditText) findViewById(R.id.editSensorRoom);
						String room = editTextRoom.getText().toString();

						EditText editTextValue = (EditText) findViewById(R.id.editSensorValue);
						String value = editTextValue.getText().toString();

						if (!"".equals(name) && !(name == null)
								&& !"".equals(floor) && !(floor == null)
								&& !"".equals(room) && !(room == null)
								&& !"".equals(value) && !(value == null)) {

							/* Start publishing the resource */

							GamiNode.publishSensor(address, latStr, lngStr,
									name, floor, room, value);

							Toast.makeText(
									context,
									getResources().getString(
											R.string.resource_published),
									Toast.LENGTH_LONG).show();

							onBackPressed();
						} else {
							Toast.makeText(
									context,
									getResources().getString(
											R.string.specify_all),
									Toast.LENGTH_LONG).show();
						}

					} else {
						Toast.makeText(
								context,
								getResources().getString(
										R.string.specify_address),
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(context,
							getResources().getString(R.string.specify_latlng),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.animate_left_in,
				R.anim.animate_right_out);
	}

}
