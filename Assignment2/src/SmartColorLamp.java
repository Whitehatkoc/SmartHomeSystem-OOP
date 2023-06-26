import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class SmartColorLamp extends SmartLamp { // indirectly, he is also a child of the SmartHomeDevice class
    /**
     * Attributes of Smart Camera class
     */
    private Integer colorCode = 4000;  // default colorCode value

    // Indicates whether the colorCode value contains a color value (true) or a kelvin value (false)
    public Boolean colorCodeBoolean = false; // the default valou of colorCode is in form of kelvin
    public ArrayList<SmartColorLamp> listOfColorLamp = new ArrayList<>();


    // Getters and Setters

    /**
     * Sets the color code and sets the corresponding boolean flag to true.
     *
     * @param colorCode The color code to be set as an Integer.
     */
    public void setColorCode(Integer colorCode){
        this.colorCode = colorCode;
        colorCodeBoolean = true;} // when setting the ColorCode value, the colorCodeBoolean value is set to true

    /**
     * Retrieves the color code.
     *
     * @return The color code as an Integer.
     */
    public Integer getColorCode() {
        return colorCode;
    }

    /**
     * Sets the boolean flag for the color code.
     *
     * @param colorCodeBoolean The boolean flag for the color code to be set.
     */
    public  void setColorCodeBoolean(Boolean colorCodeBoolean) {
        this.colorCodeBoolean = colorCodeBoolean;}

    @Override
    public void setKelvin(Integer kelvin) {
        this.colorCode = kelvin; // The ColorCode property is set, not the kelvin property
        colorCodeBoolean = false; // when setting the ColorCode value, the colorCodeBoolean value is set to false
    }

    // constructor of the class
    public SmartColorLamp(String name){
        super(name);
    }

    /**
     * Adds a new device to the SmartColorLamp. Parses the given parameters from the command line
     * and sets the corresponding properties of the device.
     *
     * @param lineSplitted an array of strings containing the command line from input file
     * @throws Errors.ErroneousCommandException If the command is erroneous, throws an exception
     * @throws NumberFormatException If there is an error in number format, throws an exception
     */
    @Override
    void addDevice(String[] lineSplitted) throws Errors.ErroneousCommandException, NumberFormatException {
    switch (lineSplitted.length) { // the requirements are different according to the length of the entered command
        case 4:
            controlIfStatusIsRightForm(lineSplitted);
            setStatus(lineSplitted[3]);
            break;
        case 5:
            throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
        case 6:  // In addition the colorCode and brightness value will be set
            controlIfStatusIsRightForm(lineSplitted);
            setStatus(lineSplitted[3]);
            int brightness = Integer.parseInt(lineSplitted[5]);
            if (lineSplitted[4].contains("0x")) { // checking whether the entered ColorCode value is hexadecimal type
                Integer hexadecimalSayi = Integer.decode(lineSplitted[4]);
                Integer max = Integer.decode("0xFFFFFF");
                // checking whether the entered values are in the valid interval
                if (hexadecimalSayi <= max && hexadecimalSayi >= 0) {
                    if (brightness <= 100 && brightness >= 0) {
                        setBrightnessValue(brightness);
                        setColorCode(hexadecimalSayi);
                        colorCodeBoolean = true;
                    } else {
                        throw new Errors.OutOfRange("ERROR: Brightness must be in range of 0%-100%!");
                    }
                } else {
                    throw new Errors.OutOfRange("ERROR: Color code value must be in range of 0x0-0xFFFFFF!");
                }
            } else {
                if (Integer.parseInt(lineSplitted[4]) <= 6500 && Integer.parseInt(lineSplitted[4]) <= 2000) {
                    if (brightness <= 100 && brightness >= 0) {
                        setBrightnessValue(brightness);
                        setColorCode(Integer.parseInt(lineSplitted[4]));
                        colorCodeBoolean = false;
                    } else {
                        throw new Errors.OutOfRange("ERROR: Brightness must be in range of 0%-100%!");
                    }
                } else {
                    throw new Errors.OutOfRange("ERROR: Kelvin value must be in range of 2000K-6500K!");
                }
            }
            break;
        default:    // the length of the entered command must be at least 3
            if(lineSplitted.length != 3){
                throw new Errors.ErroneousCommandException("ERROR: Erroneous command!");
            }
        }
    }

    /**
     * Returns a formatted string containing information about the SmartColorLamp object.
     * The returned string includes the name, status, color value, brightness value, and the time
     * to switch its status (if available) in a human-readable format.
     *
     * @return A formatted string with information about the SmartColorLamp object
     */
    @Override
    String info() {
        String returnn;
        if (!colorCodeBoolean){ // with the variable colorCodeBoolean, it is determined which type the colorcode value is
            // the variable dateTime is re-created according to the desired format
        String formattedDateTime = Optional.ofNullable(getItsTimeToSwitchItsStatus())
                .map(dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")))
                .orElse("null");
        // the "returnn" variable to be returned is created by adding string values
        returnn= "Smart Color Lamp "+ getName() +" is "+ getStatus().toLowerCase()+" and its color value is "+ getColorCode()+
            "K with "+ getBrightnessValue()+"% brightness, and its time to switch its status is "+ formattedDateTime+".";
    }else {
            String formattedDateTime = Optional.ofNullable(getItsTimeToSwitchItsStatus())
                    .map(dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")))
                    .orElse("null");
            // hexadecimal code is generated again in the desired format
            String hex = String.format("%6s", Integer.toHexString(getColorCode())).replace(' ', '0').toUpperCase();

            returnn= "Smart Color Lamp "+ getName() +" is "+ getStatus().toLowerCase()+" and its color value is 0x"+ hex+
                    " with "+ getBrightnessValue()+"% brightness, and its time to switch its status is "+ formattedDateTime+".";
        }

    return returnn;}

}
