import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.text.*;

public class Task{
	private String name;
	private Date deadline=null;
	private String deadlineString="";

	public Task(String newname) {
		//Initialise the variables;		
		name = newname;
	}

	public String getName(){
		return name;
	}

	public void setName(String newname){
		name = newname;
	}

	public Date getDeadline(){
		return deadline;
	}

	public String getDeadlineString(){
		return deadlineString;
	}

	public String getDisplay(){
		return name + "\n";

		//return deadlineString.equals("") ? name : name + " by " + deadlineString;
	}
	public Task copy(){
		Task newTask = new Task(name);
		newTask.setDeadline(deadlineString);
		return newTask;
	}

	public void setDeadline(String dline){
		deadlineString = dline;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        try {
            deadline = formatter.parse(dline);
        }
        catch (Exception e) {
        	deadline = null;
        }
	}

	
	public String toString(){
		JSONObject task = new JSONObject();
		task.put("name",name);
		task.put("deadline",deadlineString);
		
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
		    return task;

      	}catch(ParseException pe){
         	System.out.println("position: " + pe.getPosition());
         	System.out.println(pe);
         	return null;
      	}
	}
}

