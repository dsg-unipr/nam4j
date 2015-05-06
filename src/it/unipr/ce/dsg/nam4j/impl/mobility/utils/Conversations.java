package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * This class represents the list of conversations a peer is carrying on.
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

public class Conversations implements Iterable<ConversationItem> {

	/** The list of conversations a peer is carrying on */
	private Set<ConversationItem> conversations;
	
	public Conversations() {
		conversations = Collections.newSetFromMap(new ConcurrentHashMap<ConversationItem, Boolean>());
	}
	
	public boolean add(ConversationItem conversationItem) {
		if (contains(conversationItem)) {
			return true;
		} else {
			return conversations.add(conversationItem);
		}
	}
	
	public boolean contains(ConversationItem conversationItem) {
		for(ConversationItem ci : conversations) {
			if (ci.getId().equalsIgnoreCase(conversationItem.getId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method to get the {@link ConversationItem} corresponding to a specified
	 * id.
	 * 
	 * @param id
	 *            the id of the conversation
	 * 
	 * @return the conversation corresponding to a specified id
	 */
	public ConversationItem getConversationItem(String id) {
		for(ConversationItem ci : conversations) {
			if (ci.getId().equalsIgnoreCase(id)) {
				return ci;
			}
		}
		return null;
	}
	
	/**
	 * Method to remove the {@link ConversationItem} corresponding to a
	 * specified id.
	 * 
	 * @param id
	 *            the id of the conversation
	 * 
	 * @return true if the conversation was removed, false otherwise
	 */
	public boolean remove(String id) {
		ConversationItem conversationItemToRemove = null;
		for(ConversationItem ci : conversations) {
			if (ci.getId().equalsIgnoreCase(id)) {
				conversationItemToRemove = ci;
				break;
			}
		}
		if (conversationItemToRemove != null) {
			return conversations.remove(conversationItemToRemove);
		} else {
			return false;
		}
	}
	
	public int size() {
		return conversations.size();
	}
	
	@Override
	public Iterator<ConversationItem> iterator() {
		return this.conversations.iterator();
	}
	
	public boolean update(ConversationItem conversationItem) {
		boolean removed = remove(conversationItem.getId());
		boolean added = add(conversationItem);
		return removed && added;
	}
	
	public boolean addAll(Collection<ConversationItem> conversationItems) {
		boolean added = false;
		for(ConversationItem ci : conversationItems) {
			added = this.conversations.add(ci) || added;
		}
		return added;
	}
	
	public boolean isEmpty() {
		return this.conversations.isEmpty();
	}
	
	public Set<ConversationItem> getConversations() {
		return conversations;
	}
	
	public static String generateConversationItemKey() {
		Key key = new Key((new Random().nextInt()) + "");
		return key.toString();
	}
	
	public void printConversations() {
		if (!conversations.isEmpty()) {
			System.out.println("\n********** Ongoing conversations **********");

			int i = 1;

			for(ConversationItem ci : conversations) {
				System.out.println(i++ + ". " + ci.getAction() + " ; " + ci.getItemId() + " ; " + ci.getPlatform() + " ; " + ci.getPartnerContactAddress());
			}

			System.out.println("*******************************************\n");

		} else {
			System.out.println("No conversation is in progress");
		}
	}

}
