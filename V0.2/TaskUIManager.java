import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.lang.*;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class TaskUIManager {

	private static String ENTER = "Enter";
    private static String APP_NAME = "To-Do";
    private static String COMMAND_MESSAGE = "$" + APP_NAME + ": ";
    private static String NEW_LINE = "\n";
    private static int MAX_NUMBER_ROWS = 16;

    static JButton enterButton;
    public static JTextArea output;
    public static JTextField input;
    static JFrame frame;
    static JPanel panel;
    static JTable table;
    
    // column to display in table
    static String[] columnNames = new String[] {"#", "Task Name",
                        "Deadline",
                        "Start Date",
                        "End Date",
                        "Prioriry",
                        "Group",
                        "Status"
                        };
    static int[] columnWidth = new int[] {	40,
    										0,
    										100,
    										100,
    										100,
    										80,
    										150,
    										80
    	
    };
    static ArrayList<Task> dataTaskList = new ArrayList<Task>();
    static ArrayList<Task> mSaveDataList = new ArrayList<Task>();
	static int windowHeight = 5;
	static int windowWidth = 80;
	static int rowHeightDefault = 25;
    static int userCommandCount = 0;
    static int userScrollCount = 0;

	public TaskUIManager() {

	}

	static MainLogic mMainLogic = new MainLogic();
	public static void main(String[] argv) {

		try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        ArrayListPointer dataTaskListPointer = new ArrayListPointer();   
        String message = mMainLogic.process(AppConst.COMMAND_TYPE.SHOW_ALL, dataTaskListPointer);
        dataTaskList = dataTaskListPointer.getPointer();
        mSaveDataList = dataTaskList;
                
        openToDoListWindow();

        displayMessage(AppConst.MESSAGE.WELCOME);
        assert message != null;
        displayMessage(message);
	}
	
	public static String[] getDataFromTask(Task task, int i) {
		String[] data = new String[8];
		data[0] = String.valueOf(i);
		data[1] = task.getName();
		data[2] = task.getDeadline();
		data[3] = task.getStartDate();
		data[4] = task.getEndDate();
		data[5] = task.getPriority();
		data[6] = task.getGroup();
		data[7] = task.getStatus();
		return data;
	}

	
	
	private static void displayMessage(String message) {
		output.append(message);
	}


	private static void openToDoListWindow()
    {
        // Create a windown for app
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
				if (col == 5) {
					if (value.equals(AppConst.TASK_FIELD.HIGH)) {
						comp.setBackground(Color.red);
					} else if (value.equals(AppConst.TASK_FIELD.MEDIUM)) {
						comp.setBackground(Color.yellow);
					} else if (value.equals(AppConst.TASK_FIELD.LOW)) {
						comp.setBackground(Color.green);
					}
				}
				return comp;
    		}
        };
       
        table.setRowHeight(rowHeightDefault);
        
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setColumnIdentifiers(columnNames);
        
        for(int i=0; i<=7; i++) {
        	if (i != 1) {
        		table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
        		table.getColumnModel().getColumn(i).setMaxWidth(columnWidth[i]);
        	}
        }
        
        // Set data for table
        for(int i=0; i<dataTaskList.size(); i++) {
            String[] data = getDataFromTask(dataTaskList.get(i), i);
            tableModel.addRow(data);
        }
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
		centerRender.setHorizontalAlignment(SwingConstants.CENTER);
		for(int i=0; i<=7; i++) {
			if (i != 6 && i != 1 ) {
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

	// Support history user commands by press key UP and DOWN
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
        	if (userScrollCount + MAX_NUMBER_ROWS > dataTaskList.size()) {
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
    
    	DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setRowCount(0);
        for(int i=userScrollCount; i<dataTaskList.size(); i++) {
        	String[] data = getDataFromTask(dataTaskList.get(i), i);
			tableModel.addRow(data);
		}
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
                    	String message = mMainLogic.process(userCommand, dataTaskListPointer);
                        dataTaskList = dataTaskListPointer.getPointer();
                        mSaveDataList = dataTaskList;
                    	
        			   	// Message = null means user want to exit
                    	if (message == null) {
                    		displayMessage(AppConst.MESSAGE.GOODBYE);
                    		frame.setVisible(false);
                    		frame.dispose();
                    	} else {
                    		userScrollCount = 0;
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
