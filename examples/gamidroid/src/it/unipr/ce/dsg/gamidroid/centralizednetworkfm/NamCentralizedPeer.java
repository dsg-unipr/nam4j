package it.unipr.ce.dsg.gamidroid.centralizednetworkfm;

import it.unipr.ce.dsg.gamidroid.custommessages.SearchResourceByType;
import it.unipr.ce.dsg.s2p.centralized.CentralizedPeer;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class NamCentralizedPeer extends CentralizedPeer implements IEventListener {

	private final NamCentralizedPeer namCentralizedPeer;
	
	/**
	 * List of events observers.
	 */
	private ArrayList<IEventListener> listeners;

	public NamCentralizedPeer(String pathConfig, String key) {
		super(pathConfig, key);
		namCentralizedPeer = this;
		
		listeners = new ArrayList<IEventListener>();

		/*
		 * Registering for the observer pattern so that when the required
		 * resource is received, this node gets notified
		 */
		namCentralizedPeer.addEventListener(namCentralizedPeer);
	}
	
	/**
	 * Listener to notify an event.
	 */
	public void addListener(IEventListener eventListener) {
		listeners.add(eventListener);
	}

	@Override
	public void onFoundSearchedResource(Resource resource) {
		System.out.println("I am NamCentralizedPeer and I have found the resource I looked for: " + resource.getName());

		for(String key : resource.getKeySet()) {
			String value = (String) resource.getValue(key);
			System.out.println("- " + key + " ; " + value);
		}
		
		/* Informing the listeners that a resource has been received */
		for (IEventListener listener : listeners) {
			listener.onFoundSearchedResource(resource);
		}
	}

	@Override
	public void onReceivedResourceToBeResponsible(Resource resource) {
		System.out.println("I am NamCentralizedPeer and I have received a resource to be its responsible: " + resource.getName());

		for(String key : resource.getKeySet()) {
			String value = (String) resource.getValue(key);
			System.out.println("- " + key + " ; " + value);
		}
		
		/* Informing the listeners that a resource has been received */
		for (IEventListener listener : listeners) {
			listener.onReceivedResourceToBeResponsible(resource);
		}
	}

	@Override
	public void onReceivedMessage(String message) {
		System.out.println("I am NamCentralizedPeer and I have received a message: " + message);
		
		/* Informing the listeners that a resource has been received */
		for (IEventListener listener : listeners) {
			listener.onReceivedMessage(message);
		}
	}
	
	/**
	 * Method to manage the receiving of a custom message defined in
	 * it.unipr.ce.dsg.gamidroid.custommessage package.
	 */
	@Override
	protected void onReceivedJSONMsg(JsonObject peerMsg, Address sender) {
		super.onReceivedJSONMsg(peerMsg, sender);
		
		String messageType = peerMsg.get("type").getAsString();
		
		/* Received a SearchResourceByType message */
		if(messageType.equals(SearchResourceByType.MSG_KEY)) {
			Gson gson = new Gson();
			
			/* The requestor peer */
			PeerDescriptor peer = gson.fromJson(peerMsg.get("peer").toString(), PeerDescriptor.class);
			
			/* Content of the message */
			String resourceCategory = gson.fromJson(peerMsg.get("resourceCategory").toString(), String.class);
			String resourceType = gson.fromJson(peerMsg.get("resourceType").toString(), String.class);
			Integer distance = gson.fromJson(peerMsg.get("resourceType").toString(), Integer.class);
			
			/* Peer's set of resources */
			Set<Resource> resources = namCentralizedPeer.getResources();
			
			/* This set will contain the resources matching the requirements */
			Set<Resource> matchingResources = resources = Collections.newSetFromMap(new ConcurrentHashMap<Resource, Boolean>());
			
			for(Resource resource : resources) {
				
				for(String key : resource.getKeySet()) {
					
					String item = (String) resource.getValue(key);
					
					/* Get the data for the resource */
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
				
					JsonObject latitudeElement = jobjectValue.getAsJsonObject("latitude");
					String latitude = latitudeElement.getAsJsonPrimitive("value").toString().replace("\"", "");
					
					JsonObject longitudeElement = jobjectValue.getAsJsonObject("longitude");
					String longitude = longitudeElement.getAsJsonPrimitive("value").toString().replace("\"", "");
					
					// if(...) {
					//	matchingResources.add(resource);
					//}
				}
			}
		}
		
		/* Received a SearchResourceByTypeResponse message */
		if(messageType.equals(SearchResourceByType.MSG_KEY)) {
			Gson gson = new Gson();
			
			Type type = new TypeToken<Collection<Resource>>(){}.getType();
			Collection<Resource> resourceSet = gson.fromJson(peerMsg.get("resources").toString(), type);
		}
	}

}
