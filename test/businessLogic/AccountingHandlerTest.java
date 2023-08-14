package businessLogic;

import dao.*;
import domainModel.Accommodation;
import domainModel.Apartment;
import domainModel.Reservation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Apache Commons CSV
import org.apache.commons.csv.*;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountingHandlerTest {

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

    @Test
    void When_EndDateIsBeforeStartDate_Expect_Exception() throws Exception {
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), CustomerBook.getInstance(new SQLiteCustomerDAO()));
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);
        boolean check = accountingHandler.addLocalTax("Test", 10, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2019, 1, 1));
        assertFalse(check);
    }

    @Test
    void When_TargetIsNotValid_Expect_False() throws Exception {
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), CustomerBook.getInstance(new SQLiteCustomerDAO()));
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);
        boolean test = accountingHandler.addLocalTax("Test", 10, "not_valid", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1));
        assertFalse(test);
    }

    @Test
    void testEvaluateLocalTaxes() throws Exception {
        CustomerBook customerBook = CustomerBook.getInstance(new SQLiteCustomerDAO());
        AccommodationHandler accommodationHandler = AccommodationHandler.getInstance(new SQLiteApartmentDAO(), new SQLiteRoomDAO());
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), customerBook);
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);

        // Create an apartment
        Accommodation apartment = accommodationHandler.createAccommodation("apartment", "Test", 2);
        Apartment check = accommodationHandler.addApartmentDetails(apartment, 4, 2, 1, 1);
        assertNotNull(check);

        // Create local taxes
        try {
            accountingHandler.addLocalTax("Test1", 3, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
            accountingHandler.addLocalTax("Test2", 3, "adults", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
            accountingHandler.addLocalTax("Test3", 2.5, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
            accountingHandler.addLocalTax("Test4", 2.5, "adults", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
        } catch (Exception e) {
            fail();
        }

        // Create a reservation
        Reservation reservation = reservationHandler.addReservation(apartment, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 12), 4, 2, 1, customerBook.getCustomer(1), 2, 1);

        // Evaluate local taxes
        accountingHandler.evaluateLocalTaxes(reservation);

        // Check that the local taxes were correctly evaluated
        assertEquals(reservation.getCityTaxAmount(), 115.5);

    }

    @Test
    void testEvaluateCityTaxMonthlyDeclaration() {
        CustomerBook customerBook = CustomerBook.getInstance(new SQLiteCustomerDAO());
        AccommodationHandler accommodationHandler = AccommodationHandler.getInstance(new SQLiteApartmentDAO(), new SQLiteRoomDAO());
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), customerBook);
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);

        // Create an apartment
        Accommodation accommodation = accommodationHandler.createAccommodation("apartment", "Test", 2);
        Apartment apartment = accommodationHandler.addApartmentDetails(accommodation, 4, 2, 1, 1);

        // Create local taxes
        try {
            accountingHandler.addLocalTax("Test1", 3, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
            accountingHandler.addLocalTax("Test2", 3, "adults", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
            accountingHandler.addLocalTax("Test3", 2.5, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
            accountingHandler.addLocalTax("Test4", 2.5, "adults", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2031, 1, 1));
        } catch (Exception e) {
            fail();
        }

        // Import from test Airbnb files
        try {
            reservationHandler.importFromAirbnb(apartment, "test/businessLogic/airbnb_tax_test.csv", "test/businessLogic/reservations_test.csv");
        } catch (Exception e) {
            fail();
        }

        // Evaluate monthly declaration
        try {
            accountingHandler.evaluateCityTaxMonthlyDeclaration(apartment, 4, 2023);
        } catch (Exception e) {
            fail();
        }

        // Open the file and check that the declaration is correct using Apache Commons CSV
        try {
            // Open CSV file and parse it
            CSVParser parser = CSVParser.parse(new java.io.FileReader("cityTax_ID_"+apartment.getId()+"_for_4_2023.csv"), CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord csvRecord : parser) {
                assertEquals("440.0", csvRecord.get("TOTAL CITY TAX AMOUNT"));
                assertEquals("24", csvRecord.get("TOTAL GUESTS"));
                assertEquals("80", csvRecord.get("TOTAL NIGHTS"));
                assertEquals("440.0", csvRecord.get("EVALUATED CITY TAX"));
            }
        } catch (Exception e) {
            fail();
        }

    }
}