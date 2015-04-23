package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;

/**
 * <p>
 * Interface for the implementation of the OBSERVER pattern. Such an interface
 * is intended to be used by nodes needing to get notified when an item is
 * available and can be added to the classpath and possibly has to start its
 * execution. It is mainly used when items are received by Android nodes.
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

public interface IMobilityItemAvailability {

	/**
	 * Method that notifies observers that an item is available and has to be
	 * added to the classpath.
	 * 
	 * @param fileFullPath
	 *            The path of the file that has to be added to the classpath,
	 *            including the file's name and extension
	 * 
	 * @param mainClassName
	 *            The main class name of the file that has to be added to the
	 *            classpath if it is a FM or a Service. Null if it is a
	 *            dependency.
	 * 
	 * @param role
	 *            The {@link MigrationSubject} of the file that has to be added
	 *            to the classpath.
	 * 
	 * @param state
	 *            The execution state to notify observer it has been received.
	 *            Null if the notification concerns a file.
	 */
	public void onItemIsAvailable(String fileFullPath, String mainClassName, MigrationSubject role, Action action, Object state);
}
