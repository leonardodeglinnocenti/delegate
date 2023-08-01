package domainModel;

import businessLogic.*;
import dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception{

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

        // Create some accommodations
        accommodationHandler.createAccommodation("apartment", "Apartment 1", 4);
        accommodationHandler.createAccommodation("room","Room 2", 2);
        accommodationHandler.createAccommodation("apartment", "Apartment 2", 4);
        accommodationHandler.createAccommodation("apartment", "Apartment 3", 4);
        accommodationHandler.createAccommodation("room","Room 1", 2);

        // Create some customers
        customerBook.addCustomer("John Doe", "Reginald St. London", "123456789");
        customerBook.addCustomer("Lewis Devis", "223 Baker St.", "987654321");
        customerBook.addCustomer("Sherlock Holmes", "222b Baker St.", "76100100");

        // Get apartment and room ids
        ArrayList<Apartment> apartments = apartmentDAO.getAll();
        ArrayList<Room> rooms = roomDAO.getAll();

        // Display all accommodations
        System.out.println("Apartments:");
        for (Apartment apartment : apartments) {
            System.out.println(apartment.getId() + " " + apartment.getDescription() + " " + apartment.getMaxGuestsAllowed() + " " + apartment.getNumberOfRooms() + " " + apartment.getNumberOfBathrooms() + " " + apartment.getNumberOfBedrooms() + " " + apartment.getNumberOfBeds());
        }
        System.out.println("Rooms:");
        for (Room room : rooms) {
            System.out.println(room.getId() + " " + room.getDescription() + " " + room.getMaxGuestsAllowed());
        }
        System.out.println("Customers:");
        for (Customer customer : customerBook.getAllCustomers()) {
            System.out.println(customer.getId() + " " + customer.getName() + " " + customer.getAddress());
        }

        // Check all available apartments for a given period and number of guests
        ArrayList<Apartment> availableApartments = reservationHandler.getAvailableApartments(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 10), 4);

        // Select a random apartment from the available ones
        int randomApartmentIndex = (int) (Math.random() * availableApartments.size());
        Apartment randomApartment = availableApartments.get(randomApartmentIndex);

        // Select a random customer
        int randomCustomerIndex = (int) (Math.random() * customerBook.getAllCustomers().size());
        Customer randomCustomer = customerBook.getCustomer(randomCustomerIndex);

        // Create a reservation for the selected apartment
        reservationHandler.addReservation(randomApartment.getId(), LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 10), 4, 2, randomCustomer.getId(), 12.90);

        // Another guest wants to book the same apartment for an intersecting period
        availableApartments = reservationHandler.getAvailableApartments(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 15), 4);

        // Display all available apartments
        System.out.println("Available apartments:");
        for (Apartment apartment : availableApartments) {
            apartment.printApartment();
        }

        // Try to book the same apartment for the intersecting period
        reservationHandler.addReservation(randomApartment.getId(), LocalDate.of(2021, 1, 13), LocalDate.of(2021, 1, 17), 4, 2, randomCustomer.getId(), 12.90);

        // Print all reservations
        System.out.println("Reservations:");
        for (Reservation reservation : reservationHandler.getAllReservations()) {
            reservation.printReservation();
        }

        
    }

}