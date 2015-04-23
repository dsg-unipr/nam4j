package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * <p>
 * This class represents an Activity that allows the addition of sensors to a
 * building notification.
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

public class AddSensorsToBuildingActivity extends Activity {

	public static String TAG = "AddSensorsToBuildingActivity";

	Context context;
	RelativeLayout rl;
	String docString;
	Document document;
	ArrayList<EditText> dynamicRoomsEditText;
	int id, numberOfClicks;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_sensors_to_building);
		
		overridePendingTransition(R.anim.animate_left_in, R.anim.animate_right_out);

		context = this;

		Bundle b = this.getIntent().getExtras();

		docString = b.getString("doc");

		rl = (RelativeLayout) findViewById(R.id.AddSensorsRelLayout);

		Button backButton = (Button) findViewById(R.id.backButtonAddSensors);

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		dynamicRoomsEditText = new ArrayList<EditText>();

		numberOfClicks = 0;

		try {
			StringReader sr = new StringReader(docString);
			InputSource is = new InputSource(sr);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;

			builder = factory.newDocumentBuilder();

			document = builder.parse(is);

			Element rootElement = (Element) document.getDocumentElement();

			NodeList floors = rootElement.getElementsByTagName("FLOOR");

			int floorsNum = floors.getLength();

			id = floorsNum;

			for (int i = 0; i < floors.getLength(); i++) {

				Element floorElement = (Element) floors.item(i);

				NodeList rooms = floorElement.getElementsByTagName("ROOM");

				for (int j = 0; j < rooms.getLength(); j++) {

					Element roomElement = (Element) rooms.item(j);

					RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);

					p.addRule(RelativeLayout.CENTER_HORIZONTAL,
							RelativeLayout.TRUE);
					p.setMargins(20, 20, 20, 0);

					EditText roomEd = new EditText(context);

					roomEd.setEllipsize(TruncateAt.START);
					roomEd.setLayoutParams(p);
					roomEd.setTextSize(14);
					roomEd.setTextColor(Color.BLACK);
					roomEd.setBackgroundResource(R.drawable.top_input_text_style);
					roomEd.setHint("Number of sensors in room "
							+ roomElement.getAttribute("name"));
					roomEd.setGravity(Gravity.CENTER);
					roomEd.setInputType(InputType.TYPE_CLASS_NUMBER);
					roomEd.setId(id++);
					roomEd.setTag(roomElement.getAttribute("name"));

					p.addRule(RelativeLayout.BELOW, (id - 2));

					dynamicRoomsEditText.add(roomEd);

					rl.addView(roomEd);

				}

			}

			RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			p2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			p2.setMargins(20, 10, 20, 0);
			p2.addRule(RelativeLayout.BELOW, (id - 1));

			Button okButton = new Button(context);
			okButton.setText("OK");
			okButton.setId(id++);

			okButton.setLayoutParams(p2);
			okButton.setWidth(120);
			okButton.setHeight(40);
			okButton.setTextColor(Color.WHITE);
			okButton.setTextSize(14);

			okButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					ArrayList<EditText> al = new ArrayList<EditText>();

					for (int g = 0; g < dynamicRoomsEditText.size(); g++) {

						int currentNum = Integer.parseInt(dynamicRoomsEditText
								.get(g).getText().toString());

						for (int k = 0; k < currentNum; k++) {

							RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
									ViewGroup.LayoutParams.FILL_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT);

							p.addRule(RelativeLayout.CENTER_HORIZONTAL,
									RelativeLayout.TRUE);
							p.setMargins(20, 5, 20, 0);

							p.addRule(RelativeLayout.BELOW, (id - 1));

							EditText sensorEd = new EditText(context);

							sensorEd.setLayoutParams(p);
							sensorEd.setTextSize(14);
							sensorEd.setTextColor(Color.BLACK);
							sensorEd.setBackgroundResource(R.drawable.top_input_text_style);
							sensorEd.setGravity(Gravity.CENTER);
							sensorEd.setId(id++);
							sensorEd.setHint("Name of sensor " + (k + 1)
									+ " in "
									+ dynamicRoomsEditText.get(g).getTag());
							sensorEd.setTag(dynamicRoomsEditText.get(g)
									.getTag());

							al.add(sensorEd);
						}

					}

					rl.removeAllViews();

					for (int l = 0; l < al.size(); l++) {

						EditText et = al.get(l);

						rl.addView(et);
					}

					RelativeLayout.LayoutParams p3 = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);

					p3.addRule(RelativeLayout.CENTER_HORIZONTAL,
							RelativeLayout.TRUE);
					p3.setMargins(20, 10, 20, 0);
					p3.addRule(RelativeLayout.BELOW, (id - 1));

					Button publishButton = new Button(context);
					publishButton.setText("OK");

					publishButton.setLayoutParams(p3);
					publishButton.setWidth(100);
					publishButton.setHeight(40);
					publishButton.setTextColor(Color.WHITE);
					publishButton.setTextSize(14);

					rl.addView(publishButton);

					publishButton
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {

									/*
									 * Processing all the EditText in the
									 * RelativeLayout; the last child is the
									 * button, so the for cycle skips it.
									 */
									for (int i = 0; i < (rl.getChildCount() - 1); i++) {
										EditText child = (EditText) rl
												.getChildAt(i);

										String sensorName = child.getText()
												.toString();
										String room = child.getTag().toString();

										Element rootElement = (Element) document
												.getDocumentElement();

										NodeList floors = rootElement
												.getElementsByTagName("FLOOR");

										for (int n = 0; n < floors.getLength(); n++) {

											Element floorElement = (Element) floors
													.item(n);

											NodeList rooms = floorElement
													.getElementsByTagName("ROOM");

											for (int j = 0; j < rooms
													.getLength(); j++) {

												Element roomElement = (Element) rooms
														.item(j);

												String roomName = roomElement
														.getAttribute("name");

												if (roomName
														.equalsIgnoreCase(room)) {

													/* Add sensor to room */

													Element sensorElement = document
															.createElement("SENSOR");
													sensorElement.setAttribute(
															"name", sensorName);

													roomElement
															.appendChild(sensorElement);
												}

											}

										}
									}

									AlertDialog.Builder dialog = new AlertDialog.Builder(
											context);
									dialog.setMessage(getResources()
											.getString(
													R.string.start_publishing_building));
									dialog.setCancelable(true);
									dialog.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.dismiss();

													GamiNode
															.publishBuilding(document);

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
									dialog.setNegativeButton(
											"No",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.dismiss();

												}
											});

									dialog.show();

								}

							});
				}
			});

			rl.addView(okButton);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.animate_left_in, R.anim.animate_right_out);
	}

}
