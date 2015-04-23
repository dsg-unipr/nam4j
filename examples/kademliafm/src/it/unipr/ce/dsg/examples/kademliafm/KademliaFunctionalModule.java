package it.unipr.ce.dsg.examples.kademliafm;

import it.unipr.ce.dsg.examples.ontology.Lookup;
import it.unipr.ce.dsg.examples.ontology.Publish;
import it.unipr.ce.dsg.examples.ontology.Subscribe;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.security.MessageDigest;
import java.util.Random;

public class KademliaFunctionalModule extends FunctionalModule {

	private NamKademliaPeer kademliaPeer;
	private KademliaLogger kademliaLogger = null;

	public KademliaFunctionalModule(NetworkedAutonomicMachine nam, String configPath) {
		super(nam);
		this.setId("kfm");
		this.setName("KademliaFunctionalModule");
		this.kademliaLogger = new KademliaLogger("log/");
		this.kademliaLogger.log("I am " + this.getId() + " and I own to "
				+ nam.getId());

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

		// Random key
		String peerKey = getRandomKey();
		
		// Random port
		Random ran = new Random();
		int port = 1024 + ran.nextInt(9999 - 1024);

		try {
			// A null path for the certificate does so that the default location
			// is chosen
			// kademliaPeer = new KademliaPeer(kademliaConfigFile, peerKey);
			kademliaPeer = new NamKademliaPeer(configPath,
					peerKey, peerKey, port, null, false);

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

	private String getRandomKey() {
		try {
			Random random = new Random();
			String key = new Integer((random.nextInt() + 1)
					* (random.nextInt() + 1)).toString();
			byte[] bytesOfMessage = key.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < thedigest.length; i++) {
				sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public KademliaLogger getLogger() {
		return kademliaLogger;
	}

	private void lookup(String item) {
		Thread t = new Thread(new LookupRunnable(this, item, kademliaPeer),
				"Lookup thread");
		t.start();
	}

	private void publish(String item) {
		Thread t = new Thread(new PublishRunnable(this, item, kademliaPeer),
				"Publish thread");
		t.start();
	}

	public void execute(String requestorId, String requestedService,
			String parameters) {
		if (requestedService.equals("Lookup")) {
			this.lookup(parameters);
		}
		if (requestedService.equals("Publish")) {
			this.publish(parameters);
		}
	}

	public NamKademliaPeer getPeer() {
		return this.kademliaPeer;
	}

	public KademliaLogger getKademliaLogger() {
		return kademliaLogger;
	}

	public void setKademliaLogger(KademliaLogger kademliaLogger) {
		this.kademliaLogger = kademliaLogger;
	}

}
