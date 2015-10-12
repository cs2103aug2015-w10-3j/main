import java.util.ArrayList;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class TaskCommandParser {
	
	public TaskCommandParser() {
		
	}

	protected String[] getCommandInfo(String userCommand) {
		String command = getCommandType(userCommand);
		String taskInfo = getCommandArgument(userCommand);
        String[] result = new String[10];
        result[0] = command;
        result[1] = taskInfo;
        if (result[0].equals("add")) {
            result[2] = getDeadlineForTask(taskInfo);
        }
        return result;
	}

    boolean checkDateFormat(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        try {
            Date date = formatter.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String getDeadlineForTask(String taskInfo) {
        String[] splits = taskInfo.split(" ");
        if (splits.length > 1) {
            for(int i=splits.length-1; i>=0; i--) {
                if (splits[i].equals("by")) {
                    if (checkDateFormat(splits[i+1])) {
                        System.out.println(splits[i+1]);
                        return splits[i+1];
                    }
                }
            }
        }
        return "";
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
