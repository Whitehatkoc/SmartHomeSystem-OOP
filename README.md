# SmartHomeSystem-OOP
compile at src/
Compilation: javac Main.java
Run: java Main input.txt output.txt
! The input and output files must be inside the src folder !

In this assignment, which is the second assignment of the spring semester of 2023 and aims to teach OOP design, we were asked to code a smart home system.
To get more detailed information about the assignment, you can review the pdf of the assignment, named BBM104_S23_PA2_v2, prepared by our teachers. But important parts of the homework are these:

In this project, there are some smart home devices as follows: Smart Lamp (with whiteambiance and color-white-ambiance variants), Smart Plug, and Smart Camera.
Each device has its own features and functions.
The task here is, controling these devices and time with respect to commands which will be given the input.txt file. 
The commands that the system can process are defined. For example, a few commands related to time: "Set Initial Time","Set Time", "Skip Minutes" ; a few commands related to addition a devices to the system : "Adding a SmartPlug", "Adding a SmartColorLamp", "Removing Commands" ; other commands: "Nop"," Changing name of a device","Z Report". There are also special commands for each device, for example, the commands of the smart plug : commands such as "Plug In", "Plug Out".
If you want to get more accurate information about the commands, you can review the assignment pdf. 
You can access the sample input files containing the commands Assignment2/BBM104_S23_PA2_IO_Files_v2.1 here.
The program I have coded distinguishes between legal and illegal commands by debugging the input file, processes the legal commands and executes the command, and sends the user that it has successfully executed the command by typing the command into the output.txt file. For illegal commands, it prints the corresponding error message to the output.txt file.
The program that receives the Z Report command writes all the devices registered in the system to the output file along with the status of all the features belonging to that device. You can get a better understanding by examining the output file examples.
The report in the Assignment2 file is not about the code I wrote, but about my general ideas about the assignment.

