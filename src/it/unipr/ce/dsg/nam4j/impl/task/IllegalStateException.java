package it.unipr.ce.dsg.nam4j.impl.task;

/**
 * <p>
 * This class represents the exception that must be thrown when 
 * an illegal task state is reached.
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
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class IllegalStateException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
     * constructor
     */
	public IllegalStateException(String errorMsg) {
		super(errorMsg);
	}

}
