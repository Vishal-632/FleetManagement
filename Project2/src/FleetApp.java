/**
 * The Fleet Management System program is designed to store
 * and manage a collection of boats. Users can load fleet data
 * from a CSV file or a serialized database, add and remove boats,
 * track expenses, and print fleet reports.
 *
 * <p>Each boat is represented with characteristics such as:
 * <ul>
 *     <li>Type (Sailing or Power)</li>
 *     <li>Name</li>
 *     <li>Year</li>
 *     <li>Make/Model</li>
 *     <li>Length in feet</li>
 *     <li>Purchase price</li>
 *     <li>Expenses</li>
 * </ul>
 *
 * Data persistence is handled by serializing to a local file.
 *
 * @author Vishal Thoutam
 * @version 1.0
 * @since 2025-11-28
 */

import java.io.*;
import java.util.*;

/**
 * Enum representing the type of boat in the fleet.
 * Possible values are:
 * <ul>
 *     <li>SAILING</li>
 *     <li>POWER</li>
 * </ul>
 */

enum BoatType { SAILING, POWER }

/**
 * Represents a boat in the fleet with attributes such as type, name, year,
 * make/model, length, purchase price, and expenses. Implements serializable
 * to allow persistence in a database file.
 */
class Boat implements Serializable {
    private BoatType type;
    private String name;
    private int year;
    private String makeModel;
    private int lengthFeet;
    private double purchasePrice;
    private double expenses;

    /**
     * Constructs a new Boat object with the given attributes.
     *
     * @param type the type of boat (SAILING or POWER)
     * @param name the name of the boat
     * @param year the year the boat was built
     * @param makeModel the make and model of the boat
     * @param lengthFeet the length of the boat in feet
     * @param purchasePrice the purchase price of the boat
     * @param expenses the current expenses associated with the boat
     */
    public Boat(BoatType type, String name, int year, String makeModel, int lengthFeet, double purchasePrice, double expenses) {
        this.type = type;
        this.name = name;
        this.year = year;
        this.makeModel = makeModel;
        this.lengthFeet = lengthFeet;
        this.purchasePrice = purchasePrice;
        this.expenses = expenses;
    } // end of Boat

    /**
     * Returns the name of the boat.
     *
     * @return the boat's name
     */
    public String getName() {
        return name;
    } // end of getName

    /**
     * Adds an expense to the boat if it does not exceed the purchase price.
     *
     * @param amount the expense amount to add
     * @return true if the expense was added, false otherwise
     */
    public boolean addExpense(double amount) {
        if (expenses + amount <= purchasePrice) {
            expenses += amount;
            return true;
        } // end of if function
        return false;
    } // end of addExpense

    /**
     * Returns a formatted string representation of the boat.
     *
     * @return a string containing boat details
     */
    @Override
    public String toString() {
        return String.format("%-8s %-15s %4d %-12s %3d' : Paid $ %10.2f : Spent $ %10.2f", type, name, year, makeModel, lengthFeet, purchasePrice, expenses);
    } // end of toString

    /** @return the purchase price of the boat */
    public double getPurchasePrice() {
        return purchasePrice;
    } // wnd of getPurchasePrice

    /** @return the total expenses of the boat */
    public double getExpenses() {
        return expenses;
    } // end of getExpenses
}

/**
 * Manages a fleet of boats, providing functionality to load data from CSV or database,
 * save data, add/remove boats, authorize expenses, and print fleet reports.
 */
class FleetManager {
    private ArrayList<Boat> fleet = new ArrayList<>();
    private static final String DB_FILE = "FleetData.db";

    /**
     * Loads fleet data from a CSV file.
     *
     * @param filename the CSV file containing boat data
     * @throws IOException if an error occurs while reading the file
     */
    public void loadFromCSV(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                addBoat(line);
            } // end of while loop
        } // end of try
    } // end of loadFromCSV

    /**
     * Loads fleet data from the serialized database file.
     *
     * @throws IOException if an error occurs while reading the file
     * @throws ClassNotFoundException if the Boat class cannot be found
     */
    public void loadFromDB() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DB_FILE))) {
            fleet = (ArrayList<Boat>) ois.readObject();
        } // end of try
    } // end of loadFromDB

    /**
     * Saves the current fleet data to the serialized database file.
     *
     * @throws IOException if an error occurs while writing the file
     */
    public void saveToDB() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(fleet);
        } // end of try
    } // end of saveToDB

    /**
     * Adds a boat to the fleet using a CSV-formatted string.
     *
     * @param csvLine the CSV line containing boat data
     */
    public void addBoat(String csvLine) {
        String[] parts = csvLine.split(",");
        BoatType type = BoatType.valueOf(parts[0].trim().toUpperCase());
        String name = parts[1].trim();
        int year = Integer.parseInt(parts[2].trim());
        String makeModel = parts[3].trim();
        int length = Integer.parseInt(parts[4].trim());
        double price = Double.parseDouble(parts[5].trim());
        double expenses = (parts.length > 6) ? Double.parseDouble(parts[6].trim()) : 0.0;
        fleet.add(new Boat(type, name, year, makeModel, length, price, expenses));
    } // end of addBoat

    /**
     * Removes a boat from the fleet by name.
     *
     * @param name the name of the boat to remove
     * @return true if the boat was removed, false otherwise
     */
    public boolean removeBoat(String name) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) {
                fleet.remove(b);
                return true;
            } // end of if function
        } // end of for loop
        return false;
    } // end of removeBoat

    /**
     * Requests an expense for a specific boat.
     *
     * @param name the name of the boat
     * @param amount the expense amount
     * @return true if the expense was authorized, false otherwise
     */
    public boolean requestExpense(String name, double amount) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b.addExpense(amount);
            } // end of if function
        } // end of for loop
        return false;
    } // end of requestExpense

    /**
     * Finds a boat in the fleet by name.
     *
     * @param name the name of the boat
     * @return the Boat object if found, null otherwise
     */
    public Boat findBoat(String name) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            } // end of if function
        } // end of for loop
        return null;
    } // end of findBoat

    /**
     * Prints a report of all boats in the fleet, including totals.
     */
    public void printFleet() {
        double totalPaid = 0, totalSpent = 0;
        for (Boat b : fleet) {
            System.out.println(b);
            totalPaid += b.getPurchasePrice();
            totalSpent += b.getExpenses();
        } // end of for loop
        System.out.printf("%-47s : Paid $ %10.2f : Spent $ %10.2f%n", "Total", totalPaid, totalSpent);
    } // end of printFleet

    /**
     * Returns the list of boats in the fleet.
     *
     * @return an ArrayList of Boat objects
     */
    public ArrayList<Boat> getFleet() {
        return fleet;
    } // end of getFleet

} // end of boat class

/**
 * Main application class for the Fleet Management System.
 * Handles user interaction through a console and represents operations to FleetManager.
 */
public class FleetApp {

    /**
     * Entry point of the Fleet Management System.
     * Loads data, presents a menu to the user, and saves data when exiting.
     *
     * @param args optional CSV filename to load initial fleet data
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FleetManager manager = new FleetManager();

        try {
            if (args.length > 0) {
                manager.loadFromCSV(args[0]);
            }
            else {
                manager.loadFromDB();
            } // end of if-else function
        }
        catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        } // end of try-catch function

        System.out.println("Welcome to the Fleet Management System");
        System.out.println("--------------------------------------");

        boolean running = true;
        while (running) {
            System.out.print("(P)rint, (A)dd, (R)emove, (E)xpense, e(X)it : ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "P":
                    System.out.println("Fleet report:");
                    manager.printFleet();
                    break;
                case "A":
                    System.out.print("Please enter the new boat CSV data : ");
                    manager.addBoat(sc.nextLine());
                    break;
                case "R":
                    System.out.print("Which boat do you want to remove? : ");
                    String removeName = sc.nextLine();
                    if (!manager.removeBoat(removeName)) {
                        System.out.println("Cannot find boat " + removeName);
                    } // end of if function
                    break;
                case "E":
                    System.out.print("Which boat do you want to spend on? : ");
                    String boatName = sc.nextLine();
                    Boat b = manager.findBoat(boatName);
                    if (b == null) {
                        System.out.println("Cannot find boat " + boatName);
                        break;
                    } // end of if function

                    System.out.print("How much do you want to spend? : ");
                    double amt = Double.parseDouble(sc.nextLine());
                    if (b.addExpense(amt)) {
                        System.out.printf("Expense authorized, $%.2f spent.%n", amt);
                    }
                    else {
                        double remaining = b.getPurchasePrice() - b.getExpenses();
                        System.out.printf("Expense not permitted, only $%.2f left to spend.%n", remaining);
                    } // end of if-else function
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid menu option, try again");
            } // end of switch function
        } // end of while loop

        try {
            manager.saveToDB();
        }
        catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        } // end of try-catch function

        System.out.println();
        System.out.println("Exiting the Fleet Management System");

    } // end of main
} // end of FleetApp class