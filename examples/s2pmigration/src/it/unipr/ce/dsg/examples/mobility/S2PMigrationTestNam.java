package it.unipr.ce.dsg.examples.mobility;

import it.unipr.ce.dsg.examples.mobility.runnable.ManageInputRunnable;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.s2p.centralized.utils.Key;

import java.util.Random;

public class S2PMigrationTestNam extends NetworkedAutonomicMachine {
	
	private MccNamPeer peer;

	public S2PMigrationTestNam(int poolSize, String migrationStorePath, int trialsNumber, String pathConfig) {
		super(poolSize, migrationStorePath, trialsNumber);

		Key key = new Key((new Random().nextInt()) + "");

		// Random key
		Random ran = new Random();
		
		// Random port
		int port = 1024 + ran.nextInt(9999 - 1024);
		
		this.setId("S2PMigrationNam");
		this.setClientPlatform(Platform.DESKTOP, 0);
		
		peer = new MccNamPeer(pathConfig, key.toString(), key.toString(), port, this);
	}
	
	public MccNamPeer getPeer() {
		return peer;
	}
	
	public void setPeer(MccNamPeer peer) {
		this.peer = peer;
	}

	/**
	 * args[0]: the path to the config file
	 * args[1]: the path where to store received files
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please provide 2 parameters:\n1. the path to the config file\n2. where to store received jars");
		} else {
			S2PMigrationTestNam s2PMigrationTestNam = new S2PMigrationTestNam(10, args[1], 3, args[0]);
			ManageInputRunnable manageInputRunnable = new ManageInputRunnable(s2PMigrationTestNam);
			Thread manageInput = new Thread(manageInputRunnable);
			manageInput.start();
		}
	}

}
