package it.unipr.ce.dsg.examples.centralizednetworkfm;

import it.unipr.ce.dsg.examples.centralizednetworkfm.custommessages.SearchResourceByLocation;
import it.unipr.ce.dsg.examples.centralizednetworkfm.custommessages.SearchResourceResponse;
import it.unipr.ce.dsg.examples.centralizednetworkfm.utils.Location;
import it.unipr.ce.dsg.s2p.centralized.CentralizedPeer;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.message.JoinResponseMessage;
import it.unipr.ce.dsg.s2p.centralized.utils.Key;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2p.example.peer.PeerConfig;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

	public NamCentralizedPeer(String pathConfig, String key, String name,
			int port) {
		super(pathConfig, key, name, port);
		namCentralizedPeer = this;

		/*
		 * Registering for the observer pattern so that when the required
		 * resource is received, this node gets notified
		 */
		namCentralizedPeer.addEventListener(namCentralizedPeer);

		PeerConfig peerConfig = new PeerConfig(pathConfig);
		final String bootstrapPeerAddress = peerConfig.bootstrap_peer;

		/* Commands */

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while(true) {

					System.out.println();
					System.out.println("*******************************");
					System.out.println("*  ping: send a PING message  *");
					System.out.println("*  k: show known nodes        *");
					System.out.println("*  p: publish a resource      *");
					System.out.println("*  r: show resources          *");
					System.out.println("*  d: delete resource         *");
					System.out.println("*  f: find resource           *");
					System.out.println("*  q: quit                    *");
					System.out.println("*******************************\n");

					try {
						String choice = br.readLine().trim();

						if (choice.equals("ping")) {
							System.out.print("Peer contact address: ");
							String contactAddress = br.readLine().trim();
							namCentralizedPeer.pingToPeer(contactAddress);
						} else if (choice.equals("k")) {
							namCentralizedPeer.printKnownPeers();
						} else if (choice.equals("p")) {
							System.out.print("Resource name: ");
							String resourceName = br.readLine().trim();
							System.out.print("Resource value: ");
							String resourceValue = br.readLine().trim();

							Key resourceKey = new Key(resourceName);
							Resource resource = new Resource(resourceKey, resourceName);
							resource.putValue("value", resourceValue);

							namCentralizedPeer.publishResource(resource);

						} else if (choice.equals("r")) {
							namCentralizedPeer.printResources();
						} else if (choice.equals("d")) {
							System.out.print("Resource name: ");
							String resourceName = br.readLine().trim();
							namCentralizedPeer.removeResource(resourceName);
						} else if (choice.equals("f")) {
							System.out.print("Find resource having name: ");
							String resourceName = br.readLine().trim();
							namCentralizedPeer.searchResource(resourceName);
						} else if (choice.equals("q")) {
							namCentralizedPeer.leave(bootstrapPeerAddress);
							br.close();
							System.exit(0);
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		});

		t.start();
	}

	@Override
	public void onFoundSearchedResource(Resource resource) {
		System.out.println("I am NamCentralizedPeer and I have found the resource I looked for: " + resource.getName());

		for(String key : resource.getKeySet()) {
			String value = (String) resource.getValue(key);
			System.out.println("- " + key + " ; " + value);
		}
	}

	@Override
	public void onReceivedResourceToBeResponsible(Resource resource) {
		System.out.println("I am NamCentralizedPeer and I have received a resource to be its responsible: " + resource.getName());

		for(String key : resource.getKeySet()) {
			String value = (String) resource.getValue(key);
			System.out.println("- " + key + " ; " + value);
		}
	}

	/**
	 * Observer for messages received on the Mesh network.
	 * 
	 * @param peerMsg
	 * 			The received message
	 */
	@Override
	public void onReceivedMessage(JsonObject peerMsg) {
		
		String messageType = peerMsg.get("type").getAsString();
		
		System.out.println("I am NamCentralizedPeer and I have received a message: " + messageType);
		
		if (messageType.equalsIgnoreCase(JoinResponseMessage.MSG_KEY)) {
			
			/* Looking for available resources by asking to the peers received from the bootstrap */
			
			Gson gson = new Gson();
			Type type = new TypeToken<Collection<PeerDescriptor>>(){}.getType();
			Collection<PeerDescriptor> peerSet = gson.fromJson(peerMsg.get("peers").toString(), type);
			for(PeerDescriptor peerDescriptor : peerSet) {
				System.out.println("----- Sending a message to peer " + peerDescriptor.getAddress() + " to know if it has nearby resources");
				SearchResourceByLocation searchResourceByLocation = new SearchResourceByLocation(namCentralizedPeer.peerDescriptor, 5, 44.765, 10.3);
				namCentralizedPeer.sendMessage(new Address(peerDescriptor.getAddress()), new Address(peerDescriptor.getContactAddress()), namCentralizedPeer.getAddress(), searchResourceByLocation.getJSONString(), "application/json");
			}
		}
		
		/* Received a SearchResourceByType message */
		else if (messageType.equals(SearchResourceByLocation.MSG_KEY)) {
			Gson gson = new Gson();
			
			/* The requestor peer */
			PeerDescriptor peer = gson.fromJson(peerMsg.get("peer").toString(), PeerDescriptor.class);
			
			System.out.println("----- Received a message from peer " + peer.getAddress() + " to know if I have resources near it");
			
			/* Content of the message */
			Double distance = gson.fromJson(peerMsg.get("distance").toString(), Double.class);
			Double requestorLat = gson.fromJson(peerMsg.get("peerLat").toString(), Double.class);
			Double requestorLgt = gson.fromJson(peerMsg.get("peerLgt").toString(), Double.class);
			
			/* Peer's set of resources */
			Set<Resource> resources = namCentralizedPeer.getResources();
			
			/* This set will contain the resources matching the requirements */
			Set<Resource> matchingResources = Collections.newSetFromMap(new ConcurrentHashMap<Resource, Boolean>());
			
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
					
					Location requestorLocation = new Location(requestorLat, requestorLgt);
					Location currentResourceLocation = new Location(Double.parseDouble(latitude), Double.parseDouble(longitude));
					
					System.out.println("------ Resource " + resource.getName() + " is " + requestorLocation.distanceFrom(currentResourceLocation) + " km far");
					
					/* If current resource is in the requested range (centered in requestor's location)
					 * the resource is added to the set to be returned */
					if (requestorLocation.distanceFrom(currentResourceLocation) < distance) {
						matchingResources.add(resource);
					}
				}
			}
			
			System.out.println("----- I have " + matchingResources.size() + " resources near the requestor peer");
			
			if (matchingResources.size() > 0) {
				SearchResourceResponse searchResourceByTypeResponse = new SearchResourceResponse(namCentralizedPeer.peerDescriptor, matchingResources);
				namCentralizedPeer.sendMessage(new Address(peer.getAddress()), new Address(peer.getContactAddress()), namCentralizedPeer.getAddress(), searchResourceByTypeResponse.getJSONString(), "application/json");
			}
		}
		
		/* Received a SearchResourceByTypeResponse message */
		else if (messageType.equals(SearchResourceResponse.MSG_KEY)) {
			Gson gson = new Gson();
			
			Type type = new TypeToken<Collection<Resource>>(){}.getType();
			Collection<Resource> resourceSet = gson.fromJson(peerMsg.get("resources").toString(), type);
			
			for(Resource resource : resourceSet) {
				System.out.println("----- Received info about nearby resource: " + resource.getName());
			}
		}
	}

}
