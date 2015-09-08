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
1. Type `add <task_name>` into the command bar (replace \<task_name> with the name of your task).
2. The console will display instructions: **Specify a task description:**. Type in a suitable task description in the command bar
3. Follow the remaning console instructions to finish adding the task. Note you can enter a `-` if you wish to leave a field blank.

> Tip: Enter `Ctrl + C` during the console wizard prompts to abort the current operation.

###### Add a task using a one-line command
1. Type `add <task_name> <description> <deadline> <priority> <group>` in the command bar.

   | Parameter	 | Usage	                                            |
   |-------------|----------------------------------------------------------|
   | task_name   | Name of task to be added                                 |
   | description | Short description of the task for additional reference   |
   | deadline    | Format: `DDMMYY` > e.g. 270915 for (27th September 2015) |
   | priority    | Scale: 1 (lowest) - 10 (highest)                         |
   | group       | Assign the task to a group                               |
	

> For example: `add 'do assignment 1' 'algebra homework' '251015' '10' 'MA1101'`

## Deleting a task
1. Type `delete <task_name>` into the command bar (replace \<task_name> with the name of the task you wish to delete).

## Updating a task
###### Update a task using the console wizard
1. Type `update <task_name>` in the command bar (replace \<task_name> with the name of the task you wish to update).
2. The console will display the task that you wished to update along with instructions: **Enter the detail you wish to update:**. 
3. Follow the remaning console instructions to finish updating the task.

###### Add a task using a one-line command
1. Type `update <task_name> <field_to_update> <new_value>` in the command bar.
   
   | Parameter	 | Usage	                                            |
   |-------------|----------------------------------------------------------|
   | task_name   | Name of task to be updated                              |
   | field_to_update | Possible fields: task_name, description, deadline, priority or group |
   | new_value    | Value that will replace previous value for the field_to_update |

## Viewing tasks
###### Show all the tasks:
1. Type `show` or `show all`  into the command bar.
2. The console will display all the tasks in the ascending order of their deadlines. Tasks with no deadlines will be displayed in a **Tasks with no Deadline** section.

###### Show tasks in a certain group:
1. Type `show <group_name>` into the command bar (replace <group_name> with the name of the group of tasks you wish to see).
2. The console will display all the tasks in that particular group in the ascending order of their deadlines.

###### Show tasks with deadlines within a certain time period:
1. Type `show <deadline>` into the command bar. See below for possible arguments:

  | \<deadline>	| Usage	                                            |
  |-------------|----------------------------------------------------------|
  | \<DDMMYY>   | Show all tasks due on <DDMMYY>  |
  | today | show all tasks due today |
  | tomorrow	| show all tasks due tomorrow |
  | this week	| show all tasks due this week |
  | no deadline	| show all tasks that have no deadline |


The console will display tasks with deadlines that fall within the respective time period, in ascending order of their deadlines.

###### Show tasks in order of importance instead of deadlines:
Simply append `important` to any of the above commands. The tasks will be displayed in order of their priority.

Examples:  
* `show tomorrow important`
* `show 271115 important`

## Settings
###### Change notification settings, and adjust other UI options
1. Type `settings` in the command bar.

## Exiting
1. Type `exit` in the command bar. The program will exit.

## Help Panel
###### Guide to using the help panel found on the left side of the UI
1. The help panel displays the cheatsheet by default.
2. Type `help <command>` in the command bar to get detailed help about <command>
   > Example: `help update`
3. Type `help` to reset the help panel to the default cheatsheet.

# Cheatsheet

| Command            | Description                                                                                      |
|--------------------|--------------------------------------------------------------------------------------------------|
| add \<task_name>    | Add a task called \<task_name> with optional description, deadline, priority and group parameters |
| delete \<task_name> | Deletes \<task_name> from your tasks                                                              |
| update \<task_name> | Updates the details of \<task_name>                                                               |
| show               | View all tasks (in a group or those due within a certain time period)                            |
| settings           | Modify Todoer settings                                                                           |
| help               | Get help for a particular command                                                                |
| exit               | Exit Todoer                                                                                      |
