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


    public Reservation(int id, int accommodationId, LocalDate arrivalDate, LocalDate departureDate, int numberOfGuests, int numberOfChildren, int customerId, double price) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.numberOfGuests = numberOfGuests;
        this.numberOfChildren = numberOfChildren;
        this.customerId = customerId;
        // round price to 2 decimal places
        this.price = Math.round(price * 100.0) / 100.0;
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

    public void printReservation() {
        System.out.println("Reservation ID: " + this.getId() + " " + this.getAccommodationId() + " " + this.getArrivalDate() + " " + this.getDepartureDate() + " " + this.getNumberOfGuests() + " " + this.getNumberOfChildren() + " " + this.getCustomerId() + " " + this.getPrice());
    }

}

