package it.unipr.ce.dsg.nam4j.interfaces;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import it.unipr.ce.dsg.nam4j.impl.peer.PeerList;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

/**
 * <p>
 * This interface represents a list of peer descriptors.
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

public interface IPeerList {

	/**
	 * Method to add the specified {@link PeerDescriptor} to the
	 * {@link PeerList} if it is not already present. If the {@link PeerList}
	 * already contains the {@link PeerDescriptor}, the call leaves the
	 * {@link PeerList} unchanged and returns false.
	 * 
	 * @param peerDescriptor
	 *            {@link PeerDescriptor} to be added to this {@link PeerList}
	 * 
	 * @return true if the {@link PeerList} did not already contain the
	 *         specified {@link PeerDescriptor}, false otherwise
	 */
	boolean add(PeerDescriptor peerDescriptor);
	
	/**
	 * Method to check if if the {@link PeerList} contains the specified
	 * {@link PeerDescriptor}.
	 * 
	 * @param peerDescriptor
	 *            {@link PeerDescriptor} whose presence in the {@link PeerList}
	 *            is to be tested
	 * 
	 * @return true if the {@link PeerList} contains the specified
	 *         {@link PeerDescriptor}
	 */
	boolean contains(PeerDescriptor peerDescriptor);
	
	/**
	 * Method to remove the specified {@link PeerDescriptor} from this
	 * {@link PeerList} if it is present. Returns true if the {@link PeerList}
	 * contained the {@link PeerDescriptor} (or equivalently, if the
	 * {@link PeerList} changed as a result of the call). (This {@link PeerList}
	 * will not contain the {@link PeerDescriptor} once the call returns)
	 * 
	 * @param peerDescriptor
	 *            {@link PeerDescriptor} to be removed from this
	 *            {@link PeerList}, if present
	 * 
	 * @return true if the {@link PeerList} contained the specified
	 *         {@link PeerDescriptor}, false otherwise
	 */
	boolean remove(PeerDescriptor peerDescriptor);
	
	/**
	 * Method to get the number of {@link PeerDescriptor} in the
	 * {@link PeerList} (its cardinality).
	 * 
	 * @return the number of {@link PeerDescriptor} in the {@link PeerList} (its
	 *         cardinality)
	 */
	int size();
	
	Iterator<PeerDescriptor> iterator();
	
	/**
	 * Method to update the specified {@link PeerDescriptor} from the
	 * {@link PeerList} if it is present. Returns true if the {@link PeerList}
	 * contained the {@link PeerDescriptor}, false otherwise.
	 * 
	 * @param peerDescriptor
	 *            {@link PeerDescriptor} to be updated from this
	 *            {@link PeerList}, if present
	 * 
	 * @return true if the {@link PeerList} contained the specified
	 *         {@link PeerDescriptor}
	 */
	boolean update(PeerDescriptor peerDescriptor);
	
	/**
	 * Method to add all {@link PeerDescriptor}s passed as a collection object
	 * argument to the {@link PeerList}. The behavior of this operation is
	 * undefined if the specified collection is modified while the operation is
	 * in progress.
	 * 
	 * @param peerDescriptors
	 *            collection containing {@link PeerDescriptor}s to be added to
	 *            the {@link PeerList}
	 * 
	 * @return true if the {@link PeerList} changed as a result of the call
	 */
	boolean addAll(Collection<PeerDescriptor> peerDescriptors);
	
	/**
	 * Method to get the {@link PeerDescriptor}s contained in the
	 * {@link PeerList}.
	 * 
	 * @return the {@link PeerDescriptor}s contained in the {@link PeerList}
	 */
	Set<PeerDescriptor> getPeerDescriptors();
	
	/**
	 * Method to display on console the list of known peers.
	 */
	void printPeerList();
}
