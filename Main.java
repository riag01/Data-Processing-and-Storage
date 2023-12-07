import java.util.HashMap;
import java.util.Map;

// Define the MemoryDB interface
interface MemoryDB {
    void begin_transaction();  // Start a new transaction
    void put(String key, int val);  // Store a key-value pair
    int get(String key);  // Retrieve the value associated with a key
    void commit();  // Apply changes made within a transaction
    void rollback();  // Revert changes made within a transaction
}

// Implement the MemoryDB interface in the DBImpl class
class DBImpl implements MemoryDB {
    private Map<String, Integer> data;  // Main data store
    private Map<String, Integer> transactionData;  // Store changes made within a transaction
    private boolean isTransaction;  // Flag to track if a transaction is in progress

    // Constructor to initialize data structures
    public DBImpl() {
        data = new HashMap<>();
        transactionData = new HashMap<>();
        isTransaction = false;
    }

    // Retrieve the value associated with a key (within a transaction if applicable)
    @Override
    public int get(String key) {
        return transactionData.getOrDefault(key, -1);
    }

    // Store a key-value pair within a transaction
    @Override
    public void put(String key, int val) {
        if (!isTransaction) {
            throw new IllegalStateException("Transaction is not in progress");
        }
        data.put(key, val);
        System.out.println("Set value of " + key + " to " + val);
    }

    // Start a new transaction
    @Override
    public void begin_transaction() {
        if (isTransaction) {
            throw new IllegalStateException("Transaction is already in progress");
        }
        transactionData.clear();  // Clear the transaction data store
        data.clear();  // Clear the main data store
        isTransaction = true;
        System.out.println("Transaction started");
    }

    // Apply changes made within a transaction to the main data store
    @Override
    public void commit() throws IllegalStateException {
        if (!isTransaction) {
            throw new IllegalStateException("No open transaction to commit");
        }
        transactionData.putAll(data);  // Copy changes to the main data store
        isTransaction = false;
        System.out.println("Transaction committed");
    }

    // Revert changes made within a transaction
    @Override
    public void rollback() throws IllegalStateException {
        if (!isTransaction) {
            throw new IllegalStateException("No transaction to rollback");
        }
        transactionData.clear();  // Clear changes made within the transaction
        isTransaction = false;
        System.out.println("Transaction rolled back");
    }
}

// Main class to demonstrate the usage of the in-memory database
public class Main {
    public static void main(String[] args) {
        MemoryDB inMemoryDB = new DBImpl();
        System.out.println(inMemoryDB.get("A"));  // should return null

        try {
            inMemoryDB.put("A", 5);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        inMemoryDB.begin_transaction();
        inMemoryDB.put("A", 5);
        System.out.println(inMemoryDB.get("A"));

        inMemoryDB.put("A", 6);
        inMemoryDB.commit();
        System.out.println(inMemoryDB.get("A"));

        try {
            inMemoryDB.commit();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            inMemoryDB.rollback();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println(inMemoryDB.get("B"));
        inMemoryDB.begin_transaction();
        inMemoryDB.put("B", 10);
        inMemoryDB.rollback();
        System.out.println(inMemoryDB.get("B"));
    }
}