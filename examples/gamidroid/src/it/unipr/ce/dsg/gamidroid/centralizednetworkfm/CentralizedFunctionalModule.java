package it.unipr.ce.dsg.gamidroid.centralizednetworkfm;


import it.unipr.ce.dsg.gamidroid.ontology.Lookup;
import it.unipr.ce.dsg.gamidroid.ontology.Publish;
import it.unipr.ce.dsg.gamidroid.ontology.Subscribe;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2p.centralized.utils.Key;
import it.unipr.ce.dsg.s2pchord.PeerConfig;

import java.io.File;
import java.util.Random;

import android.os.Environment;

public class CentralizedFunctionalModule extends FunctionalModule {

	private CentralizedLogger centralizedLogger;
	private NamCentralizedPeer centralizedPeer;
	private String bootstrapPeerAddress;

	public CentralizedFunctionalModule(NetworkedAutonomicMachine nam, String pathConfig, String nodeName) {
		super(nam);
		this.setId("cfm");
		this.setName("CentralizedFunctionalModule");
		
		File sdLog = new File(Environment.getExternalStorageDirectory()
				+ Constants.CONFIGURATION_FILES_PATH);
		
		this.centralizedLogger = new CentralizedLogger(sdLog.getAbsolutePath() + "/");
		this.centralizedLogger.log("I am " + this.getId() + " and I own to " + nam.getId());
		
		System.out.println("I am " + this.getId() + " and I own to " + nam.getId());

		// Create Service objects and add to providedServices hashmap
		Lookup lookupService = new Lookup();
		lookupService.setId("s1");
		this.addProvidedService(lookupService.getId(), lookupService);

		Publish publishService = new Publish();
		publishService.setId("s2");
		this.addProvidedService(publishService.getId(), publishService);

		Subscribe subscribeService = new Subscribe();
		subscribeService.setId("s3");
		this.addProvidedService(subscribeService.getId(), subscribeService);

		Key key = new Key(nodeName + (new Random().nextInt()));

		try {
			centralizedPeer = new NamCentralizedPeer(pathConfig, key.toString());

			PeerConfig peerConfig = new PeerConfig(pathConfig);
			bootstrapPeerAddress = peerConfig.bootstrap_peer;

			centralizedPeer.join(bootstrapPeerAddress);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addConsumableService(String id, IService service) {

	}

	@Override
	public void addProvidedService(String id, IService service) {

	}

	public CentralizedLogger getLogger() {
		return centralizedLogger;
	}

	private void lookup(String item) {
		Thread t = new Thread(new LookupRunnable(this, item, centralizedPeer), "Lookup thread");
		t.start();
	}

	private void publish(String item) {
		Thread t = new Thread(new PublishRunnable(this, item, centralizedPeer), "Publish thread");
		t.start();
	}

	public void execute(String requestorId, String requestedService, String parameters) {
		if (requestedService.equals("Lookup")) {
			this.lookup(parameters);
		}
		if (requestedService.equals("Publish")) {
			this.publish(parameters);
		}
	}
	
	public void addEventListener(IEventListener eventListener) {
		this.centralizedPeer.addEventListener(eventListener);
	}
	
	public void disconnect() {
		this.centralizedPeer.leave(bootstrapPeerAddress);
	}

	public NamCentralizedPeer getPeer() {
		return this.centralizedPeer;
	}

	public CentralizedLogger getCentralizedLogger() {
		return centralizedLogger;
	}

	public void setCentralizedLogger(CentralizedLogger centralizedLogger) {
		this.centralizedLogger = centralizedLogger;
	}

}