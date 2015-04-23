package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 * Class which implements the server-side management of the GO mobility action.
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
public class GoActionImplementation extends GoActionHandler {
	
	NetworkedAutonomicMachine nam = null;
	BufferedReader is;
	OutputStream os;
	String receiver;
	
	// The descriptor of the object to be migrated.
	BundleDescriptor bundleDescriptor;
	
	public GoActionImplementation(NetworkedAutonomicMachine nam, BufferedReader is, OutputStream os, String receiver) {
		this.nam = nam;
		this.is = is;
		this.os = os;
		this.receiver = receiver;
		
		System.out.println("SERVER: starting GO action...");
	}
	
	// TODO: when a FM or Service is sent, remove from fmSender and
	// serviceSender hashMaps the relative record and update the reference on
	// the offloader so that it points to the new node

	private void fmMobility(String line, Socket cs) {
		
	}
	
	private void serviceMobility(String line, Socket cs) {
		
	}
	
	public void run() {
		
	}

}
