import java.io.*;
import java.util.*;


public class Storage {

	private   String fileName = "default.txt";
	private   BufferedReader br = null;
	private   BufferedWriter bw = null;
	
	// Temporary internal arrayList for storage
	private   ArrayList<Task> taskList = new ArrayList<Task>();
	
	public Storage() {
		//Initialise the variables;		
	}

	public   void setFileURL(String fileURL){
		fileName = fileURL + ".txt";
	}

	public	 void rewriteContent(ArrayList<Task> allTasks) {
		taskList = allTasks;
		try {
			writeFileByLine();
		} catch (IOException e) {
			handleException(e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}   					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private   void handleException(IOException e) {
		e.printStackTrace();
		System.exit(0);
	}

	public   ArrayList<Task> readContent() throws IOException {
		try {
			readFileByLine();
        } catch (IOException e) {
        	handleException(e);
        } catch (FileNotFoundException e) {
        	//if its the first time working with the file, it doesnt exist yet
        	//simply return an empty ArrayList
        	return new ArrayList<Task>();
        } finally {
        	closeReader();
        }
		return taskList;
	}
	
	private   void readFileByLine() throws FileNotFoundException,
	IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		ArrayList<Task> updatedTaskList = new ArrayList<Task>();
		String line = null;
		while ((line = br.readLine()) != null) {
			updatedTaskList.add(Task.stringToTask(line));
		}
		taskList = updatedTaskList;
		br.close();
	}
	
	private   void writeFileByLine() throws IOException {
		bw = new BufferedWriter(new FileWriter(fileName));
		for (int i = 0; i < taskList.size(); i++) { 
		  bw.write(taskList.get(i).toString());
		  bw.newLine();
		}
	}

	private   void closeReader() throws IOException {
		if (br != null) {
			br.close();
		}
	}
	
}

