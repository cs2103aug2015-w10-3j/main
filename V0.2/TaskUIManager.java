import java.io.*;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class TaskUIManager {

	private static String ENTER = "Enter";
    private static String CLEAR = "clear";
    private static String APP_NAME = "To-Do";
    private static String SHOWALL_COMMAND = "showall";
	private static String GOOBYE_MESSAGE = "Goodbye!";
	private static String WELCOME_MESSAGE = "Welcome to To-Do list. These are your tasks: \n";
    private static String COMMAND_MESSAGE = "$" + APP_NAME + ": ";
    private static String NEW_LINE = "\n";

    static JButton enterButton;
    public static JTextArea output;
    public static JTextField input;
    static JFrame frame;
    static JPanel panel;

	static int windowHigh = 30;
	static int windowWidth = 70;
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
        openToDoListWindow();

        displayMessage(WELCOME_MESSAGE);
        String message = mMainLogic.process(SHOWALL_COMMAND);
        assert message != null;
        displayMessage(message);
        displayMessage(NEW_LINE);
        displayMessage(COMMAND_MESSAGE);
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
        output = new JTextArea(windowHigh, windowWidth);
        output.setBackground(Color.green);
        output.setForeground(Color.blue);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setEditable(false);
        
        // Create scroll bar if text area is full
        JScrollPane scroller = new JScrollPane(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Create input pannel to get user's command
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        input = new JTextField(windowWidth);
        input.setBackground(Color.green);
        input.setForeground(Color.blue);
        input.setCaretColor(Color.red);
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
                    if (userCommand.equals(CLEAR)) {
                        output.setText("");
                        mMainLogic.deleteAllUserCommands();
                        userCommandCount = 0;
                    } else {
                    	String message = mMainLogic.process(userCommand);

                        displayMessage(userCommand);
                        displayMessage(NEW_LINE);
                    	if (message == null) {
                    		displayMessage(GOOBYE_MESSAGE);
                    		frame.setVisible(false);
                    		frame.dispose();
                    	} else {
                    		displayMessage(message);
                            displayMessage(NEW_LINE);
                    	}
                    }
                    userCommandCount = 0;
                }
            } else {
                displayMessage(NEW_LINE);
            }
            input.setText("");
            input.requestFocus();
            displayMessage(COMMAND_MESSAGE);
        }
    }

}