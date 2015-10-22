import java.util.*;

public class Command{
	private String commandType;
	private String commandArgument;
	private Task newTask;
	private Task updatedTask;

	public Command(){
		newTask = new Task("");
		updatedTask = new Task("");
	}

	public String getCommandType(){
		return commandType;
	}

	public void setCommandType(String newCommandType){
		commandType = newCommandType;
	}

	public String getCommandArgument(){
		return commandArgument;
	}

	public void setCommandArgument(String newCommandArgument){
		commandArgument = newCommandArgument;
	}

	public Task getNewTask(){
		return newTask;
	}

	public void setNewTask(Task newNewTask){
		newTask = newNewTask;
	}

	public Task getUpdatedTask(){
		return newTask;
	}

	public void setUpdatedTask(Task newUpdatedTask){
		updatedTask = newUpdatedTask;
	}

}