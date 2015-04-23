package it.unipr.ce.dsg.examples.chordfm;

import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;
import it.unipr.ce.dsg.s2pchord.ChordPeer;
import it.unipr.ce.dsg.s2pchord.resource.ResourceDescriptor;
import it.unipr.ce.dsg.s2pchord.resource.ResourceParameter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class PublishRunnable implements Runnable {

	ChordFunctionalModule cfm = null;
	private String item = null;
	private ChordPeer cp = null;

	public PublishRunnable(ChordFunctionalModule cfm, String item, ChordPeer cp) {
		this.cfm = cfm;
		this.item = item;
		this.cp = cp;
	}

	public void run() {

		cfm.getLogger().log("Service: Publish " + item);

		Gson gson = new Gson();
		ContextEvent ce = gson.fromJson(item, ContextEvent.class);

		ResourceDescriptor rd = new ResourceDescriptor();
		rd.setAttachment(item);
		
		// The name of the resource is computed by parsing the resource itself
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
	    rd.setName(resourceName);

		if (ce.getName() != null)
			rd.setType(ce.getName()); // type of resource

		rd.setResourceOwner(cp.getPeerDescriptor());

		if (ce.getSubject() != null)
			rd.addParameter(new ResourceParameter("Subject", ce.getSubject()
					.getName()));

		if (ce.getAction() != null)
			rd.addParameter(new ResourceParameter("Action", ce.getAction()
					.getName()));

		if (ce.getObject() != null)
			rd.addParameter(new ResourceParameter("Object", ce.getObject()
					.getName()));

		if (ce.getLocation() != null)
			rd.addParameter(new ResourceParameter("Location", ce.getLocation()
					.getValue()));

		/*
		 * if (ce.getTimestamp() != null) rd.addParameter(new
		 * ResourceParameter("Timestamp", ce.getTimestamp()));
		 */
		rd.generateResourceKey();

		String resourceKey = rd.getKey();
		
		System.out.println("----------------- resourceKey = " + resourceKey);
		
		cfm.getLogger().log("Generated Resource String: " + resourceKey);
		cfm.getLogger().log(
				"Generated Resource Descriptor: "
						+ rd.resourceDescriptorToString());
		// Resource res = new Resource(rd, null, System.currentTimeMillis());

		cp.publishResource(rd);
	}

}
