import java.util.*;

public interface AppConst {

	interface COMMAND_TYPE {
	
		String ADD = "add";
		
		String CLEAR = "clear";
		
		String DELETE = "delete";
		
		String EXIT = "exit";
		
		// Show Tasks
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
		
		// Show all
		String TASK_TO_DO = "These are your tasks to do!";
		String NO_TASK_FOUND = "You don't have any tasks! Use 'add' to add a new task!\n";
		
		// Delete
		String MANY_TASKS_MATCHED = "Look at the table above. Do you mean one of these? \n";
		String REMOVED_SUCCESSFUL = "'%s' was removed successfully!\n";
		
		// Update
		String UPDATED_SUCCESSFUL = "Updated successfully!\n";
		
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
		String FOUND = "%d found!\n";
		
		String TASK_NOT_FOUND = "Error: '%s' not found!\n";
		String COMMAND_ERROR = "Syntax error: command(%s) not found.\n";	
	}

}
