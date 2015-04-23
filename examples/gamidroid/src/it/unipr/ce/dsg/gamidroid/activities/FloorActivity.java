package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;
import it.unipr.ce.dsg.gamidroid.ontology.FloorStruct;
import it.unipr.ce.dsg.gamidroid.ontology.RoomStruct;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2pchord.resource.ResourceDescriptor;
import it.unipr.ce.dsg.s2pchord.resource.ResourceListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FloorActivity extends ListActivity implements ResourceListener, IEventListener {

	public static String TAG = "FloorActivity";
	private List<RoomStruct> rooms;
	private String building;
	private String floor;

	TextView titleTv;

	Context mContext;

	private static String buildingMessageType = "BuildingNotification";

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
				onBackPressed();
			}
		});

		mContext = this;

		Bundle b = this.getIntent().getExtras();
		String roomsString = b.getString("Rooms");
		building = b.getString("Building");
		floor = b.getString("Floor");

		String buildingToShow = building;
		if (buildingToShow.length() > 28)
			buildingToShow = buildingToShow.substring(0, 28) + "...";
		String floorToShow = getResources().getString(R.string.floor) + floor;
		if (floorToShow.length() > 28)
			floorToShow = floorToShow.substring(0, 28) + "...";

		titleTv = (TextView) findViewById(R.id.BuildingText);
		titleTv.setText(buildingToShow + "\n" + floorToShow);
		titleTv.setTextSize(18);

		Gson gson = new Gson();
		rooms = gson.fromJson(roomsString, new TypeToken<List<RoomStruct>>() {
		}.getType());

		List<String> listString = new ArrayList<String>();

		for (int i = 0; i < rooms.size(); i++) {
			listString.add(rooms.get(i).getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_room, R.id.textItem, listString);
		setListAdapter(adapter);

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Constants.PREFERENCES, Context.MODE_PRIVATE);
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

		String sensors = gson.toJson(rooms.get(position).getSensors());

		Intent i = new Intent(this, RoomActivity.class);
		i.putExtra("Sensors", sensors);
		i.putExtra("Building", building);
		i.putExtra("Floor", floor);
		i.putExtra("Room", rooms.get(position).getName());

		startActivity(i);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Method to manage runtime changing number of rooms on the selcted floor.
	 * */
	@Override
	public void onReceivedResource(ResourceDescriptor rd, String reason) {

		String attachment = rd.getAttachment();

		try {

			JSONObject obj = new JSONObject(attachment);
			String name = obj.getString("name");
			JSONObject subjectObj = obj.getJSONObject("subject");

			if (name.equalsIgnoreCase(buildingMessageType)) {

				String subjectValue = subjectObj.getString("value");

				Gson gson = new Gson();

				List<FloorStruct> floors = gson.fromJson(subjectValue,
						new TypeToken<List<FloorStruct>>() {
						}.getType());

				FloorStruct selectedFloor = null;

				for (int f = 0; f < floors.size(); f++) {

					if (floors.get(f).getName().equalsIgnoreCase(floor)) {
						selectedFloor = floors.get(f);
					}
				}

				if (selectedFloor != null) {

					String roomsInFloor = gson.toJson(selectedFloor.getRooms());

					rooms = gson.fromJson(roomsInFloor,
							new TypeToken<List<RoomStruct>>() {
							}.getType());

					runOnUiThread(new Runnable() {
						public void run() {

							List<String> listString = new ArrayList<String>();

							for (int i = 0; i < rooms.size(); i++) {
								listString.add(rooms.get(i).getName());
							}

							ArrayAdapter<String> adapter = new ArrayAdapter<String>(
									mContext, R.layout.list_item_room,
									R.id.textItem, listString);
							setListAdapter(adapter);

						}
					});

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onFoundSearchedResource(Resource resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceivedResourceToBeResponsible(Resource resource) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onReceivedMessage(String message) {
		System.out.println("I am NamCentralizedPeer and I have received a message: " + message);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.animate_right_in, R.anim.animate_right_out);
	}

}