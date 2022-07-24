package sait.boi.migrations;

import sait.boi.drivers.*;
import sait.boi.models.Account;

import java.io.*;
import java.sql.*;

/**
 * Migrates accounts from Random Access File to MySQL database.
 * @author Nick Hamnett
 * @version Aug 9, 2021
 */
public class MigrateAccountsTable {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        // Open connection to database using driver
    	MySQLDriver driver = new MySQLDriver();
        driver.connect();

        // Clear existing accounts
        // NOTE: This clears all transactions as well.
        System.out.println("Clearing all accounts and transactions.");
        driver.update("DELETE FROM accounts");

        // Open RandomAccessFile
        RandomAccessFile raf = new RandomAccessFile("res/accounts.bin", "r");

        // Calculate the RECORD_SIZE
        final int RECORD_SIZE = 8 + 4;

        // Loop through each record
        for (int i = 0; i < raf.length(); i += RECORD_SIZE) {
            // Read record values
            long cardNumber = raf.readLong();
            int pin = raf.readInt();

            Account account = new Account(cardNumber, pin);

            System.out.println("Inserting account: " + account);

            // Add record to accounts table.
            driver.insertAccount(account);
        }
        
        // Close Random Access File
        raf.close();

        // Disconnect from database
        driver.disconnect();
 
    }
}
