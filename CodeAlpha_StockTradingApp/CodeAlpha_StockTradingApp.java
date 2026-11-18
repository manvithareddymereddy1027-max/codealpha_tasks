import java.io.*;
import java.util.*;

class Stock {
    String symbol;
    String name;
    double price;

    Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    void display() {
        System.out.println(symbol + " | " + name +
                " | Rs. " + price);
    }
}

class User {
    String username;
    double balance;
    HashMap<String, Integer> portfolio = new HashMap<>();

    User(String username, double balance) {
        this.username = username;
        this.balance = balance;
    }

    void buyStock(Stock stock, int quantity) {
        double cost = stock.price * quantity;

        if (quantity <= 0) {
            System.out.println("Invalid quantity!");
        } else if (balance >= cost) {
            balance -= cost;

            portfolio.put(stock.symbol,
                    portfolio.getOrDefault(stock.symbol, 0)
                            + quantity);

            System.out.println("Stock purchased successfully!");
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    void sellStock(Stock stock, int quantity) {
        int owned = portfolio.getOrDefault(stock.symbol, 0);

        if (quantity <= 0) {
            System.out.println("Invalid quantity!");
        } else if (owned >= quantity) {
            balance += stock.price * quantity;

            portfolio.put(stock.symbol, owned - quantity);

            System.out.println("Stock sold successfully!");
        } else {
            System.out.println("You don't own enough shares!");
        }
    }

    void showPortfolio(ArrayList<Stock> stocks) {
        double totalValue = 0;

        System.out.println("\n----- YOUR PORTFOLIO -----");

        for (Stock stock : stocks) {
            int quantity =
                    portfolio.getOrDefault(stock.symbol, 0);

            if (quantity > 0) {
                double value = quantity * stock.price;
                totalValue += value;

                System.out.println(stock.symbol +
                        " | Quantity: " + quantity +
                        " | Value: Rs. " + value);
            }
        }

        System.out.println("Cash Balance: Rs. " + balance);
        System.out.println("Stock Value: Rs. " + totalValue);
        System.out.println("Total Assets: Rs. " +
                (balance + totalValue));
    }

    void savePortfolio() {
        try (PrintWriter writer =
                     new PrintWriter("portfolio.txt")) {

            writer.println(username);
            writer.println(balance);

            for (String symbol : portfolio.keySet()) {
                writer.println(symbol + "," +
                        portfolio.get(symbol));
            }

            System.out.println("Portfolio saved!");
        } catch (IOException e) {
            System.out.println("Error saving portfolio.");
        }
    }

    void loadPortfolio() {
        File file = new File("portfolio.txt");

        if (!file.exists()) {
            return;
        }

        try (Scanner reader = new Scanner(file)) {
            username = reader.nextLine();
            balance = Double.parseDouble(reader.nextLine());

            portfolio.clear();

            while (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(",");
                portfolio.put(data[0],
                        Integer.parseInt(data[1]));
            }

            System.out.println("Portfolio loaded!");
        } catch (Exception e) {
            System.out.println("Error loading portfolio.");
        }
    }
}

class StockTradingApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Stock> stocks = new ArrayList<>();

        stocks.add(new Stock("TCS", "Tata Consultancy", 3500));
        stocks.add(new Stock("INFY", "Infosys", 1800));
        stocks.add(new Stock("RELIANCE", "Reliance", 2900));
        stocks.add(new Stock("HDFC", "HDFC Bank", 1700));
        stocks.add(new Stock("WIPRO", "Wipro", 600));

        User user = new User("Jaswanth", 100000);

        user.loadPortfolio();

        int choice;

        do {
            System.out.println("\n===== STOCK TRADING PLATFORM =====");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. View Portfolio");
            System.out.println("5. Save Portfolio");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("\n--- MARKET DATA ---");

                    for (Stock stock : stocks) {
                        stock.display();
                    }
                    break;

                case 2:
                    System.out.print("Enter stock symbol: ");
                    String buySymbol = sc.next().toUpperCase();

                    System.out.print("Enter quantity: ");
                    int buyQuantity = sc.nextInt();

                    Stock buyStock = findStock(stocks, buySymbol);

                    if (buyStock != null) {
                        user.buyStock(buyStock, buyQuantity);
                    } else {
                        System.out.println("Stock not found!");
                    }
                    break;

                case 3:
                    System.out.print("Enter stock symbol: ");
                    String sellSymbol = sc.next().toUpperCase();

                    System.out.print("Enter quantity: ");
                    int sellQuantity = sc.nextInt();

                    Stock sellStock = findStock(stocks, sellSymbol);

                    if (sellStock != null) {
                        user.sellStock(sellStock, sellQuantity);
                    } else {
                        System.out.println("Stock not found!");
                    }
                    break;

                case 4:
                    user.showPortfolio(stocks);
                    break;

                case 5:
                    user.savePortfolio();
                    break;

                case 6:
                    user.savePortfolio();
                    System.out.println("Thank you for using the platform!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 6);

        sc.close();
    }

    static Stock findStock(ArrayList<Stock> stocks,
                           String symbol) {

        for (Stock stock : stocks) {
            if (stock.symbol.equalsIgnoreCase(symbol)) {
                return stock;
            }
        }

        return null;
    }
}
