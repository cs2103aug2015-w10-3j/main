
import java.io.*;
import java.util.*;
import java.lang.*;

public class MainLogic {

	CommandParser mTaskCommandParse = new CommandParser();

	//constants
	private static final String ENTER = "Enter";
    private static final String SETTINGS_FILE = "settings.txt";
    private static final int SEPARATE_LINE = 75;
	private static final int BIG_NUM = 100000;
	
	//the parser and the storage objects
	private CommandParser mCommandParser;
	private Storage mDataStorage;
	private Storage mSettingsStorage;

	//global variables for storing internal data
	private ArrayList<DataState> mHistory;
	private int mCurrentState;
	private ArrayList<Task> mAllTasks;
	private ArrayList<Task> mPreviousTasks;
	private ArrayList<String> mAllUserCommands;
	private Storage mStorage;
	private DateTimeHelper mDateTimeHelper;
	
	private Settings mSettings = new Settings();

	public MainLogic() {
		
		mDateTimeHelper = new DateTimeHelper();
		
		//Initialise the variables;
		mCommandParser = new CommandParser();
		
		mSettingsStorage = new Storage();
		mSettingsStorage.setFileURL(SETTINGS_FILE);
		//mSettings = mSettingsStorage.readSettings();
		mSettings.setDataFileUrl("mike_nusmods.txt");
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
	        if (p1.equals(AppConst.TASK_FIELD.LOW)) p1 = "n";
	        if (p2.equals(AppConst.TASK_FIELD.LOW)) p2 = "n";
	        return p1.compareTo(p2);
	    }
	}
	private class TaskDeadlineCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getDeadline().equals("")) {
	        	return 1;
	        }
	        if (o2.getDeadline().equals("")) {
	        	return -1;
	        }
	        return mDateTimeHelper.compareStringDates(o1.getDeadline(), o2.getDeadline());
	    }
	}
	
	private class TaskStartDateCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getStartDate().equals("")) {
	        	return 1;
	        }
	        if (o2.getStartDate().equals("")) {
	        	return -1;
	        }
	        return mDateTimeHelper.compareStringDates(o1.getStartDate(), o2.getStartDate());
	    }
	}
	
	
	private class TaskEndDateCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getEndDate().equals("")) {
	        	return 1;
	        }
	        if (o2.getEndDate().equals("")) {
	        	return -1;
	        }
	        return mDateTimeHelper.compareStringDates(o1.getEndDate(), o2.getEndDate());
	    }
	}
	
	
	private class TaskGroupCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	       	return o1.getGroup().compareTo(o2.getGroup());
	    }
	}

	protected String executeAdd(Command mCommand,  ArrayListPointer feedbackTasks){
		Task newTask = mCommand.getNewTask();
		
		if (newTask.getDeadline() == null || newTask.getStartDate() == null || newTask.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (newTask.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;
		}

		if (newTask.getName() == null || newTask.getName().equals("")) {
			return AppConst.MESSAGE.INVALID_TASK_NAME;
		}

		for(int i=0; i<mAllTasks.size(); i++) {
			if (isTasksMatched(newTask, mAllTasks.get(i)) == 1) {
				return AppConst.MESSAGE.TASK_EXISTS;
			}
		}

		// Check for valid deadline, end date and start date
		String deadline = newTask.getDeadline();
		String startDate = newTask.getStartDate();
		String endDate = newTask.getEndDate();
		String currentTime = mDateTimeHelper.getCurrentTimeString();
		
		//if (!startDate.equals("") && mDateTimeHelper.compareStringDates(currentTime, startDate)>0) {
		//	return String.format(AppConst.MESSAGE.INVALID_START_DATE, currentTime);		
		//}
			
		if (newTask.getRepeatedType() == AppConst.REPEATED_TYPE.NONE || newTask.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO) { 
		
			if (!deadline.equals("") && !startDate.equals("")) {
				if (mDateTimeHelper.compareStringDates(startDate, deadline)>0) {
					return String.format(AppConst.MESSAGE.INVALID_DEADLINE, currentTime);
				}
			}
		
			if (!endDate.equals("") && !startDate.equals("")) {
				if (mDateTimeHelper.compareStringDates(startDate, endDate)>0) {
					return String.format(AppConst.MESSAGE.INVALID_DEADLINE, currentTime);
				}
			}
		
		}
		if (isCheckOverlap(newTask, -1)) {
			return AppConst.MESSAGE.OVERLAP_TIME_PERIOD;
		}

		mAllTasks.add(newTask);
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		
		return String.format(AppConst.MESSAGE.TASK_ADDED, newTask.getTaskInfo());
	}

	protected String executeDelete(Command mCommand,  ArrayListPointer feedbackTasks){
	
		int position = 0;
		feedbackTasks.setPointer(mAllTasks);
		Task taskToDelete = mCommand.getNewTask();
		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length == 2 && commands[0].equals("id")) {
			taskToDelete = getTaskWithId(commands[1]);
		}
		if (taskToDelete == null) {
			return "";
		}
		if (taskToDelete.getDeadline() == null || taskToDelete.getStartDate() == null || taskToDelete.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (taskToDelete.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;
		}
		ArrayList<Task> possibleTasks = new ArrayList<Task>();
		for(int i=0; i<mAllTasks.size(); i++) {
			int x = isTasksMatched(taskToDelete, mAllTasks.get(i));
			if (x == 1) {
				mAllTasks.remove(i);
				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);
				feedbackTasks.setPointer(mAllTasks);
				return String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, taskToDelete.getTaskInfo());
			} else if (x == 0) {
				position = i;
				possibleTasks.add(mAllTasks.get(i));
			}
		}
		if (possibleTasks.size() == 0) {
			return AppConst.MESSAGE.NOTHING_MATCHED;
		} else if (possibleTasks.size() == 1) {
			mAllTasks.remove(position);
			updateHistory();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(mAllTasks);
			return String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, taskToDelete.getTaskInfo());
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
		
	}
	
	protected String executeDeleteAll(Command mCommand, ArrayListPointer feedbackTasks) {
		if (mAllTasks.size() == 0) {
			feedbackTasks.setPointer(mAllTasks);
			return AppConst.MESSAGE.NO_TASK_TO_DELETE;
		}
		mAllTasks = new ArrayList<Task>();
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		feedbackTasks.setPointer(mAllTasks);
		return AppConst.MESSAGE.DELETED_ALL;
	}
	
	protected String executeDeleteBy(Command mCommand, ArrayListPointer feedbackTasks) {
		String fieldToDelete = mCommand.getCommandArgument();
		String[] field = fieldToDelete.split(" ");
		String userCommand = mCommand.getCommandType() + " " + fieldToDelete;
		feedbackTasks.setPointer(mAllTasks);
		if (field.length < 2) {
			return String.format(AppConst.MESSAGE.SYNTAX_ERROR, userCommand);
		}
		String message = String.format(AppConst.MESSAGE.DELETED_FIELD, field[0], field[1]);
		ArrayList<Task> remainTaskList = new ArrayList<Task>();
		
		String taskField = field[0];
		String taskFieldArg = field[1];
		
		switch (field[0]) {
			case AppConst.TASK_FIELD.STATUS:
				if (!field[1].equals(AppConst.TASK_FIELD.DONE) && !field[1].equals(AppConst.TASK_FIELD.UNDONE) || field.length!=2) {
					return String.format(AppConst.MESSAGE.SYNTAX_ERROR, userCommand);
				}
				for(int i=0; i<mAllTasks.size(); i++) {
					if (!mAllTasks.get(i).getStatus().equals(field[1])) {
						remainTaskList.add(mAllTasks.get(i));	
					}
				}
				break;
			case AppConst.TASK_FIELD.PRIORITY:
				if (!field[1].equals(AppConst.TASK_FIELD.HIGH) && !field[1].equals(AppConst.TASK_FIELD.MEDIUM) && !field[1].equals(AppConst.TASK_FIELD.LOW) || field.length!=2) {
					return String.format(AppConst.MESSAGE.SYNTAX_ERROR, userCommand);
				}
				for(int i=0; i<mAllTasks.size(); i++) {
					if (!mAllTasks.get(i).getPriority().equals(field[1])) {
						remainTaskList.add(mAllTasks.get(i));	
					}
				}
				break;
			case AppConst.TASK_FIELD.GROUP:
				String groupName = "";
				for(int i=1; i<field.length; i++) {
					if (i>1) {
						groupName += " ";
					}
					groupName += field[i];
				}
				taskFieldArg = groupName;
				for(int i=0; i<mAllTasks.size(); i++) {
					if (!mAllTasks.get(i).getGroup().equals(groupName)) {
						remainTaskList.add(mAllTasks.get(i));	
					}
				}
				break;
			case AppConst.TASK_FIELD.DEADLINE:
				String day = "";
				for(int i=1; i<field.length; i++) {
					if (i > 1) {
						day += " ";
					}
					day += field[i];
				}
				taskFieldArg = day;
				String date =  mDateTimeHelper.getDateMonthFromString(day, 2);
				if (date == null || date.equals("")) {
					feedbackTasks.setPointer(new ArrayList<Task>());
					
					return AppConst.MESSAGE.INVALID_DAY;
				}
				for(int i=0; i<mAllTasks.size(); i++) {
					String deadline = mAllTasks.get(i).getDeadline();
					if (!deadline.equals("")) {
						if (!deadline.substring(0, 5).equals(date)) {
							remainTaskList.add(mAllTasks.get(i));
						}
					} else {
						remainTaskList.add(mAllTasks.get(i));
					} 
				}
				break;
			default:
				return String.format(AppConst.MESSAGE.SYNTAX_ERROR, userCommand);
		}
		if (mAllTasks.size() == remainTaskList.size()) {
			message = String.format(AppConst.MESSAGE.DELETED_NO_TASK, taskField, taskFieldArg);
		}
		mAllTasks = remainTaskList;
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		feedbackTasks.setPointer(mAllTasks);
		return message;
	}

	protected String executeUpdate(Command mCommand,  ArrayListPointer feedbackTasks) {
		Task updatedInfo = mCommand.getUpdatedTask();
		Task taskToBeUpdated = mCommand.getNewTask();
		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length == 2 && commands[0].equals("id")) {
			taskToBeUpdated = getTaskWithId(commands[1]);
		}
		feedbackTasks.setPointer(mAllTasks);
		if (updatedInfo == null || taskToBeUpdated == null) {
			return AppConst.MESSAGE.INVALID_UPDATE_FORMAT;
		}
		
		if (updatedInfo.getDeadline() == null || updatedInfo.getStartDate() == null || updatedInfo.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (updatedInfo.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;		
		}
		
		if (taskToBeUpdated.getDeadline() == null || taskToBeUpdated.getStartDate() == null || taskToBeUpdated.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (taskToBeUpdated.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;		
		}
		
		
		// Check deadline, start/end date for new version
		String deadline = updatedInfo.getDeadline();
		String startDate = updatedInfo.getStartDate();
		String endDate = updatedInfo.getEndDate();
		String currentTime = mDateTimeHelper.getCurrentTimeString();
		
		//if (!startDate.equals("") && mDateTimeHelper.compareStringDates(currentTime, startDate)>0) {
		//	return String.format(AppConst.MESSAGE.INVALID_START_DATE, currentTime);		
		//}
		
		if (updatedInfo.getRepeatedType() == AppConst.REPEATED_TYPE.NONE || updatedInfo.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO) {
		
			if (!deadline.equals("") && !startDate.equals("")) {
				if (mDateTimeHelper.compareStringDates(startDate, deadline)>0) {
					return String.format(AppConst.MESSAGE.INVALID_DEADLINE, currentTime);
				}
			}
		
			if (!endDate.equals("") && !startDate.equals("")) {
				if (mDateTimeHelper.compareStringDates(startDate, endDate)>0) {
					return String.format(AppConst.MESSAGE.INVALID_DEADLINE, currentTime);
				}
			}
		}
		
		int position = 0;
		ArrayList<Task> possibleTasks = new ArrayList<Task>();
		for(int i=0; i<mAllTasks.size(); i++) {
			int x = isTasksMatched(taskToBeUpdated, mAllTasks.get(i));
			if (x == 1) {
				position = i;
				possibleTasks = new ArrayList<Task>();
				possibleTasks.add(mAllTasks.get(i));
				break;
			} else if (x == 0) {
				position = i;
				possibleTasks.add(mAllTasks.get(i));
			}
		}
		
		if (possibleTasks.size() == 0) {
			return String.format(AppConst.MESSAGE.TASK_NOT_FOUND, taskToBeUpdated.getTaskInfo());
		} else if (possibleTasks.size() == 1) {
		
			Task mTask = mAllTasks.get(position);

			if ( updatedInfo.getName().equals("") ){
				updatedInfo.setName(mTask.getName());					
			}
				
			String taskInfo = " " + updatedInfo.getTaskInfo() + " ";
			
			if ( updatedInfo.getGroup().equals("") && !taskInfo.contains(" group ") && !taskInfo.contains(" grp ")) {
				updatedInfo.setGroup(mTask.getGroup());					
			}

			if ( updatedInfo.getPriority().equals("")){
				updatedInfo.setPriority(mTask.getPriority());					
			}

			if ( updatedInfo.getStartDate().equals("") && !taskInfo.contains(" from ")){
				updatedInfo.setStartDate(mTask.getStartDate());					
			}

			if ( updatedInfo.getEndDate().equals("") && !taskInfo.contains(" to ")){
				updatedInfo.setEndDate(mTask.getEndDate());					
			}

			if ( updatedInfo.getDeadline().equals("") && !taskInfo.contains(" by ") && !taskInfo.contains(" before ")) {
				updatedInfo.setDeadline(mTask.getDeadline());					
			}
			
			if (!taskInfo.contains(" repeat ")) {
				updatedInfo.setRepeatedType(mTask.getRepeatedType());
				updatedInfo.setPeriod(mTask.getPeriod());
			}
			
			updatedInfo.setStatus(mTask.getStatus());
			
			for(int i=0; i<mAllTasks.size(); i++) {
				if (isTasksMatched(updatedInfo, mAllTasks.get(i)) == 1) {
					return AppConst.MESSAGE.TASK_UPDATED_EXIST;
				}
			}
			
			if (isCheckOverlap(updatedInfo, position)) {
				return AppConst.MESSAGE.OVERLAP_TIME_PERIOD;
			}
			
			mTask.setName(updatedInfo.getName());
			mTask.setDeadline(updatedInfo.getDeadline());
			mTask.setStartDate(updatedInfo.getStartDate());
			mTask.setEndDate(updatedInfo.getEndDate());
			mTask.setPriority(updatedInfo.getPriority());
			mTask.setGroup(updatedInfo.getGroup());
			mTask.setTaskInfo(updatedInfo.getDisplay());

			
			updateHistory();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(mAllTasks);
			return String.format(AppConst.MESSAGE.UPDATED_SUCCESSFUL);
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
		
	}

	protected String executeShowby(Command mCommand,  ArrayListPointer feedbackTasks){
		if (mAllTasks.size() == 0) {
			return AppConst.MESSAGE.NO_TASK_FOUND;
		}

		ArrayList<Task> mTasks = duplicate(mAllTasks);
		feedbackTasks.setPointer(mTasks);

		switch (mCommand.getCommandArgument()){
			case AppConst.TASK_FIELD.DEADLINE:
				Collections.sort(mTasks,new TaskDeadlineCompare());
				return AppConst.MESSAGE.DISPLAY_BY_DEADLINE;
				
			case AppConst.TASK_FIELD.START_DATE:
				Collections.sort(mTasks,new TaskStartDateCompare());
				return AppConst.MESSAGE.DISPLAY_BY_START_DATE;
				
			case AppConst.TASK_FIELD.END_DATE:
				Collections.sort(mTasks,new TaskEndDateCompare());
				return AppConst.MESSAGE.DISPLAY_BY_END_DATE;
				

			case AppConst.TASK_FIELD.PRIORITY:
				Collections.sort(mTasks,new TaskPriorityCompare());
				return AppConst.MESSAGE.DISPLAY_BY_PRIORITY;

			case AppConst.TASK_FIELD.GROUP:
				Collections.sort(mTasks,new TaskGroupCompare());
				return AppConst.MESSAGE.DISPLAY_BY_GROUP;
		
			default:
				return AppConst.MESSAGE.NOT_RECOGNIZED_SYNTAX;
				
		}
	}

	protected String executeShow(Command mCommand,  ArrayListPointer feedbackTasks){
		ArrayList<Task> mTasks = new ArrayList<Task>();
		feedbackTasks.setPointer(mTasks);

		String argument = mCommand.getCommandArgument();
		// handle for show day
		String dateTimeArgument = "";
		if (mCommand.getCommandType().equals(AppConst.COMMAND_TYPE.SHOW_DAY)) {
			dateTimeArgument = mDateTimeHelper.getDateMonthFromString(argument, 1);
			
			if (dateTimeArgument == null || dateTimeArgument.equals("")) {
				return AppConst.MESSAGE.INVALID_DAY;
			}
		}

		if (mCommand.getCommandType().equals(AppConst.COMMAND_TYPE.SHOW_ID) ){
			feedbackTasks.setPointer(mPreviousTasks);
			return String.format(AppConst.MESSAGE.SHOW_BY_ID, argument,getTaskWithId(argument).getDisplay() );
		}

		for (int i=0;i<mAllTasks.size();i++){
			switch (mCommand.getCommandType()){
				case AppConst.COMMAND_TYPE.SHOW_DAY:
					String deadline = mAllTasks.get(i).getDeadline();
					
					if (!deadline.equals("")) {
						if (deadline.substring(0, 5).equals(dateTimeArgument) ){
							mTasks.add(mAllTasks.get(i));
						}
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_PRIORITY:
					if (mAllTasks.get(i).getPriority().equals(argument)){
						mTasks.add(mAllTasks.get(i));	
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_GROUP:
					if (mAllTasks.get(i).getGroup().equals(argument)){
						mTasks.add(mAllTasks.get(i));	
					}
					break;

				default: break;
			}
		}

		if (mTasks.size()==0){
			feedbackTasks.setPointer(mAllTasks);
			return String.format(AppConst.MESSAGE.NOTHING_TO_SHOW, argument);
		}
		return String.format(AppConst.MESSAGE.SHOWING_TASK, mCommand.getCommandType().substring(4), argument);
	}

	protected String executeSetFile(Command mCommand,  ArrayListPointer feedbackTasks){
		//update the internal settings object and save it to the setting file.
		mSettings.setDataFileUrl(mCommand.getCommandArgument());
		mSettingsStorage.writeSettings(mSettings);

		//Set the new file URL for the dataStorage and reload tasks;
		mDataStorage.setFileURL(mCommand.getCommandArgument());
		initialiseTasks();

		return String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, mCommand.getCommandArgument());
	}
	
	protected String executeUndo(Command mCommand, ArrayListPointer feedbackTasks){
		if (mCurrentState>0) {
			mCurrentState--;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(mAllTasks);
			return AppConst.MESSAGE.UNDID_SUCCESSFUL;
		}
		return AppConst.MESSAGE.NOTHING_UNDONE;
	}

	protected String executeRedo(Command mCommand,  ArrayListPointer feedbackTasks){
		if (mCurrentState < mHistory.size()-1){
			mCurrentState++;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(mAllTasks);
			return AppConst.MESSAGE.REDID_SUCCESSFUL;
		}
		return AppConst.MESSAGE.NOTHING_REDONE;
	}

	protected String executeSearch(Command mCommand, ArrayListPointer feedbackTasks) {
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

		ArrayList<Task> mTasks = new ArrayList<Task>();
		feedbackTasks.setPointer(mTasks);

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
				mTasks.add(mAllTasks.get(id));
			}
		}

		if (mTasks.size()==0) {
			feedbackTasks.setPointer(mAllTasks);
			return AppConst.MESSAGE.NOTHING_MATCHED;
		}

		return String.format(AppConst.MESSAGE.SHOWING_RESULT, mCommand.getCommandArgument());

	}
	
	
	protected String executeDone(Command mCommand, ArrayListPointer feedbackTasks) {
		int position = 0;
		feedbackTasks.setPointer(mAllTasks);
		Task taskToClose = mCommand.getNewTask();
		if (taskToClose == null) {
			return "";
		}
		if (taskToClose.getDeadline() == null || taskToClose.getStartDate() == null || taskToClose.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (taskToClose.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;
		}
		ArrayList<Task> possibleTasks = new ArrayList<Task>();
		for(int i=0; i<mAllTasks.size(); i++) {
			int x = isTasksMatched(taskToClose, mAllTasks.get(i));
			if (x == 1) {
				if (mAllTasks.get(i).getStatus().equals(AppConst.TASK_FIELD.DONE)) {
					return AppConst.MESSAGE.TASK_CLOSED;
				} else {
					mAllTasks.get(i).setStatus(AppConst.TASK_FIELD.DONE);
					updateHistory();
					mDataStorage.rewriteContent(mAllTasks);
					feedbackTasks.setPointer(mAllTasks);
					return AppConst.MESSAGE.MARKED_DONE_SUCCESSFUL;
				}
			} else if (x == 0) {
				position = i;
				possibleTasks.add(mAllTasks.get(i));
			}
		}
		if (possibleTasks.size() == 0) {
			return AppConst.MESSAGE.NOTHING_MATCHED;
		} else if (possibleTasks.size() == 1) {
			if (mAllTasks.get(position).getStatus().equals(AppConst.TASK_FIELD.DONE)) {
				return AppConst.MESSAGE.TASK_CLOSED;
			} else {
				mAllTasks.get(position).setStatus(AppConst.TASK_FIELD.DONE);
				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);
				feedbackTasks.setPointer(mAllTasks);
				return AppConst.MESSAGE.MARKED_DONE_SUCCESSFUL;
			}
				
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
	}
	
	protected String executeUndone(Command mCommand, ArrayListPointer feedbackTasks) {
		int position = 0;
		feedbackTasks.setPointer(mAllTasks);
		Task taskToOpen = mCommand.getNewTask();
		if (taskToOpen == null) {
			return "";
		}
		if (taskToOpen.getDeadline() == null || taskToOpen.getStartDate() == null || taskToOpen.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (taskToOpen.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;
		}
		ArrayList<Task> possibleTasks = new ArrayList<Task>();
		for(int i=0; i<mAllTasks.size(); i++) {
			int x = isTasksMatched(taskToOpen, mAllTasks.get(i));
			if (x == 1) {
				if (mAllTasks.get(i).getStatus().equals(AppConst.TASK_FIELD.UNDONE)) {
					return AppConst.MESSAGE.TASK_OPENED;
				} else {
					mAllTasks.get(i).setStatus(AppConst.TASK_FIELD.UNDONE);
					updateHistory();
					mDataStorage.rewriteContent(mAllTasks);
					feedbackTasks.setPointer(mAllTasks);
					return AppConst.MESSAGE.MARKED_UNDONE_SUCCESSFUL;
				}
			} else if (x == 0) {
				position = i;
				possibleTasks.add(mAllTasks.get(i));
			}
		}
		if (possibleTasks.size() == 0) {
			return AppConst.MESSAGE.NOTHING_MATCHED;
		} else if (possibleTasks.size() == 1) {
			if (mAllTasks.get(position).getStatus().equals(AppConst.TASK_FIELD.UNDONE)) {
				return AppConst.MESSAGE.TASK_OPENED;
			} else {
				mAllTasks.get(position).setStatus(AppConst.TASK_FIELD.UNDONE);
				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);
				feedbackTasks.setPointer(mAllTasks);
				return AppConst.MESSAGE.MARKED_UNDONE_SUCCESSFUL;
			}
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
	}
	
	protected String executeTimetable(Command mCommand, ArrayListPointer feedbackTasks) {
		feedbackTasks.setPointer(mAllTasks);
		return "ok";
	}

	
	// -1 is different
	// 1 is the same
	// 0 is almost the same
	private int isTasksMatched(Task task1, Task task2) {
		boolean isTheSame = true;
		if (!task1.getPriority().equals("") && !task1.getPriority().equals(task2.getPriority())) {
			return -1;
		} else if (task1.getPriority().equals("") && !task2.getPriority().equals("")) {
			isTheSame = false;
		}
		
		if (!task1.getDeadline().equals("") && !task1.getDeadline().equals(task2.getDeadline())) {
			return -1;
		} else if (task1.getDeadline().equals("") && !task2.getDeadline().equals("")) {
			isTheSame = false;
		}
		
		if (!task1.getPeriod().equals("") && !task1.getPeriod().equals(task2.getPeriod())) {
			return -1;
		} else if (task1.getPeriod().equals("") && !task2.getPeriod().equals("")) {
			isTheSame = false;
		}
		
		if (!task1.getGroup().equals("") && !task1.getGroup().equals(task2.getGroup())) {
			return -1;
		} else if (task1.getGroup().equals("") && !task2.getGroup().equals("")) {
			isTheSame = false;
		}
		if (!task2.getName().startsWith(task1.getName())) {
			return -1;
		} else if (!task1.getName().equals(task2.getName())) {
			isTheSame = false;
		}
		if (isTheSame) {
			return 1;
		}
		return 0;
	}
	
	protected boolean isCheckOverlap(Task newTask, int position) {
		if (newTask.getStartDate().equals("") || newTask.getEndDate().equals("")) {
			return false;
		}
		
		String startDate = newTask.getStartDate();
		String endDate = newTask.getEndDate();
		String currentTime = mDateTimeHelper.getCurrentTimeString();
		if (newTask.getRepeatedType() != AppConst.REPEATED_TYPE.EVERY_WEEK) {
			if (startDate.compareTo(currentTime)<=0) {
				startDate = currentTime;
			}
		}
		
		boolean isRepeated = false;
		if (newTask.getRepeatedType() != AppConst.REPEATED_TYPE.NONE) {
			isRepeated = true;
		}
		
		if (newTask.getRepeatedType() != AppConst.REPEATED_TYPE.EVERY_WEEK) {
				
			for(int i=0; i<mAllTasks.size(); i++) {
				if (i != position) {
					Task task = mAllTasks.get(i);
					if (!task.getStartDate().equals("") && !task.getEndDate().equals("") && task.getRepeatedType()==AppConst.REPEATED_TYPE.NONE) {
						if (mDateTimeHelper.isTwoEventOverlap(newTask.getStartDate(), newTask.getEndDate(), isRepeated, newTask.getPeriod(), task.getStartDate(), task.getEndDate(), false, "")) {
							return true;
						}
					}
					if (task.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO || task.getRepeatedType() == AppConst.REPEATED_TYPE.EVERYDAY) {
						if (mDateTimeHelper.isTwoEventOverlap(newTask.getStartDate(), newTask.getEndDate(), isRepeated, newTask.getPeriod(), task.getStartDate(), task.getEndDate(), true, task.getPeriod())) {
							return true;
						}
					}
				
					if (task.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
						if (mDateTimeHelper.isEventOverlapWithRepeating(newTask.getStartDate(), newTask.getEndDate(), isRepeated, newTask.getPeriod(), task.getStartDate(), task.getPeriod())) {
							return true;
						}
					}
				}
			}
		}
		
		
		
		if (newTask.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
			for(int i=0; i<mAllTasks.size(); i++) {
				if (i != position) {
					Task task = mAllTasks.get(i);
					if (!task.getStartDate().equals("") && !task.getEndDate().equals("") && task.getRepeatedType()==AppConst.REPEATED_TYPE.NONE) {
						if (mDateTimeHelper.isEventOverlapWithRepeating(task.getStartDate(), task.getEndDate(), false, task.getPeriod(), newTask.getStartDate(), newTask.getPeriod())) {
							return true;	
						}
					}
					if (task.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO || task.getRepeatedType() == AppConst.REPEATED_TYPE.EVERYDAY) {
						if (mDateTimeHelper.isEventOverlapWithRepeating(task.getStartDate(), task.getEndDate(), true, task.getPeriod(), newTask.getStartDate(), newTask.getPeriod())) {
							return true;
						}
					}
				
					if (task.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
						if (mDateTimeHelper.isEventOverlapWithRepeating(newTask.getStartDate(), newTask.getStartDate(), true, newTask.getPeriod(), task.getStartDate(), task.getPeriod())) {
							return true;
						}
					}	
				}
			}
		}
		return false;
	}

	protected String removeSpace(String userCommand) {
		String[] commands = userCommand.split(" ");
		String result = "";
		for(int i=0; i<commands.length; i++) {
			if (!commands[i].equals("") && !commands[i].equals(" ")) {
				if (result.length() > 0) {
					result += " ";
				}
				result += commands[i];
			}
		}
		return result;
	}
	
	protected Task getTaskWithId(String stringId) {
		if (stringId == null || stringId.equals("")) {
			return null;
		}
		int id = 0;
		for(int i=0; i<stringId.length(); i++) {
			if (stringId.charAt(i)<'0' || stringId.charAt(i)>'9') {
				return null;
			}
			id = id * 10 + (stringId.charAt(i) - '0');
		}
		id = id - 1;
		if (id < 0 || id >= mPreviousTasks.size()) {
			return null;
		}
		return mPreviousTasks.get(id);
	}

	/**
	 * the main function for the UI to call to execute a command
	 * returns a feedback String and an ArrayList of Task in feedbackTasks
	 * 
	 **/
	protected String process(String userCommand, ArrayListPointer feedbackTasks) {
		mPreviousTasks = feedbackTasks.getPointer();
		addNewUserCommand(userCommand);
		userCommand = removeSpace(userCommand);
		Command mCommand = mCommandParser.parse(userCommand);

		//For most of the times the tasks to be displayed after each command is all the tasks.
		//if it is otherwise, later codes will modify this pointer
		feedbackTasks.setPointer(mAllTasks);

		switch (mCommand.getCommandType()){
			case AppConst.COMMAND_TYPE.ADD:
				return executeAdd(mCommand, feedbackTasks);
				
			case AppConst.COMMAND_TYPE.SHOW_ALL:
				return AppConst.MESSAGE.TASK_TO_DO;

			case AppConst.COMMAND_TYPE.DELETE:
				return executeDelete(mCommand, feedbackTasks);
			
			case AppConst.COMMAND_TYPE.DELETE_ALL:
				return executeDeleteAll(mCommand, feedbackTasks);
				
			case AppConst.COMMAND_TYPE.DELETE_BY:
				return executeDeleteBy(mCommand, feedbackTasks);
			
			case AppConst.COMMAND_TYPE.UPDATE:
				return executeUpdate(mCommand, feedbackTasks);
				
			case AppConst.COMMAND_TYPE.SHOW_BY:
				return executeShowby(mCommand, feedbackTasks);
			
			case AppConst.COMMAND_TYPE.SHOW_DAY:
			case AppConst.COMMAND_TYPE.SHOW_PRIORITY:
			case AppConst.COMMAND_TYPE.SHOW_GROUP:
			case AppConst.COMMAND_TYPE.SHOW_ID:
				return executeShow(mCommand, feedbackTasks);

			case AppConst.COMMAND_TYPE.SET_FILE:
				return executeSetFile(mCommand, feedbackTasks);

			case AppConst.COMMAND_TYPE.UNDO:
				return executeUndo(mCommand, feedbackTasks);

			case AppConst.COMMAND_TYPE.REDO:
				return executeRedo(mCommand, feedbackTasks);
			
			case AppConst.COMMAND_TYPE.SHOW:
			case AppConst.COMMAND_TYPE.SEARCH:
				return executeSearch(mCommand, feedbackTasks);

			case AppConst.COMMAND_TYPE.DONE:
				return executeDone(mCommand, feedbackTasks);
			case AppConst.COMMAND_TYPE.UNDONE:
				return executeUndone(mCommand, feedbackTasks);
			case AppConst.COMMAND_TYPE.TIMETABLE:
				return executeTimetable(mCommand, feedbackTasks);
			case AppConst.COMMAND_TYPE.EXIT:
				return null;

			default:
				return String.format(AppConst.MESSAGE.SYNTAX_ERROR, userCommand);
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
