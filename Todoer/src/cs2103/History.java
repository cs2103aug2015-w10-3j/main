package cs2103;
import java.util.*;

public class History {
	private ArrayList<DataState> mHistory;
	private int currentState;

	public History() {
		mHistory = new ArrayList<DataState>();
		currentState = -1 ;
	}

	/**
	 * This function update the mHistory after each change in mAllTasks.
	 * So that we could redo/undo changes in mAllTasks
	 * 
	 * */
	public void updateHistory(ArrayList<Task> allTasks) {

		ArrayList<DataState> mH = new ArrayList<DataState>();
		for (int i = 0; i <= currentState; i++) {
			mH.add(mHistory.get(i));
		}

		mH.add(new DataState(allTasks));
		mHistory = mH;
		currentState++;

	}

	public ArrayList<Task> undo() {
		
		if (currentState>0) {
			currentState--;
			DataState duplicate = new DataState(mHistory.get(currentState).getAllTasks());
			return duplicate.getAllTasks();
		}
		return null;
	}

	public ArrayList<Task> redo() {

		if (currentState < mHistory.size()-1){
			currentState++;
			return mHistory.get(currentState).getAllTasks();
		}
		return null;
	}

}