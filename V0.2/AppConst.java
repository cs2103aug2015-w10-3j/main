import java.util.*;

public interface AppConst {

	interface KEY_WORD {
	
		String[] keywords = new String[] {
				"from",
				"to",
				"by",
				"before",
				"repeat",
				"group",
				"grp",
				"priority",
				"TO"
				};
	}

	interface COMMAND_TYPE {
	
		String ADD = "add";
		
		String CLEAR = "clear";
		
		String DELETE = "delete";
		String DELETE_ALL = "deleteall";
		String DELETE_BY = "deleteby";
		
		String EXIT = "exit";
		
		// Show Tasks
		String SHOW = "show";
		String SHOW_BY = "showby";
		String SHOW_ALL = "showall";
		String SHOW_DAY = "showday";
		String SHOW_PRIORITY = "showpriority";
		String SHOW_GROUP = "showgroup";
		
		String SET_FILE = "setfile";
		
		String REDO = "redo";
		String UNDO = "undo";
		
		// Search tasks for some keywords
		String SEARCH = "search";
		
		String UPDATE = "update";
		
		// Close to mark a task is done
		String CLOSE = "close";
		
		// Open a task that is marked 'done'
		String OPEN = "open";
	}
	
	interface TASK_FIELD {
	
		String TASK_NAME = "taskName";
		String DEADLINE = "deadline";
		String DAY = "day";
		
		// Priority field and values
		String PRIORITY = "priority";
		String HIGH = "high";
		String MEDIUM = "medium";
		String LOW = "low";
		
		String GROUP = "group";
		String START_DATE = "start date";
		String END_DATE = "end date";
		
		// Status field and values
		String STATUS = "status";
		String DONE = "done";
		String UNDONE = "undone";
	}
	
	interface MESSAGE {
	
		
		String GOODBYE = "Goodbye!";
		String WELCOME = "Welcome to To-Do list. ";
		
		// Add new task
		String TASK_EXISTS = "Can not add new task. This task already existed! Look at table above.\n";
		String TASK_ADDED = "Successful added '%s' \n";
		String INVALID_DEADLINE = "Invalid deadline or end date! Current date and time is %s\n";
		String INVALID_START_DATE = "Invalid start date! Current date and time is %s\n";
		String INVALID_DATE_TIME_FORMAT = "Invalid date time format!\n";
		String INVALID_PRIORITY = "Invalid priority format!\n";
		String INVALID_TASK_NAME = "Invalid task name! Task name can not be null, empty or space only!\n";
		
		// Show
		String TASK_TO_DO = "These are your tasks to do!";
		String NO_TASK_FOUND = "You don't have any tasks! Use 'add' to add a new task!\n";
		String DISPLAY_BY_DEADLINE = "Displaying tasks in the order of their deadlines.";
		String DISPLAY_BY_START_DATE = "Displaying tasks in the order of their start dates.";
		String DISPLAY_BY_END_DATE = "Displaying tasks in the order of their end dates.";
		String DISPLAY_BY_PRIORITY = "Displaying tasks in the order of their priorities.";
		String DISPLAY_BY_GROUP = "Displaying tasks in the order of their groups.";
		String NOT_RECOGNIZED_SYNTAX = "Syntax not recognized. Use 'showby deadline' or 'showby priority' or 'showby group'";
		String SHOWING_TASK = "These are your tasks for %s '%s'";
		String NOTHING_TO_SHOW = "You don't have any tasks to show for '%s'!\n";
		String INVALID_DAY = "Invalid day format!\n";
		
		// Delete
		String MANY_TASKS_MATCHED = "Look at the table above. Do you mean one of the above ? Please be more specific.";
		String REMOVED_SUCCESSFUL = "'%s' was removed successfully!\n";
		String DELETED_ALL = "All tasks were removed successfully!\n";
		String DELETED_FIELD = "Removed all tasks with %s '%s' successfully!\n";
		String DELETED_NO_TASK = "No more tasks with %s '%s' to delete!\n";
		String NO_TASK_TO_DELETE = "No more tasks to delete!\n";
		
		// Update
		String UPDATED_SUCCESSFUL = "Updated successfully!\n";
		String INVALID_UPDATE_FORMAT = "Invalid update format, use 'update <oldTask> TO <newTask>' oldTask and newTask can not be null, empty or space only!\n";
		String TASK_UPDATED_EXIST = "Can not update, this new task was already exist!\n";
		
		// Change file name
		String CHANGED_SUCCESSFUL = "Successful changed data file to '%s'!\n";
		
		// Undo 
		String UNDID_SUCCESSFUL = "Undid successful!\n";
		String NOTHING_UNDONE = "Nothing to be undone!\n";
		
		// Redo
		String REDID_SUCCESSFUL = "redid successfully!\n";
		String NOTHING_REDONE = "No more operations to redo!\n";
		
		// Search
		String NOTHING_MATCHED = "Nothing matched! Use 'showall' to show all your tasks!\n";
		String SHOWING_RESULT = "Showing results for '%s'";
		
		// Mark done/undone, use open and close commands
		String MARKED_DONE_SUCCESSFUL = "Task is marked 'done' successfully!\n";
		String MARKED_UNDONE_SUCCESSFUL = "Task is marked 'undone' successfully. You can update the new deadline for this task by using 'update' command!\n";
		String TASK_CLOSED = "Task is already closed!\n";
		String TASK_OPENED = "Task is already opened!\n";
		
		String TASK_NOT_FOUND = "Error: '%s' not found!\n";
		String SYNTAX_ERROR = "Syntax error: command(%s) not found.\n";	
	}

}
