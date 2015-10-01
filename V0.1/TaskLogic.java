
import java.io.*;

public class TaskLogic {

	TaskCommandParse mTaskCommandParse = new TaskCommandParse();
	private final String messageSuccessful = "Successful";

	public TaskLogic() {

	}

	protected String process() {
		String command = "", taskInfo = "";
		String[] userCommand = mTaskCommandParse.getCommand();
		command = userCommand[0];  taskInfo = userCommand[1];
		if (command.equals("exit")) {
			return null;
		} else {
			return messageSuccessful + command + taskInfo;
		}
	}

	protected String showAll() {
		String result = "";
		result = "1\n2\n3\n4\n";
		return result;
	}


}
