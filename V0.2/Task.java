import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.text.*;

public class Task {
	private String name;
	private String priority="medium";
	private String group="";
	private String status="undone";
	private String deadline="";
	private String startDate="";
	private String endDate="";
	private String taskInfo;

	public Task(String newname) {
		name = newname;
	}

	public String getName(){
		return name;
	}

	public void setName(String newname){
		name = newname;
	}

	public String getPriority(){
		return priority;
	}

	public void setPriority(String newPriority){
		priority = newPriority;
	}

	public String getGroup(){
		return group;
	}

	public void setGroup(String newGroup){
		group = newGroup;
	}

	public String getDeadline(){
		return deadline;
	}

	public void setDeadline(String newDeadline){
		deadline = newDeadline;
	}

	public String getStartDate(){
		return startDate;
	}

	public void setStartDate(String newStartDate){
		startDate = newStartDate;
	}

	public String getEndDate(){
		return startDate;
	}

	public void setEndDate(String newEndDate){
		endDate = newEndDate;
	}

	public String getStatus(){
		return status;
	}

	public void setStatus(String newStatus){
		status = newStatus;
	}
	
	public String getTaskInfo() {
		return taskInfo;
	}
	
	public void setTaskInfo(String newTaskInfo) {
		taskInfo = newTaskInfo;
	}

	public Task copy(){
		Task newTask = new Task(name);
		newTask.setDeadline(deadline);
		newTask.setGroup(group);
		newTask.setPriority(priority);
		newTask.setStartDate(startDate);
		newTask.setEndDate(endDate);
		newTask.setStatus(status);
		newTask.setTaskInfo(taskInfo);

		return newTask;
	}
}

