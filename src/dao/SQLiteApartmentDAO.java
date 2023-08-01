package dao;

import domainModel.Apartment;

import java.sql.*;
import java.util.ArrayList;

public class SQLiteApartmentDAO implements ApartmentDAO{

    @Override
    public int getNextId() {
        return idGenerator.getNextId();
    }

    @Override
    public Apartment get(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        Apartment apartment = null;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartment WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            apartment = new Apartment(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getInt("numberOfRooms"), resultSet.getInt("numberOfBathrooms"), resultSet.getInt("numberOfBedrooms"), resultSet.getInt("numberOfBeds"));
        }

        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return apartment;
    }

    @Override
    public void insert(Apartment apartment) throws Exception {
        // Consider all possible values that an apartment can have
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Apartment (id, description, maxGuestsAllowed, numberOfRooms, numberOfBathrooms, numberOfBedrooms, numberOfBeds) VALUES (?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, getNextId());
        preparedStatement.setString(2, apartment.getDescription());
        preparedStatement.setInt(3, apartment.getMaxGuestsAllowed());
        preparedStatement.setInt(4, apartment.getNumberOfRooms());
        preparedStatement.setInt(5, apartment.getNumberOfBathrooms());
        preparedStatement.setInt(6, apartment.getNumberOfBedrooms());
        preparedStatement.setInt(7, apartment.getNumberOfBeds());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public void update(Apartment apartment) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Apartment SET description = ?, maxGuestsAllowed = ?, numberOfRooms = ?, numberOfBathrooms = ?, numberOfBedrooms = ?, numberOfBeds = ? WHERE id = ?");
        preparedStatement.setString(1, apartment.getDescription());
        preparedStatement.setInt(2, apartment.getMaxGuestsAllowed());
        preparedStatement.setInt(3, apartment.getNumberOfRooms());
        preparedStatement.setInt(4, apartment.getNumberOfBathrooms());
        preparedStatement.setInt(5, apartment.getNumberOfBedrooms());
        preparedStatement.setInt(6, apartment.getNumberOfBeds());
        preparedStatement.setInt(7, apartment.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Apartment WHERE id = ?");
        preparedStatement.setInt(1, id);
        int row = preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
        return row > 0;
    }

    @Override
    public ArrayList<Apartment> getAll() throws Exception {
        Connection connection = Database.getConnection();
        ArrayList<Apartment> apartments = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartment");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Apartment apartment = new Apartment(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getInt("numberOfRooms"), resultSet.getInt("numberOfBathrooms"), resultSet.getInt("numberOfBedrooms"), resultSet.getInt("numberOfBeds"));
            apartments.add(apartment);
        }

        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return apartments;
    }
}
