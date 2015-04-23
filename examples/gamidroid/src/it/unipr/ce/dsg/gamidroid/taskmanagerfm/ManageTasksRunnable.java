package it.unipr.ce.dsg.gamidroid.taskmanagerfm;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ManageTasksRunnable implements Runnable {

	private TaskManagerFunctionalModule tmfm = null;
	
	public ManageTasksRunnable(TaskManagerFunctionalModule tmfm) {
		this.tmfm = tmfm;
	}
	
	@Override
	public void run() {
		tmfm.getLogger().log("Number of tasks = " + tmfm.getTasks().size());
		
		while (true) {
			for (int i = 0; i<tmfm.getTasks().size(); i++) {
				UPCPFTaskDescriptor utd = (UPCPFTaskDescriptor) tmfm.getTasks()
						.get(i);
				
				ArrayList<String> processingServices = utd.getProcessingServices();
				
				// look for services that are necessary for the processing state of
				// the task
				Iterator<String> psitr = processingServices.iterator();
				String psName = null;
				boolean processing = true;
				while (psitr.hasNext()) {
					boolean found = false;
					psName = psitr.next();
					Collection<FunctionalModule> c = tmfm.getNam()
							.getFunctionalModules().values();
					Iterator<FunctionalModule> itr = c.iterator();
					String serviceName = null;
					FunctionalModule tempfm = null;
					while (itr.hasNext()) {
						tempfm = itr.next();
						if (tempfm.getName().equals(tmfm.getName()))
							continue;
						Collection<IService> cc = tempfm.getProvidedServices()
								.values();
						Iterator<IService> itrr = cc.iterator();
						while (itrr.hasNext()) {
							serviceName = itrr.next().getName();
							// System.out.println("Service: " + serviceName);
							if (serviceName.equals(psName))
								found = true;
						}
					}
					if (!found) {
						tmfm.getLogger().log("Missing processing service for task "
								+ utd.getName());
						if (utd.getState().equals("PROCESSING"))
							utd.setState("PAUSED");
						processing = false;
					}
				}
				if (processing) {
					if (utd.getState().equals("UNSTARTED") || utd.getState().equals("PAUSED"))
						utd.setState("PROCESSING");
				}
					
				tmfm.convertTaskDescriptorToJSON(utd);	
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
