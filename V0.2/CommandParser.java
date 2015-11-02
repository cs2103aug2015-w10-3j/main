
import java.io.*;
import java.util.*;
import java.lang.*;

public class CommandParser {
	
	private final String EVERY = "every";
	private final String EVERYDAY = "everyday";
	private final String DAY = "day";
	
    private int mPosition = -1;
	
	private DateTimeHelper mDateTimeHelper = new DateTimeHelper();

	public CommandParser() {
		
	}
	
	protected Command parse(String userCommand) {
		
		Command command = new Command();
		String commandType = getCommandType(userCommand);
		command.setCommandType(commandType);
		command.setCommandArgument(getCommandSecondArgument(userCommand));
		command.setNewTask(null);
		command.setUpdatedTask(null);
		String[] commands = userCommand.split(" ");
		
		switch (commandType) {
			case AppConst.COMMAND_TYPE.ADD:
			case AppConst.COMMAND_TYPE.DONE:
			case AppConst.COMMAND_TYPE.UNDONE:
				command.setNewTask(getTaskFromString(commandType, userCommand));
				break;
				
			case AppConst.COMMAND_TYPE.DELETE:
				if (commands.length < 2) {
					command.setNewTask(null);
				} else if (commands[1].equals("id")) {
					if (commands.length < 3) {
						command.setNewTask(null);
					} else {
						command.setCommandArgument(commands[1] + " " + commands[2]);
					}
				} else {
					command.setNewTask(getTaskFromString(commandType, userCommand));
				}
				break;
			case AppConst.COMMAND_TYPE.UPDATE:
				if (!userCommand.contains("TO")) {
					command.setNewTask(null);
					command.setUpdatedTask(null);
					break;
				}
				String oldTaskInfo = getOldTaskInfoForUpdate(userCommand);
				String newTaskInfo = getNewTaskInfoForUpdate(userCommand);
				if (oldTaskInfo.equals("") || newTaskInfo.equals("")) {
					command.setNewTask(null);
					command.setUpdatedTask(null);
					break;
				}
				if (!commands[1].equals("id")) {
					oldTaskInfo = commandType + " " + oldTaskInfo;
					command.setNewTask(getTaskFromString(commandType, oldTaskInfo));
				} else {
					System.out.println("Update task id: " + commands[2]);
					command.setCommandArgument(commands[1] + " " + commands[2]);
					command.setNewTask(null);
				}
				
				newTaskInfo = commandType + " " + newTaskInfo;
				command.setUpdatedTask(getTaskFromString(commandType, newTaskInfo));
				break;
			default:
				command.setCommandArgument(getCommandArgument(userCommand));
				
		}
		return command;
	}
	
	private String getOldTaskInfoForUpdate(String userCommand) {
		String[] splits = userCommand.split(" ");
		if (splits.length < 2) {
			return "";
		}
		String result = "";
		if (!splits[1].equals("id")) {
			for(int i=1; i<splits.length; i++) {
				if (splits[i].equals("TO")) {
					break;
				}
				if (result.length() > 0) {
					result += " ";
				}
				result += splits[i];
			}
			return result;
		} else {
			if (splits.length < 3) {
				return "";
			}
			for(int i=0; i<splits[2].length(); i++) {
				if (splits[2].charAt(i)<'0' || splits[2].charAt(i)>'9') {
					return "";
				}
			}
			return splits[2];
		}
	}
	
	private String getNewTaskInfoForUpdate(String userCommand) {
		String[] splits = userCommand.split(" ");
		String result = "";
		for(int i=1; i<splits.length; i++) {
			if (splits[i].equals("TO")) {
				for(int j=i+1; j<splits.length; j++) {
					if (result.length() > 0) {
						result += " ";
					}
					result += splits[j];
				}
				break;
			}
		}
		return result;
	}

	private Task getTaskFromString(String commandType, String userCommand) {
		String[] splits = userCommand.split(" ");
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals("repeat")) {
				return getTaskRepeatFromString(commandType, userCommand);
			}
		}
		mPosition = -1;
		Task task = new Task("");
		String deadline = getDeadlineForTask(userCommand);
		String startDate = getStartDateForTask(userCommand);
		String endDate = getEndDateForTask(userCommand);
				
		// handle for from DateTime to DateTime of Month case
		// get Month from endDate, put to startDate
		if (startDate == null && endDate != null && !endDate.equals("")) {
			startDate = getStringDateForStartDate(userCommand);
			String month = mDateTimeHelper.getMonthStringForDateTime(endDate);
			startDate = mDateTimeHelper.getStringDateFromString(month + " " + startDate, 1);
			if (startDate == null || startDate.equals("")) {
				startDate = getStringDateForStartDate(userCommand);
				startDate = mDateTimeHelper.getTimeFromString(startDate, 1);
				if (startDate != null && !startDate.equals("")) {
					startDate = mDateTimeHelper.getDateMonthFromString(endDate, 1) + " " + startDate;
				}
			}
		}	
						
		String priority = getPriorityForTask(userCommand);
				
		if (priority!=null && priority.equals("")) {
			// Default medium
			if (commandType.equals(AppConst.COMMAND_TYPE.ADD)) {
				priority = AppConst.TASK_FIELD.MEDIUM;
			}
		}
		
		if (!mDateTimeHelper.isCorrectDate(startDate)) {
			startDate = null;
		}
		if (!mDateTimeHelper.isCorrectDate(endDate)) {
			endDate = null;
		}
		if (!mDateTimeHelper.isCorrectDate(deadline)) {
			deadline = null;
		}
				
		task.setDeadline(deadline);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setPriority(priority);
		task.setRepeatedType(0);
		task.setPeriod("");
		task.setGroup(getGroupForTask(userCommand));
		task.setTaskInfo(getCommandArgument(userCommand));
		task.setName(getTaskInfo(userCommand));
		return task;
	}
	
	private Task getTaskRepeatFromString(String commandType, String userCommand) {
		
		mPosition = -1;
		Task task = new Task("");
		
		String priority = getPriorityForTask(userCommand);
				
		if (priority!=null && priority.equals("")) {
			// Default medium
			if (commandType.equals(AppConst.COMMAND_TYPE.ADD)) {
				priority = AppConst.TASK_FIELD.MEDIUM;
			}
		}
		
		String deadline = getDeadlineForTask(userCommand);
		if (deadline != null && !deadline.equals("")) {
			deadline = null;
		}
		
		int repeatedType = 1;
		String periodTime = getPeriodForTask(userCommand);
		System.out.println("Period: " + periodTime);
		
		String splits[] = userCommand.split(" ");
		int position = 0;
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals("repeat")) {
				position = i;
				break;
			}	
		}
		String startDate = null;
		String endDate = null;
		if (position < splits.length-1) {
			if (splits[position+1].equals("from")) {
				startDate = getStartDateForPeriod(userCommand);
				endDate = getEndDateForPeriod(userCommand);
				if (startDate != null) {
					if (endDate == null) {
						endDate = "31/12 23:59";
					}
				}
				repeatedType = 1;
			} else if (splits[position+1].equals(EVERYDAY) || (splits[position+1].equals(EVERY) && position+2<splits.length && splits[position+2].equals(DAY)))  {
				repeatedType = 3;
				startDate = mDateTimeHelper.getCurrentTimeString();
				endDate = "31/12 23:59";
			} else {
				repeatedType = 2;
				String day = "";
				if (splits[position+1].equals(EVERY)) {
					if (position+2<splits.length) {
						day = mDateTimeHelper.getDateFromDayInCurrentWeek(splits[position+2]);
					}
				} else {
					day = mDateTimeHelper.getDateFromDayInCurrentWeek(splits[position+1]);
				}
				if (day != null) {
					startDate = day + " 00:00";
					endDate = day + " 23:59";
				}
			}
		}
		
		if (periodTime == null) {
			startDate = null;
			endDate = null;
		}
		
		task.setDeadline(deadline);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setPeriod(periodTime);
		task.setRepeatedType(repeatedType);
		task.setPriority(priority);
		task.setGroup(getGroupForTask(userCommand));
		task.setTaskInfo(getCommandArgument(userCommand));
		task.setName(getTaskInfo(userCommand));
		
		return task;
	}

	private String getPeriodForTask(String userCommand) {
		String[] splits = userCommand.split(" ");
		int position = 0;
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals("repeat")) {
				position = i;
				if (mPosition == -1) {
					mPosition = i;
				} else {
					mPosition = Math.min(mPosition, i);
				}
				break;
			}
		}
		String startTime = "";
		for(int i=0; i<position; i++) {
			if (splits[i].equals("from")) {
				if (mPosition == -1) {
					mPosition = i;
				} else {
					mPosition = Math.min(mPosition, i);
				}
				for(int j=i+1; j<position; j++) {
					if (splits[j].equals("to")) {
						break;
					} else {
						startTime += splits[j];
					}
				}
			}
		}
		
		
		String endTime = "";
		for(int i=0; i<position; i++) {
			if (splits[i].equals("to")) {
				if (mPosition == -1) {
					mPosition = i;
				} else {
					mPosition = Math.min(mPosition, i);
				}
				for(int j=i+1; j<position; j++) {
					if (splits[j].equals("repeat")) {
						break;
					} else {
						endTime += splits[j];
					}
				}
			}
		}
		
		startTime = mDateTimeHelper.getTimeFromString(startTime, 1);
		endTime = mDateTimeHelper.getTimeFromString(endTime, 2);
		
		System.out.println("Start time: " + startTime);
		System.out.println("End time: " + endTime);
		
		if (startTime == null || endTime == null || startTime.equals("") || endTime.equals("")) {
			return null;
		}
		if (startTime.compareTo(endTime)>=0) {
			return null;
		}
		return startTime + " " + endTime;
		
	}
	
	private String getStartDateForPeriod(String userCommand) {
	
		String[] splits = userCommand.toLowerCase().split(" ");
		int position = -1;
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals("repeat")) {
				position = i;
				if (mPosition == -1) {
					mPosition = i;
				} else {
					mPosition = Math.min(mPosition, i);
				}
				break;
			}
		}
		if (position == -1 || position > splits.length-1) {
			return null;
		}
		if (!splits[position+1].equals("from")) {
			return null;
		}
		String startDate = "";
		for(int i=position+2; i<splits.length; i++) {
			if (splits[i].equals("to")) {
				break;
			}
			if (i>position+2) {
				startDate += " ";
			}
			startDate += splits[i];
		}
		
		String result = mDateTimeHelper.getDateMonthFromString(startDate, 1);
		if (result == null || result.equals("")) {
			String endDate = getEndDateForPeriod(userCommand);
			if (endDate == null || endDate.equals("")) {
				return null;
			}
			startDate += mDateTimeHelper.getMonthStringForDateTime(endDate);
			result = mDateTimeHelper.getDateMonthFromString(startDate, 1);
		}
		
		System.out.println("Start date: " + result);
		
		return result + " 00:00";
	}
	
	private String getEndDateForPeriod(String userCommand) {
	
		String[] splits = userCommand.toLowerCase().split(" ");
		int position = 0;
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals("repeat")) {
				position = i;
				break;
			}
		}
		String endDate = "";
		for(int i=position+1; i<splits.length; i++) {
			if (splits[i].equals("to")) {
				for(int j=i+1; j<splits.length; j++) {
					if (splits[j].equals("priority") || splits[j].equals("group") || splits[j].equals("grp") || splits[j].equals("by") || splits[j].equals("before")) {
						break;
					}
					endDate += " " + splits[j];
				}
				break;
			}
		}
	
		if (endDate.equals("")) {
			return null;
		}
		
		return mDateTimeHelper.getDateMonthFromString(endDate, 2) + " 23:59";
	}
	

    private String getDeadlineForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        String time = "";
        for(int i = splits.length - 1; i >= 0; i--) {
            if (splits[i].equals("by") || splits[i].equals("before")) {
                String result = "";
                if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					continue;
   				}
                for(int j = i+1; j < splits.length; j++) {
                    if (splits[j].equals("grp") || splits[j].equals("group") || splits[j].equals("priority") || splits[j].equals("from") || splits[j].equals("to")) {
                        break;
                    } else {
                        if (j != i+1) {
                            result += " ";
                        }
                        result += splits[j];
                    }
                }
                System.out.println(result);
                if (!result.equals("")) {
                	result = mDateTimeHelper.getStringDateFromString(result, 2);
                }
                return result;
            }
        }
        return "";
    }

    private String getPriorityForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        for(int i=splits.length - 1; i >= 0; i--) {
            if (splits[i].equals("priority")) {
            	if (mPosition == -1) {
                    mPosition = i;
                } else {
                    mPosition = Math.min(mPosition, i);
                }
                if (i == splits.length-1) { 
   					continue;
   				}
                if (splits[i+1].equals("high") || splits[i+1].equals("low") || splits[i+1].equals("medium")) {
                    System.out.println(splits[i+1]);
                    return splits[i+1];
                } else if (!splits[i+1].equals("to") && !splits[i+1].equals("from") && !splits[i+1].equals("group") && !splits[i+1].equals("grp") && !splits[i+1].equals("by") && !splits[i+1].equals("before")) {
                	return null;
                }
            }
        }
        return "";
    }
    
    private String getStringDateForStartDate(String userCommand) {
    	String[] splits = userCommand.split(" ");
    	String result = "";
    	for(int i=0; i<splits.length; i++) {
    		if (splits[i].equals("from")) {
   				if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					break;
   				}
   				for(int j=i+1; j<splits.length; j++) {
   					if (splits[j].equals("to") || splits[j].equals("priority") || splits[j].equals("group") || splits[j].equals("grp") || splits[j].equals("by") || splits[j].equals("before")) {
   						break;
   					} else {
   						if (j>i+1) {
   							result += " ";
   						}
   						result += splits[j];
   					}
   				}
				return result;
   			}
    	}
    	return "";
    }
    
    private String getStartDateForTask(String userCommand) {
   		String[] splits = userCommand.split(" ");
   		String time = getStringDateForStartDate(userCommand);
	   	if (!time.equals("")) {
			time = mDateTimeHelper.getStringDateFromString(time, 1);
		}
		return time;
    }
    
    private String getEndDateForTask(String userCommand) {
   		String[] splits = userCommand.split(" ");
   		String time = "";
   		for(int i=splits.length-1; i>=0; i--) {
   			if (splits[i].equals("to")) {
   				if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					continue;
   				}
   				for(int j=i+1; j<splits.length; j++) {
   					if (splits[j].equals("priority") || splits[j].equals("group") || splits[j].equals("grp") || splits[j].equals("by") || splits[j].equals("before") || splits[j].equals("from")) {
   						break;
   					} else {
   						if (j>i+1) {
   							time += " ";
   						}
   						time += splits[j];
   					}
   				}
   				if (!time.equals("")) {
					time = mDateTimeHelper.getStringDateFromString(time, 2);
				}
				break;
   			}
   		}
   		return time;
    }

    private String getGroupForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        for(int i=splits.length - 1; i >= 0; i--) {
            if (splits[i].equals("grp") || splits[i].equals("group")) {
                if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					continue;
   				}
                String result = "";
                for(int j=i+1; j<splits.length; j++) {
                	if (splits[j].equals("priority") || splits[j].equals("by") || splits[j].equals("before") || splits[j].equals("from") || splits[j].equals("to")) {
                		break;
                	}
                	if (j > i + 1) {
                		result += " ";
                	}
                	result += splits[j];
                }
                return result;
            }
        }
        return "";
    }

    private String getTaskInfo(String userCommand) {
        String[] splits = userCommand.split(" ");
        if (splits.length <= 1) {
        	return "";
        }
        if (mPosition == -1) {
            mPosition = splits.length;
        }
        String result = "";
        if (mPosition > 1 ) {
		    for(int i = 1; i < mPosition; i++) {
		        result += splits[i];
		        if (i != mPosition - 1) {
		            result += " ";
		        }
		    }
		}   
        return result;
    }

	private String getCommandType(String userCommand) {
        
        String[] splits = userCommand.split(" ");
        return splits[0];
    }
    
    
    
    private String getCommandSecondArgument(String userCommand) {
        
        String[] splits = userCommand.split(" ");
        if (splits.length > 1) {
        	return splits[1];
        }
        return "";  
    }

    private String getCommandArgument(String userCommand) {
    	String[] splits = userCommand.split(" ");
        String result = "";
        for(int i=1; i<splits.length; i++) {
        	result += splits[i];
        	if (i != splits.length-1) {
        		result += " ";
        	}
       	}
       	return result;
    }
    
   	protected String getStartDateForTimetable(String userCommand) {
   	
   		String st = " " + userCommand + " ";
   		st = st.toLowerCase();
   		if (st.contains(" from ") && !st.contains(" to ")) {
   			return null;
   		}
   		if (!st.contains(" from ") && st.contains(" to ")) {
   			return null;
   		}
   		
		String[] commands = userCommand.toLowerCase().split(" ");
		String result = "";
		boolean isHasFrom = false;
		for(int i=0; i<commands.length; i++) {
			if (commands[i].equals("from")) {
				for(int j=i+1; j<commands.length; j++) {
				
					if (commands[j].equals("to")) {
						break;	
					}
					result += " " + commands[j];
				}
				isHasFrom = true;
				break;
			}
		}
		if (!isHasFrom) {
			for(int i=1; i<commands.length; i++) {
				result += " " + commands[i];
			}
		}
		if (result.equals("")) {
			result = "this mon";
		}
		
		result = mDateTimeHelper.getDateMonthFromString(result, 1);
		System.out.println("Timetable start date: " + result);
		return result;
	}
	
	protected String getEndDateForTimetable(String userCommand) {
	
		String st = " " + userCommand + " ";
   		st = st.toLowerCase();
   		if (st.contains(" from ") && !st.contains(" to ")) {
   			return null;
   		}
   		if (!st.contains(" from ") && st.contains(" to ")) {
   			return null;
   		}
	
		String[] commands = userCommand.toLowerCase().split(" ");
		String result = "";
		boolean isHasTo = false;
		for(int i=0; i<commands.length; i++) {
			if (commands[i].equals("to")) {
				for(int j=i+1; j<commands.length; j++) {
					result += " " + commands[j];
				}
				isHasTo = true;
				break;
			}
		}
		if (!isHasTo) {
			for(int i=1; i<commands.length; i++) {
				result += " " + commands[i];
			}
		}
		
		if (result.equals("")) {
			result = "this sun";
		}
		result = mDateTimeHelper.getDateMonthFromString(result, 2);
		System.out.println("Timetable end date: " + result);
		return result;
	}
	

}
