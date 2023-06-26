import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class Main {
        // In order to "COMMAND:" string not to be printed to the file
        // not to be confused with the lastCommand that is attribute of the Process class
        public static Boolean lastCommand = false;
        /**
         * The entry point of the application.
         * Reads input from a file specified in the command line arguments, processes the input,
         * and writes the output to a file. If the last command processed is not "ZReport", it
         * automatically processes a "ZReport" command to generate a report. Handles exceptions
         * for erroneous commands and incorrect date format.
         *
         * @param args The command line arguments, where args[0] is the input file path and args[1]
         *             is the output file path
         */
        public static void main(String[] args) {
                String[] inputFile = FileRW.readFromFile(args[0]);
                assert inputFile != null;
                try {
                        Time timeObject = new Time();
                        timeObject.setInitialTime(inputFile, args);
                        Process processObject = new Process();
                        processObject.process(inputFile, args[1]);
                        if (!Process.lastCommand.equals("ZReport")){
                                String[] lastcommand = {"ZReport"};
                                FileRW.writeToFile(args[1],"ZReport:",true,true);
                                lastCommand = true;
                                processObject.process(lastcommand,args[1]);

                        }
                } catch (Errors.ErroneousCommandException e) {
                        FileRW.writeToFile(args[1], e.getMessage(), true, false);

                } catch (ArrayIndexOutOfBoundsException | DateTimeException e) {
                        FileRW.writeToFile(args[1], "ERROR: Format of the initial date is wrong!" +
                                " Program is going to terminate!", true, false);

                } catch (NumberFormatException e){
                        FileRW.writeToFile(args[1], "ERROR: First command must be set initial time!" +
                                " Program is going to terminate!", true, false);
                }

        }
        }




