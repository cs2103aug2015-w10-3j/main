//@@author A0110917M
package cs2103;
import org.json.simple.JSONObject;
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
	public static Settings stringToSettings(String str) {
		JSONParser parser = new JSONParser();
		try {
		    Object obj = parser.parse(str);
		    JSONObject jsonObj = (JSONObject)obj;
		    Settings mSettings = new Settings();

		    mSettings.setDataFileUrl((String)jsonObj.get(AppConst.TASK_JSON_FIELD.DATA_FILE_URL));

		    return mSettings;

      	} catch (ParseException pe) {
         	return null;
      	}
	}
	
	
	/**
	 * This function returns the JSON String representation of this Setting object
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public String convertSettingsToString(Settings mSettings) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(AppConst.TASK_JSON_FIELD.DATA_FILE_URL, mSettings.getDataFileUrl());
		
		try {
			StringWriter out = new StringWriter();
	      	jsonObj.writeJSONString(out);
	      	return out.toString();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	
	//the standard date format dd/MM hh:mm is used in storage
	@SuppressWarnings("unchecked")
	public String convertTaskToString(Task task){
		JSONObject parser = new JSONObject();
		parser.put(AppConst.TASK_JSON_FIELD.NAME, task.getName());
		parser.put(AppConst.TASK_JSON_FIELD.DEADLINE, task.getDeadline());
		parser.put(AppConst.TASK_JSON_FIELD.REMIND_TIME, task.getRemindTime());
		parser.put(AppConst.TASK_JSON_FIELD.START_DATE, task.getStartDate());
		parser.put(AppConst.TASK_JSON_FIELD.END_DATE, task.getEndDate());
		parser.put(AppConst.TASK_JSON_FIELD.PRIORITY, task.getPriority());
		parser.put(AppConst.TASK_JSON_FIELD.GROUP, task.getGroup());
		parser.put(AppConst.TASK_JSON_FIELD.STATUS, task.getStatus());
		parser.put(AppConst.TASK_JSON_FIELD.TASK_INFO, task.getTaskInfo());
		parser.put(AppConst.TASK_JSON_FIELD.REPEATED_TYPE, String.valueOf(task.getRepeatedType()));
		parser.put(AppConst.TASK_JSON_FIELD.PERIOD, task.getPeriod());
		parser.put(AppConst.TASK_JSON_FIELD.TASK_ID, String.valueOf(task.getTaskId()));
		parser.put(AppConst.TASK_JSON_FIELD.PARENT_TASK_ID, String.valueOf(task.getParentTaskId()));
		
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

	public static Task stringToTask(String str) {
		JSONParser parser = new JSONParser();
		try{
		    Object obj = parser.parse(str);
		    JSONObject jsonObj = (JSONObject)obj;
		    Task task = new Task((String)jsonObj.get(AppConst.TASK_JSON_FIELD.NAME));
		    
		    task.setDeadline((String)jsonObj.get(AppConst.TASK_JSON_FIELD.DEADLINE));
		    task.setRemindTime((String)jsonObj.get(AppConst.TASK_JSON_FIELD.REMIND_TIME));
		    task.setStartDate((String)jsonObj.get(AppConst.TASK_JSON_FIELD.START_DATE));
		    task.setEndDate((String)jsonObj.get(AppConst.TASK_JSON_FIELD.END_DATE));
		    
		    task.setPriority((String)jsonObj.get(AppConst.TASK_JSON_FIELD.PRIORITY));
		    task.setGroup((String)jsonObj.get(AppConst.TASK_JSON_FIELD.GROUP));
		    task.setStatus((String)jsonObj.get(AppConst.TASK_JSON_FIELD.STATUS));
		    
		    task.setTaskInfo((String)jsonObj.get(AppConst.TASK_JSON_FIELD.TASK_INFO));
		    
		    task.setRepeatedType(Integer.parseInt((String)jsonObj.get(AppConst.TASK_JSON_FIELD.REPEATED_TYPE)));
		    
		    task.setPeriod((String)jsonObj.get(AppConst.TASK_JSON_FIELD.PERIOD));
			task.setTaskId(Integer.parseInt((String)jsonObj.get(AppConst.TASK_JSON_FIELD.TASK_ID)));
			task.setParentTaskId(Integer.parseInt((String)jsonObj.get(AppConst.TASK_JSON_FIELD.PARENT_TASK_ID)));
	
		    return task;

      	}catch(ParseException pe) {
         	return null;
      	}
	}
	
}
