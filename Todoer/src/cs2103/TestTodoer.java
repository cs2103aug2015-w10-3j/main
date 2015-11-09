package cs2103;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


public class TestTodoer {
	
	MainLogic mMainLogic = new MainLogic();
	ArrayListPointer pointer = new ArrayListPointer();
	
	@Test
	public void testAddCommands() {
		final String[] TEST_ADD_COMMANDS = {
			"deleteall",
			"add floating task",
			"add deadline task by 9am 15/12",
			"add event from 9am to 10am 15/12",
			"add recurrent task from 2am to 3am repeat every tue",
			"add task1 priority high",
			"add task2 group test",
			"add task3 by 2pm tomorrow group test priority high"
		};
		
		final String[] EXPECTED_FEEDBACK = {
			AppConst.MESSAGE.DELETED_ALL,
			String.format(AppConst.MESSAGE.TASK_ADDED, "floating task"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "deadline task by 9am 15/12"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "event from 9am to 10am 15/12"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "recurrent task from 2am to 3am repeat every tue"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task1 priority high"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task2 group test"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task3 by 2pm tomorrow group test priority high")
		};
		
		mMainLogic = new MainLogic();
		pointer.setPointer(new ArrayList<Task>());
		
		for (int i=0;i<TEST_ADD_COMMANDS.length;i++){
			
			assertEquals(mMainLogic.process(TEST_ADD_COMMANDS[i], pointer),EXPECTED_FEEDBACK[i]);
			
		}
	}
	
	@Test 
	public void testUpdateCommands() {
		final String[] TEST_UPDATE_COMMANDS = {
			"update id 2 TO priority high",
			"update id 4 TO 10am to 11am repeat every tue",
			"update id 2 TO by 10am tmr"
		};
		
		testAddCommands();
		for (int i=0;i<TEST_UPDATE_COMMANDS.length;i++){
			assertEquals(mMainLogic.process(TEST_UPDATE_COMMANDS[i], pointer),AppConst.MESSAGE.UPDATED_SUCCESSFUL);
			
		}
	}
	
	@Test 
	public void testDeleteCommands() {
		final String[] TEST_UPDATE_COMMANDS = {
			"delete task",
			"update id 4 TO 10am to 11am repeat every tue",
			"update id 2 TO by 10am tmr"
		};
		
		
		
		testAddCommands();
		for (int i=0;i<TEST_UPDATE_COMMANDS.length;i++){
			assertEquals(mMainLogic.process(TEST_UPDATE_COMMANDS[i], pointer),AppConst.MESSAGE.UPDATED_SUCCESSFUL);
			
		}
	}
	

}

