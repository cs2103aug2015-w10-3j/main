package MainController;

public class TaskInfo {
	private String taskName;
	private String taskDescription;
	private String taskGroup;
	private int taskDeadline;
	private int taskPriority;
	private int taskId;

	public ToDoInfo(String name, String description, String group, int deadline, int priority) {
		taskName = name;
		taskDescription = description;
		taskGroup = group;
		taskDeadline = deadline;
		taskPriority = priority;
	}

	public void setTaskName(String name) {
		taskName = name;
	}
	
	public void setTaskDescription(String description) {
		taskDescription = description;
	}
	
	public void setTaskGroup(String group) {
		taskGroup = group;
	}

	public void setTaskDeadline(int deadline) {
		taskDeadline = deadline;
	} 

	public void setTaskPriority(int priority) {
		taskPriority = priority;
	}

	public void setTaskId(int id) {
		taskId = id;
	}

	protected String getTaskName() {
		return taskName;
	}

	protected String getTaskDescription() {
		return taskDescription;
	}

	protected String getTaskGroup() {
		return taskGroup;
	}

	protected int getTaskDeadline() {
		return taskDeadline;
	}

	protected int getTaskPriority() {
		return taskPriority;
	}
	
	protected int getTaskId() {
		return taskId;
	}

}
