package it.unipr.ce.dsg.nam4j.impl.mobility;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.messages.DependencyChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.InfoFileChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.ItemChunkTransferMessage;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.DependencyChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.InfoFileChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.ItemChunk;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.SAXHandler;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * <p>
 * This class implements the COPY mobility action.
 * </p>
 * 
 * <p>
 *  Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 *  Permission is granted to copy, distribute and/or modify this document
 *  under the terms of the GNU Free Documentation License, Version 1.3
 *  or any later version published by the Free Software Foundation;
 *  with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
 *  A copy of the license is included in the section entitled "GNU
 *  Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class CopyActionImplementation extends CopyActionHandler {
	
	/** The path where files are stored */
	private String migrationStore;
	
	private MccNamPeer peer;
	
	public CopyActionImplementation(String migrationStore, MccNamPeer peer) {
		setMigrationStore(migrationStore);
		setPeer(peer);
	}
	
	/**
	 * Method to get the path where files are stored.
	 * 
	 * @return the path where files are stored
	 */
	public String getMigrationStore() {
		return migrationStore;
	}

	/**
	 * Method to get the path where files are stored.
	 * 
	 * @param migrationStore
	 *            the path where files are stored
	 */
	public void setMigrationStore(String migrationStore) {
		this.migrationStore = migrationStore;
	}
	
	public MccNamPeer getPeer() {
		return peer;
	}

	public void setPeer(MccNamPeer peer) {
		this.peer = peer;
	}

	/**
	 * Method to send the list of dependencies for a given item.
	 * 
	 * @param conversationKey
	 *            The id of the conversation
	 * 
	 * @param itemId
	 *            The id of the item to which the dependencies relate
	 * 
	 * @param p
	 *            The {@link NetworkedAutonomicMachine#Platform} of the
	 *            requesting peer
	 * 
	 * @param dependencies
	 *            The list of requested dependencies
	 * 
	 * @param peerDescriptor
	 *            The {@link PeerDescriptor} of current peer used to send the
	 *            chunks of the dependencies
	 * 
	 * @param senderContactAddress
	 *            The contact address of the requesting peer
	 */
	public void copyDependencyItems(String conversationKey, String itemId, Platform p, HashMap<String, String> dependencies, PeerDescriptor peerDescriptor, String senderContactAddress) {
		
		System.out.println(MobilityUtils.SENDING_DEPENDENCIES + itemId);
		
		Iterator<Entry<String, String>> dependenciesIt = dependencies.entrySet().iterator();
		while(dependenciesIt.hasNext()) {
			Entry<String, String> pairs = (Entry<String, String>) dependenciesIt.next();
			
			File dependency = null;
			
			if (!(pairs.getValue().equals(MobilityUtils.INFO_FILE_ID) || pairs.getValue().equals(MobilityUtils.RESOURCE_FILE_ID))) {
				dependency = MobilityUtils.getRequestedItem(pairs.getKey(), p, getMigrationStore());
			} else {
				// The file is a xml info file for a dependency or a resource file
				File f = new File(this.getMigrationStore() + pairs.getKey());
				if (f.exists()) {
					dependency = f;
				} else {
					dependency = null;
				}
			}
			
			if (dependency != null) {
				FileInputStream dependencyFileInputStream = null;
				byte[] bDependencyFile = new byte[(int) dependency.length()];

				try {
					// Convert file into an array of bytes
					dependencyFileInputStream = new FileInputStream(dependency);
					dependencyFileInputStream.read(bDependencyFile);
					dependencyFileInputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				ArrayList<DependencyChunk> chunkList = MobilityUtils.generateDependencyChunksFromByteArray(conversationKey, bDependencyFile, pairs.getKey(), dependency.getName());
				
				for(DependencyChunk chunk : chunkList) {
					System.out.println(MobilityUtils.SENDING_DEPENDENCY_CHUNK + (chunk.getChunkId() + 1) + "/" + chunk.getChunkNumber() + " for file " + dependency.getName() + " (" + pairs.getKey() + ")");
				
					DependencyChunkTransferMessage dependencyChunkTransferMessage = new DependencyChunkTransferMessage(peerDescriptor, chunk);
					this.getPeer().sendMessageToPeer(new Address(senderContactAddress), dependencyChunkTransferMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				}
			}
		}
	}
	
	/**
	 * Method to send an item to a requesting peer.
	 * 
	 * @param conversationKey
	 *            The id of the conversation
	 * 
	 * @param itemId
	 *            The id of the item to be sent
	 * 
	 * @param p
	 *            The {@link NetworkedAutonomicMachine#Platform} of the
	 *            requesting peer
	 * 
	 * @param peerDescriptor
	 *            The {@link PeerDescriptor} of current peer used to send the
	 *            chunks of the dependencies
	 * 
	 * @param senderContactAddress
	 *            The contact address of the requesting peer
	 * 
	 * @return the JSON representation of the message containing the item to be
	 *         sent
	 */
	public int copyItem(String conversationKey, String itemId, Platform p, PeerDescriptor peerDescriptor, String senderContactAddress) {
		
		System.out.println(MobilityUtils.SENDING_ITEM + itemId);
		
		File file = null;
		
		// Check if the requested item is available
		file = MobilityUtils.getRequestedItem(itemId, p, getMigrationStore());
		
		if (file != null) {
			
			String mainClass = "";
			
			// Only for services - the id of the functional module to which the service is bound
			String functionalModuleIdForService = null;
			
			try {
				File infoFile = new File(this.getMigrationStore() + itemId + MobilityUtils.INFO_FILE_EXTENSION);
				if (infoFile.exists()) {
					FileInputStream infoFis = new FileInputStream(infoFile);
					
					SAXParserFactory parserFactor = SAXParserFactory.newInstance();
					SAXParser parser = parserFactor.newSAXParser();
					SAXHandler handler = new SAXHandler();
					parser.parse(infoFis, handler);
				
					if (handler.getLibraryInformation() != null) {
						mainClass = handler.getLibraryInformation().getMainClass();
					
						// Object FunctionalModuleIdForService only exists if the XML
						// info file describes a Service and its associated FM (i.e.,
						// includes <functional_module> element)
						if (handler.getFunctionalModuleForService() != null)
							functionalModuleIdForService = handler.getFunctionalModuleForService().getId();
						
						FileInputStream fileInputStream = null;
						byte[] bFile = new byte[(int) file.length()];
		
						try {
							// Convert file into an array of bytes
							fileInputStream = new FileInputStream(file);
							fileInputStream.read(bFile);
							fileInputStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						ArrayList<ItemChunk> chunkList = MobilityUtils.generateItemChunksFromByteArray(conversationKey, bFile, mainClass, functionalModuleIdForService, file.getName());
						
						for(ItemChunk chunk : chunkList) {
							System.out.println(MobilityUtils.SENDING_ITEM_CHUNK + (chunk.getChunkId() + 1) + "/" + chunk.getChunkNumber());
						
							ItemChunkTransferMessage itemChunkTransferMessage = new ItemChunkTransferMessage(peerDescriptor, chunk);
							this.getPeer().sendMessageToPeer(new Address(senderContactAddress), itemChunkTransferMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
						}
						
					} else System.err.println(MobilityUtils.INFO_FILE_DOES_NOT_INCLUDE_DESCRIPTION);
				} else System.err.println(MobilityUtils.INFO_FILE_DOES_NOT_EXIST);
				
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e) {
				System.err.println(MobilityUtils.MISSING_XML_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return 0;
			
		} else {
			System.err.println(MobilityUtils.ITEM_FILE_DOES_NOT_EXIST);
			return -1;
		}
	}
	
	/**
	 * Method to send the list of dependencies for a given item.
	 * 
	 * @param conversationKey
	 *            The id of the conversation
	 * 
	 * @param itemId
	 *            The id of the item to which the dependencies relate
	 * 
	 * @param p
	 *            The {@link NetworkedAutonomicMachine#Platform} of the
	 *            requesting peer
	 * 
	 * @param dependencies
	 *            The list of requested dependencies
	 * 
	 * @param peerDescriptor
	 *            The {@link PeerDescriptor} of current peer used to send the
	 *            chunks of the dependencies
	 * 
	 * @param senderContactAddress
	 *            The contact address of the requesting peer
	 */
	public int copyInfoFile(String conversationKey, String itemId, PeerDescriptor peerDescriptor, String senderContactAddress) {
		System.out.println("Sending info file for item " + itemId);
		
		try {
			File infoFile = new File(this.getMigrationStore() + itemId + MobilityUtils.INFO_FILE_EXTENSION);
				
			if (infoFile.exists()) {
				// Convert file into an array of bytes
				FileInputStream infoFileInputStream = new FileInputStream(infoFile);
				byte[] bInfoFile = new byte[(int) infoFile.length()];
				infoFileInputStream.read(bInfoFile);
				
				ArrayList<InfoFileChunk> chunkList = MobilityUtils.generateInfoFileChunksFromByteArray(conversationKey, bInfoFile, itemId, infoFile.getName());
				
				for(InfoFileChunk chunk : chunkList) {
					System.out.println(MobilityUtils.SENDING_INFO_FILE_CHUNK + (chunk.getChunkId() + 1) + "/" + chunk.getChunkNumber());
				
					InfoFileChunkTransferMessage infoFileChunkTransferMessage = new InfoFileChunkTransferMessage(peerDescriptor, chunk);
					this.getPeer().sendMessageToPeer(new Address(senderContactAddress), infoFileChunkTransferMessage.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
				}
				infoFileInputStream.close();
				
				return 0;
				
			} else {
				System.err.println(MobilityUtils.INFO_FILE_DOES_NOT_EXIST);
				return -1;
			} 
			
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
