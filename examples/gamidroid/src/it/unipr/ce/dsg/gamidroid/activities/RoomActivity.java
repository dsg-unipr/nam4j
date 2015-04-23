package it.unipr.ce.dsg.gamidroid.activities;

import it.unipr.ce.dsg.gamidroid.R;
import it.unipr.ce.dsg.gamidroid.gaminode.GamiNode;
import it.unipr.ce.dsg.gamidroid.ontology.FloorStruct;
import it.unipr.ce.dsg.gamidroid.ontology.RoomStruct;
import it.unipr.ce.dsg.gamidroid.ontology.Sensor;
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

public class RoomActivity extends ListActivity implements ResourceListener, IEventListener {

	public static String TAG = "RoomActivity";
	private List <Sensor> sensors;
	private String building;
	private String floor;
	private String room;
	
	TextView titleTv;
	
	Context mContext;
	
	private static String buildingMessageType = "BuildingNotification";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_lookup);
        
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
        String sensorsString = b.getString("Sensors");
        building = b.getString( "Building" );
        floor = b.getString( "Floor" );
        room = b.getString( "Room" );
        
        String buildingToShow = building;
		if (buildingToShow.length() > 28) buildingToShow = buildingToShow.substring(0, 28) + "...";
		String floorToShow = getResources().getString(R.string.floor) + floor;
		if (floorToShow.length() > 28) floorToShow = floorToShow.substring(0, 28) + "...";
		String roomToShow = room;
		if (roomToShow.length() > 28) roomToShow = roomToShow.substring(0, 28) + "...";
        
        titleTv = (TextView) findViewById(R.id.BuildingText);
        titleTv.setText(buildingToShow + "\n" + floorToShow + "\n" + roomToShow);
        titleTv.setTextSize(15);
        
        Gson gson = new Gson();
        sensors = gson.fromJson(sensorsString, new TypeToken<List<Sensor>>(){}.getType());

        List<String> listString = new ArrayList<String>();
    	for ( int i = 0 ; i < sensors.size()  ; i++ ){
    		listString.add(sensors.get(i).getValue());
    	}
    	
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_sensor, R.id.textItem , listString );
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
        
		/* Stop receiving updates for the building structure */
		//AndroidDemoNam.getAndroidDemoNam().getRfm()
		//		.stopBuildingNotificationLookup();
        
		Intent i = new Intent(this, SensorDataViewActivity.class);
		
		// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		i.putExtra("Building", building);
		i.putExtra("Floor", floor);
		i.putExtra("Room", room);
		i.putExtra("Sensor", sensors.get(position).getValue());
    	
    	startActivity( i );
        
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

    /**
     * Method to manage runtime changing number of sensor in the selcted room.
     * */
	@Override
	public void onReceivedResource(ResourceDescriptor rd, String reason) {
		
		String attachment = rd.getAttachment();
		
		try {
			
			JSONObject obj = new JSONObject(attachment);

			// String id = obj.getString("id");
			String name = obj.getString("name");
			// String timestamp = obj.getString("timestamp");
			
			JSONObject subjectObj = obj.getJSONObject("subject");
			// JSONObject locationObj = obj.getJSONObject("location");

			// String subjectName = subjectObj.getString("name");
			
			if (name.equalsIgnoreCase(buildingMessageType)) {

				String subjectValue = subjectObj.getString("value");

				// JSONArray locationValue = new
				// JSONArray(locationObj.getString("value"));

				Gson gson = new Gson();

				List<FloorStruct> floors = gson.fromJson(subjectValue,
						new TypeToken<List<FloorStruct>>() {
						}.getType());
				
				FloorStruct selectedFloor = null;
				
				for(int f = 0; f < floors.size(); f++) {
					
					if (floors.get(f).getName().equalsIgnoreCase(floor)) {
						selectedFloor = floors.get(f);
					}
				}
				
				if (selectedFloor != null) {
					
					List<RoomStruct> rooms = selectedFloor.getRooms();
					
					RoomStruct selectedRoom = null;
					
					for(int h = 0; h < rooms.size(); h++) {
						
						if (rooms.get(h).getName().equalsIgnoreCase(room)) {
							selectedRoom = rooms.get(h);
						}
					}
					
					if (selectedRoom != null) {
					
						String sensorsInRoom = gson.toJson(selectedRoom.getSensors());
						
						sensors = gson.fromJson(sensorsInRoom, new TypeToken<List<Sensor>>(){}.getType());
		
						runOnUiThread(new Runnable() {
							public void run() {
		
								List<String> listString = new ArrayList<String>();
						    	for ( int i = 0 ; i < sensors.size()  ; i++ ){
						    		listString.add(sensors.get(i).getValue());
						    	}
						    	
						        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.list_item_sensor, R.id.textItem , listString );
						        setListAdapter(adapter);
		
							}
						});
					}
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
