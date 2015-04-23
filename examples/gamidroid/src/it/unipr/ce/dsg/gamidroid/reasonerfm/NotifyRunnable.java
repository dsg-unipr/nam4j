package it.unipr.ce.dsg.gamidroid.reasonerfm;

import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

public class NotifyRunnable implements Runnable {
	
	private ReasonerFunctionalModule rfm = null;
	private String item = null;
	
	public NotifyRunnable(ReasonerFunctionalModule rfm, String item) {
		this.rfm = rfm;
		this.item = item;
	}
	
	public void run() {
		// rfm.getLogger().log("Notified: " + item);
		Gson gson = new Gson();
		ContextEvent ce = gson.fromJson(item, ContextEvent.class);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date timestamp = null;
		try {
			timestamp = df.parse(ce.getTimestamp());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// rfm.getLogger().log("Timestamp of retrieved ce: " + timestamp.toString());
		String location = ce.getLocation().getValue();
		// rfm.getLogger().log("Location of retrieved ce: " + location);
		Date knownDate = rfm.getLocationMap().get(location);
		// rfm.getLogger().log("Timestamp of previously known temperature for that location: " + knownDate.toString());
		
		// if retrieved temperature is more recent than the known one, update 
		if (timestamp.after(knownDate))
			rfm.getLocationMap().put(location, timestamp);
		
		// rfm.getLogger().log("Temperature updated for room " + location);
	}

}
