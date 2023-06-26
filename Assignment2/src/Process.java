import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Process {
    // It is a data store that holds objects of the SmartHomeDevice class type
    // this arraylist will be mentioned as the main repository in the comment lines.
    public static ArrayList<SmartHomeDevices> listOfAllDevices = new ArrayList<>();
    // allows ZReport to be considered the last command in cases where the last command is not ZReport
    public static String lastCommand = "SetInitialTime"; //this variable will change every time,
                                                        // and the first version will be initial time

    /**
     * Finds a SmartHomeDevice object from the list of all devices based on the provided
     * command line input that is split into an array of strings.

     * @param lineSplitted The command line input split into an array of strings.
     * @return The found smart home device object, or null if not found.
     */
    public SmartHomeDevices findObject(String[] lineSplitted){
        SmartHomeDevices foundObject = listOfAllDevices.stream()
                .filter(object -> object.getName().equals(lineSplitted[1])).findFirst()
                .orElse(null);
        return foundObject;
    }

    /**
     * sorts the main list
     * sorts objects according to their itsTimeToSwitchItsStatus values
     */
    public void sortTheListOfAllDevices(){
        listOfAllDevices.sort(Comparator.nullsLast(Comparator.nullsLast(Comparator.comparing(
                SmartHomeDevices::getItsTimeToSwitchItsStatus,Comparator.nullsLast(Comparator.naturalOrder())))));
    }
    /**
     * Makes the input file suitable for the code,
     * detects the types of commands in the file,
     * calls the corresponding method for processing commands,
     * catches any exception if it is thrown.
     *</p>
     * This method is our main method of operation. In other words,
     * it contains the main processing structure of the program.
     * Works like a courier delivery company: commands entered by the user are delivered
     * to the addresses they need to go to. There are many cases inside the method, these cases serve as couriers.
     *
     * @param inputFile an array of strings containing the command line, it is the input file
     * @param arg the file to which file writing operations will be applied
     */
    public void process(String[] inputFile, String arg){
        //the setInitialTime command; that was processed in the setInitialTime( ) method of the time class,
        // and no longer needs to be processed, is removed from the inputfile
        List<String> list = new ArrayList<>(Arrays.asList(inputFile));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains("SetInitialTime")) {
                list.remove(i);
                break;}
        }
        inputFile = list.toArray(new String[0]);
        //the input file is processed with the for loop
        for(String line : inputFile){
            if(!line.trim().isEmpty()){ // empty lines are not processed
                String[] lineSplitted = line.split("\t");  // the lines of the inputfile are made suitable for processing one by one
                if (!Main.lastCommand){ // the following line of code must not work if the last command is not ZReport
                                        //  the variable Main.lastCommand allows this situation to be controlled
                FileRW.writeToFile(arg,"COMMAND: "+line,true,true);}
                switch (lineSplitted[0]) // the first element of the split input line is the identity,
                                        // commands are held by the corresponding case blocks according to these identities
                {
                    case "Add":  //commands whose ID is "A  dd" are processed in this case
                        try { // the entered command is checked whether it is valid
                            if (! lineSplitted[1].equals("SmartCamera") ){
                                if( lineSplitted.length < 3 ){
                                    throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}}
                            else if(lineSplitted.length < 4)
                                {throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                            controlForNotExistSameNameDevice(lineSplitted);
                            detectAndAddTheDevice(lineSplitted,arg);  // the method that finds which device to add is called
                            }
                        catch (Errors.DeviceAlreadyExist e){
                            FileRW.writeToFile(arg,"ERROR: There is already" +
                                    " a smart device with same name!",true,true);
                        } // if the names are the same, throw error will be catched
                        catch (ArrayIndexOutOfBoundsException | Errors.ErroneousCommandException exception){
                            FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);}
                        break;

                    case "Switch":  // Commands whose ID is "Switch" are processed in this case
                        try { // the entered command is checked whether it is valid

                            // the corresponding object is found in the list where all devices are stored
                            // this method is used when searching for objects inside the main repository
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null){
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                            }
                            foundObject.controlIfStatusIsRightForm(lineSplitted);
                            // the function that switches the status of the device is called
                            foundObject.switchStatusOffOn(lineSplitted[2]);
                        }
                        catch (Errors.AlreadySwitchedOffOrOn | Errors.ThereIsNoSuchDeviceException e){
                                FileRW.writeToFile(arg, e.getMessage(), true,true);
                        } catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);
                        }
                        break;

                    case "ChangeName":  // Commands whose ID is "ChangeName" are processed in this case
                        try{ // the entered command is checked whether it is valid
                            if(lineSplitted[1].equals(lineSplitted[2])){
                                throw new Errors.SameNameException("ERROR: Both of the names are the same, nothing changed!");
                            }else{
                                SmartHomeDevices foundObject = findObject(lineSplitted);
                                if(foundObject == null){
                                    throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                                }
                                // it is checked whether the new name is in the main repository
                                SmartHomeDevices foundObject2 = listOfAllDevices.stream()
                                        .filter(object -> object.getName().equals(lineSplitted[2])).findFirst()
                                        .orElse(null);
                                if (foundObject2 != null){
                                    throw new Errors.DeviceAlreadyExist("ERROR: There is already" +
                                            " a smart device with same name!");
                                }
                                // the corresponding method is called from the SmartHomeDevice class
                                foundObject.changeName(lineSplitted[2]);
                            }
                        }catch (Errors.SameNameException | Errors.ThereIsNoSuchDeviceException |
                                Errors.DeviceAlreadyExist e){
                            FileRW.writeToFile(arg,e.getMessage(),true,true);
                        } catch (ArrayIndexOutOfBoundsException e){
                            FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);
                        }
                        break;

                    case "PlugIn":  // Commands whose ID is "PlugIn" are processed in this case
                        try{        // the entered command is checked whether it is valid
                            if(lineSplitted.length!=3){
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                            }
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if ( foundObject == null ){
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                            }
                            // this case only concerns objects belonging to the SmartPlug class
                            if(!(foundObject instanceof SmartPlug)){
                                throw new Errors.IsNotAPlugException("ERROR: This device is not a smart plug!");
                            }else {
                                ((SmartPlug) foundObject).plugIn(Double.parseDouble(lineSplitted[2]));
                            }

                        }catch (ArrayIndexOutOfBoundsException e){
                            FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);
                        } catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }
                        break;
                    case "PlugOut":  // Commands whose ID is "PlugOut" are processed in this case
                        try {  // the entered command is checked whether it is valid
                            if (lineSplitted.length != 2) {
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                            }
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                            }
                            // this case only concerns objects belonging to the SmartPlug class
                            if (!(foundObject instanceof SmartPlug)) {
                                throw new Errors.IsNotAPlugException("ERROR: This device is not a smart plug!");
                            } else {
                                ((SmartPlug) foundObject).plugOut();
                            }

                        }catch (ArrayIndexOutOfBoundsException e){
                            FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);
                        } catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        } catch (NullPointerException e){
                            FileRW.writeToFile(arg,"ERROR: This plug has no item" +
                                    " to plug out from that plug!",true,true);
                        }
                        break;

                    case "SetKelvin": // Commands whose ID is "SetKelvin" are processed in this case
                        try {  // the entered command is checked whether it is valid
                            if(lineSplitted.length != 3){  // the length of the SetKelvin method must be 3
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                            }
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                            }if (foundObject.getClass() == SmartLamp.class) {
                                Integer kelvin = Integer.parseInt(lineSplitted[2]);
                                if (kelvin>=2000 && kelvin<=6500){
                                ((SmartLamp) foundObject).setKelvin(kelvin);
                                }else { throw new Errors.OutOfRange("ERROR: Kelvin value must be in range of 2000K-6500K!");}
                            } else if (foundObject.getClass()== SmartColorLamp.class) {
                                Integer kelvin = Integer.parseInt(lineSplitted[2]);
                                if (kelvin>=2000 && kelvin<=6500){
                                    ((SmartColorLamp) foundObject).setColorCode(kelvin);
                                    ((SmartColorLamp) foundObject).setColorCodeBoolean(false);}
                            } else {
                                throw new Errors.IsNotAPlugException("ERROR: This device is not a smart lamp!");
                            }
                        }catch(NumberFormatException e ) {
                            FileRW.writeToFile(arg, "ERROR: Kelvin value must" +
                                    " be a positive number!", true,true);
                        }catch (Errors.ErroneousCommandException e) {
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }
                        break;
                    case "SetBrightness":  // Commands whose ID is "SetBrightness" are processed in this case
                        try { // the entered command is checked whether it is valid
                            if(lineSplitted.length != 3){  // the length of the SetKelvin method must be  3
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                            }
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                            }if (foundObject.getClass() == SmartLamp.class || foundObject.getClass()==SmartColorLamp.class) {
                                Integer brightness = Integer.parseInt(lineSplitted[2]);
                                if (brightness >= 0 && brightness <= 200){
                                    ((SmartLamp) foundObject).setBrightnessValue(brightness);
                                }else { throw new Errors.OutOfRange("ERROR: Brightness must be in range of 0%-100%!");}
                            }else {
                                throw new Errors.IsNotAPlugException("ERROR: This device is not a smart lamp!");
                            }} catch (Errors.ThereIsNoSuchDeviceException e) {
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        } catch (Errors.ErroneousCommandException e) {
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }
                        break;

                    case "SetColorCode":  // Commands whose ID is "SetColorCode" are processed in this case
                        try{  // the entered command is checked whether it is valid
                            if (lineSplitted.length != 3){ // the length of the SetKelvin method must be 3
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                                //this case only concerns objects belonging to the SmartColorLamp class
                            }if (foundObject.getClass() == SmartColorLamp.class) {
                                if (lineSplitted[2].contains("0x")) {
                                    Integer hexadecimalSayi = Integer.decode(lineSplitted[2]);
                                    if (hexadecimalSayi <= Integer.decode("0xFFFFFF") && hexadecimalSayi >= 0) {
                                        ((SmartColorLamp) foundObject).setColorCode(hexadecimalSayi);
                                        ((SmartColorLamp) foundObject).setColorCodeBoolean(true);
                                    }else {
                                        throw new Errors.OutOfRange("ERROR: Color code value must be in range of 0x0-0xFFFFFF!");}
                            }else {throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                                }else {throw new Errors.ErroneousCommandException("ERROR: This device is not a smart color lamp!");}
                        } catch (NumberFormatException e){
                            FileRW.writeToFile(arg, "ERROR: Erroneous command!", true,true);
                        }catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }
                        break;

                    case "SetWhite": // Commands whose ID is "SetWhite" are processed in this case
                        try { // the entered command is checked whether it is valid

                            if(lineSplitted.length != 4){ // the length of the SetKelvin method must be 4
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                            }
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                                //this case only concerns objects belonging to the SmartColorLamp or SmartLamp class
                            }if (foundObject.getClass() == SmartLamp.class || foundObject.getClass() == SmartColorLamp.class) {
                                Integer kelvin = Integer.parseInt(lineSplitted[2]);
                                Integer brightness = Integer.parseInt(lineSplitted[3]);

                                if (kelvin>=2000 && kelvin<=6500){
                                    ((SmartLamp) foundObject).setKelvin(kelvin);
                                }else { throw new Errors.OutOfRange("ERROR: Kelvin value must be in range of 2000K-6500K!");}

                                if (brightness >= 0 && brightness <= 100){
                                     ((SmartLamp) foundObject).setBrightnessValue(brightness);
                                }else { throw new Errors.OutOfRange("ERROR: Brightness must be in range of 0%-100%!");}
                            } else{
                               throw new Errors.ThereIsNoSuchDeviceException("ERROR: This device is not a smart lamp!");
                            }
                        }catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }catch (NumberFormatException e){
                            FileRW.writeToFile(arg, "ERROR: Erroneous command!", true,true);
                        }
                        break;

                    case "SetColor": // Commands whose ID is "SetColor" are processed in this case
                        try{ // the entered command is checked whether it is valid
                            if (lineSplitted.length != 4){ // the length of the SetKelvin method must be at least 4
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                                //this case only concerns objects belonging to the SmartColorLamp class
                            }if (foundObject.getClass() == SmartColorLamp.class) {
                                // it is checked whether it is a hexadecimal number,
                                // and the hexadecimal number is converted to an integer
                                if (lineSplitted[2].contains("0x")) {
                                    Integer hexadecimalSayi = Integer.decode(lineSplitted[2]);
                                    Integer brightness = Integer.parseInt(lineSplitted[3]);
                                    if (hexadecimalSayi <= Integer.decode("0xFFFFFF") && hexadecimalSayi >= 0) {
                                        if (brightness >= 0 && brightness <= 100){
                                            ((SmartLamp) foundObject).setBrightnessValue(brightness);
                                            ((SmartColorLamp) foundObject).setColorCode(hexadecimalSayi);
                                            ((SmartColorLamp) foundObject).setColorCodeBoolean(true);

                                        }else {
                                            throw new Errors.OutOfRange("ERROR: Brightness must be in range of 0%-100%!");}
                                    }else {
                                        throw new Errors.OutOfRange("ERROR: Color code value must be in range of 0x0-0xFFFFFF!");}
                                }else {
                                    throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                            }else {
                                throw new Errors.ErroneousCommandException("ERROR: This device is not a smart color lamp!");
                            }
                        }catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }catch (NumberFormatException e){
                            FileRW.writeToFile(arg, "ERROR: Erroneous command!", true,true);
                        }
                        break;

                    /**
                     * deletes the requested device from the main storage if possible
                     * if the entered command is not valid and the device requested to be uninstalled
                     * is not already in the main repository, it throws an error
                     */
                    case "Remove":  // Commands whose ID is "Remove" are processed in this case
                        try {  // the entered command is checked whether it is valid
                            if(lineSplitted.length!=2){ // the length of the SetKelvin method must be 2
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");
                            }
                            // devices are turned off before they are deleted
                            if(foundObject.getStatus().equals("On")){
                                foundObject.setStatus("Off");
                            }
                            listOfAllDevices.remove(foundObject);
                            FileRW.writeToFile(arg, "SUCCESS: Information about removed " +
                                    "smart device is as follows:", true,true);
                            FileRW.writeToFile(arg,foundObject.info(),true,true);

                        } catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }
                        break;

                    case "SetSwitchTime":  // Commands whose ID is "SetSwitchTime" are processed in this case
                        try {  // the entered command is checked whether it is valid
                            if(lineSplitted.length != 3){
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                            }
                            SmartHomeDevices foundObject = findObject(lineSplitted);
                            if (foundObject == null) {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is not such a device!");}

                            String[] dateAndTime= lineSplitted[2].split("_");
                            String[] date = dateAndTime[0].split("-");
                            String[] time = dateAndTime[1].split(":");
                            LocalDateTime ItsTimeToSwitchItsStatus = LocalDateTime.of(
                                    Integer.parseInt(date[0]),Integer.parseInt(date[1]),
                                    Integer.parseInt(date[2]),Integer.parseInt(time[0]),
                                    Integer.parseInt(time[1]),Integer.parseInt(time[2]));

                            if (ItsTimeToSwitchItsStatus.isBefore(Time.dateTime)){
                                throw new Errors.IsNotAPositiveNumberException("ERROR: Switch time cannot be in the past!");}
                            //if the desired time to set is the same as the current time,
                            if (ItsTimeToSwitchItsStatus.isEqual(Time.dateTime)){
                                //its ItsTimeToSwitchItsStatus is set first,
                                foundObject.setItsTimeToSwitchItsStatus(ItsTimeToSwitchItsStatus);
                                sortTheListOfAllDevices(); // then the main store is sorted,
                                // then the status is switched
                                switch (foundObject.getStatus()){
                                    case "Off":
                                        foundObject.switchStatusOffOn("On");
                                        break;
                                    case "On":
                                        foundObject.switchStatusOffOn("Off");
                                        break;
                                }
                            }
                            if (ItsTimeToSwitchItsStatus.isAfter(Time.dateTime)) {
                                foundObject.setItsTimeToSwitchItsStatus(ItsTimeToSwitchItsStatus);}

                        } catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }
                        break;

                    /**
                     * with the nop command, switches status of the devices that need to be switched, if there are
                     * sets the current time of the program to the entered time
                     */
                    case "SetTime":  // Commands whose ID is "SetTime" are processed in this case
                        try{  // the entered command is checked whether it is valid
                            if (lineSplitted.length!=2){
                                // control if the new date entered valid
                                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}

                            String[] dateAndTime= lineSplitted[1].split("_");
                            String[] date = dateAndTime[0].split("-");
                            String[] time = dateAndTime[1].split(":");
                            LocalDateTime newDateTime = LocalDateTime.of(Integer.parseInt(date[0]),Integer.parseInt(date[1]),
                                    Integer.parseInt(date[2]),Integer.parseInt(time[0]),Integer.parseInt(time[1])
                                    ,Integer.parseInt(time[2]));

                            if (newDateTime.isEqual(Time.dateTime)){
                                throw new Errors.ErroneousCommandException("ERROR: There is nothing to change!");
                            }
                            if (newDateTime.isAfter(Time.dateTime)){
                                // is there a device whose switch time is before the desired time to set
                            for (int i= 0; i<200 ;i++){
                                List<SmartHomeDevices> DeviceList= new ArrayList<SmartHomeDevices>
                                        (controlAnySwitchBetweenTimes(newDateTime));
                            // the nop command switches if there are devices that need to be switched
                                if (!DeviceList.isEmpty()){Time.nop(DeviceList);
                                    sortTheListOfAllDevices(); // the list is sorted again every time
                            }else{Time.setDateTime(newDateTime);
                                    break;}
                            }
                            }else { throw new Errors.IsNotAPositiveNumberException("ERROR: Time cannot be reversed!");}

                        } catch (Errors.ErroneousCommandException e) {
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        }catch (Exception e){
                            FileRW.writeToFile(arg, "ERROR: Time format is not correct!", true, true);
                        }
                        break;
                    /**
                     * pulls the current time of the program forward by the amount of minutes entered
                     * sets the current time of the program to the entered time
                     */
                    case "SkipMinutes": // Commands whose ID is "SkipMinutes" are processed in this case
                        try{   // the entered command is checked whether it is valid
                        if(lineSplitted.length != 2 ){
                            throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
                        }else {
                            int minutes = Integer.parseInt(lineSplitted[1]);
                            if(minutes<=0){
                                if (minutes == 0){
                                    throw new Errors.IsNotAPositiveNumberException("ERROR: There is nothing to skip!");
                                }else {throw new Errors.IsNotAPositiveNumberException("ERROR: Time cannot be reversed!");}
                            }else {
                                LocalDateTime newDateTime = Time.dateTime.plusMinutes(minutes);
                                LocalDateTime oldTime= Time.dateTime;
                                // is there a device whose switch time is before the desired time to set
                                for(int i =0; i<200;i++){
                                    List<SmartHomeDevices> DeviceList = new ArrayList<SmartHomeDevices>
                                            (controlAnySwitchBetweenTimes(newDateTime));
                                    Time.setDateTime(newDateTime);
                                    // this block works if there are devices whose status needs to be changed between two times
                                    if (!DeviceList.isEmpty()) {
                                        LocalDateTime firstElement = listOfAllDevices.get(0).getItsTimeToSwitchItsStatus();
                                        if (firstElement !=null && (firstElement.isBefore(newDateTime)
                                                ||firstElement.isEqual(newDateTime)))
                                        {
                                            Time.nop(listOfAllDevices);
                                        }
                                    }else {Time.setDateTime(newDateTime);
                                        break;}
                                Time.dateTime=oldTime;}
                            }
                        }} catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        } catch (NumberFormatException e){
                            FileRW.writeToFile(arg, "ERROR: Erroneous command!", true,true);
                        }
                        break;

                    case "Nop":
                        try {
                            // if the switch time of the first element in the sorted main store is not null,
                            // because the first element of the sorted list is the earliest set time
                            if (listOfAllDevices.get(0).getItsTimeToSwitchItsStatus() != null) {
                                Time.nop(listOfAllDevices); // the nop command is applied
                                sortTheListOfAllDevices();  // the new list is sorted again

                            }else {
                                throw new Errors.ThereIsNoSuchDeviceException("ERROR: There is nothing to switch!");}
                        } catch (Errors.ErroneousCommandException e){
                            FileRW.writeToFile(arg, e.getMessage(), true,true);
                        } catch (IndexOutOfBoundsException e){
                            FileRW.writeToFile(arg, "ERROR: There is nothing to switch!", true,true);
                        }
                        break;
                    /**
                     * prints the information of all devices ( objects) in the main storage in sequence
                     */
                    case "ZReport":
                        FileRW.writeToFile(arg, "Time is:\t"+Time.dateTime.
                                format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")), true,true);
                        for (SmartHomeDevices device : listOfAllDevices){
                            FileRW.writeToFile(arg, device.info(), true,true);
                        }
                         break;

                    default: FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);

                }
                // the case blocks end here and the process method starts its final operations
                // after each command line is processed, the last main repository is sorted
                sortTheListOfAllDevices();
                // the command is noted here every time, to find out what the last command is in the Main class.
                lastCommand = lineSplitted[0];
            }
        }
    }

    /**
     * determines the type of device that is requested to be added to the program's repository
     * calls the method of adding the corresponding device type
     * it is called from the add case of the process method
     *
     * @param lineSplitted an array of strings containing the command line from input file
     * @param arg the file to which file writing operations will be applied
     */
    public void detectAndAddTheDevice(String[] lineSplitted,String arg) {
        // the second element of the command line gives the desired device type to be added
        switch (lineSplitted[1]) {
            case "SmartPlug":
                try {
                    SmartPlug plug = new SmartPlug(lineSplitted[2]);
                    plug.addDevice(lineSplitted);
                    // if the add method succeeds in adding, the device is added to the main repository
                    listOfAllDevices.add(plug);
                    plug.ListOfPlugDevice.add(plug); // it is also added to the relevant sublist just in case
                } catch (Errors.ErroneousCommandException e) {
                    FileRW.writeToFile(arg, e.getMessage(),  true, true);
                } catch (NumberFormatException e) {
                    FileRW.writeToFile(arg, "ERROR: Erroneous command!", true, true);
                }
                break;


            case "SmartCamera":
                try {if (lineSplitted.length < 4){throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");}
                    if (Double.parseDouble(lineSplitted[3]) <= 0) {
                        throw new Errors.IsNotAPositiveNumberException("ERROR: Megabyte value must be a positive number!");
                    }
                    SmartCamera camera = new SmartCamera(lineSplitted[2], Double.parseDouble(lineSplitted[3]));
                    // if the add method succeeds in adding, the device is added to the main repository
                    camera.addDevice(lineSplitted);
                    camera.ListOfCameraDevice.add(camera);
                    listOfAllDevices.add(camera); // it is also added to the relevant sublist just in case
                } catch (Errors.IsNotAPositiveNumberException e) {
                    FileRW.writeToFile(arg, e.getMessage(), true, true);
                } catch (NumberFormatException | Errors.ErroneousCommandException e) {
                    FileRW.writeToFile(arg, "ERROR: Erroneous command!",
                            true, true);
                }
                break;


            case "SmartLamp":
                try{
                    SmartLamp lamp = new SmartLamp(lineSplitted[2]);
                    lamp.addDevice(lineSplitted);
                    // if the add method succeeds in adding, the device is added to the main repository
                    listOfAllDevices.add(lamp);
                    lamp.ListOfLampDevice.add(lamp); // it is also added to the relevant sublist just in case
                } catch (Errors.OutOfRange e ) {
                    FileRW.writeToFile(arg, e.getMessage(), true, true);
                } catch(NumberFormatException | Errors.ErroneousCommandException e){
                    FileRW.writeToFile(arg, "ERROR: Erroneous command!", true, true);
                }
                break;


            case "SmartColorLamp":
                try{
                    SmartColorLamp colorLamp = new SmartColorLamp(lineSplitted[2]);
                    colorLamp.addDevice(lineSplitted);
                    // if the add method succeeds in adding, the device is added to the main repository
                    listOfAllDevices.add(colorLamp);
                    colorLamp.listOfColorLamp.add(colorLamp); // it is also added to the relevant sublist just in case
                }catch( NumberFormatException e){
                    FileRW.writeToFile(arg, "ERROR: Erroneous command!", true, true);
                } catch (Errors.ErroneousCommandException e ) {
                    FileRW.writeToFile(arg, e.getMessage(), true, true);
                }
                break;

            default: FileRW.writeToFile(arg,"ERROR: Erroneous command!",true,true);
        }
    }

    /**
     * Control if a smart device that has the same name already exists
     * Throws a {@link Errors.DeviceAlreadyExist} exception if a device that has same name is found.
     *
     * @param lineSplitted an array of strings containing the command line from input file
     * @throws Errors.DeviceAlreadyExist if a smart device that has the same name already exists
     */
    public void controlForNotExistSameNameDevice(String[] lineSplitted) throws Errors.DeviceAlreadyExist
        {
            if ( listOfAllDevices.stream().anyMatch(device -> device.getName().matches(lineSplitted[2])))
            {   throw new Errors.DeviceAlreadyExist("ERROR: There is already a smart device with same name!"); }

        }

    /**
     * Filters the list of all smart home devices to find devices that have a switch time between the given time range.
     * The switch time of each device is checked against the provided newDateTime.
     *
     * @param newDateTime the LocalDateTime representing the upper bound of the time range to filter against
     * @return a list of SmartHomeDevices that have a switch time between the given time range
     */
    public List<SmartHomeDevices> controlAnySwitchBetweenTimes(LocalDateTime newDateTime) {
        List<SmartHomeDevices> filteredDevicesHaveSwitchTimeBetweenTimes = listOfAllDevices.stream()
                .filter(obje -> obje.getItsTimeToSwitchItsStatus() != null &&( obje.getItsTimeToSwitchItsStatus().isAfter(Time.dateTime)
                        && obje.getItsTimeToSwitchItsStatus().isBefore(newDateTime) || obje.getItsTimeToSwitchItsStatus().isEqual(newDateTime)))
                .collect(Collectors.toList());
        return filteredDevicesHaveSwitchTimeBetweenTimes;
    }


}
