package it.unipr.ce.dsg.nam4j.impl;

import it.unipr.ce.dsg.nam4j.impl.resource.ResourceDescriptor;
import it.unipr.ce.dsg.nam4j.impl.socketmobility.ClientCopyActionManager;
import it.unipr.ce.dsg.nam4j.impl.socketmobility.ServerMobilityActionManager;
import it.unipr.ce.dsg.nam4j.interfaces.IMigrationListener;
import it.unipr.ce.dsg.nam4j.interfaces.INetworkedAutonomicMachine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * This is the main class of nam4j.
 * </p>
 * 
 * <p>
 * This file is part of nam4j.
 * </p>
 * 
 * <p>
 * nam4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * 
 * <p>
 * nam4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * </p>
 * 
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with nam4j. If not, see <http://www.gnu.org/licenses/>.
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * @author Marco Muro
 * 
 */

public abstract class NetworkedAutonomicMachine implements
		INetworkedAutonomicMachine {

	/**
	 * A String identifying the NAM.
	 */
	String id = "networkedAutonomicMachine";

	/**
	 * A String representing the name of the NAM.
	 */
	String name = "Networked Autonomic Machine";

	/**
	 * The client platform.
	 */
	public enum Platform {
		DESKTOP, ANDROID;
		
		public static Platform toPlatform(String s) {
			if (s.equals("DESKTOP"))
				return DESKTOP;
			else if (s.equals("ANDROID"))
				return ANDROID;
			else throw new IllegalArgumentException();
		}
		
		@Override
		public String toString() {
			switch (this) {
				case DESKTOP: return "DESKTOP";
				case ANDROID: return "ANDROID";
				default: throw new IllegalArgumentException();
			}
		}
	};

	/**
	 * The mobility action to be performed.
	 */
	public enum Action {
		BACK, COPY, GO, MIGRATE, OFFLOAD;
		
		public static Action toAction(String s) {
			if (s.equals("BACK"))
				return BACK;
			else if (s.equals("COPY"))
				return COPY;
			else if (s.equals("GO"))
				return GO;
			else if (s.equals("MIGRATE"))
				return MIGRATE;
			else if (s.equals("OFFLOAD"))
				return OFFLOAD;
			else throw new IllegalArgumentException();
		}
		
		@Override
		public String toString() {
			switch (this) {
				case BACK: return "BACK";
				case COPY: return "COPY";
				case GO: return "GO";
				case MIGRATE: return "MIGRATE";
				case OFFLOAD: return "OFFLOAD";
				default: throw new IllegalArgumentException();
			}
		}
	};

	/**
	 * The type of the mobility action subject.
	 */
	public enum MigrationSubject {
		FM, SERVICE, DEPENDENCY;
		
		public static MigrationSubject toMigrationSubject(String s) {
			if (s.equals("FM"))
				return FM;
			else if (s.equals("SERVICE"))
				return SERVICE;
			else if (s.equals("DEPENDENCY"))
				return DEPENDENCY;
			else throw new IllegalArgumentException();
		}
		
		@Override
		public String toString() {
			switch (this) {
				case FM: return "FM";
				case SERVICE: return "SERVICE";
				case DEPENDENCY: return "DEPENDENCY";
				default: throw new IllegalArgumentException();
			}
		}
	};

	/**
	 * An array representing the type of the NAM (ANDROID or DESKTOP). i-th
	 * element is relative to the client served by the i-th thread in the pool
	 */
	Platform[] clientPlatform;

	/**
	 * A HashMap for the functional modules added to the NAM. The keys are String
	 * identifying the functional modules. The values are FunctionalModule
	 * objects.
	 */
	HashMap<String, FunctionalModule> functionalModules = new HashMap<String, FunctionalModule>();

	/**
	 * A HashMap for the Resources of the NAM node. The keys are String
	 * identifying the Resources. The values are Resources objects.
	 */
	HashMap<String, ResourceDescriptor> resourceDescriptors = new HashMap<String, ResourceDescriptor>();

	/**
	 * A HashMap to store the address of NAMs which sent Functional Modules.
	 */
	HashMap<String, String> fmSender = new HashMap<String, String>();

	/**
	 * A HashMap to store the address of NAMs which sent Services.
	 */
	HashMap<String, String> serviceSender = new HashMap<String, String>();
	
	/**
	 * A HashMap to store the address of NAMs to which a Functional Module was sent.
	 */
	HashMap<String, String> fmReceiver = new HashMap<String, String>();

	/**
	 * A HashMap to store the address of NAMs to which a Service was sent.
	 */
	HashMap<String, String> serviceReceiver = new HashMap<String, String>();

	/**
	 * An int representing the size of the threads pool for the migration.
	 */
	int poolSize;

	/**
	 * The threads pool to manage the migration requests.
	 */
	ExecutorService poolForServerMobilityAction;

	/**
	 * The threads pool to manage the migration requests.
	 */
	ExecutorService poolForClientMobilityAction;

	/**
	 * The number of times a client tries to connect to the server
	 */
	private int trialsNumber = 3;
	
	/**
	 * The platform of current node
	 */
	private Platform platform;

	/**
	 * Address of the server to which the migration requests should be sent.
	 */
	String serverAddress = "localhost";

	/**
	 * Ports of the server to which the migration requests should be sent. Each
	 * thread of the pool listens on a different port
	 */
	private int serverPort = 11111;
	
	/**
	 * Boolean allowing to stop mobility action's management
	 */
	private boolean stopAcceptingMobilityActions = false;

	/**
	 * The path where the java, Jar and Dex files for migration are stored (both
	 * received and sent).
	 */
	String migrationStore;

	/**
	 * Class constructor.
	 * 
	 * @param poolSize
	 *            the size of the thread pool to manage incoming requests
	 * 
	 * @param migrationStorePath
	 *            the path to store files received via migration
	 * 
	 * @param trials
	 *            the number of times a client tries to connect to a server
	 * 
	 * @param platform
	 *            the {@link Platform} of current node
	 */
	public NetworkedAutonomicMachine(int poolSize, String migrationStorePath, int trials, Platform platform) {
		setPoolSize(poolSize);
		setMigrationStore(migrationStorePath);
		setTrialsNumber(trials);
		setPlatform(platform);

		// Creation of the thread pools to manage incoming mobility action requests.
		createPoolForServerMobilityAction();
		createPoolForClientMobilityAction();

		clientPlatform = new Platform[getPoolSize()];
	}

	/**
	 * Sets the address of the server to which the migration requests should be
	 * sent.
	 * 
	 * @param address
	 *            a String identifying the address of the server for migration
	 */
	public void setServerAddress(String address) {
		this.serverAddress = address;
	}

	/**
	 * Returns the address of the server to which the migration requests should
	 * be sent.
	 * 
	 * @return a String identifying the address of the server to which the
	 *         migration requests should be sent
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * Sets the String representing the type of the NAM.
	 * 
	 * @param cp
	 *            a representation of the NAM type (ANDROID or DESKTOP)
	 * @param index
	 *            an int representing the index of the clients platforms array
	 *            where the value has to be stored
	 */
	public void setClientPlatform(Platform cp, int index) {
		this.clientPlatform[index] = cp;
	}

	/**
	 * Returns the String representing the type of the NAM.
	 * 
	 * @param index
	 *            an int representing the index of the clients platforms array
	 *            where the required value is stored
	 * @return a representation of the NAM type (ANDROID or DESKTOP)
	 */
	public Platform getClientPlatform(int index) {
		return clientPlatform[index];
	}

	/**
	 * Sets the port of the server to which the migration requests should be
	 * sent.
	 * 
	 * @param port
	 *            an int identifying the port of the server for migration
	 * @param index
	 *            an int representing the index of the port array where the
	 *            value has to be stored
	 */
	public void setServerPort(int port) {
		this.serverPort = port;
	}

	/**
	 * Returns the port of the server to which the migration requests should be
	 * sent.
	 * 
	 * @param index
	 *            an int representing the index of the port array where the
	 *            required value is stored
	 * @return the port of the server to which the migration requests should be
	 *         sent
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Sets the identifier of the NAM.
	 * 
	 * @param id
	 *            a String identifying the NAM
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the identifier of the NAM.
	 * 
	 * @return a String identifying the NAM
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the name of the NAM.
	 * 
	 * @param name
	 *            a String identifying the name of the NAM
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the NAM.
	 * 
	 * @return a String identifying the name of the NAM
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the size of the thread pool to manage migration requests.
	 * 
	 * @param size
	 *            the size of the thread pool to manage migration requests
	 */
	public void setPoolSize(int size) {
		this.poolSize = size;
	}

	/**
	 * Gets the size of the thread pool to manage migration requests.
	 * 
	 * @return the size of the thread pool to manage migration requests
	 */
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * Sets the path where the files to be migrated are stored.
	 * 
	 * @param ms
	 *            the path where the files to be migrated are stored
	 */
	public void setMigrationStore(String ms) {
		this.migrationStore = ms;
	}

	/**
	 * Gets the the path where the files to be migrated are stored.
	 * 
	 * @return the path where the files to be migrated are stored
	 */
	public String getMigrationStore() {
		return migrationStore;
	}

	/**
	 * Set the number of times a client tries to connect to a server.
	 * 
	 * @param num
	 *            the number of times a client tries to connect to a server.
	 */
	public void setTrialsNumber(int num) {
		trialsNumber = num;
	}

	/**
	 * Get the number of times a client tries to connect to a server.
	 * 
	 * @return the number of times a client tries to connect to a server.
	 */
	public int getTrialsNumber() {
		return trialsNumber;
	}
	
	/**
	 * Set the {@link Platform} of current node.
	 * 
	 * @param platform
	 *            the {@link Platform} of current node
	 */
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	/**
	 * Get the {@link Platform} of current node.
	 * 
	 * @return the {@link Platform} of current node
	 */
	public Platform getPlatform() {
		return this.platform;
	}

	/**
	 * Adds a Functional Module to the NAM.
	 * 
	 * @param functionalModule
	 *            a reference to the Functional Module to be added to the NAM
	 */
	public void addFunctionalModule(FunctionalModule functionalModule) {
		functionalModules.put(functionalModule.getId(), functionalModule);
	}

	/**
	 * Remove a Functional Module from the NAM.
	 * 
	 * @param id
	 *            a String identifying the Functional Module to be removed from
	 *            the NAM
	 */
	public void removeFunctionalModule(String id) {
		functionalModules.remove(id);
	}

	/**
	 * Gets the Functional Modules added to the NAM.
	 * 
	 * @return a reference to the Hash Table containing the Functional Modules
	 *         added to the NAM
	 */
	public HashMap<String, FunctionalModule> getFunctionalModules() {
		return functionalModules;
	}

	/**
	 * Gets a Functional Module added to the NAM.
	 * 
	 * @param id
	 *            a String identifying the required Functional Module
	 * @return a reference to the required Functional Module
	 */
	public FunctionalModule getFunctionalModule(String id) {
		return functionalModules.get(id);
	}

	/**
	 * Removes a Resource from the NAM node.
	 * 
	 * @param id
	 *            a String identifying the Resource to be removed
	 */
	public void removeResource(String id) {
		resourceDescriptors.remove(id);
	}

	/**
	 * Add to a HashMap the address of a NAM which sent a FM.
	 * 
	 *  @param sender
	 *            The address of the sender
	 * @param fm
	 *            The identifier of the received FM
	 *            
	 */
	public void addFmSender(String sender, String fm) {
		fmSender.put(sender, fm);
	}

	/**
	 * Remove from the HashMap the address of a NAM which sent a FM.
	 * 
	 * @param sender
	 *            The address of the node to be removed
	 */
	public void removeFmSender(String sender) {
		fmSender.remove(sender);
	}
	
	/**
	 * Add to a HashMap the address of a NAM which sent a Service.
	 * 
	 * @param sender
	 *            The address of the sender 
	 * @param service
	 *            The identifier of the received Service
	 *            
	 */
	public void addServiceSender(String sender, String service) {
		serviceSender.put(sender, service);
	}

	/**
	 * Remove from the HashMap the address of a NAM which sent a Service.
	 * 
	 * @param sender
	 *            The address of the node to be removed
	 */
	public void removeServiceSender(String sender) {
		serviceSender.remove(sender);
	}
	
	/**
	 * Add to a HashMap the address of a NAM to which a FM was sent.
	 * 
	 * @param receiver
	 *            The address of the receiver
	 * @param fm
	 *            The identifier of the sent FM
	 * 
	 */
	public void addFmReceiver(String receiver, String fm) {
		fmReceiver.put(receiver, fm);
	}

	/**
	 * Remove from the HashMap the address of a NAM to which a FM was sent.
	 * 
	 * @param receiver
	 *            The address of the node to be removed
	 */
	public void removeFmReceiver(String receiver) {
		fmReceiver.remove(receiver);
	}
	
	/**
	 * Add to a HashMap the address of a NAM to which a Service was sent.
	 * 
	 * @param receiver
	 *            The address of the receiver
	 * @param service
	 *            The identifier of the sent Service
	 */
	public void addServiceReceiver(String receiver, String service) {
		serviceReceiver.put(receiver, service);
	}

	/**
	 * Remove from the HashMap the address of a NAM to which a Service was sent.
	 * 
	 * @param receiver
	 *            The address of the node to be removed
	 */
	public void removeServiceReceiver(String receiver) {
		serviceReceiver.remove(receiver);
	}

	/**
	 * Create a thread pool to manage migration requests.
	 * newFixedThreadPool(int nThreads) method creates a thread pool that reuses
	 * a fixed number of threads operating off a shared unbounded queue. At any
	 * point, at most nThreads threads will be active processing tasks. If
	 * additional tasks are submitted when all threads are active, they will
	 * wait in the queue until a thread is available.
	 * 
	 * @return a reference to the pool
	 */
	private ExecutorService createPoolForServerMobilityAction() {
		poolForServerMobilityAction = Executors.newFixedThreadPool(poolSize);
		return poolForServerMobilityAction;
	}

	/**
	 * Creates the thread pool to manage the migration requests.
	 * newFixedThreadPool(int nThreads) method creates a thread pool that reuses
	 * a fixed number of threads operating off a shared unbounded queue. At any
	 * point, at most nThreads threads will be active processing tasks. If
	 * additional tasks are submitted when all threads are active, they will
	 * wait in the queue until a thread is available.
	 * 
	 * @return a reference to the pool
	 */
	private ExecutorService createPoolForClientMobilityAction() {
		poolForClientMobilityAction = Executors.newFixedThreadPool(poolSize);
		return poolForClientMobilityAction;
	}

	/**
	 * Gets the Resources List of the NAM node.
	 * 
	 * @return a HashMap storing the Resources of the NAM node.
	 */
	public HashMap<String, ResourceDescriptor> getResources() {
		return resourceDescriptors;
	}

	/**
	 * Get a Resource of the NAM node.
	 * 
	 * @param id
	 *            a String identifying the required Resource
	 * @return the required Resource of the NAM node.
	 */
	public ResourceDescriptor getResource(String id) {
		return resourceDescriptors.get(id);
	}
	
	/**
	 * Method to stop the management of mobility actions.
	 */
	public void stopMobilityActionsManagement() {
		stopAcceptingMobilityActions = true;
	}

	/**
	 * Server implementation: it waits for incoming connections and dispatches
	 * them to the threadpool.
	 */
	public void startMobilityAction() {
		
		// Reset the variable used to stop the management
		stopAcceptingMobilityActions = false;

		try {

			Socket cs = null;
			ServerSocket ss = null;

			ss = new ServerSocket(serverPort);

			System.out.println("SERVER: thread "
					+ Thread.currentThread().getId()
					+ " has created server socket " + ss);

			System.out.println("SERVER: thread "
					+ Thread.currentThread().getId()
					+ " is waiting for connections.");

			while (!stopAcceptingMobilityActions) {

				cs = ss.accept();

				poolForServerMobilityAction
						.execute(new ServerMobilityActionManager(cs, this));

				System.out
						.println("SERVER: thread "
								+ Thread.currentThread().getId()
								+ " accepted connection. Waiting for another one.");

			}
			
			ss.close();
			
		} catch (IOException e1) {
			System.err.println("SERVER: connection failed for thread "
					+ Thread.currentThread().getId());
		}
	}

	/**
	 * Start a COPY mobility action.
	 * 
	 * @param functionalModule
	 *            the name of the FM to copy
	 * @param service
	 *            the name of the Service of functionalModule to be copied (can
	 *            be null if you need to copy only a FM)
	 * @param serviceId
	 *            the id of the Service of functionalModule to be copied (can be
	 *            null if you need to copy only a FM)
	 * @param clientType
	 *            (ANDROID or DESKTOP)
	 * 
	 * @param ml
	 *            The object that wants to get notified; null if notification is
	 *            not requested
	 */
	public void startCopyAction(String functionalModule, String[] service,
			String[] serviceId, Platform clientType, IMigrationListener ml) {

		ClientCopyActionManager clientCopyActionManager = new ClientCopyActionManager(this,
				functionalModule, service, serviceId, clientType, Action.COPY);
		
		// If requested, the object can subscribe to notifications
		if (ml != null)
			clientCopyActionManager.addMigrationListener(ml);
		
		poolForClientMobilityAction.execute(clientCopyActionManager);
	}

}