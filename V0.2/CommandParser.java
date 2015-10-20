import java.util.ArrayList;
import java.io.*;

public class CommandParser {
	
    private int mPosition = 0;

	public CommandParser() {
		
	}

	protected String[] getCommandInfo(String userCommand) {
        String[] result = new String[10];
        result[0] = getCommandType(userCommand);
        if (result[0].equals("add")) {
            result[2] = getDeadlineForTask(userCommand);
            result[3] = getPriorityForTask(userCommand);
            result[4] = getGroupForTask(userCommand);
            result[1] = getTaskInfo(userCommand);
        } else {
            result[1] = getCommandArgument(userCommand);
        }
        return result;
	}

    private String getDeadlineForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        for(int i = splits.length - 1; i >= 0; i--) {
            if (splits[i].equals("by") || splits[i].equals("before")) {
                String result = "";
                mPosition = i;
                for(int j = i+1; j < splits.length; j++) {
                    if (splits[j].equals("grp") || splits[j].equals("group") || splits[j].equals("priority")) {
                        break;
                    } else {
                        if (j != i+1) {
                            result += " ";
                        }
                        result += splits[j];
                    }
                }
                System.out.println(result);
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
                    }
                    return splits[i+1];
                }
            }
        }
        //default is medium
        return "medium";
    }

    private String getGroupForTask(String userCommand) {
        String[] splits = userCommand.split(" ");
        for(int i=splits.length - 1; i >= 0; i--) {
            if (splits[i].equals("grp") || splits[i].equals("group")) {
                System.out.println(splits[i+1]);
                if (mPosition == 0) {
                    mPosition = i;
                }
                return splits[i+1];
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
        
        String command = "";
        for(int i=0; i<userCommand.length(); i++) {
            if (userCommand.charAt(i) == ' ') {
                break;
            } else {
                command += userCommand.charAt(i);
            }
        }
        return command;   
    }

    private String getCommandArgument(String userCommand) {
    	String commandArgument = "";
        String command = getCommandType(userCommand);
        for(int i=command.length()+1; i<userCommand.length(); i++) {
            commandArgument = commandArgument + userCommand.charAt(i);
        }
        return commandArgument;
    }

}
