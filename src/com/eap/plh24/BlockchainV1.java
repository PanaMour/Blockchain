package com.eap.plh24;

import java.util.ArrayList;
import java.util.List;

public class BlockchainV1 {
    private List<Block> blockchain;
    private int registrationNumber;

    public BlockchainV1() {
        this.blockchain = new ArrayList<>();
        this.registrationNumber = 1;
        // Create the genesis block
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesisBlock = new Block("0"); // Previous hash for the genesis block
        blockchain.add(genesisBlock);
    }

    public synchronized void addProductSequential(Product product) {
        // Increment registration number and set it for the product
        product.setRegistrationNumber(registrationNumber++);

        // Add the product to the latest block
        Block latestBlock = blockchain.get(blockchain.size() - 1);
        latestBlock.addProduct(product);

        // Create a new block if the current block is full or if the product is the first in the blockchain
        if (latestBlock.getProducts().size() >= 5 || latestBlock.getProducts().isEmpty()) {
            createNewBlock();
        }
    }

    private void createNewBlock() {
        Block newBlock = new Block(blockchain.get(blockchain.size() - 1).getBlockHash());
        blockchain.add(newBlock);
    }

    public void viewAllProducts() {
        for (Block block : blockchain) {
            for (Product product : block.getProducts()) {
                System.out.println(product);
            }
        }
    }

    // Add methods for searching and viewing statistics as needed
    // ...

    public static void main(String[] args) {
        BlockchainV1 blockchainV1 = new BlockchainV1();

        // Example: Add products sequentially
        blockchainV1.addProductSequential(new Product(101, "Product A", 50.0, "Description A", "Category X"));
        blockchainV1.addProductSequential(new Product(102, "Product B", 75.0, "Description B", "Category Y"));

        // Example: View all products
        blockchainV1.viewAllProducts();
    }
}
