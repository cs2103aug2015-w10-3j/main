
import java.io.*;
import java.util.*;
import java.lang.*;


public class MainLogic {

	private static String ENTER = "Enter";
    private String dataFile = "data4";

	CommandParser mTaskCommandParse = new CommandParser();
	private final String mMessageSuccessful = "Successful";
	private ArrayList<DataState> mHistory;
	private int mCurrentState;
	private ArrayList<String> mAllTasksInStrings;
	private ArrayList<Task> mAllTasks;
	private ArrayList<String> mAllUserCommands;
	private Storage mStorage;
	private int mSeparateLine = 84;
	
	private int mBigNum = 100000;

	
	public MainLogic() {
		//Initialise the variables;
		mStorage = new Storage();
		mStorage.setFileURL(dataFile);

		mAllUserCommands = new ArrayList<String>();
		mAllTasks = new ArrayList<Task>();
		mHistory = new ArrayList<DataState>();
		try{
			mAllTasksInStrings = mStorage.readContent();
			for (int i=0;i<mAllTasksInStrings.size();i++){
				mAllTasks.add(Task.stringToTask(mAllTasksInStrings.get(i)));
			}
			mHistory.add(new DataState(mAllTasks));
			mCurrentState = 0;
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	protected void reloadDataInStrings(){
		mAllTasksInStrings = new ArrayList<String>();
		for (int i=0;i<mAllTasks.size();i++){
			mAllTasksInStrings.add(mAllTasks.get(i).toString());
		}
	}
	protected void updateHistory(){
		if (mCurrentState < mHistory.size()-1){
			while (mHistory.size()>mCurrentState+1)
				mHistory.remove(mCurrentState+1);
		}
		mHistory.add(new DataState(mAllTasks));
		mCurrentState++;
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
		assert commandInfo.length>0;
		command = commandInfo[0];  String field1 = commandInfo[1]; String field2 = commandInfo.length > 2 ? commandInfo[2]:"";
		taskInfo = field1;
		switch (command){
			case "add":
				Task newTask = new Task(field1);
				newTask.setDeadline(field2);
				boolean isExisted = false;
				for(int i=0; i<mAllTasks.size(); i++) {
					if (mAllTasks.get(i).getName().equals(field1)) {
						isExisted = true; 
						break;
					}
				}
				if (isExisted) {
					return "This task already existed!";
				}
				mAllTasks.add(newTask);
				updateHistory();
				mStorage.rewriteContent(mAllTasksInStrings);
				return "Successfully added '" + field1 + "'\n";
			case "showall":
				return showAll(mAllTasks);
			case "delete":
				String message = "Do you mean one of these? \n" + getSeparateLine();
				int numberMatched = 0, position = 0;
				String deleted = "";
				for (int i=0;i<mAllTasks.size();i++){
					String taskName = mAllTasks.get(i).getName();
					if (taskName.equals(taskInfo)){
						mAllTasks.remove(i);
						updateHistory();
						mStorage.rewriteContent(mAllTasksInStrings);
						return "'" + taskInfo + "' was removed successfully!\n";
					}
					if (taskName.startsWith(taskInfo)) {
						numberMatched++;
						deleted = taskName;
						position = i;
						message += mAllTasks.get(i).getDisplay() + getSeparateLine();
					}
				}
				if (numberMatched > 0) {
					if (numberMatched == 1) {
						mAllTasks.remove(position);
						updateHistory();
						mStorage.rewriteContent(mAllTasksInStrings);
						return "'" + deleted + "' was removed successfully!\n";
					}
					return message;
				}
				return "Error: task '" + taskInfo +"' not found.\n";

			case "update":
				String updated;
				String [] arguments = taskInfo.split(" ");

				if (arguments.length != 2) return "Syntax error\n";
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getName().equals(arguments[0])){
						updated = mAllTasks.get(i).getName();
						mAllTasks.get(i).setName(arguments[1]);

						updateHistory();
						mStorage.rewriteContent(mAllTasksInStrings);

						return "'" + updated + "' was updated successfully to '" + arguments[1] + "'\n";
					}
				}
				return "Error: task '" + taskInfo +"' not found.\n";
			case "showby":
				switch (field1){
					case "deadline":
						ArrayList<Task> tempTasks = duplicate(mAllTasks);
						Collections.sort(tempTasks,new TaskDeadlineCompare());
						return showAll(tempTasks);
					default:break;
				}
			case "showday":
				String res = getSeparateLine();
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getDeadlineString().equals(field1)){
						res += mAllTasks.get(i).getDisplay() + getSeparateLine();
					}
				}
				if (res.equals("")) return "no task found\n";
				else return res;
			case "undo":
				if (mCurrentState>0) {
					mCurrentState--;
					mAllTasks = mHistory.get(mCurrentState).getAllTasks();
					reloadDataInStrings();
					mStorage.rewriteContent(mAllTasksInStrings);
					return "undid successfully\n";
				}else{
					return "Nothing to be undone !\n";
				}
			case "redo":
				if (mCurrentState < mHistory.size()-1){
					mCurrentState++;
					mAllTasks = mHistory.get(mCurrentState).getAllTasks();
					reloadDataInStrings();
					mStorage.rewriteContent(mAllTasksInStrings);
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

		ArrayList<MatchCount> matchCount = new ArrayList<>();
		for(int i=0; i<mAllTasks.size(); i++) {
			matchCount.add(i, new MatchCount());
			matchCount.get(i).id = i;
			matchCount.get(i).count = 0;
			String[] args = mAllTasks.get(i).getDisplay().split(" ");
			String task = "";
			for(int j=0; j<args.length; j++) {
				task = task + args[j] + " ";
			}
			task = task.toLowerCase();

			if (task.contains(arg)) {
				matchCount.get(i).count = mBigNum;
				continue;
			}
			for(int j=0; j<arguments.length; j++) {
				String st = "";
				for(int k=j; k<arguments.length; k++) {
					st = st + arguments[k];
					if (task.contains(st)) {
						matchCount.get(i).count += k-j+1;
					}
				}
			}

		}
		Collections.sort(matchCount, new TaskSearchMatchCountCompare());
		String result = getSeparateLine();
		boolean isMatched = false;
		for(int i=0; i<Math.min(matchCount.size(), 10); i++) {
			if (i == 0 && matchCount.get(i).count == 0) {
				//Nothing matched!
				result = "";
				break;
			}
			int point = matchCount.get(i).count;
			if (point >= mBigNum) {
				isMatched = true;
			}
			if (point < mBigNum && isMatched) {
				break;
			}
			if (point > 0) {
				int id = matchCount.get(i).id;
				result = result + mAllTasks.get(id).getDisplay() + getSeparateLine();
			}
		}
		if (result.isEmpty()) {
			result = "Nothing matched! Use 'showall' to show all your tasks! \n";
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
		String result = getSeparateLine();
		for (int i=0;i<tasks.size();i++){
			result += tasks.get(i).getDisplay() + getSeparateLine();
		}
		return result;
	}

	private String getSeparateLine() {
		String result = "";
		for(int i=0; i<mSeparateLine; i++) {
			result = result + "=";
		}
		result = result + "\n";
		return result;
	}

	//supported key up/down to show old command
	private void addNewUserCommand(String userCommand) {
		mAllUserCommands.add(userCommand);
	}

	protected String getOldUserCommand(int pos) {
		if (pos > mAllUserCommands.size()) {
			return mAllUserCommands.get(0);
		} else if (pos == 0) {
			return "";
		}
		return mAllUserCommands.get(mAllUserCommands.size()-pos);
	}

	protected void deleteAllUserCommands() {
		mAllUserCommands = new ArrayList<String>();
	}

	protected int getOldUserCommandSize() {
		return mAllUserCommands.size();
	}


}
