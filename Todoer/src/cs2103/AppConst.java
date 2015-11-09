package cs2103;

public interface AppConst {

	String SETTINGS_FILE = "settings.txt";
	String DATE_FORMAT = "dd/MM HH:mm:ss";
	String ZERO_SECOND = "00";
	int BIG_NUM = 100000;
	
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
				"TO",
				"for",
				};
		String FROM = "from";
		String TO = "to";
		String BY = "by";
		String BEFORE = "before";
		String REPEAT = "repeat";
		String GROUP = "group";
		String GRP = "grp";
		String PRIORITY = "priority";
		String FOR = "for";
		
		String FROM1 = " from ";
		String TO1 = " to ";
		String BY1 = " by ";
		String BEFORE1 = " before ";
		String REPEAT1 = " repeat ";
		String GROUP1 = " group ";
		String GRP1 = " grp ";
		String PRIORITY1 = " priority ";
		String FOR1 = " for ";
		
	}
	
	interface DATE_TIME {
		int NUMBER_DAYS_PER_YEAR = 365;
		int NUMBER_DAYS_PER_WEEK = 7;
		int NUMBER_MINUTES_PER_HOUR = 60;
		int NUMBER_HOURS_PER_DAY = 24;
		int NUMBER_MONTHS = 12;
		int NUMBER_AM = 12;
		int MAX_NUMBER_DAYS_PER_MONTH = 31;
		
		String EVERY = "every";
		String EVERYDAY = "everyday";
		String DAY = "day";
		String THIS_MON = "this mon";
		String THIS_SUN = "this sun";
		
		String START_YEAR_DATE_TIME = "01/01 00:00";
		String END_YEAR_DATE_TIME = "31/12 23:59";
		String START_DATE = "01/01";
		String END_DATE = "31/12";
		String START_TIME = "00:00";
		String END_TIME = "23:59";
		int START_TIMETABLE_TIME = 8;
		int END_TIMETABLE_TIME = 20;
		
		String TODAY = "Today";
		String TOMORROW = "Tomorrow";
		String YESTERDAY = "Yesterday";
		String YTD = "ytd";
		String TMR = "tmr";
		String NOW = "now";
		
		String AM = "am";
		String PM = "pm";
		String NEXT = "next";
		String NXT = "nxt";
		String LAST = "last";
		String WEEKS = "weeks";
		String WEEK = "week";
		String LATER = "later";
		String DAYS = "days";
		String THIS = "this";
		String ZERO = "0";
		String DOUBLE_ZERO = "00";
		String SLASH = "/";
		String COLON = ":";
		String TH = "th";
		String ST = "st";
		String RD = "rd";
		String ND = "nd";
		String MINUTE = "m";
		String HOUR = "h";
		
		String DATE_FORMAT = "dd:MM";
		String DISPLAY_DATE_FORMAT = "dd MMM";
		String TIME_FORMAT = "HH:mm";
		
		int[] NUMBER_DAYS_IN_MONTH = new int[] {31, 
												28, 
												31, 
												30, 
												31, 
												30, 
												31, 
												31, 
												30, 
												31, 
												30, 
												31};
		String[] MONTH_NAMES = new String[] {"january", 
				 							 "february", 
											 "march", 
											 "april", 
											 "may", 
											 "june", 
											 "july", 
											 "august", 
											 "september", 
											 "october", 
											 "november", 
											 "december"
											 };
		String[] DAY_IN_WEEK = new String[] {  "monday",
											   "tuesday",
											   "wednesday",
											   "thursday",
											   "friday",
											   "saturday",
											   "sunday"
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
		String SHOW_ID = "showid";
		String SHOW_DONE = "showdone";
		String SHOW_UNDONE = "showundone";
		
		String SET_FILE = "setfile";
		
		String REDO = "redo";
		String UNDO = "undo";
		
		// Search tasks for some keywords
		String SEARCH = "search";
		
		String UPDATE = "update";
		
		// Close to mark a task is done
		String DONE = "done";
		
		// Open a task that is marked 'done'
		String UNDONE = "undone";
		
		String TIMETABLE = "timetable";
		
		String REMIND = "remind";
		String REPEAT = "repeat";
		String HELP = "help";
		
	}
	
	interface TASK_JSON_FIELD {
		String NAME = "name";
		String DEADLINE = "deadline";
		String REMIND_TIME = "remind_time";
		String START_DATE = "start_date";
		String END_DATE = "end_date";
		String PERIOD = "period";
		String PRIORITY = "priority";
		String GROUP = "group";
		String STATUS = "status";
		String TASK_INFO = "task_info";
		String REPEATED_TYPE = "repeat_type";
		String TASK_ID = "task_id";
		String PARENT_TASK_ID = "parent_task_id";
		String DATA_FILE_URL = "dataFileUrl";
	
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
		
		String ID = "id";
		
	}
	
	interface REPEATED_TYPE {
		
		// no repeat
		int NONE = 0;
		
		// from day to day
		int FROM_TO = 1;
		
		// every a day in week
		int EVERY_WEEK = 2;
		
		// every day
		int EVERYDAY = 3;
		
	}
	
	interface MESSAGE {
			
		String GOODBYE = "Goodbye!";
		String WELCOME = "Welcome to Todoer. ";
		
		// Add new task
		String TASK_EXISTS = "Failed to add new task. This task already exists! Look at table above.\n";
		String TASK_ADDED = "Successfully added '%s' \n";
		String INVALID_DEADLINE = "Invalid deadline or end date! Current date and time is %s\n";
		String INVALID_START_DATE = "Invalid start date! Current date and time is %s\n";
		String INVALID_DATE_TIME_FORMAT = "Invalid date time format!\n";
		String INVALID_PRIORITY = "Invalid priority format!\n";
		String INVALID_TASK_NAME = "Invalid task name! Task name can not be null, empty or space only!\n";
		String INVALID_ID = "Invalid id\n";
		
		String OVERLAP_TIME_PERIOD = "Overlap time period! Please check your timetable before you updating again.\n";
		
		String TASK_TO_DO = "These are your tasks to do!";
		String NO_TASK_FOUND = "You don't have any tasks! Use 'add' to add a new task!\n";
		String DISPLAY_BY_DEADLINE = "Displaying tasks in the order of their deadlines.";
		String DISPLAY_BY_START_DATE = "Displaying tasks in the order of their start dates.";
		String DISPLAY_BY_END_DATE = "Displaying tasks in the order of their end dates.";
		String DISPLAY_BY_PRIORITY = "Displaying tasks in the order of their priorities.";
		String DISPLAY_BY_GROUP = "Displaying tasks in the order of their groups.";
		String NOT_RECOGNIZED_SYNTAX = "Syntax not recognized. Use 'showby deadline' or 'showby priority' or 'showby group'";
		String SHOWING_TASK = "These are your tasks for %s '%s'";
		String SHOW_BY_ID = "Task number %s: %s";
		String NOTHING_TO_SHOW = "You don't have any tasks to show for '%s'!\n";
		String INVALID_DAY = "Invalid day format!\n";
		
		// Delete
		String MANY_TASKS_MATCHED = "Look at the table above. Do you mean one of the above? Please be more specific.";
		String REMOVED_SUCCESSFUL = "'%s' was removed successfully!\n";
		String DELETED_ALL = "All tasks were removed successfully!\n";
		String DELETED_FIELD = "Removed all tasks with %s '%s' successfully!\n";
		String DELETED_NO_TASK = "No more tasks with %s '%s' to delete!\n";
		String NO_TASK_TO_DELETE = "No more tasks to delete!\n";
		String REMOVE_TASKS_SUCCESSFUL = "Tasks are removed successfully!\n";
		
		// Update
		String UPDATED_SUCCESSFUL = "Updated successfully!\n";
		String INVALID_UPDATE_FORMAT = "Invalid update format, use 'update <oldTask> TO <newTask>' oldTask and newTask can not be null, empty or space only!\n";
		String TASK_UPDATED_EXIST = "Failed to update, a task with your desired specifications already exists!\n";
		String CAN_NOT_UPDATE = "Failed to update for this task! You can only delete it!\n";
		
		// Change file name
		String CHANGED_SUCCESSFUL = "Successfully changed data file to '%s'!\n";
		
		// Undo 
		String UNDID_SUCCESSFUL = "Undid successful!\n";
		String NOTHING_UNDONE = "Nothing to be undone!\n";
		
		// Redo
		String REDID_SUCCESSFUL = "redid successfully!\n";
		String NOTHING_REDONE = "No more operations to redo!\n";
		
		// Search
		String NOTHING_MATCHED = "Nothing matched! Use 'showall' to show all your tasks!\n";
		String SHOWING_RESULT = "Showing results for '%s'";
		
		// Mark done/undone
		String MARKED_DONE_SUCCESSFUL = "Task is marked 'done' successfully!\n";
		String MARKED_UNDONE_SUCCESSFUL = "Task is marked 'undone' successfully. You can update the new deadline for this task by using 'update' command!\n";
		String TASK_CLOSED = "Task is already done!\n";
		String TASK_OPENED = "Task is already undone!\n";
		String TASKS_DONE = "Tasks are marked done successfully!\n";
		String TASKS_UNDONE = "Tasks are marked undone successfully!\n";
		
		String TASK_NOT_FOUND = "Error: '%s' not found!\n";
		String SYNTAX_ERROR = "Syntax error: command(%s) not found.\n";	
		String REMIND_DEADLINE = "You have some deadline tasks coming!!! These tasks are not done yet! Use 'done' command to mark it as done.\n";
		String NOTIFICATIONS = "You have some deadline tasks coming!!! Please complete these tasks!\n";
		String SET_REPEAT_SUCCESSFUL = "Set repeat successful!\n";
		String INVALID_REPEAT_COMMAND = "Invalid repeat command! Can not find the task!\n";
		String TASK_HAS_NO_DEADLINE = "Task has no deadline!\n";
		String TURN_OFF_REPEAT_SUCCESSFUL = "Turn off repeat successful!\n";
	}
	
	interface UI_CONST { 
		static int ROW_HEIGHT_DEFAULT = 25;
		String TIME_ZERO = " 00:00";
		
		// String to display for priority
	    static final String HIGH = "High";
	    static final String MEDIUM = "Medium";
	    static final String LOW = "Low";
	    static String ENTER = "Enter";
	    static String APP_NAME = "Todoer";
	    static String COMMAND_MESSAGE = "$Todoer: ";
	    static String NEW_LINE = "\n";
	    static String DEFAULT_SOUND_FILE_NAME = "sound1.wav";
	    static String SLASH = "\\";
	    static String EVERYDAY = "Everyday";
	    static String EVERY = "Every ";
	    
	    
	    static String NOTIFICATION = "Notification!";
	    static String OK_BUTTON = "OK";
	    static String SPACE = "                        ";
	    
	    
	    static int DEFAULT_WINDOW_HEIGHT = 5;
		static int DEFAULT_WINDOW_WIDTH = 95;
		static double DEFAULT_RATIO = 0.08;
		static int MAX_NUMBER_ROWS = 16;
		static int MAX_NUMBER_TASKS_SEARCH_RESULT = 10;
		static int DEFAULT_COLUMN_WIDTH = 150;
		
		// check deadline task for every 1 second
	    static int DEFAULT_TIME_REMIND = 1 * 1000;
	    static int DEFAULT_TIME_DISMISS = 4 * 1000;
		static int DEFAULT_TIME_PLAY_SOUND = 1 * 1000;
		
		String[] TASK_COLUMN_NAMES = new String[] {	"#", 
													"Task Name",
													"Deadline",
													"Start Date/Time",
													"End Date/Time",
													"Period",
													"Priority",
													"Group",
													"Status"
													};
		int COLUMN_INDEX = 0;
		int COLUMN_TASK_NAME = 1;
		int COLUMN_DEADLINE = 2;
		int COLUMN_START_DATE_TIME = 3;
		int COLUMN_END_DATE_TIME = 4;
		int COLUMN_PERIOD = 5;
		int COLUMN_PRIORITY = 6;
		int COLUMN_GROUP = 7;
		int COLUMN_STATUS = 8;
		
		int COMMAND_ROW1 = 0;
		int COMMAND_ROW2 = 8;
		
		int[] HELP_COLUMN_WIDTH = new int[] {   280,
												150,
												220,
												0,
												220,
											};
		
		
		String[] HELP_TABLE_COLUMN_NAMES = new String[] {   "HELP",
															"",
															"",
															"",
															"",		
															};
				
		String[][] HELP_ROW = new String[][] { 
				{
					"add", 
					"done",
					"delete",
					"update",
					"show",
				},
				{	"add <task_name> ",
					"done <task_name>",
					"delete <task_name>",
					"update <task_name> TO <keyword> <new_value>",
					"showday <date>",
				},
				{
					"by <deadline>",
					"done id <id_number>",
					"delete id ",
					"",
					"showpriority <high/medium/low>",
				},
				{
					"priority <high/medium/low>",
					"",
					"deleteby <field> <value_of_field>",
					"",
					"showgroup <groupname>",
				},
				{
					"group <group_name>",
					"",
					"deleteall",
					"",
					"showby <column_field>",
					"",
					"",
					"",
					"",
				},
				{
					"from <start_datetime> to <end_datetime>",
					"",
					"",
					"",
					"showall",
				},
				{
					"repeat <period>",
					"",
					"",
					"",
					"",
				},
				{
					"",
					"",
					"",
					"",
					"",
				},
				{
					"timetable",
					"setfile",
					"help",
					"exit",
					"",
				},
				{
					"only 'timetable' to show this week",
					"setfile <file_path>",
					"Display the helpsheet",
					"Exit Todoer",
					"",
				},
				{
					"timetable from <startdate> to <enddate>",
					"",
					"",
					"",
					"",
				},
				{
					"timetable next <period>",
					"",
					"",
					"",
					"",
				},
				{
					"",
					"",
					"",
					"",
					"",
				}
		};
		
		String[] TIMETABLE_COLUMN_NAMES = new String[] { 	"#",
															"8-9", 
															"9-10", 
															"10-11", 
															"11-12", 
															"12-13", 
															"13-14", 
															"14-15",
															"15-16", 
															"16-17", 
															"17-18", 
															"18-19", 
															"19-20"
															};
		int[] TASK_COLUMN_WIDTH = new int[] {	40,
												350,
												130,
												130,
												130,
												100,
												80,
												130,
												80
												};
	}

}
