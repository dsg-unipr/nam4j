package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.mobility.peer.MccNamPeer;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.MinimumRequirements;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.SAXHandler;
import it.unipr.ce.dsg.nam4j.impl.service.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * <p>
 * This class includes utility methods and strings used by mobility actions.
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

public class MobilityUtils {
	
	/** The format identifier for JSON messages. */
	public static String JSON_MESSAGE_FORMAT = "application/json";
	
	/** The size in bytes of a chunk transmitted on the network */
	public static int CHUNK_SIZE = 1000;
	
	/** Error strings */
	public static final String ERROR_REQUEST_ITEM_ANSWER_MESSAGE = "RequestItemAnswerMessage message could not be generated";
	public static final String ERROR_REQUEST_COPY_MESSAGE = "RequestCopyMessage message could not be generated";
	public static final String ERROR_REQUEST_MIGRATE_MESSAGE = "RequestMigrateMessage message could not be generated";
	public static final String ERROR_NULL_STATE = "Received state is null";
	public static final String ERROR_GENERATING_STATE_CHUNKS = "An error occurred during state chunks generation";
	public static final String ERROR_PARSING_XML_FILE_FOR_DEPENDENCIES = "An error occurred while trying to parse the XML file to get the list of dependencies";
	public static final String ERROR_IN_RESOURCE_TYPE = "The type of the resource I have is different than the requested one";
	public static final String MISSING_XML_FILE = "The info file for the item is not available";
	
	/** General strings */
	public static final String DEPENDENCY_ADDED = "--- Added dependency to the class path";
	public static final String ADDING_ITEM_TO_CP_BEFORE_RECEIVING_STATE = "Adding the item to the classpath before receiving the state";
	public static final String CONTACTED_NODE_DOES_NOT_HAVE_ITEM = "Contacted peer does not have the requested item";
	public static final String RECEIVED_DEPENDENCY_CHUNK = "Received dependency chunk ";
	public static final String SERVER_OLD_ITEM_AVAILABLE = "An older version of the item is available; informing the client that the COPY is not possible";
	public static final String REQUESTED_ITEM_IS_AVAILABLE_SENDING_DEPENDENCIES = "The item is available (the same required version or a new one); sending the list of dependencies to the client";
	public static final String SENDING_DEPENDENCIES = "Sending dependencies for item ";
	public static final String SENDING_DEPENDENCY_CHUNK = "--- Sending dependency chunk ";
	public static final String CLIENT_ALL_DEPENDENCIES_AVAILABLE = "All dependencies are available";
	public static final String ALL_DEPENDENCIES_AVAILABLE = "The client has all dependencies";
	public static final String CLIENT_DEPENDENCY_AVAILABLE = "I already have dependency ";
	public static final String INFO_FILE_NOT_AVAILABLE = "The info file is not available; waiting to receive it...";
	public static final String SENDING_INFO_FILE = "Sending info file...";
	public static final String SENDING_INFO_FILE_CHUNK = "--- Sending info file chunk ";
	public static final String ITEM_NOT_AVAILABLE = "The item file is not available; waiting to receive it...";
	public static final String OLD_ITEM_AVAILABLE = "An older version of the item is available; waiting to receive the updated version...";
	public static final String INFO_FILE_AVAILABLE_ON_CLIENT = "The client has the info file";
	public static final String INFO_FILE_AVAILABLE = "The info file is available";
	public static final String ITEM_AVAILABLE_WAITING_FOR_STATE = "The item is already available (the same required version or a new one); adding it to the class path and waiting to receive the execution state...";
	public static final String ITEM_RECEIVED_WAITING_FOR_STATE = "The item has been received; adding it to the class path and waiting to receive the execution state...";
	public static final String SENDING_ITEM_CHUNK = "--- Sending item chunk ";
	public static final String SENDING_ITEM = "--- Sending item ";
	public static final String RECEIVED_ITEM_CHUNK = "Received item chunk ";
	public static final String SENDING_STATE = "Sending execution state...";
	public static final String SENDING_STATE_CHUNK = "--- Sending state chunk ";
	public static final String RECEIVED_STATE_CHUNK = "Received state chunk ";
	public static final String ITEM_RECEIVED = "The client received the item";
	public static final String STARTING_EXECUTION = "The item is available; starting execution...";
	public static final String ACTION_SUCCESSFUL = "action succesfully completed";
	public static final String SERVER_ITEM_NOT_AVAILABLE = "The requested item is not available";
	public static final String RECEIVED_INFO_FILE = "------ Received an info file for a dependency";
	public static final String RECEIVED_RESOURCE_FILE = "------ Received a resource file for a dependency";
	public static final String RECEIVED_FM_FILE = "------ Received a FM file for a dependency";
	public static final String RECEIVED_LIB_FILE = "------ Received a library file for a dependency";
	public static final String CREATING_FILE = "Creating file...";
	public static final String PATH_SEPARATOR = "/";
	public static final String REFUSING_MIGRATION_BECAUSE_REQUIREMENTS_ARE_NOT_MET = "Refusing migration because minimum requirements are not met";
	
	/** Error strings */
	public static final String INFO_FILE_DOES_NOT_INCLUDE_DESCRIPTION = "The info file does not include the item description";
	public static final String INFO_FILE_DOES_NOT_EXIST = "The info file does not exist";
	public static final String ITEM_FILE_DOES_NOT_EXIST = "The item file does not exist";
	public static final String UNSUPPORTED_CLASS_LOADER = "Class loader not supported";
	
	/** File extensions */
	public static final String INFO_FILE_EXTENSION = ".xml";
	public static final String DESKTOP_FILE_EXTENSION = ".jar";
	public static final String ANDROID_FILE_EXTENSION = ".dex";
	
	/** Identifiers */
	public static final String INFO_FILE_ID = "INFO";
	public static final String RESOURCE_FILE_ID = "RESOURCE";
	public static final String FM_ID = "FM";
	public static final String SERVICE_ID = "SERVICE";
	public static final String LIB_ID = "LIB";
	
	/**
	 * Method to check if current node has a given file.
	 * 
	 * @param requestedClassname
	 *            The name of a class in the file (used to identify the file
	 *            itself)
	 * 
	 * @param platform
	 *            The {@link NetworkedAutonomicMachine#Platform} of the
	 *            requesting node
	 * 
	 * @param migrationStore
	 *            The path where dependencies are stored
	 * 
	 * @return the requested file
	 */
	public static File getRequestedItem(String requestedClassname, Platform platform, String migrationStore) {
		// Get the list of all files in the migration repository
		String filename = "";
		File folder = new File(migrationStore);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			String ext = null;
			
			if (platform == Platform.DESKTOP) {
				ext = MobilityUtils.DESKTOP_FILE_EXTENSION;
			} else if (platform == Platform.ANDROID) {
				ext = MobilityUtils.ANDROID_FILE_EXTENSION;
			}
			
			if (ext == null)
				return null;
			
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(ext)) {
				filename = listOfFiles[i].getAbsolutePath();
				
				if (filename.contains(requestedClassname)) {
					// Found the item to be migrated: listOfFiles[i].getAbsolutePath()
					return new File(listOfFiles[i].getAbsolutePath());
				}
			}
		}
		return null;
	}

	/**
	 * Method to generate a list of chunks for a byte array representing
	 * execution state.
	 * 
	 * @param conversationId
	 *            The id of the conversation the state is about
	 * 
	 * @param chunkSize
	 *            The maximum size of each chunk
	 * 
	 * @param bObject
	 *            The byte array that has to be split
	 * 
	 * @return the list of chunks for a byte array
	 */
	public static ArrayList<StateChunk> generateStateChunksFromByteArray(String conversationId, byte[] bObject) {
		try {
			ArrayList<StateChunk> chunkList = new ArrayList<StateChunk>();
			double chunkNumber = Math.ceil((double) bObject.length / (double) MobilityUtils.CHUNK_SIZE);
	
			for (int chunkIndex = 0; chunkIndex < chunkNumber; chunkIndex++) {
				int length;
	
				if (chunkIndex + 1 < chunkNumber)
					length = MobilityUtils.CHUNK_SIZE;
				else
					length = bObject.length - chunkIndex * MobilityUtils.CHUNK_SIZE;
				
				byte chunkBuffer[] = new byte[length];
				System.arraycopy(bObject, chunkIndex * MobilityUtils.CHUNK_SIZE, chunkBuffer, 0, length);
				StateChunk newChunk = new StateChunk(conversationId, (int) chunkNumber, chunkIndex, chunkBuffer);
				chunkList.add(newChunk);
			}
	
			return chunkList;
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	/**
	 * Method to generate a list of chunks for a byte array representing a
	 * dependency file.
	 * 
	 * @param conversationId
	 *            The id of the conversation the state is about
	 * 
	 * @param chunkSize
	 *            The maximum size of each chunk
	 * 
	 * @param bObject
	 *            The byte array that has to be split
	 * 
	 * @param dependencyId
	 *            The id of the dependency
	 * 
	 * @param fileName
	 *            The name of the dependency file
	 * 
	 * @return the list of chunks for a byte array
	 */
	public static ArrayList<DependencyChunk> generateDependencyChunksFromByteArray(String conversationId, byte[] bObject, String dependencyId, String fileName) {
		try {
	
			ArrayList<DependencyChunk> chunkList = new ArrayList<DependencyChunk>();
			double chunkNumber = Math.ceil((double) bObject.length / (double) MobilityUtils.CHUNK_SIZE);
	
			for (int chunkIndex = 0; chunkIndex < chunkNumber; chunkIndex++) {
				int length;
	
				if (chunkIndex + 1 < chunkNumber)
					length = MobilityUtils.CHUNK_SIZE;
				else
					length = bObject.length - chunkIndex * MobilityUtils.CHUNK_SIZE;
				
				byte chunkBuffer[] = new byte[length];
				System.arraycopy(bObject, chunkIndex * MobilityUtils.CHUNK_SIZE, chunkBuffer, 0, length);
				DependencyChunk newChunk = new DependencyChunk(conversationId, dependencyId, fileName, (int) chunkNumber, chunkIndex, chunkBuffer);
				chunkList.add(newChunk);
			}
	
			return chunkList;
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	/**
	 * Method to generate a list of chunks for a byte array representing an item
	 * to be migrated.
	 * 
	 * @param conversationId
	 *            The id of the conversation the state is about
	 * 
	 * @param chunkSize
	 *            The maximum size of each chunk
	 * 
	 * @param bObject
	 *            The byte array that has to be split
	 * 
	 * @param mainClass
	 *            The main class of the item
	 * 
	 * @param functionalModuleIdForService
	 *            If the item is a {@link Service}, the parameter represents the
	 *            identifier of the {@link FunctionalModule} to which it has to
	 *            be bound; null otherwise
	 * 
	 * @param fileName
	 *            The name of the dependency file
	 * 
	 * @return the list of chunks for a byte array
	 */
	public static ArrayList<ItemChunk> generateItemChunksFromByteArray(String conversationId, byte[] bObject, String mainClass, String functionalModuleIdForService, String fileName) {
		try {
			ArrayList<ItemChunk> chunkList = new ArrayList<ItemChunk>();
			double chunkNumber = Math.ceil((double) bObject.length / (double) MobilityUtils.CHUNK_SIZE);
	
			for (int chunkIndex = 0; chunkIndex < chunkNumber; chunkIndex++) {
				int length;
	
				if (chunkIndex + 1 < chunkNumber)
					length = MobilityUtils.CHUNK_SIZE;
				else
					length = bObject.length - chunkIndex * MobilityUtils.CHUNK_SIZE;
				
				byte chunkBuffer[] = new byte[length];
				System.arraycopy(bObject, chunkIndex * MobilityUtils.CHUNK_SIZE, chunkBuffer, 0, length);
				ItemChunk newChunk = new ItemChunk(conversationId, (int) chunkNumber, chunkIndex, chunkBuffer, mainClass, functionalModuleIdForService, fileName);
				chunkList.add(newChunk);
			}
	
			return chunkList;
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	/**
	 * Method to generate a list of chunks for a byte array representing the
	 * info file.
	 * 
	 * @param conversationId
	 *            The id of the conversation the info file is about
	 * 
	 * @param chunkSize
	 *            The maximum size of each chunk
	 * 
	 * @param bObject
	 *            The byte array that has to be split
	 * 
	 * @param dependencyId
	 *            The id of the dependency
	 * 
	 * @param fileName
	 *            The name of the info file
	 * 
	 * @return the list of chunks for a byte array
	 */
	public static ArrayList<InfoFileChunk> generateInfoFileChunksFromByteArray(String conversationId, byte[] bObject, String infoFileId, String fileName) {
		try {
			ArrayList<InfoFileChunk> chunkList = new ArrayList<InfoFileChunk>();
			double chunkNumber = Math.ceil((double) bObject.length / (double) MobilityUtils.CHUNK_SIZE);
	
			for (int chunkIndex = 0; chunkIndex < chunkNumber; chunkIndex++) {
				int length;
	
				if (chunkIndex + 1 < chunkNumber)
					length = MobilityUtils.CHUNK_SIZE;
				else
					length = bObject.length - chunkIndex * MobilityUtils.CHUNK_SIZE;
				
				byte chunkBuffer[] = new byte[length];
				System.arraycopy(bObject, chunkIndex * MobilityUtils.CHUNK_SIZE, chunkBuffer, 0, length);
				InfoFileChunk newChunk = new InfoFileChunk(conversationId, infoFileId, fileName, (int) chunkNumber, chunkIndex, chunkBuffer);
				chunkList.add(newChunk);
			}
	
			return chunkList;
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
	}

	/**
	 * Method to dynamically add a file to the path and return an object of its
	 * main class.
	 * 
	 * @param nam
	 *            The {@link NetworkedAutonomicMachine} representing the system
	 * 
	 * @param fileToAddToClassPath
	 *            a String representing the name of the file received from the
	 *            server
	 * 
	 * @param completeClassName
	 *            a String representing the complete name of the file's main
	 *            class (null if the method does not have to instantiate any
	 *            class, but only to add the file to class path)
	 * 
	 * @param fType
	 *            the type of the class to be added - SERVICE or FM (Functional
	 *            Module)
	 * 
	 * @return an object the jar's main class
	 * 
	 * @throws IOException
	 */
	public static Object addToClassPath(NetworkedAutonomicMachine nam, String fileToAddToClassPath, String completeClassName, MigrationSubject fType) {
		System.out.println("Adding file " + fileToAddToClassPath + " to class path");
		
		Object obj = null;
	
		try {
			File f = new File(fileToAddToClassPath);
			URL u = f.toURI().toURL();
	
			if (nam.getPlatform() == Platform.DESKTOP) {
	
				// Adding to the class path on a desktop node
				URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
				Class<?> sysclass = URLClassLoader.class;
	
				try {
					Method method = sysclass.getDeclaredMethod("addURL", MccNamPeer.parameters);
					method.setAccessible(true);
					method.invoke(sysloader, new Object[] { u });
				} catch (Throwable t) {
					t.printStackTrace();
					throw new IOException("Error, could not add URL to system classloader");
				}
			} else if (nam.getPlatform() == Platform.ANDROID) {
				// Adding to the class path on Android nodes happens locally on the device
			}
	
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Check if it has to instantiate an object of the main class
		if (completeClassName != null) {
			if (nam.getPlatform() == Platform.DESKTOP) {
				
				try {
					if (fType == MigrationSubject.SERVICE && completeClassName != null) {
						Constructor<?> cs = ClassLoader.getSystemClassLoader().loadClass(completeClassName).getConstructor();
						obj = cs.newInstance();
					}
					if (fType == MigrationSubject.FM && completeClassName != null) {
						// FM's constructor takes as parameter the NAM to which the FM has to be added
						Constructor<?> cs = ClassLoader.getSystemClassLoader().loadClass(completeClassName).getConstructor(NetworkedAutonomicMachine.class);
						obj = cs.newInstance(nam);
						nam.addFunctionalModule((FunctionalModule) obj);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					System.err.println("Main class (" + e.getMessage() + ") not found in file");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
			} else
				System.out.println("CLIENT: Android platform");		
		}
	
		return obj;
	}
	
	/**
	 * Method to parse the XML file which describes a library.
	 * 
	 * @param itemId
	 *            The id of the xml file to be parsed, corresponding to its name
	 *            with no extension
	 * 
	 * @return an object representing the SAX events handler
	 */
	public static SAXHandler parseXMLFile(String itemId, NetworkedAutonomicMachine nam) {
		try {
			File infoFile = new File(nam.getMigrationStore() + itemId + INFO_FILE_EXTENSION);
			FileInputStream infoFis = new FileInputStream(infoFile);
			SAXParserFactory parserFactor = SAXParserFactory.newInstance();
			SAXParser parser = parserFactor.newSAXParser();
			SAXHandler handler = new SAXHandler();
			parser.parse(infoFis, handler);
			return handler;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println(MISSING_XML_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Method to add a {@link Service} to a {@link FunctionalModule}.
	 * 
	 * @param s
	 *            The {@link Service} that has to be added
	 * 
	 * @param fmId
	 *            The identifier of the {@link FunctionalModule} to which the
	 *            {@link Service} has to be added
	 * 
	 * @param nam
	 *            The {@link NetworkedAutonomicMachine} to which the
	 *            {@link FunctionalModule} is associated
	 * 
	 * @param fmCompleteMainClassName
	 *            The {@link FunctionalModule}'s main class name
	 * 
	 * @return the {@link FunctionalModule} to which the {@link Service} has
	 *         been added
	 */
	public static FunctionalModule addServiceToFm(Service s, String fmId, NetworkedAutonomicMachine nam, String fmCompleteMainClassName) {
		
		System.out.println("--------- The main class name for the FM to which the Service is associated is " + fmCompleteMainClassName + " - instantiating it and adding the Service to such a Functional Module...");
		
		try {
			Constructor<?> cs = ClassLoader.getSystemClassLoader().loadClass(fmCompleteMainClassName).getConstructor(NetworkedAutonomicMachine.class);
			Object fmObj = cs.newInstance(nam);
			
			FunctionalModule fm = (FunctionalModule) fmObj;
			fm.setNam(nam);
			fm.addProvidedService(s.getId(), s);

			return fm;
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Method to check whether available resources are enough to accept a
	 * mobility {@link Action} request. It applies to MIGRATE, OFFLOAD, GO
	 * actions.
	 * 
	 * @param action
	 *            The received mobility {@link Action} request
	 * 
	 * @param minimumRequirements
	 *            The minimum requirements specified in the info file for the
	 *            item to be migrated. Null if no requirement is specified in
	 *            such a file.
	 * 
	 * @return true if the {@link Action} is accepted, false otherwise
	 */
	public static boolean decideWhetherToAcceptMobilityRequest(Action action, MinimumRequirements minimumRequirements) {
		
		// TODO: check if available resources are enough for the migrated module's execution
		
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		
		// Amount of free memory available to the JVM
		long freeJvmMemory = Runtime.getRuntime().freeMemory();

		// Max amount of memory the JVM will attempt to use (returns Long.MAX_VALUE if there is no preset limit)
		long maxJvmMemory = Runtime.getRuntime().maxMemory();
		
		// Total memory currently available to the JVM (it may change over time depending on the environment)
		long totalJvmMemory = Runtime.getRuntime().totalMemory();
		
		System.out.println("Available processors (cores): " + availableProcessors);
		System.out.println("Free memory (bytes): " + freeJvmMemory);
		System.out.println("Maximum memory (bytes): " + (maxJvmMemory == Long.MAX_VALUE ? "no limit" : maxJvmMemory));
		System.out.println("Total memory available to JVM (bytes): " + totalJvmMemory);

		// Get a list of all file system roots and print info
		File[] roots = File.listRoots();
		for (File root : roots) {
			System.out.println("File system root: " + root.getAbsolutePath());
			System.out.println("Total space (bytes): " + root.getTotalSpace());
			System.out.println("Free space (bytes): " + root.getFreeSpace());
			System.out.println("Usable space (bytes): " + root.getUsableSpace());
		}
		
		if (minimumRequirements != null) {
			System.out.println("Minimum requirements are specified");
			
			System.out.println("--- Cores: " + minimumRequirements.getNumProcessors());
			System.out.println("--- Ram (MB): " + minimumRequirements.getRam());
			System.out.println("--- Clock (Hz): " + minimumRequirements.getClockFrequency());
			System.out.println("--- Disk space (Hz): " + minimumRequirements.getStorage());
			System.out.println("--- Network connection is requested: " + minimumRequirements.isNetworkRequested());
			System.out.println("--- Location sensor is requested: " + minimumRequirements.isLocationSensorRequested());
			System.out.println("--- Camera is requested: " + minimumRequirements.isCameraRequested());
			
			return true;
			
		} else {
			System.out.println("--- Minimum requirements are not specified");
			return true;
		}
	}

}
