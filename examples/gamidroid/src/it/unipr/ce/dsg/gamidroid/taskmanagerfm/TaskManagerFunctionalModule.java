package it.unipr.ce.dsg.gamidroid.taskmanagerfm;

import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.task.TaskDescriptor;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

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
		
		File sdLog = new File(Environment.getExternalStorageDirectory()
				+ Constants.CONFIGURATION_FILES_PATH);
		
		this.tmLogger = new TaskManagerLogger(sdLog.getAbsolutePath() + "/");
		
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
	public void addConsumableService(String id, IService service) {}

	@Override
	public void addProvidedService(String id, IService service) {}
}
