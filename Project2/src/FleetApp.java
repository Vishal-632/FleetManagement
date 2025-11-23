import java.io.*;
import java.util.*;

enum BoatType { SAILING, POWER }

class Boat implements Serializable {
    private BoatType type;
    private String name;
    private int year;
    private String makeModel;
    private int lengthFeet;
    private double purchasePrice;
    private double expenses;

    public Boat(BoatType type, String name, int year, String makeModel,
                int lengthFeet, double purchasePrice, double expenses) {
        this.type = type;
        this.name = name;
        this.year = year;
        this.makeModel = makeModel;
        this.lengthFeet = lengthFeet;
        this.purchasePrice = purchasePrice;
        this.expenses = expenses;
    }

    public String getName() { return name; }

    public boolean addExpense(double amount) {
        if (expenses + amount <= purchasePrice) {
            expenses += amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%-8s %-15s %4d %-12s %3d' : Paid $ %10.2f : Spent $ %10.2f",
                type, name, year, makeModel, lengthFeet, purchasePrice, expenses);
    }

    public double getPurchasePrice() { return purchasePrice; }
    public double getExpenses() { return expenses; }
}

class FleetManager {
    private ArrayList<Boat> fleet = new ArrayList<>();
    private static final String DB_FILE = "FleetData.db";

    public void loadFromCSV(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                addBoat(line);
            }
        }
    }

    public void loadFromDB() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DB_FILE))) {
            fleet = (ArrayList<Boat>) ois.readObject();
        }
    }

    public void saveToDB() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(fleet);
        }
    }

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
    }

    public boolean removeBoat(String name) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) {
                fleet.remove(b);
                return true;
            }
        }
        return false;
    }

    public boolean requestExpense(String name, double amount) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b.addExpense(amount);
            }
        }
        return false;
    }

    public Boat findBoat(String name) {
        for (Boat b : fleet) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    public void printFleet() {
        double totalPaid = 0, totalSpent = 0;
        for (Boat b : fleet) {
            System.out.println(b);
            totalPaid += b.getPurchasePrice();
            totalSpent += b.getExpenses();
        }
        System.out.printf("%-47s : Paid $ %10.2f : Spent $ %10.2f%n", "Total", totalPaid, totalSpent);
    }

    public ArrayList<Boat> getFleet() {
        return fleet;
    }
}

public class FleetApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FleetManager manager = new FleetManager();

        try {
            if (args.length > 0) {
                manager.loadFromCSV(args[0]);
            } else {
                manager.loadFromDB();
            }
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }

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
                    }
                    break;
                case "E":
                    System.out.print("Which boat do you want to spend on? : ");
                    String boatName = sc.nextLine();
                    Boat b = manager.findBoat(boatName);
                    if (b == null) {
                        System.out.println("Cannot find boat " + boatName);
                        break;
                    }

                    System.out.print("How much do you want to spend? : ");
                    double amt = Double.parseDouble(sc.nextLine());
                    if (b.addExpense(amt)) {
                        System.out.printf("Expense authorized, $%.2f spent.%n", amt);
                    } else {
                        double remaining = b.getPurchasePrice() - b.getExpenses();
                        System.out.printf("Expense not permitted, only $%.2f left to spend.%n", remaining);
                    }
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid menu option, try again");
            }
        }

        try {
            manager.saveToDB();
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Exiting the Fleet Management System");

    }
}