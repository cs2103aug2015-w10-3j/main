//@@author A0110917M
package cs2103;

public class Task {
	private String name = "";
	private String priority = AppConst.TASK_FIELD.MEDIUM;
	private String group = "";
	private String status = AppConst.TASK_FIELD.UNDONE;
	private String deadline = "";
	private String remindTime = "";
	private String startDate = "";
	private String endDate = "";
	private String taskInfo;
	private String period = "00:00 23:59";
	private int repeatedType = 0;
	private int taskId = 0;
	private int parentTaskId = 0;
	
	public Task(String newname) {
		name = newname;
	}

	public String getName() {
		return name;
	}

	public void setName(String newname) {
		name = newname;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String newPriority) {
		priority = newPriority;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String newGroup) {
		group = newGroup;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String newDeadline) {
		deadline = newDeadline;
	}
	
	public String getRemindTime() {
		return remindTime;	
	}	
	
	public void setRemindTime(String newRepeatTime) {
		remindTime = newRepeatTime;
	}
	
	public void setRepeatedType(int repeated) {
		repeatedType = repeated;
	}
	
	public int getRepeatedType() {
		return repeatedType;
	}
	
	public void setPeriod(String newPeriod) {
		period = newPeriod;
	}
	
	public String getPeriod() {
		return period;
	}
	
	public String getDisplay() {
		return name;
		
	}

	public String getStartDate(){
		return startDate;
	}

	public void setStartDate(String newStartDate) {
		startDate = newStartDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String newEndDate) {
		endDate = newEndDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String newStatus) {
		status = newStatus;
	}
	
	public String getTaskInfo() {
		return taskInfo;
	}
	
	public void setTaskInfo(String newTaskInfo) {
		taskInfo = newTaskInfo;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int newTaskId) {
		taskId = newTaskId;
	}
	
	public int getParentTaskId() {
		return parentTaskId;
	}
	
	public void setParentTaskId(int newParentTaskId) {
		parentTaskId = newParentTaskId;
	}

	public Task copy(){
		Task newTask = new Task(name);
		newTask.setDeadline(deadline);
		newTask.setRemindTime(remindTime);
		newTask.setGroup(group);
		newTask.setPriority(priority);
		newTask.setStartDate(startDate);
		newTask.setEndDate(endDate);
		newTask.setStatus(status);
		newTask.setTaskInfo(taskInfo);
		newTask.setRepeatedType(repeatedType);
		newTask.setPeriod(period);
		newTask.setTaskId(taskId);
		newTask.setParentTaskId(parentTaskId);

		return newTask;
	}
}

