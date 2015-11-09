package cs2103;
import java.io.*;
import java.util.*;

import javax.swing.*;

import  sun.audio.*; 

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.Rectangle;
import javax.swing.text.DefaultCaret;

public class TaskUIManager {
    
	private static TableHelper mTableHelper = new TableHelper();
	private static MainLogic mMainLogic = new MainLogic();
	
    static JButton enterButton;
    public static JTextArea output;
    public static JTextField input;
    static JFrame frame;
    static JPanel panel;
    static JTable table;
    static Toolkit toolkit;
    static Timer timer;								
    										
    static ArrayList<Task> dataTaskList = new ArrayList<Task>();
    static int windowHeight = AppConst.UI_CONST.DEFAULT_WINDOW_HEIGHT;
	static int windowWidth = AppConst.UI_CONST.DEFAULT_WINDOW_WIDTH;
    static int userCommandCount = 0;
    static int userScrollCount = 0;
    static int mTableRowCount = 0;
    
	public TaskUIManager() {

	}
	
	public static void main(String[] argv) {

		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
		
        ArrayListPointer dataTaskListPointer = new ArrayListPointer();
        
        toolkit = Toolkit.getDefaultToolkit();
        Dimension d = toolkit.getScreenSize();
		windowWidth = (int)(d.width * AppConst.UI_CONST.DEFAULT_RATIO);
        
        // Input a showall command upon launching Todoer
        String message = mMainLogic.process(AppConst.COMMAND_TYPE.SHOW_ALL, dataTaskListPointer);
        dataTaskList = dataTaskListPointer.getPointer();
        mTableRowCount = dataTaskList.size();
        mTableHelper.setDataListForTable(dataTaskList);
        mTableHelper.createTableToDisplayTasks();    
        
        openToDoListWindow();

        displayMessage(AppConst.MESSAGE.WELCOME);
        assert message != null;
        displayMessage(message);
        
        // Set schedule for reminding
        
	    timer = new Timer();
		timer.scheduleAtFixedRate(new RemindTask(), 0, AppConst.UI_CONST.DEFAULT_TIME_REMIND);
	}
	
	// Display message in the display message box
	private static void displayMessage(String message) {
		output.append(message);
	}

	/* 
	** The window contains a table box to display table, a display message box to display message,
	** and the user input field for user typing commands
	*/
	private static void openToDoListWindow() {
	
        // Create a window for app
        frame = new JFrame(AppConst.UI_CONST.APP_NAME);
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
        
        table = mTableHelper.getTable();
		// Create scroll bar if table area is full        
        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setFillsViewportHeight(true);

		// Create scroll bar if text area is full
        JScrollPane scroller2 = new JScrollPane(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Create input panel to get user's command
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        input = new JTextField(windowWidth);
        input.setBackground(Color.white);
        input.setForeground(Color.red);
        input.setCaretColor(Color.blue);
        input.setActionCommand(AppConst.UI_CONST.ENTER);  
        input.addActionListener(buttonListener);
        input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                panelKeyPressAction(evt);
            }
        });
        
        // Added the table box, display message box and the input to the panel
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
        displayMessage(AppConst.UI_CONST.COMMAND_MESSAGE);
    } 

    public static class ButtonListener implements ActionListener
    {
        public void actionPerformed(final ActionEvent ev)
        {
            if (!input.getText().trim().equals(""))
            {
                String cmd = ev.getActionCommand();
                if (AppConst.UI_CONST.ENTER.equals(cmd))
                {
                	String userCommand = input.getText();
                	output.setText("");
                	displayMessage(AppConst.UI_CONST.COMMAND_MESSAGE + userCommand + AppConst.UI_CONST.NEW_LINE);
                	
                	// Clear text field
                    if (userCommand.equals(AppConst.COMMAND_TYPE.CLEAR)) {
                        output.setText("");
                        mMainLogic.deleteAllUserCommands();
                        displayMessage(AppConst.UI_CONST.COMMAND_MESSAGE);
                        userCommandCount = 0;
                    } else {
                    	
                    	// Executed user command
                        ArrayListPointer dataTaskListPointer = new ArrayListPointer();   
                        dataTaskListPointer.setPointer(dataTaskList);
                    	String message = mMainLogic.process(userCommand, dataTaskListPointer);
                        dataTaskList = dataTaskListPointer.getPointer();
                        mTableRowCount = dataTaskList.size();
                        userScrollCount = 0;
                        mTableHelper.setDataListForTable(dataTaskList);
                        String[] commands = userCommand.split(" ");
                        if (commands[0].equals(AppConst.COMMAND_TYPE.TIMETABLE)) {
                        	displayMessage(mTableHelper.createTimetable(userCommand));
                        	mTableRowCount = mTableHelper.getTableRowCount();
                        } else if (message == null) {
                    		displayMessage(AppConst.MESSAGE.GOODBYE);
                    		frame.setVisible(false);
                    		frame.dispose();
                    	} else {
                    		mTableHelper.createTableToDisplayTasks();
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
    
    // Support historical user commands by press key UP and DOWN
	// Support scroll table by press key PgUp and PgDn
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
        	if (userScrollCount + AppConst.UI_CONST.MAX_NUMBER_ROWS > mTableRowCount) {
        		userScrollCount--;
        	}
        	
        	table.scrollRectToVisible(new Rectangle(0, 
        											userScrollCount * table.getRowHeight(), 
        											table.getWidth(), 
        											AppConst.UI_CONST.MAX_NUMBER_ROWS * table.getRowHeight()));
        	
        }
        if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
        	
        	userScrollCount--;
        	if (userScrollCount < 0) {
        		userScrollCount++;
        	}
        	
        	table.scrollRectToVisible(new Rectangle(0, 
        											userScrollCount * table.getRowHeight(), 
        											table.getWidth(), 
        											AppConst.UI_CONST.MAX_NUMBER_ROWS * table.getRowHeight()));
        }
    }
    
    public static class RemindTask extends TimerTask {
	
		@Override
		public void run() {
			
			/*
			** Get the list of tasks with the reminder time is coming
			** User can set the reminder time for a task by using repeat id <id> <time>
			*/
			ArrayListPointer dataTaskListPointer = new ArrayListPointer();   
            dataTaskListPointer.setPointer(dataTaskList);
			String message = mMainLogic.process(AppConst.COMMAND_TYPE.REMIND, dataTaskListPointer);
			
			if (message != null) {				
				
				dataTaskList = dataTaskListPointer.getPointer();
				mTableRowCount = dataTaskList.size();
				userScrollCount = 0;
				mTableHelper.setDataListForTable(dataTaskList);
				mTableHelper.createTableToDisplayTasks();
				output.setText("");
				displayMessage(message);
				
				// show notification to users
				// option pane with no buttons.
				JOptionPane opt = new JOptionPane(	AppConst.MESSAGE.NOTIFICATIONS, 
													JOptionPane.INFORMATION_MESSAGE, 
													JOptionPane.DEFAULT_OPTION, 
													null, 
													new Object[]{AppConst.UI_CONST.OK_BUTTON}); 
													
		  		final JDialog dlg = opt.createDialog(AppConst.UI_CONST.NOTIFICATION);
		  		new Thread(new Runnable() {
					@SuppressWarnings("restriction")
					public void run() {		
		  				try {
		  					/* 
		  					** Turn on the sound and the notification
		  					** User can turn off by press ENTER
		  					** If not, after timeDismiss seconds, the notification will be turned off
		  					*/
		  					InputStream in = new FileInputStream(AppConst.UI_CONST.DEFAULT_SOUND_FILE_NAME);
							AudioStream as = new AudioStream(in);	
							AudioPlayer.player.start(as);  
							Thread.sleep(AppConst.UI_CONST.DEFAULT_TIME_PLAY_SOUND);
							AudioPlayer.player.stop(as);
							Thread.sleep(AppConst.UI_CONST.DEFAULT_TIME_DISMISS);
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
    

}
