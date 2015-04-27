package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.messages.RequestItemAnswerMessage;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.Dependency;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.SAXHandler;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * <p>
 * This class manages the dependencies for a certain item. When a node has to
 * receive an item, it may miss a number of libraries required by it. This class
 * gets the list of requested libraries so that the node can identify which ones
 * is missing.
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
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ManageDependencies {
	
	/** The path where dependencies are stored */
	private String migrationStore;
	
	/**
	 * Constructor.
	 * 
	 * @param migrationStore
	 *            the path where dependencies are stored
	 */
	public ManageDependencies(String migrationStore) {
		setMigrationStore(migrationStore);
	}
	
	/**
	 * Method to get the path where dependencies are stored.
	 * 
	 * @return the path where dependencies are stored
	 */
	public String getMigrationStore() {
		return migrationStore;
	}

	/**
	 * Method to get the path where dependencies are stored.
	 * 
	 * @param migrationStore
	 *            the path where dependencies are stored
	 */
	public void setMigrationStore(String migrationStore) {
		this.migrationStore = migrationStore;
	}
	
	public String answerToCopyRequest(String conversationKey, String itemId, Platform p, MigrationSubject r, PeerDescriptor peerDescriptor, PeerDescriptor senderPeerDescriptor) {
		
		HashMap<String, String> items = getDependenciesForItem(r, itemId, p);
		
		if (items != null) {
			RequestItemAnswerMessage requestMigrationAnswerMessage = new RequestItemAnswerMessage(conversationKey);
			
			for(String key : items.keySet()) {
				requestMigrationAnswerMessage.addItem(key, items.get(key));
			}
			
			return requestMigrationAnswerMessage.getJSONString();
		} else {
			return null;
		}
	}
	
	/**
	 * Method to get the list of dependencies for an item to be migrated. Such a
	 * list is taken from an XML file associated to the item.
	 * 
	 * @param r
	 *            The {@link MigrationSubject} of the item to be migrated
	 * 
	 * @param itemId
	 *            The id of the item to be migrated
	 * 
	 * @param p
	 *            The client {@link NetworkedAutonomicMachine#Platform}
	 * 
	 * @return the list of dependencies for the item to be migrated
	 */
	public HashMap<String, String> getDependenciesForItem(MigrationSubject r, String itemId, Platform p) {
		HashMap<String, String> items = new HashMap<String, String>();
		
		System.out.println("--- Getting the list of dependencies");
		
		try {
			// The list of dependencies is stored in an xml file having the same name of the item id
			
			File infoFile = new File(this.getMigrationStore() + itemId + MobilityUtils.INFO_FILE_EXTENSION);
			FileInputStream infoFis = new FileInputStream(infoFile);
		
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
			SAXHandler handler = new SAXHandler();
			parser.parse(infoFis, handler);
			
			MigrationSubject migrationSubject = handler.getLibraryInformation().getType();
			
			// Check if the owned item is of the same type as the one
			// requested by the client - e.g. the client requested a FM, but
			// the owned item is a Service
			if (migrationSubject != null && !migrationSubject.equals(r)) {
				System.err.println(MobilityUtils.ERROR_IN_RESOURCE_TYPE + " (client requested a " + r + ", but I have a " + migrationSubject + ")");
				return null;
			}
			
			for (Dependency dependency : handler.getDependencyList()) {
				System.out.println("\tDependency " + dependency.getId() + " (v. " + dependency.getVersion() + ")");
				items.put(dependency.getId(), dependency.getVersion());
			}
			
			System.out.println("General info: " + handler.getLibraryInformation().getId() + " ; " + handler.getLibraryInformation().getMainClass() + " (v. " + handler.getLibraryInformation().getVersion() + ")");
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println(MobilityUtils.MISSING_XML_FILE);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	/**
	 * When an item has to be migrated, the node that will send it also sends
	 * the list of required libraries. Thus, the receiving node can check if it
	 * has them all. The method performs the check and returns the list of
	 * missing libraries.
	 * 
	 * @param p
	 *            The platform of the requesting node
	 *            {@link NetworkedAutonomicMachine#Platform}
	 * 
	 * @param dependencies
	 *            The list of dependencies for the requested item
	 * 
	 * @return the list of missing dependencies as pairs (library_id, library_version)
	 */
	public HashMap<String, String> getMissingDependenciesList(Platform p, HashMap<String, String> dependencies, MigrationSubject r, MccNamPeer mccNamPeer) {
		
		System.out.println("Received the list of dependencies for the requested item (size = " + dependencies.size() + ")");
		
		HashMap<String, String> missingItems = new HashMap<String, String>();
		HashMap<String, String> missingXmlFiles = new HashMap<String, String>();
		
		Iterator<Entry<String, String>> it = dependencies.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> pairs = (Entry<String, String>) it.next();
			
			String dependencyId = pairs.getKey();
			String dependencyMinVersion = pairs.getValue();
			
			if (dependencyMinVersion.equals(MobilityUtils.INFO_FILE_ID)) {
				// Current file is the xml info file for a dependency, so it must be requested
				missingXmlFiles.put(dependencyId, dependencyMinVersion);
				System.out.println("------ Dependency " + dependencyId + " is an info file, waiting to check if the associated FM is available");
			} else if(dependencyMinVersion.equals(MobilityUtils.RESOURCE_FILE_ID)) {
				// Current file is a resource file, so it must be requested
				missingItems.put(dependencyId, dependencyMinVersion);
				System.out.println("------ Dependency " + dependencyId + " is a resource file, so I am requesting it.");
			} else {
				
				System.out.println("------ Analyzing dependency " + dependencyId);
				
				// Check in the XML file of the owned dependency if the version
				// is >= than the requested minimum one. If not, or if no XML
				// file is in the jar, request it
			
				String libVersion = "";
				
				try {
					File infoFile = new File(this.getMigrationStore() + dependencyId + MobilityUtils.INFO_FILE_EXTENSION);
					if (infoFile.exists()) {
						System.out.println((this.getMigrationStore() + dependencyId + MobilityUtils.INFO_FILE_EXTENSION) + " exists");
						
						// If the xml info file exists, then check for its version
						FileInputStream infoFis = new FileInputStream(infoFile);
					
						SAXParserFactory parserFactor = SAXParserFactory.newInstance();
						SAXParser parser = parserFactor.newSAXParser();
						SAXHandler handler = new SAXHandler();
						parser.parse(infoFis, handler);
					
						libVersion = handler.getLibraryInformation().getVersion();
						
						if (Float.compare(Float.parseFloat(libVersion), Float.parseFloat(dependencyMinVersion)) < 0) {
							// An older version of the item is available; request the updated version
							System.out.println("An older version (" + libVersion + ") of dependency " + dependencyId + " is available; requesting the updated version (" + dependencyMinVersion + ")");
							missingItems.put(dependencyId, dependencyMinVersion);
						} else {
							System.out.println(MobilityUtils.CLIENT_DEPENDENCY_AVAILABLE + dependencyId);
								
							// Add dependency to class path
							if (mccNamPeer.getNam().getClientPlatform(0) == Platform.DESKTOP) {
								MobilityUtils.addToClassPath(mccNamPeer.getNam(), this.getMigrationStore() + dependencyId + MobilityUtils.DESKTOP_FILE_EXTENSION, null, null);
							} else if (mccNamPeer.getNam().getClientPlatform(0) == Platform.ANDROID) {
								
								// Use observer pattern to add dependency to class path for Android
								mccNamPeer.notifyObservers(this.getMigrationStore() + dependencyId + MobilityUtils.ANDROID_FILE_EXTENSION, null, null, null, null);
							}
						}
					} else {
						System.out.print((this.getMigrationStore() + dependencyId + MobilityUtils.INFO_FILE_EXTENSION) + " is not available.");
						
						// If the xml info file does not exist, the dependency is not a FM so just check for its availability
						
						File f = MobilityUtils.getRequestedItem(pairs.getKey(), p, getMigrationStore());
						if (f == null || !f.exists()) {
							missingItems.put(pairs.getKey(), dependencyMinVersion);
							System.out.print("I do not have such a dependency, so I am requesting it.\n");
						} else {
							System.out.println("I already have the library, so I will not request it.");
							
							// Add dependency to class path
							if (mccNamPeer.getNam().getClientPlatform(0) == Platform.DESKTOP) {
								MobilityUtils.addToClassPath(mccNamPeer.getNam(), this.getMigrationStore() + dependencyId + MobilityUtils.DESKTOP_FILE_EXTENSION, null, null);
							} else if (mccNamPeer.getNam().getClientPlatform(0) == Platform.ANDROID) {
								
								// Use observer pattern to add dependency to class path for Android
								mccNamPeer.notifyObservers(this.getMigrationStore() + dependencyId + MobilityUtils.ANDROID_FILE_EXTENSION, null, null, null, null);
							}
						}
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// The available library misses XML file; request the dependency
					// System.out.println("Dependency " + dependencyId + " is missing XML description file; requesting it");
					// missingItems.put(dependencyId, dependencyMinVersion);
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Add to missingItems the xml info files for which the associated FM is going to be requested as well
		// Xml info files for available FMs will not be requested
		for(Entry<String, String> entry : missingXmlFiles.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();

		    if (missingItems.containsKey(key.replace(MobilityUtils.INFO_FILE_EXTENSION, ""))) {
		    	// Adding to the requested info files
		    	System.out.println("Adding " + key + " to missing files");
		    	missingItems.put(key, value);
		    }
		}
			
		return missingItems;
	}

}
