import java.util.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DateTimeHelper {
	
	private static SimpleDateFormat standardTimeFormat = new SimpleDateFormat("dd/MM HH:mm");
	private static final String startDateTime = " 00:00";
	private static final String endDateTime = " 23:59";
	private static final String AM = "am";
	private static final String PM = "pm";
	private static final String NEXT = "next";
	private static final String WEEKS = "weeks";
	private static final String[] months = new String[] {"january", 
														 "february", 
														 "march", 
														 "april", 
														 "may", 
														 "june", 
														 "july", 
														 "august", 
														 "september", 
														 "october", 
														 "november", 
														 "december"};
	private static final String[] days = new String[] {"monday",
													   "tuesday",
													   "wednesday",
													   "thursday",
													   "friday",
													   "saturday",
													   "sunday"};
										
	
	public DateTimeHelper() {
	
	}
	
	protected String getCurrentTimeString() {
		return standardTimeFormat.format(new Date());
	}
	
	protected Date getCurrentDate() {
		return new Date();
	}
	
	protected Date convertStringToDate(String dateTime) {
		try {
			return standardTimeFormat.parse(dateTime);
		} catch (ParseException pe) {
			return null;	
		}
	}
	
	protected String convertDateToString(Date dateTime) {
		return standardTimeFormat.format(dateTime);
	}
	
	protected int compareDates(Date date1, Date date2) {
		if (date1 == null) {
			return -1;
		}
		if (date2 == null) {
			return 1;
		}
		return date1.compareTo(date2);
	}
	
	protected int compareStringDates(String stringDate1, String stringDate2) {
	
		Date date1 = convertStringToDate(stringDate1);
		Date date2 = convertStringToDate(stringDate2);
		return compareDates(date1, date2);	
	}

	private String getMinutesFromString(String time) {
		
		int leng = time.length();
		if (leng<=2) {
			return "00";
		}
		String minute = String.valueOf(time.charAt(leng-2)) + String.valueOf(time.charAt(leng-1));
		if (!isNumber(minute)) {
			return null;
		}
		int x = Integer.parseInt(minute);
		if (x >= 60) {
			return null;
		}
		minute = String.valueOf(x);
		if (minute.length() < 2) {
			minute = "0" + minute;
		}
		return minute;
	}
	
	private String getHoursFromString(String time, int flag) {

		if (time.length() <= 2) {
			if (!isNumber(time)) {
				return null;
			}
			if (flag == 2) {
				int x = Integer.parseInt(time);
				if (x < 12) {
					x += 12;
				}
				time = String.valueOf(x);
			}
			if (time.length() < 2) {
				time = "0" + time;
			}
			System.out.println(time);
			return time;
		}
		String hour = "";
		for(int i=0; i<time.length()-2; i++) {
			if (time.charAt(i)<'0' || time.charAt(i)>'9') {
				break;
			}
			hour += String.valueOf(time.charAt(i));
		}
		if (hour.equals("")) {
			return "";
		}
		int x = Integer.parseInt(hour);
		if (flag == 2) {
			if (x < 12) {
				x += 12;
			}
		}
		if (x >= 24) {
			return null;
		}
		
		hour = String.valueOf(x);
		if (hour.length() < 2) {
			hour = "0" + hour;
		}
		System.out.println(hour);
		
		return hour;
	}
	
	private boolean isNumber(String st) {
		for(int i=0; i<st.length(); i++) {
			if (st.charAt(i)<'0' || st.charAt(i)>'9') {
				return false;
			}
		}
		return true;
	}

	private String getTimeFromString(String dateTime, int flag) {
		String time = "";
		if (flag == 1) {
			time = startDateTime;
		} else {
			time = endDateTime;
		}
		
		String[] splits = dateTime.split(" ");
		
		String hour = "";
		String minute = "";
		int x = 1;
		
		if (splits.length == 0) {
			return "";
		}
		
		for(int i=splits.length-1; i>=0; i--) {
		
			if (splits[i].endsWith(AM) || splits[i].endsWith(PM)) {
				if (splits[i].endsWith(PM)) {
					x = 2;
				}
				if (splits[i].equals(AM) || splits[i].equals(PM)) {
					if (i > 0) {
						hour = getHoursFromString(splits[i-1], x);
						minute = getMinutesFromString(splits[i-1]);
					}
					break;
				} else {
					String st = splits[i].substring(0, splits[i].length()-2);
					hour = getHoursFromString(st, x);
					minute = getMinutesFromString(st);
					break;
				}
			}
			if (splits[i].contains(".") || splits[i].contains(":")) {
				hour = getHoursFromString(splits[i], x);
				minute = getMinutesFromString(splits[i]);
				break;
			}
			if (splits[i].length() > 2 && isNumber(splits[i])) {
				hour = getHoursFromString(splits[i], x);
				minute = getMinutesFromString(splits[i]);
				break;
			}
			
		}
		if (hour != null && minute != null) {
			if (hour.equals("") || minute.equals("")) {
				return time;
			}
			return hour + ":" + minute;
		}
		return null;
	}
	
	private int getMonthMatched(String st) {
		for(int i=0; i<months.length; i++) {
			int j = 0, k = 0;
			String st1 = months[i];
			while (j<st.length() && k<st1.length()) {
				while (k<st1.length() && st1.charAt(k)!=st.charAt(j)) {
					k++;
				}
				if (k==st1.length()) {
					break;
				}
				k++;  j++;
			}
			if (j==st.length()) {
				return i+1;
			}
		}
		return 0;
	}
	
	private String getMonthFromString(String dateTime) {
		
		String[] splits = dateTime.split(" ");
		if (splits.length == 0) {
			return "";
		}
		int count = 0, res = 0;
		for(int i=0; i<splits.length; i++) {
			int x = getMonthMatched(splits[i]);
			if (x>0) {
				count++;
				res = x;
			}
		}
		if (count>1) {
			// More than 1 matched Month format
			return null;
		} else if (count == 1) {
			String result = String.valueOf(res);
			if (result.length()<2) {
				result = "0" + result;
			}
			return result;
		}	
		return "";
	}
	
	private String getMonth(String st) {
		String month = "";
		if (st.length()<3) {
			return null;
		}
		
		for(int i=st.length()-1; i>=0; i--) {
			if (st.charAt(i)<'0'||st.charAt(i)>'9') {
				break;
			}
			month = String.valueOf(st.charAt(i)) + month;
		}
		if (month.equals("")) {
			return null;
		}
		int x = Integer.parseInt(month);
		if (x>12) {
			return null;
		}
		if (month.length() < 2) {
			month = "0" + month;
		}
		return month;
	}
	
	private String getDate(String st) {
		String date = "";
		for(int i=0; i<st.length(); i++) {
			if (st.charAt(i)<'0'||st.charAt(i)>'9') {
				break;
			}
			date += st.charAt(i);
		}
		if (date.equals("")) {
			return null;
		}
		int x = Integer.parseInt(date);
		if (x>31) {
			return null;
		}
		if (date.length() < 2) {
			date = "0" + date;
		}
		return date;
		
	}
	
	protected String getDateMonthFromString(String dateTime) {
	
		dateTime = dateTime.toLowerCase();

		String month = getMonthFromString(dateTime);
		String date = "";
		if (month == null) {
			return null;
		}
		
		String[] splits = dateTime.split(" ");
		
		int count = 0;
		
		if (!month.equals("")) {
			for(int i=0; i<splits.length; i++) {
				String st = splits[i];
				System.out.println(st);
				if (st.endsWith("th") || st.endsWith("st") || st.endsWith("rd") || st.endsWith("nd") || (st.length()<3 && isNumber(st))) {
					date = getDate(st);
					if (date != null && !date.equals("")) {
						count++;
					}
				} 
			}
			if (count>1 || count==0) {
				return null;
			}
			return date + "/" + month;
		}
		
		for(int i=0; i<splits.length; i++) {
			String st = splits[i];
			if (st.contains("/")) {
				month = getMonth(st);
				date = getDate(st);
				if (month != null && date != null && !month.equals("") && !date.equals("")) {
					count++;
				}
			}	
		}
		if (count > 1) {
			return null;
		} else if (count == 1){
			return date + "/" + month;
		}
		
		return "";
	}

	protected String getStringDateFromString(String dateTime, int flag) {
		
		dateTime = dateTime.toLowerCase();
		
		String time = getTimeFromString(dateTime, flag);
		String date = getDateMonthFromString(dateTime);
		System.out.println(time);
		System.out.println(date);
		if (time == null || date == null) {
			return null;
		}
		if (date.equals("")) {
			return null;
		}
		
		return date + " " + time;
	}

	// have a date time with format: dd/MM HH:mm
	// get MM from this string
	protected String getMonthStringForDateTime(String dateTime) {
		String month = String.valueOf(dateTime.charAt(3)) + String.valueOf(dateTime.charAt(4));
		int x = Integer.parseInt(month);
		return months[x-1];
	}

}
