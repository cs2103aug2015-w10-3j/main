//@@author A0110917M
package cs2103;
import java.util.*;


public class Tests {

	private static final String FILE_NAME = "test.txt";
	
	// Add
	private static final String ADD_COMMAND_CORRECT = "add do homework by 30/10 priority high group CS2103T";
	private static final String ADD_COMMAND_EXIST = "add do homework by 30/10 priority high group CS2103T";
	private static final String ADD_COMMAND_WRONG_TASK_NAME = "add by 30/10";
	private static final String ADD_COMMAND_WRONG_DEADLINE = "add do homework by 12/10";
	private static final String ADD_COMMAND_WRONG_PRIORITY = "add do homework priority normal";
	
	// Delete
	private static final String DELETE_NO_TASK = "delete ok";
	// add 'do something' and 'do another thing' first
	private static final String DELETE_MANY_MATCH = "delete do";
	private static final String DELETE_OK = "delete do something";
	
	// Update
	private static final String UPDATE_WRONG_TASK_NAME = "update do something TO ";
	private static final String UPDATE_NOT_FOUND = "update ok TO lol";
	private static final String UPDATE_EXIST = "update do something TO do another thing";

	private static Storage mStorage = new Storage();
	private static MainLogic mMainLogic = new MainLogic();
	private static ArrayList<Task> mListTask = new ArrayList<Task>();
	
	private static void clearFile() {
		mListTask = new ArrayList<Task>();
		mStorage.rewriteContent(mListTask);
	}
	
	public static void main(String[] args) {
		mMainLogic = new MainLogic();
		mStorage.setFileURL(FILE_NAME);
		clearFile();
		mMainLogic.initialiseTasks();
		
		ArrayListPointer dataTaskListPointer = new ArrayListPointer();
	
		String responseMessage = "";
		
		// Tests for add 
		responseMessage = mMainLogic.process(ADD_COMMAND_CORRECT, dataTaskListPointer);
		mListTask = dataTaskListPointer.getPointer();
		System.out.println(mListTask.size());
		System.out.println(responseMessage);
		assert (mListTask.size() == 1);
		assert (responseMessage.equals("Successful added 'do homework by 30/10 priority high group CS2103T' \n"));
		
		responseMessage = mMainLogic.process(ADD_COMMAND_EXIST, dataTaskListPointer);
		mListTask = dataTaskListPointer.getPointer();
		System.out.println(responseMessage);
		assert (mListTask.size() == 1);
		assert (responseMessage.equals(AppConst.MESSAGE.TASK_EXISTS));
		
		responseMessage = mMainLogic.process(ADD_COMMAND_WRONG_TASK_NAME, dataTaskListPointer);
		assert (responseMessage.equals(AppConst.MESSAGE.INVALID_TASK_NAME));
		
		responseMessage = mMainLogic.process(ADD_COMMAND_WRONG_DEADLINE, dataTaskListPointer);
		assert (responseMessage.startsWith("Invalid deadline or end date! Current date and time is"));
		
		responseMessage = mMainLogic.process(ADD_COMMAND_WRONG_PRIORITY, dataTaskListPointer);
		assert (responseMessage.equals(AppConst.MESSAGE.INVALID_PRIORITY));
		
		// Test for delete 
		responseMessage = mMainLogic.process(DELETE_NO_TASK, dataTaskListPointer);
		assert (responseMessage.equals(AppConst.MESSAGE.NOTHING_MATCHED));
		assert (mListTask.size() == 1);
		
		mMainLogic.process("add do something", dataTaskListPointer);
		mMainLogic.process("add do another thing", dataTaskListPointer);
		assert (mListTask.size() == 3);
		
		responseMessage = mMainLogic.process(DELETE_MANY_MATCH, dataTaskListPointer);
		assert (responseMessage.equals(AppConst.MESSAGE.MANY_TASKS_MATCHED));
		assert (mListTask.size() == 3);
		
		responseMessage = mMainLogic.process(DELETE_OK, dataTaskListPointer);
		assert (responseMessage.equals(String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, "do something")));
		assert (mListTask.size() == 2);
		

		// Tests for update

		responseMessage = mMainLogic.process(UPDATE_WRONG_TASK_NAME, dataTaskListPointer);
		assert (responseMessage.equals(AppConst.MESSAGE.INVALID_UPDATE_FORMAT));
		
		
		responseMessage = mMainLogic.process(UPDATE_NOT_FOUND, dataTaskListPointer);
		assert (responseMessage.equals(String.format(AppConst.MESSAGE.TASK_NOT_FOUND, "ok")));
		
		mMainLogic.process("add do something", dataTaskListPointer);
		responseMessage = mMainLogic.process(UPDATE_EXIST, dataTaskListPointer);
		assert (responseMessage.equals(AppConst.MESSAGE.TASK_UPDATED_EXIST));
		
	}

} 
