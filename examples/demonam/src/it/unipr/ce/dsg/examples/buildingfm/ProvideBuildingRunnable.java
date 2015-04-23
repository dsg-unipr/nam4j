package it.unipr.ce.dsg.examples.buildingfm;

import it.unipr.ce.dsg.examples.ontology.Building;
import it.unipr.ce.dsg.examples.ontology.BuildingNotification;
import it.unipr.ce.dsg.examples.ontology.FloorStruct;
import it.unipr.ce.dsg.examples.ontology.Location;
import it.unipr.ce.dsg.examples.ontology.RoomStruct;
import it.unipr.ce.dsg.examples.ontology.Sensor;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.service.Parameter;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class ProvideBuildingRunnable implements Runnable {

	private BuildingFunctionalModule bfm = null;
	
	
	public ProvideBuildingRunnable( BuildingFunctionalModule bfm) {
		
		this.bfm = bfm;

	}

	@Override
	public void run() {
		
		Collection<FunctionalModule> c = bfm.getNam().getFunctionalModules().values();
		Iterator<FunctionalModule> itr = c.iterator();
		String serviceName = null;
		FunctionalModule fm = null;
		FunctionalModule tempfm = null;
		while (itr.hasNext()) {
			tempfm = itr.next();
			if (tempfm.getName().equals(bfm.getName()))
				continue;

			Collection<IService> cc = tempfm.getProvidedServices().values();
			Iterator<IService> itrr = cc.iterator();
			while (itrr.hasNext()) {
				serviceName = itrr.next().getName();

				if (serviceName.equals("Publish")) {
					fm = tempfm;
				}
			}
		}

		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BuildingNotification buildingNotification = new BuildingNotification();
			Location location = new Location();
			try {
				
				DocumentBuilderFactory factory;
				DocumentBuilder parser;
				Document document;
				factory = DocumentBuilderFactory.newInstance();
				parser = factory.newDocumentBuilder();
				File file = new File("building.xml");
				
				document = parser.parse( file);
				Element rootElement = (Element)document.getDocumentElement();
				location.setBuilding( new Building( rootElement.getAttribute("name")));
				
				NodeList floors = rootElement.getElementsByTagName("FLOOR");
				List<FloorStruct> listFloors = new ArrayList<FloorStruct>();				
				
				
				for ( int i = 0 ; i < floors.getLength() ; i++ ){

					Element floorElement = (Element)floors.item(i);
					
					NodeList rooms = floorElement.getElementsByTagName("ROOM");
					FloorStruct floorNotification = new FloorStruct();
					floorNotification.setName( floorElement.getAttribute("name"));
					
					for ( int j = 0 ; j < rooms.getLength() ; j++ ){
						
						Element roomElement = (Element)rooms.item(j);
						
						NodeList sensors = roomElement.getElementsByTagName("SENSOR"); 
						RoomStruct roomNotification = new RoomStruct();
						roomNotification.setName( roomElement.getAttribute("name"));
						
						for ( int k = 0 ; k < sensors.getLength() ; k++ ){
							
							Element sensorElement = (Element)sensors.item(k);
							Sensor sensor = new Sensor();
							sensor.setValue( sensorElement.getAttribute("name") );
							
							roomNotification.getSensors().add( sensor );
						}
						floorNotification.getRooms().add( roomNotification );
						
					}
					
					listFloors.add( floorNotification );
					
				}
				Gson gson = new Gson();
				
				Parameter parameter = new Parameter();
				parameter.setValue( gson.toJson( location ));
				buildingNotification.setLocation(parameter);
				
				Building subject = new Building();
				subject.setId("iBuilding");
				subject.setValue( gson.toJson(listFloors));
				buildingNotification.setSubject(subject);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			
			Date timestamp = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			buildingNotification.setTimestamp(df.format(timestamp));

			Gson gson = new Gson();
			String json = gson.toJson(buildingNotification);
			
			fm.execute(bfm.getId(), "Publish", json);
		}
	}
		

}
