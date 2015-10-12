import java.io.*;
import java.util.*;


public class TaskStorage {

	private static String fileName = "default.txt";
	private static BufferedReader br = null;
	private static BufferedWriter bw = null;
	
	// Temporary internal arrayList for storage
	private static ArrayList<String> taskList = new ArrayList<String>();
	
	public TaskStorage() {
		//Initialise the variables;		
	}

	public static void setFileURL(String fileURL){
		fileName = fileURL + ".txt";
	}

	public static void rewriteContent(ArrayList<String> allTasks) {
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
	
	private static void handleException(IOException e) {
		e.printStackTrace();
		System.exit(0);
	}

	public static ArrayList<String> readContent() throws IOException {
		try {
			readFileByLine();
        } catch (IOException e) {
        	//if its the first time working with the file, it doesnt exist yet
        	//simply return an empty ArrayList
        	if (e instanceof FileNotFoundException) return new ArrayList<String>();
        	//handleException(e);
        } finally {
        	closeReader();
        }
		return taskList;
	}
	
	private static void readFileByLine() throws FileNotFoundException,
	IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		ArrayList<String> updatedTaskList = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			updatedTaskList.add(line);
		}
		taskList = updatedTaskList;
		br.close();
	}
	
	private static void writeFileByLine() throws IOException {
		bw = new BufferedWriter(new FileWriter(fileName));
		for (int i = 0; i < taskList.size(); i++) { 
		  bw.write(taskList.get(i));
		  bw.newLine();
		}
	}

	private static void closeReader() throws IOException {
		if (br != null) {
			br.close();
		}
	}

}

