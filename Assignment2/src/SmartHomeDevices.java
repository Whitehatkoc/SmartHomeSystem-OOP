import java.time.LocalDateTime;

/**
 * This class is the main class, a total of 4 classes derive from it directly or indirectly.
 * It includes the elaborations and methods common to the classes derived from it.
 */
public abstract class  SmartHomeDevices {
    /**
     * Attributes of Smart Camera class
     */
    private String name;
    private String status = "Off";
    private LocalDateTime itsTimeToSwitchItsStatus;

    //Getters and Setters

    /**
     * Sets the name.
     *
     * @param name The name to be set as a String.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name.
     *
     * @return The name as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the status of the device and resets the property "ItsTimeToSwitchItsStatus" of the corresponding device.
     *
     * @param Status The status to be set as a String.
     */
    public void setStatus(String Status) {
        this.status = Status;
        // the property ItsTimeToSwitchItsStatus of the device whose status is switched is also reset
        setItsTimeToSwitchItsStatus(null);
    }

    /**
     * Retrieves the status of the device.
     *
     * @return The status of the device as a String.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the time when it's time to switch the status of the device.
     *
     * @param time The LocalDateTime object representing the time when it's time to switch the status.
     */
    public void setItsTimeToSwitchItsStatus(LocalDateTime time) {
        itsTimeToSwitchItsStatus = time;

    }

    /**
     * Retrieves the time when it's time to switch the status of the device.
     *
     * @return The LocalDateTime object representing the time when it's time to switch the status.
     */
    public LocalDateTime getItsTimeToSwitchItsStatus() {
        return itsTimeToSwitchItsStatus;

    }

    public SmartHomeDevices(String name){
     this.name=name;
    }

    /**
     * This method is used to switch the status of a device from off to on, or vice versa.
     *
     * @param statu the status requested to be changed
     * @throws Errors.AlreadySwitchedOffOrOn If the device is already switched on or off.
     */
    public void switchStatusOffOn(String statu) throws Errors.AlreadySwitchedOffOrOn {
        if (status.equals(statu)){
            switch (status){ // The current status of the device is status
                case "On":
                    throw new Errors.AlreadySwitchedOffOrOn("ERROR: This device is already switched on!");
                case "Off":
                    throw new Errors.AlreadySwitchedOffOrOn("ERROR: This device is already switched off!");
            }
        }
        else{ // if it is a valid command, the status is set
            setStatus(statu);
        }
    }
    /**
     * This method is used to change the name of an object.
     *
     * @param name2 The new name to be set for the object.
     */
    public void changeName(String name2){
        setName(name2);
    }

    /**
     * This method is used to control if the status in the command is in the correct format
     * if it is valid, it does not throw an error if it is not valid, it throws an error
     *
     * @param lineSplitted An array containing the parts of the command that are split by \t.
     * @throws Errors.ErroneousCommandException If the command contains an erroneous status.
     */
    public void controlIfStatusIsRightForm(String[] lineSplitted) throws Errors.ErroneousCommandException {
        if (!lineSplitted[1].equals("SmartCamera") && !(lineSplitted[0].equals("Switch"))) {
            if (!lineSplitted[3].equals("Off") && ! lineSplitted[3].equals("On")) {
                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
            }
        } else if (lineSplitted[0].equals("Switch")){
            if (!lineSplitted[2].equals("Off") &&  ! lineSplitted[2].equals("On")) {
                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
            }
        }
        else {
            if ((lineSplitted[4].equals("Off") &&  lineSplitted[3].equals("On"))) {
                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
            }
        }
    }

    /**
     * This abstract method is used to add a device based on the input command.
     *
     * @param lineSplitted An array containing the parts of the command that are split by \t
     * @throws Exception If an error occurs while adding the device.
     */
    abstract void addDevice(String[] lineSplitted) throws Exception;

    /**
     * This abstract method is used to retrieve information about the device.
     *
     * @return A string containing information about the device.
     */
    abstract String info();


}
