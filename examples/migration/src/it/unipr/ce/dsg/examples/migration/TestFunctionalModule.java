package it.unipr.ce.dsg.examples.migration;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.io.Serializable;

/**
 * <p>
 * This class represents an example of a functional module with a thread
 * providing methods to get started, suspended, resumed and stopped.
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

public class TestFunctionalModule extends FunctionalModule implements Serializable {

	private static final long serialVersionUID = -5131949252622985694L;
	private FunctionalModuleRunnable functionalModuleRunnable = null;
	
	public TestFunctionalModule(NetworkedAutonomicMachine nam) {
		super(nam);
		this.setId("testfm");
		this.setName("TestFunctionalModule");

		System.out.println("\nI am " + this.getId() + " and I own to " + nam.getId());
		
		// Creating the thread
		functionalModuleRunnable = new FunctionalModuleRunnableImplementation();
	}

	@Override
	public FunctionalModuleRunnable getFunctionalModuleRunnable() {
		return this.functionalModuleRunnable;
	}

	@Override
	public void addConsumableService(String id, IService service) {}

	@Override
	public void addProvidedService(String id, IService service) {}
	
	/**
	 * A counter.
	 */
	public class FunctionalModuleRunnableImplementation extends FunctionalModuleRunnable {
		private static final long serialVersionUID = -2465563711866309028L;
		private int sleepingTime = 1000;
		private int counter = 0;
		
		@Override
		public void saveState() {
			
		}
		
		@Override
		public void restoreState() {
			
		}
		
		public int getSleepingTime() {
			return sleepingTime;
		}

		public void setSleepingTime(int sleepingTime) {
			this.sleepingTime = sleepingTime;
		}

		@Override
		public void run() {
			try {
				while (true) {
					System.out.println(++counter);
					Thread.sleep(getSleepingTime());
					
					synchronized (this) {
						while (isSuspended()) {
							wait();
						}
					}
				}
			} catch (InterruptedException e) {
				 System.out.println("Thread interrupted.");
			}
		}
		
	}
}