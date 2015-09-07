# About
Todoer is a keystroke-orientated task scheduler that provides functionality for managing to-do lists. The UI is designed to provide a 'command-line' feel that we know our users will love. Follow this guide to learn how to use Todoer effectively.

# Table of Contents
<!-- MarkdownTOC -->

- [Installation and Setup](#setup)
- [Feature Details](#feature-details)
  - [Adding a task](#add-task)
  - [Deleting a task](#delete-task)
  - [Updating a task](#update-task)
  - [Viewing tasks](#view-tasks)
  - [Changing settings](#settings)
  - [Exiting](#exiting)
- [Help Panel](#help-panel)
- [Cheatsheet](#cheatsheet)

<!-- /MarkdownTOC -->

# Installation and Setup

1. **Download Todoer**: You can save Todoer into any folder of your choice. All your data will    be saved 

2. **Launch Todoer**: by simply double-clicking the `Todoer.exe file`.

3. You will be greeted with a simple interface that has a console and a command bar. 
   This command bar is where you enter commands to tell Todoer what to do. 

5. **Set notifications for Todoer**: When launching for the first time, Todoer will ask you to set the frequency and audio controls for notifications and reminders about tasks that are nearing deadlines.
   
6. **Add a task**: Type `add 'do laundry'` in the command bar. Follow the prompts to add your first task!

7. **Try more commands**: 
     * `showall` - See all outstanding tasks
     * `delete 'do laundry'` - Mark the task as done
     * `exit` - Exit Todoer using the command bar

To learn more details of Todoer features, refer to the 'Feature Details' section below.

# Feature Details
## Adding a task
###### Add a task using the console wizard
1. Type `add` into the command bar. 
2. The console will display instructions: **Specify a task name:**. Type in a suitable task name in the command bar
3. Follow the remaning console instructions to finish adding the task. Note you can enter a `-` if you wish to leave a field (such as 'Priority') blank.

> Tip: Enter `Ctrl + C` during the console wizard prompts to abort the current operation.

###### Add a task using a one-line command
1. Type `add <task_name> <description> <deadline> <priority> <group>
   Parameters:
| task_name   | Name of task                                             |
|-------------|----------------------------------------------------------|
| description | Short description of the task for additional reference   |
| deadline    | Format: `DDMMYY` > e.g. 270915 for (27th September 2015) |
| priority    | Scale: 1 (lowest) - 10 (highest)                         |
| group       |                                                          |
	

> For example: `add 'do assignment 1' 'algebra homework' '251015' '10' 'MA1101'`

## Deleting a task
###### Delete a task using the console wizard
1. Type `delete` into the command bar.
2. The console will display instructions: **Specify which task to delete:**. Enter the task name you wish to delete into the command bar.

###### Delete a task using a one-line command
1. Type `delete <task_name>`

# Cheatsheet

| ID                 | I can … (i.e. Functionality)                                                                            | so that I … (i.e. Value)                                                                                                                           |
|--------------------|---------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| addTask            | add a task by specifying a task name only                                                               | can record tasks that I want to do                                                                                                                 |
| addTaskDeadline    | add a task by specifying a task name and deadline only                                                  | can record tasks that I want to do by a certain date                                                                                               |
| addTaskDescription | add a task by specifying task name and task description (and/or deadline)                               | can record tasks that I want to do followed by some additional details that I need to remind myself of                                             |
| addTaskPriority    | add a task by specifying task name and priority (and/or deadline + description)                         | can record tasks that I want to do and attach a priority rating to it to remind myself of its importance                                           |
| addTaskGroup       | add a task by specifying task name and group (and/or deadline + description + priority)                 | can record tasks that I want to do and place it under a group (e.g. Placing a documentation task under Group: Project A) for organization purposes |
| deleteTask         | delete a task by specifying the task name                                                               | mark the task as done or remove it from the schedule                                                                                               |
| deleteGroup        | delete a group and all the tasks inside                                                                 | mark the group (and all its tasks) as done or remove the group from the schedule                                                                   |
| updateTaskName     | update a task’s name                                                                                    | rename a task to a more appropriate name                                                                                                           |
| updateTaskDeadline | update a task’s deadline                                                                                | set extra time for the task                                                                                                                        |
| updateTaskDesc     | update a task’s description                                                                             | give myself a better description of the task                                                                                                       |
| updateTaskPriority | update a task’s priority                                                                                | lower or raise the priority of a task according to my updated circumstances                                                                        |
| updateTaskGroup    | update a task’s group                                                                                   | reassign the task to a more relevant group                                                                                                         |
| showAll            | show every task that is contained in the scheduler                                                      | review all the tasks I have as a whole                                                                                                             |
| showAllAuto        | Implements the above command ‘showAll’ each time bashlist is opened                                     | can quickly review all my tasks as soon as I launch the application                                                                                |
| showGroup          | show every task that is contained in the specified group                                                | review all the tasks in that particular group                                                                                                      |
| showPriority       | show every task that is above the specified priority                                                    | review all tasks that are urgent to me                                                                                                             |
| showToday          | show every task that is due today                                                                       | review all tasks that must be completed by this day                                                                                                |
| showThisWeek       | show every task that is due this week                                                                   | review all tasks that must be completed by this week                                                                                               |
| showPeriod         | show every task that is due within the specified period                                                 | review all tasks that must be completed within this period                                                                                         |
| show…SortPriority  | show tasks according to any of the above show commands, but sorts the tasks by priority (highest first) | review specific list of tasks, ordered by priority so that I know which ones are urgent and important                                              |
| settings           | review the settings                                                                                     | view the presets for notifications, format display, and additional parameters                                                                      |
| setNotification    | turn on the notification system                                                                         | set automatic reminders for myself for the tasks that are close to due                                                                             |
| setNotifAudio      | turn on notification audio                                                                              | get an audible reminder that I have tasks due soon                                                                                                 |
| setNotifFreq       | set the notification frequency                                                                          | adjust how often and when I want to be reminded of the tasks that are nearing due date                                                             |
| exit               | exits the program                                                                                       | shut down the program and save system resources                                                                                                    |
| minimize           | minimizes the program                                                                                   | allow the program to run in the background                                                                                                         |
| help…              | displays documentation in the help panel in the UI                                                      | easily see how to type commands in the UI                                                                                                          |


