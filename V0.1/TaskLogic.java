
import java.io.*;
import java.util.*;


public class TaskLogic {

	private static String ENTER = "Enter";
    private String dataFile = "data2";

	TaskCommandParse mTaskCommandParse = new TaskCommandParse();
	private final String messageSuccessful = "Successful";
	private ArrayList<String> allTasksInStrings;
	private ArrayList<Task> allTasks;
	private TaskStorage mTaskStorage;

	
	public TaskLogic() {
		//Initialise the variables;
		mTaskStorage = new TaskStorage();
		mTaskStorage.setFileURL(dataFile);

		allTasks = new ArrayList<Task>();
		try{
			allTasksInStrings = mTaskStorage.readContent();
			for (int i=0;i<allTasksInStrings.size();i++){
				allTasks.add(Task.stringToTask(allTasksInStrings.get(i)));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	protected String process(String userCommand) {
		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		command = commandInfo[0];  taskInfo = commandInfo[1];
		
		switch (command){
			case "add":
				Task newTask = new Task(taskInfo);
				allTasks.add(newTask);
				allTasksInStrings.add(newTask.toString());

				mTaskStorage.rewriteContent(allTasksInStrings);
				return "Successfully added '" + taskInfo + "'\n";
			case "showall":
				return showAll();
			case "delete":
				String deleted;
				for (int i=0;i<allTasks.size();i++){
					if (allTasks.get(i).getName().equals(taskInfo)){
						deleted = allTasks.get(i).getName();
						allTasks.remove(i);
						allTasksInStrings.remove(i);
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
						allTasksInStrings.set(i, allTasks.get(i).toString() );

						return "'"+updated+"' was updated successfully to '" + arguments[1] + "'\n";
					}
				}
				return "Error: task '" + taskInfo +"' not found.\n";

			case "exit":
				return null;
			default:
				return "Syntax error: command(" + command + ") not found.\n";
		}
		
	}

	protected String showAll() {
		String result = "";
		for (int i=0;i<allTasks.size();i++){
			result += allTasks.get(i).getName()+"\n";
		}
		return result;
	}


}
