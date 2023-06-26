import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * The SmartColorLamp class derives from this class
 */
public class SmartLamp extends SmartHomeDevices {
    /**
     * Attributes of Smart Camera class
     */
    private Integer kelvinValue = 4000;  // Default kelvin value is 4000K
    private Integer brightnessValue = 100; // Default brightness value is 100%
    public ArrayList<SmartLamp> ListOfLampDevice = new ArrayList<>();
    public void setKelvin(Integer kelvin){
        this.kelvinValue = kelvin;
    }
    public void  setBrightnessValue(Integer brightness){
        this.brightnessValue = brightness;
    }

    // constructor of SmartLamp class
    public SmartLamp(String name){
        super(name);
    }
    // Getters
    public Integer getKelvinValue() {
        return kelvinValue;
    }

    public Integer getBrightnessValue() {
        return brightnessValue;
    }

    /**
     * Adds a SmartLamp devices with different parameters to the smart home
     * Overrides the addDevice method of the SmartHomeDevices class
     *
     * @param lineSplitted The input array of strings containing the parameters for adding a SmartLamp device.
     * @throws Errors.ErroneousCommandException If the input array length is not valid.
     * @throws NumberFormatException           If the input parameters are not in valid numeric format.
     * @throws Errors.OutOfRange               If the kelvin value or brightness value is out of valid range.
     */
    @Override
    void addDevice(String[] lineSplitted) throws Errors.ErroneousCommandException, NumberFormatException {
        if (lineSplitted.length != 3){switch (lineSplitted.length){

            case 4:
                controlIfStatusIsRightForm(lineSplitted);
                setStatus(lineSplitted[3]);
                break;
            case 6:
                setStatus(lineSplitted[3]);
                if (2000<=Integer.parseInt(lineSplitted[4]) && Integer.parseInt(lineSplitted[4]) <=6500){
                    setKelvin(Integer.parseInt(lineSplitted[4]));
                }
                else {throw new Errors.OutOfRange("ERROR: Kelvin value must be in range of 2000K-6500K!");}

                if (Integer.parseInt(lineSplitted[5])<=100 && Integer.parseInt(lineSplitted[5])>=0){
                    setBrightnessValue(Integer.parseInt(lineSplitted[5]));
                }
                else {
                    throw new Errors.OutOfRange("ERROR: Brightness must be in range of 0%-100%!");
                }
                break;
            default:
                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");

        }}

    }
    /**
     * Returns a formatted string representation of the smart lamp's information,
     * including its name, status, kelvin value, brightness value, and time to switch its status
     * overrides the info method of the SmartHomeDevices class
     * it is used in the report and remove functions
     *
     * @return A string representation of the smart lamp's information.
     */
    @Override
    String info() {
        // If the time to switch its status is null, "null" is displayed
        String formattedDateTime = Optional.ofNullable(getItsTimeToSwitchItsStatus())
                .map(dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")))
                .orElse("null");
    return "Smart Lamp "+ getName()+ " is "+ getStatus().toLowerCase()+" and its kelvin value is "+getKelvinValue().toString().toLowerCase()+"K with "+
        getBrightnessValue()+"% brightness, and its time to switch its status is " + formattedDateTime+".";
    }
}
