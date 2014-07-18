package edu.pdx.cs410J.singh2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.ParserException;

import java.io.*;

/**
 * The main class for the CS410J airline Project
 */
public class Project2 {

    public static final String USAGE="usage: java edu.pdx.cs410J.singh2.Project1 [options] <args>" +
            "\nargs are (in this order):" +
            "\nname             The name of the airline" +
            "\nflightNumber     The flight number" +
            "\nsrc              Three-letter code of departure airport" +
            "\ndepartTime       Departure date and time (24-hour time)" +
            "\ndest             Three-letter code of arrival airport" +
            "\narriveTime       Arrival date and time (24-hour time)" +
            "\noptions are (options may appear in any order):" +
            "\n-textFile file   Where to read/write the airline info" +
            "\n-print           Prints a description of the new flight" +
            "\n-README          Prints a README for this project and exits";

    public static final String INVALID_FN = "Invalid Flight Number";
    public static final String INVALID_CODE = "Invalid Three-letter Code";
    public static final String INVALID_DATE = "Invalid Date Format. Must be in mm/dd/yyyy";
    public static final String INVALID_TIME = "Invalid Time Format. Must in 24-hour time";
    public static final String INVALID_FILE = "Invalid File. Must be a txt file";

    private static void handlePrintREADME() {
        System.out.println("\nAuthor\n-------\nHarmanpreet Singh\nProject1\nCS410J\n7/9/2014\n");
        System.out.println("What is it?\n-----------");
        System.out.println("A simple basic program that keeps track of Flights. Flight can be created" +
                "\nand added to Airline database. An Airline has a name that consists of" +
                "\nmultiples flights. Each Flight departs from a source and leaves at a" +
                "\ngiven departure time and arrives at the destination at given arrival time.");

        System.out.println("\nHow to run?\n----------\nBefore running the application, read the USAGE section below. " +
                "To run this program \nenter the following on a terminal or command prompt:");
        System.out.println("\tjava edu.pdx.cs410J.singh2.Project1 [options] <args>\n" +
                "\tjava edu.pdx.cs410J.singh2.Project1 -print \"Air Express\" 432 PDX 01/12/2014 03:34 LAX 01/12/2014 05:40");

        System.out.println("\nUSAGE\n-------\nNote: Arguments must be in order. Options order does not matter and they are optional.\n\n" + USAGE);

        System.exit(0);
    }

    /**
     * If -print flag is detected from the command line args, then print a description
     * of the new flight
     *
     * @param args
     *        string of args with print flag
     */
    private static void handlePrintFlag(String... args) {
        int flightNumber;

        String src, departDate, departTime, dest, arriveDate, arriveTime;

        Airline airline = new Airline(args[0]);

        flightNumber = validateFlightNumber(args[1]);
        src = validateThreeLetterCode(args[2]);
        departDate = validateDate(args[3]);
        departTime = validateTime(args[4]);
        dest = validateThreeLetterCode(args[5]);
        arriveDate = validateDate(args[6]);
        arriveTime = validateTime(args[7]);

        Flight flight = new Flight(flightNumber, src, departDate + " " + departTime, dest, arriveDate + " " + arriveTime);
        airline.addFlight(flight);

        System.out.println(flight.toString());
    }

    private static void handleTextFile(String [] args, String fileName) throws ParserException, IOException {

        int flightNumber;
        String src, dest;
        String departDate, departTime;
        String arriveDate, arriveTime;
        String airlineName;
        File file;

        TextParser textParser;
        TextDumper textDumper;
        AbstractAirline airline = null;

        int i = 0;

        if (args[0].compareToIgnoreCase("-print") == 0) {
            i = 3;
            airlineName = args[i];
        }
        else if (args[2].compareToIgnoreCase("-print") == 0) {
            i = 3;
            airlineName = args[i];
        }
        else {
            i = 2;
            airlineName = args[i];
        }


        // if file does not exist, parse will throw file not found
        // exception. Make a new file and create an empty airline
        try {
            textParser = new TextParser(fileName);
            airline = textParser.parse();
        } catch (ParserException e) {
            if (e.getMessage().contains("File Is Empty")) {
                throw new ParserException("File Is Empty");
            }
            if (e.getMessage().contains("File Not Found") || e.getMessage().contains("File Does Not Exist")) {
                file = new File(fileName);

                try {
                    file.createNewFile();
                }
                /*Writer outFile;
                try {
                    outFile = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(fileName), "UTF-8"));
                    outFile.write(airline.getName());

                    outFile.close();*/
                catch (IOException e1){
                    throw new ParserException("File Write Error");
                }
            }
        }

        if (airline == null) {
            airline = new Airline(airlineName);
        }
        flightNumber = validateFlightNumber(args[i + 1]);
        src = validateThreeLetterCode(args[i + 2]);
        departDate = validateDate(args[i + 3]);
        departTime = validateTime(args[i + 4]);
        dest = validateThreeLetterCode(args[i + 5]);
        arriveDate = validateDate(args[i + 6]);
        arriveTime = validateTime(args[i + 7]);

        Flight flight = new Flight(flightNumber, src, departDate + " " + departTime, dest, arriveDate + " " + arriveTime);
        airline.addFlight(flight);

        if (airline.getName().compareToIgnoreCase(airlineName) == 0) {
            textDumper = new TextDumper(fileName);
            textDumper.dump(airline);
        }
        else if (airline.getName().compareToIgnoreCase(airlineName) != 0) {
            throw new ParserException("Airline does not match");
        }

    }


    /**
     * This function validates if flightNumber is valid integer. If flight number is not a valid integer,
     * the program exits with a user-friendly error message.
     *
     * @param arg
     *        string to parse
     * @return flightNumber
     *         parsed value of integer
     */

    private static int validateFlightNumber(String arg) {
        int flightNumber;

        try {

            flightNumber = Integer.parseInt(arg);

        } catch (NumberFormatException ex) {
            printUsageAndExit(INVALID_FN);
            throw new AssertionError("Unreachable statement");
        }

        if (flightNumber < 0) {
            printUsageAndExit(INVALID_FN);
            throw new AssertionError("Unreachable statement");
        }

        return flightNumber;
    }

    /**
     * Validate the date so the date format is mm/dd/yyyy
     * if <code>args</code> is invalid, the program exits
     * @param args
     *        the date of departure/arrival
     * @return date
     *         returns the valid date in correct format (mm/dd/yyyy)
     */

    private static String validateDate(String args) {

        String date[] = args.split("/");
        int month, day, year;

        try {

            month = Integer.parseInt(date[0]);
            day = Integer.parseInt(date[1]);
            year = Integer.parseInt(date[2]);

        } catch (NumberFormatException ex) {
            printUsageAndExit(INVALID_DATE);
            throw new AssertionError("Unreachable statement");
        }

        if (month < 1 || month > 12) {
            printUsageAndExit(INVALID_DATE);
            throw new AssertionError("Unreachable statement");
        }

        if (day < 1 || day > 31) {
            printUsageAndExit(INVALID_DATE);
            throw new AssertionError("Unreachable statement");
        }

        /* year is 9999 assuming humanity or earth survives that long */
        if (year < 1800 || year > 9999) {
            printUsageAndExit(INVALID_DATE);
            throw new AssertionError("Unreachable statement");
        }

        /* date[0] = month
           date[1] = day
           date[2] = year

           if the date is valid, then return the original valid date */
        return date[0] + "/" + date[1] + "/" + date[2];
    }

    /**
     * Validates the time format. if <code>args</code> is not
     * valid time, program exits
     * @param args
     *        the time in 24-hour format (string to parse)
     * @return
     *        returns the validated time in correct format (hh:mm)
     */
    private static String validateTime(String args) {
        String time[] = args.split(":");
        int hour, minute;

        try {

            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);

        } catch (NumberFormatException ex) {
            printUsageAndExit(INVALID_TIME);
            throw new AssertionError("Unreachable statement");
        }

        if (hour > 23 || hour < 00) {
            printUsageAndExit(INVALID_TIME);
            throw new AssertionError("Unreachable statement");
        }

        if (minute > 59 || minute < 00) {
            printUsageAndExit(INVALID_TIME);
            throw new AssertionError("Unreachable statement");
        }

        return time[0] + ":" + time[1];
    }

    /**
     * Print the error messaged parsed in and exits with system error code 1
     * @param errorMessage
     *        the error message to print
     */
    private static void printUsageAndExit(String errorMessage) {

        System.err.println(errorMessage);
        System.err.println();
        System.err.println(USAGE);

        System.exit(1);
    }

    /**
     * validates three letter code is characters only. If it contains
     * anything except characters, the program will exit
     * @param arg
     *        the string to parse with three letter code
     * @return
     *        validated parsed three letter code in upper case
     */
    private static String validateThreeLetterCode(String arg) {

        for (char c: arg.toCharArray()) {
            if (arg.length() != 3 || Character.isDigit(c)) {
                printUsageAndExit(INVALID_CODE);
                throw new AssertionError("Unreachable statement");
            }
        }

        return arg.toUpperCase();
    }

    /**
     * Main program that parses the command line, creates a new
     * <code>Airline</code> and <code>Flight</code>. Depending on
     * the given options, the program will call appropriate function
     * and throws a user friendly message if invalid option.
     * @param args
     *        command line arguments
     */
    public static void main(String[] args) throws IOException, ParserException {
        boolean hasPrintFlag = false;
        boolean hasREADMEFlag = false;
        boolean hasNoFlag = false;
        boolean textFileFlag = false;
        String fileName;

        /* if command line argument contains only README */
        for (String str : args) {
            if (str.compareToIgnoreCase("-README") == 0) {
                handlePrintREADME();
            }
        }

        if (args.length < 8 || args.length > 12)
            printUsageAndExit("Not enough or too many command line arguments");

        int counter = 0;
        int fileLocation = 0;
        /* loop through the args to check if options exist(options can be in any order) */
        for (String s : args) {
            counter++;
            if (s.compareToIgnoreCase("-README") == 0) {
                hasREADMEFlag = true;
            } else if (s.compareToIgnoreCase("-print") == 0) {
                hasPrintFlag = true;
            } else if (s.compareToIgnoreCase("-README") != 0 && s.compareToIgnoreCase("-print") != 0
                    && s.compareToIgnoreCase("-textFile") !=0) {
                hasNoFlag = true;
            }
            else if (s.compareToIgnoreCase("-textFile") == 0) {
                textFileFlag = true;
                fileLocation = counter;
            }

            if (s.compareToIgnoreCase("-README") != 0 && s.compareToIgnoreCase("-print") != 0 &&
                    s.compareToIgnoreCase("-textFile") !=0 && s.matches("-.*")) {
                printUsageAndExit("Invalid Option");
            }

        }

        fileName = args[fileLocation];

        /* if README flag is detected, print A README for this project and exit */
        if (hasREADMEFlag) {
            handlePrintREADME();
        }

        /* if print flag is detected and args length is greater than 11 that means we have unknown args */
        if (textFileFlag && hasNoFlag && hasPrintFlag && args.length > 11) {
            printUsageAndExit("Unknown command line argument first");
        }

        /* if no flag and no print flag and argument length is greater than 8 with unknown arg */
        if (hasNoFlag && !textFileFlag && !hasPrintFlag && args.length > 8) {
            printUsageAndExit("Unknown command line argument second");
        }

        if (hasPrintFlag && hasNoFlag && !textFileFlag && args.length > 9) {
            printUsageAndExit("Unknown command line argument third");
        }

        // check if the file ends with text
        if (!fileName.endsWith(".txt"))
            fileName = fileName + ".txt";

        /* if textFile is given then check if print flag is given as well*/
        if (textFileFlag && hasPrintFlag) {
            if (args[fileLocation].compareToIgnoreCase("-print") == 0)
                printUsageAndExit("Invalid File or Missing File");

        }
        if (textFileFlag && hasPrintFlag){
            handlePrintFlag(args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
            handleTextFile(args, fileName);
        }
        else if (textFileFlag && !hasPrintFlag){
            handleTextFile(args, fileName);
        }

        /* if -print flag is detected, call printFlag to print the description of the project */
        if (hasPrintFlag && !textFileFlag) {
            handlePrintFlag(args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        }

        /* if there is no print flag is given */
        if (hasNoFlag && !hasPrintFlag && !textFileFlag) {
            int flightNumber;
            String name = args[0];
            String src, dest;
            String departDate, departTime;
            String arriveDate, arriveTime;

            Airline airline = new Airline(name);

            flightNumber = validateFlightNumber(args[1]);
            src = validateThreeLetterCode(args[2]);
            departDate = validateDate(args[3]);
            departTime = validateTime(args[4]);
            dest = validateThreeLetterCode(args[5]);
            arriveDate = validateDate(args[6]);
            arriveTime = validateTime(args[7]);

            Flight flight = new Flight(flightNumber, src, departDate + " " + departTime, dest, arriveDate + " " + arriveTime);
            airline.addFlight(flight);
        }

        System.exit(0);
    }

}