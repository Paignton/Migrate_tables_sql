package sait.boi.test;

import sait.boi.contracts.*;
import sait.boi.drivers.*;
import sait.boi.helpers.*;
import sait.boi.models.Account;

import org.junit.*;

import java.sql.*;
import java.util.*;

/**
 * Tests the accounts table is correct.
 * @author Nick Hamnett
 * @version Aug 9, 2021
 */
public class AccountsTableTest {
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
     * Tests the accounts table has the correct structure (columns)
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testAccountsTable() throws SQLException {
        ResultSet resultSet = this.driver.get("SELECT * FROM accounts");

        ResultSetHelper resultSetHelper = new ResultSetHelper(resultSet);

        Assert.assertEquals(2, resultSetHelper.getTotalColumns());
        Assert.assertArrayEquals(new String[] { "card_number", "pin" }, resultSetHelper.getColumnNames());
    }

    /**
     * Tests the accounts table is populated with correct data.
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testAccountsAll() throws SQLException {
        ArrayList<Account> accounts = this.driver.findAccounts();

        // Should be 7 records
        Assert.assertEquals(7, accounts.size());

        // Check first record card number is 1234123412341234 and PIN is 1234
        Account first = accounts.get(0);

        Assert.assertEquals(1234123412341234L, first.getCardNumber());
        Assert.assertEquals(1234, first.getPin());

        // Check last record card number is 5555555555554444 and PIN is 1010
        Account last = accounts.get(6);

        Assert.assertEquals(5555555555554444L, last.getCardNumber());
        Assert.assertEquals(1010, last.getPin());
    }

    /**
     * Tests finding a single Account record.
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testAccountSingleFound() throws SQLException {
        Account account = this.driver.findAccount(4222222222222222L);

        // Check account was found
        Assert.assertNotNull(account);

        // Check account card number is 4222222222222222 and PIN is 1212
        Assert.assertEquals(4222222222222222L, account.getCardNumber());
        Assert.assertEquals(1212, account.getPin());
    }

    /**
     * Tests unable to find a single Account record.
     * @throws SQLException Thrown if unable to perform SQL statement.
     */
    @Test
    public void testAccountSingleNotFound() throws SQLException {
        Account account = this.driver.findAccount(1111222233334444L);

        // Check account was not found
        Assert.assertNull(account);
    }
    
    
}
