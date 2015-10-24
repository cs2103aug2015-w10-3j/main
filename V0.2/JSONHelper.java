import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;

public class JSONHelper {

	
	public JSONHelper() {
	
	}
	
	/**
	 * This function convert a JSON String to a setting object
	 * 
	 * */
	public static Settings stringToSettings(String str){
		JSONParser parser = new JSONParser();
		try{
		    Object obj = parser.parse(str);
		    JSONObject jsonObj = (JSONObject)obj;
		    Settings mSettings = new Settings();

			String fileUrl = (String)jsonObj.get("dataFileUrl");
		    mSettings.setDataFileUrl( (String)jsonObj.get("dataFileUrl") );

		    return mSettings;

      	}catch(ParseException pe){
         	System.out.println("Error in parsing String to Setting; position: " + pe.getPosition());
         	System.out.println(pe);
         	return null;
      	}
	}
	
	
	/**
	 * This function returns the JSON String representation of this Setting object
	 * 
	 * */
	public String convertSettingsToString(Settings mSettings){
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("dataFileUrl",mSettings.getDataFileUrl());
		
		try{
			StringWriter out = new StringWriter();
	      	jsonObj.writeJSONString(out);
	      	return out.toString();
	    }
	    catch (Exception e){
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	
	//the standard date format dd/MM hh:mm is used in storage
	public String convertTaskToString(Task task){
		JSONObject parser = new JSONObject();
		parser.put("name", task.getName());
		parser.put("deadline", task.getDeadline());
		parser.put("startDate", task.getStartDate());
		parser.put("endDate", task.getEndDate());
		parser.put("priority", task.getPriority());
		parser.put("group", task.getGroup());
		parser.put("status", task.getStatus());
		parser.put("taskInfo", task.getTaskInfo());
		
		try{
			StringWriter out = new StringWriter();
	      	parser.writeJSONString(out);
	      
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
