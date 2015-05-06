package it.unipr.ce.dsg.namdroid.utils;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.Key;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
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
	String peerToBeContactedAddress = "1101001011011100110010100001101110111101000010100001101100011000010100100001001110010001001111111010101101110011011010111110010010001111100010000101100101000111@160.78.27.183:3121";

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
		peer.requestService(peerToBeContactedAddress, "TextParserService", Platform.ANDROID, Action.COPY, "1.0");
	}
	
	/** Method to request a Sudoku solver Service. */
	public void requestSudokuService() {
		peer.requestService(peerToBeContactedAddress, "SudokuService", Platform.ANDROID, Action.COPY, "1.0");
	}
	
	/** Method to request a Sudoku solver Service. */
	public void requestNQueensProblemService() {
		peer.requestService(peerToBeContactedAddress, "EightQueensProblemService", Platform.ANDROID, Action.COPY, "1.0");
	}
	
	/** Sample method to test FM requests. */
	public void performRequestFMTest() {
		peer.requestFM(peerToBeContactedAddress, "TestFunctionalModule", Platform.ANDROID, Action.COPY, "1.0");
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
