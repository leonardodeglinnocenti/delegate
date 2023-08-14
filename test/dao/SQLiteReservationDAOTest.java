package dao;

import domainModel.Apartment;
import domainModel.Customer;
import domainModel.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteReservationDAOTest {

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

        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        Apartment apartment = new Apartment(-1, "Apartment 1", 4, 2, 1, 1, 2);
        apartmentDAO.insert(apartment);

        CustomerDAO customerDAO = new SQLiteCustomerDAO();
        Customer customer = new Customer(-1, "John", "", "");
        customerDAO.insert(customer);

        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        Reservation reservation = new Reservation(-1, apartmentDAO.get(apartment.getId()), LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 2), 4, 2, 1, customer, 2, LocalDate.now(), 1);
        reservationDAO.insert(reservation);
    }

    @Test
    void testFindReservationByPeriod() throws Exception {
        // Test that a reservation is found when it exists
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        Apartment apartment = new Apartment(-1, "Apartment 1", 4, 2, 1, 1, 2);
        apartmentDAO.insert(apartment);
        reservationDAO.insert(new Reservation(-1, apartment, LocalDate.of(2023, 12, 1), LocalDate.of(2024, 1, 14), 4, 2, 1, new Customer(-1, "John", "", ""), 2, LocalDate.now(), 1));
        Reservation reservation = reservationDAO.findReservationByPeriod(apartment, LocalDate.of(2023, 12, 1), LocalDate.of(2024, 1, 14));
        assertNotNull(reservation);
    }

    @org.junit.jupiter.api.Test
    void When_CustomerIsDeleted_Expect_AllReservationsForThatCustomerAreDeleted() throws Exception {
        // Test that all reservations for the customer are deleted when the customer is deleted
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        CustomerDAO customerDAO = new SQLiteCustomerDAO();
        int nextId = apartmentDAO.getNextId();
        try {
            // Insert a reservation for the apartment
            Customer customer = new Customer(-1, "John", "", "");
            Apartment apartment = apartmentDAO.get(nextId-1);
            Reservation reservation = new Reservation(-1, apartmentDAO.get(apartment.getId()), LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 2), 4, 2, 1, customer, 2, LocalDate.now(), 1);
            reservationDAO.insert(reservation);
            // Delete the customer
            customerDAO.delete(customer.getId());
            // Check that the reservation is deleted
            assertNull(reservationDAO.get(reservation.getId()));
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void When_ApartmentIsDeleted_Expect_AllReservationsForThatApartmentAreDeleted() throws Exception {
        // Test that all reservations for the apartment are deleted when the apartment is deleted
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        CustomerDAO customerDAO = new SQLiteCustomerDAO();
        int nextId = apartmentDAO.getNextId();
        try {
            // Insert a reservation for the apartment
            Customer customer = new Customer(-1, "John", "", "");
            Apartment apartment = apartmentDAO.get(nextId-1);
            Reservation reservation = new Reservation(-1, apartmentDAO.get(apartment.getId()), LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 2), 4, 2, 1, customer, 2, LocalDate.now(), 1);
            reservationDAO.insert(reservation);
            // Delete the apartment
            apartmentDAO.delete(nextId-1);
            customerDAO.delete(customer.getId());
            // Check that the reservation is deleted
            assertNull(reservationDAO.get(reservation.getId()));
        } catch (Exception e) {
            fail();
        }
    }
}