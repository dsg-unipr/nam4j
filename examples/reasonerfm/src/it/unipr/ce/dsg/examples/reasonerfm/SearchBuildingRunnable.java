package it.unipr.ce.dsg.examples.reasonerfm;

import it.unipr.ce.dsg.examples.ontology.Building;
import it.unipr.ce.dsg.examples.ontology.BuildingNotification;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.service.Parameter;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

public class SearchBuildingRunnable implements Runnable {

	ReasonerFunctionalModule rfm = null;
	Random r = null;
	private volatile boolean stopThread;
	
	public SearchBuildingRunnable(ReasonerFunctionalModule rfm) {
		stopThread = false;
		this.rfm = rfm;
		r = new Random (987654321);
	}

	@Override
	public void run() {
		
			Collection<FunctionalModule> c = rfm.getNam().getFunctionalModules().values();
			Iterator<FunctionalModule> itr = c.iterator();
			String serviceName = null;
			FunctionalModule fm = null;
			FunctionalModule tempfm = null;
			
			while (itr.hasNext()) {
				tempfm = itr.next();
				if (tempfm.getName().equals(rfm.getName()))
					continue;
				Collection<IService> cc = tempfm.getProvidedServices().values();
				Iterator<IService> itrr = cc.iterator();
				while (itrr.hasNext()) {
					serviceName = itrr.next().getName();
					if (serviceName.equals("Lookup")) {
						fm = tempfm;
					}
				}
			}
			
			BuildingNotification buildingNotif = new BuildingNotification();

			Gson gson = new Gson();
		
			while (!stopThread) {
				
				List<String> locations = new ArrayList<String>(rfm.getLocationMap().keySet());
				
				Parameter parameter = new Parameter();
				parameter.setValue(locations.get(r.nextInt(rfm.getLocationMap().size())));
				
				buildingNotif.setLocation( parameter );
				buildingNotif.setSubject( new Building() );
				
				String json = gson.toJson( buildingNotif );		
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				fm.execute(rfm.getId(), "Lookup", json);
			}
	}
	
    public void stopThread(){
    	stopThread = true;
    }
		
}
