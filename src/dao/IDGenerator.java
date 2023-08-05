package dao;

public class IDGenerator {
    // Manage generation of unique IDs for each object
    private static int nextId = 0;

    public static int getNextId() {
        nextId++;
        return nextId;
    }
}
