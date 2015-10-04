
import java.io.*;
import java.util.*;


public class TaskLogic {

	private static String ENTER = "Enter";
    private String dataFile = "data.txt";

	TaskCommandParse mTaskCommandParse = new TaskCommandParse();
	private final String messageSuccessful = "Successful";
	private ArrayList<String> allTasks;
	private TaskStorage mTaskStorage;

	
	public TaskLogic() {
		//Initialise the variables;
		mTaskStorage = new TaskStorage();
		mTaskStorage.setFileURL(dataFile);

		allTasks = mTaskStorage.readContent();
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
