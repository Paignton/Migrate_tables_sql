package sait.boi.migrations;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;

import sait.boi.drivers.MySQLDriver;
import sait.boi.models.Account;
import sait.boi.models.Transaction;

/**
 * Populates the transactions table from the supplied transactions random access file.
 * @author Paignton Wild
 * @version Dec 7, 2021
 *
 */
public class MigrateTransactionsTable {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
    	// Open connection to database using driver
    	MySQLDriver driver = new MySQLDriver();
        driver.connect();

        // Open RandomAccessFile
        RandomAccessFile raf = new RandomAccessFile("res/transactions.bin", "r");

        // Calculate the RECORD_SIZE
        final int RECORD_SIZE = 8 + 2 + 8 + 21;
        
        // Loop through each record
        for (int i = 0; i < raf.length(); i += RECORD_SIZE) {
            // Read record values
            long cardNumber = raf.readLong();
            char type = raf.readChar();
            double amount = raf.readDouble();
            String dateTime = raf.readUTF();

            Transaction transaction = new Transaction(cardNumber,  type,  amount, dateTime);

            System.out.println("Inserting transactions into: " + transaction);

            // Add record to accounts table.
            driver.insertTransaction(transaction);
        }
        
        // Close Random Access File
        raf.close();

        // Disconnect from database
        driver.disconnect();
 
    }


}
