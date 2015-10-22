import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.text.*;

public class Task{
	private String name;
	private String priority="normal";
	private String group="";
	private String done="done";
	private Date deadline=null;
	private Date startDate=null;
	private Date endDate=null;
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

	public Date getDeadline(){
		return deadline;
	}

	public void setDeadline(Date newDeadline){
		deadline = newDeadline;
	}

	public Date getStartDate(){
		return startDate;
	}

	public void setStartDate(Date newStartDate){
		startDate = newStartDate;
	}

	public Date getEndDate(){
		return startDate;
	}

	public void setEndDate(Date newEndDate){
		endDate = newEndDate;
	}

	public String getDone(){
		return done;
	}

	public void setDone(String newDone){
		done=newDone;
	}

	public Task copy(){
		Task newTask = new Task(name);
		newTask.setDeadline(deadline);
		newTask.setGroup(group);
		newTask.setPriority(priority);
		newTask.setStartDate(startDate);
		newTask.setEndDate(endDate);
		newTask.setDone(done);

		return newTask;
	}

	//helper function to convert from String in dd/MM hh:mm format to Date
	private static Date stringToDate(String dateString){
        try {
            return standardDateFormat.parse(dateString);
        } catch (Exception e) {
        	return null;
        }
	}

	//the standard date format dd/MM hh:mm is used in storage
	public String toString(){
		JSONObject task = new JSONObject();
		task.put("name",name);
		task.put("deadline",standardDateFormat.format(deadline));
		task.put("startDate",standardDateFormat.format(startDate));
		task.put("endDate",standardDateFormat.format(endDate));
		task.put("priority",priority);
		task.put("group",group);
		task.put("done",done);
		
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
		    
		    task.setDeadline( stringToDate((String)jsonObj.get("deadline")) );
		    task.setStartDate( stringToDate((String)jsonObj.get("startDate")) );
		    task.setEndDate( stringToDate((String)jsonObj.get("endDate")) );
		    
		    task.setGroup((String)jsonObj.get("group"));
		    task.setPriority((String)jsonObj.get("priority"));
		    task.setDone((String)jsonObj.get("done"));

		    return task;

      	}catch(ParseException pe){
         	System.out.println("position: " + pe.getPosition());
         	System.out.println(pe);
         	return null;
      	}
	}
}

