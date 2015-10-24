import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.text.*;

public class Task{
	private String name;
	private String priority="medium";
	private String group="";
	private String status="undone";
	private String deadline="";
	private String startDate="";
	private String endDate="";
	private String taskInfo;
	static SimpleDateFormat standardDateFormat = new SimpleDateFormat("dd/MM hh:mm");

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

	//the standard date format dd/MM hh:mm is used in storage
	public String toString(){
		JSONObject task = new JSONObject();
		task.put("name", name);
		task.put("deadline", deadline);
		task.put("startDate", startDate);
		task.put("endDate", endDate);
		task.put("priority", priority);
		task.put("group", group);
		task.put("status", status);
		task.put("taskInfo", taskInfo);
		
		try{
			StringWriter out = new StringWriter();
	      	task.writeJSONString(out);
	      
	      	return out.toString();
	    }
	    catch (Exception e){
	    	e.printStackTrace();
	    	return null;
	    }

	}

	public static Task stringToTask(String str){
		JSONParser parser = new JSONParser();
		try{
		    Object obj = parser.parse(str);
		    JSONObject jsonObj = (JSONObject)obj;
		    Task task = new Task((String)jsonObj.get("name"));
		    
		    task.setDeadline((String)jsonObj.get("deadline"));
		    task.setStartDate((String)jsonObj.get("startDate"));
		    task.setEndDate((String)jsonObj.get("endDate"));
		    
		    task.setGroup((String)jsonObj.get("group"));
		    task.setPriority((String)jsonObj.get("priority"));
		    task.setStatus((String)jsonObj.get("status"));
		    
		    task.setTaskInfo((String)jsonObj.get("taskInfo"));

		    return task;

      	}catch(ParseException pe){
         	System.out.println("position: " + pe.getPosition());
         	System.out.println(pe);
         	return null;
      	}
	}
}

