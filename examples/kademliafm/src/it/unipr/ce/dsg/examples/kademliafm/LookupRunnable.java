package it.unipr.ce.dsg.examples.kademliafm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import it.unipr.ce.dsg.s2p.kademlia.KademliaKey;

/**
 * Class to look for a resource in the centralized network.
 */
public class LookupRunnable implements Runnable {

	private KademliaFunctionalModule kfm = null;
	private String item = null;
	private NamKademliaPeer kp = null;
	
	public LookupRunnable(KademliaFunctionalModule kfm, String item, NamKademliaPeer kp) {
		this.kfm = kfm;
		this.item = item;
		this.kp = kp;
	}
	
	@Override
	public void run() {
		
		kfm.getKademliaLogger().log("Service: Lookup " + item);
		
		// The name of the file containing the resource is computed by parsing the resource itself
		JsonElement jelement = new JsonParser().parse(item);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("location");
	    JsonPrimitive value = jobject.getAsJsonPrimitive("value");
	    String valueToString = value.toString().replace("\\", "");
	    valueToString = valueToString.substring(1, valueToString.toString().length() - 1);
	    JsonElement jelementValue = new JsonParser().parse(valueToString);
	    JsonObject  jobjectValue = jelementValue.getAsJsonObject();
	    JsonObject buildingElement = jobjectValue.getAsJsonObject("building");
	    String buildingAddress = buildingElement.getAsJsonPrimitive("value").toString().replace("\"", "");
	    JsonObject roomElement = jobjectValue.getAsJsonObject("room");
	    String roomAddress = roomElement.getAsJsonPrimitive("value").toString().replace("\"", "");
	    JsonObject floorElement = jobjectValue.getAsJsonObject("floor");
	    String floorAddress = floorElement.getAsJsonPrimitive("value").toString().replace("\"", "");
	    JsonObject sensorElement = jobjectValue.getAsJsonObject("sensor");
	    String sensorAddress = sensorElement.getAsJsonPrimitive("value").toString().replace("\"", "");
	    
	    String fileName = (buildingAddress + "_" + roomAddress + "_" + floorAddress + "_" + sensorAddress).trim().replaceAll("\\W", "");
	    
		// Generate a key for the resource by hashing its name
		KademliaKey resourceKey = new KademliaKey(fileName);
		
		System.out.println("Searching for resource having key: " + resourceKey);
		
		kp.findNodeOrValue(resourceKey, 'v');

	}

}
