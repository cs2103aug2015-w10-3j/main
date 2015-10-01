import java.util.ArrayList;
import java.io.*;

public class TaskCommandParse {
	
	public TaskCommandParse() {
		
	}

	protected String[] getCommandInfo(String userCommand) {
		String command = getCommandType(userCommand);
		String taskInfo = getCommandArgument(userCommand);
        String[] result = new String[10];
        result[0] = command;
        result[1] = taskInfo;
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
