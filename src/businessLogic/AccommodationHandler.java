package businessLogic;

import dao.ApartmentDAO;
import dao.RoomDAO;
import domainModel.Apartment;
import domainModel.Room;

import java.util.ArrayList;

public class AccommodationHandler {
    // This method helps the user create a new accommodation, passing in input whether it is a room or an apartment
    // This class is a singleton
    private static AccommodationHandler instance = null;
    private final ApartmentDAO apartmentDAO;
    private final RoomDAO roomDAO;

    private AccommodationHandler(ApartmentDAO apartmentDAO, RoomDAO roomDAO) {
        this.apartmentDAO = apartmentDAO;
        this.roomDAO = roomDAO;
    }

    public static AccommodationHandler getInstance(ApartmentDAO apartmentDAO, RoomDAO roomDAO) {
        if (instance == null) {
            instance = new AccommodationHandler(apartmentDAO, roomDAO);
        }
        return instance;
    }

    public int createAccommodation(String type, String description, int maxGuestsAllowed) {
        if (type.equals("apartment")) {
            // id will be automatically generated by the database
            Apartment apartment = new Apartment(-1, description, maxGuestsAllowed);
            try {
                apartmentDAO.insert(apartment);
            } catch (Exception e) {
                System.err.println("ERROR: Could not insert apartment.");
                return -1;
            }
            // the insert method automatically sets the id of the apartment
            return apartment.getId();
        } else if (type.equals("room")) {
            Room room = new Room(-1, description, maxGuestsAllowed);
            try {
                roomDAO.insert(room);
            } catch (Exception e) {
                System.err.println("ERROR: Could not insert room.");
                return -1;
            }
            // the insert method automatically sets the id of the room
            return room.getId();
        } else {
            System.err.println("ERROR: Invalid accommodation type.");
            return -1;
        }
    }

    public boolean deleteAccommodation(int id) {
        // Check if the given id is referred to an apartment or a room (each is unique)
        try {
            if(apartmentDAO.delete(id)) {
                return true;
            }
            if(roomDAO.delete(id)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("ERROR: Something went wrong with the database and the accommodation could not be deleted.");
            return false;
        }
        System.err.println("ERROR: Could not delete accommodation (wrong id).");
        return false;
    }

    public ArrayList<Apartment> getAllApartments() {
        ArrayList<Apartment> apartments;
        try {
            apartments = new ArrayList<>(apartmentDAO.getAll());
        } catch (Exception e) {
            System.err.println("ERROR: Could not get all accommodations.");
            return null;
        }
        return apartments;
    }

    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> rooms;
        try {
            rooms = new ArrayList<>(roomDAO.getAll());
        } catch (Exception e) {
            System.err.println("ERROR: Could not get all accommodations.");
            return null;
        }
        return rooms;
    }

    public Apartment getApartmentById(int id) {
        Apartment apartment;
        try {
            apartment = apartmentDAO.get(id);
        } catch (Exception e) {
            System.err.println("ERROR: Could not get accommodation.");
            return null;
        }
        return apartment;
    }

    public Room getRoomById(int id) {
        Room room;
        try {
            room = roomDAO.get(id);
        } catch (Exception e) {
            System.err.println("ERROR: Could not get accommodation.");
            return null;
        }
        return room;
    }

    public Boolean addApartmentDetails(int apartmentId, int numberOfRooms, int numberOfBathrooms, int numberOfBedrooms, int numberOfBeds) {
        Apartment apartment = getApartmentById(apartmentId);
        if (apartment == null) {
            System.err.println("ERROR: Could not get apartment.");
            return false;
        }
        apartment.setNumberOfRooms(numberOfRooms);
        apartment.setNumberOfBathrooms(numberOfBathrooms);
        apartment.setNumberOfBedrooms(numberOfBedrooms);
        apartment.setNumberOfBeds(numberOfBeds);
        try {
            apartmentDAO.update(apartment);
        } catch (Exception e) {
            System.err.println("ERROR: Could not update apartment.");
            return false;
        }
        return true;
    }

    public Boolean addRoomDetails(int roomId, boolean hasPrivateBathroom, boolean hasKitchen) {
        Room room = getRoomById(roomId);
        if (room == null) {
            System.err.println("ERROR: Could not get room.");
            return false;
        }
        room.setHasPrivateBathroom(hasPrivateBathroom);
        room.setHasKitchen(hasKitchen);
        try {
            roomDAO.update(room);
        } catch (Exception e) {
            System.err.println("ERROR: Could not update room.");
            return false;
        }
        return true;
    }

}
