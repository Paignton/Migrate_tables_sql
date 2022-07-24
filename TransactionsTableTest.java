package sait.boi.test;

import sait.boi.contracts.*;
import sait.boi.drivers.*;
import sait.boi.helpers.*;
import sait.boi.models.Transaction;

import org.junit.*;

import java.sql.*;
import java.util.*;

/**
 * Tests the transactions table is correct.
 * @author Nick Hamnett
 * @version Aug 9, 2021
 */
public class TransactionsTableTest {
    private DatabaseDriver driver;

    /**
     * Initializes driver and connects to database.
     * @throws ClassNotFoundException Thrown if MariaDB JAR file is missing.
     * @throws SQLException Thrown if unable to connect to database.
     */
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        this.driver = new MySQLDriver();

        this.driver.connect();
    }

    /**
     * Disconnects from database.
     * @throws SQLException Thrown if an error occurs closing connection.
     */
    @After
    public void tearDown() throws SQLException {
        this.driver.disconnect();
    }

    /**
     * Tests the transactions table has the correct structure (columns)
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testTransactionsTable() throws SQLException {
        ResultSet resultSet = this.driver.get("SELECT * FROM transactions");

        ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet);

        Assert.assertEquals(4, resultSetHelper.getTotalColumns());
        Assert.assertArrayEquals(new String[] { "card_number", "type", "amount", "datetime" }, resultSetHelper.getColumnNames());
    }

    /**
     * Tests the transactions table is populated with correct data.
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testTransactionsAll() throws SQLException {
        ArrayList<Transaction> transactions = this.driver.findTransactions();

        // Should be 213 records
        Assert.assertEquals(213, transactions.size());

        // Check first record card number is 5555555555554444, type is D, and amount is 61.00
        Transaction first = transactions.get(0);

        Assert.assertEquals(5555555555554444L, first.getCardNumber());
        Assert.assertEquals(Transaction.TYPE_DEPOSIT, first.getType());
        Assert.assertEquals(6100, first.getAmountPennies());

        // Check last record card number is 1234123412341234, type is W, and amount is 47.76
        Transaction last = transactions.get(transactions.size() - 1);

        Assert.assertEquals(1234123412341234L, last.getCardNumber());
        Assert.assertEquals(Transaction.TYPE_WITHDRAW, last.getType());
        Assert.assertEquals(4776, last.getAmountPennies());
        
        // Check transactions total is 1200991 pennies
        long total = 0;
        
        for (Transaction transaction : transactions) {
        	total += transaction.getAmountPennies();
        }
        
        Assert.assertEquals(1200974, total);
    }

    /**
     * Tests finding transactions for a card number.
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testTransactionsForCardFound() throws SQLException {
        ArrayList<Transaction> transactions = this.driver.findTransactionsForCardNumber(4012888888881881L);

        // Check 26 records were found.
        Assert.assertEquals(26, transactions.size());
        
        // Check first record card number is 4012888888881881, type is D, and amount is 88.41
        Transaction first = transactions.get(0);

        Assert.assertEquals(4012888888881881L, first.getCardNumber());
        Assert.assertEquals(Transaction.TYPE_DEPOSIT, first.getType());
        Assert.assertEquals(8841, first.getAmountPennies());

        // Check last record card number is 4012888888881881, type is D, and amount is 31.96
        Transaction last = transactions.get(transactions.size() - 1);

        Assert.assertEquals(4012888888881881L, last.getCardNumber());
        Assert.assertEquals(Transaction.TYPE_DEPOSIT, last.getType());
        Assert.assertEquals(3196, last.getAmountPennies());
        
        // Check transactions total is 166079 pennies
        long total = 0;
        
        for (Transaction transaction : transactions) {
        	total += transaction.getAmountPennies();
        }
        
        Assert.assertEquals(166079, total);
    }

    /**
     * Tests not finding transactions for a card number.
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testTransactionsForCardNotFound() throws SQLException {
        ArrayList<Transaction> transactions = this.driver.findTransactionsForCardNumber(4321432143214321L);

        // Check no records were found.
        Assert.assertTrue(transactions.isEmpty());
    }
    
    
}
