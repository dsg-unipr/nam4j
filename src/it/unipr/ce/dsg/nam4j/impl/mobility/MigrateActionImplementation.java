package it.unipr.ce.dsg.nam4j.impl.mobility;

import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.StateChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * <p>
 * This class implements the MIGRATE mobility action.
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

public class MigrateActionImplementation extends MigrateActionHandler {
	
	/** The path where files are stored */
	private String migrationStore;
	
	private MccNamPeer peer;
	
	public MigrateActionImplementation(String migrationStore, MccNamPeer peer) {
		setMigrationStore(migrationStore);
		setPeer(peer);
	}

	public String getMigrationStore() {
		return migrationStore;
	}
	
	public MccNamPeer getPeer() {
		return peer;
	}

	public void setPeer(MccNamPeer peer) {
		this.peer = peer;
	}

	public void setMigrationStore(String migrationStore) {
		this.migrationStore = migrationStore;
	}

	/**
	 * Method to generate chunks representing the execution state.
	 * 
	 * @param conversationKey
	 *            The id of the conversation to which the state relates
	 * 
	 * @param object
	 *            The serialized object representing the execution state
	 * 
	 * @return the list of state chunks
	 */
	public ArrayList<StateChunk> generateStateChunks(String conversationKey, Object object) {
		byte[] bObject = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			bObject = bos.toByteArray();
		} catch(NotSerializableException e) {
			System.err.println("The Runnable object representing the state cannot be serialized (" + e.getMessage() + " is not serializable - define it as transient and edit storeState() and retrieveState() methods to manage it)");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// The byte array representing the object is divided in chunks
		ArrayList<StateChunk> chunkList = MobilityUtils.generateStateChunksFromByteArray(conversationKey, bObject);
		
		return chunkList;
	}
	
}
