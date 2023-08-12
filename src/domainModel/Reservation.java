package domainModel;

import java.time.LocalDate;
import dao.ApartmentDAO;
import dao.RoomDAO;

public class Reservation {
    private int id;
    private Accommodation accommodation;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    // numberOfGuests and numberOfChildren are the total number of guests and children, respectively.
    private int numberOfGuests;
    private int numberOfChildren;
    private int numberOfInfants;
    private Customer customer;
    private double price;
    private LocalDate dateOfReservation;
    private double cityTaxAmount;


    public Reservation(int id, Accommodation accommodation, LocalDate arrivalDate, LocalDate departureDate, int numberOfGuests, int numberOfChildren, int numberOfInfants, Customer customer, double price, LocalDate dateOfReservation, double cityTaxAmount) {
        this.id = id;
        this.accommodation = accommodation;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.numberOfGuests = numberOfGuests;
        this.numberOfChildren = numberOfChildren;
        this.numberOfInfants = numberOfInfants;
        this.customer = customer;
        this.price = price;
        this.dateOfReservation = dateOfReservation;
        this.cityTaxAmount = cityTaxAmount;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id; }
    public Accommodation getAccommodation() {
        return accommodation;
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
    public int getNumberOfInfants() {
        return numberOfInfants;
    }
    public Customer getCustomer() {
        return customer;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) { this.price = price; }
    public LocalDate getDateOfReservation() {
        return dateOfReservation;
    }
    public void setDateOfReservation(LocalDate dateOfReservation) {
        this.dateOfReservation = dateOfReservation;
    }
    public double getCityTaxAmount() {
        return cityTaxAmount;
    }
    public void setCityTaxAmount(double cityTaxAmount) {
        this.cityTaxAmount = cityTaxAmount;
    }

    public void printReservation() {
        if (numberOfGuests != 0) {
            System.out.println("Reservation ID: " + this.getId() + " ID(" + this.accommodation.id + ") from " + this.getArrivalDate() + " to " + this.getDepartureDate() + " for " + this.getNumberOfGuests() + " people of which " + this.getNumberOfChildren() + " children and " + this.getNumberOfInfants() + " infants, customerId: " + this.customer.getId() + " for " + this.getPrice() + "€, booked: " + this.getDateOfReservation() + ", city tax: " + this.getCityTaxAmount() + "€");
        } else {
            System.out.println("UNAVAILABILITY: ID(" + this.accommodation.id + ") from " + this.getArrivalDate() + " to " + this.getDepartureDate());
        }
    }

}

