package it.unipr.ce.dsg.examples.kademliafm;

import it.unipr.ce.dsg.s2p.kademlia.KademliaKey;
import it.unipr.ce.dsg.s2p.kademlia.KademliaResource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Class to publish a resource in the Kademlia network.
 * The resource is saved in a .txt file whose name is computed
 * based on the resource parameters.
 *
 */
public class PublishRunnable implements Runnable {
	
	private KademliaFunctionalModule kfm = null;
	private String item = null;
	private NamKademliaPeer kp = null;

	public PublishRunnable(KademliaFunctionalModule kfm, String item, NamKademliaPeer kp) {
		this.kfm = kfm;
		this.item = item;
		this.kp = kp;
	}

	@Override
	public void run() {
		
		kfm.getKademliaLogger().log("Service: Publish " + item);
		
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
		
		// The resource is saved into a text file to be published in the network
		KademliaResource kr = new KademliaResource(resourceKey, item.getBytes(), fileName + ".txt");
		
		kp.publishResource(kr);
		
		kfm.getKademliaLogger().log("Generated Resource with key: " + resourceKey);
		System.out.println("Published resource " + item + "\n" + "having key " + resourceKey + "\nin file " + kr.getFileName());

	}

}
