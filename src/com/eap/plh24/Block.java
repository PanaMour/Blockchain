package com.eap.plh24;

import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class Block {
    private List<Product> products;
    private String previousHash;
    private String blockHash;
    private long timestamp;
    private int nonce;

    public Block(String previousHash) {
        this.products = new ArrayList<>();
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
        this.blockHash = calculateBlockHash();
    }

    public void addProduct(Product product) {
        product.setRegistrationNumber(products.size() + 1);
        product.setPreviousRegistrationNumber(products.isEmpty() ? -1 : products.size());
        products.add(product);
        this.blockHash = mineBlock(4); // Update block hash after adding a product
    }

    private String calculateBlockHash() {
        StringBuilder dataToHash = new StringBuilder();
        dataToHash.append(previousHash)
                .append(String.valueOf(timestamp));

        for (Product product : products) {
            dataToHash.append(product.toString());
        }

        dataToHash.append(String.valueOf(nonce));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(dataToHash.toString().getBytes("UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!blockHash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            blockHash = calculateBlockHash();
        }
        return blockHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Product> getProducts() {
        return products;
    }
}
