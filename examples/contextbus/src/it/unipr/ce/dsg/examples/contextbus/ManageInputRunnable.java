package it.unipr.ce.dsg.examples.contextbus;

import it.unipr.ce.dsg.nam4j.interfaces.IPeer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>
 * This class represents a {@link Runnable} to manage input commands provided by
 * the user.
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

public class ManageInputRunnable implements Runnable {
	
	boolean stopThread = false;

	private IPeer peer;
	
	public ManageInputRunnable(IPeer peer) {
		this.peer = peer;
	}

	@Override
	public void run() {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println();
			System.out.println("***************************");
			System.out.println("*  p: send PING           *");
			System.out.println("*  k: show known nodes    *");
			System.out.println("*  q: quit                *");
			System.out.println("***************************");
			System.out.print("Enter command: ");
			try {
				String choice = br.readLine().trim();
				
				if (choice.equals("p")) {
					System.out.print("Enter contact address: ");
					String ca = br.readLine().trim();
					peer.ping(ca);				
				}

				else if (choice.equals("k")) {
					peer.getPeerList().printPeerList();
				}

				else if (choice.equals("q")) {
					br.close();
					System.exit(0);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}