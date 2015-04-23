package it.unipr.ce.dsg.nam4j.impl.peer;

import it.unipr.ce.dsg.nam4j.interfaces.IPeerList;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * This class represents a list of peer descriptors.
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

public class PeerList implements Iterable<PeerDescriptor>, IPeerList {

	/** The peer list container. */
	private Set<PeerDescriptor> peerDescriptors;

	/** Constructs a new, empty {@link PeerList}. */
	public PeerList() {
		peerDescriptors = Collections.newSetFromMap(new ConcurrentHashMap<PeerDescriptor, Boolean>());
	}

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
	@Override
	public boolean add(PeerDescriptor peerDescriptor) {
		if (contains(peerDescriptor)) {
			return true;
		} else {
			return peerDescriptors.add(peerDescriptor);
		}
	}

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
	@Override
	public boolean contains(PeerDescriptor peerDescriptor) {
		for(PeerDescriptor pd : peerDescriptors) {
			if (pd.getContactAddress().equalsIgnoreCase(peerDescriptor.getContactAddress())) {
				return true;
			}
		}
		return false;
	}

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
	@Override
	public boolean remove(PeerDescriptor peerDescriptor) {
		PeerDescriptor peerDescriptorToRemove = null;
		for(PeerDescriptor pd : peerDescriptors) {
			if (pd.getContactAddress().equalsIgnoreCase(peerDescriptor.getContactAddress())) {
				peerDescriptorToRemove = pd;
				break;
			}
		}
		if (peerDescriptorToRemove != null) {
			return peerDescriptors.remove(peerDescriptorToRemove);
		} else {
			return false;
		}
	}

	/**
	 * Method to get the number of {@link PeerDescriptor} in the
	 * {@link PeerList} (its cardinality).
	 * 
	 * @return the number of {@link PeerDescriptor} in the {@link PeerList} (its
	 *         cardinality)
	 */
	@Override
	public int size() {
		return peerDescriptors.size();
	}

	@Override
	public Iterator<PeerDescriptor> iterator() {
		return peerDescriptors.iterator();
	}

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
	@Override
	public boolean update(PeerDescriptor peerDescriptor) {
		boolean removed = remove(peerDescriptor);
		boolean added = add(peerDescriptor);
		return removed && added;
	}

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
	@Override
	public boolean addAll(Collection<PeerDescriptor> peerDescriptors) {
		boolean added = false;
		for(PeerDescriptor pd : peerDescriptors) {
			added = this.peerDescriptors.add(pd) || added;
		}
		return added;
	}

	/**
	 * Returns true if the {@link PeerList} contains no {@link PeerDescriptor}s.
	 * 
	 * @return true if the {@link PeerList} contains no {@link PeerDescriptor}s.
	 */
	public boolean isEmpty() {
		return peerDescriptors.isEmpty();
	}

	/**
	 * Method to get the {@link PeerDescriptor}s contained in the
	 * {@link PeerList}.
	 * 
	 * @return the {@link PeerDescriptor}s contained in the {@link PeerList}
	 */
	@Override
	public Set<PeerDescriptor> getPeerDescriptors() {
		return peerDescriptors;
	}
	
	/**
	 * Method to display on console the list of known peers.
	 * 
	 * @param list
	 *            The list of peers to be printed
	 */
	@Override
	public void printPeerList() {
		
		if (!peerDescriptors.isEmpty()) {

			System.out.println("\n********** Known peers **********");

			int i = 1;

			for(PeerDescriptor pd : peerDescriptors) {
				System.out.println(i++ + ". " +  pd.getContactAddress());
			}

			System.out.println("*********************************\n");

		} else {
			System.out.println("No known peer");
		}
	}
	
	/**
	 * Method to get a number of {@link PeerDescriptor}s contained in the
	 * {@link PeerList}, chosen in a random way. This method is used to answer
	 * to peer list requests when certain types of network structures are used.
	 * Therefore, the requesting peer's descriptor does not have to get included
	 * in the returned list.
	 * 
	 * @param numberOfPeerDescriptors
	 *            The number of peer descriptors to be returned
	 * 
	 * @param peerToBeIgnored
	 *            A {@link PeerDescriptor} that does not have to get included in
	 *            the list
	 * 
	 * @return a number of {@link PeerDescriptor}s contained in the
	 *         {@link PeerList}, chosen in a random way
	 */
	public Set<PeerDescriptor> getRandomPeerDescriptors(int numberOfPeerDescriptors, PeerDescriptor peerToBeIgnored) {
		
		Set<PeerDescriptor> randomPeerDescriptors = Collections.newSetFromMap(new ConcurrentHashMap<PeerDescriptor, Boolean>());;
		
		Random rand = new Random();
		
		for(int i = 0; i < numberOfPeerDescriptors; i++) {
			
			// Generate a random integer between 1 and the number of known peers
			int randomNum = rand.nextInt((this.size() - 1) + 1) + 1;
			int j = 1;
			
			for(PeerDescriptor pd : this.getPeerDescriptors()) {
				if (j == randomNum && !(randomPeerDescriptors.contains(pd)) && !(peerToBeIgnored.getContactAddress().equalsIgnoreCase(pd.getContactAddress()))) {
					randomPeerDescriptors.add(pd);
					
					if (randomPeerDescriptors.contains(pd)) {
						i--;
						break;
					}
				}
				j++;
			}
		}
		
		return randomPeerDescriptors;
	}
	
}
