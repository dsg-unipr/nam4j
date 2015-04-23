package it.unipr.ce.dsg.gamidroid.reasonerfm;

import it.unipr.ce.dsg.gamidroid.ontology.Room;
import it.unipr.ce.dsg.gamidroid.ontology.Temperature;
import it.unipr.ce.dsg.gamidroid.ontology.TemperatureNotification;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

public class SearchTemperatureRunnable implements Runnable {

	ReasonerFunctionalModule rfm = null;
	String locationsFileName = "nowhere";
	Random r = null;
	private volatile boolean stopThread = false;

	public SearchTemperatureRunnable(ReasonerFunctionalModule rfm) {
		stopThread = false;
		this.rfm = rfm;
		r = new Random(987654321);
	}

	public void run() {
		// call publish service passing json message
		// look into other functional modules, looking for requested service
		Collection<FunctionalModule> c = rfm.getNam().getFunctionalModules()
				.values();
		Iterator<FunctionalModule> itr = c.iterator();
		String serviceName = null;
		FunctionalModule fm = null;
		FunctionalModule tempfm = null;
		while (itr.hasNext()) {
			tempfm = itr.next();
			if (tempfm.getName().equals(rfm.getName()))
				continue;
			// System.out.println("Temp FM: " + tempfm.getName());
			Collection<IService> cc = tempfm.getProvidedServices().values();
			Iterator<IService> itrr = cc.iterator();
			while (itrr.hasNext()) {
				serviceName = itrr.next().getName();
				// System.out.println("Service: " + serviceName);
				if (serviceName.equals("Lookup")) {
					fm = tempfm;
					// System.out.println("FM: " + fm.getName());
				}
			}
		}

		Temperature temperature = new Temperature();
		TemperatureNotification tempNotif = new TemperatureNotification();
		tempNotif.setSubject(temperature);
		Room room = new Room();
		Gson gson = new Gson();

		while (!stopThread) {
			// pick a random location among those allowed
			List<String> locations = new ArrayList<String>(rfm.getLocationMap()
					.keySet());
			room.setValue(locations.get(r.nextInt(rfm.getLocationMap().size())));
			tempNotif.setLocation(room);
			String json = gson.toJson(tempNotif);

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// rfm.getLogger().log(tempNotif);
			System.out.println("###\n [RFM] Lookup for: " + json + "\n ### \n");

			/*
			 * fm is a ChordFunctionalModule and its "execute" method takes, as
			 * second parameter, the requested service which can be Join, Leave,
			 * Lookup, Publish, Subscribe.
			 */
			fm.execute(rfm.getId(), "Lookup", json);
		}
	}

	public void stopThread() {
		stopThread = true;
	}

}
