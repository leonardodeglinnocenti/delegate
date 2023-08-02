package businessLogic;

import dao.ReservationDAO;
import domainModel.Accommodation;
import domainModel.Apartment;
import domainModel.Reservation;
import domainModel.Room;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReservationHandler {

    // This class is a singleton
    private static ReservationHandler instance = null;
    private ReservationDAO reservationDAO;
    private CustomerBook customerBook;

    private ReservationHandler(ReservationDAO reservationDAO, CustomerBook customerBook) {
        this.reservationDAO = reservationDAO;
        this.customerBook = customerBook;
    }

    public static ReservationHandler getInstance(ReservationDAO reservationDAO, CustomerBook customerBook) {
        if (instance == null) {
            instance = new ReservationHandler(reservationDAO, customerBook);
        }
        return instance;
    }

    public int addReservation(int accommodationId, LocalDate startDate, LocalDate endDate, int numberOfGuests, int numberOfChildren, int customerId, double price) {
        // check if the accommodation is available for the given dates using ReservationDAO
        // this function returns the id of the reservation if the accommodation is available, -1 otherwise
        try {
            reservationDAO.checkAvailability(accommodationId, startDate, endDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        // The id passed as a parameter is ignored when passed to the DAO.
        Reservation reservation = new Reservation(-1, accommodationId, startDate, endDate, numberOfGuests, numberOfChildren, customerId, price);

        // Integrity check for the reservation object
        // Check whether the endDate is after the startDate
        if (reservation.getArrivalDate().isAfter(reservation.getDepartureDate())) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return -1;
        }
        // Check whether the endDate is equal to the startDate
        if (reservation.getArrivalDate().isEqual(reservation.getDepartureDate())) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return -1;
        }
        // Check whether the number of guests is greater than 0
        if (reservation.getNumberOfGuests() <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return -1;
        }
        // Check whether the number of children is greater than the number of guests
        if (reservation.getNumberOfChildren() > reservation.getNumberOfGuests()) {
            System.err.println("ERROR: The number of children must be less than or equal to the number of guests.");
            return -1;
        }
        // Check whether the price is greater or equal to 0
        if (reservation.getPrice() < 0) {
            System.err.println("ERROR: The price must be greater or equal to 0.");
            return -1;
        }
        // Check whether the customer exists using CustomerBook
        if (customerBook.getCustomer(customerId) == null) {
            System.err.println("ERROR: The customer does not exist.");
            return -1;
        }
        // Check whether the apartment is available for the given dates using ReservationDAO
        // this function returns the id of the reservation if the apartment is available, -1 otherwise
        try {
            if (!reservationDAO.checkAvailability(accommodationId, startDate, endDate)) {
                System.err.println("ERROR: The apartment is not available for the given dates.");
                return -1;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        try {
            reservationDAO.insert(reservation);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        // return the effective reservation id present in the database
        try {
            return reservationDAO.getCurrentId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean deleteReservation(int reservationId) {
        // Check whether the reservation exists using ReservationDAO
        try {
            if (reservationDAO.get(reservationId) == null) {
                System.err.println("ERROR: The reservation does not exist.");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        // Delete the reservation using ReservationDAO
        try {
            reservationDAO.delete(reservationId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    // Make function to return all available accommodations of a given type for a given period of time and number of guests
    // Use ReservationDAO to get all the reservations for the given period of time
    public ArrayList<Apartment> getAvailableApartments(LocalDate startDate, LocalDate endDate, int numberOfGuests) {
        // Check whether the endDate is after the startDate
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return null;
        }
        // Check whether the number of guests is greater than 0
        if (numberOfGuests <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return null;
        }
        // Get all the reservations for the given period of time
        try {
            return reservationDAO.getAvailableApartments(startDate, endDate, numberOfGuests);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate, int numberOfGuests) {
        // Check whether the endDate is after the startDate
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return null;
        }
        // Check whether the number of guests is greater than 0
        if (numberOfGuests <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return null;
        }
        // Get all the reservations for the given period of time
        try {
            return reservationDAO.getAvailableRooms(startDate, endDate, numberOfGuests);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Reservation> getAllReservations() {
        try {
            return reservationDAO.getAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Reservation> getAccommodationReservations(int accommodationId) {
        try {
            return reservationDAO.getAccommodationReservations(accommodationId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

}
