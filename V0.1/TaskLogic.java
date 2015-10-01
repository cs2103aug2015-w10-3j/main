
import java.io.*;


public class TaskLogic {


	private static String ENTER = "Enter";
    
	TaskCommandParse mTaskCommandParse = new TaskCommandParse();
	private final String messageSuccessful = "Successful";

	public TaskLogic() {

	}

	protected String process(String userCommand) {
		String command = "", taskInfo = "";
		String[] commandInfo = mTaskCommandParse.getCommandInfo(userCommand);
		command = commandInfo[0];  taskInfo = commandInfo[1];
		if (command.equals("exit")) {
			return null;
		} else {
			return command + ' ' + taskInfo + '\n';
		}
	}

	protected String showAll() {
		String result = "";
		result = "1\n2\n3\n4\n";
		return result;
	}


}
