import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


public class Time {

    // the current time of the program
    public static LocalDateTime dateTime;

    // Getters and Setters
    public static void setDateTime(LocalDateTime newDateTime){
        Time.dateTime = newDateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the initial time based on the input file and command line arguments.
     * Checks the validity of the date entered by the user and throws an error if necessary
     *
     * @param inputFile The array of strings representing the input file lines.
     * @param arg The array of strings representing the command line arguments.
     * @throws Errors.SetInitialTimeException If the first command in the input file is not "SetInitialTime".
     * @throws Errors.ErroneousCommandException If the first command in the input file is not properly formatted.
     */
    public void setInitialTime(String[] inputFile,String[] arg) throws Errors.ErroneousCommandException {
        for (String line : inputFile) {
            if (!line.trim().isEmpty()) {  // omits empty lines in the file
                FileRW.writeToFile(arg[1],"COMMAND: "+line,true,true);
                String[] lineSplitted = line.split("\t");
                if (!lineSplitted[0].equals("SetInitialTime")) { // the first command of the program must be SetInitialTime
                    throw new Errors.SetInitialTimeException("ERROR: First command must be set initial time!" +
                            " Program is going to terminate!");
                }
                if (lineSplitted.length != 2){ // The setInitialTime command must consist of two parts
                    throw new Errors.ErroneousCommandException("ERROR: First command must be set initial time! Program is going to terminate!");
                }
                    String[] dateAndTime= lineSplitted[1].split("_");
                    String[] date = dateAndTime[0].split("-");
                    String[] time = dateAndTime[1].split(":");
                    LocalDateTime paddedTime = LocalDateTime.of(Integer.parseInt(date[0]),Integer.parseInt(date[1]),
                    Integer.parseInt(date[2]),Integer.parseInt(time[0]),Integer.parseInt(time[1]),Integer.parseInt(time[2]));
                    setDateTime(paddedTime);

                // the current time of the program is printed in the desired format
                FileRW.writeToFile(arg[1],"SUCCESS: Time has been set to "+getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"))+"!",true,true);
                break;
            }
        }
    }

    /**
     * Performs a no-operation (nop) command on the list of smart home devices.
     * Sets the date and time of the first device in the list as the current date and time.
     * Filters the list to find devices whose switch time matches the current date and time.
     * Switches the status of the filtered devices from "On" to "Off" or vice versa, and resets their switch time.
     * The devices that will be switched at the time when the nop command brings the program are found
     * Then the devices are switched by maintaining the order relative to each other.
     *
     * @param listOfAllDevices The list of all smart home devices.
     * @throws Errors.ErroneousCommandException If there is an error while executing the nop command.
     */
    public static void nop(List<SmartHomeDevices> listOfAllDevices) throws Errors.ErroneousCommandException {
            Time.setDateTime(listOfAllDevices.get(0).getItsTimeToSwitchItsStatus()); // Set the current date and time
            // Filter devices with matching switch time
            List<SmartHomeDevices> filteredDevicesThatItsSwitchTimeIsNow = listOfAllDevices.stream()
                    .filter(obje -> obje.getItsTimeToSwitchItsStatus() !=null && obje.getItsTimeToSwitchItsStatus().
                            equals(listOfAllDevices.get(0).getItsTimeToSwitchItsStatus()))
                    .collect(Collectors.toList());
            // Devices that need to be switched are switched and their setItsTimeToSwitchItsStatus are turned "null"
            for (SmartHomeDevices device : filteredDevicesThatItsSwitchTimeIsNow){
                switch (device.getStatus()){
                    case "On":
                        device.setStatus("Off");
                        device.setItsTimeToSwitchItsStatus(null);
                        break;
                    case "Off":
                        device.setStatus("On");
                        device.setItsTimeToSwitchItsStatus(null);
                        break;}
         }
    }
}
