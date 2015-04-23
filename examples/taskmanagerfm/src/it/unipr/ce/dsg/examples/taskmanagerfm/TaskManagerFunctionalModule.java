package it.unipr.ce.dsg.examples.taskmanagerfm;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.task.TaskDescriptor;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.util.ArrayList;

import com.google.gson.Gson;

public class TaskManagerFunctionalModule extends FunctionalModule {

	private ArrayList<TaskDescriptor> tasks = null;
	private TaskManagerLogger tmLogger = null;
	
	public ArrayList<TaskDescriptor> getTasks() {
		return tasks;
	}

	public TaskManagerFunctionalModule(NetworkedAutonomicMachine nam) {
		super(nam);
		this.setId("tmfm");
		this.setName("TaskManagerFunctionalModule");
		this.tmLogger = new TaskManagerLogger("log/");
		tmLogger.log("I am " + this.getId() + " and I own to " + nam.getId());
		this.tasks = new ArrayList<TaskDescriptor>();
	}

	public void addTaskDescriptor(TaskDescriptor td) {
		if (!tasks.contains(td))
			tasks.add(td);
	}
	
	public void removeTaskDescriptor(TaskDescriptor td) {
		if (tasks.contains(td))
			tasks.remove(td);
	}
	
	public String convertTaskDescriptorToJSON(TaskDescriptor td) {
		Gson gson = new Gson();
		String json = gson.toJson(td);
		tmLogger.log(td);
		return json;
	}
	
	public TaskManagerLogger getLogger() {
		return tmLogger;
	}
	
	public void startTaskManagement() {
		Thread t = new Thread(new ManageTasksRunnable(this), "Perform task management");
		t.start();
	}

	@Override
	public void addConsumableService(String id, IService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addProvidedService(String id, IService service) {
		// TODO Auto-generated method stub
		
	}
}
