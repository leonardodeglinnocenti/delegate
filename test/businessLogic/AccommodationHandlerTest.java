package businessLogic;

import dao.*;
import domainModel.Accommodation;
import domainModel.Apartment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class AccommodationHandlerTest {

    @BeforeAll
    static void initDb() throws Exception {
        // Set up database
        Database.setDatabase("test.db");
        Database.initDatabase();
    }

    @BeforeEach
    void init() throws Exception {
        // Query to delete all local taxes
        Connection connection = Database.getConnection();
        connection.createStatement().executeUpdate("DELETE FROM LocalTax");
        connection.createStatement().executeUpdate("DELETE FROM Reservation");
        connection.createStatement().executeUpdate("DELETE FROM Apartment");
        connection.close();
    }

    @org.junit.jupiter.api.Test
    void testCreateAccommodation() throws Exception {
        // Test that the accommodation created is the same as the one retrieved using the get() method
        AccommodationHandler accommodationHandler = AccommodationHandler.getInstance(new SQLiteApartmentDAO(), new SQLiteRoomDAO());
        try {
            Accommodation accommodation = accommodationHandler.createAccommodation("apartment", "Test", 2);
            accommodationHandler.addApartmentDetails(accommodation, 5, 1, 2, 3);
            // Use instanceof to check if the accommodation is an apartment and not a room
            Apartment apartment = (Apartment) accommodationHandler.getAccommodationById(accommodation.getId());
            assertEquals(accommodation.getId(), apartment.getId());
            assertEquals("Test", apartment.getDescription());
            assertEquals(2, apartment.getMaxGuestsAllowed());
            assertEquals(5, apartment.getNumberOfRooms());
            assertEquals(1, apartment.getNumberOfBathrooms());
            assertEquals(2, apartment.getNumberOfBedrooms());
            assertEquals(3, apartment.getNumberOfBeds());
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void When_InvalidAccommodationType_Expect_Exception() throws Exception {
        AccommodationHandler accommodationHandler = AccommodationHandler.getInstance(new SQLiteApartmentDAO(), new SQLiteRoomDAO());
        Accommodation accommodation = null;
        try {
            accommodation = accommodationHandler.createAccommodation("invalid", "Test", 2);
            fail();
        } catch (Exception e) {
            assertEquals("Invalid accommodation type.", e.getMessage());
        }
    }
}