package com.eap.plh24;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

public class BlockchainV3 {
    private Connection connection;

    public BlockchainV3() {
        try {
            String url = "jdbc:sqlite:blockchain.db";
            this.connection = DriverManager.getConnection(url);
            initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database: " + e.getMessage());
        }
    }
    private void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS blocks (" +
                "registration_number INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "block_id TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "timestamp LONG NOT NULL, " +
                "price DOUBLE NOT NULL, " +
                "description TEXT, " +
                "category TEXT, " +
                "previous_hash TEXT, " +
                "hash TEXT NOT NULL);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public void showMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("===================================");
            System.out.println("Blockchain Application Menu:");
            System.out.println("1. View All Products");
            System.out.println("2. Add a Product");
            System.out.println("3. Add Multiple Products");
            System.out.println("4. Search for a Product");
            System.out.println("5. View Statistics of a Product");
            System.out.println("6. Exit");
            System.out.println("===================================");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewAllProducts();
                    break;
                case 2:
                    addProduct();
                    break;
                case 3:
                    addMultipleProducts();
                    break;
                case 4:
                    searchForProduct();
                    break;
                case 5:
                    viewProductStatistics();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllProducts() {
        String query = "SELECT * FROM blocks";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Registration Number: " + rs.getInt("registration_number"));
                System.out.println("Block ID: " + rs.getString("block_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Timestamp: " + rs.getLong("timestamp"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Previous Hash: " + rs.getString("previous_hash"));
                System.out.println("Hash: " + rs.getString("hash"));
                System.out.println("------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving blocks: " + e.getMessage());
        }
    }

    private void addProduct() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter block ID: ");
            String blockId = scanner.nextLine();

            System.out.print("Enter block title: ");
            String title = scanner.nextLine();

            double price = 0;
            boolean validPrice = false;
            while (!validPrice) {
                System.out.print("Enter block price: ");
                try {
                    price = scanner.nextDouble();
                    validPrice = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a numeric value for price.");
                    scanner.nextLine();
                }
            }
            scanner.nextLine();

            System.out.print("Enter block description: ");
            String description = scanner.nextLine();

            System.out.print("Enter block category: ");
            String category = scanner.nextLine();

            String previousHash = getLastBlockHash();
            long timeStamp = System.currentTimeMillis();
            Block newBlock = new Block(blockId, title, timeStamp, price, description, category, previousHash);

            Thread thread = new Thread(() -> {
                newBlock.mineBlock(1);
                try {
                    insertBlockIntoDatabase(newBlock);
                    System.out.println("Product with ID " + newBlock.getBlockId() + " has been added to the database.");
                } catch (SQLException e) {
                    System.out.println("Error inserting block into database: " + e.getMessage());
                }
            });
            thread.start();

        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    private String getLastBlockHash() throws SQLException {
        String query = "SELECT hash FROM blocks ORDER BY registration_number DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("hash");
            } else {
                return "0"; // If no previous block, return "0" as the previous hash
            }
        }
    }

    private synchronized void insertBlockIntoDatabase(Block block) throws SQLException {
        String insertSQL = "INSERT INTO blocks (block_id, title, timestamp, price, description, category, previous_hash, hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, block.getBlockId());
            pstmt.setString(2, block.getTitle());
            pstmt.setLong(3, block.getTimeStamp());
            pstmt.setDouble(4, block.getPrice());
            pstmt.setString(5, block.getDescription());
            pstmt.setString(6, block.getCategory());
            pstmt.setString(7, block.getPreviousHash());
            pstmt.setString(8, block.getHash());
            pstmt.executeUpdate();
        }
    }

    private void addMultipleProducts() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("How many products do you want to add? ");
        int numberOfProducts = 0;
        while (numberOfProducts <= 0) {
            try {
                numberOfProducts = scanner.nextInt();
                if (numberOfProducts <= 0) {
                    System.out.println("Please enter a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                scanner.nextLine();
            }
        }
        scanner.nextLine();

        for (int i = 0; i < numberOfProducts; i++) {
            System.out.println("Entering details for product " + (i + 1) + ":");

            System.out.print("Enter block ID: ");
            String blockId = scanner.nextLine();

            System.out.print("Enter block title: ");
            String title = scanner.nextLine();

            System.out.print("Enter block price: ");
            double price = scanner.nextDouble();
            scanner.nextLine();

            System.out.print("Enter block description: ");
            String description = scanner.nextLine();

            System.out.print("Enter block category: ");
            String category = scanner.nextLine();

            String previousHash = getLastBlockHash();
            long timeStamp = System.currentTimeMillis();
            Block newBlock = new Block(blockId, title, timeStamp, price, description, category, previousHash);

            Thread thread = new Thread(() -> {
                newBlock.mineBlock(1);
                try {
                    insertBlockIntoDatabase(newBlock);
                    System.out.println("Product with ID " + newBlock.getBlockId() + " has been added to the database.");
                } catch (SQLException e) {
                    System.out.println("Error inserting block into database: " + e.getMessage());
                }
            });
            thread.start();
        }
    }

    private void searchForProduct() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Search by: ");
        System.out.println("1. Block ID");
        System.out.println("2. Title");
        System.out.println("3. Price");
        System.out.println("4. Category");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String field = "";
        switch (choice) {
            case 1:
                field = "block_id";
                break;
            case 2:
                field = "title";
                break;
            case 3:
                field = "price";
                break;
            case 4:
                field = "category";
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        System.out.print("Enter the value to search for: ");
        String value = scanner.nextLine();

        System.out.println("Do you want to view:");
        System.out.println("1. First appearance");
        System.out.println("2. Last appearance");
        System.out.println("3. All appearances");
        System.out.print("Enter your choice: ");
        int appearanceChoice = scanner.nextInt();
        scanner.nextLine();

        String query = constructSearchQuery(field, appearanceChoice);
        executeSearchQuery(query, value);
    }

    private String constructSearchQuery(String field, int appearanceChoice) {
        String baseQuery = "SELECT * FROM blocks WHERE " + field + " = ?";
        if (appearanceChoice == 1) {
            baseQuery += " ORDER BY registration_number ASC LIMIT 1";
        } else if (appearanceChoice == 2) {
            baseQuery += " ORDER BY registration_number DESC LIMIT 1";
        }
        return baseQuery;
    }

    private void executeSearchQuery(String query, String value) {
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) { // Check if the ResultSet is empty
                System.out.println("No blocks found with the specified criteria.");
                return;
            }

            while (rs.next()) {
                // Display each block's details
                System.out.println("Registration Number: " + rs.getInt("registration_number"));
                System.out.println("Block ID: " + rs.getString("block_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Timestamp: " + rs.getLong("timestamp"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Previous Hash: " + rs.getString("previous_hash"));
                System.out.println("Hash: " + rs.getString("hash"));
                System.out.println("------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error executing search query: " + e.getMessage());
        }
    }

    private void viewProductStatistics() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the block ID to view its price statistics: ");
        String blockId = scanner.nextLine();

        String query = "SELECT price, timestamp FROM blocks WHERE block_id = ? ORDER BY timestamp ASC";
        executeStatisticsQuery(query, blockId);
    }

    private void executeStatisticsQuery(String query, String blockId) {
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, blockId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No records found for the block ID: " + blockId);
                return;
            }

            System.out.println("Price Statistics for Block ID: " + blockId);
            while (rs.next()) {
                double price = rs.getDouble("price");
                long timestamp = rs.getLong("timestamp");
                String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
                System.out.println("Date: " + formattedDate + ", Price: " + price);
            }
        } catch (SQLException e) {
            System.out.println("Error executing statistics query: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing the database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        BlockchainV3 app = new BlockchainV3();
        try {
            app.showMenu();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            app.closeConnection();
        }
    }
}
