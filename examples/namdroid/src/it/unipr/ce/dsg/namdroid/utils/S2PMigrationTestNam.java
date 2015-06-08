package it.unipr.ce.dsg.namdroid.utils;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.Key;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils.EncryptionAlgorithm;
import it.unipr.ce.dsg.nam4j.interfaces.IMobilityItemAvailability;
import it.unipr.ce.dsg.namdroid.interfaces.IMobilityItemIsAvailableObserver;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.os.Environment;

public class S2PMigrationTestNam extends NetworkedAutonomicMachine implements IMobilityItemAvailability {
	
	private static S2PMigrationTestNam s2PMigrationTestNam = null;
	private static String pathToSaveFile = Environment.getExternalStorageDirectory().toString() + MobilityUtils.PATH_SEPARATOR;
	private MccNamPeer peer;
	private ArrayList<IMobilityItemIsAvailableObserver> listeners;
	
	/** Set the variable to the address of the peer to be contacted */
	String peerToBeContactedAddress = "1001010111111010000010001111010111001110001111110101100001111100011100001101000101011010100101001110100111010010100001110000101011011111010110101110001001001110@192.168.1.146:9394";

	private S2PMigrationTestNam(Context mContext, String confFile) {
		super(10, pathToSaveFile, 3, Platform.ANDROID);
		
		this.listeners = new ArrayList<IMobilityItemIsAvailableObserver>();

		Key key = new Key((new Random().nextInt()) + "");

		// Random key
		Random ran = new Random();
		
		// Random port
		int port = 1024 + ran.nextInt(9999 - 1024);
		
		this.setId("S2PMigrationNam");
		this.setClientPlatform(Platform.ANDROID, 0);
		
		peer = new MccNamPeer(null, key.toString(), key.toString(), port, this);
		peer.addMobilityItemAvailabilityListener(this);
		System.out.println(peer.getPeerDescriptor());
	}
	
	/** Method to request a text parser Service. */
	public void requestTextParseService() {
		peer.requestService(peerToBeContactedAddress, "TextParserService", Platform.ANDROID, Action.COPY, "1.0", EncryptionAlgorithm.AES_CTR);
	}
	
	/** Method to request a Sudoku solver Service. */
	public void requestSudokuService() {
		peer.requestService(peerToBeContactedAddress, "SudokuService", Platform.ANDROID, Action.COPY, "1.0", EncryptionAlgorithm.AES_CTR);
	}
	
	/** Method to request a N queens problem solver Service. */
	public void requestNQueensProblemService() {
		peer.requestService(peerToBeContactedAddress, "EightQueensProblemService", Platform.ANDROID, Action.COPY, "1.0", EncryptionAlgorithm.AES_CTR);
	}
	
	/** Sample method to test FM requests. */
	public void performRequestFMTest() {
		peer.requestFM(peerToBeContactedAddress, "TestFunctionalModule", Platform.ANDROID, Action.COPY, "1.0", EncryptionAlgorithm.AES_CTR);
	}
	
	public void addIMobilityItemIsAvailableObserver(IMobilityItemIsAvailableObserver iMobilityItemIsAvailableObserver) {
		this.listeners.add(iMobilityItemIsAvailableObserver);
	}
	
	public static S2PMigrationTestNam getInstance(Context mContext) {
		if (s2PMigrationTestNam == null)
			s2PMigrationTestNam = new S2PMigrationTestNam(mContext, null);
		return s2PMigrationTestNam;
	}
	
	public MccNamPeer getPeer() {
		return peer;
	}

	/**
	 * Observer pattern implementation to manage event notifications.
	 */
	@Override
	public void onItemIsAvailable(String fileFullPath, String mainClassName, MigrationSubject role, Action action, Object state) {
		for(IMobilityItemIsAvailableObserver listener : listeners) {
			listener.onItemIsAvailable(fileFullPath, mainClassName, role, action, state);
		}
	}

}
