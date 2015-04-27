package it.unipr.ce.dsg.nam4j.impl.mobility.peer;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.messages.AllDependenciesAreAvailableMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.DependencyChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.InfoFileChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.InfoFileIsAvailableMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.ItemChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.ItemIsAvailableMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.ItemNotAvailableMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.MigrationFailedMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PingMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PongMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.ReceivedStateMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.RequestCopyMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.RequestDependenciesMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.RequestItemAnswerMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.RequestMigrateMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.StateChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.mobility.CopyActionImplementation;
import it.unipr.ce.dsg.nam4j.impl.mobility.MigrateActionImplementation;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.ConversationItem;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.Conversations;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.DependencyChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.InfoFileChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.ItemChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.ManageDependencies;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.StateChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.SAXHandler;
import it.unipr.ce.dsg.nam4j.impl.peer.NamPeer;
import it.unipr.ce.dsg.nam4j.impl.service.Service;
import it.unipr.ce.dsg.nam4j.interfaces.IMobilityItemAvailability;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;
import it.unipr.ce.dsg.s2p.util.FileHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.zoolu.tools.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>
 * This class represents a peer that manages mobility actions.
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

public class MccNamPeer extends NamPeer {
	
	private FileHandler fileHandler;
	private Log log;
	private NetworkedAutonomicMachine nam;
	
	/** The list of ongoing conversations. */
	private Conversations conversations;
	
	/** The list of received chunks for state migration */
	private HashMap<String, ArrayList<StateChunk>> receivedStateChunkList = null;
	
	/** The list of received chunks for dependency migration */
	private HashMap<String, ArrayList<DependencyChunk>> receivedDependencyChunkList = null;
	
	/** The list of received chunks for item migration */
	private HashMap<String, ArrayList<InfoFileChunk>> receivedInfoFileChunkList = null;
	
	/** The list of received chunks for item migration */
	private HashMap<String, ArrayList<ItemChunk>> receivedItemChunkList = null;
	
	private CopyActionImplementation copyActionImplementation = null;
	private ManageDependencies manageDependencies = null;
	private MigrateActionImplementation migrateActionImplementation = null;
	
	/** An array of classes used for reflection */
	public static final Class<?>[] parameters = new Class[] { URL.class };
	
	/** A list of peers interested in mobility events related to file availability (mainly used to manage the Android platform) */
	private ArrayList<IMobilityItemAvailability> listeners;
	
	/**
	 * Class constructor.
	 * 
	 * @param configFilePath
	 *            The path to the configuration file
	 * 
	 * @param key
	 *            The key of the peer
	 * 
	 * @param name
	 *            The name of the peer
	 * 
	 * @param port
	 *            The port on which the peer is listening
	 * 
	 * @param nam
	 *            The {@link NetworkedAutonomicMachine} to which the peer is
	 *            associated
	 */
	public MccNamPeer(String configFilePath, String key, String name, int port, NetworkedAutonomicMachine nam) {
		super(configFilePath, key, name, port);
		this.nam = nam;
		init();
	}
	
	/** Method to perform the initial setup of the peer. */
	private void init() {
		
		fileHandler = new FileHandler();
		this.conversations = new Conversations();
		this.receivedStateChunkList = new HashMap<String, ArrayList<StateChunk>>();
		this.receivedDependencyChunkList = new HashMap<String, ArrayList<DependencyChunk>>();
		this.receivedInfoFileChunkList = new HashMap<String, ArrayList<InfoFileChunk>>();
		this.receivedItemChunkList = new HashMap<String, ArrayList<ItemChunk>>();
		
		this.listeners = new ArrayList<IMobilityItemAvailability>();
		
		if (nodeConfig.log_path != null){
			if (!fileHandler.isDirectoryExists(nodeConfig.log_path))
				fileHandler.createDirectory(nodeConfig.log_path);

			log = new Log(nodeConfig.log_path + "info_" + peerDescriptor.getAddress() + ".log", Log.LEVEL_MEDIUM);
		}
	}
	
	/**
	 * Method to get the associated {@link NetworkedAutonomicMachine}.
	 * 
	 * @return the associated {@link NetworkedAutonomicMachine}
	 */
	public NetworkedAutonomicMachine getNam() {
		return this.nam;
	}
	
	/**
	 * Method to register a listener for item availability notifications.
	 * 
	 * @param listener
	 *            The listener to be registered
	 */
	public void addMobilityItemAvailabilityListener(IMobilityItemAvailability listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Method which requests to a peer if it is available to receive a
	 * {@link FunctionalModule} and the list of its dependencies to continue the
	 * execution.
	 * 
	 * @param peerToBeContactedAddress
	 *            the contact address of the receiving peer
	 * 
	 * @param fmId
	 *            the id of the {@link FunctionalModule} to be migrated
	 * 
	 * @param platform
	 *            the target {@link NetworkedAutonomicMachine#Platform}
	 * 
	 * @param action
	 *            the required {@link NetworkedAutonomicMachine#Action}
	 * 
	 * @param r
	 *            the required
	 *            {@link NetworkedAutonomicMachine#MigrationSubject}
	 * 
	 * @param object
	 *            the {@link Object} representing the execution state
	 * 
	 * @param version
	 *            the version of the {@link FunctionalModule} to be migrated
	 */
	public void migrateFM(String peerToBeContactedAddress, String fmId, Platform platform, Action action, Object object, MigrationSubject r, String version) {
		if (migrateActionImplementation == null) {
			migrateActionImplementation = new MigrateActionImplementation(this.nam.getMigrationStore(), this);
		}
		
		// Create a new conversation
		String conversationKey = Conversations.generateConversationItemKey();
		ConversationItem conversationItem = new ConversationItem(conversationKey, peerToBeContactedAddress, fmId, object, version, action, MigrationSubject.FM, platform);
		this.conversations.add(conversationItem);
		
		if (manageDependencies == null) {
			manageDependencies = new ManageDependencies(this.nam.getMigrationStore());
		}
		HashMap<String, String> items = manageDependencies.getDependenciesForItem(r, fmId, platform);
		
		if (items != null) {
			RequestMigrateMessage peerMsg = new RequestMigrateMessage(conversationKey, peerDescriptor, platform, fmId, MigrationSubject.FM, action, items, version);
			sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
		} else {
			System.err.println(MobilityUtils.ERROR_PARSING_XML_FILE_FOR_DEPENDENCIES);
		}
	}
	
	/**
	 * Method which requests to a peer if it is available to receive a
	 * {@link Service} and the list of its dependencies to continue the
	 * execution.
	 * 
	 * @param peerToBeContactedAddress
	 *            the contact address of the receiving peer
	 * 
	 * @param serviceId
	 *            the id of the {@link Service} to be migrated
	 * 
	 * @param platform
	 *            the target {@link NetworkedAutonomicMachine#Platform}
	 * 
	 * @param action
	 *            the required {@link NetworkedAutonomicMachine#Action}
	 * 
	 * @param object
	 *            the {@link Object} representing the execution state
	 * 
	 * @param r
	 *            the required
	 *            {@link NetworkedAutonomicMachine#MigrationSubject}
	 * 
	 * @param version
	 *            the version of the {@link Service} to be migrated
	 */
	public void migrateService(String peerToBeContactedAddress, String serviceId, Platform platform, Action action, Object object, MigrationSubject r, String version) {
		if (migrateActionImplementation == null) {
			migrateActionImplementation = new MigrateActionImplementation(this.nam.getMigrationStore(), this);
		}
		
		// Create a new conversation
		String conversationKey = Conversations.generateConversationItemKey();
		ConversationItem conversationItem = new ConversationItem(conversationKey, peerToBeContactedAddress, serviceId, object, version, action, MigrationSubject.SERVICE, platform);
		this.conversations.add(conversationItem);
		
		if (manageDependencies == null) {
			manageDependencies = new ManageDependencies(this.nam.getMigrationStore());
		}
		HashMap<String, String> items = manageDependencies.getDependenciesForItem(r, serviceId, platform);
		
		if (items != null) {
			RequestMigrateMessage peerMsg = new RequestMigrateMessage(conversationKey, peerDescriptor, platform, serviceId, MigrationSubject.SERVICE, action, items, version);
			sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
		}  else {
			System.err.println(MobilityUtils.ERROR_PARSING_XML_FILE_FOR_DEPENDENCIES);
		}
	}
	
	/**
	 * Method to request a {@link FunctionalModule} to a peer.
	 * 
	 * @param peerToBeContactedAddress
	 *            the contact address of the receiving peer
	 * 
	 * @param fmId
	 *            the id of the {@link FunctionalModule} to be migrated
	 * 
	 * @param platform
	 *            the target {@link NetworkedAutonomicMachine#Platform}
	 * 
	 * @param action
	 *            the required {@link NetworkedAutonomicMachine#Action}
	 * 
	 * @param version
	 *            The minimum version of the requested item
	 */
	public void requestFM(String peerToBeContactedAddress, String fmId, Platform platform, Action action, String version) {
		// Create a new conversation
		String conversationKey = Conversations.generateConversationItemKey();
		ConversationItem conversationItem = new ConversationItem(conversationKey, peerToBeContactedAddress, fmId, null, version, action, MigrationSubject.FM, platform);
		this.conversations.add(conversationItem);
		RequestCopyMessage peerMsg = new RequestCopyMessage(conversationKey, peerDescriptor, platform, fmId, MigrationSubject.FM, action, version);
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
	}
	
	/**
	 * Method to request a {@link Service} to a peer.
	 * 
	 * @param peerToBeContactedAddress
	 *            the contact address of the receiving peer
	 * 
	 * @param serviceId
	 *            the id of the {@link Service} to be migrated
	 * 
	 * @param platform
	 *            the target {@link Service}
	 * 
	 * @param action
	 *            the required {@link Service}
	 * 
	 * @param version
	 *            The minimum version of the requested {@link Service}
	 */
	public void requestService(String peerToBeContactedAddress, String serviceId, Platform platform, Action action, String version) {
		// Create a new conversation
		String conversationKey = Conversations.generateConversationItemKey();
		ConversationItem conversationItem = new ConversationItem(conversationKey, peerToBeContactedAddress, serviceId, null, version, action, MigrationSubject.SERVICE, platform);
		this.conversations.add(conversationItem);
		RequestCopyMessage peerMsg = new RequestCopyMessage(conversationKey, peerDescriptor, platform, serviceId, MigrationSubject.SERVICE, action, version);
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
	}
	
	/**
	 * Method to send a message to another peer.
	 * 
	 * @param address
	 *            The address of the peer the message has to be sent to
	 * 
	 * @param message
	 *            The message to be sent
	 * 
	 * @param format
	 *            The format used to represent the message
	 */
	public void sendMessageToPeer(Address address, String message, String format) {
		sendMessage(address, address, this.getAddress(), message, format);
	}
	
	/**
	 * Method that notifies observers that an item (FM, Service or dependency)
	 * is available and has to be added to the class path. FM and Services will
	 * start the execution as well.
	 * 
	 * @param fileFullPath
	 *            The path of the file that has to be added to the class path,
	 *            including the file's name and extension
	 * 
	 * @param mainClassName
	 *            The main class name of the file that has to be added to the
	 *            class path if it is a FM or a Service. Null if it is a
	 *            dependency.
	 * 
	 * @param role
	 *            The {@link MigrationSubject} of the file that has to be added
	 *            to the class path.
	 */
	public void notifyObservers(String fileFullPath, String mainClassName, MigrationSubject role, Action action, Object state) {
		for (IMobilityItemAvailability listener : listeners) {
			listener.onItemIsAvailable(fileFullPath, mainClassName, role, action, state);
		}
	}
	
	/**
	 * Method to manage mobility actions errors.
	 * 
	 * @param conversationId
	 *            The id of the {@link ConversationItem} for which the error
	 *            occurred
	 */
	private void manageError(String conversationId) {
		
		// TODO: decide what to do when the mobility action fails
		
		// Remove conversation item from conversations list
		this.conversations.remove(conversationId);
	}
	
	/**
	 * Method to manage received JSON messages represented as {@link JsonObject}
	 * objects.
	 * 
	 * @param peerMsg
	 *            The received message
	 * 
	 * @param sender
	 *            The {@link Address} of the sender
	 */
	@Override
	protected void onReceivedJSONMsg(JsonObject peerMsg, Address sender) {
		Gson gson = new Gson();
		String messageType = peerMsg.get("type").getAsString();
		
		// Logging
		if (nodeConfig.log_path != null) {
			int msgLength = messageType.length();

			JsonObject info = new JsonObject();
			info.addProperty("timestamp", System.currentTimeMillis());
			info.addProperty("type", "recv");
			info.addProperty("typeMessage", messageType);
			info.addProperty("byte", msgLength);
			info.addProperty("sender", sender.getURL());

			printJSONLog(info, log, false);
		}
		
		if (messageType.equals(PingMessage.MSG_KEY) || messageType.equals(PongMessage.MSG_KEY)) {
			
			// If a PING or a PONG message is received, the node adds the sender
			// to the list of known peers (if not yet included)
			
			PeerDescriptor senderPeerDescriptor = gson.fromJson(peerMsg.get("peer").toString(), PeerDescriptor.class);
			
			if ((!(this.getPeerList().contains(senderPeerDescriptor))) && (!senderPeerDescriptor.getName().equals("bootstrap"))) {
				System.out.println("--- Adding a new peer to the peers list: " + senderPeerDescriptor.getContactAddress());

				this.getPeerList().add(senderPeerDescriptor);
				this.getPeerList().printPeerList();
			}
			
			if (messageType.equals(PingMessage.MSG_KEY)) {
				System.out.println("Received PING message from " + senderPeerDescriptor.getContactAddress() + "\nSending pong message to " + senderPeerDescriptor.getContactAddress());
				
				// If the message was a ping, the node answers with a pong
				pong(senderPeerDescriptor.getContactAddress());
			} else {
				System.out.println("Received PONG message from " + senderPeerDescriptor.getContactAddress());
			}
			
		} else if (messageType.equals(RequestCopyMessage.MSG_KEY)) {
			PeerDescriptor senderPeerDescriptor = gson.fromJson(peerMsg.get("peer").toString(), PeerDescriptor.class);
			
			String conversationItemId = peerMsg.get("conversationKey").getAsString();
			String senderContactAddress = senderPeerDescriptor.getContactAddress();
			
			// Get from the message the item id and requestor's platform
			String platform = peerMsg.get("platform").getAsString();
			String role = peerMsg.get("role").getAsString();
			String action = peerMsg.get("action").getAsString();
			String itemId = peerMsg.get("itemId").getAsString();
			String requiredLibVersion = peerMsg.get("version").getAsString();
			
			Platform p = Platform.toPlatform(platform);
			MigrationSubject r = MigrationSubject.toMigrationSubject(role);
			Action a = Action.toAction(action);
			
			// This is the first message of a new conversation, create a new one
			ConversationItem conversationItem = new ConversationItem(conversationItemId, senderContactAddress, itemId, null, requiredLibVersion, a, r, p);
			this.conversations.add(conversationItem);
			
			System.out.println("--- Received a " + action + " request for item " + itemId + " from a " + platform + " node");
			
			// Before checking for the dependencies, check if the item is
			// available and if its version is >= than the required one
			
			File file = null;
			
			// Check if the requested item is available
			file = MobilityUtils.getRequestedItem(itemId, p, this.nam.getMigrationStore());
			
			if (file != null) {
				SAXHandler handler = MobilityUtils.parseXMLFile(itemId, this.nam);
				String libVersion = handler.getLibraryInformation().getVersion();
				
				// Compare the version of the library required for the COPY
				// with the version of the available one. If the latter is
				// lower, than an ItemNotAvailableMessage is sent, otherwise the
				// list of dependencies is.
				if (Float.compare(Float.parseFloat(libVersion), Float.parseFloat(requiredLibVersion)) < 0) {
					System.err.println(MobilityUtils.SERVER_OLD_ITEM_AVAILABLE);
					
					ItemNotAvailableMessage itemNotAvailable = new ItemNotAvailableMessage(conversationItemId);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), itemNotAvailable.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				
					// Removing conversation
					this.conversations.remove(conversationItemId);
					
				} else {
					System.out.println(MobilityUtils.REQUESTED_ITEM_IS_AVAILABLE_SENDING_DEPENDENCIES);

					if (manageDependencies == null) {
						manageDependencies = new ManageDependencies(this.nam.getMigrationStore());
					}
					String jsonMessage = manageDependencies.answerToCopyRequest(conversationItemId, itemId, p, r, this.getPeerDescriptor(), senderPeerDescriptor);
					
					if (jsonMessage != null) {
						sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), jsonMessage, MobilityUtils.JSON_MESSAGE_FORMAT);
					} else {
						System.out.println(MobilityUtils.ERROR_REQUEST_COPY_MESSAGE);
						
						ItemNotAvailableMessage itemNotAvailable = new ItemNotAvailableMessage(conversationItemId);
						sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), itemNotAvailable.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
					}
				}
				
			} else {
				System.err.println(MobilityUtils.SERVER_ITEM_NOT_AVAILABLE);
				
				ItemNotAvailableMessage itemNotAvailable = new ItemNotAvailableMessage(conversationItemId);
				sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), itemNotAvailable.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
			
				// Removing conversation
				this.conversations.remove(conversationItemId);
			}
			
		} else if (messageType.equals(ItemNotAvailableMessage.MSG_KEY)) {

			String conversationId = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationId);
			
			System.err.println(MobilityUtils.CONTACTED_NODE_DOES_NOT_HAVE_ITEM + " whose id is " + conversationItem.getItemId());
			
			// Removing conversation
			this.conversations.remove(conversationId);
			
		} else if (messageType.equals(RequestItemAnswerMessage.MSG_KEY)) {
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationId);
			
			Platform p = conversationItem.getPlatform();
			String senderContactAddress = conversationItem.getPartnerContactAddress();
			MigrationSubject r = conversationItem.getRole();
			
			// Get the list of dependencies from the message
			Type type = new TypeToken<HashMap<String, String>>(){}.getType();
			HashMap<String, String> dependencies = gson.fromJson(peerMsg.get("items").toString(), type);
	
			if (manageDependencies == null) {
				manageDependencies = new ManageDependencies(this.nam.getMigrationStore());
			}
			
			HashMap<String, String> missingItems = manageDependencies.getMissingDependenciesList(p, dependencies, r, this);
			
			if (missingItems.size() > 0) {
				conversationItem.addMissingDependencies(missingItems);
				
				RequestDependenciesMessage dependencyMessage = new RequestDependenciesMessage(conversationId);
				
				Iterator<Entry<String, String>> missingIt = missingItems.entrySet().iterator();
				while(missingIt.hasNext()) {
					Entry<String, String> pairs = (Entry<String, String>) missingIt.next();
					dependencyMessage.addItem(pairs.getKey(), pairs.getValue());
				}
				String jsonMessage = dependencyMessage.getJSONString();
				
				if (jsonMessage != null) {
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), jsonMessage, MobilityUtils.JSON_MESSAGE_FORMAT);
				} else {
					System.err.println(MobilityUtils.ERROR_REQUEST_ITEM_ANSWER_MESSAGE);
					
					// Notify sender that the mobility action failed
					MigrationFailedMessage migrationFailedMessage = new MigrationFailedMessage(conversationId, MobilityUtils.ERROR_REQUEST_ITEM_ANSWER_MESSAGE);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), migrationFailedMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				
					manageError(conversationId);
				}
			} else {
				System.out.println(MobilityUtils.CLIENT_ALL_DEPENDENCIES_AVAILABLE);
				
				boolean infoFileIsAvailable = (new File(this.nam.getMigrationStore() + conversationItem.getItemId() + MobilityUtils.INFO_FILE_EXTENSION)).exists();
				if (infoFileIsAvailable) {
					// The info file is available
					System.out.println(MobilityUtils.INFO_FILE_AVAILABLE);
				
					File itemFile = MobilityUtils.getRequestedItem(conversationItem.getItemId(), p, this.nam.getMigrationStore());
					
					if (itemFile != null && itemFile.exists()) {
						
						SAXHandler handler = MobilityUtils.parseXMLFile(conversationItem.getItemId(), this.nam);
						String libVersion = handler.getLibraryInformation().getVersion();
						
						String requiredLibVersion = conversationItem.getItemVersion();
						
						// Compare the version of the library the peer wants to send
						// with the version of the available one. If the version
						// is >= than the required one, an
						// ItemIsAvailableMessage message is sent to ask for the
						// execution state, otherwise an
						// AllDependenciesAreAvailableMessage message is sent to
						// inform that all dependencies are available but not
						// the item.
						if (Float.compare(Float.parseFloat(libVersion), Float.parseFloat(requiredLibVersion)) < 0) {
							AllDependenciesAreAvailableMessage receivedAllDependencies = new AllDependenciesAreAvailableMessage(conversationId);
							sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedAllDependencies.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
							System.out.println(MobilityUtils.OLD_ITEM_AVAILABLE);
						} else {
							// The item is already available (the same required version or a new one)
							ItemIsAvailableMessage itemIsAvailable = new ItemIsAvailableMessage(conversationId);
							sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), itemIsAvailable.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
							
							if (conversationItem.getAction().equals(Action.COPY)) {
								String mainClassName = handler.getLibraryInformation().getMainClass();
								MigrationSubject role = conversationItem.getRole();
								
								if (nam.getClientPlatform(0) == Platform.DESKTOP) {
									System.out.println(MobilityUtils.STARTING_EXECUTION);
									
									Object obj = MobilityUtils.addToClassPath(this.nam, this.nam.getMigrationStore() + itemFile.getName(), mainClassName, role);
									
									if (role.equals(MigrationSubject.FM)) {
										FunctionalModule fm = (FunctionalModule) obj;
										fm.getFunctionalModuleRunnable().start();
									}
									else if (role.equals(MigrationSubject.SERVICE)) {
										
										// String functionalModuleId = handler.getLibraryInformation().getFunctionalModule();
										String functionalModuleId = handler.getFunctionalModuleForService().getId();
										
										System.out.println("------ The id of the FM associated to the received Service is " + functionalModuleId);
										
										// Adding the service to the associated functional module
										Service s = (Service) obj;
										
										boolean fmInfoFileIsAvailable = (new File(nam.getMigrationStore() + functionalModuleId + MobilityUtils.INFO_FILE_EXTENSION)).exists();
										if (fmInfoFileIsAvailable) {
											SAXHandler infoFmHandler = MobilityUtils.parseXMLFile(functionalModuleId, this.nam);
											String fmCompleteMainClassName = infoFmHandler.getLibraryInformation().getMainClass();
											
											FunctionalModule fm = MobilityUtils.addServiceToFm(s, functionalModuleId, this.nam, fmCompleteMainClassName);
											s.setFunctionalModule(fm);

											// Starting the service
											s.getServiceRunnable().start();
											
										} else {
											System.err.println("The info file for the FM to which the Service is associated is not available");
										}
									}
									
									System.out.println("COPY " + MobilityUtils.ACTION_SUCCESSFUL);
									
								} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
									
									// Use observer pattern for notifications
									notifyObservers(this.nam.getMigrationStore() + itemFile.getName(), mainClassName, role, conversationItem.getAction(), null);
								}
								
							} else if (conversationItem.getAction().equals(Action.MIGRATE)) {
								System.out.println(MobilityUtils.ITEM_AVAILABLE_WAITING_FOR_STATE);
								
								// The class is added to the class path, but not instantiated (the second argument is null)
								if (nam.getClientPlatform(0) == Platform.DESKTOP) {
									MobilityUtils.addToClassPath(this.nam, itemFile.getAbsolutePath(), null, conversationItem.getRole());
								} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
									
									// Use observer pattern for notifications
									notifyObservers(itemFile.getAbsolutePath(), null, conversationItem.getRole(), conversationItem.getAction(), null);
								}
							}
						}
					} else {
						System.out.println(MobilityUtils.ITEM_NOT_AVAILABLE);
						InfoFileIsAvailableMessage infoFileIsAvailableMessage = new InfoFileIsAvailableMessage(conversationId);
						sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), infoFileIsAvailableMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
					}
				} else {
					// Requesting info file
					System.out.println(MobilityUtils.INFO_FILE_NOT_AVAILABLE);
					AllDependenciesAreAvailableMessage receivedAllDependencies = new AllDependenciesAreAvailableMessage(conversationId);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedAllDependencies.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				}
			}
			
		} else if (messageType.equals(RequestMigrateMessage.MSG_KEY)) {
			
			PeerDescriptor senderPeerDescriptor = gson.fromJson(peerMsg.get("peer").toString(), PeerDescriptor.class);
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			String senderContactAddress = senderPeerDescriptor.getContactAddress();
			
			// The itemId and platform are taken from the response message since
			// more requests can be made in parallel and thus, a class variable
			// cannot be used to store the requested item's id
			String platform = peerMsg.get("platform").getAsString();
			String role = peerMsg.get("role").getAsString();
			String action = peerMsg.get("action").getAsString();
			String itemId = peerMsg.get("itemId").getAsString();
			String requiredLibVersion = peerMsg.get("version").getAsString();
			
			Platform p = Platform.toPlatform(platform);
			MigrationSubject r = MigrationSubject.toMigrationSubject(role);
			Action a = Action.toAction(action);
			
			// This is the first message of a new conversation, create a new one
			ConversationItem conversationItem = new ConversationItem(conversationId, senderContactAddress, itemId, null, requiredLibVersion, a, r, p);
			this.conversations.add(conversationItem);
			
			// Get the list of dependencies from the message
			Type type = new TypeToken<HashMap<String, String>>(){}.getType();
			HashMap<String, String> dependencies = gson.fromJson(peerMsg.get("items").toString(), type);
	
			System.out.println("--- Received a " + action + " request for item " + itemId + " from a " + platform + " node whose dependencies are: ");
			
			for (String key : dependencies.keySet()) {
		        System.out.print(key + "; ");
		    }
			System.out.print("\n");
			
			if (manageDependencies == null) {
				manageDependencies = new ManageDependencies(this.nam.getMigrationStore());
			}
			
			// TODO: decide whether to accept or not a MIGRATE request
			
			HashMap<String, String> missingItems = manageDependencies.getMissingDependenciesList(p, dependencies, r, this);
			
			if (missingItems.size() > 0) {
				conversationItem.addMissingDependencies(missingItems);
				
				RequestDependenciesMessage dependencyMessage = new RequestDependenciesMessage(conversationId);
				
				// Add dependencies
				Iterator<Entry<String, String>> missingIt = missingItems.entrySet().iterator();
				while(missingIt.hasNext()) {
					Entry<String, String> pairs = (Entry<String, String>) missingIt.next();
					dependencyMessage.addItem(pairs.getKey(), pairs.getValue());
				}
				String jsonMessage = dependencyMessage.getJSONString();
				
				if (jsonMessage != null) {
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), jsonMessage, MobilityUtils.JSON_MESSAGE_FORMAT);
				} else {
					System.err.println(MobilityUtils.ERROR_REQUEST_MIGRATE_MESSAGE);
					
					// Notify sender that the mobility operation failed
					MigrationFailedMessage migrationFailedMessage = new MigrationFailedMessage(conversationId, MobilityUtils.ERROR_REQUEST_MIGRATE_MESSAGE);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), migrationFailedMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				
					manageError(conversationId);
				}
			} else {
				System.out.println(MobilityUtils.CLIENT_ALL_DEPENDENCIES_AVAILABLE);
				
				boolean infoFileIsAvailable = (new File(this.nam.getMigrationStore() + conversationItem.getItemId() + MobilityUtils.INFO_FILE_EXTENSION)).exists();
				if (infoFileIsAvailable) {
					System.out.println(MobilityUtils.INFO_FILE_AVAILABLE);
				
					File itemFile = MobilityUtils.getRequestedItem(itemId, p, this.nam.getMigrationStore());
					
					if (itemFile != null && itemFile.exists()) {
						
						SAXHandler handler = MobilityUtils.parseXMLFile(itemId, this.nam);
						
						String libVersion = handler.getLibraryInformation().getVersion();
						String mainClassName = handler.getLibraryInformation().getMainClass();
						
						// Compare the version of the library the peer wants to send
						// with the version of the available one. If the version
						// is >= than the required one, an
						// ItemIsAvailableMessage message is sent to ask for the
						// execution state, otherwise an
						// AllDependenciesAreAvailableMessage message is sent to
						// inform that all dependencies are available but not
						// the item.
						if (Float.compare(Float.parseFloat(libVersion), Float.parseFloat(requiredLibVersion)) < 0) {
							// Requesting the updated info file
							AllDependenciesAreAvailableMessage receivedAllDependencies = new AllDependenciesAreAvailableMessage(conversationId);
							sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedAllDependencies.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
							System.out.println(MobilityUtils.OLD_ITEM_AVAILABLE);
						} else {
							System.out.println(MobilityUtils.ITEM_AVAILABLE_WAITING_FOR_STATE);
							
							// Adding the item to the class path before receiving the state
							if (p == Platform.DESKTOP) {
								System.out.println(MobilityUtils.ADDING_ITEM_TO_CP_BEFORE_RECEIVING_STATE);
								MobilityUtils.addToClassPath(this.nam, itemFile.getAbsolutePath(), null, null);
							}
							
							notifyObservers(itemFile.getAbsolutePath(), mainClassName, r, a, null);
							
							ItemIsAvailableMessage itemIsAvailable = new ItemIsAvailableMessage(conversationId);
							sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), itemIsAvailable.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
						}
					} else {
						System.out.println(MobilityUtils.ITEM_NOT_AVAILABLE);
						InfoFileIsAvailableMessage infoFileIsAvailableMessage = new InfoFileIsAvailableMessage(conversationId);
						sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), infoFileIsAvailableMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
					}
				} else {
					// Requesting info file
					System.out.println(MobilityUtils.INFO_FILE_NOT_AVAILABLE);
					AllDependenciesAreAvailableMessage receivedAllDependencies = new AllDependenciesAreAvailableMessage(conversationId);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedAllDependencies.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				}
			}
		} else if (messageType.equals(RequestDependenciesMessage.MSG_KEY)) {
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationId);
			
			Platform platform = conversationItem.getPlatform();
			// Action action = conversationItem.getAction();
			String itemId = conversationItem.getItemId();
			String senderContactAddress = conversationItem.getPartnerContactAddress();
			
			// Get the list of dependencies from the message
			Type type = new TypeToken<HashMap<String, String>>(){}.getType();
			HashMap<String, String> dependencies = gson.fromJson(peerMsg.get("dependencies").toString(), type);
			
			// The item get copied in spite of the mobility action, so a CopyActionImplementation method is used
			if (copyActionImplementation == null) {
				copyActionImplementation = new CopyActionImplementation(this.nam.getMigrationStore(), this);
			}
			copyActionImplementation.copyDependencyItems(conversationId, itemId, platform, dependencies, this.getPeerDescriptor(), senderContactAddress);

		} else if (messageType.equals(DependencyChunkTransferMessage.MSG_KEY)) {
			
			JsonObject params = peerMsg.getAsJsonObject("payload").getAsJsonObject("params");
			DependencyChunk chunk = (DependencyChunk) gson.fromJson(params.get("chunk").toString(), DependencyChunk.class);

			ConversationItem conversationItem = this.conversations.getConversationItem(chunk.getConversationId());
			
			System.out.println(MobilityUtils.RECEIVED_DEPENDENCY_CHUNK + (chunk.getChunkId() + 1) + MobilityUtils.PATH_SEPARATOR + chunk.getChunkNumber() + " for file: " + chunk.getFileName() + " (ID: " + chunk.getDependencyId() + ")");
			
			if (!this.receivedDependencyChunkList.containsKey(chunk.getDependencyId()))
				this.receivedDependencyChunkList.put(chunk.getDependencyId(), new ArrayList<DependencyChunk>());

			if (!this.receivedDependencyChunkList.get(chunk.getDependencyId()).contains(chunk))
				this.receivedDependencyChunkList.get(chunk.getDependencyId()).add(chunk);

			if (this.receivedDependencyChunkList.get(chunk.getDependencyId()).size() == chunk.getChunkNumber()) {
				System.out.println("Received all chunks for dependency file " + chunk.getFileName() + " (ID: " + chunk.getDependencyId() + ")");
				System.out.println(MobilityUtils.CREATING_FILE);

				ArrayList<DependencyChunk> fileChunks = this.receivedDependencyChunkList.get(chunk.getDependencyId());

				// Ordering chunks
				Collections.sort(fileChunks, new Comparator<DependencyChunk>() {

					@Override
					public int compare(DependencyChunk chunk1, DependencyChunk chunk2) {

						if (chunk1.getChunkId() < chunk2.getChunkId())
							return -1;

						if (chunk1.getChunkId() > chunk2.getChunkId())
							return 1;

						return 0;
					}
				});

				String dirPath = this.nam.getMigrationStore();

				File userDirectory = new File(dirPath);

				if (!userDirectory.exists())
					userDirectory.mkdirs();

				// Creating the dependency file
				File file = new File(dirPath + File.separatorChar + chunk.getFileName());

				try {
					FileOutputStream out = new FileOutputStream(file);

					for (DependencyChunk fileChunk : fileChunks)
						out.write(fileChunk.getBuffer());

					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				this.receivedDependencyChunkList.remove(chunk.getFileName());
				
				// Add dependency to class path only if it is not an xml info file
				if (chunk.getFileName().indexOf(MobilityUtils.INFO_FILE_EXTENSION) == -1) {
					if (nam.getClientPlatform(0) == Platform.DESKTOP) {
						MobilityUtils.addToClassPath(this.nam, dirPath + chunk.getFileName(), null, null);
					} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
						
						// Use observer pattern to add dependency to class path for Android
						notifyObservers(dirPath + chunk.getFileName(), null, null, conversationItem.getAction(), null);
					}
				} else {
					System.out.println(MobilityUtils.RECEIVED_INFO_FILE);
				}
				
				// Removing the missing dependency from the conversation
				conversationItem.removeMissingDependency(chunk.getDependencyId());
				
				// If no more dependencies are missing, ask for the item
				if (conversationItem.getMissingDependenciesSize() == 0) {
					System.out.println(MobilityUtils.CLIENT_ALL_DEPENDENCIES_AVAILABLE);
					
					String senderContactAddress = conversationItem.getPartnerContactAddress();
					
					File infoFile = new File(this.nam.getMigrationStore() + conversationItem.getItemId() + MobilityUtils.INFO_FILE_EXTENSION);
					
					boolean infoFileIsAvailable = infoFile.exists();
					if (infoFileIsAvailable) {
						System.out.println("The information file is available");
					
						File itemFile = MobilityUtils.getRequestedItem(conversationItem.getItemId(), conversationItem.getPlatform(), this.nam.getMigrationStore());
						
						if (itemFile != null && itemFile.exists()) {
							ItemIsAvailableMessage receivedItem = new ItemIsAvailableMessage(chunk.getConversationId());
							sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedItem.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
						
							if (conversationItem.getAction().equals(Action.COPY)) {
								SAXHandler handler = null;
								try {
									FileInputStream infoFis = new FileInputStream(infoFile);
									SAXParserFactory parserFactor = SAXParserFactory.newInstance();
									SAXParser parser = parserFactor.newSAXParser();
									handler = new SAXHandler();
									parser.parse(infoFis, handler);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (ParserConfigurationException e) {
									e.printStackTrace();
								} catch (SAXException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								String mainClassName = handler.getLibraryInformation().getMainClass();
								MigrationSubject role = conversationItem.getRole();
								
								if (nam.getClientPlatform(0) == Platform.DESKTOP) {
									System.out.println("The item is available; starting execution...");
									
									Object obj = MobilityUtils.addToClassPath(this.nam, this.nam.getMigrationStore() + itemFile.getName(), mainClassName, role);
									
									if (role.equals(MigrationSubject.FM)) {
										FunctionalModule fm = (FunctionalModule) obj;
										fm.getFunctionalModuleRunnable().start();
									}
									else if (role.equals(MigrationSubject.SERVICE)) {
										
										// String functionalModuleId = handler.getLibraryInformation().getFunctionalModule();
										String functionalModuleId = handler.getFunctionalModuleForService().getId();
										
										System.out.println("------ The id of the FM associated to the received Service is " + functionalModuleId);
										
										// Adding the service to the associated functional module
										Service s = (Service) obj;
										
										boolean fmInfoFileIsAvailable = (new File(nam.getMigrationStore() + functionalModuleId + MobilityUtils.INFO_FILE_EXTENSION)).exists();
										if (fmInfoFileIsAvailable) {
											SAXHandler infoFmHandler = MobilityUtils.parseXMLFile(functionalModuleId, this.nam);
											String fmCompleteMainClassName = infoFmHandler.getLibraryInformation().getMainClass();
											
											FunctionalModule fm = MobilityUtils.addServiceToFm(s, functionalModuleId, this.nam, fmCompleteMainClassName);
											s.setFunctionalModule(fm);

											// Starting the service
											s.getServiceRunnable().start();
											
										} else {
											System.err.println("The info file for the FM to which the Service is associated is not available");
										}
									}
									
									System.out.println("COPY " + MobilityUtils.ACTION_SUCCESSFUL);
									
								} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
									
									// Use observer pattern for notifications
									notifyObservers(this.nam.getMigrationStore() + itemFile.getName(), mainClassName, role, conversationItem.getAction(), null);
								}
								
							} else if (conversationItem.getAction().equals(Action.MIGRATE)) {
								System.out.println(MobilityUtils.ITEM_AVAILABLE_WAITING_FOR_STATE);
								
								// The class is added to the class path, but not instantiated (the second argument is null)
								if (nam.getClientPlatform(0) == Platform.DESKTOP) {
									MobilityUtils.addToClassPath(this.nam, itemFile.getAbsolutePath(), null, conversationItem.getRole());
								} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
									
									// Use observer pattern for notifications
									notifyObservers(itemFile.getAbsolutePath(), null, conversationItem.getRole(), conversationItem.getAction(), null);
								}
							}
						
						} else {
							System.out.println(MobilityUtils.ITEM_NOT_AVAILABLE);
							InfoFileIsAvailableMessage infoFileIsAvailableMessage = new InfoFileIsAvailableMessage(chunk.getConversationId());
							sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), infoFileIsAvailableMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
						}
					} else {
						System.out.println("The information file is not available; requesting it...");
						AllDependenciesAreAvailableMessage receivedAllDependencies = new AllDependenciesAreAvailableMessage(chunk.getConversationId());
						sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedAllDependencies.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
					}
				}
			}
			
		} else if (messageType.equals(InfoFileChunkTransferMessage.MSG_KEY)) {
			
			JsonObject params = peerMsg.getAsJsonObject("payload").getAsJsonObject("params");
			InfoFileChunk chunk = (InfoFileChunk) gson.fromJson(params.get("chunk").toString(), InfoFileChunk.class);

			ConversationItem conversationItem = this.conversations.getConversationItem(chunk.getConversationId());
			
			System.out.println(MobilityUtils.RECEIVED_ITEM_CHUNK + (chunk.getChunkId() + 1) + MobilityUtils.PATH_SEPARATOR + chunk.getChunkNumber() + " for file: " + chunk.getFileName());
			
			if (!this.receivedInfoFileChunkList.containsKey(chunk.getInfoFileId()))
				this.receivedInfoFileChunkList.put(chunk.getInfoFileId(), new ArrayList<InfoFileChunk>());

			if (!this.receivedInfoFileChunkList.get(chunk.getInfoFileId()).contains(chunk))
				this.receivedInfoFileChunkList.get(chunk.getInfoFileId()).add(chunk);

			if (this.receivedInfoFileChunkList.get(chunk.getInfoFileId()).size() == chunk.getChunkNumber()) {
				System.out.println("Received all chunks for item " + chunk.getFileName());
				System.out.println(MobilityUtils.CREATING_FILE);

				ArrayList<InfoFileChunk> fileChunks = this.receivedInfoFileChunkList.get(chunk.getInfoFileId());

				// Ordering chunks
				Collections.sort(fileChunks, new Comparator<InfoFileChunk>() {

					@Override
					public int compare(InfoFileChunk chunk1, InfoFileChunk chunk2) {

						if (chunk1.getChunkId() < chunk2.getChunkId())
							return -1;

						if (chunk1.getChunkId() > chunk2.getChunkId())
							return 1;

						return 0;
					}
				});

				String dirPath = this.nam.getMigrationStore();

				File userDirectory = new File(dirPath);

				if (!userDirectory.exists())
					userDirectory.mkdirs();

				// Creating the info file
				File file = new File(dirPath + File.separatorChar + chunk.getFileName());

				try {
					FileOutputStream out = new FileOutputStream(file);

					for (InfoFileChunk fileChunk : fileChunks)
						out.write(fileChunk.getBuffer());

					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String senderContactAddress = conversationItem.getPartnerContactAddress();
				
				// Requesting the item even if it is already available since the info file was for an old version and it has already been updated
				InfoFileIsAvailableMessage infoFileIsAvailableMessage = new InfoFileIsAvailableMessage(chunk.getConversationId());
				sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), infoFileIsAvailableMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
			}
			
		} else if (messageType.equals(AllDependenciesAreAvailableMessage.MSG_KEY)) {
			
			System.out.println(MobilityUtils.SENDING_INFO_FILE);
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationId);
			
			String itemId = conversationItem.getItemId();
			String senderContactAddress = conversationItem.getPartnerContactAddress();
			
			// The item get copied in spite of the mobility action, so a CopyActionImplementation method is used
			if (copyActionImplementation == null) {
				copyActionImplementation = new CopyActionImplementation(this.nam.getMigrationStore(), this);
			}
			int result = copyActionImplementation.copyInfoFile(conversationId, itemId, this.getPeerDescriptor(), senderContactAddress);
			
			if (result == -1) {
				System.err.println("An error occurred during a " + conversationItem.getAction().toString() + " mobility action with peer " + conversationItem.getPartnerContactAddress());
			}
			
		} else if (messageType.equals(InfoFileIsAvailableMessage.MSG_KEY)) {
			
			System.out.println(MobilityUtils.INFO_FILE_AVAILABLE_ON_CLIENT);
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationId);
			
			Platform p = conversationItem.getPlatform();
			String itemId = conversationItem.getItemId();
			String senderContactAddress = conversationItem.getPartnerContactAddress();
			
			// The item get copied in spite of the mobility action, so a CopyActionImplementation method is used
			if (copyActionImplementation == null) {
				copyActionImplementation = new CopyActionImplementation(this.nam.getMigrationStore(), this);
			}
			int result = copyActionImplementation.copyItem(conversationId, itemId, p, this.getPeerDescriptor(), senderContactAddress);
			
			if (result == -1) {
				System.err.println("An error occurred during a " + conversationItem.getAction().toString() + " mobility action with peer " + conversationItem.getPartnerContactAddress());
			}

		} else if (messageType.equals(ItemChunkTransferMessage.MSG_KEY)) {
			
			JsonObject params = peerMsg.getAsJsonObject("payload").getAsJsonObject("params");
			ItemChunk chunk = (ItemChunk) gson.fromJson(params.get("chunk").toString(), ItemChunk.class);

			ConversationItem conversationItem = this.conversations.getConversationItem(chunk.getConversationId());
			
			System.out.println(MobilityUtils.RECEIVED_ITEM_CHUNK + (chunk.getChunkId() + 1) + MobilityUtils.PATH_SEPARATOR + chunk.getChunkNumber() + " for file: " + chunk.getFileName() + " (ID: " + chunk.getMainClassName() + ")");
			
			if (!this.receivedItemChunkList.containsKey(chunk.getMainClassName()))
				this.receivedItemChunkList.put(chunk.getMainClassName(), new ArrayList<ItemChunk>());

			if (!this.receivedItemChunkList.get(chunk.getMainClassName()).contains(chunk))
				this.receivedItemChunkList.get(chunk.getMainClassName()).add(chunk);

			if (this.receivedItemChunkList.get(chunk.getMainClassName()).size() == chunk.getChunkNumber()) {
				System.out.println("Received all chunks for item " + chunk.getFileName() + " (ID: " + chunk.getMainClassName() + ")");
				System.out.println(MobilityUtils.CREATING_FILE);

				ArrayList<ItemChunk> fileChunks = this.receivedItemChunkList.get(chunk.getMainClassName());

				// Ordering chunks
				Collections.sort(fileChunks, new Comparator<ItemChunk>() {

					@Override
					public int compare(ItemChunk chunk1, ItemChunk chunk2) {

						if (chunk1.getChunkId() < chunk2.getChunkId())
							return -1;

						if (chunk1.getChunkId() > chunk2.getChunkId())
							return 1;

						return 0;
					}
				});

				String dirPath = this.nam.getMigrationStore();

				File userDirectory = new File(dirPath);

				if (!userDirectory.exists())
					userDirectory.mkdirs();

				// Creating the item file
				File file = new File(dirPath + File.separatorChar + chunk.getFileName());

				try {
					FileOutputStream out = new FileOutputStream(file);

					for (ItemChunk fileChunk : fileChunks)
						out.write(fileChunk.getBuffer());

					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				MigrationSubject role = conversationItem.getRole();
				Action action = conversationItem.getAction();
				String senderContactAddress = conversationItem.getPartnerContactAddress();
				
				// Requested item's file name
				String fileName = chunk.getFileName();
				String mainClassName = chunk.getMainClassName();
				
				if (action.equals(Action.COPY)) {
				
					System.out.println("Received " + file.getName() + " (size is " + file.length() + " bytes) " + file.getAbsolutePath());
			
					System.out.println("The file is a " + role + " and its main class is " + mainClassName);
					
					if (nam.getClientPlatform(0) == Platform.DESKTOP) {
						Object obj = MobilityUtils.addToClassPath(this.nam, this.nam.getMigrationStore() + fileName, mainClassName, role);
						
						if (role.equals(MigrationSubject.FM)) {
							FunctionalModule fm = (FunctionalModule) obj;
							fm.getFunctionalModuleRunnable().start();
							
							// Store the address of the peer that sent the FM
							this.nam.addFmSender(conversationItem.getPartnerContactAddress(), conversationItem.getItemId());
						}
						else if (role.equals(MigrationSubject.SERVICE)) {
							String functionalModuleId = chunk.getFunctionalModuleIdForService();
							
							System.out.println("------ Functional module id (retrieved from the last chunk) = " + functionalModuleId);
							
							// Adding the service to the associated functional module
							Service s = (Service) obj;
							
							boolean fmInfoFileIsAvailable = (new File(nam.getMigrationStore() + functionalModuleId + MobilityUtils.INFO_FILE_EXTENSION)).exists();
							if (fmInfoFileIsAvailable) {
								SAXHandler infoFmHandler = MobilityUtils.parseXMLFile(functionalModuleId, this.nam);
								String fmCompleteMainClassName = infoFmHandler.getLibraryInformation().getMainClass();
								
								FunctionalModule fm = MobilityUtils.addServiceToFm(s, functionalModuleId, this.nam, fmCompleteMainClassName);
								s.setFunctionalModule(fm);

								// Starting the service
								s.getServiceRunnable().start();
								
							} else {
								System.err.println("The info file for the FM to which the Service is associated is not available");
							}
							
							// Store the address of the peer that sent the Service
							this.nam.addServiceSender(conversationItem.getPartnerContactAddress(), conversationItem.getItemId());
						}
					} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
						
						// Use observer pattern for notifications
						notifyObservers(this.nam.getMigrationStore() + fileName, mainClassName, role, action, null);
					}
					
					System.out.println("COPY " + MobilityUtils.ACTION_SUCCESSFUL);
					
					ItemIsAvailableMessage receivedItem = new ItemIsAvailableMessage(chunk.getConversationId());
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedItem.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
					
					// Remove conversation item from conversations list
					this.conversations.remove(chunk.getConversationId());
				
				} else if (action.equals(Action.MIGRATE)) {
					System.out.println(MobilityUtils.ITEM_RECEIVED_WAITING_FOR_STATE);
					System.out.println("--- Received " + file.getName() + " (size is " + file.length() + " bytes) " + file.getAbsolutePath());
					System.out.println("--- The file is a " + role + " and its main class is " + mainClassName);
					
					// The class is added to the class path, but not instantiated (the second argument is null)
					if (nam.getClientPlatform(0) == Platform.DESKTOP) {
						MobilityUtils.addToClassPath(this.nam, this.nam.getMigrationStore() + fileName, null, role);
					} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
						
						// Use observer pattern for notifications
						notifyObservers(this.nam.getMigrationStore() + fileName, null, role, action, null);
					}

					ItemIsAvailableMessage receivedItem = new ItemIsAvailableMessage(chunk.getConversationId());
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedItem.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				}
				
				this.receivedItemChunkList.remove(chunk.getMainClassName());
			}
			
		} else if (messageType.equals(StateChunkTransferMessage.MSG_KEY)) {

			JsonObject params = peerMsg.getAsJsonObject("payload").getAsJsonObject("params");
			StateChunk chunk = (StateChunk) gson.fromJson(params.get("chunk").toString(), StateChunk.class);

			ConversationItem conversationItem = this.conversations.getConversationItem(chunk.getConversationId());
			
			String senderContactAddress = conversationItem.getPartnerContactAddress();
			
			System.out.println(MobilityUtils.RECEIVED_STATE_CHUNK + (chunk.getChunkId() + 1) + MobilityUtils.PATH_SEPARATOR + chunk.getChunkNumber() + " for conversation: " + chunk.getConversationId());
			
			if (!this.receivedStateChunkList.containsKey(chunk.getConversationId()))
				this.receivedStateChunkList.put(chunk.getConversationId(), new ArrayList<StateChunk>());

			if (!this.receivedStateChunkList.get(chunk.getConversationId()).contains(chunk))
				this.receivedStateChunkList.get(chunk.getConversationId()).add(chunk);

			if (this.receivedStateChunkList.get(chunk.getConversationId()).size() == chunk.getChunkNumber()) {
				System.out.println("Received all state chunks for conversation " + chunk.getConversationId());
				
				System.out.println("Creating object...");

				ArrayList<StateChunk> fileChunks = this.receivedStateChunkList.get(chunk.getConversationId());

				// Ordering chunks
				Collections.sort(fileChunks, new Comparator<StateChunk>() {

					@Override
					public int compare(StateChunk chunk1, StateChunk chunk2) {

						if (chunk1.getChunkId() < chunk2.getChunkId())
							return -1;

						if (chunk1.getChunkId() > chunk2.getChunkId())
							return 1;

						return 0;
					}
				});

				byte fullArray[] = null;
				Object state = null;
				
				try {
					// Concatenate all byte arrays
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					for(StateChunk fileChunk : fileChunks) {
						outputStream.write(fileChunk.getBuffer());
					}
					fullArray = outputStream.toByteArray();
					
					// Convert byte array to Object
					ByteArrayInputStream in = new ByteArrayInputStream(fullArray);
				    ObjectInputStream is = new ObjectInputStream(in);
				    
					state = is.readObject();
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				this.receivedStateChunkList.remove(chunk.getConversationId());
				
				if (state == null) {
					System.err.println(MobilityUtils.ERROR_NULL_STATE);
					
					// Notify sender that the mobility action failed
					MigrationFailedMessage migrationFailedMessage = new MigrationFailedMessage(chunk.getConversationId(), MobilityUtils.ERROR_NULL_STATE);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), migrationFailedMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				
					manageError(chunk.getConversationId());
				}
				else {
					System.out.println("MIGRATE " + MobilityUtils.ACTION_SUCCESSFUL);
					
					conversationItem.setObject(state);
				
					if (nam.getClientPlatform(0) == Platform.DESKTOP) {
						
						if (conversationItem.getRole().equals(MigrationSubject.FM)) {
							FunctionalModule tfm = (FunctionalModule) state;
							tfm.getFunctionalModuleRunnable().restoreState();
							tfm.getFunctionalModuleRunnable().resume();
							
							// Store the address of the peer that sent the FM
							this.nam.addFmSender(conversationItem.getPartnerContactAddress(), conversationItem.getItemId());
						}
						else if (conversationItem.getRole().equals(MigrationSubject.SERVICE)) {
							SAXHandler handler = MobilityUtils.parseXMLFile(conversationItem.getItemId(), this.nam);
							String functionalModuleId = handler.getFunctionalModuleForService().getId();
							
							System.out.println("------ The id of the FM associated to the received Service is " + functionalModuleId);
							
							// Adding the service to the associated functional module
							Service s = (Service) state;
							
							boolean fmInfoFileIsAvailable = (new File(nam.getMigrationStore() + functionalModuleId + MobilityUtils.INFO_FILE_EXTENSION)).exists();
							if (fmInfoFileIsAvailable) {
								SAXHandler infoFmHandler = MobilityUtils.parseXMLFile(functionalModuleId, this.nam);
								String fmCompleteMainClassName = infoFmHandler.getLibraryInformation().getMainClass();
								
								FunctionalModule fm = MobilityUtils.addServiceToFm(s, functionalModuleId, this.nam, fmCompleteMainClassName);
								s.setFunctionalModule(fm);
	
								// Resuming the service execution
								s.getServiceRunnable().restoreState();
								s.getServiceRunnable().resume();
								
							} else {
								System.err.println("The info file for the FM to which the Service is associated is not available");
							}
							
							// Store the address of the peer that sent the Service
							this.nam.addServiceSender(conversationItem.getPartnerContactAddress(), conversationItem.getItemId());
						}
						
						// Remove conversation item from conversations list
						this.conversations.remove(chunk.getConversationId());
					
					} else if (nam.getClientPlatform(0) == Platform.ANDROID) {
						
						// Use observer pattern for notifications - notify that the state has been received
						SAXHandler handler = MobilityUtils.parseXMLFile(conversationItem.getItemId(), this.nam);
						String mainClassName = handler.getLibraryInformation().getMainClass();
						notifyObservers(this.nam.getMigrationStore() + conversationItem.getItemId() + MobilityUtils.ANDROID_FILE_EXTENSION, mainClassName, conversationItem.getRole(), conversationItem.getAction(), state);
						
						// Remove conversation item from conversations list
						this.conversations.remove(chunk.getConversationId());
					}

					ReceivedStateMessage receivedStateMessage = new ReceivedStateMessage(chunk.getConversationId());
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), receivedStateMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				}
			}
			
		} else if (messageType.equals(ReceivedStateMessage.MSG_KEY)) {
			System.out.println("MIGRATE " + MobilityUtils.ACTION_SUCCESSFUL);
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			
			// Remove conversation item from conversations list
			this.conversations.remove(conversationId);
			
		} else if (messageType.equals(ItemIsAvailableMessage.MSG_KEY)) {
			
			System.out.println(MobilityUtils.ITEM_RECEIVED);
			
			String conversationId = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationId);
			
			Action action = conversationItem.getAction();
			
			if (action.equals(Action.COPY)) {
				System.out.println("COPY " + MobilityUtils.ACTION_SUCCESSFUL);
				
				// Remove conversation item from conversations list
				this.conversations.remove(conversationId);
				
			} else if (action.equals(Action.MIGRATE)) {
				
				System.out.println(MobilityUtils.SENDING_STATE);
				
				String senderContactAddress = conversationItem.getPartnerContactAddress();
				Object object = conversationItem.getObject();
				
				if (migrateActionImplementation == null) {
					migrateActionImplementation = new MigrateActionImplementation(this.nam.getMigrationStore(), this);
				}
				ArrayList<StateChunk> chunkList = migrateActionImplementation.generateStateChunks(conversationId, object);
				
				if (chunkList == null) {
					System.err.println(MobilityUtils.ERROR_GENERATING_STATE_CHUNKS);
					
					// Notify sender that the mobility action failed
					MigrationFailedMessage migrationFailedMessage = new MigrationFailedMessage(conversationId, MobilityUtils.ERROR_GENERATING_STATE_CHUNKS);
					sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), migrationFailedMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				
					manageError(conversationId);
					
				} else {
					// Send state chunks
					for(StateChunk chunk : chunkList) {
						System.out.println(MobilityUtils.SENDING_STATE_CHUNK + (chunk.getChunkId() + 1) + MobilityUtils.PATH_SEPARATOR + chunk.getChunkNumber());
					
						StateChunkTransferMessage stateChunkTransferMessage = new StateChunkTransferMessage(this.getPeerDescriptor(), chunk);
						sendMessage(new Address(senderContactAddress), new Address(senderContactAddress), this.getAddress(), stateChunkTransferMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
					}
				}
			}
			
		} else if (messageType.equals(MigrationFailedMessage.MSG_KEY)) {
			
			String errorDescription = peerMsg.get("errorDescription").getAsString();
			String conversationKey = peerMsg.get("conversationKey").getAsString();
			ConversationItem conversationItem = this.conversations.getConversationItem(conversationKey);
			String partner = conversationItem.getPartnerContactAddress();
			MigrationSubject role = conversationItem.getRole();
			Action action = conversationItem.getAction();
			String itemId = conversationItem.getItemId();
			
			System.err.println("An error occurred during a " + action + " mobility action");
			System.err.println("--- Client contact address: " + partner);
			System.err.println("--- Item id: " + itemId + " (" + role + ")");
			System.err.println("--- Error message: " + errorDescription);
		}
		
	}
	
	/**
	 * Method invoked when a JSON message could not get delivered to the
	 * addressee.
	 * 
	 * @param peerMsg
	 *            The message that could not get delivered
	 * 
	 * @param receiver
	 *            The {@link Address} of the addressee peer
	 * 
	 * @param contentType
	 *            The format of the message (i.e. "application/json")
	 */
	protected void onDeliveryMsgFailure(String sentMessage, Address receiver, String contentType) {
		System.out.println("Could not deliver message to " + receiver);
		
		for(PeerDescriptor pd : this.getPeerList()) {
			if (pd.getContactAddress().equalsIgnoreCase(receiver.getURL())) {
				
				System.out.println("Removing the peer from the peer list.");
				
				this.getPeerList().remove(pd);
				this.getPeerList().printPeerList();
				
				break;
			}
		}
	}

	/**
	 * Method invoked when a JSON message got successfully delivered to the
	 * addressee.
	 * 
	 * @param peerMsg
	 *            The message that got delivered
	 * 
	 * @param receiver
	 *            The {@link Address} of the addressee peer
	 * 
	 * @param contentType
	 *            The format of the message (i.e. "application/json")
	 */
	protected void onDeliveryMsgSuccess(String sentMessage, Address receiver, String contentType) {}
	
}
