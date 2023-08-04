package domainModel;

import businessLogic.*;
import dao.*;

import java.time.LocalDate;
import java.util.ArrayList;

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

        // Create an accommodation
        int apartmentPointer = accommodationHandler.createAccommodation("apartment", "Cosy apt", 6);
        reservationHandler.importFromAirbnb(apartmentPointer, "/home/leonardo/Downloads/airbnb_tax_06_2023-08_2023.csv", "/home/leonardo/Downloads/reservations(1).csv", 5.50);

        // Print all reservations
        ArrayList<Reservation> reservations = reservationHandler.getAllReservations();
        for (Reservation reservation : reservations) {
            reservation.printReservation();
        }


        /*
        // Create some accommodations
        accommodationHandler.createAccommodation("apartment", "Apartment 1", 4);
        int roomPointer = accommodationHandler.createAccommodation("room","Room 2", 2);
        accommodationHandler.createAccommodation("apartment", "Apartment 2", 4);
        int apartmentPointer = accommodationHandler.createAccommodation("apartment", "Apartment 3", 4);
        accommodationHandler.createAccommodation("room","Room 1", 2);

        // This will fail if everything is working correctly
        accommodationHandler.createAccommodation("unknown","Room 3", 2);

        // Personalize the accommodation and the room with the pointer
        accommodationHandler.addApartmentDetails(apartmentPointer, 2, 6, 1, 0);
        accommodationHandler.addRoomDetails(roomPointer, true, true);

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
            apartment.printApartment();
        }
        System.out.println("Rooms:");
        for (Room room : rooms) {
            room.printRoom();
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
        reservationHandler.addReservation(randomApartment.getId(), LocalDate.of(2020, 12, 31), LocalDate.of(2021, 1, 1), 4, 2, randomCustomer.getId(), 12.90);

        // Print all reservations for a given apartment
        System.out.println("Reservations for apartment " + randomApartment.getId() + ":");
        for (Reservation reservation : reservationHandler.getAccommodationReservations(randomApartment.getId())) {
            reservation.printReservation();
        }

        // Get random room
        rooms = roomDAO.getAll();
        int[] roomIndexes = new int[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            roomIndexes[i] = rooms.get(i).getId();
        }
        int randomRoomIndex = roomIndexes[(int) (Math.random() * (roomIndexes.length))];
        System.out.println("Random room index: " + randomRoomIndex);
        Room randomRoom = roomDAO.get(randomRoomIndex);

        // Create a reservation for the selected room
        reservationHandler.addReservation(randomRoomIndex, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 10), 2, 1, randomCustomer.getId(), 12.90);

        // Test reservation cancellation
        reservationHandler.deleteReservation(2);

        // Print all reservations for a given room
        System.out.println("Reservations for room " + randomRoom.getId() + ":");
        for (Reservation reservation : reservationHandler.getAccommodationReservations(randomRoom.getId())) {
            reservation.printReservation();
        }

        // This reservation should be deleted when the accommodation is deleted
        reservationHandler.addReservation(roomPointer, LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 10), 4, 2, 0, 12.90);
        reservationHandler.addReservation(apartmentPointer, LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 10), 4, 2, 0, 12.90);

        // Delete accommodation with a reservation
        accommodationHandler.deleteAccommodation(1);
        accommodationHandler.deleteAccommodation(apartmentPointer);
        // customerBook.deleteCustomer(0);
        customerBook.deleteCustomer(6);

        // Print all customers
        System.out.println("All customers:");
        for (Customer customer : customerBook.getAllCustomers()) {
            customer.printCustomer();
        }

        // Print all reservations
        System.out.println("All reservations:");
        for (Reservation reservation : reservationHandler.getAllReservations()) {
            reservation.printReservation();
        }
         */
    }

}