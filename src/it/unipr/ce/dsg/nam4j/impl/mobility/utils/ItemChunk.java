package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

/**
 * <p>
 * This class represents a chunk of the item to be migrated.
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

public class ItemChunk {
	
	private String conversationId = null;
	private int chunkNumber = -1;
	private int chunkId = -1;
	private String mainClassName = null;
	private String functionalModuleIdForService = null;
	private String fileName = null;
	
	/** The encrypted chunk */
	private byte buffer[];
	
	/** The initialization vector used by some block cipher operation modes */
	private byte iv[];
	
	public ItemChunk(String conversationId, int chunkNumber,  int chunkId, byte[] buffer, byte[] iv, String mainClassName, String functionalModuleIdForService, String fileName) {
		super();
		setConversationId(conversationId);
		setChunkId(chunkId);
		setBuffer(buffer);
		setIv(iv);
		setChunkNumber(chunkNumber);
		setMainClassName(mainClassName);
		setFunctionalModuleIdForService(functionalModuleIdForService);
		setFileName(fileName);
	}
	
	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public int getChunkId() {
		return chunkId;
	}

	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	public int getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(int fileChunkNumber) {
		this.chunkNumber = fileChunkNumber;
	}

	public String getMainClassName() {
		return mainClassName;
	}

	public void setMainClassName(String mainClassName) {
		this.mainClassName = mainClassName;
	}

	public String getFunctionalModuleIdForService() {
		return functionalModuleIdForService;
	}

	public void setFunctionalModuleIdForService(String functionalModuleIdForService) {
		this.functionalModuleIdForService = functionalModuleIdForService;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean equals(Object arg0) {
		ItemChunk chunkObj = (ItemChunk) arg0;
		
		if (this.conversationId.equals(chunkObj.getConversationId()) && chunkObj.getChunkId() == this.chunkId && chunkObj.getChunkNumber() == this.chunkNumber)
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		String hashCode = this.conversationId + "#" + this.chunkNumber + "#" + this.chunkId;
		return hashCode.hashCode();
	}

}
