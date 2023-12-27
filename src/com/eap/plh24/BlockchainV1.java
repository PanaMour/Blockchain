package com.eap.plh24;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlockchainV1 {
    private List<Block> blockchain;
    private int registrationNumber;
    private static final String DATABASE_URL = "jdbc:sqlite:blockchain.db";

    public BlockchainV1() {
        this.blockchain = new ArrayList<>();
        this.registrationNumber = 1;
        // Create the genesis block
        createGenesisBlock();
        initializeDatabase();
    }

    private void createGenesisBlock() {
        Block genesisBlock = new Block("0"); // Previous hash for the genesis block
        blockchain.add(genesisBlock);
    }
    private void initializeDatabase() {
        try (Connection connection = connect()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS PRODUCTS " +
                    "(ID INTEGER NOT NULL PRIMARY KEY," +
                    "REGISTRATION_NUMBER INTEGER," +
                    "PRODUCT_ID INTEGER," +
                    "PRODUCT_TITLE VARCHAR(50)," +
                    "TIMESTAMP BIGINT," +
                    "PRICE DOUBLE," +
                    "DESCRIPTION VARCHAR(255)," +
                    "CATEGORY VARCHAR(50)," +
                    "PREVIOUS_REGISTRATION_NUMBER INTEGER)";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addProductSequential(Product product) {
        // Increment registration number and set it for the product
        product.setRegistrationNumber(registrationNumber++);

        // Add the product to the latest block
        Block latestBlock = blockchain.get(blockchain.size() - 1);
        latestBlock.addProduct(product);

        saveProductToDatabase(product);

        // Create a new block if the current block is full or if the product is the first in the blockchain
        if (latestBlock.getProducts().size() >= 5 || latestBlock.getProducts().isEmpty()) {
            createNewBlock();
        }
    }

    private void createNewBlock() {
        Block newBlock = new Block(blockchain.get(blockchain.size() - 1).getBlockHash());
        blockchain.add(newBlock);
    }
    private void saveProductToDatabase(Product product) {
        try (Connection connection = connect()) {
            String insertSQL = "INSERT INTO PRODUCTS " +
                    "(REGISTRATION_NUMBER, PRODUCT_ID, PRODUCT_TITLE, TIMESTAMP, PRICE, DESCRIPTION, CATEGORY, PREVIOUS_REGISTRATION_NUMBER) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setInt(1, product.getRegistrationNumber());
                preparedStatement.setInt(2, product.getProductId());
                preparedStatement.setString(3, product.getProductTitle());
                preparedStatement.setLong(4, product.getTimestamp());
                preparedStatement.setDouble(5, product.getPrice());
                preparedStatement.setString(6, product.getDescription());
                preparedStatement.setString(7, product.getCategory());
                preparedStatement.setInt(8, product.getPreviousRegistrationNumber());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*public void viewAllProducts() {
        for (Block block : blockchain) {
            for (Product product : block.getProducts()) {
                System.out.println(product);
            }
        }
    }*/
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
    public void viewAllProductsFromDatabase() {
        try (Connection connection = connect()) {
            String selectSQL = "SELECT * FROM PRODUCTS";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)) {

                while (resultSet.next()) {
                    int registrationNumber = resultSet.getInt("REGISTRATION_NUMBER");
                    int productId = resultSet.getInt("PRODUCT_ID");
                    String productTitle = resultSet.getString("PRODUCT_TITLE");
                    long timestamp = resultSet.getLong("TIMESTAMP");
                    double price = resultSet.getDouble("PRICE");
                    String description = resultSet.getString("DESCRIPTION");
                    String category = resultSet.getString("CATEGORY");
                    int previousRegistrationNumber = resultSet.getInt("PREVIOUS_REGISTRATION_NUMBER");

                    Product product = new Product(productId, productTitle, price, description, category);
                    product.setRegistrationNumber(registrationNumber);
                    product.setTimestamp(timestamp);
                    product.setPreviousRegistrationNumber(previousRegistrationNumber);

                    System.out.println(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        BlockchainV1 blockchainV1 = new BlockchainV1();

        // Example: Add products sequentially
        blockchainV1.addProductSequential(new Product(101, "Product A", 50.0, "Description A", "Category X"));
        blockchainV1.addProductSequential(new Product(102, "Product B", 75.0, "Description B", "Category Y"));

        // Example: View all products
        blockchainV1.viewAllProductsFromDatabase();
    }
}
