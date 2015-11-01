This is the guide for demo our product. I will list out all functions of this project with examples:

I. First, there are 4 kinds of tasks in our product: 
a. There are 4 kinds of tasks:
- Task without deadline
- Task with deadline
- Task event (from date to date)
- Task recurrent (from time to time, from date to date/ every day/ every week)

b. A tasks consists of:
- task name <required, can not be null, empty or only space>
- deadline <optional, date and time>
- start date/time <optional, for event task, start date/time is required and is the start date and time of event, for recurrent task, start date/time is the start time for the recurrent, default "00:00" if recurrent type from to, for recurrent type every (day or day in week), start date/time is the start time>
- end date/time <optional, for event task, end date/time is required and is the end date and time of event, for recurrent task, end date/time is the end time for the recurrent, default "23:59" if recurrent type from to, for recurrent type every (day or day in week), end date/time is the end time>
- period <only for recurrent task, will be every day, every <a day in week> or the start time, end time for from to recurrent task>
- priority: high/medium/low, default medium
- group: <optional>
- status: undone/done

II. Key words:

- List of key words:
	"from", 
	"to",
	"by",
	"before",
	"repeat",
	"group",
	"grp",
	"priority",
	"TO"
	
NOTE: If you want to use key words in task name or task group, you have to add "\" in front.

III. Command format

See next sections for Date time format when using this product

Two tasks ar consider the same if they has the same task name, deadline, start date, end date, start time, end time, period, priority, group

1. ADD
a. add task without deadline:
	- add <task name> <priority> <group>
	Example: add do homework priority high group CS2101.
	If no priority provided, default "medium"
	
b. add task with deadline:
	- add <task name> by <deadline> <priority> <group>
	Example: add do homework by 02 Nov
	If no time provided, default "23:59"

c. add event:
	- add <task name> from <start date> to <end date>
	Example: add do homework from 01/11 to 02/11
	If no time provided, default "00:00" for start date, "23:59" for end date. Also can add deadline into event.
	
d. add recurrent task:
	- add <task name> from <start time> to <end time> repeat from <start date> to <end date>
	Example: add do homework from 2pm to 4pm repeat from 02/11 to 03/11
	- add <task name> from <start time> to <end time> repeat every day
	Example: add do homework from 8pm to 10pm repeat every day
	- add <task name> from <start time> to <end time> repeat <every> <day in week>
	Example: add CS2101 section from 12pm to 2pm repeat every Mon
	If no start time/end time provided, default "00:00" for start time, "23:59" for end time.
	
NOTE: make sure new task not the same as 1 one of the existing tasks, and the start date/time and end date/time not overlap the other tasks.
2. UPDATE

- update <task to be updated> TO <new task>

+ need to specify the old task clearly, avoid multiple tasks found
+ need to make sure new task will not be one of the existing tasks, and start date/time and end date/time not overlap the other tasks.

3. DELETE

a. delete a task:
	- delete <task to be delete>
	
b. delete a group of tasks:
	- deleteby <field> <value of field>
	Example: deleteby priority medium, deleteby status done
c. delete all task:
	- deleteall
	
4. SHOW

a. show a day:
	- showday <date>
	Example: showday 02/11
b. showpriority/ showgroup:
	- showpriority high/ showgroup <group name>
c. showby:
	- showby deadline/start date/ end date/ priority/group/status
	all tasks will be sorted
d. show:
	- like search
c. show all tasks:
	- showall
	
5. CLOSE
- if you want to mark a task as done, use close command
- close <task to close>

5. OPEN
- if you want to mark a task as undone, open the task again
- open <task to open>

6. SEARCH
- search <description>
Example: search do homework

7. REDO
- redo

8. UNDO
- undo

9. SETFILE
- setfile to change the file for saving tasks

10. EXIT
- exit program

11. CLEAR
- clear display message

12. TIMETABLE
- timetable <date to display>
- timetable from <date> to <date>

- Use timetable command to display timetable


Other functions:

UP/DOWN to see old commands
PAGE UP/DOWN to scroll table

	
IV. Date time format

Date format: You can use multiple date format, for example:
- yesterday/ytd/today/tomorrow/tmr/last Fri/last week/last 2 weeks/Fri last week/Fri last 2 weeks/Fri/this Fri/Fri this week/next Fri/Fri next 2 weeks/last 2 days/next 2 days/"02/11"/"02-11"/02 Nov/ 2 of Nov/2 of November/...

Time format: you can use multiple time format, for example:
- 1400/2pm/14:00/14.00/630...
	
	
