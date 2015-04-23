package it.unipr.ce.dsg.examples.chordfm;

import it.unipr.ce.dsg.examples.ontology.Lookup;
import it.unipr.ce.dsg.examples.ontology.Publish;
import it.unipr.ce.dsg.examples.ontology.Subscribe;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2pchord.ChordPeer;
import it.unipr.ce.dsg.s2pchord.eventlistener.ChordEventListener;
import it.unipr.ce.dsg.s2pchord.resource.ResourceDescriptor;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;


public class ChordFunctionalModule extends FunctionalModule implements ChordEventListener {

	private ChordPeer chordPeer = null;
	private ChordLogger cLogger = null;
	
	public ChordFunctionalModule(NetworkedAutonomicMachine nam, String configPath) {
		super(nam);
		this.setId("cfm");
		this.setName("ChordFunctionalModule");
		this.cLogger = new ChordLogger("log/");
		this.cLogger.log("I am " + this.getId() + " and I own to " + nam.getId());
		
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
		int port = 1024 + ran.nextInt(9999-1024);
		
		try {
			chordPeer = new ChordPeer(configPath, peerKey, peerKey, port);
			chordPeer.join();
			chordPeer.setChordEventListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getRandomKey() {
		try {
			Random random = new Random();
			String key = new Integer((random.nextInt() + 1)
					* (random.nextInt() + 1)).toString();
			byte[] bytesOfMessage = key.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			
			// Convert the byte to hex format method 1
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
	
	public ChordLogger getLogger() {
		return cLogger;
	}
	
	private void join() {
		System.out.println("JOIN");
	}
	
	private void leave() {
		System.out.println("LEAVE");
	}
	
	private void lookup(String item) {
		Thread t = new Thread(new LookupRunnable(this, item, chordPeer), "Lookup thread");
		t.start();
	}
	
	private void publish(String item) {
		Thread t = new Thread(new PublishRunnable(this, item, chordPeer), "Publish thread");
		t.start();
	}
	
	private void subscribe(String fmId, String item) {
		System.out.println(fmId + " SUBSCRIBE to " + item);
	}
	
	public void execute(String requestorId, String requestedService, String parameters) {
		if (requestedService.equals("Join")) {
			this.join();
		}
		if (requestedService.equals("Leave")) {
			this.leave();
		}
		if (requestedService.equals("Lookup")) {
			this.lookup(parameters);
		}
		if (requestedService.equals("Publish")) {
			this.publish(parameters);
		}
		if (requestedService.equals("Subscribe")) {
			this.subscribe(requestorId, parameters);
		}
	}

	@Override
	public void searchResultEvent(String resourceKey, ResourceDescriptor rd, PeerDescriptor responsiblePeer, PeerDescriptor ownerPeer) {
		
		// look into other functional modules, looking for requested service
		
		Collection<FunctionalModule> c = this.getNam().getFunctionalModules().values();
		Iterator<FunctionalModule> itr = c.iterator();
		String serviceName = null;
		FunctionalModule fm = null;
		FunctionalModule tempfm = null;
		
		while (itr.hasNext()) {
			
			tempfm = itr.next();
			
			if (tempfm.getName().equals(this.getName()))
				continue;
			
			Collection<IService> cc = tempfm.getProvidedServices().values();
			Iterator<IService> itrr = cc.iterator();
			
			while (itrr.hasNext()) {
				serviceName = itrr.next().getName();
				
				if (serviceName.equals("Notify")) {
					fm = tempfm;
				}
			}
		}
		
		fm.execute(this.getId(), "Notify", rd.getAttachment());
	}

	@Override
	public void publishResultEvent(ResourceDescriptor rd, PeerDescriptor responsiblePeer, PeerDescriptor ownerPeer) {
		cLogger.log("Received a Publish Result Event Notification for resource: " + rd.getKey() + " with responsible: " + responsiblePeer.getKey() + " and owner: " + ownerPeer.getKey());	
	}

	@Override
	public void addConsumableService(String id, IService service) {
		
	}

	@Override
	public void addProvidedService(String id, IService service) {
		
	}

}
