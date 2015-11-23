//@@author A0093929M
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
			"add something",
			"deleteall",
			"add floating task",
			"add deadline task by 9am 15/12",
			"add event from 9am to 10am 15/12",
			"add recurrent task from 2am to 3am repeat every tue",
			"add task1 priority high",
			"add task2 group test",
			"add task3 by 2pm 04 Dec group test priority high",
			"add task1 priority high",
			"add task4 from 2am to 5am repeat every tue priority low",
			"add task5 by 20/12 priority low",
			"add task6 by 5pm 05/12",
			"add task7 by 5am 05 Dec group cs2101 priority high",
			"add event2 from 8am to 1pm repeat every Thu priority high"
			
		};
		
		final String[] EXPECTED_FEEDBACK = {
			String.format(AppConst.MESSAGE.TASK_ADDED, "something"),
			AppConst.MESSAGE.DELETED_ALL,
			String.format(AppConst.MESSAGE.TASK_ADDED, "floating task"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "deadline task by 9am 15/12"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "event from 9am to 10am 15/12"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "recurrent task from 2am to 3am repeat every tue"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task1 priority high"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task2 group test"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task3 by 2pm 04 Dec group test priority high"),
			AppConst.MESSAGE.TASK_EXISTS,
			AppConst.MESSAGE.OVERLAP_TIME_PERIOD,
			String.format(AppConst.MESSAGE.TASK_ADDED, "task5 by 20/12 priority low"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task6 by 5pm 05/12"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "task7 by 5am 05 Dec group cs2101 priority high"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "event2 from 8am to 1pm repeat every Thu priority high"),
		};
		
		mMainLogic = new MainLogic();
		pointer.setPointer(new ArrayList<Task>());
		
		for (int i=0;i<TEST_ADD_COMMANDS.length;i++){
			
			assertEquals(EXPECTED_FEEDBACK[i],mMainLogic.process(TEST_ADD_COMMANDS[i], pointer));
			
		}
	}
	
	@Test 
	public void testUpdateCommands() {
		final String[] TEST_UPDATE_COMMANDS = {
			"update id 2 TO priority high",
			"update id 4 TO from 10am to 11am repeat every tue",
			"update id 2 TO by 10am tmr"
		};
		
		testAddCommands();
		for (int i=0;i<TEST_UPDATE_COMMANDS.length;i++){
			assertEquals(mMainLogic.process(TEST_UPDATE_COMMANDS[i], pointer),AppConst.MESSAGE.UPDATED_SUCCESSFUL);
			
		}
	}
	
	@Test 
	public void testDeleteCommands() {
		final String[] TEST_DELETE_COMMANDS = {
			"delete task",
			"delete id 1",
			"delete id 1"
		};
		final String[] EXPECTED_FEEDBACK = {
			AppConst.MESSAGE.MANY_TASKS_MATCHED,
			AppConst.MESSAGE.REMOVE_TASKS_SUCCESSFUL,
			AppConst.MESSAGE.REMOVE_TASKS_SUCCESSFUL
		};
		
		testAddCommands();
		for (int i=0;i<TEST_DELETE_COMMANDS.length;i++){
			assertEquals(EXPECTED_FEEDBACK[i],mMainLogic.process(TEST_DELETE_COMMANDS[i], pointer));
			
		}
	}
	
	@Test 
	public void testSetfileCommand() {
		final String[] TEST_SETFILE_COMMANDS = {
			"setfile newfile",
			"add something",
			"deleteall",
			"add test task",
			"setfile anotherfile",
			"add something",
			"deleteall",
			"add test task 2",
			"setfile newfile",
			"delete test task"
		};
		final String[] EXPECTED_FEEDBACK = {
			String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, "newfile"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "something"),
			AppConst.MESSAGE.DELETED_ALL,
			String.format(AppConst.MESSAGE.TASK_ADDED, "test task"),
			String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, "anotherfile"),
			String.format(AppConst.MESSAGE.TASK_ADDED, "something"),
			AppConst.MESSAGE.DELETED_ALL,
			String.format(AppConst.MESSAGE.TASK_ADDED, "test task 2"),
			String.format(AppConst.MESSAGE.CHANGED_SUCCESSFUL, "newfile"),
			String.format(AppConst.MESSAGE.REMOVED_SUCCESSFUL, "test task"),
		};
		
		testAddCommands();
		for (int i=0;i<TEST_SETFILE_COMMANDS.length;i++){
			assertEquals(EXPECTED_FEEDBACK[i],mMainLogic.process(TEST_SETFILE_COMMANDS[i], pointer));
			
		}
	}
	
	@Test 
	public void testShowbyDeadline() {
		
		testAddCommands();
		assertEquals(AppConst.MESSAGE.DISPLAY_BY_DEADLINE,mMainLogic.process("showby deadline", pointer));
		ArrayList<Task> mTasks = pointer.getPointer();
		boolean sorted = true;
		Comparators.TaskDeadlineCompare comparator = new Comparators.TaskDeadlineCompare();
		
		for (int i=1;i<mTasks.size();i++){
			if (comparator.compare(mTasks.get(i-1),mTasks.get(i)) > 1) sorted = false;
		}
		assertTrue(sorted);
	}
	
	@Test 
	public void testShowbyPriority() {
		
		testAddCommands();
		assertEquals(AppConst.MESSAGE.DISPLAY_BY_PRIORITY,mMainLogic.process("showby priority", pointer));
		ArrayList<Task> mTasks = pointer.getPointer();
		boolean sorted = true;
		Comparators.TaskPriorityCompare comparator = new Comparators.TaskPriorityCompare();
		
		for (int i=1;i<mTasks.size();i++){
			if (comparator.compare(mTasks.get(i-1),mTasks.get(i)) > 1) sorted = false;
		}
		assertTrue(sorted);
	}
	
	@Test 
	public void testShowbyGroup() {
		
		testAddCommands();
		assertEquals(AppConst.MESSAGE.DISPLAY_BY_GROUP,mMainLogic.process("showby group", pointer));
		ArrayList<Task> mTasks = pointer.getPointer();
		boolean sorted = true;
		Comparators.TaskGroupCompare comparator = new Comparators.TaskGroupCompare();
		
		for (int i=1;i<mTasks.size();i++){
			if (comparator.compare(mTasks.get(i-1),mTasks.get(i)) > 1) sorted = false;
		}
		assertTrue(sorted);
	}
	
	@Test 
	public void testShowbyStarttime() {
		
		testAddCommands();
		assertEquals(AppConst.MESSAGE.DISPLAY_BY_START_DATE,mMainLogic.process("showby start date", pointer));
		ArrayList<Task> mTasks = pointer.getPointer();
		boolean sorted = true;
		Comparators.TaskStartDateCompare comparator = new Comparators.TaskStartDateCompare();
		
		for (int i=1;i<mTasks.size();i++){
			if (comparator.compare(mTasks.get(i-1),mTasks.get(i)) > 1) sorted = false;
		}
		assertTrue(sorted);
	}
	
	@Test 
	public void testShowbyEndtime() {
		
		testAddCommands();
		assertEquals(AppConst.MESSAGE.DISPLAY_BY_END_DATE,mMainLogic.process("showby end date", pointer));
		ArrayList<Task> mTasks = pointer.getPointer();
		boolean sorted = true;
		Comparators.TaskEndDateCompare comparator = new Comparators.TaskEndDateCompare();
		
		for (int i=1;i<mTasks.size();i++){
			if (comparator.compare(mTasks.get(i-1),mTasks.get(i)) > 1) sorted = false;
		}
		assertTrue(sorted);
	}
	
	
}

