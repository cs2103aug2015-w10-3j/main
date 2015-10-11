import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;

public class Task{
	private String name;

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
	
	public String toString(){
		JSONObject task = new JSONObject();
		task.put("name",name);
		
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
		    
		    return new Task((String)jsonObj.get("name"));

      	}catch(ParseException pe){
         	System.out.println("position: " + pe.getPosition());
         	System.out.println(pe);
         	return null;
      	}
	}
}

