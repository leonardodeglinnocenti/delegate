package businessLogic;

import dao.SQLiteApartmentDAO;
import dao.SQLiteCustomerDAO;
import dao.SQLiteReservationDAO;
import dao.SQLiteRoomDAO;
import domainModel.Accommodation;
import domainModel.Apartment;
import org.junit.jupiter.api.Test;

// Apache Commons CSV
import org.apache.commons.csv.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountingHandlerTest {

    @Test
    void ifEndDateIsBeforeStartDate_ThenExceptionIsThrown() throws Exception {
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), CustomerBook.getInstance(new SQLiteCustomerDAO()));
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);
        boolean check = accountingHandler.addLocalTax("Test", 10, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2019, 1, 1));
        assertFalse(check);
    }

    void ifTargetIsNotValid_ThenExceptionIsThrown() {
        ReservationHandler reservationHandler = ReservationHandler.getInstance(new SQLiteReservationDAO(), CustomerBook.getInstance(new SQLiteCustomerDAO()));
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);
        assertThrows(Exception.class, () -> {
            accountingHandler.addLocalTax("Test", 10, "not_valid", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1));
        });
    }

    @Test
    void evaluateLocalTaxes() {
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

        // Import from test Airbnb files
        try {
            reservationHandler.importFromAirbnb(apartment, "test/businessLogic/airbnb_tax_test.csv", "test/businessLogic/reservations_test.csv");
        } catch (Exception e) {
            fail();
        }

        // Evaluate local taxes
    }

    @Test
    void evaluateCityTaxMonthlyDeclaration() {
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
            CSVParser parser = CSVParser.parse(new java.io.FileReader("cityTax_1_4_2023.csv"), CSVFormat.DEFAULT.withFirstRecordAsHeader());
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