package it.unipr.ce.dsg.nam4j.impl.service;

import java.util.ArrayList;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.interfaces.IFunctionalModule;
import it.unipr.ce.dsg.nam4j.interfaces.IServiceHandler;
import it.unipr.ce.dsg.nam4j.interfaces.IServiceRequest;

public abstract class ServiceHandler implements IServiceHandler, Runnable {

	private FunctionalModule functionalModule = null;
	private String address = null;
	private ArrayList<IServiceHandler> remoteServiceHandlers = null;
	
	
	public ServiceHandler(FunctionalModule functionalModule, String address) {
		this.functionalModule = functionalModule;
		this.address = address;
		this.remoteServiceHandlers = new ArrayList<IServiceHandler>();
	}
	
	
	public IFunctionalModule getAssociatedFunctionalModule() {
		return functionalModule;
	}

	
	public void setAssociatedFunctionalModule(IFunctionalModule functionalModule) {
		this.functionalModule = (FunctionalModule) functionalModule;
	}

	
	public String getAddress() {
		return address;
	}

	
	public void setAddress(String address) {
		this.address = address;
	}

	
	public int addRemoteServiceHandler(IServiceHandler remoteServiceHandler) {
		remoteServiceHandlers.add((ServiceHandler) remoteServiceHandler);
		return 0;
	}
	
	
	public int removeRemoteServiceHandler(IServiceHandler remoteServiceHandler) {
		if (remoteServiceHandlers.contains((ServiceHandler) remoteServiceHandler)) {
			remoteServiceHandlers.remove((ServiceHandler) remoteServiceHandler);
			return 0;
		}
		else
			return -1;
	}
	
	
	public int deliverServiceRequest(IServiceRequest serviceRequest) {
		// TODO 
		
		// if functional module is local, then call execute(serviceRequest)
		
		// else propagate serviceRequest to remote SH
		
		return 0;
	}
	
	// the SH must be instantiated in a separate thread by the associated FM
	public void run() { 
		// TODO
		
		// listen to incoming service requests (from remote SHs or Dispatchers)
		// ServiceRequest serviceRequest = .. 	
		// this.deliverServiceRequest(serviceRequest);
	}
	
}
