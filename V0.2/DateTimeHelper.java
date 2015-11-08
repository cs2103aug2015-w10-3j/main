import java.util.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
	

public class DateTimeHelper {
	
	private static final String TODAY = "Today";
	private static final String TOMORROW = "Tomorrow";
	private static final String YESTERDAY = "Yesterday";
	private static final String YTD = "ytd";
	private static final String TMR = "tmr";
	private static final String NOW = "now";
	private static SimpleDateFormat standardTimeFormat = new SimpleDateFormat("dd/MM HH:mm");
	private static SimpleDateFormat displayDateTimeFormat = new SimpleDateFormat("dd MMM HH:mm");
	private static final String startDateTime = "00:00";
	private static final String endDateTime = "23:59";
	private static final String AM = "am";
	private static final String PM = "pm";
	private static final String NEXT = "next";
	private static final String NXT = "nxt";
	private static final String LAST = "last";
	private static final String WEEKS = "weeks";
	private static final String WEEK = "week";
	private static final String LATER = "later";
	private static final String DAY = "day";
	private static final String DAYS = "days";
	private static final String THIS = "this";
	private static final String ZERO = "0";
	private static final String DOUBLE_ZERO = "00";
	private static final String SLASH = "/";
	
	private static int[] daysInMonth = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
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
	
	
	protected boolean isCorrectDate(String dateTime) {
		if (dateTime == null || dateTime.equals("")) {
			return true;
		}
		
		int day = getDayFromStringDate(dateTime);
		int month = getMonthFromStringDate(dateTime);
		if (day <= 0 || month <= 0 || month > 12) {
			return false;
		}
		if (day > daysInMonth[month-1]) {
			return false;
		}
		return true;
	}
	
	// string 'date' should have format dd/MM HH:mm or dd/MM
	// get first 2 characters for date
	protected int getDayFromStringDate(String date) {
		String day = String.valueOf(date.charAt(0)) + String.valueOf(date.charAt(1));
		return Integer.parseInt(day);
	}
	
	// string 'date' should have format dd/MM HH:mm or dd/MM
	// get character index 3 and 4 for month
	protected int getMonthFromStringDate(String date) {
		String month = String.valueOf(date.charAt(3)) + String.valueOf(date.charAt(4));
		return Integer.parseInt(month);
	}
	
	// string 'date' should have format dd/MM HH:mm or dd/MM
	protected int getHourFromStringDate(String date) {
		int position = 0;
		for(int i=date.length()-1; i>=0; i--) {
			if (date.charAt(i) == ':') {
				position = i; break;
			}
		}
		if (position == 0) {
			return 0;
		}
		String hour = String.valueOf(date.charAt(position-2)) + String.valueOf(date.charAt(position-1));
		return Integer.parseInt(hour);
	}
	
	// string 'date' should have format dd/MM HH:mm or dd/MM
	protected int getMinuteFromStringDate(String date) {
		int position = 0;
		for(int i=date.length()-1; i>=0; i--) {
			if (date.charAt(i) == ':') {
				position = i; break;
			}
		}
		if (position == 0) {
			return 0;
		}
		String minute = String.valueOf(date.charAt(position+1)) + String.valueOf(date.charAt(position+2));
		return Integer.parseInt(minute);
	}
	
	protected boolean isToday(String dateTime) {
		int day = getDayFromStringDate(dateTime);
		int month = getMonthFromStringDate(dateTime);
		String date = getCurrentTimeString();
		int currentDay = getDayFromStringDate(date);
		int currentMonth = getMonthFromStringDate(date);
		if (day == currentDay && month == currentMonth) {
			return true;
		}
		return false;
	}
	
	protected boolean isYesterday(String dateTime) {
		int day = getDayFromStringDate(dateTime);
		int month = getMonthFromStringDate(dateTime);
		String date = getCurrentTimeString();
		int yesterdayDay = getDayFromStringDate(getYesterday());
		int yesterdayMonth = getMonthFromStringDate(getYesterday());
		if (day == yesterdayDay && month == yesterdayMonth) {
			return true;
		}
		return false;
	}
	
	protected boolean isTomorrow(String dateTime) {
		int day = getDayFromStringDate(dateTime);
		int month = getMonthFromStringDate(dateTime);
		int tomorrowDay = getDayFromStringDate(getTomorrow());
		int tomorrowMonth = getMonthFromStringDate(getTomorrow());
		if (day == tomorrowDay && month == tomorrowMonth) {
			return true;
		}
		return false;
	}
	
	protected String getYesterday() {
		String dateTime = getCurrentTimeString();
		int day = getDayFromStringDate(dateTime);
		int month = getMonthFromStringDate(dateTime);
		day--;
		if (day == 0 && month != 1) {
			month--;  day = daysInMonth[month-1];		
		}
		if (month > 0 && month < 13 && day > 0 && day <= daysInMonth[month-1]) {
			String dayString = String.valueOf(day);
			String monthString = String.valueOf(month);
			if (dayString.length() < 2) {
				dayString = ZERO + dayString;
			}
			if (monthString.length() < 2) {
				monthString = ZERO + monthString;
			}
			return dayString + SLASH + monthString;
		}
		return "";
	}
	
	protected String getToday() {
		String dateTime = getCurrentTimeString();
		int day = getDayFromStringDate(dateTime);
		String dayString = String.valueOf(day);
		if (dayString.length() < 2) {
			dayString = ZERO + dayString;
		}
		int month = getMonthFromStringDate(dateTime);
		String monthString = String.valueOf(month);
		if (monthString.length() < 2) {
			monthString = ZERO + monthString;
		}
		return dayString + SLASH + monthString;
	}
	
	protected String getTomorrow() {
		String dateTime = getCurrentTimeString();
		int day = getDayFromStringDate(dateTime);
		int month = getMonthFromStringDate(dateTime);
		day++;
		if (day > daysInMonth[month-1]) {
			day = 1; month++;
		}
		if (month > 0 && month < 13 && day > 0 && day <= daysInMonth[month-1]) {
			String dayString = String.valueOf(day);
			String monthString = String.valueOf(month);
			if (dayString.length() < 2) {
				dayString = ZERO + dayString;
			}
			if (monthString.length() < 2) {
				monthString = ZERO + monthString;
			}
			return dayString + SLASH + monthString;
		}
		return "";
	}
	
	
	// saved string date time with format dd/MM HH:mm
	// but display date time with format dd MMM HH:mm
	protected String convertToDisplayFormat(String dateTime) {
		if (dateTime == null || dateTime.equals("")) {
			return dateTime;
		}
		dateTime = dateTime.toLowerCase();
		Date date = convertStringToDate(dateTime);
		if (date == null) {
			return dateTime;
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String time = " " + timeFormat.format(date);
		
		if (isYesterday(dateTime)) {
			return YESTERDAY + time;
		}
		
		if (isToday(dateTime)) {
			return TODAY + time;
		}
		if (isTomorrow(dateTime)) {
			return TOMORROW + time;
		}
		
		return displayDateTimeFormat.format(date);			 
	}
	
	
	protected String convertDateMonthToDisplayFormat(String dateTime) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
		if (dateTime == null || dateTime.equals("")) {
			return dateTime;
		}
		
		dateTime = dateTime.toLowerCase();
		Date date = convertStringToDate(dateTime);
		if (date == null) {
			return dateTime;
		}
		
		if (isYesterday(dateTime)) {
			return YESTERDAY;
		}
		
		if (isToday(dateTime)) {
			return TODAY;
		}
		if (isTomorrow(dateTime)) {
			return TOMORROW;
		}
		
		return dateFormat.format(date);
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


	// to get time, use this function to get minutes
	protected String getMinutesFromString(String time) {
		
		int leng = time.length();
		if (leng<=2) {
			return DOUBLE_ZERO;
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
			minute = ZERO + minute;
		}
		return minute;
	}
	
	
	// to get time, use this function to get hour
	protected String getHoursFromString(String time, int flag) {

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
				time = ZERO + time;
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
			hour = ZERO + hour;
		}
		System.out.println(hour);
		
		return hour;
	}
	
	protected boolean isNumber(String st) {
		for(int i=0; i<st.length(); i++) {
			if (st.charAt(i)<'0' || st.charAt(i)>'9') {
				return false;
			}
		}
		return true;
	}


	// get time from a string of date and time
	// flag = 1: if no time found, default 00:00
	// flag = 2: if no time found, default 23:59
	protected String getTimeFromString(String dateTime, int flag) {
		String time = "";
		if (flag == 1) {
			time = startDateTime;
		} else if (flag == 2) {
			time = endDateTime;
		}
		
		String[] splits = dateTime.split(" ");
		
		String hour = "";
		String minute = "";
		int x = 1;
		
		if (splits.length == 0) {
			return time;
		}
		dateTime = dateTime.toLowerCase();
		splits = dateTime.split(" ");
		
		int count = 0;
		
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
					count++;
					continue;
				} else {
					String st = splits[i].substring(0, splits[i].length()-2);
					hour = getHoursFromString(st, x);
					minute = getMinutesFromString(st);
					count++;
					continue;
				}
			}
			if (splits[i].contains(".") || splits[i].contains(":")) {
				hour = getHoursFromString(splits[i], x);
				minute = getMinutesFromString(splits[i]);
				count++;
				continue;
			}
			if (splits[i].length() > 2 && isNumber(splits[i])) {
				hour = getHoursFromString(splits[i], x);
				minute = getMinutesFromString(splits[i]);
				count++;
				continue;
			}
			
		}
		
		if (count > 1) {
			return null;
		}
		if (count == 0) {
			return time;
		}
		
		if (hour != null && minute != null) {
			if (hour.equals("") || minute.equals("")) {
				return time;
			}
			if (hour.length()<2) {
				hour = ZERO + hour;
			}
			if (minute.length()<2) {
				minute = ZERO + minute;
			}
			return hour + ":" + minute;
		}
		return null;
	}
	
	// return 1->12 for month matched
	// return 0 if no matched found
	protected int getMonthMatched(String st) {
	
		st = st.toLowerCase();
		if (st == null || st.equals("")) {
			return 0;
		}
		for(int i=0; i<months.length; i++) {
			int j = 0, k = 0;
			String st1 = months[i];
			if (st1.charAt(0) != st.charAt(0)) {
				continue;
			}
			while (j<st.length() && k<st1.length()) {
				while (k<st1.length() && st1.charAt(k)!=st.charAt(j)) {
					k++;
				}
				if (k>=st1.length()) {
					break;
				}
				k++;  j++;
			}
			if (j>=st.length()) {
				return i+1;
			}
		}
		return 0;
	}
	
	// This is to get month in characters, like Oct, Sep
	protected String getMonthFromString(String dateTime) {
		
		String[] splits = dateTime.split(" ");
		if (splits.length == 0) {
			return "";
		}
		dateTime = dateTime.toLowerCase();
		splits = dateTime.split(" ");
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
				result = ZERO + result;
			}
			return result;
		}	
		return "";
	}
	
	// get month in a string of date and month,
	// get month for number, for example: 30/12
	protected String getMonth(String st) {
		String month = "";
		if (st.length()<3) {
			return null;
		}
		
		st = st.toLowerCase();
		
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
			month = ZERO + month;
		}
		return month;
	}
	
	// get date in a string date month
	protected String getDate(String st) {
		String date = "";
		for(int i=0; i<st.length(); i++) {
			if (st.charAt(i)<'0'||st.charAt(i)>'9') {
				if (i < st.length()-3) {
					return null;
				}
				break;
			}
			date += st.charAt(i);
		}
		if (date.equals("")) {
			return null;
		}
		int x = Integer.parseInt(date);
		if (x>31 || x <= 0) {
			return null;
		}
		if (date.length() < 2) {
			date = ZERO + date;
		}
		return date;
		
	}
	
	// 0->6: Mon->Sun, -1: not matched
	protected int getMatchedDayInWeek(String day) {
		if (day == null || day.length()<2) {
			return -1;
		}
		day = day.toLowerCase();
		for(int i=0; i<7; i++) {
			if (days[i].startsWith(day)) {
				return i;
			}
		}
		return -1;
	}
	
	
	// Used 1/1/2015 is Thursday to compute day in week of a date
	protected int getDayInWeekForDate(String date) {
		int day = getDayFromStringDate(date);
		int month = getMonthFromStringDate(date);
		int number = 0;
		for(int i=1; i<month; i++) {
			number += daysInMonth[i-1];
		}
		number += day;
		number += 3;
		// 1/1/2015 is Thursday, we go back to Monday is 29/12/2014
		number = number % 7;
		if (number == 0) {
			number = 7;
		}
		return number-1;
		
	}
	
	
	// Return day in week (Mon-Sun) for a string of date time format dd/MM HH:mm
	protected String getStringDayInWeekForDate(String date) {
		if (date == null || date.equals("")) {
			return null;
		}
		int dayInWeek = getDayInWeekForDate(date);
		String result = days[dayInWeek];
		return String.valueOf(result.charAt(0)).toUpperCase() + String.valueOf(result.charAt(1)) + String.valueOf(result.charAt(2));
	}
	
	protected String getDateAfterSomeDaysFromNow(int number) {
		String currentDateTime = getCurrentTimeString();
		int day = getDayFromStringDate(currentDateTime);
		int month = getMonthFromStringDate(currentDateTime);
		day += number;
		while (month < 12 && day > daysInMonth[month-1]) {
			day -= daysInMonth[month-1];
			month++;
		}
		
		if (day > 0 && day < 32 && month > 0 && month < 13) {
		
			String dayString = String.valueOf(day);
			if (dayString.length() < 2) {
				dayString = ZERO + dayString;
			}
			String monthString = String.valueOf(month);
			if (monthString.length() < 2) {
				monthString = ZERO + monthString;
			}
			return dayString + SLASH + monthString;
		}
		return null;
	}
	
	protected String getDateBeforeSomeDaysFromNow(int number) {
		String currentDateTime = getCurrentTimeString();
		int day = getDayFromStringDate(currentDateTime);
		int month = getMonthFromStringDate(currentDateTime);
		day -= number;
		while (month > 1 && day <= 0) {
			month--;
			day += daysInMonth[month-1];
		}
		if (day > 0 && day < 32 && month > 0 && month < 13) {
			String dayString = String.valueOf(day);
			if (dayString.length() < 2) {
				dayString = ZERO + dayString;
			}
			String monthString = String.valueOf(month);
			if (monthString.length() < 2) {
				monthString = ZERO + monthString;
			
			}
			return day + SLASH + month;
		}	
		return null;
	}
	
	// day_ is a day in a week, like Mon, Tues, ..
	// this function is to get date for the day_ in current week
	// for example: today is Fri 30/10, if day_ = Thus -> return 29/10
	
	protected String getDateFromDayInCurrentWeek(String day_) {
		day_ = day_.toLowerCase();
		int dayInWeek = getMatchedDayInWeek(day_);
		if (dayInWeek == -1) {
			return null;
		}
		
		String currentDateTime = getCurrentTimeString();

		int currentDayInWeek = getDayInWeekForDate(currentDateTime);
		int day = getDayFromStringDate(currentDateTime);
		int month = getMonthFromStringDate(currentDateTime);
		
		if (dayInWeek > currentDayInWeek) { 			
			day += dayInWeek - currentDayInWeek;
			if (day > daysInMonth[month-1]) {
				day -= daysInMonth[month-1];
				month++;
			}
		} else {
			day += dayInWeek - currentDayInWeek;
			if (day <= 0 && month > 1) {
				month--;
				day += daysInMonth[month-1];
			}
		}
		
		if (day > 0 && day < 32 && month > 0 && month < 13) {
			String dayString = String.valueOf(day);
			if (dayString.length() < 2) {
				dayString = ZERO + dayString;
			}
			String monthString = String.valueOf(month);
			if (monthString.length() < 2) {
				monthString = ZERO + monthString;
			
			}
			return dayString + SLASH + monthString;
		}
		
		return null;
	}
	
	// This function to get date month from a date time string user typed
	// supported multiple kinds of date time format
	// for example: 30/12, 30 of Dec, this Fri, next Fri, Fri next 2 weeks,
	// next 2 days, next 2 weeks ...
	
	protected String getDateMonthFromString(String dateTime, int flag) {
		
		dateTime = dateTime.toLowerCase();

		if (dateTime.equals(NOW)) {
			String result = getCurrentTimeString();
			return result.substring(0, 5);
		}

		String month = getMonthFromString(dateTime);
		String date = "";
		if (month == null) {
			return null;
		}
		
		String[] splits = dateTime.split(" ");
		
		int count = 0;
		String result = null;
		int numberResult = 0;
		
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
			if (date.length() < 2) {
				date = ZERO + date;
			}
			
			if (month.length() < 2) {
				month = ZERO + month;
			}
			result = date + SLASH + month;
			numberResult = 1;
			
		}
		
		count = 0;
		
		for(int i=0; i<splits.length; i++) {
			String st = splits[i];
			if (st.contains(SLASH) || st.contains("-")) {
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
			if (numberResult > 0) {
				return null;
			}
			numberResult = 1;
			
			if (date.length() < 2) {
				date = ZERO + date;
			}
			
			if (month.length() < 2) {
				month = ZERO + month;
			}
			result = date + SLASH + month;
		}
		
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals(YESTERDAY.toLowerCase()) || splits[i].equals(YTD)) {
				if (numberResult > 0) {
					return null;
				}
				numberResult = 1;
				result = getYesterday();
			}
			
			if (splits[i].equals(TODAY.toLowerCase())) {
				if (numberResult > 0) {
					return null;
				}
				numberResult = 1;
				result = getToday();
			}
			
			if (splits[i].equals(TOMORROW.toLowerCase()) || splits[i].equals(TMR)) {
				if (numberResult > 0) {
					return null;
				}
				numberResult = 1;
				result = getTomorrow();
			}
		}
		
		int dayInWeek = -1;
		count = 0;
		for(int i=0; i<splits.length; i++) {
			int x = getMatchedDayInWeek(splits[i]);
			if (x != -1) {
				count++;  dayInWeek = x;
			}
		}
		if (count > 1) {
			return null;
		}
		
		count = 0;
		
		int daysFromNow = 0;
		int weeksFromNow = 0;	
		
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals(THIS) || splits[i].equals(NEXT) || splits[i].equals(NXT) || splits[i].equals(LAST)) {
				count++;
			}
		}
		if (count > 1) {
			return null;
		}
		
		count = 0;
		
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals(NEXT) || splits[i].equals(NXT)) {
				if (i >= splits.length - 1) {
					break;
				}
				if (isNumber(splits[i+1])) {
					if (i >= splits.length - 2) {
						continue;
					}
					String st = splits[i+2];
					if (st.equals(DAY) || st.equals(DAYS)) {
						daysFromNow = Integer.parseInt(splits[i+1]);
						count++;
						continue;
					}
					if (st.equals(WEEK) || st.equals(WEEKS)) {
						weeksFromNow = Integer.parseInt(splits[i+1]);
						count++;
						continue;
					}
				} else if (splits[i+1].equals(DAY) || splits[i+1].equals(DAYS)) {
					daysFromNow = 1;
					count++;
					continue;
				} else if (splits[i+1].equals(WEEK) || splits[i+1].equals(WEEKS)) {
					weeksFromNow = 1;
					count++;
					continue;
				} else if (getMatchedDayInWeek(splits[i+1])>=0) {
					weeksFromNow = 1;
					continue;
				}
			}
			
			if (splits[i].equals(LAST)) {
				if (i >= splits.length - 1) {
					break;
				}
				if (isNumber(splits[i+1])) {
					if (i >= splits.length - 2) {
						continue;
					}
					String st = splits[i+2];
					if (st.equals(DAY) || st.equals(DAYS)) {
						daysFromNow = -Integer.parseInt(splits[i+1]);
						count++;
						continue;
					}
					if (st.equals(WEEK) || st.equals(WEEKS)) {
						weeksFromNow = -Integer.parseInt(splits[i+1]);
						count++;
						continue;
					}
				} else if (splits[i+1].equals(DAY) || splits[i+1].equals(DAYS)) {
					daysFromNow = -1;
					count++;
					continue;
				} else if (splits[i+1].equals(WEEK) || splits[i+1].equals(WEEKS)) {
					weeksFromNow = -1;
					count++;
					continue;
				} else if (getMatchedDayInWeek(splits[i+1])>=0) {
					weeksFromNow = -1;
					continue;
				}
			}			
		}
		
		if (count > 1) {
			return null;
		}
		if (count == 1 && numberResult > 0) {
			return null;
		}
		
		for(int i=0; i<splits.length; i++) {
			if (splits[i].equals(THIS)) {
				if (i == splits.length - 1) {
					break;
				}
				if (getMatchedDayInWeek(splits[i+1])>=0) {
					count++;
					continue;
				}
				if (splits[i+1].equals(WEEK) || splits[i+1].equals(WEEKS)) {
					count++;
					continue;
				}
			}
		}
		
		if (count > 1) {
			return null;
		}
		
		if (count == 0 && dayInWeek!=-1) {
			count++;
		} else {
			if (dayInWeek == -1 && daysFromNow == 0) {
				if (flag == 1) {
					dayInWeek = 0;
				} else {
					dayInWeek = 6;
				}
			}
		}
		
		if (count == 1 && numberResult > 0) {
			return null;
		}
		
		if (count == 0) {
			return result;
		}
		
		if (daysFromNow != 0) {
			if (daysFromNow > 0) {
				return getDateAfterSomeDaysFromNow(daysFromNow);
			} else {
				return getDateBeforeSomeDaysFromNow(daysFromNow);
			}
		}
		
		// get the date for dayInWeek for this current week
		String dateForDayInWeek = getDateFromDayInCurrentWeek(days[dayInWeek]);
		int day_ = getDayFromStringDate(dateForDayInWeek);
		int month_ = getMonthFromStringDate(dateForDayInWeek);
		int numberOfDays = weeksFromNow * 7;
		if (numberOfDays > 0) {
			day_ += numberOfDays;
			while (month_ <= 12 && day_ > daysInMonth[month_-1]) {
				day_ -= daysInMonth[month_-1];
				month_++;
			}
		} else if (numberOfDays > 0) {
			day_ -= numberOfDays;
			while (day_ <= 0 && month_ > 1) {
				month_--;
				day_ += daysInMonth[month_-1];
			}
		}
		
		if (month_ > 0 && month_ < 13 && day_ > 0 && day_ <= daysInMonth[month_-1]) {
			
			String dayString = String.valueOf(day_);
			if (dayString.length() < 2) {
				dayString = ZERO + dayString;
			}
			String monthString = String.valueOf(month_);
			if (monthString.length() < 2) {
				monthString = ZERO + monthString;
			}
			
			return dayString + SLASH + monthString;
		}
		
		return null;
	}


	// This main function to get date and time from user command
	// flag = 1 means this is the 'from' date time,
	// we use 00:00 for time if there is no time
	// we use Mon for date if only have week and no specific date
	// flag = 2 means this is the 'to' or 'deadline' date time,
	// we use 23:59 for time, Sun for date
	protected String getStringDateFromString(String dateTime, int flag) {
		
		dateTime = dateTime.toLowerCase();
		
		if (dateTime.equals(NOW)) {
			return getCurrentTimeString();
		}
		
		String time = getTimeFromString(dateTime, flag);
		String date = getDateMonthFromString(dateTime, flag);
		if (time == null || date == null) {
			return null;
		}
		
		System.out.println(date + " " + time);
		
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
	
	
	// To get how many days from 1/1/2015 to day/month/2015
	protected int getNumberOfDayFromThisYearForDate(int day, int month) {
		int result = 0;
		for(int i=1; i<month; i++) {
			result += daysInMonth[i-1];
		}
		result += day;
		return result;
	}
	
	// To get date for numberOfDays from 1/1/2015
	protected String getDateForNumberOfDays(int numberOfDays) {
		int month = 1, day = numberOfDays;
		while (month<=12) {
			if (day<=daysInMonth[month-1]) {
				break;
			}
			day -= daysInMonth[month-1];
			month++;
		}
		if (day == 0) {
			return "01/01";
		}
		if (month > 12) {
			return "31/12";
		}
		String dayString = String.valueOf(day);
		if (dayString.length() < 2) {
			dayString = ZERO + dayString;
		}
		String monthString = String.valueOf(month);
		if (monthString.length() < 2) {
			monthString = ZERO + monthString;
		}
		return dayString + SLASH + monthString;
	}
	
	// period has format: HH:mm HH:mm
	protected int getStartTimeFromStringPeriod(String period) {
		String hour = String.valueOf(period.charAt(0)) + String.valueOf(period.charAt(1));
		String minute = String.valueOf(period.charAt(3)) + String.valueOf(period.charAt(4));
		return Integer.parseInt(hour)*60 + Integer.parseInt(minute);
	}
	
	protected String getStringStartTimeForStringPeriod(String period) {
		String hour = String.valueOf(period.charAt(0)) + String.valueOf(period.charAt(1));
		String minute = String.valueOf(period.charAt(3)) + String.valueOf(period.charAt(4));
		return hour + ":" + minute;
	}
	
	// period has format: HH:mm HH:mm
	protected int getEndTimeFromStringPeriod(String period) {
		int length = period.length();
		String hour = String.valueOf(period.charAt(length-5)) + String.valueOf(period.charAt(length-4));
		String minute = String.valueOf(period.charAt(length-2)) + String.valueOf(period.charAt(length-1));
		return Integer.parseInt(hour)*60 + Integer.parseInt(minute);
	}
	
	protected String getStringEndTimeForStringPeriod(String period) {
		int length = period.length();
		String hour = String.valueOf(period.charAt(length-5)) + String.valueOf(period.charAt(length-4));
		String minute = String.valueOf(period.charAt(length-2)) + String.valueOf(period.charAt(length-1));
		return hour + ":" + minute;
	}
	
	
	protected String getDateTimeAfterFewMinuteFromNow(int minute) {
		
		String currentTime = getCurrentTimeString();
		int day = getDayFromStringDate(currentTime);
		int month = getMonthFromStringDate(currentTime);
		int time = getEndTimeFromStringPeriod(currentTime);
		time += minute;
		while (time >= 24*60) {
			time -= 24*60;
			day++;
			if (day > daysInMonth[month-1]) {
				day -= daysInMonth[month-1];
				month++;
			}
		}
		String dayString = String.valueOf(day);
		if (dayString.length()<2) {
			dayString = ZERO + dayString;
		}
		
		String monthString = String.valueOf(month);
		if (monthString.length()<2) {
			monthString = ZERO + monthString;
		}
		
		String hourString = String.valueOf(time/60);
		if (hourString.length()<2) {
			hourString = ZERO + hourString;
		}
		
		String minuteString = String.valueOf(time % 60);
		if (minuteString.length()<2) {
			minuteString = ZERO + minuteString;
		}
		
		return dayString + SLASH + monthString + " " + hourString + ":" + minuteString;
	}
	
	
	
	protected boolean isTwoEventOverlap(String fromDateTime1, 
										String toDateTime1, 
										boolean isHasPeriod1, 
										String period1, 
										String fromDateTime2, 
										String toDateTime2, 
										boolean isHasPeriod2, 
										String period2) {
		int fromDay1 = getNumberOfDayFromThisYearForDate(getDayFromStringDate(fromDateTime1), getMonthFromStringDate(fromDateTime1));
		int fromTime1 = getHourFromStringDate(fromDateTime1) * 60 + getMinuteFromStringDate(fromDateTime1);
		int toDay1 = getNumberOfDayFromThisYearForDate(getDayFromStringDate(toDateTime1), getMonthFromStringDate(toDateTime1));
		int toTime1 = getHourFromStringDate(toDateTime1) * 60 + getMinuteFromStringDate(toDateTime1);
		int fromDay2 = getNumberOfDayFromThisYearForDate(getDayFromStringDate(fromDateTime2), getMonthFromStringDate(fromDateTime2));
		int fromTime2 = getHourFromStringDate(fromDateTime2) * 60 + getMinuteFromStringDate(fromDateTime2);
		int toDay2 = getNumberOfDayFromThisYearForDate(getDayFromStringDate(toDateTime2), getMonthFromStringDate(toDateTime2));
		int toTime2 = getHourFromStringDate(toDateTime2) * 60 + getMinuteFromStringDate(toDateTime2);
		for(int i=fromDay1; i<=toDay1; i++) {
			if (i>=fromDay2 && i<=toDay2) {
				int startTime1 = 0, endTime1 = 23*60+59;
				if (i==fromDay1) {
					startTime1 = fromTime1;
				}				
				if (i==toDay1) {
					endTime1 = toTime1;
				}
				
				if (isHasPeriod1) {
					startTime1 = getStartTimeFromStringPeriod(period1);
					endTime1 = getEndTimeFromStringPeriod(period1);
				}
				
				int startTime2 = 0, endTime2 = 23*60+59;
				if (i==fromDay2) {
					startTime2 = fromTime2;
				}				
				if (i==toDay2) {
					endTime2 = toTime2;
				}
				
				if (isHasPeriod2) {
					startTime2 = getStartTimeFromStringPeriod(period2);
					endTime2 = getEndTimeFromStringPeriod(period2);
				}
				
				System.out.println("Get period: " + period1 + " " + period2);
				
				if (startTime1 <= startTime2 && startTime2 < endTime1) {
					return true;
				}
				if (startTime1 < endTime2 && endTime2 <= endTime1) {
					return true;
				}
				if (startTime2 <= startTime1 && startTime1 < endTime2) {
					return true;
				}
				if (startTime2 < endTime1 && endTime1 <= endTime2) {
					return true;
				}
				
			}
		}
		return false;
	}
	
	protected boolean isEventOverlapWithRepeating(	String startDateTime, 
													String endDateTime, 
													boolean isHasPeriod, 
													String period,
													String repeatedDay, 
													String periodRepeated ) {
		int fromDay = getNumberOfDayFromThisYearForDate(getDayFromStringDate(startDateTime), getMonthFromStringDate(startDateTime));
		int fromTime = getHourFromStringDate(startDateTime) * 60 + getMinuteFromStringDate(startDateTime);
		int toDay = getNumberOfDayFromThisYearForDate(getDayFromStringDate(endDateTime), getMonthFromStringDate(endDateTime));
		int toTime = getHourFromStringDate(endDateTime) * 60 + getMinuteFromStringDate(endDateTime);	
		int dayInWeekRepeated = getDayInWeekForDate(repeatedDay);								
		for(int i=fromDay; i<=toDay; i++) {
			
			int startTime = 0, endTime = 23*60+59;
			if (i==fromDay) {
				startTime = fromTime;
			}				
			if (i==toDay) {
				endTime = toTime;
			}
				
			if (isHasPeriod) {
				startTime = getStartTimeFromStringPeriod(period);
				endTime = getEndTimeFromStringPeriod(period);
			}
		
			String date = getDateForNumberOfDays(i);
			int dayInWeek = getDayInWeekForDate(date);
			
			if (dayInWeek == dayInWeekRepeated) {
				int startRepeatedTime = getStartTimeFromStringPeriod(periodRepeated);
				int endRepeatedTime = getEndTimeFromStringPeriod(periodRepeated);
				if (startTime <= startRepeatedTime && startRepeatedTime < endTime) {
					return true;
				}
				if (startTime < endRepeatedTime && endRepeatedTime <= endTime) {
					return true;
				}
				
				if (startRepeatedTime <= startTime && startTime < endRepeatedTime) {
					return true;
				}
				if (startRepeatedTime < endTime && endTime <= endRepeatedTime) {
					return true;
				}
			}
		}	
		return false;									
	}
	
	
	protected int[] getTimetableForDate(String date, ArrayList<Task> allTasks) {
		
		int[] result = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		int dayInWeek = getDayInWeekForDate(date);
		int day = getNumberOfDayFromThisYearForDate(getDayFromStringDate(date), getMonthFromStringDate(date));
		for(int i=0; i<allTasks.size(); i++) {
			Task task = allTasks.get(i);
			String startDate = task.getStartDate();
			String endDate = task.getEndDate();
			if (startDate.equals("") || endDate.equals("")) {
				continue;
			}
			
			int fromDay = getNumberOfDayFromThisYearForDate(getDayFromStringDate(startDate), getMonthFromStringDate(startDate));
			int fromTime = getHourFromStringDate(startDate) * 60 + getMinuteFromStringDate(startDate);
			int toDay = getNumberOfDayFromThisYearForDate(getDayFromStringDate(endDate), getMonthFromStringDate(endDate));
			int toTime = getHourFromStringDate(endDate) * 60 + getMinuteFromStringDate(endDate);
			
			if (task.getRepeatedType() != AppConst.REPEATED_TYPE.EVERY_WEEK) {
				if (day>=fromDay && day<=toDay) {
					int startTime = 0, endTime = 24;
					if (day == fromDay) {
						startTime = fromTime / 60;
					}
					if (day == toDay) {
						endTime = (toTime + 59) / 60;
					}
					if (task.getRepeatedType()>0) {
						startTime = getStartTimeFromStringPeriod(task.getPeriod()) / 60;
						endTime = (getEndTimeFromStringPeriod(task.getPeriod()) + 59) / 60;
					}
					System.out.println(String.valueOf(startTime) + " " + String.valueOf(endTime));
					for(int j=startTime; j<endTime; j++) {
						if (j>=8 && j<=19) {
							result[j-8] = i;
						}
					}
				}
			} else {
				int fromDayInWeek = getDayInWeekForDate(startDate);
				if (day>=fromDay && fromDayInWeek==dayInWeek) {
					int startTime = getStartTimeFromStringPeriod(task.getPeriod()) / 60;
					int endTime = (getEndTimeFromStringPeriod(task.getPeriod()) + 59) / 60;
					System.out.println(String.valueOf(startTime) + " " + String.valueOf(endTime));
					for(int j=startTime; j<endTime; j++) {
						if (j>=8 && j<=19) {
							result[j-8] = i;
						}
					}
				}
			}
		}
		
		return result;
		
	}
	
	

}
