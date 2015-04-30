package it.unipr.ce.dsg.namdroid.utils;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.service.Service;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

/**
 * <p>
 * This class includes utility methods used by mobility actions targeting
 * Android nodes.
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

public class MobilityUtilsAndroid extends MobilityUtils {
	
	/** The name of the directory where optimized dex files are created */
	private static final String OPTIMIZED_DEX_OUTPUT_PATH = "outdex";
	
	// ****************** Methods which add dex files to class path at runtime ******************
    
	/**
	 * Method to set the elements in a class loader.
	 * 
	 * @param classLoader
	 *            The class loader of interest
	 * 
	 * @param elements
	 *            The elements to be set as content of the class loader
	 * 
	 * @throws Exception
	 */
    private static void setDexClassLoader(BaseDexClassLoader classLoader, Object elements) throws Exception {
        Class<BaseDexClassLoader> dexClassLoader = BaseDexClassLoader.class;
        Field pathListField = dexClassLoader.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(classLoader);
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        dexElementsField.set(pathList, elements);
    }
 
	/**
	 * Method to get the list of elements in a class loader.
	 * 
	 * @param classLoader
	 *            The class loader of interest
	 *            
	 * @return an object containing the list of elements in a class loader
	 * 
	 * @throws Exception
	 */
    private static Object getDexClassLoader(BaseDexClassLoader classLoader) throws Exception {
        Class<BaseDexClassLoader> dexClassLoader = BaseDexClassLoader.class;
        Field pathListField = dexClassLoader.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(classLoader);
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        Object dexElements = dexElementsField.get(pathList);
        return dexElements;
    }
    
	/**
	 * Method to merge two arrays.
	 * 
	 * @param firstObject
	 *            The first array to be merged
	 * 
	 * @param secondObject
	 *            The second array to be merged
	 * 
	 * @return an object containing the elements of the merged arrays
	 */
    private static Object joinArrays(Object firstObject, Object secondObject) {
        Class<?> o1Type = firstObject.getClass().getComponentType();
        Class<?> o2Type = secondObject.getClass().getComponentType();
 
        if(o1Type != o2Type)
            throw new IllegalArgumentException();
 
        int firstObjectSize = Array.getLength(firstObject);
        int secondObjectSize = Array.getLength(secondObject);
        Object array = Array.newInstance(o1Type, firstObjectSize + secondObjectSize);
 
        int offset = 0, i;
        for(i = 0; i < firstObjectSize; i++, offset++)
            Array.set(array, offset, Array.get(firstObject, i));
        for(i = 0; i < secondObjectSize; i++, offset++)
            Array.set(array, offset, Array.get(secondObject, i));
 
        return array;
    }
    
    // ****************** End of methods which add dex files to class path at runtime ******************
    
	/**
	 * Method to dynamically add a file to the path and return an object of its
	 * main class. Such a method is the Android implementation of
	 * {@link MobilityUtils#addToClassPath(NetworkedAutonomicMachine, String, String, MigrationSubject)}
	 * method used by non-Android nodes.
	 * 
	 * @param mContext
	 *            The application context
	 * 
	 * @param nam
	 *            The {@link NetworkedAutonomicMachine} representing the system
	 * 
	 * @param itemName
	 *            The full name (including its path) of the dex file to be added
	 *            to the class loader
	 * 
	 * @param mainClassName
	 *            The main class name of the dex
	 * 
	 * @param role
	 *            The {@link MigrationSubject} of the dex
	 * 
	 * @param action
	 *            The mobility {@link Action}
	 * 
	 * @return an object of the dex's main class
	 */
	public static Object addToClassPath(Context mContext, NetworkedAutonomicMachine nam, String itemName, String mainClassName, MigrationSubject role, Action action) {
		File f = new File(itemName);
		Object obj = null;
		
		if(f.exists()) {
			System.out.println("Dex file " + f.getAbsolutePath() + " is available");

			final File optimizedDexOutputPath = mContext.getDir(MobilityUtilsAndroid.OPTIMIZED_DEX_OUTPUT_PATH, 0);

			// Get system class loader
			ClassLoader localClassLoader = mContext.getClassLoader();
			
			// Defining a new class loader for the dex file to be added
			BaseDexClassLoader classLoader = new DexClassLoader(
					f.getAbsolutePath(),
					optimizedDexOutputPath.getAbsolutePath(), null,
					localClassLoader);

			if (localClassLoader instanceof BaseDexClassLoader) {
				try {
					// Getting all elements in system class loader
					Object existing = getDexClassLoader((BaseDexClassLoader) localClassLoader);
					
					// Getting the element in the new class loader (the dex to be added)
					Object incoming = getDexClassLoader(classLoader);
					
					// Merging the system class loader elements with the one in the new class loader
					Object joined = joinArrays(incoming, existing);

					// Setting the merging result as the set of elements in the system class loader
					setDexClassLoader((BaseDexClassLoader) localClassLoader, joined);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				throw new UnsupportedOperationException(MobilityUtils.UNSUPPORTED_CLASS_LOADER);
			}
			
			if(mainClassName != null && role != null && action != null) {
				try {
					Class<?> myClass = classLoader.loadClass(mainClassName);
					Constructor<?> cs;
					
					if (role.equals(MigrationSubject.FM)) {
						if(action == Action.COPY) {
							// FM's constructor takes as parameter the NAM to which the FM has to be added
							cs = myClass.getConstructor(NetworkedAutonomicMachine.class);
							obj = (Object) cs.newInstance(nam);
						}
					}
					else if (role.equals(MigrationSubject.SERVICE)) {
						// TODO: get the associated FM from the conversationItem
						// String functionalModuleId = handler.getLibraryInformation().getFunctionalModule();
						
						// System.out.println("------ Functional module id = " + functionalModuleId);
						
						// TODO: the conversationItem must be update on the class that owns it
						// conversationItem.setFunctionalModuleId(functionalModuleId);
						
						// TODO: add service to functional module
						
						if(action == Action.COPY) {
							// Service's constructor takes as parameter the FM to which the Service is associated
							cs = myClass.getConstructor();
							obj = (Object)cs.newInstance();
						}
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		} else System.err.println("File " + f.getAbsolutePath() + " does not exist on the SD Card");
		
		return obj;
	}
	
	/**
	 * Method to add a {@link Service} to a {@link FunctionalModule}.
	 * 
	 * @param mContext
	 *            The application context
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
	public static FunctionalModule addServiceToFm(Context mContext, Service s, String fmId, NetworkedAutonomicMachine nam, String fmCompleteMainClassName) {
		
		try {
			ClassLoader classLoader = mContext.getClassLoader();
			Class<?> myClass = classLoader.loadClass(fmCompleteMainClassName);
			Constructor<?> cs = myClass.getConstructor(NetworkedAutonomicMachine.class);
			Object fmObj = cs.newInstance(nam);
			FunctionalModule fm = (FunctionalModule) fmObj;
			fm.setNam(nam);
			fm.addProvidedService(s.getId(), s);
			return fm;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
