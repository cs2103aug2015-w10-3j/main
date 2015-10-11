
import java.io.*;
import java.util.*;


public class TaskLogic {

	private static String ENTER = "Enter";
    private String dataFile = "data";

	TaskCommandParse mTaskCommandParse = new TaskCommandParse();
	private final String messageSuccessful = "Successful";
	private ArrayList<String> allTasks;
	private TaskStorage mTaskStorage;

	
	public TaskLogic() {
		//Initialise the variables;
		mTaskStorage = new TaskStorage();
		mTaskStorage.setFileURL(dataFile);

		try{
			allTasks = mTaskStorage.readContent();
		}
		catch (Exception e){
			if (e instanceof FileNotFoundException){
				allTasks = new ArrayList<String>();
			}
		}
	}

	protected String process(String userCommand) {
		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		command = commandInfo[0];  taskInfo = commandInfo[1];
		
		switch (command){
			case "add":
				allTasks.add(taskInfo);
				mTaskStorage.rewriteContent(allTasks);
				return "Successfully added '" + taskInfo + "'\n";
			case "showall":
				return showAll();
			case "delete":
				String deleted;
				for (int i=0;i<allTasks.size();i++){
					if (allTasks.get(i).equals(taskInfo)){
						deleted = allTasks.get(i);
						allTasks.remove(i);
						return "'"+deleted+"' was removed successfully\n";
					}
				}

				return "Error: task '" + taskInfo +"' not found.\n";
			case "update":
				String updated;
				String [] arguments = taskInfo.split(" ");

				if (arguments.length != 2) return "Syntax error\n";
				for (int i=0;i<allTasks.size();i++){
					if (allTasks.get(i).equals(arguments[0])){
						updated = allTasks.get(i);
						allTasks.set(i,arguments[1]);
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
			result += allTasks.get(i)+"\n";
		}
		return result;
	}


}
