package dao;

import domainModel.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.idGenerator;

public class SQLiteRoomDAO implements RoomDAO{
    @Override
    public int getNextId() throws Exception {
        return idGenerator.getNextId();
    }

    @Override
    public Room get(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        Room room = null;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Room WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            room = new Room(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getBoolean("hasPrivateBathroom"), resultSet.getBoolean("hasKitchen"));
        }

        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return room;
    }

    @Override
    public void insert(Room room) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Room (id, description, maxGuestsAllowed) VALUES (?, ?, ?)");
        preparedStatement.setInt(1, getNextId());
        preparedStatement.setString(2, room.getDescription());
        preparedStatement.setInt(3, room.getMaxGuestsAllowed());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public void update(Room room) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Room SET description = ?, maxGuestsAllowed = ? WHERE id = ?");
        preparedStatement.setString(1, room.getDescription());
        preparedStatement.setInt(2, room.getMaxGuestsAllowed());
        preparedStatement.setInt(3, room.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Room WHERE id = ?");
        preparedStatement.setInt(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
        return rowsAffected > 0;
    }

    @Override
    public ArrayList<Room> getAll() throws Exception {
        Connection connection = Database.getConnection();
        ArrayList<Room> rooms = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Room");
        while (resultSet.next()) {
            Room room = new Room(resultSet.getInt("id"), resultSet.getString("description"), resultSet.getInt("maxGuestsAllowed"), resultSet.getBoolean("hasPrivateBathroom"), resultSet.getBoolean("hasKitchen"));
            rooms.add(room);
        }
        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return rooms;
    }
}
