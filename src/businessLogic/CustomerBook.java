package businessLogic;

import dao.CustomerDAO;
import domainModel.Customer;

import java.util.ArrayList;

public class CustomerBook {
    private CustomerDAO customerDAO;

    // This class is a singleton
    private static CustomerBook instance = null;

    private CustomerBook(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public static CustomerBook getInstance(CustomerDAO customerDAO) {
        if (instance == null) {
            instance = new CustomerBook(customerDAO);
        }
        return instance;
    }

    public int addCustomer(String name, String address, String phone) {
        Customer customer = new Customer(-1, name, address, phone);
        try {
            customerDAO.insert(customer);
        } catch (Exception e) {
            System.err.println("ERROR: Could not insert customer.");
            return -1;
        }
        return customer.getId();
    }

    public Boolean deleteCustomer(int id) {
        try {
            if(!customerDAO.delete(id)) {
                return false;
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not delete customer.");
            return false;
        }
        return true;
    }

    public Customer getCustomer(int id) {
        Customer customer;
        try {
            customer = customerDAO.get(id);
            if (customer == null) {
                System.err.println("ERROR: Customer not found.");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Could not get customer.");
            return null;
        }
        return customer;
    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers;
        try {
            customers = customerDAO.getAll();
        } catch (Exception e) {
            System.err.println("ERROR: Could not get customers.");
            return null;
        }
        return customers;
    }


}
