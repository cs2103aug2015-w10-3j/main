
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class MainLogic {

   	private final String PRIORITY_LOW = "low";

	CommandParser mTaskCommandParse = new CommandParser();

	//constants
	private static final String ENTER = "Enter";
    private static final String SETTINGS_FILE = "settings.txt";
    private static final int SEPARATE_LINE = 75;
	private static final int BIG_NUM = 100000;
	
	//standard day format
	private static SimpleDateFormat standardDayFormat = new SimpleDateFormat("dd/MM");
	private static SimpleDateFormat standardTimeFormat = new SimpleDateFormat("dd/MM hh:mm");
	
	//the parser and the storage objects
	private CommandParser mCommandParser;
	private Storage mDataStorage;
	private Storage mSettingsStorage;

	//global variables for storing internal data
	private ArrayList<DataState> mHistory;
	private int mCurrentState;
	private ArrayList<Task> mAllTasks;
	private ArrayList<String> mAllUserCommands;
	private Storage mStorage;
	
	private Settings mSettings = new Settings();

	public MainLogic() {
		//Initialise the variables;
		mCommandParser = new CommandParser();
		
		mSettingsStorage = new Storage();
		mSettingsStorage.setFileURL(SETTINGS_FILE);
		//mSettings = mSettingsStorage.readSettings();
		mSettings.setDataFileUrl("data1.txt");
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
	        if (p1.equals(PRIORITY_LOW)) p1 = "n";
	        if (p2.equals(PRIORITY_LOW)) p2 = "n";
	        return p1.compareTo(p2);
	    }
	}
	private class TaskDeadlineCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getDeadline().equals("")) return 1;
	        if (o2.getDeadline().equals("")) return -1;
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

	protected String executeAdd(Command mCommand, ArrayList<Task> feedbackTasks){
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
			return AppConst.MESSAGE.TASK_EXISTS;
		}

		Date date = new Date();
		String currentTime = standardTimeFormat.format(date);
		String newTaskDeadline = newTask.getDeadline();
		if (!newTaskDeadline.equals("") && newTaskDeadline!=null) {
			if (currentTime.compareTo(newTaskDeadline)>0) {
				return AppConst.MESSAGE.INVALID_DEADLINE;
			}
		}

		mAllTasks.add(newTask);
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		
		return String.format(AppConst.MESSAGE.TASK_ADDED, newTask.getTaskInfo());
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
				return String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, taskName);
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
				return String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, deletedTask);
			}
			feedbackTasks = possibleMatches;
			return AppConst.MESSAGE.MANY_TASKS_MATCHED;
		}
		return String.format(AppConst.MESSAGE.TASK_NOT_FOUND, taskToBeDeleted);
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

				return String.format(AppConst.MESSAGE.UPDATED_SUCCESSFUL, taskToBeUpdated);
			}
		}

		return String.format(AppConst.MESSAGE.TASK_NOT_FOUND, taskToBeUpdated);
	}

	protected String executeShowby(Command mCommand, ArrayList<Task> feedbackTasks){
		if (mAllTasks.size() == 0) {
			return AppConst.MESSAGE.NO_TASK_FOUND;
		}

		feedbackTasks = duplicate(mAllTasks);

		switch (mCommand.getCommandArgument()){
			case AppConst.TASK_FIELD.DEADLINE:
				Collections.sort(feedbackTasks,new TaskDeadlineCompare());
				return AppConst.MESSAGE.DISPLAY_BY_DEADLINE;

			case AppConst.TASK_FIELD.PRIORITY:
				Collections.sort(feedbackTasks,new TaskPriorityCompare());
				return AppConst.MESSAGE.DISPLAY_BY_PRIORITY;

			case AppConst.TASK_FIELD.GROUP:
				Collections.sort(feedbackTasks,new TaskGroupCompare());
				return AppConst.MESSAGE.DISPLAY_BY_GROUP;

			default:
				return AppConst.MESSAGE.NOT_RECOGNIZED_SYNTAX;
				
		}
	}

	protected String executeShow(Command mCommand, ArrayList<Task> feedbackTasks){
		feedbackTasks = new ArrayList<Task>();
		String argument = mCommand.getCommandArgument();

		for (int i=0;i<mAllTasks.size();i++){
			switch (mCommand.getCommandType()){
				case AppConst.COMMAND_TYPE.SHOW_DAY:
					String deadline = mAllTasks.get(i).getDeadline();

					if (deadline.equals(argument) ){
						feedbackTasks.add(mAllTasks.get(i));
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_PRIORITY:
					if (mAllTasks.get(i).getPriority().equals(argument)){
						feedbackTasks.add(mAllTasks.get(i));	
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_GROUP:
					if (mAllTasks.get(i).getGroup().equals(argument)){
						feedbackTasks.add(mAllTasks.get(i));	
					}
					break;

				default: break;
			}
		}

		if (feedbackTasks.size()==0){
			feedbackTasks = mAllTasks;
			return String.format(AppConst.MESSAGE.NOTHING_TO_SHOW, argument);
		}
		return String.format(AppConst.MESSAGE.SHOWING_TASK, mCommand.getCommandType().substring(4), argument);
	}

	protected String executeSetFile(Command mCommand, ArrayList<Task> feedbackTasks){
		//update the internal settings object and save it to the setting file.
		mSettings.setDataFileUrl(mCommand.getCommandArgument());
		mSettingsStorage.writeSettings(mSettings);

		//Set the new file URL for the dataStorage and reload tasks;
		mDataStorage.setFileURL(mCommand.getCommandArgument());
		initialiseTasks();

		return String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, mCommand.getCommandArgument());
	}
	
	protected String executeUndo(Command mCommand, ArrayList<Task> feedbackTasks){
		if (mCurrentState>0) {
			mCurrentState--;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks = mAllTasks;
			return AppConst.MESSAGE.UNDID_SUCCESSFUL;
		}
		return AppConst.MESSAGE.NOTHING_UNDONE;
	}

	protected String executeRedo(Command mCommand, ArrayList<Task> feedbackTasks){
		if (mCurrentState < mHistory.size()-1){
			mCurrentState++;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks = mAllTasks;
			return AppConst.MESSAGE.REDID_SUCCESSFUL;
		}
		return AppConst.MESSAGE.NOTHING_REDONE;
	}

	protected String executeSearch(Command mCommand, ArrayList<Task> feedbackTasks) {
		String[] arguments = mCommand.getCommandArgument().split(" ");

		ArrayList<MatchCount> matchCount = new ArrayList<MatchCount>();
		
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
		String result = "";

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
			return AppConst.MESSAGE.NOTHING_MATCHED;
		}

		return String.format(AppConst.MESSAGE.SHOWING_RESULT, mCommand.getCommandArgument());

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
			case AppConst.COMMAND_TYPE.ADD:
				return executeAdd(mCommand,feedbackTasks);
				
			case AppConst.COMMAND_TYPE.SHOW_ALL:
				return AppConst.MESSAGE.TASK_TO_DO;

			case AppConst.COMMAND_TYPE.DELETE:
				return executeDelete(mCommand,feedbackTasks);

			case AppConst.COMMAND_TYPE.UPDATE:
				return executeUpdate(mCommand,feedbackTasks);
				
			case AppConst.COMMAND_TYPE.SHOW_BY:
				return executeShowby(mCommand,feedbackTasks);
			
			case AppConst.COMMAND_TYPE.SHOW_DAY:
			case AppConst.COMMAND_TYPE.SHOW_PRIORITY:
			case AppConst.COMMAND_TYPE.SHOW_GROUP:
				return executeShow(mCommand,feedbackTasks);

			case AppConst.COMMAND_TYPE.SET_FILE:
				return executeSetFile(mCommand,feedbackTasks);

			case AppConst.COMMAND_TYPE.UNDO:
				return executeUndo(mCommand,feedbackTasks);

			case AppConst.COMMAND_TYPE.REDO:
				return executeRedo(mCommand,feedbackTasks);
			
			case AppConst.COMMAND_TYPE.SHOW:
			case AppConst.COMMAND_TYPE.SEARCH:
				return executeSearch(mCommand,feedbackTasks);

			case AppConst.COMMAND_TYPE.EXIT:
				return null;

			default:
				return String.format(AppConst.MESSAGE.SYNTAX_ERROR, mCommand.getCommandType());
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
