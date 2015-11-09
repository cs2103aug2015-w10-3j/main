package cs2103;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class TableHelper {
	
	private DateTimeHelper mDateTimeHelper = new DateTimeHelper();
	private CommandParser mCommandParser = new CommandParser();
	private ArrayList<Task> mDataTaskList = new ArrayList<Task>();
    
 // column to display in table
    String[] mColumnNames = AppConst.UI_CONST.TASK_COLUMN_NAMES;
                        
    String[] mTimeTableColumnNames = AppConst.UI_CONST.TIMETABLE_COLUMN_NAMES;
    int[] mColumnWidth = AppConst.UI_CONST.TASK_COLUMN_WIDTH;
    
    private static JTable mTable;
    
	public TableHelper() {
		
		// Create table to display tasks
	    mTable = new JTable() {
		    @Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				Object value = getModel().getValueAt(row, col);
				comp.setBackground(Color.white);
				
				/*
				** If the table is displaying list of tasks, column priority will display the priority of tasks
				** either High, Medium, or Low.
				** otherwise, the table is displaying the timetable
				** Check the value of column priority to check which data the table is displaying.
				*/
				Object checkValue = getModel().getValueAt(row, AppConst.UI_CONST.COLUMN_PRIORITY);
				if (checkValue.equals(AppConst.UI_CONST.HIGH) || checkValue.equals(AppConst.UI_CONST.MEDIUM) || checkValue.equals(AppConst.UI_CONST.LOW)) {
					if (col == AppConst.UI_CONST.COLUMN_PRIORITY) {
						if (value.equals(AppConst.UI_CONST.HIGH)) {
							comp.setBackground(Color.red);
						} else if (value.equals(AppConst.UI_CONST.MEDIUM)) {
							comp.setBackground(Color.yellow);
						} else if (value.equals(AppConst.UI_CONST.LOW)) {
							comp.setBackground(Color.green);
						}
					}
				} else {
					String st = (String)value;
					if (st.endsWith(AppConst.TASK_FIELD.HIGH)) {
						comp.setBackground(Color.red);
					} else if (st.endsWith(AppConst.TASK_FIELD.MEDIUM)) {
						comp.setBackground(Color.yellow);
					} else if (st.endsWith(AppConst.TASK_FIELD.LOW)) {
						comp.setBackground(Color.green);
					}
				}
				return comp;
			}
	    };
	   
	    mTable.setRowHeight(AppConst.UI_CONST.ROW_HEIGHT_DEFAULT);   
	    
	    createTableToDisplayTasks();
	}
	
	protected JTable getTable() {
		return mTable;
	}

	protected int getTableRowCount() {
		return mTable.getRowCount();
	}
	
	protected void setDataListForTable(ArrayList<Task> tasks) {
		mDataTaskList = tasks;
	}
	
	/* Get the data to display in the table for a task	
	* Column 0: index number of a task in the table, 
	* user can use this index instead of task name for commands
	* Column 1: Task name
	* Column 2: Deadline for task
	* Column 3: Start date/time for task,
	* for event task, the value will be the start time and date of the event
	* for recurrent task type repeat from date to date, the value will be the start date of event
	* for recurrent task type repeat every day or a day in week, the value will the the start time of each day
	* Column 4: End date/time for task,
	* for event task, the value will be the end time and date of the event
	* for recurrent task type repeat from date to date, the value will be the end date of event
	* for recurrent task type repeat every day or a day in week, the value will the the end time of each day
	* Column 5: Period of an recurrent task
	* For recurrent task type repeat from date to date, the value will be the start and end time of each day
	* For recurrent task type repeat every day or a day in week, the value will either "Every day" or "Every <a day in week>"
	* Column 6: Priority of a task, for display, it's either High, Medium or Low, comes with the color
	* Column 7: Group name of a task
	* Column 8: Status of a task, either "done" or "undone"
	*/
	public String[] getDataFromTask(Task task, int i) {
	
		String[] data = new String[mColumnNames.length];
		data[AppConst.UI_CONST.COLUMN_INDEX] = String.valueOf(i + 1);
		data[AppConst.UI_CONST.COLUMN_TASK_NAME] = removeSlash(task.getName());
		data[AppConst.UI_CONST.COLUMN_DEADLINE] = mDateTimeHelper.convertToDisplayFormat(task.getDeadline());
		
		int repeatedType = task.getRepeatedType();
		switch (repeatedType) {
			case AppConst.REPEATED_TYPE.FROM_TO: 
				data[AppConst.UI_CONST.COLUMN_PERIOD] = task.getPeriod();
				data[AppConst.UI_CONST.COLUMN_START_DATE_TIME] = mDateTimeHelper.convertDateMonthToDisplayFormat(task.getStartDate());
				data[AppConst.UI_CONST.COLUMN_END_DATE_TIME] = mDateTimeHelper.convertDateMonthToDisplayFormat(task.getEndDate());
				break;
			case AppConst.REPEATED_TYPE.EVERY_WEEK:
			case AppConst.REPEATED_TYPE.EVERYDAY:
				data[AppConst.UI_CONST.COLUMN_START_DATE_TIME] = mDateTimeHelper.getStringStartTimeForStringPeriod(task.getPeriod());
				data[AppConst.UI_CONST.COLUMN_END_DATE_TIME] = mDateTimeHelper.getStringEndTimeForStringPeriod(task.getPeriod());
				if (repeatedType == AppConst.REPEATED_TYPE.EVERY_WEEK) { 
					data[AppConst.UI_CONST.COLUMN_PERIOD] = AppConst.UI_CONST.EVERY + mDateTimeHelper.getStringDayInWeekForDate(task.getStartDate());
				} else {
					data[AppConst.UI_CONST.COLUMN_PERIOD] = AppConst.UI_CONST.EVERYDAY;
				}
				break;
			
			default: 
				data[AppConst.UI_CONST.COLUMN_START_DATE_TIME] = mDateTimeHelper.convertToDisplayFormat(task.getStartDate());
				data[AppConst.UI_CONST.COLUMN_END_DATE_TIME] = mDateTimeHelper.convertToDisplayFormat(task.getEndDate());
				data[AppConst.UI_CONST.COLUMN_PERIOD] = "";
		}
		data[AppConst.UI_CONST.COLUMN_PRIORITY] = task.getPriority();
		data[AppConst.UI_CONST.COLUMN_PRIORITY] = data[AppConst.UI_CONST.COLUMN_PRIORITY].substring(0, 1).toUpperCase() + data[AppConst.UI_CONST.COLUMN_PRIORITY].substring(1);
		data[AppConst.UI_CONST.COLUMN_GROUP] = removeSlash(task.getGroup());
		data[AppConst.UI_CONST.COLUMN_STATUS] = task.getStatus();
		return data;
	}
	
	/*
	** Users have to use slash "\" in front if they use a key word in the task name or task group.
	** for display, we remove the slash before displaying
	*/
	protected String removeSlash(String st) {
		if (st == null || st.equals("")) {
			return st;
		}
		
		String splits[] = st.split(" ");
		if (st.length() == 0) {
			return st;
		}
		
		String[] keys = AppConst.KEY_WORD.keywords;
		
		for(int i = 0; i < splits.length; i++) {
			if (splits[i].startsWith(AppConst.UI_CONST.SLASH) && splits[i].length() > 1) {
				String s = splits[i].substring(1, splits[i].length());
				boolean isKeyword = false;
				for(int j = 0; j < keys.length; j++) {
					if (keys[j].equals(s)) {
						isKeyword = true;
						break;
					}
				}
				if (isKeyword) {
					splits[i] = s;
				}
			}
		}
		
		String result = "";
		for(int i = 0; i < splits.length; i++) {
			if (i > 0) {
				result += " ";
			}
			result += splits[i];
		}
		return result;
		
	}
	
	protected void createTableToDisplayTasks() {
    	DefaultTableModel tableModel = (DefaultTableModel)mTable.getModel();
        tableModel.setColumnIdentifiers(mColumnNames);
        tableModel.setRowCount(0);
        
        for (int i = 0 ; i < mColumnNames.length; i++) {
        	if (i != AppConst.UI_CONST.COLUMN_STATUS) {
        		mTable.getColumnModel().getColumn(i).setPreferredWidth(mColumnWidth[i]);
        		mTable.getColumnModel().getColumn(i).setMaxWidth(mColumnWidth[i]);
        	}
        }
        
        // Set data for table
        for (int i = 0; i < mDataTaskList.size(); i++) {
            String[] data = getDataFromTask(mDataTaskList.get(i), i);
            tableModel.addRow(data);
        }
    
    	DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0 ; i < mColumnNames.length; i++) {
			if (i != AppConst.UI_CONST.COLUMN_GROUP && i != AppConst.UI_CONST.COLUMN_TASK_NAME ) {
				mTable.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        	}
        }
       
        ((DefaultTableCellRenderer)mTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        mTable.setModel(tableModel);
        tableModel.fireTableDataChanged();
    }
	
	
	public String createTimetable(String userCommand) {
	    
    	userCommand = removeSpace(userCommand);    		
    	String startDate = mCommandParser.getStartDateForTimetable(userCommand);
    	String endDate = mCommandParser.getEndDateForTimetable(userCommand);
    	if (startDate == null || startDate.equals("") || endDate == null || endDate.equals("")) {
    		createTableToDisplayTasks();
    		return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
    	}
    	
    	int from = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(startDate), mDateTimeHelper.getMonthFromStringDate(startDate));
    	int to = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(endDate), mDateTimeHelper.getMonthFromStringDate(endDate));
		
    	if (from > to) {
			createTableToDisplayTasks();
			return AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT;
		}
		DefaultTableModel tableModel = (DefaultTableModel)mTable.getModel();
    	tableModel.setRowCount(0);
    	tableModel.setColumnIdentifiers(mTimeTableColumnNames);		
    	mTable.getColumnModel().getColumn(0).setPreferredWidth(AppConst.UI_CONST.DEFAULT_COLUMN_WIDTH);
    	mTable.getColumnModel().getColumn(0).setMaxWidth(AppConst.UI_CONST.DEFAULT_COLUMN_WIDTH);
		 
		for(int i = from; i <= to; i++) {   	
			String date = mDateTimeHelper.getDateForNumberOfDays(i);
			
			// TIME_ZERO = 00:00
			date += AppConst.UI_CONST.TIME_ZERO;
			int[] timetable = mDateTimeHelper.getTimetableForDate(date, mDataTaskList);
			String[] data = new String[mTimeTableColumnNames.length];
			data[0] = mDateTimeHelper.convertDateMonthToDisplayFormat(date) + " (" + mDateTimeHelper.getStringDayInWeekForDate(date) + ")";
			for(int j = 1; j < mTimeTableColumnNames.length; j++) {
				if (timetable[j-1] != -1) {
					data[j] = mDataTaskList.get(timetable[j - 1]).getName() + AppConst.UI_CONST.SPACE + AppConst.TASK_FIELD.PRIORITY + mDataTaskList.get(timetable[j - 1]).getPriority();
				} else {
					data[j] = "";
				}
			}
			tableModel.addRow(data);
		}
		tableModel.fireTableDataChanged();
    	return "";
    }
	
	private String removeSpace(String userCommand) {
		String[] commands = userCommand.split(" ");
		String result = "";
		for(int i = 0; i < commands.length; i++) {
			if (!commands[i].equals("") && !commands[i].equals(" ")) {
				if (result.length() > 0) {
					result += " ";
				}
				result += commands[i];
			}
		}
		return result;
	}
	
}
