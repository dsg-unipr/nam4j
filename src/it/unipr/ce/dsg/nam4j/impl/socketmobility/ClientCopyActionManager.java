package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.logger.NamLogger;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.service.Service;
import it.unipr.ce.dsg.nam4j.interfaces.IMigrationListener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * <p>
 * Class which implements the client-side management of the COPY mobility
 * action.
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
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */
public class ClientCopyActionManager implements Runnable {

	NetworkedAutonomicMachine nam = null;
	String requiredFmClass;
	String[] requiredServiceClass;
	String[] requiredServiceId;
	Action action;
	Platform clientType;
	
	/** The logger object */
	private NamLogger messageLogger;

	/**
	 * The descriptor of the object to be migrated.
	 */
	BundleDescriptor bundleDescriptor;

	/**
	 * An array of classes used for reflection.
	 */
	private static final Class<?>[] parameters = new Class[] { URL.class };
	
	// OBSERVER pattern implementation
	private ArrayList<IMigrationListener> listeners;

	public ClientCopyActionManager(NetworkedAutonomicMachine nam,
			String requiredFmClass, String[] requiredServiceClass,
			String[] requiredServiceId, Platform platform, Action action) {

		this.nam = nam;
		this.requiredFmClass = requiredFmClass;
		this.requiredServiceClass = requiredServiceClass;
		this.requiredServiceId = requiredServiceId;
		this.action = action;
		this.clientType = platform;
		this.listeners = new ArrayList<IMigrationListener>();
		
		messageLogger = new NamLogger("ClientCopyActionManager");
	}
	
	/**
	 * Method to subscribe to received items notifications.
	 * 
	 * @param ml
	 *            The object that wants to subscribe
	 */
	public void addMigrationListener(IMigrationListener ml) {
		this.listeners.add(ml);
	}

	/**
	 * Returns an object of the main class of the file dynamically added to the
	 * path.
	 * 
	 * @param cName
	 *            complete class name of the functional module, or the service,
	 *            added to the class path
	 *            
	 * @param cType
	 *            the type of the class to be added - SERVICE or FM (Functional
	 *            Module)
	 *            
	 * @return an object of the class added to the path
	 */
	private Object getItemFromFile(String cName, MigrationSubject cType) {

		Object obj = null;

		try {

			if (cType == MigrationSubject.SERVICE) {
				Class<?> c = ClassLoader.getSystemClassLoader()
						.loadClass(cName);
				obj = c.newInstance();
			}

			if (cType == MigrationSubject.FM) {
				Constructor<?> cs;
				cs = ClassLoader.getSystemClassLoader().loadClass(cName)
						.getConstructor(NetworkedAutonomicMachine.class);
				obj = cs.newInstance(nam);
				nam.addFunctionalModule((FunctionalModule) obj);
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * Dynamically adds a file to the path and returns an object of its main
	 * class.
	 * 
	 * @param receivedFilename
	 *            a String representing the name of the file received from the
	 *            server
	 *            
	 * @param completeClassName
	 *            a String representing the complete name of the file main class
	 *            
	 * @param fType
	 *            the type of the class to be added - SERVICE or FM (Functional
	 *            Module)
	 *            
	 * @throws IOException
	 */
	private Object addToClassPath(String receivedFilename,
			String completeClassName, MigrationSubject fType) {

		Object obj = null;

		try {
			File f = new File(receivedFilename);
			URL u = f.toURI().toURL();

			if (nam.getClientPlatform(0) == Platform.DESKTOP) {

				// Adding to the class path on a desktop node
				URLClassLoader sysloader = (URLClassLoader) ClassLoader
						.getSystemClassLoader();

				Class<?> sysclass = URLClassLoader.class;

				try {
					Method method = sysclass.getDeclaredMethod("addURL",
							parameters);
					method.setAccessible(true);
					method.invoke(sysloader, new Object[] { u });
				} catch (Throwable t) {
					t.printStackTrace();
					throw new IOException(
							"Error, could not add URL to system classloader");
				}
			} else {
				// Adding to the class path on Android nodes happens locally on the device
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (nam.getClientPlatform(0) == Platform.DESKTOP) {
			obj = getItemFromFile(completeClassName, fType);
		} else
			messageLogger.debug("CLIENT: Android platform");

		return obj;

	}

	/**
	 * Returns a reference to the required Functional Module or Service.
	 * 
	 * @param fName
	 *            the name of the functional module or of the service to be migrated
	 *            
	 * @param clientType
	 *            (ANDROID or DESKTOP)
	 *            
	 * @param fType
	 *            a String representing the type of the item to migrate -
	 *            SERVICE or FM (Functional Module)
	 *            
	 * @param a
	 *            the mobility action
	 * */
	private Object findRemoteItem(String fName, Platform clientType,
			MigrationSubject fType, Action a) {

		nam.setClientPlatform(clientType, 0);

		Socket s = null;

		BufferedReader is = null;
		PrintStream os = null;

		Object obj = null;

		int bytesRead;
		int current = 0;
		int filesize = 6022386; // Temporary hardcoded filesize

		boolean connected = false;

		// The client tries 3 times to connect to server
		for (int j = 0; j < nam.getTrialsNumber(); j++) {
			try {

				s = new Socket(nam.getServerAddress(), nam.getServerPort());

				is = new BufferedReader(new InputStreamReader(
						s.getInputStream()));

				os = new PrintStream(new BufferedOutputStream(
						s.getOutputStream()));

				connected = true;

				break;

			} catch (IOException e) {
				if (j < 2)
					System.out
							.println("CLIENT: connection failed. Trying again...");
				else
					messageLogger.debug("CLIENT: connection failed");
			}
		}

		if (connected) {

			messageLogger.debug("CLIENT: created socket " + s);

			// Sending the mobility action
			os.println(a);
			os.flush();

			// To send the platform enum on the socket it is converted to a
			// String
			os.println(clientType.name());
			os.flush();

			os.println(fType);
			os.flush();

			os.println(fName);
			os.flush();

			messageLogger.debug("CLIENT: waiting for " + fType
					+ " descriptor...");

			String fileNameAndExt = "";
			String className = "";
			String completeClassName = "";

			try {

				InputStream inputS = s.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(inputS);
				bundleDescriptor = (BundleDescriptor) ois.readObject();
				fileNameAndExt = bundleDescriptor.getFileName();
				className = bundleDescriptor.getMainClassName();
				completeClassName = bundleDescriptor.getCompleteName();

				String fileName = fileNameAndExt.split("\\.")[0];
				String ext = fileNameAndExt.split("\\.")[1];

				if (!fileNameAndExt.equalsIgnoreCase("notAvailable")) {

					messageLogger.debug("CLIENT: file name received from server = "	+ fileNameAndExt);
					messageLogger.debug("CLIENT: main class name received from server = " + className);
					messageLogger.debug("CLIENT: complete class name received from server = " + completeClassName);
					messageLogger.debug("CLIENT: waiting for " + fType + " file " + fileNameAndExt);

					// Writing for the received file

					byte[] mybytearray = new byte[filesize];
					InputStream inputStr = s.getInputStream();

					String receivedFilename = nam.getMigrationStore() + MobilityUtils.PATH_SEPARATOR + fileName + "." + ext;

					FileOutputStream fos = new FileOutputStream(
							receivedFilename);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					bytesRead = inputStr.read(mybytearray, 0,
							mybytearray.length);
					current = bytesRead;

					bos.write(mybytearray, 0, current);
					bos.flush();
					bos.close();
					fos.close();

					// End of writing

					is.close();
					os.close();
					s.close();

					s.close();

					File f = new File(receivedFilename);
					if (f.exists()) {
						messageLogger.debug("CLIENT: " + f.toURI().toURL() + " received");

						obj = addToClassPath(receivedFilename,
								completeClassName, fType);
						
						// Interested objects get notified that an item has been received
						for (IMigrationListener ml : this.listeners) {
							ml.onReceivedItem(receivedFilename, completeClassName, fType);
						}

						// Adding the FM and Service names and sender's address
						// to the HashMap
						if (fType == MigrationSubject.FM) {

							nam.addFmSender(s.getRemoteSocketAddress()
									.toString(), fileNameAndExt);

						} else if (fType == MigrationSubject.SERVICE) {

							nam.addServiceSender(s.getRemoteSocketAddress()
									.toString(), fileNameAndExt);
						}

					} else {
						messageLogger.debug("CLIENT: file not received");
					}

				}
			} catch (IOException e) {
				messageLogger.debug(e.getMessage());
			} catch (Exception e) {
				messageLogger.debug(e.getMessage());
			}

		} else {
			messageLogger.debug("CLIENT: could not connect to server");
		}

		return obj;
	}

	@Override
	public void run() {

		messageLogger.debug("CLIENT: requesting FM including class "
				+ requiredFmClass);

		FunctionalModule fm = (FunctionalModule) findRemoteItem(
				requiredFmClass, clientType, MigrationSubject.FM, action);

		// Adding the FM to the NAM FM HashMap on a DESKTOP node
		if (fm != null)
			nam.addFunctionalModule(fm);

		// Check if the client asked for one or more services of the FM to get
		// copied
		if (requiredServiceClass != null) {

			for (int g = 0; g < requiredServiceClass.length; g++) {

				String currentServiceClassName = requiredServiceClass[g];
				String currentServiceId = requiredServiceId[g];

				if (currentServiceClassName != null && currentServiceId != null) {

					messageLogger.debug("CLIENT: requesting Service including class " + requiredServiceClass);

					// Obtaining the Service
					Service serv = (Service) findRemoteItem(
							currentServiceClassName, clientType,
							MigrationSubject.SERVICE, action);

					// Adding the Service to the FM
					if (serv != null)
						if (fm != null)
							fm.addProvidedService(currentServiceId, serv);
					else
						messageLogger.error("CLIENT: Error in service migration");
				}
			}

		}
	}

}
