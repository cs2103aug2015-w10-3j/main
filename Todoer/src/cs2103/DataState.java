//@@author A0093929M
package cs2103;
import java.util.*;

public class DataState {
	private ArrayList<Task> allTasks;

	public DataState() {

	}

	public DataState(ArrayList<Task> tasks) {
		//Initialise the variables;	
		allTasks = new ArrayList<Task>();	
		for (int i = 0;i < tasks.size();i++)
			allTasks.add(tasks.get(i).copy());
	}

	public ArrayList<Task> getAllTasks() {
		return allTasks;
	}

	public void setAllTasks(ArrayList<Task> tasks) {
		allTasks = tasks;
	}
	
}