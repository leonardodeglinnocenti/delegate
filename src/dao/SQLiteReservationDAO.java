package dao;

import businessLogic.AccommodationHandler;
import domainModel.Accommodation;
import domainModel.Apartment;
import domainModel.Room;
import domainModel.Reservation;

import java.time.LocalDate;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteReservationDAO implements ReservationDAO{
    @Override
    public int getNextId() throws Exception {
        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM Reservation");
        int id = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return id + 1;
    }

    public int getCurrentId() throws Exception {
        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM Reservation");
        int id = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return id;
    }

    @Override
    public Reservation get(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        Reservation reservation = null;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Reservation WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        // Check if the reservation is for an apartment or a room
        if (resultSet.next()) {
            ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
            CustomerDAO customerDAO = new SQLiteCustomerDAO();
            if (apartmentDAO.get(resultSet.getInt("accommodationId")) != null) {
                reservation = new Reservation(id, apartmentDAO.get(resultSet.getInt("accommodationId")), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), customerDAO.get(resultSet.getInt("customerId")), resultSet.getDouble("price"), resultSet.getDate("dateOfReservation").toLocalDate(), resultSet.getDouble("cityTaxAmount"));
            } else {
                RoomDAO roomDAO = new SQLiteRoomDAO();
                if (roomDAO.get(resultSet.getInt("accommodationId")) != null)
                    reservation = new Reservation(id, roomDAO.get(resultSet.getInt("accommodationId")), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), customerDAO.get(resultSet.getInt("customerId")), resultSet.getDouble("price"), resultSet.getDate("dateOfReservation").toLocalDate(), resultSet.getDouble("cityTaxAmount"));
            }
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return reservation;
    }

    @Override
    public ArrayList<Reservation> getAll() throws Exception {
        Connection connection = Database.getConnection();
        ArrayList<Reservation> reservations = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Reservation");

        while(resultSet.next()){
            ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
            CustomerDAO customerDAO = new SQLiteCustomerDAO();
            Reservation reservation = null;
            if (apartmentDAO.get(resultSet.getInt("accommodationId")) != null) {
                reservation = new Reservation(resultSet.getInt("accommodationId"), apartmentDAO.get(resultSet.getInt("accommodationId")), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), customerDAO.get(resultSet.getInt("customerId")), resultSet.getDouble("price"), resultSet.getDate("dateOfReservation").toLocalDate(), resultSet.getDouble("cityTaxAmount"));
            } else {
                RoomDAO roomDAO = new SQLiteRoomDAO();
                if (roomDAO.get(resultSet.getInt("accommodationId")) != null)
                    reservation = new Reservation(resultSet.getInt("accommodationId"), roomDAO.get(resultSet.getInt("accommodationId")), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), customerDAO.get(resultSet.getInt("customerId")), resultSet.getDouble("price"), resultSet.getDate("dateOfReservation").toLocalDate(), resultSet.getDouble("cityTaxAmount"));
            }
            reservations.add(reservation);
        }

        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return reservations;
    }

    @Override
    public void insert(Reservation reservation) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Reservation (id, accommodationId, arrivalDate, departureDate, numberOfGuests, numberOfChildren, customerId, price, dateOfReservation, cityTaxAmount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        // Get the next id using idGenerator and ignore the id passed as a parameter
        preparedStatement.setInt(1, getNextId());
        preparedStatement.setInt(2, reservation.getAccommodation().getId());
        preparedStatement.setDate(3, Date.valueOf(reservation.getArrivalDate()));
        preparedStatement.setDate(4, Date.valueOf(reservation.getDepartureDate()));
        preparedStatement.setInt(5, reservation.getNumberOfGuests());
        preparedStatement.setInt(6, reservation.getNumberOfChildren());
        preparedStatement.setInt(7, reservation.getCustomer().getId());
        preparedStatement.setDouble(8, reservation.getPrice());
        preparedStatement.setDate(9, Date.valueOf(reservation.getDateOfReservation()));
        preparedStatement.setDouble(10, reservation.getCityTaxAmount());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public void update(Reservation reservation) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Reservation SET accommodationId = ?, arrivalDate = ?, departureDate = ?, numberOfGuests = ?, numberOfChildren = ?, customerId = ?, price = ?, dateOfReservation = ?, cityTaxAmount = ? WHERE id = ?");
        preparedStatement.setInt(1, reservation.getAccommodation().getId());
        preparedStatement.setDate(2, Date.valueOf(reservation.getArrivalDate()));
        preparedStatement.setDate(3, Date.valueOf(reservation.getDepartureDate()));
        preparedStatement.setInt(4, reservation.getNumberOfGuests());
        preparedStatement.setInt(5, reservation.getNumberOfChildren());
        preparedStatement.setInt(6, reservation.getCustomer().getId());
        preparedStatement.setDouble(7, reservation.getPrice());
        preparedStatement.setDate(8, Date.valueOf(reservation.getDateOfReservation()));
        preparedStatement.setDouble(9, reservation.getCityTaxAmount());
        preparedStatement.setInt(10, reservation.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Reservation WHERE id = ?");
        preparedStatement.setInt(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
        return rowsAffected > 0;
    }

    @Override
    public boolean checkAvailability(Accommodation accommodation, LocalDate startDate, LocalDate endDate) throws SQLException{
        Connection connection = Database.getConnection();
        // Prepare a statement to check if there is a reservation for the apartment in an intersecting date range
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Reservation WHERE accommodationId = ? AND NOT ((arrivalDate >= ? AND arrivalDate >= ?) OR (departureDate <= ? AND departureDate <= ?))");
        preparedStatement.setInt(1, accommodation.getId());
        preparedStatement.setDate(2, Date.valueOf(startDate));
        preparedStatement.setDate(3, Date.valueOf(endDate));
        preparedStatement.setDate(4, Date.valueOf(startDate));
        preparedStatement.setDate(5, Date.valueOf(endDate));
        ResultSet resultSet = preparedStatement.executeQuery();
        // If there is a reservation for the apartment in an intersecting date range, it is not available
        boolean available = !resultSet.next();
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return available;
    }
    
    @Override
    public ArrayList<Apartment> getAvailableApartments(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws Exception {
    Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartment WHERE Apartment.maxGuestsAllowed >= ?");
        preparedStatement.setInt(1, numberOfGuests);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Apartment> availableApartments = new ArrayList<>();
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        while(resultSet.next()){
            if(checkAvailability(apartmentDAO.get(resultSet.getInt("id")), startDate, endDate)){
                availableApartments.add(new Apartment(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getInt("numberOfRooms"), resultSet.getInt("numberOfBathrooms"), resultSet.getInt("numberOfBedrooms"), resultSet.getInt("numberOfBeds")));
            }
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return availableApartments;
    }

    @Override
    public ArrayList<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws Exception {
    Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Room WHERE Room.maxGuestsAllowed >= ?");
        preparedStatement.setInt(1, numberOfGuests);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Room> availableRooms = new ArrayList<>();
        RoomDAO roomDAO = new SQLiteRoomDAO();
        while(resultSet.next()){
            if(checkAvailability(roomDAO.get(resultSet.getInt("id")), startDate, endDate)){
                availableRooms.add(new Room(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getBoolean("hasPrivateBathroom"), resultSet.getBoolean("hasKitchen")));
            }
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return availableRooms;
    }

    @Override
    public ArrayList<Reservation> getAccommodationReservations(int accommodationId) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Reservation WHERE accommodationId = ?");
        preparedStatement.setInt(1, accommodationId);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Reservation> reservations = new ArrayList<>();
        while(resultSet.next()){
            Reservation reservation = null;
            ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
            CustomerDAO customerDAO = new SQLiteCustomerDAO();
            if (apartmentDAO.get(resultSet.getInt("accommodationId")) != null) {
                reservation = new Reservation(resultSet.getInt("accommodationId"), apartmentDAO.get(resultSet.getInt("accommodationId")), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), customerDAO.get(resultSet.getInt("customerId")), resultSet.getDouble("price"), resultSet.getDate("dateOfReservation").toLocalDate(), resultSet.getDouble("cityTaxAmount"));
            } else {
                RoomDAO roomDAO = new SQLiteRoomDAO();
                if (roomDAO.get(resultSet.getInt("accommodationId")) != null)
                    reservation = new Reservation(resultSet.getInt("accommodationId"), roomDAO.get(resultSet.getInt("accommodationId")), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), customerDAO.get(resultSet.getInt("customerId")), resultSet.getDouble("price"), resultSet.getDate("dateOfReservation").toLocalDate(), resultSet.getDouble("cityTaxAmount"));
            }
            reservations.add(reservation);
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return reservations;
    }

}
