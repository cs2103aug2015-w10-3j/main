import java.io.*;
import java.util.*;


public class TaskStorage {

	private String filename;
	
	public TaskStorage() {
		//Initialise the variables;
		
	}

	public void setFileURL(String fileURL){

	}

	public void rewriteContent(ArrayList<String> allTasks){

	}

	public ArrayList<String> readContent(){
		ArrayList<String> fileContent = new ArrayList<String>();
		fileContent.add("task1");
		fileContent.add("task2");
		fileContent.add("task3");
		return fileContent;
	}

}
