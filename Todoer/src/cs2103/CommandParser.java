package cs2103;
import java.util.*;

public class CommandParser {
	
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
				command.setNewTask(getTaskFromString(commandType, userCommand));
				break;
			
			case AppConst.COMMAND_TYPE.DONE:
			case AppConst.COMMAND_TYPE.UNDONE:
			case AppConst.COMMAND_TYPE.DELETE:
				if (commands.length < 2) {
					command.setNewTask(null);
				} else if (commands[1].equals(AppConst.TASK_FIELD.ID)) {
					if (commands.length < 3) {
						command.setNewTask(null);
					} else {
						String st = "";
						for(int i = 1; i < commands.length; i++) {
							if (i > 1) {
								st += " ";
							}
							st += commands[i];
						}
						command.setCommandArgument(st);
					}
				} else {
					command.setNewTask(getTaskFromString(commandType, userCommand));
				}
				break;
			case AppConst.COMMAND_TYPE.UPDATE:
				if (!userCommand.contains(AppConst.KEY_WORD.TO.toUpperCase())) {
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
				if (!commands[1].equals(AppConst.TASK_FIELD.ID)) {
					oldTaskInfo = commandType + " " + oldTaskInfo;
					command.setNewTask(getTaskFromString(commandType, oldTaskInfo));
				} else {
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
		if (!splits[1].equals(AppConst.TASK_FIELD.ID)) {
			for(int i = 1; i < splits.length; i++) {
				if (splits[i].equals(AppConst.KEY_WORD.TO.toUpperCase())) {
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
			for(int i = 0; i < splits[2].length(); i++) {
				if (splits[2].charAt(i) < '0' || splits[2].charAt(i) > '9') {
					return "";
				}
			}
			return splits[2];
		}
	}
	
	private String getNewTaskInfoForUpdate(String userCommand) {
		String[] splits = userCommand.split(" ");
		String result = "";
		for(int i = 1; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.TO.toUpperCase())) {
				for(int j = i+1; j < splits.length; j++) {
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
		for(int i = 0; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.REPEAT)) {
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
				
		if (priority != null && priority.equals("")) {
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
				
		if (priority != null && priority.equals("")) {
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
		
		String splits[] = userCommand.split(" ");
		int position = 0;
		for(int i = 0; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.REPEAT)) {
				position = i;
				break;
			}	
		}
		String startDate = null;
		String endDate = null;
		if (position < splits.length-1) {
			if (splits[position+1].equals(AppConst.KEY_WORD.FROM)) {
				startDate = getStartDateForPeriod(userCommand);
				endDate = getEndDateForPeriod(userCommand);
				if (startDate != null) {
					if (endDate == null) {
						endDate = AppConst.DATE_TIME.END_YEAR_DATE_TIME;
					}
				}
				repeatedType = 1;
			} else if (splits[position+1].equals(AppConst.DATE_TIME.EVERYDAY) || (splits[position+1].equals(AppConst.DATE_TIME.EVERY) && position+2<splits.length && splits[position+2].equals(AppConst.DATE_TIME.DAY)))  {
				repeatedType = 3;
				startDate = mDateTimeHelper.getCurrentTimeString();
				endDate = AppConst.DATE_TIME.END_YEAR_DATE_TIME;
			} else {
				repeatedType = 2;
				String day = "";
				if (splits[position+1].equals(AppConst.DATE_TIME.EVERY)) {
					if (position + 2 < splits.length) {
						day = mDateTimeHelper.getDateFromDayInCurrentWeek(splits[position+2]);
					}
				} else {
					day = mDateTimeHelper.getDateFromDayInCurrentWeek(splits[position+1]);
				}
				if (day != null) {
					startDate = day + " " + AppConst.DATE_TIME.START_TIME;
					endDate = day + " " + AppConst.DATE_TIME.END_TIME;
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
		for(int i = 0; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.REPEAT)) {
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
		for(int i = 0; i < position; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.FROM)) {
				if (mPosition == -1) {
					mPosition = i;
				} else {
					mPosition = Math.min(mPosition, i);
				}
				for(int j = i + 1; j < position; j++) {
					if (splits[j].equals(AppConst.KEY_WORD.TO)) {
						break;
					} else {
						startTime += splits[j];
					}
				}
			}
		}
		
		
		String endTime = "";
		for(int i = 0; i < position; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.TO)) {
				if (mPosition == -1) {
					mPosition = i;
				} else {
					mPosition = Math.min(mPosition, i);
				}
				for(int j = i+1; j < position; j++) {
					if (splits[j].equals(AppConst.KEY_WORD.REPEAT)) {
						break;
					} else {
						endTime += splits[j];
					}
				}
			}
		}
		
		startTime = mDateTimeHelper.getTimeFromString(startTime, 1);
		endTime = mDateTimeHelper.getTimeFromString(endTime, 2);
		
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
		for(int i = 0; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.REPEAT)) {
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
		if (!splits[position+1].equals(AppConst.KEY_WORD.FROM)) {
			return null;
		}
		String startDate = "";
		for(int i = position+2; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.TO)) {
				break;
			}
			if (i > position+2) {
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
			
		return result + " " + AppConst.DATE_TIME.START_TIME;
	}
	
	private String getEndDateForPeriod(String userCommand) {
	
		String[] splits = userCommand.toLowerCase().split(" ");
		int position = 0;
		for(int i = 0; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.REPEAT)) {
				position = i;
				break;
			}
		}
		String endDate = "";
		for(int i = position+1; i < splits.length; i++) {
			if (splits[i].equals(AppConst.KEY_WORD.TO)) {
				for(int j = i + 1; j < splits.length; j++) {
					if (splits[j].equals(AppConst.KEY_WORD.PRIORITY) || splits[j].equals(AppConst.KEY_WORD.GROUP) || splits[j].equals(AppConst.KEY_WORD.GRP) || splits[j].equals(AppConst.KEY_WORD.BY) || splits[j].equals(AppConst.KEY_WORD.BEFORE)) {
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
		
		return mDateTimeHelper.getDateMonthFromString(endDate, 2) + " " + AppConst.DATE_TIME.END_TIME;
	}
	

    private String getDeadlineForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        for(int i = splits.length - 1; i >= 0; i--) {
            if (splits[i].equals(AppConst.KEY_WORD.BY) || splits[i].equals(AppConst.KEY_WORD.BEFORE)) {
                String result = "";
                if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					continue;
   				}
                for(int j = i + 1; j < splits.length; j++) {
                    if (splits[j].equals(AppConst.KEY_WORD.GRP) || splits[j].equals(AppConst.KEY_WORD.GROUP) || splits[j].equals(AppConst.KEY_WORD.PRIORITY) || splits[j].equals(AppConst.KEY_WORD.FROM) || splits[j].equals(AppConst.KEY_WORD.TO)) {
                        break;
                    } else {
                        if (j != i + 1) {
                            result += " ";
                        }
                        result += splits[j];
                    }
                }
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
        for(int i = splits.length - 1; i >= 0; i--) {
            if (splits[i].equals(AppConst.KEY_WORD.PRIORITY)) {
            	if (mPosition == -1) {
                    mPosition = i;
                } else {
                    mPosition = Math.min(mPosition, i);
                }
                if (i == splits.length-1) { 
   					continue;
   				}
                if (splits[i+1].equals(AppConst.TASK_FIELD.HIGH) || splits[i+1].equals(AppConst.TASK_FIELD.LOW) || splits[i+1].equals(AppConst.TASK_FIELD.MEDIUM)) {
                    System.out.println(splits[i+1]);
                    return splits[i+1];
                } else if (!splits[i+1].equals(AppConst.KEY_WORD.TO) && !splits[i+1].equals(AppConst.KEY_WORD.FROM) && !splits[i+1].equals(AppConst.KEY_WORD.GROUP) && !splits[i+1].equals(AppConst.KEY_WORD.GRP) && !splits[i+1].equals(AppConst.KEY_WORD.BY) && !splits[i+1].equals(AppConst.KEY_WORD.BEFORE)) {
                	return null;
                }
            }
        }
        return "";
    }
    
    private String getStringDateForStartDate(String userCommand) {
    	String[] splits = userCommand.split(" ");
    	String result = "";
    	for(int i = 0; i < splits.length; i++) {
    		if (splits[i].equals(AppConst.KEY_WORD.FROM)) {
   				if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					break;
   				}
   				for(int j = i+1; j < splits.length; j++) {
   					if (splits[j].equals(AppConst.KEY_WORD.TO) || splits[j].equals(AppConst.KEY_WORD.PRIORITY) || splits[j].equals(AppConst.KEY_WORD.GROUP) || splits[j].equals(AppConst.KEY_WORD.GRP) || splits[j].equals(AppConst.KEY_WORD.BY) || splits[j].equals(AppConst.KEY_WORD.BEFORE)) {
   						break;
   					} else {
   						if (j > i+1) {
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
   		String time = getStringDateForStartDate(userCommand);
	   	if (!time.equals("")) {
			time = mDateTimeHelper.getStringDateFromString(time, 1);
		}
		return time;
    }
    
    private String getEndDateForTask(String userCommand) {
   		String[] splits = userCommand.split(" ");
   		String time = "";
   		for(int i = splits.length - 1; i >= 0; i--) {
   			if (splits[i].equals(AppConst.KEY_WORD.TO)) {
   				if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					continue;
   				}
   				for(int j = i+1; j < splits.length; j++) {
   					if (splits[j].equals(AppConst.KEY_WORD.PRIORITY) || splits[j].equals(AppConst.KEY_WORD.GROUP) || splits[j].equals(AppConst.KEY_WORD.GRP) || splits[j].equals(AppConst.KEY_WORD.BY) || splits[j].equals(AppConst.KEY_WORD.BEFORE) || splits[j].equals(AppConst.KEY_WORD.FROM)) {
   						break;
   					} else {
   						if (j > i + 1) {
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
        for(int i = splits.length - 1; i >= 0; i--) {
            if (splits[i].equals(AppConst.KEY_WORD.GRP) || splits[i].equals(AppConst.KEY_WORD.GROUP)) {
                if (mPosition == -1) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				if (i == splits.length-1) { 
   					continue;
   				}
                String result = "";
                for(int j = i + 1; j < splits.length; j++) {
                	if (splits[j].equals(AppConst.KEY_WORD.PRIORITY) || splits[j].equals(AppConst.KEY_WORD.BY) || splits[j].equals(AppConst.KEY_WORD.BEFORE) || splits[j].equals(AppConst.KEY_WORD.FROM) || splits[j].equals(AppConst.KEY_WORD.TO)) {
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
        for(int i = 1; i < splits.length; i++) {
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
   		if (st.contains(AppConst.KEY_WORD.FROM1) && !st.contains(AppConst.KEY_WORD.TO1)) {
   			return null;
   		}
   		if (!st.contains(AppConst.KEY_WORD.FROM1) && st.contains(AppConst.KEY_WORD.TO1)) {
   			return null;
   		}
   		
		String[] commands = userCommand.toLowerCase().split(" ");
		String result = "";
		boolean isHasFrom = false;
		for(int i = 0; i < commands.length; i++) {
			if (commands[i].equals(AppConst.KEY_WORD.FROM)) {
				for(int j = i + 1; j < commands.length; j++) {
				
					if (commands[j].equals(AppConst.KEY_WORD.TO)) {
						break;	
					}
					result += " " + commands[j];
				}
				isHasFrom = true;
				break;
			}
		}
		if (!isHasFrom) {
			for(int i = 1; i < commands.length; i++) {
				result += " " + commands[i];
			}
		}
		if (result.equals("")) {
			result = AppConst.DATE_TIME.THIS_MON;
		}
		
		result = mDateTimeHelper.getDateMonthFromString(result, 1);
		return result;
	}
	
	protected String getEndDateForTimetable(String userCommand) {
	
		String st = " " + userCommand + " ";
   		st = st.toLowerCase();
   		if (st.contains(AppConst.KEY_WORD.FROM1) && !st.contains(AppConst.KEY_WORD.TO1)) {
   			return null;
   		}
   		if (!st.contains(AppConst.KEY_WORD.FROM1) && st.contains(AppConst.KEY_WORD.TO1)) {
   			return null;
   		}
	
		String[] commands = userCommand.toLowerCase().split(" ");
		String result = "";
		boolean isHasTo = false;
		for(int i = 0; i < commands.length; i++) {
			if (commands[i].equals(AppConst.KEY_WORD.TO)) {
				for(int j = i + 1; j < commands.length; j++) {
					result += " " + commands[j];
				}
				isHasTo = true;
				break;
			}
		}
		if (!isHasTo) {
			for(int i = 1; i < commands.length; i++) {
				result += " " + commands[i];
			}
		}
		
		if (result.equals("")) {
			result = AppConst.DATE_TIME.THIS_SUN;
		}
		result = mDateTimeHelper.getDateMonthFromString(result, 2);
		return result;
	}
	
	
	private int getNumberFromString(String st, int pos) {
		int number = 0;
		for(int i = pos; i < st.length(); i++) {
			if (st.charAt(i) < '0' || st.charAt(i) > '9') {
				break;
			}
			number = number * 10 + (st.charAt(i) - '0');
		}
		return number;
	}
	
	private boolean isNumber(String st) {
		if (st == null || st.length() == 0) {
			return false;
		}
		for(int i = 0; i < st.length(); i++) {
			if (st.charAt(i) < '0' || st.charAt(i) > '9') {
				return false;
			}
		}
		return true;
	}
	
	protected ArrayList<Integer> getListOfId(String id) {
		String[] ids = id.split(" ");
		id = "";
		for(int i = 0; i < ids.length; i++) {
			if (i > 0) {
				if (isNumber(ids[i]) && isNumber(ids[i-1])) {
					id += " ";
				} else if (isNumber(ids[i-1]) && ids[i].charAt(0) >= '0' && ids[i].charAt(i) <= '9') {
					id += " ";
				}
			}
			id += ids[i];
		}
		id += " ";
		ArrayList<Integer> result = new ArrayList<Integer>();
		int pos = -1, i = 0;
		while (i < id.length()) {
			if (id.charAt(i) >= '0' && id.charAt(i) <= '9') {
				if (pos == -1) {
					pos = i;
				}
				i++;
			} else if (id.charAt(i) == '-') {
				if (pos != -1) {
					int start = getNumberFromString(id, pos);
					int end = getNumberFromString(id, i+1);
					if (start <= end && start != 0) {
						for(int j = start; j <= end; j++) {
							result.add(j);
						}
					} else {
						return null;
					}
					i++;  pos = -1;
					while (i < id.length()) {
						if (id.charAt(i) < '0' || id.charAt(i) > '9') {
							break;
						}
						i++;
					}
				} else {
					return null;
				}
			} else if (id.charAt(i) == '.' && i < id.length()-1 && id.charAt(i+1) == '.') {
				i++;
				if (pos != -1) {
					int start = getNumberFromString(id, pos);
					int end = getNumberFromString(id, i+1);
					if (start <= end && start != 0) {
						for(int j = start; j <= end; j++) {
							result.add(j);
						}
					} else {
						return null;
					}
					i++	;  pos = -1;
					while (i < id.length()) {
						if (id.charAt(i) < '0' || id.charAt(i) > '9') {
							break;
						}
						i++;
					}
				} else {
					return null;
				}
			} else if (id.charAt(i) == '.' || id.charAt(i) == ',' || id.charAt(i) == ' ') {
				if (pos != -1) {
					result.add(getNumberFromString(id, pos));
				}
				i++;  pos = -1;
			} else {
				return null;	
			}
		}
		return result;
	}

}
