import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * SmartCamera class represents a smart camera that extends the SmartHomeDevices class.
 * It inherits the properties and behaviors of the SmartHomeDevices class and can have additional
 * properties and behaviors specific to a smart camera.
 */
public class SmartCamera extends SmartHomeDevices {
    /**
     * Attributes of Smart Camera class
     */
    private final double megabytesConsumedPerMinute; // The amount of storage the camera device uses per minute (in megabytes)
    public ArrayList<SmartCamera> ListOfCameraDevice = new ArrayList<>(); // List of devices of the camera type
    private Double totalStorage = 0.0; // The total amount of memory used, updated by the storage( ) method
    private LocalDateTime lastSwitchTimeToOn; // the required time info to be able to measure the storage space

    /**
     * Constructs a new SmartCamera object with the given name and megabytes consumed per minute.
     *
     * @param name The name of the smart camera
     * @param megabytesConsumedPerMinute The amount of megabytes consumed per minute by the smart camera
     */
    public SmartCamera(String name, double megabytesConsumedPerMinute) {
        super(name);
        this.megabytesConsumedPerMinute = megabytesConsumedPerMinute;
    }

    /**
     * Overrides the setStatus method of the SmartHomeDevices class to set the status
     * of the smart camera and update the switch time and totalStorage accordingly.
     *
     * @param statu The status to set ("On" or "Off")
     */
    @Override
    public void setStatus(String statu) {
        super.setStatus(statu);
        switch (statu){
            case "On":
                setLastSwitchTimeToOn(Time.dateTime);
                break;
            case "Off":
                storage(Time.dateTime);
                break;

        }
    }

    // All other Getters and Setters
    /**
     * Retrieves the amount of megabytes consumed per minute.
     *
     * @return The amount of megabytes consumed per minute as a double value.
     */
    public double getMegabytesConsumedPerMinute() {
        return megabytesConsumedPerMinute;
    }

    /**
     * Sets the last switch time to ON.
     *
     * @param lastSwitchTimeToOn The LocalDateTime object representing the last switch time to ON.
     */
    public void setLastSwitchTimeToOn(LocalDateTime lastSwitchTimeToOn) {
        this.lastSwitchTimeToOn = lastSwitchTimeToOn;
    }

    /**
     * Retrieves the last switch time to ON.
     *
     * @return The LocalDateTime object representing the last switch time to ON.
     */
    public LocalDateTime getLastSwitchTimeToOn() {
        return lastSwitchTimeToOn;
    }

    /**
     * Sets the total storage value.
     *
     * @param totalStorage The total storage value to be set as a Double.
     */
    public void setTotalStorage(Double totalStorage) {
        this.totalStorage = totalStorage;
    }

    /**
     * Retrieves the total storage value.
     *
     * @return The total storage value as a Double.
     */
    public Double getTotalStorage() {
        return totalStorage;
    }

    /**
     *Calculates and updates the total storage of the smart camera.
     *
     *@param switchTime The LocalDateTime of the current switch time
     */
    public void storage(LocalDateTime switchTime){
        try {
            Duration duration = Duration.between(getLastSwitchTimeToOn(), switchTime);
            setTotalStorage(getTotalStorage() + duration.toMinutes() * getMegabytesConsumedPerMinute());
        }catch (NullPointerException e){}
    }

    /**
     * Adds a new device to the smart home
     * Overrides the addDevice method of the SmartHomeDevices class
     * Sets the status, and updates the last switch time to "On" if applicable
     *
     * @param lineSplitted an array of strings containing the command line from input file
     * @throws Errors.ErroneousCommandException If the command line input is in an erroneous format
     */
    @Override
    public void addDevice(String[] lineSplitted) throws Errors.ErroneousCommandException {
        if (lineSplitted.length == 5) {
            controlIfStatusIsRightForm(lineSplitted);
            setStatus(lineSplitted[4]);
            if (lineSplitted[3].contains("On")){lastSwitchTimeToOn = Time.dateTime;}
        }

    }
    /**
     * Overrides the info method of the SmartHomeDevices class to return a formatted string representation of the
     * smart camera's information, including its name, status, total storage usage, and time to switch its status.
     * it is used in the report and remove functions
     *
     * @return A string representation of the smart camera's information.
     */
    @Override
    String info() {
        // If the time to switch its status is null, "null" is displayed.
        String formattedDateTime = Optional.ofNullable(getItsTimeToSwitchItsStatus())
                .map(dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")))
                .orElse("null");
        double roundedHours = Math.round(getTotalStorage() * 100.0) / 100.0;  //The total storage usage is rounded to

        String formattedd = String.format("%.2f", roundedHours);            // with two decimal places and formatted
        String newString = formattedd.replace(".",",");   // comma as the decimal separator.
    return "Smart Camera "+ getName() +" is " + getStatus().toLowerCase()+" and used "+ newString+
            " MB of storage so far (excluding current status), and its time to switch its status is " + formattedDateTime+".";
    }
}
