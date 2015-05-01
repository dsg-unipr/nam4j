package it.unipr.ce.dsg.examples.contextbus;

/**
 * <p>
 * This class includes configuration constants for the distributed context bus.
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
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 *
 */

public class Utils {

	/** Full mesh network structure identifier */
	public static String FULL_MESH = "full_mesh";

	/** Random graph (Gnutella) network structure identifier */
	public static String RANDOM_GRAPH = "random_graph";

	/** Event publishing action request */
	public static String PUBLISH_REQUEST = "publish";

	/** Subscribe to event updates request */
	public static String SUBSCRIBE_REQUEST = "subscribe";

	/** Unsubscribe from event updates request */
	public static String UNSUBSCRIBE_REQUEST = "unsubscribe";

	/**
	 * Default number of nodes returned by the bootstrap when the random graph
	 * structure is used and a peer requests a peer list.
	 */
	public static int PEER_LIST_ANSWER_SIZE = 3;

	/**
	 * Each peer runs a thread that checks if the minimum number of known peers
	 * is below a threshold. If it is, a list of peers is requested to the
	 * bootstrap.
	 */
	public static int MINIMUM_NUMBER_OF_PEERS = 3;

	/** Default thread iteration interval [ms] */
	public static int THREAD_SLEEP_TIME = 20000;

	/** Default event publishing repetition interval [ms] */
	public static int EVENT_PUBLISHING_RATE = 5000;

	/** The number of hops a message has to be forwarded. */
	public static int HOPS_NUMBER = 3;

}
