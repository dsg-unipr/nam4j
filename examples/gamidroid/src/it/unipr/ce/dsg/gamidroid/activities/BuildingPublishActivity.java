package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * <p>
 * This class represents an Activity that allows the publication of building
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

public class BuildingPublishActivity extends Activity {

	public static String TAG = "BuildingPublishActivity";

	RelativeLayout rl;

	ArrayList<EditText> dynamicFloorsEditText, dynamicFloorsEditText2;
	ArrayList<Button> dynamicFloorsButton;
	ArrayList<ImageView> lines;

	Context context;

	int id, floorsNum, belowIndex;

	String address, currentLocationAddress;
	LatLng currentLocation;

	HashMap<String, String[]> buildingStructure;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.building_publish);

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
			EditText tvAddr = (EditText) findViewById(R.id.editBuildingAddress);
			tvAddr.setText(currentLocationAddress);
		}
		if (currentLocation != null) {
			double lat = currentLocation.latitude;
			double lng = currentLocation.longitude;
			EditText tvAddr = (EditText) findViewById(R.id.editLatlng);
			tvAddr.setText(lat + "," + lng);
		}

		Button backButton = (Button) findViewById(R.id.backButtonPublish);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		rl = (RelativeLayout) findViewById(R.id.BuildingPublishRelLayout);
		Button buttonOkFloors = (Button) findViewById(R.id.buttonOkFloors);

		dynamicFloorsEditText = new ArrayList<EditText>();
		dynamicFloorsEditText2 = new ArrayList<EditText>();
		dynamicFloorsButton = new ArrayList<Button>();
		lines = new ArrayList<ImageView>();
		buildingStructure = new HashMap<String, String[]>();

		buttonOkFloors.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText editText = (EditText) findViewById(R.id.editNumberOfFloors);

				if (!("".equals(editText.getText().toString()))
						&& editText.getText().toString() != null) {

					floorsNum = Integer.parseInt(editText.getText().toString());

					id = floorsNum;

					for (int i = 0; i < floorsNum; i++) {

						rl.removeView(findViewById(R.id.editNumberOfFloors));
						rl.removeView(findViewById(R.id.buttonOkFloors));

						RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.FILL_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT);

						RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT);

						p.addRule(RelativeLayout.CENTER_HORIZONTAL,
								RelativeLayout.TRUE);
						p.setMargins(20, 5, 20, 0);

						p2.addRule(RelativeLayout.CENTER_HORIZONTAL,
								RelativeLayout.TRUE);
						p2.setMargins(20, 10, 20, 0);

						EditText floorEd = new EditText(context);
						floorEd.setId(id++);

						final Button floorButton = new Button(context);
						floorButton.setText("OK");
						floorButton.setId(id++);

						/* Setting a custom tag equal to the floor number */
						floorButton.setTag((i + 1));

						TextView tv = (TextView) findViewById(R.id.FloorText);
						tv.setText(getResources().getString(
								R.string.number_of_rooms));

						/* The first button is under the OK button */
						if (i == 0) {
							p.addRule(RelativeLayout.BELOW, R.id.FloorText);
						}

						/* The following buttons are under the one before them */
						else {
							p.addRule(RelativeLayout.BELOW,
									dynamicFloorsEditText.get(i - 1).getId());
						}

						floorEd.setEllipsize(TruncateAt.START);
						floorEd.setLayoutParams(p);
						floorEd.setTextSize(14);
						floorEd.setTextColor(Color.BLACK);
						floorEd.setBackgroundResource(R.drawable.top_input_text_style);
						floorEd.setHint("Number of rooms on floor " + (i + 1));
						floorEd.setGravity(Gravity.CENTER);
						floorEd.setInputType(InputType.TYPE_CLASS_NUMBER);

						/* Setting a custom tag equal to the floor number */
						floorEd.setTag((i + 1));

						dynamicFloorsEditText.add(floorEd);
						p2.addRule(RelativeLayout.BELOW, floorEd.getId());

						floorButton.setLayoutParams(p2);
						floorButton.setWidth(100);
						floorButton.setHeight(40);
						floorButton.setTextColor(Color.WHITE);
						floorButton.setTextSize(14);

						floorButton
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {

										/*
										 * Checking if the user fulfilled all
										 * the fields
										 */
										boolean allFulfilled = true;

										for (int q = 0; q < dynamicFloorsEditText
												.size(); q++) {

											if ("".equals(dynamicFloorsEditText
													.get(q).getText()
													.toString())) {
												allFulfilled = false;

												System.out
														.println("Field "
																+ (q + 1)
																+ " is empty");

												break;
											}
										}

										if (allFulfilled) {

											int lastInsertedViewId = 0;

											TextView tv = (TextView) findViewById(R.id.FloorText);
											tv.setText(getResources()
													.getString(
															R.string.names_of_rooms));

											for (int h = 0; h < dynamicFloorsEditText
													.size(); h++) {

												EditText editTextRoom = dynamicFloorsEditText
														.get(h);

												if (editTextRoom.getTag() != null) {

													int floorNumber = Integer
															.parseInt(editTextRoom
																	.getTag()
																	.toString());

													int numberOfRooms = Integer
															.parseInt(editTextRoom
																	.getText()
																	.toString());

													if (!editTextRoom
															.getText()
															.toString()
															.equalsIgnoreCase(
																	"")
															&& editTextRoom
																	.getText()
																	.toString() != null) {

														for (int k = 0; k < numberOfRooms; k++) {

															EditText roomEd = new EditText(
																	context);
															roomEd.setId(id++);

															RelativeLayout.LayoutParams p3 = new RelativeLayout.LayoutParams(
																	ViewGroup.LayoutParams.FILL_PARENT,
																	ViewGroup.LayoutParams.WRAP_CONTENT);

															p3.addRule(
																	RelativeLayout.CENTER_HORIZONTAL,
																	RelativeLayout.TRUE);
															p3.setMargins(20,
																	5, 20, 0);

															if (h == 0
																	&& k == 0) {
																p3.addRule(
																		RelativeLayout.BELOW,
																		R.id.FloorText);
															} else {
																p3.addRule(
																		RelativeLayout.BELOW,
																		lastInsertedViewId);
															}

															roomEd.setLayoutParams(p3);
															roomEd.setTextSize(14);
															roomEd.setTextColor(Color.BLACK);
															roomEd.setBackgroundResource(R.drawable.top_input_text_style);
															roomEd.setHint("Floor "
																	+ floorNumber
																	+ ", room "
																	+ (k + 1)
																	+ " name");
															roomEd.setGravity(Gravity.CENTER);

															roomEd.setTag(floorNumber);

															dynamicFloorsEditText2
																	.add(roomEd);

															lastInsertedViewId = roomEd
																	.getId();

														}
													}
												}
											}

											rl.removeView(floorButton);

											for (int f = 0; f < dynamicFloorsEditText
													.size(); f++) {
												rl.removeView(dynamicFloorsEditText
														.get(f));
											}

											for (int f = 0; f < dynamicFloorsEditText2
													.size(); f++) {
												rl.addView(dynamicFloorsEditText2
														.get(f));
											}

											/*
											 * Adding the button to start
											 * publishing the info about the
											 * building.
											 */

											RelativeLayout.LayoutParams p6 = new RelativeLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													ViewGroup.LayoutParams.WRAP_CONTENT);

											p6.addRule(
													RelativeLayout.CENTER_HORIZONTAL,
													RelativeLayout.TRUE);

											RelativeLayout rlButton = (RelativeLayout) findViewById(R.id.rlButton);

											Button publishButton = new Button(
													context);
											publishButton
													.setText("Publish Building Information");
											publishButton.setId(id++);
											publishButton.setLayoutParams(p6);
											publishButton.setWidth(220);
											publishButton.setHeight(40);
											publishButton
													.setTextColor(Color.WHITE);
											publishButton.setTextSize(12);

											rlButton.addView(publishButton);

											publishButton
													.setOnClickListener(new View.OnClickListener() {

														@Override
														public void onClick(
																View v) {

															/*
															 * If all the fields
															 * have been
															 * fulfilled by the
															 * user, the field
															 * keeps the true
															 * value, otherwise
															 * it is set to
															 * false.
															 */
															boolean completed = true;

															EditText editTextAddress = (EditText) findViewById(R.id.editBuildingAddress);
															address = editTextAddress
																	.getText()
																	.toString();

															if (!"".equals(address)
																	&& address != null) {

																/*
																 * Creation of
																 * the xml
																 * Document
																 * containing
																 * the structure
																 * of the
																 * building
																 */
																Document doc = null;

																try {
																	DocumentBuilderFactory docFactory = DocumentBuilderFactory
																			.newInstance();
																	DocumentBuilder docBuilder;

																	docBuilder = docFactory
																			.newDocumentBuilder();

																	doc = docBuilder
																			.newDocument();

																	Element titleElement = doc
																			.createElement("BUILDING");
																	titleElement
																			.setAttribute(
																					"name",
																					address);

																	for (int f = 0; f < floorsNum; f++) {

																		int currentFloorNum = f + 1;

																		Element floorElement = doc
																				.createElement("FLOOR");
																		floorElement
																				.setAttribute(
																						"name",
																						currentFloorNum
																								+ "");

																		for (int t = 0; t < dynamicFloorsEditText2
																				.size(); t++) {

																			if (dynamicFloorsEditText2
																					.get(t)
																					.getTag() != null) {

																				int floorOnWhichTheRoomIs = Integer
																						.parseInt(dynamicFloorsEditText2
																								.get(t)
																								.getTag()
																								.toString());

																				if (floorOnWhichTheRoomIs == currentFloorNum) {

																					if (!dynamicFloorsEditText2
																							.get(t)
																							.getText()
																							.toString()
																							.equalsIgnoreCase(
																									"")
																							&& dynamicFloorsEditText2
																									.get(t)
																									.getText()
																									.toString() != null) {
																						Element roomElement = doc
																								.createElement("ROOM");
																						roomElement
																								.setAttribute(
																										"name",
																										dynamicFloorsEditText2
																												.get(t)
																												.getText()
																												.toString());
																						floorElement
																								.appendChild(roomElement);
																					} else {

																						completed = false;

																						break;
																					}
																				}
																			}
																		}

																		titleElement
																				.appendChild(floorElement);
																	}

																	doc.appendChild(titleElement);

																} catch (ParserConfigurationException e) {
																	e.printStackTrace();
																}

																if (doc != null
																		&& completed) {

																	final Document docToBeSent = doc;
																	String output = "";

																	try {
																		TransformerFactory tf = TransformerFactory
																				.newInstance();
																		Transformer transformer;

																		transformer = tf
																				.newTransformer();

																		transformer
																				.setOutputProperty(
																						OutputKeys.OMIT_XML_DECLARATION,
																						"yes");
																		StringWriter writer = new StringWriter();
																		transformer
																				.transform(
																						new DOMSource(
																								docToBeSent),
																						new StreamResult(
																								writer));
																		output = writer
																				.getBuffer()
																				.toString()
																				.replaceAll(
																						"\n|\r",
																						"");
																	} catch (TransformerConfigurationException e) {
																		e.printStackTrace();
																	} catch (TransformerException e) {
																		e.printStackTrace();
																	}

																	final String outString = output;

																	AlertDialog.Builder dialog = new AlertDialog.Builder(
																			context);
																	dialog.setMessage(getResources()
																			.getString(
																					R.string.add_rooms_to_building));
																	dialog.setCancelable(true);
																	dialog.setPositiveButton(
																			"OK",
																			new DialogInterface.OnClickListener() {
																				public void onClick(
																						DialogInterface dialog,
																						int id) {
																					dialog.dismiss();

																					Intent i = new Intent(
																							context,
																							AddSensorsToBuildingActivity.class);
																					i.putExtra(
																							"doc",
																							outString);

																					startActivity(i);

																					onBackPressed();

																				}
																			});
																	dialog.setNegativeButton(
																			"No",
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(
																						DialogInterface dialog,
																						int id) {
																					dialog.dismiss();

																					GamiNode.publishBuilding(docToBeSent);

																					Toast.makeText(
																							context,
																							getResources()
																									.getString(
																											R.string.resource_published),
																							Toast.LENGTH_LONG)
																							.show();

																					onBackPressed();
																				}
																			});

																	dialog.show();

																} else if (!completed) {
																	Toast.makeText(
																			context,
																			getResources()
																					.getString(
																							R.string.specify_room_names),
																			Toast.LENGTH_LONG)
																			.show();
																}

															} else {
																Toast.makeText(
																		context,
																		getResources()
																				.getString(
																						R.string.specify_address),
																		Toast.LENGTH_LONG)
																		.show();
															}
														}
													});

										} else {
											Toast.makeText(
													context,
													getResources()
															.getString(
																	R.string.number_of_rooms),
													Toast.LENGTH_LONG).show();
										}
									}

								});

						rl.addView(floorEd);

						if (i == (floorsNum - 1)) {
							dynamicFloorsButton.add(floorButton);
							rl.addView(floorButton);
						}
					}
				} else {
					Toast.makeText(
							context,
							getResources()
									.getString(R.string.specify_floor_num),
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
