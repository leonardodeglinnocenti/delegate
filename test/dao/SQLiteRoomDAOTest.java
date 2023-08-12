package dao;

import domainModel.Room;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteRoomDAOTest {

    @org.junit.jupiter.api.BeforeAll
    static void initDb() throws Exception {
        // Set up database
        Database.setDatabase("test.db");
        Database.initDatabase();
    }

    @org.junit.jupiter.api.BeforeEach
    void init() throws Exception {
        // Set up database
        Database.setDatabase("test.db");
        Database.initDatabase();

        RoomDAO roomDAO = new SQLiteRoomDAO();
        Room room = new Room(-1, "Room 1", 2);
        roomDAO.insert(room);
    }

    @org.junit.jupiter.api.Test
    void getNextId() throws Exception {
        RoomDAO roomDAO = new SQLiteRoomDAO();
        int nextId = roomDAO.getNextId();
        try {
            Room room = roomDAO.get(nextId-1);
            assertEquals(nextId-1, room.getId());
            assertEquals("Room 1", room.getDescription());
            assertEquals(2, room.getMaxGuestsAllowed());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void insert() throws Exception {
        // Test that the room inserted is the same as the one retrieved using the get() method
        RoomDAO roomDAO = new SQLiteRoomDAO();
        int nextId = roomDAO.getNextId();
        try {
            Room room = new Room(nextId-1, "Room 2", 4);
            roomDAO.insert(room);
            Room retrievedRoom = roomDAO.get(room.getId());
            assertEquals(room.getId(), retrievedRoom.getId());
            assertEquals("Room 2", retrievedRoom.getDescription());
            assertEquals(4, retrievedRoom.getMaxGuestsAllowed());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void update() throws Exception {
        // Test that the room updated is the same as the one retrieved using the get() method
        RoomDAO roomDAO = new SQLiteRoomDAO();
        int nextId = roomDAO.getNextId();
        try {
            Room room = new Room(nextId-1, "Room 2", 4);
            roomDAO.update(room);
            Room retrievedRoom = roomDAO.get(room.getId());
            assertEquals(room.getId(), retrievedRoom.getId());
            assertEquals("Room 2", retrievedRoom.getDescription());
            assertEquals(4, retrievedRoom.getMaxGuestsAllowed());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void delete() throws Exception {
        // Test that the room deleted is not the same as the one retrieved using the get() method
        RoomDAO roomDAO = new SQLiteRoomDAO();
        int nextId = roomDAO.getNextId();
        try {
            Room room = new Room(nextId-1, "Room 2", 4);
            roomDAO.insert(room);
            roomDAO.delete(room.getId());
            Room retrievedRoom = roomDAO.get(room.getId());
            assertNull(retrievedRoom);
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void get() throws Exception {
        // Test that the room retrieved is the same as the one inserted
        RoomDAO roomDAO = new SQLiteRoomDAO();
        int nextId = roomDAO.getNextId();
        try {
            Room room = new Room(nextId-1, "Room 2", 4);
            roomDAO.insert(room);
            Room retrievedRoom = roomDAO.get(room.getId());
            assertEquals(room.getId(), retrievedRoom.getId());
            assertEquals("Room 2", retrievedRoom.getDescription());
            assertEquals(4, retrievedRoom.getMaxGuestsAllowed());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void getAll() throws Exception {
        // Test that the rooms retrieved are the same as the ones inserted
        RoomDAO roomDAO = new SQLiteRoomDAO();
        int nextId = roomDAO.getNextId();
        try {
            Room room1 = new Room(nextId-1, "Room 2", 4);
            Room room2 = new Room(nextId, "Room 3", 4);
            roomDAO.insert(room1);
            roomDAO.insert(room2);
            assertEquals(3, roomDAO.getAll().size());
        } catch (Exception e) {
            fail();
        }
    }

}