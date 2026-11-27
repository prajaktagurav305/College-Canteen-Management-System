import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Canteen canteen = new Canteen(sc);

        // Keep the application running so student and admin can use the same data.
        while (true) {
            System.out.println("\n===== COLLEGE CANTEEN MANAGEMENT SYSTEM =====");
            System.out.println("Enter passkey: student / admin (or type exit)");
            System.out.print("Passkey: ");
            String passkey = sc.nextLine().trim();

            if (passkey.equalsIgnoreCase("student")) {
                Student student = new Student(canteen);
                student.studentDashboard();
            } else if (passkey.equalsIgnoreCase("admin")) {
                Admin admin = new Admin(canteen);
                admin.dashboard();
            } else if (passkey.equalsIgnoreCase("exit")) {
                System.out.println("Canteen system closed. Goodbye!");
                sc.close();
                return;
            } else {
                System.out.println("Invalid passkey. Enter student, admin, or exit.");
            }
        }
    }
}

class Canteen {
    private static final Path ORDERS_FILE = Paths.get("orders.txt");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Scanner sc;
    private final String[] dishNames = {
        "Idli", "Dosa", "Vada Pav", "Vada",
        "Pani Puri", "Bhel", "Tea", "Coffee"
    };
    private final int[] prices = {30, 35, 12, 15, 20, 20, 5, 10};
    private final boolean[] available = {
        true, true, true, true, true, true, true, true
    };

    private final List<Order> orders = new ArrayList<>();
    private int nextOrderId = 1;

    Canteen(Scanner sc) {
        this.sc = sc;
        loadOrdersFromFile();
    }

    int readInt() {
        while (true) {
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid whole number: ");
            }
        }
    }

    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    int getDishCount() {
        return dishNames.length;
    }

    void displayMenu() {
        System.out.println("\n------------- BREAKFAST -------------");
        for (int i = 0; i < 4; i++) {
            printDish(i);
        }

        System.out.println("\n--------------- SNACKS ---------------");
        for (int i = 4; i < 6; i++) {
            printDish(i);
        }

        System.out.println("\n--------------- DRINKS ---------------");
        for (int i = 6; i < 8; i++) {
            printDish(i);
        }
    }

    private void printDish(int index) {
        String status = available[index] ? "" : " (NOT AVAILABLE)";
        System.out.printf("%d. %-12s - Rs. %d%s%n",
                index + 1, dishNames[index], prices[index], status);
    }

    void selectItems(int[] cart) {
        while (true) {
            displayMenu();
            System.out.print("\nEnter dish number to add (0 to finish): ");
            int choice = readInt();

            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > dishNames.length) {
                System.out.println("Invalid dish number.");
                continue;
            }

            int index = choice - 1;
            if (!available[index]) {
                System.out.println("Sorry, this dish is currently unavailable.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = readInt();
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                continue;
            }

            cart[index] += quantity;
            System.out.println(quantity + " x " + dishNames[index] + " added to your cart.");
        }
    }

    void displaySelectedItems(int[] cart) {
        int total = calculateTotal(cart);
        System.out.println("\n----------- SELECTED DISHES -----------");

        if (total == 0) {
            System.out.println("Your cart is empty.");
            return;
        }

        for (int i = 0; i < cart.length; i++) {
            if (cart[i] > 0) {
                System.out.printf("%-12s x %d = Rs. %d%n",
                        dishNames[i], cart[i], cart[i] * prices[i]);
            }
        }
        System.out.println("---------------------------------------");
        System.out.println("Total amount: Rs. " + total);
    }

    private int calculateTotal(int[] cart) {
        int total = 0;
        for (int i = 0; i < cart.length; i++) {
            total += cart[i] * prices[i];
        }
        return total;
    }

    void checkout(int[] cart) {
        int total = calculateTotal(cart);
        if (total == 0) {
            System.out.println("Your cart is empty. Add dishes before checkout.");
            return;
        }

        System.out.println("\n================ BILL ================");
        displaySelectedItems(cart);
        System.out.println("\n1. Confirm order");
        System.out.println("2. Cancel checkout");
        System.out.print("Enter your choice: ");
        int choice = readInt();

        if (choice != 1) {
            System.out.println("Checkout cancelled. Your cart has been kept.");
            return;
        }

        String studentName = readRequiredText("Enter student name: ");
        String studentId = readRequiredText("Enter student ID / USN: ");

        Order order = new Order(
                nextOrderId,
                studentName,
                studentId,
                cart.clone(),
                total,
                false,
                LocalDateTime.now()
        );

        try {
            appendOrderToFile(order);
            orders.add(order);
            nextOrderId++;
            Arrays.fill(cart, 0);
            System.out.println("\nOrder placed and saved successfully!");
            System.out.println("Your Order ID is: " + order.id);
            System.out.println("Keep this ID to refer to your order.");
        } catch (IOException e) {
            System.out.println("Could not save your order: " + e.getMessage());
            System.out.println("Your cart has not been cleared. Please try again.");
        }
    }

    void showReceivedOrders() {
        boolean found = false;
        System.out.println("\n----------- PENDING ORDERS -----------");

        for (Order order : orders) {
            if (!order.completed) {
                displayOrder(order);
                found = true;
            }
        }

        if (!found) {
            System.out.println("There are no pending orders.");
            return;
        }

        System.out.print("Enter Order ID to mark as completed (0 to go back): ");
        int id = readInt();
        if (id == 0) {
            return;
        }

        Order selectedOrder = findOrder(id);
        if (selectedOrder == null) {
            System.out.println("Order ID not found.");
        } else if (selectedOrder.completed) {
            System.out.println("This order is already completed.");
        } else {
            selectedOrder.completed = true;
            try {
                rewriteOrdersFile();
                System.out.println("Order #" + id + " marked as completed and saved.");
            } catch (IOException e) {
                selectedOrder.completed = false;
                System.out.println("Could not update the order file: " + e.getMessage());
            }
        }
    }

    void showCompletedOrders() {
        boolean found = false;
        System.out.println("\n----------- COMPLETED ORDERS -----------");

        for (Order order : orders) {
            if (order.completed) {
                displayOrder(order);
                found = true;
            }
        }

        if (!found) {
            System.out.println("There are no completed orders yet.");
        }
    }

    private void displayOrder(Order order) {
        System.out.println("\nOrder ID: " + order.id);
        System.out.println("Student: " + order.studentName);
        System.out.println("Student ID / USN: " + order.studentId);
        System.out.println("Placed at: " + order.createdAt.format(DATE_FORMAT));

        for (int i = 0; i < order.quantities.length; i++) {
            if (order.quantities[i] > 0) {
                System.out.printf("%-12s x %d = Rs. %d%n",
                        dishNames[i], order.quantities[i],
                        order.quantities[i] * prices[i]);
            }
        }

        System.out.println("Total: Rs. " + order.total);
        System.out.println("Status: " + (order.completed ? "COMPLETED" : "PENDING"));
        System.out.println("----------------------------------------");
    }

    private Order findOrder(int id) {
        for (Order order : orders) {
            if (order.id == id) {
                return order;
            }
        }
        return null;
    }

    void showTotalAmountEarned() {
        int earned = 0;
        int pendingValue = 0;

        for (Order order : orders) {
            if (order.completed) {
                earned += order.total;
            } else {
                pendingValue += order.total;
            }
        }

        System.out.println("\n----------- SALES SUMMARY -----------");
        System.out.println("Revenue from completed orders: Rs. " + earned);
        System.out.println("Value of pending orders:       Rs. " + pendingValue);
    }

    void changeAvailability() {
        displayMenu();
        System.out.print("\nEnter dish number to toggle availability (0 to go back): ");
        int choice = readInt();

        if (choice == 0) {
            return;
        }
        if (choice < 1 || choice > dishNames.length) {
            System.out.println("Invalid dish number.");
            return;
        }

        int index = choice - 1;
        available[index] = !available[index];
        System.out.println(dishNames[index] + " is now "
                + (available[index] ? "AVAILABLE" : "NOT AVAILABLE") + ".");
        System.out.println("Note: Menu availability changes last only until the program is closed.");
    }

    private void appendOrderToFile(Order order) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                ORDERS_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(serializeOrder(order));
            writer.newLine();
        }
    }

    private void rewriteOrdersFile() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                ORDERS_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (Order order : orders) {
                writer.write(serializeOrder(order));
                writer.newLine();
            }
        }
    }

    private String serializeOrder(Order order) {
        String encodedName = Base64.getEncoder().encodeToString(
                order.studentName.getBytes(StandardCharsets.UTF_8));
        String encodedStudentId = Base64.getEncoder().encodeToString(
                order.studentId.getBytes(StandardCharsets.UTF_8));

        StringBuilder quantities = new StringBuilder();
        for (int i = 0; i < order.quantities.length; i++) {
            if (i > 0) {
                quantities.append(',');
            }
            quantities.append(order.quantities[i]);
        }

        return order.id + "|"
                + encodedName + "|"
                + encodedStudentId + "|"
                + quantities + "|"
                + order.total + "|"
                + (order.completed ? "COMPLETED" : "PENDING") + "|"
                + order.createdAt.format(DATE_FORMAT);
    }

    private void loadOrdersFromFile() {
        if (!Files.exists(ORDERS_FILE)) {
            System.out.println("No previous order file found. A new one will be created when an order is placed.");
            return;
        }

        int loadedCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(ORDERS_FILE, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Order order = deserializeOrder(line);
                    orders.add(order);
                    nextOrderId = Math.max(nextOrderId, order.id + 1);
                    loadedCount++;
                } catch (RuntimeException e) {
                    System.out.println("Warning: Could not read order record on line " + lineNumber + ". It was skipped.");
                }
            }

            System.out.println("Loaded " + loadedCount + " saved order(s) from orders.txt.");
        } catch (IOException e) {
            System.out.println("Could not load previous orders: " + e.getMessage());
        }
    }

    private Order deserializeOrder(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid order record");
        }

        int id = Integer.parseInt(parts[0]);
        String studentName = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String studentId = new String(Base64.getDecoder().decode(parts[2]), StandardCharsets.UTF_8);
        String[] quantityParts = parts[3].split(",", -1);

        if (quantityParts.length != dishNames.length) {
            throw new IllegalArgumentException("Invalid dish quantity count");
        }

        int[] quantities = new int[dishNames.length];
        for (int i = 0; i < quantityParts.length; i++) {
            quantities[i] = Integer.parseInt(quantityParts[i]);
            if (quantities[i] < 0) {
                throw new IllegalArgumentException("Negative quantity");
            }
        }

        int total = Integer.parseInt(parts[4]);
        boolean completed;
        if ("COMPLETED".equals(parts[5])) {
            completed = true;
        } else if ("PENDING".equals(parts[5])) {
            completed = false;
        } else {
            throw new IllegalArgumentException("Invalid order status");
        }

        LocalDateTime createdAt = LocalDateTime.parse(parts[6], DATE_FORMAT);
        return new Order(id, studentName, studentId, quantities, total, completed, createdAt);
    }
}

class Order {
    final int id;
    final String studentName;
    final String studentId;
    final int[] quantities;
    final int total;
    boolean completed;
    final LocalDateTime createdAt;

    Order(int id, String studentName, String studentId, int[] quantities,
          int total, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.studentName = studentName;
        this.studentId = studentId;
        this.quantities = quantities;
        this.total = total;
        this.completed = completed;
        this.createdAt = createdAt;
    }
}

class Admin {
    private final Canteen canteen;

    Admin(Canteen canteen) {
        this.canteen = canteen;
    }

    void dashboard() {
        int choice;
        do {
            System.out.println("\n========== ADMIN DASHBOARD ==========");
            System.out.println("1. Orders Received (Pending)");
            System.out.println("2. Orders Completed");
            System.out.println("3. Sales Summary");
            System.out.println("4. Availability of Dishes");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = canteen.readInt();

            switch (choice) {
                case 1:
                    canteen.showReceivedOrders();
                    break;
                case 2:
                    canteen.showCompletedOrders();
                    break;
                case 3:
                    canteen.showTotalAmountEarned();
                    break;
                case 4:
                    canteen.changeAvailability();
                    break;
                case 5:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }
}

class Student {
    private final Canteen canteen;

    Student(Canteen canteen) {
        this.canteen = canteen;
    }

    void studentDashboard() {
        int[] cart = new int[canteen.getDishCount()];
        int choice;

        do {
            System.out.println("\n========= STUDENT DASHBOARD =========");
            System.out.println("1. View Menu");
            System.out.println("2. Select Dishes");
            System.out.println("3. View Selected Dishes");
            System.out.println("4. Generate Bill / Place Order");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = canteen.readInt();

            switch (choice) {
                case 1:
                    canteen.displayMenu();
                    break;
                case 2:
                    canteen.selectItems(cart);
                    break;
                case 3:
                    canteen.displaySelectedItems(cart);
                    break;
                case 4:
                    canteen.checkout(cart);
                    break;
                case 5:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }
}
