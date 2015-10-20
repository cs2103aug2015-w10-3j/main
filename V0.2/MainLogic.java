
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class MainLogic {

	private static String ENTER = "Enter";
    private String dataFile = "data6";

	CommandParser mTaskCommandParse = new CommandParser();
	private final String mMessageSuccessful = "Successful";
	private ArrayList<DataState> mHistory;
	private int mCurrentState;
	private ArrayList<Task> mAllTasks;
	private ArrayList<String> mAllUserCommands;
	private Storage mStorage;
	private int mSeparateLine = 75;
	
	private int mBigNum = 100000;

	
	public MainLogic() {
		//Initialise the variables;
		mStorage = new Storage();
		mStorage.setFileURL(dataFile);
		mAllUserCommands = new ArrayList<String>();

 		initialiseTasks();
	}

	protected void initialiseTasks(){
		mAllTasks = new ArrayList<Task>();
		mHistory = new ArrayList<DataState>();
		try{
			mAllTasks = mStorage.readContent();
			mHistory.add(new DataState(mAllTasks));
			mCurrentState = 0;
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	protected void updateHistory(){
		if (mCurrentState < mHistory.size()-1){
			while (mHistory.size()>mCurrentState+1)
				mHistory.remove(mCurrentState+1);
		}
		mHistory.add(new DataState(mAllTasks));
		mCurrentState++;
	}


	private class TaskPriorityCompare implements Comparator<Task> {

	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        String p1 = o1.getPriority();
	        String p2 = o2.getPriority();
	        if (p1.equals("low")) p1 = "n";
	        if (p2.equals("low")) p2 = "n";
	        
	        return p1.compareTo(p2);
	    }
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
	private class TaskGroupCompare implements Comparator<Task> {

	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	       	return o1.getGroup().compareTo(o2.getGroup());
	    }
	}
	protected String process(String userCommand) {

		addNewUserCommand(userCommand);

		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		assert commandInfo.length>0;
		command = commandInfo[0];  String field1 = commandInfo[1];
		taskInfo = field1;
		int count;
		String res;
		switch (command){
			case "add":
				Task newTask = new Task(field1);
				newTask.setDeadline(commandInfo[2]);
				newTask.setPriority(commandInfo[3]);
				newTask.setGroup(commandInfo[4]);
				newTask.setTaskInfo(getTaskInfo(userCommand));

				boolean isExisted = false;
				for(int i=0; i<mAllTasks.size(); i++) {
					if (mAllTasks.get(i).getTaskInfo().equals(getTaskInfo(userCommand))) {
						isExisted = true; 
						break;
					}
				}
				if (isExisted) {
					return "This task already existed!\n";
				}
				mAllTasks.add(newTask);
				updateHistory();
				mStorage.rewriteContent(mAllTasks);
				return "Successfully added '" + newTask.getDisplay() + "'\n\n" + showAll(mAllTasks);
			case "showall":
				if (mAllTasks.size() > 0) {
					return showAll(mAllTasks);
				} else {
					return "You don't have any tasks! Use 'add' to add a new task!\n";
				}
			case "delete":
				String message = "Do you mean one of these? \n" + getSeparateLine();
				int numberMatched = 0, position = 0;
				String deleted = "";
				for (int i=0;i<mAllTasks.size();i++){
					String taskName = mAllTasks.get(i).getTaskInfo();
					if (taskName.equals(taskInfo)){
						mAllTasks.remove(i);
						updateHistory();
						mStorage.rewriteContent(mAllTasks);
						return "'" + taskInfo + "' was removed successfully!\n\n" + showAll(mAllTasks);
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
						mStorage.rewriteContent(mAllTasks);
						return "'" + deleted + "' was removed successfully!\n\n" + showAll(mAllTasks);
					}
					return message;
				}
				return "Error: task '" + taskInfo +"' not found.\n\n" + showAll(mAllTasks);

			case "update":
				String updated;
				String [] arguments = taskInfo.split(" ");

				if (arguments.length != 2) return "Syntax error\n";
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getName().equals(arguments[0])){
						updated = mAllTasks.get(i).getName();
						mAllTasks.get(i).setName(arguments[1]);

						updateHistory();
						mStorage.rewriteContent(mAllTasks);

						return "'" + updated + "' was updated successfully to '" + arguments[1] + "'\n\n" + showAll(mAllTasks);
					}
				}
				return "Error: task '" + taskInfo +"' not found.\n\n" + showAll(mAllTasks);
			case "showby":
				if (mAllTasks.size() == 0) {
					return "Nothing to show! Use 'add' to add a new task!\n";
				}			
				ArrayList<Task> tempTasks;
				switch (field1){
					case "deadline":
						tempTasks = duplicate(mAllTasks);
						Collections.sort(tempTasks,new TaskDeadlineCompare());
						if (tempTasks.size() > 0) {
							return showAll(tempTasks);
						} else {
							return "Nothing to show! Use 'add' to add a new task!\n";
						}
					case "priority":
						tempTasks = duplicate(mAllTasks);
						Collections.sort(tempTasks,new TaskPriorityCompare());
						return showAll(tempTasks);
					case "group":
						tempTasks = duplicate(mAllTasks);
						Collections.sort(tempTasks,new TaskGroupCompare());
						if (tempTasks.size() > 0) {
							return showAll(tempTasks);
						} else {
							return "Nothing to show! Use 'add' to add a new task!\n";
						}
					default:break;
				}
			case "showday":
				res = getSeparateLine();
				count = 0;
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getDeadlineString().equals(field1)){
						res += mAllTasks.get(i).getDisplay() + getSeparateLine();
						count++;
					}
				}
				if (count == 0) {
					return "Nothing to show! Using 'showall' to show all your tasks! \n";
				} 
				return res;

			case "showpriority":
				res = getSeparateLine();
				count = 0;
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getPriority().equals(field1)){
						res += mAllTasks.get(i).getDisplay() + getSeparateLine();
						count++;
					}
				}
				if (count == 0) {
					return "Nothing to show! Using 'showall' to show all your tasks! \n";
				} 
				return res;
			case "showgroup":
				res = getSeparateLine();
				count = 0;
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getGroup().equals(field1)){
						res += mAllTasks.get(i).getDisplay() + getSeparateLine();
						count++;
					}
				}
				if (count == 0) {
					return "Nothing to show! Using 'showall' to show all your tasks! \n";
				} 
				return res;
			case "setfile":
				mStorage.setFileURL(field1);
				initialiseTasks();
				return "Successfully changed data file to "+ field1+".\n";
			case "undo":
				if (mCurrentState>0) {
					mCurrentState--;
					mAllTasks = mHistory.get(mCurrentState).getAllTasks();
					mStorage.rewriteContent(mAllTasks);
					return "undid successfully\n";
				}else{
					return "Nothing to be undone !\n";
				}
			case "redo":
				if (mCurrentState < mHistory.size()-1){
					mCurrentState++;
					mAllTasks = mHistory.get(mCurrentState).getAllTasks();
					mStorage.rewriteContent(mAllTasks);
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

	private String getTaskInfo(String userCommand) {
		String[] splits = userCommand.split(" ");
		String result = "";
		for(int i = 1; i < splits.length; i++) {
			if (i > 1) {
				result += " ";
			}
			result += splits[i];
		}
		return result;
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
			String[] args = mAllTasks.get(i).getTaskInfo().split(" ");
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
		int count = 0;
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
				count++;
				result = result + mAllTasks.get(id).getDisplay() + getSeparateLine();
			}
		}
		if (count == 0) {
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
		if (tasks.size() == 0) {
			return "";
		}
		String result = "These are all your tasks!\n" + getSeparateLine();
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
