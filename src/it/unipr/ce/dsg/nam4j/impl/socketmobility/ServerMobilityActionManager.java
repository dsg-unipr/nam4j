package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.logger.NamLogger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerMobilityActionManager implements Runnable {

	NetworkedAutonomicMachine nam = null;
	Socket cs;

	// Action Handlers
	private BackActionImplementation backActionImplementation;
	private CopyActionImplementation copyActionImplementation;
	private GoActionImplementation goActionImplementation;
	private MigrateActionImplementation migrateActionImplementation;
	private OffloadActionImplementation offloadActionImplementation;
	
	/** The logger object */
	private NamLogger messageLogger;

	public ServerMobilityActionManager(Socket connection,
			NetworkedAutonomicMachine nam) {

		this.cs = connection;
		this.nam = nam;
		
		messageLogger = new NamLogger("ServerMobilityActionManager");
	}

	public void run() {

		BufferedReader is = null;
		OutputStream os = null;

		messageLogger.debug("SERVER: thread " + Thread.currentThread().getId()
				+ " accepted connection from " + cs);

		try {

			/*
			 * Get the input stream reader and the output stream writer for the
			 * socket. Both will be passed to the specific mobility action
			 * manager.
			 */
			BufferedInputStream ib = new BufferedInputStream(
					cs.getInputStream());
			is = new BufferedReader(new InputStreamReader(ib));

			os = cs.getOutputStream();

			// Checking the required action
			Action a = Action.valueOf(new String(is.readLine()));

			switch (a) {
				case BACK: {
	
					backActionImplementation = new BackActionImplementation(
							this.nam, is, os, cs.getRemoteSocketAddress().toString());
	
					Thread backActionThreadStart = new Thread(
							backActionImplementation);
					backActionThreadStart.start();
	
					break;
				}
				case COPY: {
	
					copyActionImplementation = new CopyActionImplementation(
							this.nam, is, os, cs.getRemoteSocketAddress().toString());
	
					Thread copyActionThreadStart = new Thread(
							copyActionImplementation);
					copyActionThreadStart.start();
	
					break;
				}
				case GO: {
	
					goActionImplementation = new GoActionImplementation(this.nam,
							is, os, cs.getRemoteSocketAddress().toString());
	
					Thread goActionThreadStart = new Thread(goActionImplementation);
					goActionThreadStart.start();
	
					break;
				}
				case MIGRATE: {
	
					migrateActionImplementation = new MigrateActionImplementation(
							this.nam, is, os, cs.getRemoteSocketAddress().toString());
	
					Thread migrateActionThreadStart = new Thread(
							migrateActionImplementation);
					migrateActionThreadStart.start();
	
					break;
				}
				case OFFLOAD: {
	
					offloadActionImplementation = new OffloadActionImplementation(
							this.nam, is, os, cs.getRemoteSocketAddress().toString());
	
					Thread offloadActionThreadStart = new Thread(
							offloadActionImplementation);
					offloadActionThreadStart.start();
	
					break;
				}
			}

		} catch (Exception e) {
			messageLogger.error("SERVER: error: " + e + " for thread "
					+ Thread.currentThread().getId());
		}
	}

}
