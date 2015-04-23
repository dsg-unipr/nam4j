package it.unipr.ce.dsg.example.galileo.service;

import it.unipr.ce.dsg.examples.galileo.fm.SensorFunctionalModule;
import it.unipr.ce.dsg.examples.ontology.Building;
import it.unipr.ce.dsg.examples.ontology.Floor;
import it.unipr.ce.dsg.examples.ontology.Latitude;
import it.unipr.ce.dsg.examples.ontology.Location;
import it.unipr.ce.dsg.examples.ontology.Longitude;
import it.unipr.ce.dsg.examples.ontology.Room;
import it.unipr.ce.dsg.examples.ontology.Sensor;
import it.unipr.ce.dsg.examples.ontology.Temperature;
import it.unipr.ce.dsg.examples.ontology.TemperatureNotification;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.service.Parameter;
import it.unipr.ce.dsg.nam4j.impl.service.Service;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.google.gson.Gson;

public class ReadTemperatureSensorService extends Service {

	/*
	 * The data of the building where the sensor is placed and of the sensor
	 * itself
	 */
	private String[] args;
	private SensorFunctionalModule srfm;

	private String id = "ReadTemperatureSensorServiceId";
	private String name = "ReadTemperatureSensorService";

	/* The file containing the sensor value */
	private final String fileToRead = "/sys/bus/iio/devices/iio:device0/in_voltage0_raw";

	private boolean stopThread = false;

	/* The frequency, in ms, the sensor is read */
	private int temperaturePollingFrequency = 5000;

	public ReadTemperatureSensorService(String[] args,
			SensorFunctionalModule srfm) {
		super();

		this.args = args;
		this.srfm = srfm;

		Thread t = new Thread(new ReadTemperatureSensorServiceRunnable(),
				"ReadTemperatureSensorService thread");
		t.start();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void stopThread() {
		stopThread = true;
	}

	class ReadTemperatureSensorServiceRunnable implements Runnable {

		public ReadTemperatureSensorServiceRunnable() {

		}

		@Override
		public void run() {

			BufferedReader br = null;

			while (!stopThread) {

				try {
					Thread.sleep(temperaturePollingFrequency);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

/*				try {

					String sCurrentLine;
					
					FileReader fr = new FileReader(fileToRead);
					
					if (fr != null) {

						br = new BufferedReader(fr);
*/	
						/*
						 * Call publish service passing json message. Look into
						 * other functional modules, looking for requested service.
						 */
						Collection<FunctionalModule> c = srfm.getNam()
								.getFunctionalModules().values();
						Iterator<FunctionalModule> itr = c.iterator();
						String serviceName = null;
						FunctionalModule fm = null;
						FunctionalModule tempfm = null;
	
						while (itr.hasNext()) {
							tempfm = itr.next();
							if (tempfm.getName().equals(srfm.getName()))
								continue;
	
							Collection<IService> cc = tempfm.getProvidedServices()
									.values();
							Iterator<IService> itrr = cc.iterator();
	
							while (itrr.hasNext()) {
								serviceName = itrr.next().getName();
								if (serviceName.equals("Publish")) {
									fm = tempfm;
								}
							}
						}
/*	
						while ((sCurrentLine = br.readLine()) != null) {
							System.out.println("Current sensor value is: "
									+ sCurrentLine);
*/							
							TemperatureNotification tempNotif = new TemperatureNotification();
	
							Building building = new Building();
							building.setValue(args[3]);
	
							Floor floor = new Floor();
							floor.setValue(args[4]);
	
							Room room = new Room();
							room.setValue(args[5]);
	
							Sensor sensor = new Sensor();
							sensor.setValue(args[6]);
	
							Temperature temperature = new Temperature();
							temperature.setId("i21");
							// temperature.setValue(sCurrentLine);
							temperature.setValue("VIRTUAL_SENSOR_VALUE");
	
							Latitude latitude = new Latitude();
							latitude.setValue(args[7]);
	
							Longitude longitude = new Longitude();
							longitude.setValue(args[8]);
	
							Location location = new Location();
							location.setBuilding(building);
							location.setFloor(floor);
							location.setRoom(room);
							location.setSensor(sensor);
							location.setLatitude(latitude);
							location.setLongitude(longitude);
	
							Gson gson = new Gson();
	
							Parameter parameter = new Parameter();
							parameter.setValue(gson.toJson(location));
							tempNotif.setLocation(parameter);
	
							tempNotif.setSubject(temperature);
							Date timestamp = new Date();
							DateFormat df = new SimpleDateFormat(
									"dd/MM/yyyy HH:mm:ss");
							tempNotif.setTimestamp(df.format(timestamp));
	
							String json = gson.toJson(tempNotif);
	
							/*
							 * fm is a ChordFunctionalModule and its "execute"
							 * method takes, as second parameter, the requested
							 * service which can be Join, Leave, Lookup, Publish,
							 * Subscribe.
							 */
							fm.execute(srfm.getId(), "Publish", json);
						}
/*					}
					
					fr.close();
				}

				catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}*/
		}
	}

}
