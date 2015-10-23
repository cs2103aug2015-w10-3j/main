
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class MainLogic {

	//constants
	private static final String ENTER = "Enter";
    private static final String SETTINGS_FILE = "settings.text";
    private static final String MESSAGE_SUCCESSFULL = "Successful";
    private static final int SEPARATE_LINE = 75;
	private static final int BIG_NUM = 100000;
	private static final String MESSAGE_NO_TASK_AFTER_SHOW_FILTER = "Nothing to show !";
	private static final String MESSAGE_NO_TASK_AFTER_SHOWBY = "Nothing to show! Use 'add' to add a new task!";
	
	//standard day format
	private static SimpleDateFormat standardDayFormat = new SimpleDateFormat("dd/MM");
	
	//the parser and the storage objects
	private CommandParser mCommandParser;
	private Storage mDataStorage;
	private Storage mSettingsStorage;

	//global variables for storing internal data
	private ArrayList<DataState> mHistory;
	private int mCurrentState;
	private ArrayList<Task> mAllTasks;
	private ArrayList<String> mAllUserCommands;
	private Settings mSettings;

	public MainLogic() {
		//Initialise the variables;
		mCommandParser = new CommandParser();
		
		mSettingsStorage = new Storage();
		mSettingsStorage.setFileURL(SETTINGS_FILE);
		mSettings = mSettingsStorage.readSettings();

		mDataStorage = new Storage();
		mDataStorage.setFileURL(mSettings.getDataFileUrl());
		
		mAllUserCommands = new ArrayList<String>();
 		initialiseTasks();
	}


	
	/**
	 * This function initilises the mAllTasks with data read from mDataStorage.
	 * It also initialises the mHistory and mCurrentState
	 * 
	 * */
	protected void initialiseTasks(){
		mAllTasks = new ArrayList<Task>();
		mHistory = new ArrayList<DataState>();
		try{
			mAllTasks = mDataStorage.readContent();
			mHistory.add(new DataState(mAllTasks));
			mCurrentState = 0;
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This function update the mHistory after each change in mAllTasks.
	 * So that we could redo/undo changes in mAllTasks
	 * 
	 * */
	protected void updateHistory(){
		if (mCurrentState < mHistory.size()-1){
			while (mHistory.size()>mCurrentState+1)
				mHistory.remove(mCurrentState+1);
		}
		mHistory.add(new DataState(mAllTasks));
		mCurrentState++;
	}

	/**
	 * These are the comparator classes for comparing Task according to different fields
	 * 
	 * */
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
	        if (o1.getDeadline()==null) return 1;
	        if (o2.getDeadline()==null) return -1;
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

	protected String executeAdd(Command mCommand,ArrayList<Task> feedbackTasks){
		boolean isExisted = false;
		Task newTask = mCommand.getNewTask();

		for(int i=0; i<mAllTasks.size(); i++) {
			if (mAllTasks.get(i).getName().equals(newTask.getName()) ) {
				isExisted = true; 
				break;
			}
		}

		if (isExisted) {
			feedbackTasks = mAllTasks;
			return "This task already existed!";
		}

		mAllTasks.add(newTask);
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		
		return "Successfully added '" + newTask.getName();
	}

	protected String executeDelete(Command mCommand, ArrayList<Task> feedbackTasks){
		int numberMatched = 0, position = 0;
		String deletedTask = "";
		String taskToBeDeleted = mCommand.getCommandArgument();
		ArrayList<Task> possibleMatches = new ArrayList<Task>();

		for (int i=0;i<mAllTasks.size();i++){
			String taskName = mAllTasks.get(i).getName();
			if (taskName.equals(taskToBeDeleted)){
				mAllTasks.remove(i);
				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);
				return "'" + taskName + "' was removed successfully!";
			}
			if (taskName.startsWith(taskToBeDeleted)) {
				numberMatched++;
				deletedTask = taskName;
				position = i;
				possibleMatches.add(mAllTasks.get(i));
			}
		}

		if (numberMatched > 0) {
			if (numberMatched == 1) {
				mAllTasks.remove(position);
				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);
				return "'" + deletedTask+ "' was removed successfully!";
			}
			feedbackTasks = possibleMatches;
			return "Do you mean one of the above ? Please be more specific.";
		}
		return "Error: task '" + taskToBeDeleted +"' not found.";
	}

	protected String executeUpdate(Command mCommand, ArrayList<Task> feedbackTasks){
		Task updatedInfo = mCommand.getUpdatedTask();
		String taskToBeUpdated = mCommand.getCommandArgument();

		for (int i=0;i<mAllTasks.size();i++){
			if ( mAllTasks.get(i).getName().equals(taskToBeUpdated) ){
				
				Task mTask = mAllTasks.get(i);

				if ( !updatedInfo.getName().equals("") ){
					mTask.setName(updatedInfo.getName());					
				}
				
				if ( !updatedInfo.getGroup().equals("") ){
					mTask.setGroup(updatedInfo.getGroup());					
				}

				if ( !updatedInfo.getPriority().equals("") ){
					mTask.setPriority(updatedInfo.getPriority());					
				}

				if ( updatedInfo.getStartDate() != null ){
					mTask.setStartDate(updatedInfo.getStartDate());					
				}

				if ( updatedInfo.getEndDate() != null ){
					mTask.setEndDate(updatedInfo.getEndDate());					
				}

				if ( updatedInfo.getDeadline() != null ){
					mTask.setDeadline(updatedInfo.getDeadline());					
				}

				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);

				return "'" + mTask.getName() + "' was updated successfully." ;
			}
		}

		return "Error: task '" + taskToBeUpdated +"' not found.";
	}

	protected String executeShowby(Command mCommand, ArrayList<Task> feedbackTasks){
		if (mAllTasks.size() == 0) {
			return MESSAGE_NO_TASK_AFTER_SHOWBY;
		}

		feedbackTasks = duplicate(mAllTasks);

		switch (mCommand.getCommandArgument()){
			case "deadline":
				Collections.sort(feedbackTasks,new TaskDeadlineCompare());
				return "Displaying tasks in the order of their deadlines.";

			case "priority":
				Collections.sort(feedbackTasks,new TaskPriorityCompare());
				return "Displaying tasks in the order of their priority.";

			case "group":
				Collections.sort(feedbackTasks,new TaskGroupCompare());
				return "Displaying tasks according to their group.";

			default:
				return "Syntax not recognized. Use 'showby deadline' or 'showby priority' or 'showby group'";
				
		}
	}

	protected String executeShow(Command mCommand, ArrayList<Task> feedbackTasks){
		feedbackTasks = new ArrayList<Task>();
		String argument = mCommand.getCommandArgument();

		for (int i=0;i<mAllTasks.size();i++){
			switch (mCommand.getCommandType()){
				case "showday":
					Date mDeadline = mAllTasks.get(i).getDeadline();

					if ( standardDayFormat.format(mDeadline).equals(argument) ){
						feedbackTasks.add(mAllTasks.get(i));
					}
					break;

				case "showpriority":
					if (mAllTasks.get(i).getPriority().equals(argument)){
						feedbackTasks.add(mAllTasks.get(i));	
					}
					break;

				case "showgroup":
					if (mAllTasks.get(i).getGroup().equals(argument)){
						feedbackTasks.add(mAllTasks.get(i));	
					}
					break;

				default: break;
			}
		}

		if (feedbackTasks.size()==0){
			feedbackTasks = mAllTasks;
			return MESSAGE_NO_TASK_AFTER_SHOW_FILTER;
		}
		return "These are your tasks for " + mCommand.getCommandType().substring(4) + " " + argument;
	}

	protected String executeSetFile(Command mCommand, ArrayList<Task> feedbackTasks){
		//update the internal settings object and save it to the setting file.
		mSettings.setDataFileUrl(mCommand.getCommandArgument());
		mSettingsStorage.writeSettings(mSettings);

		//Set the new file URL for the dataStorage and reload tasks;
		mDataStorage.setFileURL(mCommand.getCommandArgument());
		initialiseTasks();

		return "Successfully changed data file to "+ mCommand.getCommandArgument();
	}

	protected String executeUndo(Command mCommand, ArrayList<Task> feedbackTasks){
		if (mCurrentState>0) {
			mCurrentState--;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks = mAllTasks;
			return "undid successfully";
		} else {
			return "Nothing to be undone !";
		}
	}

	protected String executeRedo(Command mCommand, ArrayList<Task> feedbackTasks){
		if (mCurrentState < mHistory.size()-1){
			mCurrentState++;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks = mAllTasks;
			return "redid successfully";
		}
		else return "No more operations to redo !";
	}

	protected String executeSearch(Command mCommand, ArrayList<Task> feedbackTasks) {
		String[] arguments = mCommand.getCommandArgument().split(" ");

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

			if (task.contains(mCommand.getCommandArgument())) {
				matchCount.get(i).count = BIG_NUM;
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

		feedbackTasks = new ArrayList<Task>();

		boolean isMatched = false;
		for(int i=0; i<Math.min(matchCount.size(), 10); i++) {
			if (i == 0 && matchCount.get(i).count == 0) {
				//Nothing matched!
				break;
			}
			int point = matchCount.get(i).count;
			if (point >= BIG_NUM) {
				isMatched = true;
			}
			if (point < BIG_NUM && isMatched) {
				break;
			}
			if (point > 0) {
				int id = matchCount.get(i).id;
				feedbackTasks.add(mAllTasks.get(id));
			}
		}

		if (feedbackTasks.size()==0) {
			feedbackTasks = mAllTasks;
			result = MESSAGE_NO_TASK_AFTER_SHOW_FILTER;
		}

		return "Showing results for '" + mCommand.getCommandArgument() + "'";

	}

	/**
	 * the main function for the UI to call to execute a command
	 * returns a feedback String and an ArrayList of Task in feedbackTasks
	 * 
	 * */
	protected String process(String userCommand, ArrayList<Task> feedbackTasks) {

		addNewUserCommand(userCommand);
		Command mCommand = mCommandParser.parse(userCommand);

		//For most of the times the tasks to be displayed after each command is all the tasks.
		//if it is otherwise, later codes will modify this pointer
		feedbackTasks = mAllTasks;

		switch (mCommand.getCommandType()){
			case "add":
				return executeAdd(mCommand,feedbackTasks);
				
			case "showall":
				return "These are all the tasks";

			case "delete":
				return executeDelete(mCommand,feedbackTasks);

			case "update":
				return executeUpdate(mCommand,feedbackTasks);
				
			case "showby":
				return executeShowby(mCommand,feedbackTasks);
			
			case "showday":
			case "showpriority":
			case "showgroup":
				return executeShow(mCommand,feedbackTasks);

			case "setfile":
				return executeSetFile(mCommand,feedbackTasks);

			case "undo":
				return executeUndo(mCommand,feedbackTasks);

			case "redo":
				return executeRedo(mCommand,feedbackTasks);
			
			case "show":
			case "search":
				return executeSearch(mCommand,feedbackTasks);

			case "exit":
				return null;

			default:
				return "Syntax error: command(" + mCommand.getCommandType() + ") not found.";
		}
		
	}

	// private String getTaskInfo(String userCommand) {
	// 	String[] splits = userCommand.split(" ");
	// 	String result = "";
	// 	for(int i = 1; i < splits.length; i++) {
	// 		if (i > 1) {
	// 			result += " ";
	// 		}
	// 		result += splits[i];
	// 	}
	// 	return result;
	// }
	
	//supported simple search API

	private class MatchCount {
		int id, count;
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

	private String getSeparateLine() {
		String result = "";
		for(int i=0; i<SEPARATE_LINE; i++) {
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
