
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class MainLogic {

    private String dataFile = "default.txt";
   	private final String PRIORITY_LOW = "low";

	CommandParser mTaskCommandParse = new CommandParser();
	private ArrayList<DataState> mHistory;
	private int mCurrentState;
	private ArrayList<Task> mAllTasks;
	private ArrayList<String> mAllUserCommands;
	private Storage mStorage;
	
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
	protected String process(String userCommand, ArrayList<Task> taskList) {

		taskList = new ArrayList<Task>();
		addNewUserCommand(userCommand);

		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		assert commandInfo.length>0;
		command = commandInfo[0];  String field1 = commandInfo[1];
		taskInfo = field1;
		int count;
		String res;
		switch (command){
			case AppConst.COMMAND_TYPE.ADD:
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
					taskList = mAllTasks;
					return AppConst.MESSAGE.TASK_EXISTS;
				}
				mAllTasks.add(newTask);
				updateHistory();
				mStorage.rewriteContent(mAllTasks);
				taskList = mAllTasks;
				System.out.println(mAllTasks.size());
				return String.format(AppConst.MESSAGE.TASK_ADDED, newTask.getTaskInfo());
			case AppConst.COMMAND_TYPE.SHOW_ALL:
				taskList = mAllTasks;
				if (mAllTasks.size() > 0) {
					return AppConst.MESSAGE.TASK_TO_DO;
				} else {
					return AppConst.MESSAGE.NO_TASK_FOUND;
				}
			case AppConst.COMMAND_TYPE.DELETE:
				String message = AppConst.MESSAGE.MANY_TASKS_MATCHED;
				int numberMatched = 0, position = 0;
				String deleted = "";
				for (int i=0;i<mAllTasks.size();i++){
					String taskName = mAllTasks.get(i).getTaskInfo();
					if (taskName.equals(taskInfo)){
						mAllTasks.remove(i);
						updateHistory();
						mStorage.rewriteContent(mAllTasks);
						taskList = mAllTasks;
						return String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, taskName);
					}
					if (taskName.startsWith(taskInfo)) {
						numberMatched++;
						deleted = taskName;
						position = i;
					}
				}
				if (numberMatched > 0) {
					if (numberMatched == 1) {
						mAllTasks.remove(position);
						updateHistory();
						mStorage.rewriteContent(mAllTasks);
						taskList = mAllTasks;
						return String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, deleted);
					}
					return message;
				}
				taskList = mAllTasks;
				return  String.format(AppConst.MESSAGE.TASK_NOT_FOUND, taskInfo);

			case AppConst.COMMAND_TYPE.UPDATE:
				String updated;
				String [] arguments = taskInfo.split(" ");

				if (arguments.length != 2) {
					return String.format(AppConst.MESSAGE.COMMAND_ERROR, taskInfo);
				}
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getName().equals(arguments[0])){
						updated = mAllTasks.get(i).getName();
						mAllTasks.get(i).setName(arguments[1]);

						updateHistory();
						mStorage.rewriteContent(mAllTasks);
						taskList = mAllTasks;
						return AppConst.MESSAGE.UPDATED_SUCCESSFUL;
					}
				}
				taskList = mAllTasks;
				return String.format(AppConst.MESSAGE.TASK_NOT_FOUND, arguments[0]);
			case AppConst.COMMAND_TYPE.SHOW_BY:
				if (mAllTasks.size() == 0) {
					return AppConst.MESSAGE.NO_TASK_FOUND;
				}			
				switch (field1){
					case AppConst.TASK_FIELD.DEADLINE:
						taskList = duplicate(mAllTasks);
						Collections.sort(taskList,new TaskDeadlineCompare());
						break;
					case AppConst.TASK_FIELD.PRIORITY:
						taskList = duplicate(mAllTasks);
						Collections.sort(taskList,new TaskPriorityCompare());
						break;
					case AppConst.TASK_FIELD.GROUP:
						taskList = duplicate(mAllTasks);
						Collections.sort(taskList,new TaskGroupCompare());
						break;
					default:
						taskList = mAllTasks;
				}
				return "";
			case AppConst.COMMAND_TYPE.SHOW_DAY:
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getDeadline().equals(field1)){
						taskList.add(mAllTasks.get(i));
					}
				}
				if (taskList == null || taskList.size()==0) {
					return AppConst.MESSAGE.NO_TASK_FOUND;
				}
				return "";

			case AppConst.COMMAND_TYPE.SHOW_PRIORITY:
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getPriority().equals(field1)){
						taskList.add(mAllTasks.get(i));
					}
				}
				if (taskList == null || taskList.size()==0) {
					return AppConst.MESSAGE.NO_TASK_FOUND;
				} 
				return "";
			case AppConst.COMMAND_TYPE.SHOW_GROUP:
				for (int i=0;i<mAllTasks.size();i++){
					if (mAllTasks.get(i).getGroup().equals(field1)){
						taskList.add(mAllTasks.get(i));
					}
				}
				if (taskList == null || taskList.size()==0) {
					return AppConst.MESSAGE.NO_TASK_FOUND;
				} 
				return "";
			case AppConst.COMMAND_TYPE.SET_FILE:
				mStorage.setFileURL(field1);
				initialiseTasks();
				return String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, field1);
			case AppConst.COMMAND_TYPE.UNDO:
				if (mCurrentState>0) {
					mCurrentState--;
					mAllTasks = mHistory.get(mCurrentState).getAllTasks();
					mStorage.rewriteContent(mAllTasks);
					taskList = mAllTasks;
					return AppConst.MESSAGE.UNDID_SUCCESSFUL;
				}else{
					taskList = mAllTasks;
					return AppConst.MESSAGE.NOTHING_UNDONE;
				}
			case AppConst.COMMAND_TYPE.REDO:
				if (mCurrentState < mHistory.size()-1){
					mCurrentState++;
					mAllTasks = mHistory.get(mCurrentState).getAllTasks();
					mStorage.rewriteContent(mAllTasks);
					taskList = mAllTasks;
					return AppConst.MESSAGE.REDID_SUCCESSFUL;
				} else {
					taskList = mAllTasks;
					return AppConst.MESSAGE.NOTHING_REDONE;
				}
			case AppConst.COMMAND_TYPE.SEARCH:
				return searchForKey(field1, taskList);
			case AppConst.COMMAND_TYPE.EXIT:
				return null;
			default:
				taskList = mAllTasks;
				return String.format(AppConst.MESSAGE.COMMAND_ERROR, command);
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

	private String searchForKey(String arg, ArrayList<Task> taskList) {
		String[] arguments = arg.split(" ");

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
		String result = "";
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
				taskList.add(mAllTasks.get(id));
				count++;
			}
		}
		if (count == 0) {
			result = AppConst.MESSAGE.NOTHING_MATCHED;
		} else {
			result = String.format(AppConst.MESSAGE.FOUND, count);
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
