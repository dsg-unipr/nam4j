package it.unipr.ce.dsg.nam4j.impl.messages;

import it.unipr.ce.dsg.nam4j.impl.mobility.utils.InfoFileChunk;
import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.message.Payload;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents a message to send a chunk of the file which describes
 * the item to be migrated.
 * </p>
 * 
 * <p>
 * Copyright (c) 2014, Distributed Systems Group, University of Parma, Italy.
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

public class InfoFileChunkTransferMessage extends BasicMessage {

	public static final String MSG_KEY = "INFO_FILE_CHUNK_MSG";

	public InfoFileChunkTransferMessage(PeerDescriptor peerDesc, InfoFileChunk chunk) {
		super();
		this.setType(MSG_KEY);
		Payload peerLoad = new Payload(peerDesc);
		peerLoad.getParams().put("peerDesc", peerDesc);
		peerLoad.getParams().put("chunk", chunk);
		this.setPayload(peerLoad);
	}
	
	/**
	 * Method to obtain a JSON representation of this {@link InfoFileChunkTransferMessage}.
	 * 
	 * @return a JSON representation of this {@link InfoFileChunkTransferMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
