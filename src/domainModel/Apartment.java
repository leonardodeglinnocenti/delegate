package domainModel;

public class Apartment extends Accommodation {
    public Apartment(int id, String description, int maxGuestsAllowed) {
        super(id, description, maxGuestsAllowed);
    }
    public Apartment(int id, String description, int maxGuestsAllowed, int numberOfRooms, int numberOfBathrooms, int numberOfBedrooms, int numberOfBeds) {
        super(id, description, maxGuestsAllowed);
        this.numberOfRooms = numberOfRooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBeds = numberOfBeds;
    }
    private int numberOfRooms;
    private int numberOfBathrooms;
    private int numberOfBedrooms;
    private int numberOfBeds;

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(int numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(int numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public void printApartment() {
        System.out.println("Apartment ID: " + this.getId() + " " + this.getDescription() + " " + this.getMaxGuestsAllowed() + " " + this.getNumberOfRooms() + " " + this.getNumberOfBathrooms() + " " + this.getNumberOfBedrooms() + " " + this.getNumberOfBeds());
    }

}
