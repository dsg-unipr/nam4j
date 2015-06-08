package it.unipr.ce.dsg.examples.mobility.runnable;

import it.unipr.ce.dsg.examples.migration.EightQueensProblemService;
import it.unipr.ce.dsg.examples.migration.SudokuService;
import it.unipr.ce.dsg.examples.migration.TestFunctionalModule;
import it.unipr.ce.dsg.examples.migration.TextParserService;
import it.unipr.ce.dsg.examples.mobility.S2PMigrationTestNam;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils.EncryptionAlgorithm;
import it.unipr.ce.dsg.nam4j.impl.service.Service;

import java.io.InputStreamReader;
import java.util.Scanner;

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
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ManageInputRunnable implements Runnable {
	
	boolean stopThread = false;

	private S2PMigrationTestNam s2PMigrationTestNam;
	private MccNamPeer peer;
	
	public ManageInputRunnable(S2PMigrationTestNam s2PMigrationTestNam) {
		this.s2PMigrationTestNam = s2PMigrationTestNam;
		this.peer = this.s2PMigrationTestNam.getPeer();
	}

	@Override
	public void run() {
		
		Scanner scanner = new Scanner(new InputStreamReader(System.in));
		while(true) {
			System.out.print("\n*********************************\n"
							+ "*  pd: print peer descriptor    *\n"
							+ "*  p: send PING                 *\n"
							+ "*  k: show known nodes          *\n"
							+ "*  rfm: request a FM (COPY)     *\n"
							+ "*  rs: request a service (COPY) *\n"
							+ "*  sfm: send a FM (MIGRATE)     *\n"
							+ "*  ss: send a service (MIGRATE) *\n"
							+ "*  q: quit                      *\n"
							+ "*********************************\n"
							+ "Enter command: ");
			
			String choice = scanner.nextLine().trim();

			if (choice.equals("pd")) {
				System.out.println(peer.getPeerDescriptor());				
			}
			
			else if (choice.equals("p")) {
				System.out.print("Enter contact address: ");
				String ca = scanner.nextLine().trim();
				peer.ping(ca);				
			}

			else if (choice.equals("k")) {
				peer.getPeerList().printPeerList();
			}
			
			else if (choice.equals("rfm")) {
				System.out.print("Please provide the contact address of the peer to which the request has to be sent: ");
				String ca = scanner.nextLine().trim();
				System.out.print("Please provide the FM id: ");
				String fmId = scanner.nextLine().trim();
				peer.requestFM(ca, fmId, Platform.DESKTOP, Action.COPY, "1.0", EncryptionAlgorithm.AES_CBC);
			}
			
			else if (choice.equals("rs")) {
				System.out.print("Please provide the contact address of the peer to which the request has to be sent: ");
				String ca = scanner.nextLine().trim();
				System.out.print("Please provide the service id: ");
				String sId = scanner.nextLine().trim();
				peer.requestService(ca, sId, Platform.DESKTOP, Action.COPY, "1.0", EncryptionAlgorithm.AES_CBC);
			}
			
			else if (choice.equals("sfm")) {
				System.out.print("Please provide the contact address of the peer to which the request has to be sent: ");
				String ca = scanner.nextLine().trim();
				// System.out.print("Please provide the FM id: ");
				// String fmId = scanner.nextLine().trim();
				String fmId = "TestFunctionalModule";
				
				TestFunctionalModule tfm = new TestFunctionalModule(s2PMigrationTestNam);
				tfm.getFunctionalModuleRunnable().start();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {}
				tfm.getFunctionalModuleRunnable().saveState();
				tfm.getFunctionalModuleRunnable().suspend();
				
				peer.migrateFM(ca, fmId, Platform.ANDROID, Action.MIGRATE, tfm, MigrationSubject.FM, "1.0", EncryptionAlgorithm.AES_CBC);
			}
			
			else if (choice.equals("ss")) {
				System.out.print("Please provide the contact address of the peer to which the request has to be sent: ");
				String ca = scanner.nextLine().trim();
				System.out.print("Please provide the service id: ");
				String sId = scanner.nextLine().trim();
				
				TestFunctionalModule tfm = new TestFunctionalModule(s2PMigrationTestNam);

				Service ts = null;
				
				if (sId.equals("TextParserService")) {
					ts = new TextParserService();
				} else if (sId.equals("SudokuService")) {
					ts = new SudokuService();
				} else if (sId.equals("EightQueensProblemService")) {
					ts = new EightQueensProblemService();
				}

				if (ts != null) {
					ts.setFunctionalModule(tfm);
					ts.getServiceRunnable().start();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ts.getServiceRunnable().saveState();
					ts.getServiceRunnable().suspend();
				}
				
				peer.migrateService(ca, sId, Platform.ANDROID, Action.MIGRATE, ts, MigrationSubject.SERVICE, "1.0", EncryptionAlgorithm.AES_CBC);
			}

			else if (choice.equals("q")) {
				scanner.close();
				System.exit(0);
			}
			
			else System.out.println("No command named " + choice + " is specified.");
		}
	}
	
}