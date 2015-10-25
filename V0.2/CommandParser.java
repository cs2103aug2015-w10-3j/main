
import java.io.*;
import java.util.*;
import java.lang.*;

public class CommandParser {
	
    private int mPosition = 0;
	
	private DateTimeHelper mDateTimeHelper = new DateTimeHelper();

	public CommandParser() {
		
	}
	
	protected Command parse(String userCommand) {
		
		mPosition = 0;
		Command command = new Command();
		String commandType = getCommandType(userCommand);
		command.setCommandType(commandType);
		command.setCommandArgument(getCommandSecondArgument(userCommand));
		command.setNewTask(null);
		command.setUpdatedTask(null);
		
		switch (commandType) {
			case AppConst.COMMAND_TYPE.ADD:
			case AppConst.COMMAND_TYPE.UPDATE:
				Task task = new Task("");
				String deadline = getDeadlineForTask(userCommand);
				String endDate = getEndDateForTask(userCommand);
				if (deadline != null && deadline.equals("")) {
					deadline = endDate;
				}
				if (endDate != null && endDate.equals("")) {
					endDate = deadline;
				}
				task.setDeadline(deadline);
				task.setStartDate(getStartDateForTask(userCommand));
				task.setEndDate(endDate);
				task.setPriority(getPriorityForTask(userCommand));
				task.setGroup(getGroupForTask(userCommand));
				task.setTaskInfo(getCommandArgument(userCommand));
				task.setName(getTaskInfo(userCommand));
		
				command.setNewTask(task);
				command.setUpdatedTask(task);
				break;
			default:
				command.setCommandArgument(getCommandArgument(userCommand));
				
		}
		return command;
	}

    private String getDeadlineForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        String time = "";
        for(int i = splits.length - 1; i >= 0; i--) {
            if (splits[i].equals("by") || splits[i].equals("before")) {
                String result = "";
                if (mPosition == 0) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
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
                if (splits[i+1].equals("high") || splits[i+1].equals("low") || splits[i+1].equals("medium")) {
                    System.out.println(splits[i+1]);
                    if (mPosition == 0) {
                        mPosition = i;
                    } else {
                    	mPosition = Math.min(mPosition, i);
                    }
                    return splits[i+1];
                }
            }
        }
        //default is medium
        return "medium";
    }
    
    private String getStartDateForTask(String userCommand) {
   		String[] splits = userCommand.split(" ");
   		String result = mDateTimeHelper.getCurrentTimeString();
   		String time = "";
   		for(int i=splits.length-1; i>=0; i--) {
   			if (splits[i].equals("from")) {
   				if (mPosition == 0) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
   				}
   				for(int j=i+1; j<splits.length; j++) {
   					if (splits[j].equals("to") || splits[j].equals("priority") || splits[j].equals("group") || splits[j].equals("grp") || splits[j].equals("by") || splits[j].equals("before")) {
   						break;
   					} else {
   						if (j>i+1) {
   							time += " ";
   						}
   						time += splits[j];
   					}
   				}
	   			if (!time.equals("")) {
					time = mDateTimeHelper.getStringDateFromString(time, 1);
				}
				break;
   			}
   		}
   		if (time == null) {
   			return null;
   		}
   		if (!time.equals("")) {
   			if (mDateTimeHelper.compareStringDates(result, time) < 0) {
   				result = time;
   			}
   		}
   		return result;
    }
    
    private String getEndDateForTask(String userCommand) {
   		String[] splits = userCommand.split(" ");
   		String time = "";
   		for(int i=splits.length-1; i>=0; i--) {
   			if (splits[i].equals("to")) {
   				if (mPosition == 0) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
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
                System.out.println(splits[i+1]);
                if (mPosition == 0) {
   					mPosition = i;
   				} else {
   					mPosition = Math.min(mPosition, i);
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
        if (mPosition == 0) {
            mPosition = splits.length;
        }
        String result = "";
        for(int i = 1; i < mPosition; i++) {
            result += splits[i];
            if (i != mPosition - 1) {
                result += " ";
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
