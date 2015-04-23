package it.unipr.ce.dsg.nam4j.impl.messages;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents a message to inform that an error occurred during the
 * mobility operation.
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

public class MigrationFailedMessage {

	public static final String MSG_KEY = "MIGRATION_FAILED";
	private String type;
	private String conversationKey;
	private String errorDescription;
	
	public MigrationFailedMessage(String conversationKey, String errorDescription) {
		setType(MSG_KEY);
		setConversationKey(conversationKey);
		setErrorDescription(errorDescription);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConversationKey() {
		return conversationKey;
	}

	public void setConversationKey(String conversationKey) {
		this.conversationKey = conversationKey;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * Method to obtain a JSON representation of this {@link CopyItemMessage}.
	 * 
	 * @return a JSON representation of this {@link CopyItemMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
