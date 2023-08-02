package dao;

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
            if (apartmentDAO.get(resultSet.getInt("accommodationId")) != null)
                reservation = new Reservation(id, apartmentDAO.get(resultSet.getInt("accommodationId")).getId(), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), resultSet.getInt("customerId"), resultSet.getDouble("price"));
            else {
                RoomDAO roomDAO = new SQLiteRoomDAO();
                if (roomDAO.get(resultSet.getInt("accommodationId")) != null)
                    reservation = new Reservation(id, roomDAO.get(resultSet.getInt("accommodationId")).getId(), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), resultSet.getInt("customerId"), resultSet.getDouble("price"));
            }
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return reservation;
    }

    @Override
    public ArrayList<Reservation> getAll() throws Exception {
        ApartmentDAO apartmentDAO = new SQLiteApartmentDAO();
        Connection connection = Database.getConnection();
        ArrayList<Reservation> reservations = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Reservation");

        while(resultSet.next()){
            reservations.add(new Reservation(resultSet.getInt("id"), resultSet.getInt("accommodationId"), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), resultSet.getInt("customerId"), resultSet.getDouble("price")));
        }

        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return reservations;
    }

    @Override
    public void insert(Reservation reservation) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Reservation (id, accommodationId, arrivalDate, departureDate, numberOfGuests, numberOfChildren, customerId, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        // Get the next id using idGenerator and ignore the id passed as a parameter
        preparedStatement.setInt(1, getNextId());
        preparedStatement.setInt(2, reservation.getAccommodationId());
        preparedStatement.setDate(3, Date.valueOf(reservation.getArrivalDate()));
        preparedStatement.setDate(4, Date.valueOf(reservation.getDepartureDate()));
        preparedStatement.setInt(5, reservation.getNumberOfGuests());
        preparedStatement.setInt(6, reservation.getNumberOfChildren());
        preparedStatement.setInt(7, reservation.getId());
        preparedStatement.setDouble(8, reservation.getPrice());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public void update(Reservation reservation) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Reservation SET accommodationId = ?, arrivalDate = ?, departureDate = ?, numberOfGuests = ?, numberOfChildren = ?, customerId = ?, price = ? WHERE id = ?");
        preparedStatement.setInt(1, reservation.getAccommodationId());
        preparedStatement.setDate(2, Date.valueOf(reservation.getArrivalDate()));
        preparedStatement.setDate(3, Date.valueOf(reservation.getDepartureDate()));
        preparedStatement.setInt(4, reservation.getNumberOfGuests());
        preparedStatement.setInt(5, reservation.getNumberOfChildren());
        preparedStatement.setInt(6, reservation.getId());
        preparedStatement.setDouble(7, reservation.getPrice());
        preparedStatement.setInt(8, reservation.getId());
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
    public boolean checkAvailability(int accommodationId, LocalDate startDate, LocalDate endDate) throws SQLException{
        Connection connection = Database.getConnection();
        // Prepare a statement to check if there is a reservation for the apartment in an intersecting date range
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Reservation WHERE accommodationId = ? AND NOT ((arrivalDate >= ? AND arrivalDate >= ?) OR (departureDate <= ? AND departureDate <= ?))");
        preparedStatement.setInt(1, accommodationId);
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
    public ArrayList<Apartment> getAvailableApartments(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws SQLException{
    Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartment WHERE Apartment.maxGuestsAllowed >= ?");
        preparedStatement.setInt(1, numberOfGuests);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Apartment> availableApartments = new ArrayList<>();
        while(resultSet.next()){
            if(checkAvailability(resultSet.getInt("id"), startDate, endDate)){
                availableApartments.add(new Apartment(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getInt("numberOfRooms"), resultSet.getInt("numberOfBathrooms"), resultSet.getInt("numberOfBedrooms"), resultSet.getInt("numberOfBeds")));
            }
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return availableApartments;
    }

    @Override
    public ArrayList<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws SQLException{
    Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Room WHERE Room.maxGuestsAllowed >= ?");
        preparedStatement.setInt(1, numberOfGuests);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Room> availableRooms = new ArrayList<>();
        while(resultSet.next()){
            if(checkAvailability(resultSet.getInt("id"), startDate, endDate)){
                availableRooms.add(new Room(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getBoolean("hasPrivateBathroom"), resultSet.getBoolean("hasKitchen")));
            }
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return availableRooms;
    }

    @Override
    public ArrayList<Reservation> getAccommodationReservations(int accommodationId) throws SQLException{
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Reservation WHERE accommodationId = ?");
        preparedStatement.setInt(1, accommodationId);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Reservation> reservations = new ArrayList<>();
        while(resultSet.next()){
            reservations.add(new Reservation(resultSet.getInt("id"), resultSet.getInt("accommodationId"), resultSet.getDate("arrivalDate").toLocalDate(), resultSet.getDate("departureDate").toLocalDate(), resultSet.getInt("numberOfGuests"), resultSet.getInt("numberOfChildren"), resultSet.getInt("customerId"), resultSet.getDouble("price")));
        }
        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return reservations;
    }

}
