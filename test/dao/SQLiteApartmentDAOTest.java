package dao;

import domainModel.Apartment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteApartmentDAOTest {

    @BeforeAll
    static void initDb() throws Exception {
        // Set up database
        Database.setDatabase("test.db");
        Database.initDatabase();
    }

    @BeforeEach
    void init() throws Exception {
        Connection connection = Database.getConnection();
        connection.prepareStatement("DELETE FROM Apartment").executeUpdate();

        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        Apartment apartment = new Apartment(-1, "Apartment 1", 4, 2, 1, 1, 2);
        apartmentDAO.insert(apartment);
    }

    @org.junit.jupiter.api.Test
    void testGetNextId() throws Exception {
        // Test that the next ID is greater than the current ID by 1
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        int currentId = apartmentDAO.getNextId();
        int nextId = apartmentDAO.getNextId();
        assertEquals(currentId + 1, nextId);
    }

    @org.junit.jupiter.api.Test
    void testGet() throws Exception {
        // Test that the apartment retrieved is the same as the one inserted using the init() method
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        int nextId = apartmentDAO.getNextId();
        try {
            Apartment apartment = apartmentDAO.get(nextId-1);
            assertEquals(nextId-1, apartment.getId());
            assertEquals("Apartment 1", apartment.getDescription());
            assertEquals(4, apartment.getMaxGuestsAllowed());
            assertEquals(2, apartment.getNumberOfRooms());
            assertEquals(1, apartment.getNumberOfBathrooms());
            assertEquals(1, apartment.getNumberOfBedrooms());
            assertEquals(2, apartment.getNumberOfBeds());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void testInsert() throws Exception {
        // Test that the apartment inserted is the same as the one retrieved using the get() method
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        int nextId = apartmentDAO.getNextId();
        try {
            Apartment apartment = new Apartment(nextId-1, "Apartment 2", 4, 2, 1, 1, 2);
            apartmentDAO.insert(apartment);
            Apartment retrievedApartment = apartmentDAO.get(apartment.getId());
            assertEquals(apartment.getId(), retrievedApartment.getId());
            assertEquals("Apartment 2", retrievedApartment.getDescription());
            assertEquals(4, retrievedApartment.getMaxGuestsAllowed());
            assertEquals(2, retrievedApartment.getNumberOfRooms());
            assertEquals(1, retrievedApartment.getNumberOfBathrooms());
            assertEquals(1, retrievedApartment.getNumberOfBedrooms());
            assertEquals(2, retrievedApartment.getNumberOfBeds());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void testUpdate() throws Exception {
        // Test that the apartment updated is the same as the one retrieved using the get() method
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        int nextId = apartmentDAO.getNextId();
        try {
            Apartment apartment = new Apartment(nextId-1, "Apartment 2", 4, 2, 1, 1, 2);
            apartmentDAO.update(apartment);
            Apartment retrievedApartment = apartmentDAO.get(apartment.getId());
            assertEquals(apartment.getId(), retrievedApartment.getId());
            assertEquals("Apartment 2", retrievedApartment.getDescription());
            assertEquals(4, retrievedApartment.getMaxGuestsAllowed());
            assertEquals(2, retrievedApartment.getNumberOfRooms());
            assertEquals(1, retrievedApartment.getNumberOfBathrooms());
            assertEquals(1, retrievedApartment.getNumberOfBedrooms());
            assertEquals(2, retrievedApartment.getNumberOfBeds());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void testDelete() throws Exception {
        // Test that the apartment is deleted
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        // Get the next ID to be used, so that we can delete the apartment inserted using the init() method
        int nextId = apartmentDAO.getNextId();
        try {
            apartmentDAO.delete(nextId-1);
            assertNull(apartmentDAO.get(nextId-1));
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void testGetAll() {
        // Test that the list of apartments retrieved is the same as the one inserted using the init() method
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        try {
            Apartment apartment = new Apartment(-1, "Apartment 2", 4, 2, 1, 1, 2);
            apartmentDAO.insert(apartment);
            assertEquals(2, apartmentDAO.getAll().size());
        } catch (Exception e) {
            fail();
        }
    }

}