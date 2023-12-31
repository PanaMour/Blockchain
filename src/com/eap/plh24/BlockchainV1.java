package com.eap.plh24;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

public class BlockchainV1 {
    private Connection connection;

    public BlockchainV1() {
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

    public void showMenu() {
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

            newBlock.mineBlock(1);
            insertBlockIntoDatabase(newBlock);

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

    private void insertBlockIntoDatabase(Block block) throws SQLException {
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

    private void addMultipleProducts() {
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
            addProduct(); // Call addProduct method to handle each product addition
        }
    }

    private void searchForProduct() {
        // Implementation to search for a product
    }

    private void viewProductStatistics() {
        // Implementation to view statistics of a product
    }

    public static void main(String[] args) {
        BlockchainV1 app = new BlockchainV1();
        app.showMenu();
    }
}
