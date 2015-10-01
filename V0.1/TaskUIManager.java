import java.io.*;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class TaskUIManager {

	private static String ENTER = "Enter";
	static JButton enterButton;
    public static JTextArea output;
    public static JTextField input;
    static JFrame frame;
    static JPanel panel;
	private static String goodByeMessage = "Goodbye!";
	private static String welcomeMessage = "Welcome to To-Do list. These are your tasks: \n";
	static int windowHigh = 20;
	static int windowWidth = 69;

	public TaskUIManager() {

	}

	static TaskLogic mTaskLogic = new TaskLogic();
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

        displayMessage(welcomeMessage);
        String message = mTaskLogic.showAll();
        displayMessage(message);
	}

	private static void displayMessage(String message) {
		output.append(message);
	}

	private static void openToDoListWindow()
    {
        frame = new JFrame("To-Do");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        
        ButtonListener buttonListener = new ButtonListener();
        output = new JTextArea(windowHigh, windowWidth);
        output.setWrapStyleWord(true);
        output.setEditable(false);
        
        JScrollPane scroller = new JScrollPane(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        input = new JTextField(windowWidth);
        input.setActionCommand(ENTER);
        input.addActionListener(buttonListener);
        
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
                	String message = mTaskLogic.process(userCommand);
                	if (message == null) {
                		displayMessage(goodByeMessage);
                		frame.setVisible(false);
                		frame.dispose();
                	} else {
                		displayMessage(message);
                	}
                }
            }
            input.setText("");
            input.requestFocus();
        }
    }

}