package it.unipr.ce.dsg.examples.centralizednetworkfm;

import it.unipr.ce.dsg.s2p.centralized.utils.Key;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Class to publish a resource in the centralized network.
 */
public class PublishRunnable implements Runnable {

	private CentralizedFunctionalModule cfm = null;
	private String item = null;
	private NamCentralizedPeer cp = null;

	public PublishRunnable(CentralizedFunctionalModule cfm, String item, NamCentralizedPeer cp) {
		this.cfm = cfm;
		this.item = item;
		this.cp = cp;
	}

	@Override
	public void run() {

		cfm.getCentralizedLogger().log("Service: Publish " + item);

		// The name of the resource containing the resource is computed by parsing the resource itself
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

		String resourceName = (buildingAddress + "_" + roomAddress + "_" + floorAddress + "_" + sensorAddress).trim().replaceAll("\\W", "");
		
		Key resourceKey = new Key(resourceName);
		Resource resource = new Resource(resourceKey, resourceName);
		resource.putValue("value", item);
		
		cp.publishResource(resource);
		
		cfm.getCentralizedLogger().log("Generated Resource with name: " + resourceName);
		System.out.println("Published resource " + item + "\n" + "having name " + resourceName + "\n");

	}

}
