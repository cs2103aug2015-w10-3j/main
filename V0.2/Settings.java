import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;

public class Settings{
	
	private static final String DEFAULT_DATA_FILE = "data.text";

	private String dataFileUrl = DEFAULT_DATA_FILE;

	public Settings(){
		
	}

	public String getDataFileUrl(){
		return dataFileUrl;
	}

	public void setDataFileUrl(String newUrl){
		dataFileUrl = newUrl;
	}

	/**
	 * This function returns the JSON String representation of this Setting object
	 * 
	 * */
	public String toString(){
		JSONObject mSettings = new JSONObject();
		mSettings.put("dataFileUrl",dataFileUrl);
		
		try{
			StringWriter out = new StringWriter();
	      	mSettings.writeJSONString(out);
	      	return out.toString();
	    }
	    catch (Exception e){
	    	e.printStackTrace();
	    	return null;
	    }
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

		    mSettings.setDataFileUrl( (String)jsonObj.get("dataFileUrl") );

		    return mSettings;

      	}catch(ParseException pe){
         	System.out.println("Error in parsing String to Setting; position: " + pe.getPosition());
         	System.out.println(pe);
         	return null;
      	}
	}


}