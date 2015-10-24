import java.util.*;

public interface AppConst {

	interface COMMAND_TYPE {
	
		String ADD = "add";
		
		String CLEAR = "clear";
		
		String DELETE = "delete";
		
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
		
		// Search
		String SEARCH = "search";
		
		String UPDATE = "update";
	}
	
	interface TASK_FIELD {
	
		String TASK_NAME = "taskName";
		String DEADLINE = "deadline";
		String PRIORITY = "priority";
		String GROUP = "group";
		String START_DATE = "startDate";
		String END_DATE = "endDate";
		String STATUS = "status";
	}
	
	interface MESSAGE {
		
		// Add new task
		String TASK_EXISTS = "Can not add new task. This task already existed! Look at table above.\n";
		String TASK_ADDED = "Successful added '%s' \n";
		String INVALID_DEADLINE = "Can not add new task with invalid deadline!\n";
		
		// Show
		String TASK_TO_DO = "These are your tasks to do!";
		String NO_TASK_FOUND = "You don't have any tasks! Use 'add' to add a new task!\n";
		String DISPLAY_BY_DEADLINE = "Displaying tasks in the order of their deadlines.";
		String DISPLAY_BY_PRIORITY = "Displaying tasks in the order of their priorities.";
		String DISPLAY_BY_GROUP = "Displaying tasks in the order of their groups.";
		String NOT_RECOGNIZED_SYNTAX = "Syntax not recognized. Use 'showby deadline' or 'showby priority' or 'showby group'";
		String SHOWING_TASK = "These are your tasks for %s '%s'";
		String NOTHING_TO_SHOW = "You don't have any tasks to show for '%s'!\n";
		
		// Delete
		String MANY_TASKS_MATCHED = "Look at the table above. Do you mean one of the above ? Please be more specific.";
		String REMOVED_SUCCESSFUL = "'%s' was removed successfully!\n";
		
		// Update
		String UPDATED_SUCCESSFUL = "'%s' was updated successfully!\n";
		
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
		
		String TASK_NOT_FOUND = "Error: '%s' not found!\n";
		String SYNTAX_ERROR = "Syntax error: command(%s) not found.\n";	
	}

}
