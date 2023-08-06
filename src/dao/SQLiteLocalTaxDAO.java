package dao;

import domainModel.LocalTax;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class SQLiteLocalTaxDAO implements LocalTaxDAO{

    @Override
    public int getNextId() throws Exception {
        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM LocalTax");
        int id = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        return id + 1;
    }

    @Override
    public void insert(domainModel.LocalTax localTax) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LocalTax VALUES (?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, getNextId());
        preparedStatement.setString(2, localTax.getDescription());
        preparedStatement.setDouble(3, localTax.getAmount());
        preparedStatement.setString(4, localTax.getTarget());
        preparedStatement.setInt(5, localTax.getDaysThreshold());
        preparedStatement.setDate(6, Date.valueOf(localTax.getStartDate()));
        preparedStatement.setDate(7, Date.valueOf(localTax.getEndDate()));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public LocalTax get(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM LocalTax WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        domainModel.LocalTax localTax = null;
        if (resultSet.next()) {
            localTax = new LocalTax(
                    resultSet.getString("description"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("target"),
                    resultSet.getInt("daysThreshold"),
                    resultSet.getDate("startDate").toLocalDate(),
                    resultSet.getDate("endDate").toLocalDate()
            );
        }
        resultSet.close();
        preparedStatement.close();
        return localTax;
    }

    @Override
    public void update(domainModel.LocalTax localTax) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE LocalTax SET description = ?, amount = ?, target = ?, daysThreshold = ?, startDate = ?, endDate = ? WHERE id = ?");
        preparedStatement.setString(1, localTax.getDescription());
        preparedStatement.setDouble(2, localTax.getAmount());
        preparedStatement.setString(3, localTax.getTarget());
        preparedStatement.setInt(4, localTax.getDaysThreshold());
        preparedStatement.setDate(5, Date.valueOf(localTax.getStartDate()));
        preparedStatement.setDate(6, Date.valueOf(localTax.getEndDate()));
        preparedStatement.setInt(7, localTax.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM LocalTax WHERE id = ?");
        preparedStatement.setInt(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        preparedStatement.close();
        return rowsAffected > 0;
    }

    @Override
    public java.util.ArrayList<domainModel.LocalTax> getAll() throws Exception {
        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM LocalTax");
        java.util.ArrayList<domainModel.LocalTax> localTaxes = new java.util.ArrayList<>();
        while (resultSet.next()) {
            domainModel.LocalTax localTax = new LocalTax(
                    resultSet.getString("description"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("target"),
                    resultSet.getInt("daysThreshold"),
                    resultSet.getDate("startDate").toLocalDate(),
                    resultSet.getDate("endDate").toLocalDate()
            );
            localTax.setId(resultSet.getInt("id"));
            localTaxes.add(localTax);
        }
        resultSet.close();
        statement.close();
        return localTaxes;
    }


    public ArrayList<LocalTax> getLocalTaxesByTarget(String target, LocalDate startDate, LocalDate endDate) throws Exception {
        // Get all taxes that intersect with the given date range
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM LocalTax WHERE target = ? AND ((startDate <= ? AND endDate >= ?) OR (startDate <= ? AND endDate >= ?) OR (startDate >= ? AND endDate <= ?))");
        preparedStatement.setString(1, target);
        preparedStatement.setDate(2, Date.valueOf(startDate));
        preparedStatement.setDate(3, Date.valueOf(startDate));
        preparedStatement.setDate(4, Date.valueOf(endDate));
        preparedStatement.setDate(5, Date.valueOf(endDate));
        preparedStatement.setDate(6, Date.valueOf(startDate));
        preparedStatement.setDate(7, Date.valueOf(endDate));
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<LocalTax> localTaxes = new ArrayList<>();
        while (resultSet.next()) {
            LocalTax localTax = new LocalTax(
                    resultSet.getString("description"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("target"),
                    resultSet.getInt("daysThreshold"),
                    resultSet.getDate("startDate").toLocalDate(),
                    resultSet.getDate("endDate").toLocalDate()
            );
            localTax.setId(resultSet.getInt("id"));
            localTaxes.add(localTax);
        }
        resultSet.close();
        preparedStatement.close();
        return localTaxes;
    }

}
