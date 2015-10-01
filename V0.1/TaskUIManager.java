import java.io.*;

public class TaskUIManager {

	private static String goodByeMessage = "Goodbye!";
	private static String welcomeMessage = "Welcome to To-Do list. These are your tasks: ";

	public TaskUIManager() {

	}

	static TaskLogic mTaskLogic = new TaskLogic();
	public static void main(String[] argv) {
		displayMessage(welcomeMessage);
		String message = mTaskLogic.showAll();
		displayMessage(message);
		while (true) {
			message = mTaskLogic.process();
			if (message == null) {
				displayMessage(goodByeMessage);
				break;
			}
			displayMessage(message);
		}
	}

	private static void displayMessage(String message) {
		System.out.println(message);
	}

}