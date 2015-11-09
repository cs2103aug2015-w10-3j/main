package cs2103;
import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.lang.*;
import java.lang.Throwable;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.*;
import java.net.URL;
import javax.swing.*;
import  sun.audio.*; 

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Container;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JOptionPane;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class TaskUIManager {

	private static String ENTER = "Enter";
    private static String APP_NAME = "Todoer";
    private static String COMMAND_MESSAGE = "$" + APP_NAME + ": ";
    private static String NEW_LINE = "\n";
    private static String SLASH = "\\";
    private static int MAX_NUMBER_ROWS = 16;
    private static String EVERYDAY = "Everyday";
    private static String EVERY = "Every ";
    
    private static String NOTIFICATION = "Notification!";
    private static String OK_BUTTON = "OK";
    
    // String to display for priority
    private static final String HIGH = "High";
    private static final String MEDIUM = "Medium";
    private static final String LOW = "Low";
    
    private static final String TIME_ZERO = " 00:00";
    
    // column to display in table
    static String[] columnNames = new String[] {"#", "Task Name",
                        "Deadline",
                        "Start Date/Time",
                        "End Date/Time",
                        "Period",
                        "Prioriry",
                        "Group",
                        "Status"
                        };
                        
    static String[] timeTableColumnNames = new String[] { 	"#",
    														"8-9", 
    														"9-10", 
    														"10-11", 
    														"11-12", 
    														"12-13", 
    														"13-14", 
    														"14-15",
    														"15-16", 
    														"16-17", 
    														"17-18", 
    														"18-19", 
    														"19-20"
    														};
    static int[] columnWidth = new int[] {	40,
    										260,
    										180,
    										180,
    										180,
    										100,
    										80,
    										130,
    										80
    										};
    
	private static DateTimeHelper mDateTimeHelper = new DateTimeHelper();
	private static CommandParser mCommandParser = new CommandParser();
	
    static JButton enterButton;
    public static JTextArea output;
    public static JTextField input;
    static JFrame frame;
    static JPanel panel;
    static JTable table;
    static Toolkit toolkit;
    static Timer timer;								
    										
    static ArrayList<Task> dataTaskList = new ArrayList<Task>();
	static int windowHeight = 5;
	static int windowWidth = 120;
	static int rowHeightDefault = 25;
    static int userCommandCount = 0;
    static int userScrollCount = 0;
    static int mTableRowCount = 0;
    
    // check deadline task for every 1 second
    static int timeRemind = 1 * 1000;
    static int timeDismiss = 30 * 1000;
	static int timePlaySound = 4 * 1000;

	public TaskUIManager() {

	}

	static MainLogic mMainLogic = new MainLogic();
	public static void main(String[] argv) {

		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
		toolkit = Toolkit.getDefaultToolkit();
		Dimension d = toolkit.getScreenSize();
		int dummy = (int)(d.width * 0.08);
		
		System.out.println(dummy);
		windowWidth = dummy;
		
        ArrayListPointer dataTaskListPointer = new ArrayListPointer();
        // Input a showall command upon launching Todoer
        String message = mMainLogic.process(AppConst.COMMAND_TYPE.SHOW_ALL, dataTaskListPointer);
        dataTaskList = dataTaskListPointer.getPointer();
        mTableRowCount = dataTaskList.size();
                
        openToDoListWindow();

        displayMessage(AppConst.MESSAGE.WELCOME);
        assert message != null;
        displayMessage(message);
        
       
        
	    timer = new Timer();
		timer.scheduleAtFixedRate(new RemindTask(), 0, timeRemind);
        
	}
	
	public static class RemindTask extends TimerTask {
	
		public void run() {
			
			/*
			* 
			*
			*/
			ArrayListPointer dataTaskListPointer = new ArrayListPointer();   
            dataTaskListPointer.setPointer(dataTaskList);
			String message = mMainLogic.process(AppConst.COMMAND_TYPE.REMIND, dataTaskListPointer);
			if (message != null) {				
				dataTaskList = dataTaskListPointer.getPointer();
				mTableRowCount = dataTaskList.size();
				userScrollCount = 0;
				updateTable();
				output.setText("");
				displayMessage(message);
				
				// show notification to users
				// option pane with no buttons.
				JOptionPane opt = new JOptionPane(	AppConst.MESSAGE.NOTIFICATIONS, 
													JOptionPane.INFORMATION_MESSAGE, 
													JOptionPane.DEFAULT_OPTION, 
													null, 
													new Object[]{OK_BUTTON}); 
													
		  		final JDialog dlg = opt.createDialog(NOTIFICATION);
		  		new Thread(new Runnable() {
		  			public void run() {		
		  				try {
		  					InputStream in = new FileInputStream("sound1.wav");
							AudioStream as = new AudioStream(in);	
							AudioPlayer.player.start(as);  
							Thread.sleep(timePlaySound);
							AudioPlayer.player.stop(as);
						} catch (Throwable th) {
							
						}
		  			}
		  		}).start();
		  		new Thread(new Runnable() {
		  		
					public void run() {	 
						try {
		  					Thread.sleep(timeDismiss);
							dlg.dispose();
						} catch (Throwable th) {
						}
											
				  	}
				}).start();
		  		dlg.setVisible(true);
			}
			toolkit.beep();
		}
	}

	public static String[] getDataFromTask(Task task, int i) {
		String[] data = new String[9];
		data[0] = String.valueOf(i + 1);
		data[1] = removeSlash(task.getName());
		data[2] = mDateTimeHelper.convertToDisplayFormat(task.getDeadline());
		
		int repeatedType = task.getRepeatedType();
		switch (repeatedType) {
			case AppConst.REPEATED_TYPE.FROM_TO: 
				data[5] = task.getPeriod();
				data[3] = mDateTimeHelper.convertDateMonthToDisplayFormat(task.getStartDate());
				data[4] = mDateTimeHelper.convertDateMonthToDisplayFormat(task.getEndDate());
				break;
			case AppConst.REPEATED_TYPE.EVERY_WEEK:
			case AppConst.REPEATED_TYPE.EVERYDAY:
				data[3] = mDateTimeHelper.getStringStartTimeForStringPeriod(task.getPeriod());
				data[4] = mDateTimeHelper.getStringEndTimeForStringPeriod(task.getPeriod());
				if (repeatedType == AppConst.REPEATED_TYPE.EVERY_WEEK) { 
					data[5] = EVERY + mDateTimeHelper.getStringDayInWeekForDate(task.getStartDate());
				} else {
					data[5] = EVERYDAY;
				}
				break;
			
			default: 
				data[3] = mDateTimeHelper.convertToDisplayFormat(task.getStartDate());
				data[4] = mDateTimeHelper.convertToDisplayFormat(task.getEndDate());
				data[5] = "";
		}
		data[6] = task.getPriority();
		data[6] = data[6].substring(0, 1).toUpperCase() + data[6].substring(1);
		data[7] = removeSlash(task.getGroup());
		data[8] = task.getStatus();
		return data;
	}
	
	private static String removeSlash(String st) {
		if (st == null || st.equals("")) {
			return st;
		}
		
		String splits[] = st.split(" ");
		if (st.length() == 0) {
			return st;
		}
		
		String[] keys = AppConst.KEY_WORD.keywords;
		
		for(int i=0; i<splits.length; i++) {
			if (splits[i].startsWith(SLASH) && splits[i].length()>1) {
				String s = splits[i].substring(1, splits[i].length());
				boolean isKeyword = false;
				for(int j=0; j<keys.length; j++) {
					if (keys[j].equals(s)) {
						isKeyword = true;
						break;
					}
				}
				if (isKeyword) {
					splits[i] = s;
				}
			}
		}
		
		String result = "";
		for(int i=0; i<splits.length; i++) {
			if (i > 0) {
				result += " ";
			}
			result += splits[i];
		}
		return result;
		
	}

	
	
	private static void displayMessage(String message) {
		output.append(message);
	}


	private static void openToDoListWindow() {
        // Create a window for app
        frame = new JFrame(APP_NAME);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        
        // Create text area to display message to users
        ButtonListener buttonListener = new ButtonListener();
        output = new JTextArea(windowHeight, windowWidth);
        output.setBackground(Color.white);
        output.setForeground(Color.black);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setEditable(false);
        
        // Create table to display tasks
        table = new JTable() {
		    @Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				Object value = getModel().getValueAt(row, col);
				comp.setBackground(Color.white);
				
				Object checkValue = getModel().getValueAt(row, 6);
				if (checkValue.equals(HIGH) || checkValue.equals(MEDIUM) || checkValue.equals(LOW)) {
					if (col == 6) {
						if (value.equals(HIGH)) {
							comp.setBackground(Color.red);
						} else if (value.equals(MEDIUM)) {
							comp.setBackground(Color.yellow);
						} else if (value.equals(LOW)) {
							comp.setBackground(Color.green);
						}
					}
				} else {
					String st = (String)value;
					if (st.endsWith(AppConst.TASK_FIELD.HIGH)) {
						comp.setBackground(Color.red);
					} else if (st.endsWith(AppConst.TASK_FIELD.MEDIUM)) {
						comp.setBackground(Color.yellow);
					} else if (st.endsWith(AppConst.TASK_FIELD.LOW)) {
						comp.setBackground(Color.green);
					}
				}
				return comp;
    		}
        };
       
        table.setRowHeight(rowHeightDefault);
        
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setColumnIdentifiers(columnNames);
        
        for (int i = 0 ; i <= 8; i++) {
        	//if (i != 1) {
        		table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        		table.getColumnModel().getColumn(i).setMaxWidth(columnWidth[i]);
        	//}
        }
        
        // Set data for table
        for (int i = 0; i < dataTaskList.size(); i++) {
            String[] data = getDataFromTask(dataTaskList.get(i), i);
            tableModel.addRow(data);
        }
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0 ; i <= 8; i++) {
			if (i != 7 && i != 1 ) {
				table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        	}
        }
       
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
		// Create scroll bar if table area is full        
        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setFillsViewportHeight(true);

		// Create scroll bar if text area is full
        JScrollPane scroller2 = new JScrollPane(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Create input pannel to get user's command
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        input = new JTextField(windowWidth);
        input.setBackground(Color.white);
        input.setForeground(Color.red);
        input.setCaretColor(Color.blue);
        input.setActionCommand(ENTER);  
        input.addActionListener(buttonListener);
        input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                panelKeyPressAction(evt);
            }
        });
        
        DefaultCaret caret = (DefaultCaret) output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(scroller);
        panel.add(scroller2);
        inputpanel.add(input);
        panel.add(inputpanel);
        
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        
        input.requestFocus();
        userCommandCount = 0;
        displayMessage(COMMAND_MESSAGE);
    }  

	// Support historical user commands by press key UP and DOWN
    private static void panelKeyPressAction(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) {
            String userCommand = "";
            if (event.getKeyCode() == KeyEvent.VK_UP) {
                userCommandCount++;
                if (userCommandCount > mMainLogic.getOldUserCommandSize()) {
                    userCommandCount--;
                }
                userCommand = mMainLogic.getOldUserCommand(userCommandCount);
            } else {
                userCommandCount--;
                if (userCommandCount < 0) {
                    userCommandCount = 0;
                } else {
                    userCommand = mMainLogic.getOldUserCommand(userCommandCount);
                }
            }
            input.requestFocus();
            input.setText(userCommand);
            input.setCaretPosition(userCommand.length());
        }
        
        if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
        	userScrollCount++;
        	if (userScrollCount + MAX_NUMBER_ROWS > mTableRowCount) {
        		userScrollCount--;
        	}
        	
        	table.scrollRectToVisible(new Rectangle(0, 
        											userScrollCount * table.getRowHeight(), 
        											table.getWidth(), 
        											MAX_NUMBER_ROWS * table.getRowHeight()));
        	
        }
        if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
        	
        	userScrollCount--;
        	if (userScrollCount < 0) {
        		userScrollCount++;
        	}
        	
        	table.scrollRectToVisible(new Rectangle(0, 
        											userScrollCount * table.getRowHeight(), 
        											table.getWidth(), 
        											MAX_NUMBER_ROWS * table.getRowHeight()));
        }
    }
    
    public static void updateTable() {
         
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    	DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(columnNames);
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for(int i = 0; i <= 8; i++) {
			if (i != 7 && i != 1 ) {
				table.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        	}
        }
        
        for(int i = 0; i <= 8; i++) {
        	//if (i != 1) {
        		table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        		table.getColumnModel().getColumn(i).setMaxWidth(columnWidth[i]);
        	//}
        }
        
        for(int i = 0; i < dataTaskList.size(); i++) {
        	String[] data = getDataFromTask(dataTaskList.get(i), i);
			tableModel.addRow(data);
		}
		table.setModel(tableModel);
        tableModel.fireTableDataChanged();
    }
    
    
    public static void createTimetable(String userCommand) {
    
    	userCommand = mMainLogic.removeSpace(userCommand);
    	String[] commands = userCommand.split(" ");
    		
    	String startDate = mCommandParser.getStartDateForTimetable(userCommand);
    	String endDate = mCommandParser.getEndDateForTimetable(userCommand);
    	if (startDate == null || startDate.equals("") || endDate == null || endDate.equals("")) {
    		updateTable();
    		displayMessage(AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT);
    		return;
    	}
    	
    	int from = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(startDate), mDateTimeHelper.getMonthFromStringDate(startDate));
    	int to = mDateTimeHelper.getNumberOfDayFromThisYearForDate(mDateTimeHelper.getDayFromStringDate(endDate), mDateTimeHelper.getMonthFromStringDate(endDate));
		if (from > to) {
			displayMessage(AppConst.MESSAGE.INVALID_DATE_TIME_FORMAT);
			updateTable();
			return;
		}
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
    	tableModel.setRowCount(0);
    	tableModel.setColumnIdentifiers(timeTableColumnNames);		
    	table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(0).setMaxWidth(150);
		 
		for(int i = from; i <= to; i++) {   	
			String date = mDateTimeHelper.getDateForNumberOfDays(i);
			
			// TIME_ZERO = 00:00
			date += TIME_ZERO;
			int[] timetable = mDateTimeHelper.getTimetableForDate(date, dataTaskList);
			String[] data = new String[13];
			data[0] = mDateTimeHelper.convertDateMonthToDisplayFormat(date) + " (" + mDateTimeHelper.getStringDayInWeekForDate(date) + ")";
			for(int j=1; j<=12; j++) {
				if (timetable[j-1] != -1) {
					data[j] = dataTaskList.get(timetable[j-1]).getName() + "                      " + AppConst.TASK_FIELD.PRIORITY + dataTaskList.get(timetable[j-1]).getPriority();
				} else {
					data[j] = "";
				}
			}
			tableModel.addRow(data);
		}
		mTableRowCount = to - from + 1;
    	tableModel.fireTableDataChanged();
    }

    public static class ButtonListener implements ActionListener
    {

        public void actionPerformed(final ActionEvent ev)
        {
            if (!input.getText().trim().equals(""))
            {
                String cmd = ev.getActionCommand();
                if (ENTER.equals(cmd))
                {
                	String userCommand = input.getText();
                	output.setText("");
                	displayMessage(COMMAND_MESSAGE + userCommand + NEW_LINE);
                	
                	// Clear text field
                    if (userCommand.equals(AppConst.COMMAND_TYPE.CLEAR)) {
                        output.setText("");
                        mMainLogic.deleteAllUserCommands();
                        displayMessage(COMMAND_MESSAGE);
                        userCommandCount = 0;
                    } else {
                    	
                    	// Executed user command
                        ArrayListPointer dataTaskListPointer = new ArrayListPointer();   
                        dataTaskListPointer.setPointer(dataTaskList);
                    	String message = mMainLogic.process(userCommand, dataTaskListPointer);
                        dataTaskList = dataTaskListPointer.getPointer();
                        mTableRowCount = dataTaskList.size();
                        userScrollCount = 0;
                        
                        String[] commands = userCommand.split(" ");
                        if (commands[0].equals(AppConst.COMMAND_TYPE.TIMETABLE)) {
                        	createTimetable(userCommand);
                        } else if (message == null) {
                    		displayMessage(AppConst.MESSAGE.GOODBYE);
                    		frame.setVisible(false);
                    		frame.dispose();
                    	} else {
                    		updateTable();
                    		displayMessage(message);
                    	}
                    }
                    userCommandCount = 0;
                }
            }
            input.setText("");
            input.requestFocus();
        }
    }

}