package domainModel;

public class Customer {
    // The id is automatically generated and incremented.
    private int id;
    private String name;
    private String address;
    private String phone;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Customer(int id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public void printCustomer() {
        System.out.println("Customer: " + name + " (id: " + id + ") " + "Address: " + address + " Phone: " + phone);
    }
}
