package it.unipr.ce.dsg.nam4j.interfaces;

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

public interface IMigrationListener {

	public void onReceivedItem(String itemName, String completeClassName, MigrationSubject fileType);
}
