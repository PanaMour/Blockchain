package com.eap.plh24;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private String hash;
    private String previousHash;
    private String blockId; // Block ID
    private String title; // Block title
    private long timeStamp; // Timestamp for block creation
    private double price; // Price of the block
    private String description; // Description of the block
    private String category; // Category of the block
    private int nonce; // Nonce used in mining

    public Block(String blockId, String title, long timeStamp,
                 double price, String description, String category,
                 String previousHash) {
        this.blockId = blockId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateBlockHash();
    }

    public String calculateBlockHash() {
        String dataToHash = previousHash
                + blockId
                + title
                + Double.toString(price)
                + description
                + category
                + Long.toString(timeStamp)
                + Integer.toString(nonce);
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public String mineBlock(int prefix) {
        long startTime = System.currentTimeMillis();
        String target = new String(new char[prefix]).replace('\0', '0');

        while (!hash.substring(0, prefix).equals(target) || System.currentTimeMillis() - startTime < 60000) {
            nonce++;
            hash = calculateBlockHash();
        }

        return hash;
    }


    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public String getPreviousHash() {
        return previousHash;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getTitle() {
        return title;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
