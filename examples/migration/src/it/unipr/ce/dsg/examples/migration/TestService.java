package it.unipr.ce.dsg.examples.migration;

import it.unipr.ce.dsg.nam4j.impl.service.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p>
 * This class represents an example of a service with a thread providing methods
 * to get started, suspended, resumed and stopped.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class TestService extends Service implements Serializable {

	private static final long serialVersionUID = -2513137456274951312L;
	private ServiceRunnableImplementation serviceRunnableImplementation = null;
	private Service service; // Reference to the Service used by the Runnable

	public TestService() {
		super();
		this.setId("TestService");
		this.setName("TestService");
		service = this;
		serviceRunnableImplementation = new ServiceRunnableImplementation();
	}
	
	@Override
	public ServiceRunnableImplementation getServiceRunnable() {
		return serviceRunnableImplementation;
	}

	public class ServiceRunnableImplementation extends ServiceRunnable {
		private static final long serialVersionUID = -1764242477267769580L;
		private int sleepingTime = 1000;
		private File file = null;
		private String filePath = null;
		private String fileName = "divina_commedia.txt";
		private String inputLine = null;
		private TreeMap<String, Integer> map = new TreeMap<String, Integer>();
		private int parsedLines = 0;
		
		// BufferedReader and FileReader are not serializable and therefore have
		// to be declared as transient
		transient private BufferedReader bufferedReader = null;
		transient private FileReader fileReader = null;
		
		/** Class constructor. */
		public ServiceRunnableImplementation() {}
		
		// No need to save non-serializable attributes in that the number of
		// parsed lines is serializable and is used by restoreState method to
		// move the cursor to the next line
		@Override
		public void saveState() {}
		
		@Override
		public void restoreState() {
			
			filePath = service.getFunctionalModule().getNam().getMigrationStore();
			
			if (filePath != null) {
				try {
					// Restore the file and buffer readers that could not get serialized
					file = new File(filePath + fileName);

					try {
			        	fileReader = new FileReader(file);
						bufferedReader = new BufferedReader(fileReader);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					
					// A counter is used to skip the already scanned lines
					int j = 0;
					
					while ((inputLine = bufferedReader.readLine()) != null && ++j < parsedLines);
	
					System.out.println("State restored");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else System.err.println("The file path retrieved from the associated FM is null");
		}
		
		public int getSleepingTime() {
			return sleepingTime;
		}

		public void setSleepingTime(int sleepingTime) {
			this.sleepingTime = sleepingTime;
		}

		public void run() {
			
			System.out.println("I am " + service.getId() + " ; I am associated to " + service.getFunctionalModule().getId() + " FM which is associated to " + service.getFunctionalModule().getNam().getId() + " NAM ---");
			
			if(fileReader == null || bufferedReader == null) {
				filePath = service.getFunctionalModule().getNam().getMigrationStore();
				if (filePath != null) {
					try {
						file = new File(filePath + fileName);
						if (file.exists()) {
							fileReader = new FileReader(file);
							bufferedReader = new BufferedReader(fileReader);
						} else System.out.println("The file to be parsed (" + (filePath + fileName) + " does not exist");
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				} else System.err.println("The file path retrieved from the associated FM is null");
			}
			
			try {
	            while ((inputLine = bufferedReader.readLine()) != null) {
	            
	            	System.out.print("Examining line " + ++parsedLines + ": \"" + inputLine + "\"\r");
	            	
	            	String[] words = inputLine.split("[ \n\t\r.,;:!?(){}]");
	 
	                for (int counter = 0; counter < words.length; counter++) {
	                    String key = words[counter].toLowerCase(); // remove .toLowerCase for Case Sensitive result
						if (key.length() > 0) {
							if (map.get(key) == null) {
								map.put(key, 1);
							} else {
								int value = map.get(key).intValue();
								value++;
								map.put(key, value);
							}
						}
					}
	                
	                synchronized (this) {
						while (isSuspended()) {
							wait();
						}
					}
	            }
	            
	            bufferedReader.close();
	            
	            Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
	            System.out.println("--- The five most used words ---\n\n" + "Word" + "\t\t" + "number of occurances");
	            int i = 1;
	            for (Map.Entry<String, Integer> entry : entrySet) {
	                System.out.println(entry.getKey() + "\t\t" + entry.getValue());
	                if ((i++) == 5)
	                	break;
	            }
			} catch (InterruptedException e) {
				 System.out.println("Thread interrupted.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
