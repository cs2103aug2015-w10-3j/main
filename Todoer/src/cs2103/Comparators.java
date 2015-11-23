//@@author A0093929M
package cs2103;
import java.util.*;
public class Comparators {
	private static DateTimeHelper mDateTimeHelper = new DateTimeHelper();
	/**
	 * These are the comparator classes for comparing Task according to different fields
	 * 
	 * */
	public static class TaskPriorityCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        String p1 = o1.getPriority();
	        String p2 = o2.getPriority();
	        if (p1.equals(AppConst.TASK_FIELD.LOW)) p1 = "n";
	        if (p2.equals(AppConst.TASK_FIELD.LOW)) p2 = "n";
	        return p1.compareTo(p2);
	    }
	}
	public static class TaskDeadlineCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getDeadline().equals("")) {
	        	return 1;
	        }
	        if (o2.getDeadline().equals("")) {
	        	return -1;
	        }
	        int x = mDateTimeHelper.compareStringDates(o1.getDeadline(), o2.getDeadline());
	        if (x != 0) {
	        	return x;
	        }
	        
	        x = 2;
			if (o1.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				x = 3;
			} else if (o1.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				x = 1;
			}
			
			int y = 2;
			if (o2.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				y = 3;
			} else if (o2.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				y = 1;
			}
			
			if (x > y) {
				return -1;
			}
			return 1;
	    }
	}
	
	public static class TaskNotificationCompare implements Comparator<Task> {
		@Override
		public int compare(Task o1, Task o2) {
			String s1 = o1.getDeadline();
			String s2 = o2.getDeadline();
			int x = 2;
			if (o1.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				x = 3;
			} else if (o1.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				x = 1;
			}
			
			int y = 2;
			if (o2.getPriority().equals(AppConst.TASK_FIELD.HIGH)) {
				y = 3;
			} else if (o2.getPriority().equals(AppConst.TASK_FIELD.LOW)) {
				y = 1;
			}
			int xx = mDateTimeHelper.compareStringDates(s2, s1);
			if (xx != 0) {
				return xx;
			}
			if (x > y) {
				return -1;
			}
			return 1;
		}
	}
	
	public static class TaskStartDateCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getStartDate().equals("")) {
	        	return 1;
	        }
	        if (o2.getStartDate().equals("")) {
	        	return -1;
	        }
	        return mDateTimeHelper.compareStringDates(o1.getStartDate(), o2.getStartDate());
	    }
	}
	
	
	public static class TaskEndDateCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	        if (o1.getEndDate().equals("")) {
	        	return 1;
	        }
	        if (o2.getEndDate().equals("")) {
	        	return -1;
	        }
	        return mDateTimeHelper.compareStringDates(o1.getEndDate(), o2.getEndDate());
	    }
	}
	
	
	public static class TaskGroupCompare implements Comparator<Task> {
	    @Override
	    public int compare(Task o1, Task o2) {
	        // write comparison logic here like below , it's just a sample
	       	return o1.getGroup().compareTo(o2.getGroup());
	    }
	}

	
}