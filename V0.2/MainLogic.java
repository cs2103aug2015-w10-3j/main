
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;

public class MainLogic {

	CommandParser mTaskCommandParse = new CommandParser();

	//constants
	private static final String ENTER = "Enter";
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String DATE_FORMAT = "dd/MM HH:mm:ss";
    private static final String ZERO_SECOND = "00";
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
		mSettings.setDataFileUrl("test.txt");
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
	        int x = mDateTimeHelper.compareStringDates(o1.getDeadline(), o2.getDeadline());
	        if (x != 0) {
	        	return x;
	        }
	        
	        x = 2;
			if (o1.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				x = 3;
			} else if (o1.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				x = 1;
			}
			
			int y = 2;
			if (o2.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				y = 3;
			} else if (o2.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				y = 1;
			}
			
			if (x > y) {
				return -1;
			}
			return 1;
	    }
	}
	
	private class TaskNotificationCompare implements Comparator<Task> {
		@Override
		public int compare(Task o1, Task o2) {
			String s1 = o1.getDeadline();
			String s2 = o2.getDeadline();
			int x = 2;
			if (o1.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				x = 3;
			} else if (o1.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				x = 1;
			}
			
			int y = 2;
			if (o2.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				y = 3;
			} else if (o2.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				y = 1;
			}
			int xx = mDateTimeHelper.compareStringDates(s2, s1);
			if (xx != 0) {
				return xx;
			}
			if (x > y) {
				return -1;
			}
			return 1;
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
	
	// Get the list of tasks to be displayed 
	private ArrayList<Task> getTasksToDisplay(ArrayList<Task> tasks, int parentTaskId) {
		ArrayList<Task> result = new ArrayList<Task>();
		if (parentTaskId != -1) {
			for(int i=0; i<tasks.size(); i++) {
				if (tasks.get(i).getParentTaskId() == parentTaskId && tasks.get(i).getTaskId() != parentTaskId) {
					result.add(tasks.get(i));
				}
			}
		} else {
			for(int i=0; i<tasks.size(); i++) {
				if (tasks.get(i).getParentTaskId() == tasks.get(i).getTaskId()) {
					result.add(tasks.get(i));
				}
			}
		}
		return result;
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
		
		addNewTasks(newTask);
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
		
		return String.format(AppConst.MESSAGE.TASK_ADDED, newTask.getTaskInfo());
	}
	
	private void addNewTasks(Task newTask) {
	
		newTask.setRepeatTime(newTask.getDeadline());
		int parentTaskId = getNextTaskId();
		newTask.setTaskId(parentTaskId);
		newTask.setParentTaskId(parentTaskId);
		mAllTasks.add(newTask);
		if (newTask.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO) {
			int start = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(newTask.getStartDate()), mDateTimeHelper.getMonthFromStringDate(newTask.getStartDate()));
			int end = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(newTask.getEndDate()), mDateTimeHelper.getMonthFromStringDate(newTask.getEndDate()));
			for(int i=start; i<=end; i++) {
				String date = mDateTimeHelper.getDateForNumberOfDays(i);
				Task task = newTask.copy();
				task.setRepeatedType(AppConst.REPEATED_TYPE.NONE);
				task.setStartDate(date + " " + mDateTimeHelper.getStringStartTimeForStringPeriod(newTask.getPeriod()));
				task.setEndDate(date + " " + mDateTimeHelper.getStringEndTimeForStringPeriod(newTask.getPeriod()));
				task.setTaskId(getNextTaskId());
				task.setParentTaskId(parentTaskId);
				mAllTasks.add(task);
			}
		} else if (newTask.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
			int start = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(newTask.getStartDate()), mDateTimeHelper.getMonthFromStringDate(newTask.getStartDate()));
			while (start < 365) {
				String date = mDateTimeHelper.getDateForNumberOfDays(start);
				Task task = newTask.copy();
				task.setRepeatedType(AppConst.REPEATED_TYPE.NONE);
				task.setStartDate(date + " " + mDateTimeHelper.getStringStartTimeForStringPeriod(newTask.getPeriod()));
				task.setEndDate(date + " " + mDateTimeHelper.getStringEndTimeForStringPeriod(newTask.getPeriod()));
				task.setTaskId(getNextTaskId());
				task.setParentTaskId(parentTaskId);
				mAllTasks.add(task);
				start += 7;
			}
		}
	}
	
	private int findTasksMatched(Task task, ArrayList<Task> listTasks, ArrayListPointer returnTasks) {
		ArrayList<Task> possibleTasks = new ArrayList<Task>();
		int position = 0;
		for(int i=0; i<listTasks.size(); i++) {
			int x = isTasksMatched(task, listTasks.get(i));
			if (x == 1) {
				position = i;
				possibleTasks = new ArrayList<Task>();
				possibleTasks.add(listTasks.get(i));
				break;
			} else if (x == 0) {
				position = i;
				possibleTasks.add(listTasks.get(i));
			}
		}
		returnTasks.setPointer(possibleTasks);
		return position;
	}

	private void deleteTasks(int position) {
		int parentTaskId = mAllTasks.get(position).getParentTaskId();
		int taskId = mAllTasks.get(position).getTaskId();
		if (taskId != parentTaskId) {
			mAllTasks.remove(position);
		} else {
			ArrayList<Task> remainTaskList = new ArrayList<Task>();
			for(int i=0; i<mAllTasks.size(); i++) {
				if (mAllTasks.get(i).getParentTaskId() != parentTaskId) {
					remainTaskList.add(mAllTasks.get(i));
				}
			}
			mAllTasks = remainTaskList;
		}
	}
	
	private void markDone(int position) {
		int parentTaskId = mAllTasks.get(position).getParentTaskId();
		int taskId = mAllTasks.get(position).getTaskId();
		if (taskId != parentTaskId) {
			mAllTasks.get(position).setStatus(AppConst.TASK_FIELD.DONE);
		} else {
			for(int i=0; i<mAllTasks.size(); i++) {
				if (mAllTasks.get(i).getParentTaskId() != parentTaskId) {
					mAllTasks.get(i).setStatus(AppConst.TASK_FIELD.DONE);
				}
			}
		}
	}
	
	private void markUndone(int position) {
		int parentTaskId = mAllTasks.get(position).getParentTaskId();
		int taskId = mAllTasks.get(position).getTaskId();
		if (taskId != parentTaskId) {
			mAllTasks.get(position).setStatus(AppConst.TASK_FIELD.UNDONE);
		} else {
			for(int i=0; i<mAllTasks.size(); i++) {
				if (mAllTasks.get(i).getParentTaskId() != parentTaskId) {
					mAllTasks.get(i).setStatus(AppConst.TASK_FIELD.UNDONE);
				}
			}
		}
	}

	protected String executeDelete(Command mCommand, ArrayListPointer feedbackTasks){
	
		feedbackTasks.setPointer(mPreviousTasks);
		Task taskToDelete = mCommand.getNewTask();
		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length == 2 && commands[0].equals(AppConst.TASK_FIELD.ID)) {
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
		
		String message = "";
		
		ArrayListPointer returnListPointer = new ArrayListPointer();
		int position = findTasksMatched(taskToDelete, mPreviousTasks, returnListPointer);
		ArrayList<Task> possibleTasks = returnListPointer.getPointer();
		if (possibleTasks.size() == 0) {
			return AppConst.MESSAGE.NOTHING_MATCHED;
		} else if (possibleTasks.size() == 1) {
			Task task = mPreviousTasks.get(position);
			position = findTasksMatched(task, mAllTasks, returnListPointer);
			deleteTasks(position);
			updateHistory();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
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
		feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
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
		feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
		return message;
	}

	protected String executeUpdate(Command mCommand,  ArrayListPointer feedbackTasks) {
		Task updatedInfo = mCommand.getUpdatedTask();
		Task taskToBeUpdated = mCommand.getNewTask();
		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length == 2 && commands[0].equals(AppConst.TASK_FIELD.ID)) {
			taskToBeUpdated = getTaskWithId(commands[1]);
		}
		feedbackTasks.setPointer(mPreviousTasks);
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
		
		ArrayListPointer pointer = new ArrayListPointer();
		int position = findTasksMatched(taskToBeUpdated, mPreviousTasks, pointer);
		ArrayList<Task> possibleTasks = pointer.getPointer();
		
		if (possibleTasks.size() == 0) {
			return String.format(AppConst.MESSAGE.TASK_NOT_FOUND, taskToBeUpdated.getTaskInfo());
		} else if (possibleTasks.size() == 1) {
		
			Task mTask = mAllTasks.get(findTasksMatched(mPreviousTasks.get(position), mAllTasks, pointer));

			if ( updatedInfo.getName().equals("") ){
				updatedInfo.setName(mTask.getName());					
			}
				
			String taskInfo = " " + updatedInfo.getTaskInfo() + " ";
			
			if ( updatedInfo.getGroup().equals("") && !taskInfo.contains(AppConst.KEY_WORD.GROUP1) && !taskInfo.contains(AppConst.KEY_WORD.GRP1)) {
				updatedInfo.setGroup(mTask.getGroup());					
			}

			if ( updatedInfo.getPriority().equals("")){
				updatedInfo.setPriority(mTask.getPriority());					
			}

			if ( updatedInfo.getStartDate().equals("") && !taskInfo.contains(AppConst.KEY_WORD.FROM1)) {
				updatedInfo.setStartDate(mTask.getStartDate());					
			}

			if ( updatedInfo.getEndDate().equals("") && !taskInfo.contains(AppConst.KEY_WORD.TO1)) {
				updatedInfo.setEndDate(mTask.getEndDate());					
			}

			if ( updatedInfo.getDeadline().equals("") && !taskInfo.contains(AppConst.KEY_WORD.BY1) && !taskInfo.contains(AppConst.KEY_WORD.BEFORE1)) {
				updatedInfo.setDeadline(mTask.getDeadline());					
			}
			
			updatedInfo.setRepeatTime(updatedInfo.getDeadline());
			updatedInfo.setTaskId(mTask.getTaskId());
			updatedInfo.setParentTaskId(mTask.getParentTaskId());
			
			if (!taskInfo.contains(AppConst.KEY_WORD.REPEAT1) && !taskInfo.contains(AppConst.KEY_WORD.FROM1)) {
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
			mTask.setRepeatTime(updatedInfo.getRepeatTime());
			mTask.setStartDate(updatedInfo.getStartDate());
			mTask.setEndDate(updatedInfo.getEndDate());
			mTask.setPriority(updatedInfo.getPriority());
			mTask.setGroup(updatedInfo.getGroup());
			mTask.setTaskInfo(updatedInfo.getDisplay());
			
			if (mTask.getParentTaskId() == mTask.getTaskId() || mTask.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO || mTask.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
				int pos1 = findTasksMatched(mPreviousTasks.get(position), mAllTasks, pointer);
				deleteTasks(pos1);
				addNewTasks(mTask);

			}
			updateHistory();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
			return String.format(AppConst.MESSAGE.UPDATED_SUCCESSFUL);
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
		
	}

	protected String executeShowby(Command mCommand,  ArrayListPointer feedbackTasks){
		if (mAllTasks.size() == 0) {
			return AppConst.MESSAGE.NO_TASK_FOUND;
		}

		ArrayList<Task> mTasks = duplicate(mPreviousTasks);
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
			Task mTask = getTaskWithId(argument);
			if (mTask == null) {
				return AppConst.MESSAGE.INVALID_ID;
			}
			ArrayList<Task> result = new ArrayList<Task>();
			if (mTask.getRepeatedType() == AppConst.REPEATED_TYPE.FROM_TO || mTask.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
				for(int i=0; i<mAllTasks.size(); i++) {
					if (mAllTasks.get(i).getParentTaskId() == mTask.getParentTaskId() && mAllTasks.get(i).getTaskId() != mTask.getParentTaskId()) {
						result.add(mAllTasks.get(i));
					}
				}
			} else {
				result.add(mTask);
			}
			feedbackTasks.setPointer(result);
			return String.format(AppConst.MESSAGE.SHOW_BY_ID, argument,mTask.getDisplay() );
		}

		for (int i=0;i<mAllTasks.size();i++){
			switch (mCommand.getCommandType()){
				case AppConst.COMMAND_TYPE.SHOW_DAY:
					String deadline = mPreviousTasks.get(i).getDeadline();
					
					if (!deadline.equals("")) {
						if (deadline.substring(0, 5).equals(dateTimeArgument) ){
							mTasks.add(mPreviousTasks.get(i));
						}
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_PRIORITY:
					if (mAllTasks.get(i).getPriority().equals(argument)){
						mTasks.add(mPreviousTasks.get(i));	
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_GROUP:
					if (mAllTasks.get(i).getGroup().equals(argument)){
						mTasks.add(mPreviousTasks.get(i));	
					}
					break;

				case AppConst.COMMAND_TYPE.SHOW_DONE:
				case AppConst.COMMAND_TYPE.SHOW_UNDONE:
					if (mAllTasks.get(i).getStatus().equals(mCommand.getCommandType().substring(4))){
						mTasks.add(mPreviousTasks.get(i));
					}
					break;

				default: break;
					
			}
		}

		if (mTasks.size()==0){
			feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
			return String.format(AppConst.MESSAGE.NOTHING_TO_SHOW, argument);
		}
		return String.format(AppConst.MESSAGE.SHOWING_TASK, mCommand.getCommandType().substring(4), argument);
	}

	protected String executeSetFile(Command mCommand,  ArrayListPointer feedbackTasks) {
		//update the internal settings object and save it to the setting file.
		mSettings.setDataFileUrl(mCommand.getCommandArgument());
		mSettingsStorage.writeSettings(mSettings);

		//Set the new file URL for the dataStorage and reload tasks;
		mDataStorage.setFileURL(mCommand.getCommandArgument());
		initialiseTasks();
		feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));

		return String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, mCommand.getCommandArgument());
	}
	
	protected String executeUndo(Command mCommand, ArrayListPointer feedbackTasks){
		if (mCurrentState>0) {
			mCurrentState--;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
			return AppConst.MESSAGE.UNDID_SUCCESSFUL;
		}
		return AppConst.MESSAGE.NOTHING_UNDONE;
	}

	protected String executeRedo(Command mCommand,  ArrayListPointer feedbackTasks){
		if (mCurrentState < mHistory.size()-1){
			mCurrentState++;
			mAllTasks = mHistory.get(mCurrentState).getAllTasks();
			mDataStorage.rewriteContent(mAllTasks);
			feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
			return AppConst.MESSAGE.REDID_SUCCESSFUL;
		}
		return AppConst.MESSAGE.NOTHING_REDONE;
	}

	protected String executeSearch(Command mCommand, ArrayListPointer feedbackTasks) {
		String[] arguments = mCommand.getCommandArgument().toLowerCase().split(" ");

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
			feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
			return AppConst.MESSAGE.NOTHING_MATCHED;
		}

		return String.format(AppConst.MESSAGE.SHOWING_RESULT, mCommand.getCommandArgument());

	}
	
	
	protected String executeDone(Command mCommand, ArrayListPointer feedbackTasks) {
		feedbackTasks.setPointer(mPreviousTasks);
		Task taskToClose = mCommand.getNewTask();

		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length == 2 && commands[0].equals(AppConst.TASK_FIELD.ID)) {
			taskToClose = getTaskWithId(commands[1]);
			if (taskToClose==null) return AppConst.MESSAGE.INVALID_ID;
		}

		if (taskToClose == null) {
			return "";
		}
		if (taskToClose.getDeadline() == null || taskToClose.getStartDate() == null || taskToClose.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (taskToClose.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;
		}
		
		ArrayListPointer pointer = new ArrayListPointer();
		int position = findTasksMatched(taskToClose, mPreviousTasks, pointer);
		ArrayList<Task> possibleTasks = pointer.getPointer();
		if (possibleTasks.size() == 0) {
			return AppConst.MESSAGE.NOTHING_MATCHED;
		} else if (possibleTasks.size() == 1) {
			if (mPreviousTasks.get(position).getStatus().equals(AppConst.TASK_FIELD.DONE)) {
				return AppConst.MESSAGE.TASK_CLOSED;
			} else {
				position = findTasksMatched(mPreviousTasks.get(position), mAllTasks, pointer);
				markDone(position);
				updateHistory();
				mDataStorage.rewriteContent(getTasksToDisplay(mAllTasks, -1));
				feedbackTasks.setPointer(mAllTasks);
				return AppConst.MESSAGE.MARKED_DONE_SUCCESSFUL;
			}
				
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
	}
	
	protected String executeUndone(Command mCommand, ArrayListPointer feedbackTasks) {
		feedbackTasks.setPointer(mPreviousTasks);
		Task taskToOpen = mCommand.getNewTask();

		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length == 2 && commands[0].equals(AppConst.TASK_FIELD.ID)) {
			taskToOpen = getTaskWithId(commands[1]);
			if (taskToOpen==null) return AppConst.MESSAGE.INVALID_ID;
		}

		if (taskToOpen == null) {
			return "";
		}
		if (taskToOpen.getDeadline() == null || taskToOpen.getStartDate() == null || taskToOpen.getEndDate() == null) {
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		
		if (taskToOpen.getPriority() == null) {
			return AppConst.MESSAGE.INVALID_PRIORITY;
		}
		ArrayListPointer pointer = new ArrayListPointer();
		int position = findTasksMatched(taskToOpen, mPreviousTasks, pointer);
		ArrayList<Task> possibleTasks = pointer.getPointer();
		if (possibleTasks.size() == 0) {
			return AppConst.MESSAGE.NOTHING_MATCHED;
		} else if (possibleTasks.size() == 1) {
			if (mPreviousTasks.get(position).getStatus().equals(AppConst.TASK_FIELD.UNDONE)) {
				return AppConst.MESSAGE.TASK_OPENED;
			} else {
				position = findTasksMatched(mPreviousTasks.get(position), mAllTasks, pointer);
				markUndone(position);
				updateHistory();
				mDataStorage.rewriteContent(mAllTasks);
				feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
				return AppConst.MESSAGE.MARKED_UNDONE_SUCCESSFUL;
			}
		}
		feedbackTasks.setPointer(possibleTasks);
		return AppConst.MESSAGE.MANY_TASKS_MATCHED;
	}
	
	protected String executeTimetable(Command mCommand, ArrayListPointer feedbackTasks) {
		ArrayList<Task> result = new ArrayList<Task>();
		for(int i=0; i<mAllTasks.size(); i++) {
			if (mAllTasks.get(i).getRepeatedType() != AppConst.REPEATED_TYPE.EVERY_WEEK && mAllTasks.get(i).getRepeatedType() != AppConst.REPEATED_TYPE.FROM_TO) {
				result.add(mAllTasks.get(i));
			}
		}
		feedbackTasks.setPointer(result);
		return "";
	}


	protected String executeRemind(ArrayListPointer feedbackTasks) {
		ArrayList<Task> result = new ArrayList<Task>();
	
		// get current time, exactly for 1 seconds
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		String currentTime = dateFormat.format(new Date());
		
		// only execute when the second is 00
		if (!currentTime.endsWith(ZERO_SECOND)) {
			return null;
		}
		boolean isHasDeadlineComming = false;
		for(int i=0; i<mAllTasks.size(); i++) {
			String repeatTime = mAllTasks.get(i).getRepeatTime();
			if (!repeatTime.equals("") && mDateTimeHelper.compareStringDates(currentTime, repeatTime)>=0 && mAllTasks.get(i).getStatus().equals(AppConst.TASK_FIELD.UNDONE)) {
				if (currentTime.startsWith(repeatTime)) {
					isHasDeadlineComming = true;
				}
				result.add(mAllTasks.get(i));
			}
		}
		if (result.size() == 0 || !isHasDeadlineComming) {
			return null;
		}
		
		Collections.sort(result,new TaskNotificationCompare());
		feedbackTasks.setPointer(result);
		return AppConst.MESSAGE.REMIND_DEADLINE;
	}
	
	protected String executeRepeat(Command mCommand, ArrayListPointer feedbackTasks) {
		String[] commands = mCommand.getCommandArgument().split(" ");
		if (commands.length != 3) {
			return AppConst.MESSAGE.INVALID_REPEAT_COMMAND;
		}
		if (!commands[0].equals(AppConst.TASK_FIELD.ID)) {
			return AppConst.MESSAGE.INVALID_REPEAT_COMMAND;
		}
		Task task = getTaskWithId(commands[1]);
		if (task == null) {
			return AppConst.MESSAGE.INVALID_REPEAT_COMMAND;
		}
		if (task.getDeadline().equals("")) {
			return AppConst.MESSAGE.TASK_HAS_NO_DEADLINE;
		}
		int time = 0;
		for(int i=0; i<commands[2].length()-1; i++) {
			if (commands[2].charAt(i) < '0' || commands[2].charAt(i) > '9') {
				return AppConst.MESSAGE.INVALID_REPEAT_COMMAND;
			}
			time = time * 10 + (commands[2].charAt(i) - '0');
		}		
		if (commands[2].charAt(commands[2].length()-1) == 'h') {
			time *= 60;
		} else if (commands[2].charAt(commands[2].length()-1) == 'm') {
		} else if (commands[2].charAt(commands[2].length()-1) < '0' || commands[2].charAt(commands[2].length()-1) > '9') {
			return AppConst.MESSAGE.INVALID_REPEAT_COMMAND;
		} else {
			time = time * 10 + (commands[2].charAt(commands[2].length()-1) - '0');
		}
		String message = "";
		if (time == 0) {
			task.setRepeatTime("");
			message = AppConst.MESSAGE.TURN_OFF_REPEAT_SUCCESSFUL;
		} else {
			task.setRepeatTime(mDateTimeHelper.getDateTimeAfterFewMinuteFromNow(time));
			message = AppConst.MESSAGE.SET_REPEAT_SUCCESSFUL;
		}
		for(int i=0; i<mAllTasks.size(); i++) {
			int x = isTasksMatched(task, mAllTasks.get(i));
			if (x == 1) {
				mAllTasks.get(i).setRepeatTime(task.getRepeatTime());
				break;
			}
			
		}
		updateHistory();
		mDataStorage.rewriteContent(mAllTasks);
		feedbackTasks.setPointer(mAllTasks);
		return message;
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
		
		if (task1.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK && task2.getRepeatedType() == AppConst.REPEATED_TYPE.EVERY_WEEK) {
			int day1 = mDateTimeHelper.getDayInWeekForDate(task1.getStartDate());
			int day2 = mDateTimeHelper.getDayInWeekForDate(task2.getStartDate());
			if (day1 != day2) {
				return -1;
			}
		} else {
			if (!task1.getStartDate().equals("") && !task1.getStartDate().equals(task2.getStartDate())) {
				return -1;
			} else if (task1.getStartDate().equals("") && !task2.getStartDate().equals("")) {
				isTheSame = false;
			}
		
			if (!task1.getEndDate().equals("") && !task1.getEndDate().equals(task2.getEndDate())) {
				return -1;
			} else if (task1.getEndDate().equals("") && !task2.getEndDate().equals("")) {
				isTheSame = false;
			}
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
					if (task.getRepeatedType() == AppConst.REPEATED_TYPE.EVERYDAY) {
						if (mDateTimeHelper.isTwoEventOverlap(newTask.getStartDate(), newTask.getEndDate(), isRepeated, newTask.getPeriod(), task.getStartDate(), task.getEndDate(), true, task.getPeriod())) {
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
					if (task.getRepeatedType() == AppConst.REPEATED_TYPE.EVERYDAY) {
						if (mDateTimeHelper.isEventOverlapWithRepeating(task.getStartDate(), task.getEndDate(), true, task.getPeriod(), newTask.getStartDate(), newTask.getPeriod())) {
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
	
	private int getNextTaskId() {
		int result = 0;
		for(int i=0; i<mAllTasks.size(); i++) {
			result = Math.max(result, mAllTasks.get(i).getTaskId());
		}
		return result + 1;
	}

	/**
	 * the main function for the UI to call to execute a command
	 * returns a feedback String and an ArrayList of Task in feedbackTasks
	 * 
	 **/
	protected String process(String userCommand, ArrayListPointer feedbackTasks) {
		mPreviousTasks = feedbackTasks.getPointer();
		if (!userCommand.startsWith(AppConst.COMMAND_TYPE.REMIND)) {
			addNewUserCommand(userCommand);
		}
		userCommand = removeSpace(userCommand);
		Command mCommand = mCommandParser.parse(userCommand);

		// For most of the times the tasks to be displayed after each command is all the tasks.
		// if it is otherwise, later codes will modify this pointer
		feedbackTasks.setPointer(getTasksToDisplay(mAllTasks, -1));
		
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
			case AppConst.COMMAND_TYPE.SHOW_DONE:
			case AppConst.COMMAND_TYPE.SHOW_UNDONE:
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
				
			case AppConst.COMMAND_TYPE.REMIND:
				return executeRemind(feedbackTasks);

			case AppConst.COMMAND_TYPE.REPEAT:
				return executeRepeat(mCommand, feedbackTasks);

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
