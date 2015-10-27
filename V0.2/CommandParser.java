
import java.io.*;
import java.util.*;
import java.lang.*;

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
		
		switch (commandType) {
			case AppConst.COMMAND_TYPE.ADD:
			case AppConst.COMMAND_TYPE.CLOSE:
			case AppConst.COMMAND_TYPE.OPEN:
			case AppConst.COMMAND_TYPE.DELETE:
				command.setNewTask(getTaskFromString(commandType, userCommand));
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
				oldTaskInfo = commandType + " " + oldTaskInfo;
				newTaskInfo = commandType + " " + newTaskInfo;
				command.setNewTask(getTaskFromString(commandType, oldTaskInfo));
				command.setUpdatedTask(getTaskFromString(commandType, newTaskInfo));
				break;
			default:
				command.setCommandArgument(getCommandArgument(userCommand));
				
		}
		return command;
	}
	
	private String getOldTaskInfoForUpdate(String userCommand) {
		String[] splits = userCommand.split(" ");
		String result = "";
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
		mPosition = -1;
		Task task = new Task("");
		String deadline = getDeadlineForTask(userCommand);
		String startDate = getStartDateForTask(userCommand);
		String endDate = getEndDateForTask(userCommand);
		if (deadline != null && deadline.equals("")) {
			deadline = endDate;
		}
		if (endDate != null && endDate.equals("")) {
			endDate = deadline;
		}
				
		// handle for from DateTime to DateTime of Month case
		// get Month from endDate, put to startDate
		if (startDate == null && endDate != null && !endDate.equals("")) {
			startDate = getStringDateForStartDate(userCommand);
			String month = mDateTimeHelper.getMonthStringForDateTime(endDate);
			startDate = mDateTimeHelper.getStringDateFromString(month + " " + startDate, 1);
			if (startDate != null && !startDate.equals("")) {
				String currentDate = mDateTimeHelper.getCurrentTimeString();
				if (mDateTimeHelper.compareStringDates(startDate, currentDate)<0) {
					startDate = currentDate;
				}
			}
		}
				
		String priority = getPriorityForTask(userCommand);
				
		if (commandType.equals(AppConst.COMMAND_TYPE.ADD)) {
			if (startDate!=null && startDate.equals("")) {
				startDate = mDateTimeHelper.getCurrentTimeString();
			}
					
			if (priority!=null && priority.equals("")) {
				// Default medium
				priority = AppConst.TASK_FIELD.MEDIUM;
			}
		}
				
		task.setDeadline(deadline);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setPriority(priority);
		task.setGroup(getGroupForTask(userCommand));
		task.setTaskInfo(getCommandArgument(userCommand));
		task.setName(getTaskInfo(userCommand));
		return task;
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
   		System.out.println("End date: " + time);
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

}
