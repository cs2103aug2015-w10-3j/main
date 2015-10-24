import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.io.*;

public class Settings{
	
	private static final String DEFAULT_DATA_FILE = "data.txt";

	private String dataFileUrl = DEFAULT_DATA_FILE;

	public Settings(){
		
	}

	public String getDataFileUrl(){
		return dataFileUrl;
	}

	public void setDataFileUrl(String newUrl){
		dataFileUrl = newUrl;
	}

}
