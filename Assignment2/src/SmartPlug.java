import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class SmartPlug extends SmartHomeDevices{
    /**
     * Attributes of Smart Camera class
     */
    private final Integer voltageValue = 220 ;
    private double ampereValue ;  // the ampere value of the device plugged in
    private double totalEnergyConsuption = 0; // the total energy consumed by the company

    // lastSwitchTimeToOn and lastPlugInTime are used in the energyConsuption function
    private LocalDateTime lastSwitchTimeToOn; // it is updated when the device is set to "On"
    private LocalDateTime lastPlugInTime;  // it is updated when the device is plugged in
    public ArrayList<SmartPlug> ListOfPlugDevice = new ArrayList<>();

    // Getters and Setters as follows:
    public void setAmpereValue(double ampereValue) {
        this.ampereValue = ampereValue;
    }

    public double getAmpereValue() {
        return ampereValue;
    }

    public void setLastSwitchTimeToOn(LocalDateTime lastSwitchTime) {
        this.lastSwitchTimeToOn = lastSwitchTime;
    }

    public LocalDateTime getLastSwitchTimeToOn() {
        return lastSwitchTimeToOn;
    }

    public void setTotalEnergyConsuption(double totalEnergyConsuption) {
        this.totalEnergyConsuption = totalEnergyConsuption;
    }
    public double getTotalEnergyConsuption() {
        return totalEnergyConsuption;
    }


    public LocalDateTime getLastPlugInTime() {
        return lastPlugInTime;
    }

    public void setLastPlugInTime(LocalDateTime lastPlugInTime) {
        this.lastPlugInTime = lastPlugInTime;
    }

    //constructor
    public SmartPlug(String name){
        super(name);
    }


    /**
     * Adds a smart device with the given parameters and sets its status, ampere value, and last plug-in time.
     *
     * @param lineSplitted An array of strings representing the parameters for adding a smart device. The expected format depends on the length of the array.
     * @throws Errors.ErroneousCommandException If the command to add a smart device is not correct.
     * @throws Errors.IsNotAPositiveNumberException If the ampere value is not a positive number.
     */
    @Override
    public void addDevice(String[] lineSplitted) throws Errors.ErroneousCommandException {

        if(lineSplitted.length > 3){controlIfStatusIsRightForm(lineSplitted);
            switch (lineSplitted.length){
                case 4:  // set the status and update last switch time to On if applicable
                    setStatus(lineSplitted[3]);
                    if (lineSplitted[3].contains("On")){lastSwitchTimeToOn = Time.dateTime;}
                    break;
                case 5: // set the status, ampere value, and last plug-in time
                    setStatus(lineSplitted[3]);
                    if (lineSplitted[3].contains("On")){lastSwitchTimeToOn = Time.dateTime;}
                    setAmpereValue(Double.parseDouble(lineSplitted[4]));
                    if (getAmpereValue()<=0){
                        throw new Errors.IsNotAPositiveNumberException("ERROR: Ampere value must be a positive number!");
                    }else {setLastPlugInTime(Time.dateTime);}
                    break;
            }
        }

    }

    /**
     * Sets the status of the smart device and performs additional actions based on the status.
     *
     * @param statu The status of the smart device, either "On" or "Off".
     */
    @Override
    public void setStatus(String statu) {
        switch (statu){
            case "On": // If the status is "On", set the last switch time to On to the current time
                setLastSwitchTimeToOn(Time.dateTime);
                break;
            case "Off": // If the status is "Off", check if the ampere value is greater than 0, and if so, calculate energy consumption
                Optional.ofNullable(getAmpereValue())
                        .filter(value -> value > 0)
                        .ifPresent(value -> energyConsuption(Time.dateTime));
                break;
        }
        // Set the status of the smart device with super class's method
        super.setStatus(statu);
    }

    /**
     * Calculates the energy consumption of the smart device based on the time of switching.
     *
     * @param switchTime The LocalDateTime representing the time of switching.
     */
    public void energyConsuption(LocalDateTime switchTime){
        try {
            // Check if the last plug-in time is after the last switch time to On
            if (getLastPlugInTime().isAfter(getLastSwitchTimeToOn())) {
                Duration duration = Duration.between(getLastPlugInTime(), switchTime);
                double hours = duration.getSeconds() / 3600.0;
                setTotalEnergyConsuption( getTotalEnergyConsuption() + hours * voltageValue * getAmpereValue());

                // Check if the last plug-in time is before the last switch time to On
            } else if (getLastPlugInTime().isBefore(getLastSwitchTimeToOn())) {
                Duration duration = Duration.between(this.lastSwitchTimeToOn, switchTime);
                double hours = duration.getSeconds() / 3600.0;
                setTotalEnergyConsuption(getTotalEnergyConsuption() + hours * voltageValue * getAmpereValue());

                // Check if the last plug-in time is equal to the last switch time to On
            }else if (getLastPlugInTime().isEqual(this.lastSwitchTimeToOn)){

                Duration duration = Duration.between(this.lastSwitchTimeToOn, switchTime);
                double hours = duration.getSeconds() / 3600.0;
                setTotalEnergyConsuption(getTotalEnergyConsuption() + hours * voltageValue * getAmpereValue());
            }
        }catch (NullPointerException e){
            // Handle NullPointerException if getLastPlugInTime() or getLastSwitchTimeToOn() returns null
            //this catch block is empty, the reason is that this error is normal
            // and the block must ignore it if it catches the exception
        }
    }
    /**
     * Plugs in an item to the plug with the specified ampere value.
     *
     * @param ampere The ampere value of the item to be plugged in.
     * @throws Errors.AlreadyPlugInOrOutException If there is already an item plugged in.
     * @throws Errors.OutOfRange If the ampere value is not a positive number.
     */
    public void plugIn(double ampere) throws Errors.AlreadyPlugInOrOutException, Errors.OutOfRange {
        if(getAmpereValue()>0){
            // Check if there is already an item plugged in
            throw new Errors.AlreadyPlugInOrOutException("ERROR: There is already an item plugged in to that plug!");
        }else{
            if(ampere>0){ // Check if the ampere value is positive
                setAmpereValue(ampere);
                setLastPlugInTime(Time.dateTime);
            }else{
                throw new Errors.OutOfRange("ERROR: Ampere value must be a positive number!");
            }
        }
    }

    /**
     * After checking whether there is a device in the plug, if there is, it removes it, or it throws an error
     *
     * @throws Errors.AlreadyPlugInOrOutException
     */
    public void plugOut() throws Errors.AlreadyPlugInOrOutException {
        if(this.ampereValue == 0.0 ){
            throw new Errors.AlreadyPlugInOrOutException("ERROR: This plug has no item to plug out from that plug!");
        }else{
            setAmpereValue(0.0); // the value of the ampere is equalized to zero,
                                // indicating that there is no device plugged into the outlet
            if(getStatus().equals("On")){ // if plug is on and plugged-out:
                energyConsuption(Time.dateTime); // the totalEnergyConsuption is updated when the device is removed from the plug.
            }
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
        double roundedHours = Math.round(getTotalEnergyConsuption() * 100.0) / 100.0; //the number is rounded and formatted
        String formattedd = String.format("%.2f", roundedHours);                    //so that there are two digits after the comma
        String newString = formattedd.replace(".",",");
        return "Smart Plug "+ getName()+" is "+ getStatus().toLowerCase()+" and consumed "+ newString+
                "W so far (excluding current device), and its time to switch its status is "+ formattedDateTime+".";
    }
}
