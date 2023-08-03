package dao;

import domainModel.Customer;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteCustomerDAO implements CustomerDAO{

    @Override
    public int getNextId() throws Exception {
        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM Customer");
        int id = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return id;
    }

    @Override
    public Customer get(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        Customer customer = null;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Customer WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            customer = new Customer(id, resultSet.getString("name"), resultSet.getString("address"), resultSet.getString("phone"));
        }

        resultSet.close();
        preparedStatement.close();
        Database.closeConnection(connection);
        return customer;
    }

    @Override
    public ArrayList<Customer> getAll() throws Exception {
        Connection connection = Database.getConnection();
        ArrayList<Customer> customers = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Customer");

        while (resultSet.next()) {
            Customer customer = new Customer(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("address"), resultSet.getString("phone"));
            customers.add(customer);
        }

        resultSet.close();
        statement.close();
        Database.closeConnection(connection);
        return customers;
    }

    @Override
    public void insert(Customer customer) throws Exception {
        Connection connection = Database.getConnection();
        customer.setId(getNextId());
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Customer (id, name, address, phone) VALUES (?, ?, ?, ?)");
        preparedStatement.setInt(1, customer.getId());
        preparedStatement.setString(2, customer.getName());
        preparedStatement.setString(3, customer.getAddress());
        preparedStatement.setString(4, customer.getPhone());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public void update(Customer customer) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Customer SET name = ?, address = ?, phone = ? WHERE id = ?");
        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getAddress());
        preparedStatement.setString(3, customer.getPhone());
        preparedStatement.setInt(4, customer.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
    }

    @Override
    public boolean delete(Integer id) throws Exception {
        Connection connection = Database.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Customer WHERE id = ?");
        preparedStatement.setInt(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        preparedStatement.close();
        // Delete all reservations for this customer
        preparedStatement = connection.prepareStatement("DELETE FROM Reservation WHERE customerId = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        Database.closeConnection(connection);
        return rowsAffected > 0;
    }

}
