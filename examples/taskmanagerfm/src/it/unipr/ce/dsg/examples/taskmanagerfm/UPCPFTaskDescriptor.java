package it.unipr.ce.dsg.examples.taskmanagerfm;

import it.unipr.ce.dsg.nam4j.impl.task.TaskDescriptor;

import java.util.ArrayList;

public class UPCPFTaskDescriptor extends TaskDescriptor {

	private ArrayList<String> processingServices = null;
	
	public UPCPFTaskDescriptor(String name, String id) {
		super(name, id);
		ArrayList<String> allowedStates = new ArrayList<String>();
		allowedStates.add("UNSTARTED");
		allowedStates.add("PROCESSING");
		allowedStates.add("COMPLETED");
		allowedStates.add("PAUSED");
		allowedStates.add("FAILED");
		this.setAllowedStates(allowedStates);
		processingServices = new ArrayList<String>();
	}
	
	public void addProcessingService(String serviceName) {
		processingServices.add(serviceName);
	}
	
	public ArrayList<String> getProcessingServices() {
		return processingServices;
	}

}
