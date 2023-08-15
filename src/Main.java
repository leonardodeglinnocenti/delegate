import businessLogic.*;
import dao.*;
import domainModel.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {

        // Initialize database
        Database.setDatabase("base.db");
        Database.initDatabase();

        // Instantiate SQLite DAOs
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        RoomDAO roomDAO = new SQLiteRoomDAO();
        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        CustomerDAO customerDAO = new SQLiteCustomerDAO();

        // Instantiate CustomerBook
        CustomerBook customerBook = CustomerBook.getInstance(customerDAO);

        // Instantiate AccommodationHandler
        AccommodationHandler accommodationHandler = AccommodationHandler.getInstance(apartmentDAO, roomDAO);

        // Instantiate ReservationHandler
        ReservationHandler reservationHandler = ReservationHandler.getInstance(reservationDAO, customerBook);

        // Instantiate AccountingHandler
        AccountingHandler accountingHandler = AccountingHandler.getInstance(reservationHandler);

        // Create an accommodation
        Accommodation accommodation = accommodationHandler.createAccommodation("apartment", "Cosy apt", 6);
        accommodationHandler.addApartmentDetails(accommodation, 4, 1, 2, 3);
        Apartment apartmentPointer = apartmentDAO.get(accommodation.getId());
        reservationHandler.importFromAirbnb(apartmentPointer, "test/businessLogic/airbnb_tax_test.csv", "test/businessLogic/reservations_test.csv");
        // reservationHandler.importFromAirbnb(apartmentPointer, "/home/leonardo/Downloads/test_tax.csv", "/home/leonardo/Downloads/test_res.csv");

        // Print all reservations
        ArrayList<Reservation> reservations = reservationHandler.getAllReservations();
        for (Reservation reservation : reservations) {
            reservation.printReservation();
        }

        // Print all customers
        ArrayList<Customer> customers = customerBook.getAllCustomers();
        for (Customer customer : customers) {
            customer.printCustomer();
        }

        // Add city tax
        accountingHandler.addLocalTax("Florence", 5.5, "adults", 7, LocalDate.of(2023, 4, 1), LocalDate.of(2030, 12, 31));
        accountingHandler.addLocalTax("Florence", 5.5, "children", 7, LocalDate.of(2023, 4, 1), LocalDate.of(2030, 12, 31));
        accountingHandler.addLocalTax("Florence", 5.5, "infants", 7, LocalDate.of(2023, 4, 1), LocalDate.of(2030, 12, 31));

        accountingHandler.addLocalTax("Florence", 4, "adults", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2023, 3, 31));
        accountingHandler.addLocalTax("Florence", 4, "children", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2023, 3, 31));
        accountingHandler.addLocalTax("Florence", 4, "infants", 7, LocalDate.of(2020, 1, 1), LocalDate.of(2023, 3, 31));

        // Create some customers
        Customer customerPointer = customerBook.addCustomer("Dymphna O'Connor", "Reginald St. London", "123456789");
        customerBook.addCustomer("Lewis Devis", "223 Baker St.", "987654321");
        customerBook.addCustomer("Sherlock Holmes", "222b Baker St.", "76100100");

        // Add a reservation in between the city tax change
        Reservation reservationPointer = reservationHandler.addReservation(apartmentPointer, LocalDate.of(2024, 3, 29), LocalDate.of(2024, 4, 3), 4, 2, 0, customerPointer, 160, 0);
        accountingHandler.evaluateLocalTaxes(reservationPointer);

        // Add Dymphna reservation
        // Reservation reservationPointer = reservationHandler.addReservation(apartmentPointer, LocalDate.of(2023, 5, 20), LocalDate.of(2023, 5, 27), 4, 0, 0, customerPointer, 742, 0);
        // accountingHandler.evaluateLocalTaxes(reservationPointer);

        // Evaluate the city tax using the accounting handler
        accountingHandler.evaluateCityTaxMonthlyDeclaration(apartmentPointer, 4, 2023);

        Accommodation accommodationTest = accommodationHandler.createAccommodation("apartment", "ciao", 13);
        accommodationHandler.addApartmentDetails(accommodationTest, 6, 1, 0, 7);
        if (accommodationTest instanceof Apartment apartment) {
            apartment.printApartment();
        } else if (accommodationTest instanceof Room room) {
            room.printRoom();
        }

        Accommodation accommodation3 = accommodationHandler.createAccommodation("room", "ciao", 13);
        accommodationHandler.addRoomDetails(accommodation3, true, true);
        if (accommodation3 instanceof Apartment apartment) {
            apartment.printApartment();
        } else if (accommodation3 instanceof Room room) {
            room.printRoom();
        }

    }

}