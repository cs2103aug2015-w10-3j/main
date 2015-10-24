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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class TaskUIManager {

	private static String ENTER = "Enter";
    private static String APP_NAME = "To-Do";
	private static String GOOBYE_MESSAGE = "Goodbye!";
	private static String WELCOME_MESSAGE = "Welcome to To-Do list. ";
    private static String COMMAND_MESSAGE = "$" + APP_NAME + ": ";
    private static String NEW_LINE = "\n";

    static JButton enterButton;
    public static JTextArea output;
    public static JTextField input;
    static JFrame frame;
    static JPanel panel;
    static JTable table;
    
    // column to display in table
    static String[] columnNames = new String[] {"Task Name",
                        "Deadline",
                        "Start Date",
                        "End Date",
                        "Prioriry",
                        "Group",
                        "Status"
                        };
    static ArrayList<Task> dataTaskList = new ArrayList<Task>();
	static int windowHeight = 5;
	static int windowWidth = 80;
    static int userCommandCount = 0;

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
        String message = mMainLogic.process(AppConst.COMMAND_TYPE.SHOW_ALL, dataTaskList);
        
        
        Storage storage = new Storage();
        storage.setFileURL("data1.txt");
        try {
        	dataTaskList = storage.readContent();
        } catch (IOException e) {
        }
        
        openToDoListWindow();

        displayMessage(WELCOME_MESSAGE);
        assert message != null;
        displayMessage(message);
	}
	
	public static String[] getDataFromTask(Task task) {
		String[] data = new String[7];
		data[0] = task.getName();
		data[1] = task.getDeadline();
		data[2] = task.getStartDate();
		data[3] = task.getEndDate();
		data[4] = task.getPriority();
		data[5] = task.getGroup();
		data[6] = task.getStatus();
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
				if (col == 4) {
					if (value.equals("high")) {
						comp.setBackground(Color.red);
					} else if (value.equals("medium")) {
						comp.setBackground(Color.yellow);
					} else if (value.equals("low")) {
						comp.setBackground(Color.green);
					}
				}
				return comp;
    		}
        };
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setColumnIdentifiers(columnNames);
        
        // Set data for table
        for(int i=0; i<dataTaskList.size(); i++) {
            String[] data = getDataFromTask(dataTaskList.get(i));
            tableModel.addRow(data);
        }
        
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
                    	String message = mMainLogic.process(userCommand, dataTaskList);
                    	
                    	Storage storage = new Storage();
        storage.setFileURL("data1.txt");
        try {
        	dataTaskList = storage.readContent();
        } catch (IOException e) {
        }
                    	
                    	// Message = null means user want to exit
                    	if (message == null) {
                    		displayMessage(GOOBYE_MESSAGE);
                    		frame.setVisible(false);
                    		frame.dispose();
                    	} else {
                    		
                    		// Updated table depends on user command
                    		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                    		tableModel.setRowCount(0);
				            for(int i=0; i<dataTaskList.size(); i++) {
				            	String[] data = getDataFromTask(dataTaskList.get(i));
				            	tableModel.addRow(data);
				            }
                    		tableModel.fireTableDataChanged();
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
