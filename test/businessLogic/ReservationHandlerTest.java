package businessLogic;

import dao.*;
import domainModel.Apartment;
import domainModel.Customer;
import domainModel.Reservation;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationHandlerTest {

    @BeforeAll
    static void initDb() throws Exception {
        // Set up database
        Database.setDatabase("test.db");
        Database.initDatabase();
    }

    @org.junit.jupiter.api.Test
    void When_AccommodationIsNotAvailable_Expect_ReservationIsNotInserted() throws Exception {
        // Test that if the accommodation is not available, the reservation is not inserted
        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        CustomerDAO customerDAO = new SQLiteCustomerDAO();
        CustomerBook customerBook = CustomerBook.getInstance(customerDAO);

        ReservationHandler reservationHandler = ReservationHandler.getInstance(reservationDAO, customerBook);

        // Insert a reservation for the apartment
        Customer customer = new Customer(-1, "John", "", "");
        Apartment apartment = new Apartment(-1, "Apartment 1", 4, 2, 1, 1, 2);
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        apartmentDAO.insert(apartment);
        Reservation checkOk = reservationHandler.addReservation(apartment, LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 2), 4, 2, 1, customer, 2,1);

        // Insert a reservation for the same apartment for an intersecting period
        Reservation checkNotOk = reservationHandler.addReservation(apartment, LocalDate.of(2029, 12, 31), LocalDate.of(2030, 1, 4), 4, 2, 1, customer, 2,1);

        // Check that the reservation was not inserted and the previous one was
        assertNull(checkNotOk);
        assertNotNull(checkOk);
    }

    @org.junit.jupiter.api.Test
    void testImportFromAirbnb() throws Exception {
        // Create an apartment
        Apartment apartment = new Apartment(-1, "Apartment 1", 4, 2, 1, 1, 2);
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        try {
            apartmentDAO.insert(apartment);
        } catch (Exception e) {
            fail();
        }
        // Import from test Airbnb files
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), CustomerBook.getInstance(new SQLiteCustomerDAO()));
        boolean check = reservationHandler.importFromAirbnb(apartment, "test/businessLogic/airbnb_tax_test.csv", "test/businessLogic/reservations_test.csv");
        // Get the files from the current directory
        // Check that the import was successful
        assertTrue(check);
    }

}