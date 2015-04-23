package it.unipr.ce.dsg.examples.centralizednetworkfm;

import it.unipr.ce.dsg.examples.ontology.Lookup;
import it.unipr.ce.dsg.examples.ontology.Publish;
import it.unipr.ce.dsg.examples.ontology.Subscribe;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;
import it.unipr.ce.dsg.s2p.centralized.utils.Key;
import it.unipr.ce.dsg.s2p.example.peer.PeerConfig;

import java.util.Random;

public class CentralizedFunctionalModule extends FunctionalModule {

	private CentralizedLogger centralizedLogger;
	private NamCentralizedPeer centralizedPeer;

	public CentralizedFunctionalModule(NetworkedAutonomicMachine nam, String pathConfig) {
		super(nam);
		this.setId("cfm");
		this.setName("CentralizedFunctionalModule");
		this.centralizedLogger = new CentralizedLogger("log/");
		this.centralizedLogger.log("I am " + this.getId() + " and I own to " + nam.getId());

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

		Key key = new Key((new Random().nextInt()) + "");

		// Random key
		Random ran = new Random();
		
		// Random port
		int port = 1024 + ran.nextInt(9999 - 1024);
		
		try {
			centralizedPeer = new NamCentralizedPeer(pathConfig, key.toString(), key.toString(), port);

			PeerConfig peerConfig = new PeerConfig(pathConfig);
			String bootstrapPeerAddress = peerConfig.bootstrap_peer;

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

	public NamCentralizedPeer getPeer() {
		return this.centralizedPeer;
	}

	public CentralizedLogger getCentralizedLogger() {
		return centralizedLogger;
	}

	public void setKademliaLogger(CentralizedLogger centralizedLogger) {
		this.centralizedLogger = centralizedLogger;
	}

}
