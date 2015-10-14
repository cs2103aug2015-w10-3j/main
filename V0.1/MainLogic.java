
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
	private ArrayList<String> allUserCommands;
	private Storage mStorage;

	private int mBigNum = 100000;

	
	public MainLogic() {
		//Initialise the variables;
		mStorage = new Storage();
		mStorage.setFileURL(dataFile);

		allUserCommands = new ArrayList<String>();
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

		addNewUserCommand(userCommand);

		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		
		command = commandInfo[0];  String field1 = commandInfo[1]; String field2 = commandInfo.length > 2 ? commandInfo[2]:"";
		taskInfo = field1;
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
			case "search":
				return searchForKey(field1);
			case "exit":
				return null;
			default:
				return "Syntax error: command(" + command + ") not found.\n";
		}
		
	}

	
	//supported simple search API
	private class MatchCount {
		int id, count;
	}

	private String searchForKey(String arg) {
		String[] arguments = arg.split(" ");
		String args = "";
		for(int i=0; i<arguments.length; i++) {
			args = args + arguments[i];
		}
		args = args.toLowerCase();

		ArrayList<MatchCount> matchCount = new ArrayList<>();
		for(int i=0; i<allTasks.size(); i++) {
			matchCount.add(i, new MatchCount());
			matchCount.get(i).id = i;
			matchCount.get(i).count = 0;
			arguments = allTasks.get(i).getDisplay().split(" ");
			String task = "";
			for(int j=0; j<arguments.length; j++) {
				task = task + arguments[j];
			}
			task = task.toLowerCase();

			if (task.contains(arg)) {
				matchCount.get(i).count = mBigNum;
				continue;
			}
			for(int j=0; j<args.length(); j++) {
				String st = "";
				for(int k=j; k<args.length(); k++) {
					st = st + args.charAt(k);
					if (task.contains(st)) {
						matchCount.get(i).count++;
					}
				}
			}

		}
		Collections.sort(matchCount, new TaskSearchMatchCountCompare());
		String result = "";
		for(int i=0; i<matchCount.size(); i++) {
			if (matchCount.get(i).count > 0) {
				int id = matchCount.get(i).id;
				result = result + allTasks.get(id).getDisplay(); 
			}
		}
		if (result.isEmpty()) {
			result = "Nothing matched!";
		}

		return result;

	}

	private class TaskSearchMatchCountCompare implements Comparator<MatchCount> {

	    @Override
	    public int compare(MatchCount o1, MatchCount o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.count <= o2.count) {
	        	return 1;
	        }
	        return -1;
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

	//supported key up/down to show old command
	private void addNewUserCommand(String userCommand) {
		allUserCommands.add(userCommand);
	}

	protected String getOldUserCommand(int pos) {
		if (pos > allUserCommands.size()) {
			return allUserCommands.get(0);
		} else if (pos == 0) {
			return "";
		}
		return allUserCommands.get(allUserCommands.size()-pos);
	}

	protected void deleteAllUserCommands() {
		allUserCommands = new ArrayList<String>();
	}

	protected int getOldUserCommandSize() {
		return allUserCommands.size();
	}

}
