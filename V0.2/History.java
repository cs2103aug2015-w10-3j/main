import java.util.*;

public class History{
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
	public void updateHistory(ArrayList<Task> allTasks){
		if (currentState < mHistory.size()-1){

			while (mHistory.size()>currentState+1)
				mHistory.remove(mHistory.size()-1);
		}
		mHistory.add(new DataState(allTasks));
		currentState++;
	}

	public ArrayList<Task> undo(){
		if (currentState>0) {
			currentState--;
			return mHistory.get(currentState).getAllTasks();
		}

		return null;
	}

	public ArrayList<Task> redo(){
		if (currentState < mHistory.size()-1){
			currentState++;
			return mHistory.get(currentState).getAllTasks();
		}
		return null;
	}

}