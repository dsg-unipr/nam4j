package it.unipr.ce.dsg.examples.mobility;

import it.unipr.ce.dsg.examples.mobility.runnable.ManageInputRunnable;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.Key;

import java.util.Random;

public class S2PMigrationTestNam extends NetworkedAutonomicMachine {
	
	private MccNamPeer peer;

	public S2PMigrationTestNam(String migrationStorePath, String pathConfig) {
		super(migrationStorePath, Platform.DESKTOP);

		Key key = new Key((new Random().nextInt()) + "");

		// Random key
		Random ran = new Random();
		
		// Random port
		int port = 1024 + ran.nextInt(9999 - 1024);
		
		this.setId("S2PMigrationNam");
		
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
			S2PMigrationTestNam s2PMigrationTestNam = new S2PMigrationTestNam(args[1], args[0]);
			ManageInputRunnable manageInputRunnable = new ManageInputRunnable(s2PMigrationTestNam);
			Thread manageInput = new Thread(manageInputRunnable);
			manageInput.start();
		}
	}

}
