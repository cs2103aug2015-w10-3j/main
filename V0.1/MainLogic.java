
import java.io.*;
import java.util.*;


public class MainLogic {

	private static String ENTER = "Enter";
    private String dataFile = "data3";

	CommandParser mTaskCommandParse = new CommandParser();
	private final String messageSuccessful = "Successful";
	private ArrayList<DataState> history;
	private int currentState;
	private ArrayList<String> allTasksInStrings;
	private ArrayList<Task> allTasks;
	private Storage mStorage;

	
	public MainLogic() {
		//Initialise the variables;
		mStorage = new Storage();
		mStorage.setFileURL(dataFile);

		allTasks = new ArrayList<Task>();
		history = new ArrayList<DataState>();
		try{
			allTasksInStrings = mStorage.readContent();
			for (int i=0;i<allTasksInStrings.size();i++){
				allTasks.add(Task.stringToTask(allTasksInStrings.get(i)));
			}
			history.add(new DataState(allTasks));
			currentState = 0;
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	protected void reloadDataInStrings(){
		allTasksInStrings = new ArrayList<String>();
		for (int i=0;i<allTasks.size();i++){
			allTasksInStrings.add(allTasks.get(i).toString());
		}
	}
	protected void updateHistory(){
		if (currentState < history.size()-1){
			while (history.size()>currentState+1)
				history.remove(currentState+1);
		}
		history.add(new DataState(allTasks));
		currentState++;
		reloadDataInStrings();
		
	}
	private class TaskDeadlineCompare implements Comparator<Task> {

	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getDeadlineString().equals("")) return 1;
	        if (o2.getDeadlineString().equals("")) return -1;
	        return o1.getDeadline().compareTo(o2.getDeadline());
	    }
	}
	protected String process(String userCommand) {
		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		command = commandInfo[0];  String field1 = commandInfo[1]; String field2 = commandInfo.length > 2 ? commandInfo[2]:"";
		
		switch (command){
			case "add":
				Task newTask = new Task(field1);
				newTask.setDeadline(field2);

				allTasks.add(newTask);
				updateHistory();
				mStorage.rewriteContent(allTasksInStrings);
				return "Successfully added '" + field1 + "'\n";
			case "showall":
				return showAll(allTasks);
			case "delete":
				String deleted;
				for (int i=0;i<allTasks.size();i++){
					if (allTasks.get(i).getName().equals(taskInfo)){
						deleted = allTasks.get(i).getName();
						allTasks.remove(i);
						updateHistory();
						mStorage.rewriteContent(allTasksInStrings);
						return "'"+deleted+"' was removed successfully\n";
					}
				}
				return "Error: task '" + taskInfo +"' not found.\n";

			case "update":
				String updated;
				String [] arguments = taskInfo.split(" ");

				if (arguments.length != 2) return "Syntax error\n";
				for (int i=0;i<allTasks.size();i++){
					if (allTasks.get(i).getName().equals(arguments[0])){
						updated = allTasks.get(i).getName();
						allTasks.get(i).setName(arguments[1]);

						updateHistory();
						mStorage.rewriteContent(allTasksInStrings);

						return "'"+updated+"' was updated successfully to '" + arguments[1] + "'\n";
					}
				}
				return "Error: task '" + taskInfo +"' not found.\n";
			case "showby":
				switch (field1){
					case "deadline":
						ArrayList<Task> tempTasks = duplicate(allTasks);
						Collections.sort(tempTasks,new TaskDeadlineCompare());
						return showAll(tempTasks);
					default:break;
				}
			case "showday":
				String res = "";
				for (int i=0;i<allTasks.size();i++){
					if (allTasks.get(i).getDeadlineString().equals(field1)){
						res += allTasks.get(i).getDisplay();
					}
				}
				if (res.equals("")) return "no task found\n";
				else return res;
			case "undo":
				if (currentState>0) {
					currentState--;
					allTasks = history.get(currentState).getAllTasks();
					reloadDataInStrings();
					mStorage.rewriteContent(allTasksInStrings);
					return "undid successfully\n";
				}else{
					return "Nothing to be undone !\n";
				}
			case "redo":
				if (currentState < history.size()-1){
					currentState++;
					allTasks = history.get(currentState).getAllTasks();
					reloadDataInStrings();
					mStorage.rewriteContent(allTasksInStrings);
					return "redid successfully\n";
				}
				else return "No more operations to redo !\n";
			case "exit":
				return null;
			default:
				return "Syntax error: command(" + command + ") not found.\n";
		}
		
	}
	
	protected ArrayList<Task> duplicate(ArrayList<Task> tasks){
		ArrayList<Task> newTasks = new ArrayList<Task>();
		for (int i=0;i<tasks.size();i++)
			newTasks.add(tasks.get(i).copy());
		return newTasks;
	}

	protected String showAll(ArrayList<Task> tasks) {
		String result = "";
		for (int i=0;i<tasks.size();i++){
			result += tasks.get(i).getDisplay();
		}
		return result;
	}


}
