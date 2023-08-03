package domainModel;

import java.time.LocalDate;
import dao.ApartmentDAO;
import dao.RoomDAO;

public class Reservation {
    private final int id;
    private int accommodationId;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    // numberOfGuests and numberOfChildren are the total number of guests and children, respectively.
    private int numberOfGuests;
    private int numberOfChildren;
    private int customerId;
    double price;
    private LocalDate dateOfReservation;


    public Reservation(int id, int accommodationId, LocalDate arrivalDate, LocalDate departureDate, int numberOfGuests, int numberOfChildren, int customerId, double price, LocalDate dateOfReservation) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.numberOfGuests = numberOfGuests;
        this.numberOfChildren = numberOfChildren;
        this.customerId = customerId;
        // round price to 2 decimal places
        this.price = Math.round(price * 100.0) / 100.0;
        this.dateOfReservation = dateOfReservation;
    }

    public int getId() {
        return id;
    }
    public int getAccommodationId() {
        return accommodationId;
    }
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }
    public LocalDate getDepartureDate() {
        return departureDate;
    }
    public int getNumberOfGuests() {
        return numberOfGuests;
    }
    public int getNumberOfChildren() {
        return numberOfChildren;
    }
    public int getCustomerId() {
        return customerId;
    }
    public double getPrice() {
        return price;
    }
    public LocalDate getDateOfReservation() {
        return dateOfReservation;
    }

    public void printReservation() {
        System.out.println("Reservation ID: " + this.getId() + " ID(" + this.getAccommodationId() + ") from " + this.getArrivalDate() + " to " + this.getDepartureDate() + " for " + this.getNumberOfGuests() + " people of which " + this.getNumberOfChildren() + " children, customerId: " + this.getCustomerId() + " for " + this.getPrice() + "€, booked: " + this.getDateOfReservation());
    }

}

