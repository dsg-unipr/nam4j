package it.unipr.ce.dsg.namdroid.interfaces;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;

/**
 * <p>
 * Interface for the implementation of the OBSERVER pattern. Such an interface
 * is intended to be used by objects that need to get notified when an item has
 * been received. It is especially useful when items received by Android nodes
 * have to influence the UI.
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

public interface IMobilityItemIsAvailableObserver {

	/**
	 * Method that notifies observers that an item is available and has to be
	 * added to the class path.
	 * 
	 * @param itemName
	 *            The path of the file that has to be added to the class path,
	 *            including the file's name and extension
	 * 
	 * @param completeClassName
	 *            The main class name of the file that has to be added to the
	 *            class path if it is a FM or a Service. Null if it is a
	 *            dependency.
	 * 
	 * @param fileType
	 *            The {@link MigrationSubject} of the file that has to be added
	 *            to the class path.
	 * 
	 * @param action
	 *            The mobility action which generated the notification
	 * 
	 * @param state
	 *            The execution state to notify observer it has been received.
	 *            Null if the notification concerns a file.
	 */
	public void onItemIsAvailable(String itemName, String completeClassName, MigrationSubject fileType, Action action, Object state);
}
