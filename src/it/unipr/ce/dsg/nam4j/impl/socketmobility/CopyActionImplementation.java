package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.logger.NamLogger;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;

/**
 * 
 * Class which implements the server-side management of the COPY mobility action.
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 * This file is part of nam4j.
 *
 * nam4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nam4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nam4j. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
public class CopyActionImplementation extends CopyActionHandler {

	NetworkedAutonomicMachine nam = null;
	BufferedReader is;
	OutputStream os;
	String receiver;

	ObjectOutputStream oos;

	/** The descriptor of the object to be migrated */
	BundleDescriptor bundleDescriptor;
	
	/** The logger object */
	private NamLogger messageLogger;

	public CopyActionImplementation(NetworkedAutonomicMachine nam,
			BufferedReader is, OutputStream os, String receiver) {
		this.nam = nam;
		this.is = is;
		this.os = os;
		this.receiver = receiver;
		
		messageLogger = new NamLogger("CopyActionImplementation");

		messageLogger.debug("SERVER: starting COPY action...");
	}

	/**
	 * Method to copy a Functional Method on a remote NAM.
	 * 
	 * @param requestedClassname
	 *            The name of the class requested from the client
	 */
	private void fmMobility(String requestedClassname) {

		try {
			// Get the list of all files
			String filename = "";
			File folder = new File(nam.getMigrationStore());
			File[] listOfFiles = folder.listFiles();
			
			// Set to true when the file to be migrated is found
			boolean found = false;

			messageLogger.debug("SERVER: thread "
					+ Thread.currentThread().getId()
					+ " received a FM request for class \""
					+ requestedClassname + "\"");

			// Data of the file to be migrated
			String className = requestedClassname;
			String completeName = null;
			String fileToBeMigrated = "";
			File file = null;

			for (int i = 0; i < listOfFiles.length; i++) {

				if (listOfFiles[i].isFile()
						&& listOfFiles[i].getName().endsWith(".jar")) {
					filename = listOfFiles[i].getAbsolutePath();

					messageLogger.debug("SERVER: thread "
							+ Thread.currentThread().getId()
							+ " is checking inside file " + filename);

					JarFile jarFile = new JarFile(filename, false);

					Enumeration<JarEntry> entries = jarFile.entries();

					while (entries.hasMoreElements()) {

						JarEntry entry = entries.nextElement();
						String entryName = entry.getName();

						if (entryName.endsWith(".class")) {

							String[] currentClassName = entryName.split(MobilityUtils.PATH_SEPARATOR);
							String justClassName = currentClassName[currentClassName.length - 1]
									.replace(".class", "");
							
							if (requestedClassname
									.equalsIgnoreCase(justClassName)) {

								/*
								 * listOfFiles[i] is the file to be migrated
								 * 
								 * Using BCEL class parser to get the package
								 * name for the class: a temporary copy of the
								 * class file is created locally and then parsed
								 * to get the package
								 */

								/*
								 * Generating a pseudo-random name for the temp
								 * file
								 */
								int fileNameLength = 20;
								char[] chars = "abcdefghijklmnopqrstuvwxyz"
										.toCharArray();
								StringBuilder sb = new StringBuilder();
								Random random = new Random();
								for (int y = 0; y < fileNameLength; y++) {
									char c = chars[random.nextInt(chars.length)];
									sb.append(c);
								}
								String output = sb.toString() + ".class";

								/* Creating the temp file */
								File f = new File(output);

								/*
								 * Copying the content of the class in the new
								 * file
								 */
								InputStream inputS = jarFile
										.getInputStream(entry);
								java.io.FileOutputStream fos = new FileOutputStream(
										f);
								while (inputS.available() > 0) {
									fos.write(inputS.read());
								}
								fos.close();
								inputS.close();

								if (f.exists()) {

									messageLogger.debug("SERVER: thread "
											+ Thread.currentThread().getId()
											+ " has found requested file: \""
											+ filename + "\"");

									found = true;

									fileToBeMigrated = listOfFiles[i]
											.getAbsolutePath();

									// Parsing the class to get its
									// package name
									ClassParser parser = new ClassParser(
											f.getAbsolutePath());
									completeName = parser.parse()
											.getPackageName() + "." + className;

									// Deleting the copy of the class
									boolean success = f.delete();
									if (!success)
										throw new IllegalArgumentException(
												"Delete: deletion failed");

									break;
								}
							}
						}
					}
					
					jarFile.close();
				}

				if (found)
					break;
			}

			if (found) {

				if (nam.getClientPlatform(0) == Platform.DESKTOP)
					file = new File(fileToBeMigrated);
				else
					file = new File(fileToBeMigrated.replace(".jar", ".dex"));

				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending file name = " + file.getName());
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending FM main class name = " + className);
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending FM main class complete name = "
						+ completeName);

				bundleDescriptor = new BundleDescriptor(file.getName(),
						className, completeName);
				oos.writeObject(bundleDescriptor);

				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending file...");

				byte[] myBytearray = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(myBytearray, 0, myBytearray.length);
				os.write(myBytearray, 0, myBytearray.length);
				os.flush();

				nam.addFmReceiver(receiver, fileToBeMigrated);
				
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " has finished sending");

				bis.close();

			} else {
				System.out
						.println("SERVER: thread "
								+ Thread.currentThread().getId()
								+ " could not send requested FM since it is not available on the node");

				// Informing the client that the FM could not be found
				bundleDescriptor = new BundleDescriptor("notAvailable", "", "");
				oos.writeObject(bundleDescriptor);
			}
		} catch (IOException e) {
			messageLogger.error(e.getMessage());
		}
	}

	/**
	 * Method to copy a Service on a remote NAM.
	 * 
	 * @param line
	 * @param cs
	 */
	private void serviceMobility(String line) {

		try {
			// Get the list of all files
			String filename = "";
			File folder = new File(nam.getMigrationStore());
			File[] listOfFiles = folder.listFiles();
			
			// The boolean is set to true if found the file to be migrated
			boolean found = false;

			messageLogger.debug("SERVER: thread "
					+ Thread.currentThread().getId()
					+ " received a service request from a client");

			messageLogger.debug("SERVER: thread "
					+ Thread.currentThread().getId()
					+ " received a request for \"" + line + "\"");

			// Data of the file to be migrated
			String className = line;
			String completeName = null;
			String fileToBeMigrated = "";
			File file = null;

			for (int i = 0; i < listOfFiles.length; i++) {

				if (listOfFiles[i].isFile()
						&& listOfFiles[i].getName().endsWith(".java")) {
					filename = listOfFiles[i].getAbsolutePath();

					messageLogger.debug("SERVER: thread "
							+ Thread.currentThread().getId()
							+ " is checking file " + filename);

					String[] currentClassName = filename.split(MobilityUtils.PATH_SEPARATOR);
					String justClassName = currentClassName[currentClassName.length - 1]
							.replace(".java", "");

					/*
					 * Since NAME.java file contains NAME class, if the name
					 * of the current file is equal to the required service's
					 * name, then current file is the one to be sent.
					 */
					if (line.equalsIgnoreCase(justClassName)) {
						fileToBeMigrated = listOfFiles[i].getAbsolutePath();

						FileInputStream fis = new FileInputStream(
								listOfFiles[i]);

						String fileContent = "";
						int oneByte;
						while ((oneByte = fis.read()) != -1) {
							char l = (char) oneByte;
							fileContent += l;
						}
						System.out.flush();
						fis.close();

						Pattern p = Pattern.compile("package.*;");
						Matcher m = p.matcher(fileContent);
						if (m.find()) {
							int occurrenceStart = m.start();
							int occurrenceEnd = m.end();

							completeName = fileContent
									.substring(occurrenceStart, occurrenceEnd)
									.replace("package ", "").replace(";", "")
									+ "." + className;

							found = true;
						}

						break;
					}
				}
			}

			if (found) {

				if (nam.getClientPlatform(0) == Platform.DESKTOP)
					file = new File(fileToBeMigrated);
				else
					file = new File(fileToBeMigrated.replace(".java", ".dex"));

				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending file name = " + file.getName());
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending service class name = " + className);
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending service class complete name = "
						+ completeName);

				bundleDescriptor = new BundleDescriptor(file.getName(),
						className, completeName);
				oos.writeObject(bundleDescriptor);

				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " is sending file...");

				byte[] myBytearray = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(myBytearray, 0, myBytearray.length);
				os.write(myBytearray, 0, myBytearray.length);
				os.flush();
				
				nam.addServiceReceiver(receiver, fileToBeMigrated);
				
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " has finished sending");

				bis.close();

			} else {
				System.out
						.println("SERVER: thread "
								+ Thread.currentThread().getId()
								+ " could not send requested service since it is not available on the node");

				// Informing the client that the FM could not be found
				bundleDescriptor = new BundleDescriptor("notAvailable", "", "");
				oos.writeObject(bundleDescriptor);

			}
		} catch (IOException e) {
			messageLogger.error(e.getMessage());
		}
	}

	public void run() {

		messageLogger.debug("SERVER: thread " + Thread.currentThread().getId()
				+ " accepted connection from " + os);

		try {

			// Setting the output stream for the socket
			oos = new ObjectOutputStream(os);

			String line;
			line = new String(is.readLine());

			/*
			 * To send the platform enum on the socket, the client converted it
			 * to a String so it is necessary to convert it back to enum when
			 * passing it to the setClientPlatform function.
			 */
			nam.setClientPlatform(Platform.valueOf(line), 0);

			if (nam.getClientPlatform(0) == Platform.DESKTOP)
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " connected to a desktop node");
			else
				messageLogger.debug("SERVER: thread "
						+ Thread.currentThread().getId()
						+ " connected to an Android node");

			line = new String(is.readLine());

			messageLogger.debug("SERVER: thread "
					+ Thread.currentThread().getId()
					+ " received a message from client = \"" + line + "\"");

			if (line.equalsIgnoreCase("FM")) {
				line = new String(is.readLine());
				fmMobility(line);
			}

			else if (line.equalsIgnoreCase("SERVICE")) {
				line = new String(is.readLine());
				serviceMobility(line);
			}

			is.close();
			oos.close();
			os.close();

		} catch (Exception e) {
			messageLogger.error("SERVER: error: " + e + " for thread "
					+ Thread.currentThread().getId());
		}
	}

}
