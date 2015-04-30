package it.unipr.ce.dsg.examples.contextbus;

import it.unipr.ce.dsg.examples.ontology.Light;
import it.unipr.ce.dsg.examples.ontology.LightNotification;
import it.unipr.ce.dsg.examples.ontology.Room;
import it.unipr.ce.dsg.examples.ontology.Temperature;
import it.unipr.ce.dsg.examples.ontology.TemperatureNotification;
import it.unipr.ce.dsg.examples.reasonerfm.ReasonerFunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;
import it.unipr.ce.dsg.s2p.centralized.utils.Key;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * <p>
 * This class represents a NAM that connects to the network through the
 * {@link ContextBusBootstrap} and utilizes the features provided by the context
 * bus.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ContextBusTestNam extends NetworkedAutonomicMachine {
	
	private MeshContextBus contextBus = null;
	private FunctionalModule reasonerFunctionalModule = null;
	private MeshContextPeer peer;
	
	/**
	 * Constructor
	 * 
	 * @param poolSize
	 * @param migrationStorePath
	 * @param trialsNumber
	 * @param pathConfig
	 */
	public ContextBusTestNam(int poolSize, String migrationStorePath, int trialsNumber, String pathConfig, String contextEventName, String contextEventLocation, String contextEventValue) {
		super(poolSize, migrationStorePath, trialsNumber, Platform.DESKTOP);
		
		Key key = new Key((new Random().nextInt()) + "");

		// Random key
		Random ran = new Random();
		
		// Random port
		int port = 1024 + ran.nextInt(9999 - 1024);
		
		this.setId("CBusNam");
		
		peer = new MeshContextPeer(pathConfig, key.toString(), key.toString(), port);
		
		peer.setContextEventLocation(contextEventLocation);
		peer.setContextEventName(contextEventName);
		peer.setContextEventValue(contextEventValue);
		
		contextBus = new MeshContextBus(this, peer.getPeerConfig().getName());
		this.addFunctionalModule(contextBus);
		
		reasonerFunctionalModule = new ReasonerFunctionalModule(this);
		this.addFunctionalModule(reasonerFunctionalModule);
	}
	
	private MeshContextBus getContextBus(){
		return contextBus;
	}
	
	private MeshContextPeer getContextPeer(){
		return peer;
	}
	
	private ContextEvent setContextEvent(MeshContextPeer peer) {
		
		if (peer.getContextEventName().equals("TemperatureNotification")) {
			
			Temperature temperature = new Temperature();
			temperature.setId("i21");
			temperature.setValue(peer.getContextEventValue());
			TemperatureNotification tempNotif = new TemperatureNotification();
			Room room = new Room();
			room.setValue(peer.getContextEventLocation());
			tempNotif.setLocation(room);
			tempNotif.setSubject(temperature);
			Date timestamp = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			tempNotif.setTimestamp(df.format(timestamp));
			
			return tempNotif;
		}
		else if (peer.getContextEventName().equals("LightNotification")) {
			
			Light light = new Light();
			light.setId("i21");
			light.setValue(peer.getContextEventValue());
			LightNotification lightNotif = new LightNotification();
			Room room = new Room();
			room.setValue(peer.getContextEventLocation());
			lightNotif.setLocation(room);
			lightNotif.setSubject(light);
			Date timestamp = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			lightNotif.setTimestamp(df.format(timestamp));
			
			return lightNotif;
		}	
		else
			return null;
	}
	
	/**
	 * args[0]: the path to the config file
	 * 
	 * args[1]: a string indicating the network structure
	 * 
	 * args[2]: the action (subscribe, publish)
	 * 
	 * args[3]: context event name
	 * 
	 * args[4]: context event room name
	 * 
	 * args[5]: context event value (just for publishers)
	 * 
	 */
	public static void main(String[] args) {
		
		if (args.length < 5 || args.length > 6) {
			System.out
					.println("To publish context events specify six parameters:\n- the path to the config file\n- a string describing the network structure (e.g. full_mesh or random_graph)\n- the action (i.e. publish)\n- the mame of the event\n- the location of the event\n- the value of the event");
			System.out
					.println("To subscribe or unsubscribe to context events notifications specify five parameters:\n- the path to the config file\n- a string describing the network structure (e.g. full_mesh or random_graph)\n- the action (i.e. publish)\n- the mame of the event\n- the location of the event");
		} else {
		
			// If the node wants to publish, 6 arguments are needed; if the node wants to subscribe to an event, 5 arguments are needed
			boolean publisherNode = (args.length == 6); // true if the node is a publisher, false otherwise
			ContextBusTestNam cBusNam = (publisherNode) ? new ContextBusTestNam(10, "examples/migration", 3, args[0], args[3], args[4], args[5]) : new ContextBusTestNam(10, "examples/migration", 3, args[0], args[3], args[4], null);
			
			MeshContextBus contextBus = cBusNam.getContextBus(); 
			MeshContextPeer peer = cBusNam.getContextPeer();
			
			contextBus.setId(peer.getPeerDescriptor().getContactAddress());
			
			System.out.println(peer.getPeerDescriptor());
			
			peer.join(peer.getPeerConfig().getBootstrapContactAddress());
			
			peer.registerObserver(contextBus);
			
			ManageInputRunnable manageInputRunnable = new ManageInputRunnable(peer);
			Thread manageInput = new Thread(manageInputRunnable);
			manageInput.start();
								
			if (args[2].equalsIgnoreCase(Utils.SUBSCRIBE_REQUEST)) {
				
				try {
					Thread.sleep(Utils.THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (args[1].equalsIgnoreCase(Utils.FULL_MESH)) {
					
					// Full mesh structure
					
					// If a full mesh structure is in use, the number of hops is set to -1
					contextBus.subscribe(peer.getContextEventName(), cBusNam.reasonerFunctionalModule.getId(), null, peer, peer.getPeerList(), -1);
					System.out.println("--- Subscribed to event: " + peer.getContextEventName());
					
					try {
						Thread.sleep(Utils.THREAD_SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// If a full mesh structure is in use, the number of hops is set to -1
					contextBus.unsubscribe(peer.getContextEventName(), cBusNam.reasonerFunctionalModule.getId(), null, peer, peer.getPeerList(), -1);
					
				} else if (args[1].equalsIgnoreCase( Utils.RANDOM_GRAPH)) {
					
					// Random graph structure
					
					contextBus.subscribe(peer.getContextEventName(), cBusNam.reasonerFunctionalModule.getId(), null, peer, peer.getPeerList(), Utils.HOPS_NUMBER);
					System.out.println("--- Subscribed to event: " + peer.getContextEventName());
					
					try {
						Thread.sleep(3 * Utils.THREAD_SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					contextBus.unsubscribe(peer.getContextEventName(), cBusNam.reasonerFunctionalModule.getId(), null, peer, peer.getPeerList(), Utils.HOPS_NUMBER);
				}
			}
			
			if (args[2].equalsIgnoreCase(Utils.PUBLISH_REQUEST)) {
				
				while(true) {
	
					contextBus.publish(cBusNam.setContextEvent(peer), peer);
					
					try {
						Thread.sleep(Utils.EVENT_PUBLISHING_RATE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
