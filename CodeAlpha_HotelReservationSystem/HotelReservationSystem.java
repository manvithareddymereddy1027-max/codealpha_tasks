import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class HotelReservationSystem {

    static Scanner sc = new Scanner(System.in);

    static String[] categories = {
        "Standard", "Standard",
        "Deluxe", "Deluxe",
        "Suite", "Suite"
    };

    static int[] roomNumbers = {
        101, 102, 201, 202, 301, 302
    };

    static double[] prices = {
        1500, 1500, 2500, 2500, 4000, 4000
    };

    static ArrayList<String> bookings = new ArrayList<>();

    static int bookingId = 1001;

    static final String FILE = "bookings.txt";

    // Read a valid integer
    static int readNumber(String message) {

        while (true) {
            System.out.print(message);

            String input = sc.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Search Available Rooms
    static void searchRooms() {

        System.out.println("\n--- AVAILABLE ROOMS ---");

        for (int i = 0; i < roomNumbers.length; i++) {

            System.out.println(
                "Room: " + roomNumbers[i] +
                " | Category: " + categories[i] +
                " | Price: Rs." + prices[i]
            );
        }
    }

    // Check Room Availability
    static boolean isAvailable(int room,
                               LocalDate checkIn,
                               LocalDate checkOut) {

        for (String data : bookings) {

            String[] p = data.split(",");

            if (p.length == 8 &&
                Integer.parseInt(p[2]) == room &&
                p[7].equals("Confirmed")) {

                LocalDate oldIn = LocalDate.parse(p[5]);
                LocalDate oldOut = LocalDate.parse(p[6]);

                if (checkIn.isBefore(oldOut) &&
                    checkOut.isAfter(oldIn)) {

                    return false;
                }
            }
        }

        return true;
    }

    // Book Room
    static void bookRoom() {

        System.out.println("\n--- ROOM BOOKING ---");

        try {

            System.out.print("Enter Customer Name: ");
            String name = sc.nextLine();

            if (name.trim().isEmpty()) {
                System.out.println("Customer name cannot be empty.");
                return;
            }

            System.out.println("\nRoom Categories:");
            System.out.println("1. Standard - Rs.1500");
            System.out.println("2. Deluxe   - Rs.2500");
            System.out.println("3. Suite    - Rs.4000");

            int categoryChoice = readNumber("Choose Room Category: ");

            String selectedCategory;

            switch (categoryChoice) {
                case 1:
                    selectedCategory = "Standard";
                    break;

                case 2:
                    selectedCategory = "Deluxe";
                    break;

                case 3:
                    selectedCategory = "Suite";
                    break;

                default:
                    System.out.println("Invalid room category!");
                    return;
            }

            System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(sc.nextLine());

            System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(sc.nextLine());

            if (checkIn.isBefore(LocalDate.now()) ||
                !checkIn.isBefore(checkOut)) {

                System.out.println("Please enter valid booking dates!");
                return;
            }

            System.out.println("\n--- AVAILABLE ROOMS ---");

            boolean found = false;

            for (int i = 0; i < roomNumbers.length; i++) {

                if (categories[i].equals(selectedCategory) &&
                    isAvailable(roomNumbers[i], checkIn, checkOut)) {

                    System.out.println(
                        "Room Number: " + roomNumbers[i] +
                        " | Category: " + categories[i] +
                        " | Price: Rs." + prices[i]
                    );

                    found = true;
                }
            }

            if (!found) {
                System.out.println("No rooms available in this category.");
                return;
            }

            int room = readNumber("\nEnter Room Number: ");

            int index = -1;

            for (int i = 0; i < roomNumbers.length; i++) {

                if (roomNumbers[i] == room &&
                    categories[i].equals(selectedCategory)) {

                    index = i;
                    break;
                }
            }

            if (index == -1 ||
                !isAvailable(room, checkIn, checkOut)) {

                System.out.println("Invalid or unavailable room!");
                return;
            }

            int days = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

            double total = prices[index] * days;

            System.out.println("\n===== BOOKING SUMMARY =====");
            System.out.println("Customer Name: " + name);
            System.out.println("Room Number: " + room);
            System.out.println("Category: " + selectedCategory);
            System.out.println("Check-in: " + checkIn);
            System.out.println("Check-out: " + checkOut);
            System.out.println("Total Days: " + days);
            System.out.println("Total Amount: Rs." + total);

            int payment = readNumber(
                "\nConfirm Payment? (1 = Yes, 0 = No): "
            );

            if (payment == 1) {

                String record =
                    bookingId + "," +
                    name.replace(",", " ") + "," +
                    room + "," +
                    days + "," +
                    total + "," +
                    checkIn + "," +
                    checkOut + ",Confirmed";

                bookings.add(record);

                saveBookings();

                System.out.println("\nPayment Successful!");
                System.out.println("Room Booked Successfully!");
                System.out.println("Your Booking ID: " + bookingId);

                bookingId++;

            } else if (payment == 0) {

                System.out.println("Payment Cancelled!");

            } else {

                System.out.println("Invalid payment choice. Booking not completed.");
            }

        } catch (Exception e) {

            System.out.println("Invalid input! Please try again.");

        }
    }

    // View Booking Details
    static void viewBookings() {

        System.out.println("\n--- BOOKING DETAILS ---");

        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        for (String data : bookings) {

            String[] p = data.split(",");

            System.out.println("\nBooking ID: " + p[0]);
            System.out.println("Customer: " + p[1]);
            System.out.println("Room Number: " + p[2]);
            System.out.println("Days: " + p[3]);
            System.out.println("Amount: Rs." + p[4]);
            System.out.println("Check-in: " + p[5]);
            System.out.println("Check-out: " + p[6]);
            System.out.println("Status: " + p[7]);

            System.out.println("-------------------------");
        }
    }

    // Cancel Booking
    static void cancelBooking() {

        int id = readNumber("Enter Booking ID: ");

        for (int i = 0; i < bookings.size(); i++) {

            String[] p = bookings.get(i).split(",");

            if (Integer.parseInt(p[0]) == id &&
                p[7].equals("Confirmed")) {

                p[7] = "Cancelled";

                bookings.set(i, String.join(",", p));

                saveBookings();

                System.out.println("Booking Cancelled Successfully!");
                return;
            }
        }

        System.out.println("Booking not found or already cancelled.");
    }

    // Save Bookings
    static void saveBookings() {

        try (PrintWriter writer =
                 new PrintWriter(new FileWriter(FILE))) {

            for (String data : bookings) {
                writer.println(data);
            }

        } catch (IOException e) {

            System.out.println("File saving error!");

        }
    }

    // Load Bookings
    static void loadBookings() {

        File file = new File(FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] p = line.split(",");

                if (p.length == 8) {

                    bookings.add(line);

                    int id = Integer.parseInt(p[0]);

                    if (id >= bookingId) {
                        bookingId = id + 1;
                    }
                }
            }

        } catch (Exception e) {

            System.out.println("File loading error!");

        }
    }

    // Main Method
    public static void main(String[] args) {

        loadBookings();

        int choice;

        do {

            System.out.println("\n================================");
            System.out.println("     HOTEL RESERVATION SYSTEM");
            System.out.println("================================");

            System.out.println("1. Search Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. View Booking Details");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Exit");

            choice = readNumber("Enter Your Choice: ");

            switch (choice) {

                case 1:
                    searchRooms();
                    break;

                case 2:
                    bookRoom();
                    break;

                case 3:
                    viewBookings();
                    break;

                case 4:
                    cancelBooking();
                    break;

                case 5:
                    System.out.println("Thank You for Using Our Hotel!");
                    break;

                default:
                    System.out.println("Please enter a valid number.");
            }

        } while (choice != 5);

        sc.close();
    }
}
