package cs2103;
import java.util.*;
public class ArrayListPointer {
	
	private ArrayList<Task> pointer;
	public ArrayListPointer() {
		pointer = null;
	}
	public ArrayList<Task> getPointer() {
		return pointer;
	}
	public void setPointer(ArrayList<Task> newPointer) {
		pointer = newPointer;	
	}
}