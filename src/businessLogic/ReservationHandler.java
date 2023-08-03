package businessLogic;

import dao.ReservationDAO;
import domainModel.Apartment;
import domainModel.Reservation;
import domainModel.Room;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ReservationHandler {

    // This class is a singleton
    private static ReservationHandler instance = null;
    private ReservationDAO reservationDAO;
    private CustomerBook customerBook;

    private ReservationHandler(ReservationDAO reservationDAO, CustomerBook customerBook) {
        this.reservationDAO = reservationDAO;
        this.customerBook = customerBook;
    }

    public static ReservationHandler getInstance(ReservationDAO reservationDAO, CustomerBook customerBook) {
        if (instance == null) {
            instance = new ReservationHandler(reservationDAO, customerBook);
        }
        return instance;
    }

    public int addReservation(int accommodationId, LocalDate startDate, LocalDate endDate, int numberOfGuests, int numberOfChildren, int customerId, double price) {
        // check if the accommodation is available for the given dates using ReservationDAO
        // this function returns the id of the reservation if the accommodation is available, -1 otherwise
        try {
            reservationDAO.checkAvailability(accommodationId, startDate, endDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        // The id passed as a parameter is ignored when passed to the DAO.
        Reservation reservation = new Reservation(-1, accommodationId, startDate, endDate, numberOfGuests, numberOfChildren, customerId, price, LocalDate.now());

        // Integrity check for the reservation object
        // Check whether the endDate is after the startDate
        if (reservation.getArrivalDate().isAfter(reservation.getDepartureDate())) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return -1;
        }
        // Check whether the endDate is equal to the startDate
        if (reservation.getArrivalDate().isEqual(reservation.getDepartureDate())) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return -1;
        }
        // Check whether the number of guests is greater than 0
        if (reservation.getNumberOfGuests() <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return -1;
        }
        // Check whether the number of children is greater than the number of guests
        if (reservation.getNumberOfChildren() > reservation.getNumberOfGuests()) {
            System.err.println("ERROR: The number of children must be less than or equal to the number of guests.");
            return -1;
        }
        // Check whether the price is greater or equal to 0
        if (reservation.getPrice() < 0) {
            System.err.println("ERROR: The price must be greater or equal to 0.");
            return -1;
        }
        // Check whether the customer exists using CustomerBook
        if (customerBook.getCustomer(customerId) == null) {
            System.err.println("ERROR: The customer does not exist.");
            return -1;
        }
        // Check whether the apartment is available for the given dates using ReservationDAO
        // this function returns the id of the reservation if the apartment is available, -1 otherwise
        try {
            if (!reservationDAO.checkAvailability(accommodationId, startDate, endDate)) {
                System.err.println("ERROR: The apartment is not available for the given dates.");
                return -1;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        try {
            reservationDAO.insert(reservation);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        // return the effective reservation id present in the database
        try {
            return reservationDAO.getCurrentId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean deleteReservation(int reservationId) {
        // Check whether the reservation exists using ReservationDAO
        try {
            if (reservationDAO.get(reservationId) == null) {
                System.err.println("ERROR: The reservation does not exist.");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        // Delete the reservation using ReservationDAO
        try {
            reservationDAO.delete(reservationId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    // Make function to return all available accommodations of a given type for a given period of time and number of guests
    // Use ReservationDAO to get all the reservations for the given period of time
    public ArrayList<Apartment> getAvailableApartments(LocalDate startDate, LocalDate endDate, int numberOfGuests) {
        // Check whether the endDate is after the startDate
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return null;
        }
        // Check whether the number of guests is greater than 0
        if (numberOfGuests <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return null;
        }
        // Get all the reservations for the given period of time
        try {
            return reservationDAO.getAvailableApartments(startDate, endDate, numberOfGuests);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate, int numberOfGuests) {
        // Check whether the endDate is after the startDate
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return null;
        }
        // Check whether the number of guests is greater than 0
        if (numberOfGuests <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return null;
        }
        // Get all the reservations for the given period of time
        try {
            return reservationDAO.getAvailableRooms(startDate, endDate, numberOfGuests);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Reservation> getAllReservations() {
        try {
            return reservationDAO.getAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Reservation> getAccommodationReservations(int accommodationId) {
        try {
            return reservationDAO.getAccommodationReservations(accommodationId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    // The following method allows the user to import reservations from an Airbnb csv file
    public boolean importFromAirbnb(String taxesFilePath, String reservationFilePath, int apartmentId, double localCityTax) throws FileNotFoundException, IOException {
        // Open file and read line by line
        // For each line, create a reservation and add it to the database
        // The apartmentId is the id of the apartment that the reservations refer to
        // The file is in the format: data of interest are separated by commas
        // The first line is the header, so it is skipped
        // The data of interest are: Data (which is the date of the reservation), Arrivo (which is the check-in time), Notti (which is the number of nights), Ospite (which is the number of guests), Guadagno lordo (which is the price)
        // The date is in the format: mm/dd/yyyy

        // Open files
        BufferedReader br0, br1;
        try {
            br0 = new BufferedReader(new FileReader(taxesFilePath));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: The file does not exist.");
            return false;
        }
        try {
            br1 = new BufferedReader(new FileReader(reservationFilePath));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: The file does not exist.");
            return false;
        }

        // Skip the first line of both files
        try {
            br0.readLine();
            br1.readLine();
        } catch (IOException e) {
            System.err.println("ERROR: The file is empty.");
            return false;
        }

        // Create a temporary file
        File tempFile = new File("temp.csv");

        // Create a buffered writer to write to the temporary file
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

        String line, apartmentName = null;
        ArrayList<String[]> data0 = new ArrayList<>();
        ArrayList<String[]> data1 = new ArrayList<>();
        String[] data;
        while ((line = br0.readLine()) != null) {
            data = line.split(",");
            // Get data from data[6] until the next double quote (this allows to get the name of the apartment even if it contains commas)
            apartmentName = data[6] + ",";
            int i = 7;
            while (!data[i].contains("\"")) {
                apartmentName = apartmentName + data[i] + ",";
                i++;
            }
            apartmentName = apartmentName + data[i];
            line = line.replaceAll("\"[^\"]*\"", "");
            data0.add(line.split(","));
        }
        while ((line = br1.readLine()) != null) {
            line = line.replaceAll(apartmentName, "");
            // Delete all double quotes
            line = line.replaceAll("\"", "");
            data1.add(line.split(","));
        }

        // Merge the two files in the temporary file using data0[2] and data1[0] as keys
        for (String[] data0Line : data0) {
            for (String[] data1Line : data1) {
                if (data0Line[2].equals(data1Line[0])) {
                    for (String s : data0Line) {
                        bw.write(s + ",");
                    }
                    for (String s : data1Line) {
                        bw.write(s + ",");
                    }
                    bw.newLine();
                }
            }
        }

        // Close the buffered writer
        bw.close();

        // Read from the temporary file
        BufferedReader tempData = new BufferedReader(new FileReader(tempFile));
        // Read the file line by line until the end
        while ((line = tempData.readLine()) != null) {
            // Delete data between double quotes
            // Split the line by commas
            data = line.split(",");
            // Get the date
            String dateOfReservationString = data[26];
            LocalDate dateOfReservation = LocalDate.parse(dateOfReservationString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // Get the check-in time
            String arrivalDateString = data[3];
            LocalDate arrivalDate = LocalDate.parse(arrivalDateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            // Get the number of nights
            int numberOfNights = Integer.parseInt(data[4]);
            LocalDate departureDate = arrivalDate.plusDays(numberOfNights);
            // Get the number of guests and children
            int numberOfGuests = Integer.parseInt(data[20]) + Integer.parseInt(data[21]) + Integer.parseInt(data[22]);
            int numberOfChildren = Integer.parseInt(data[21]) + Integer.parseInt(data[22]);
            // Get the price
            double price = Double.parseDouble(data[14]);
            // Create a new customer
            int customerId = customerBook.addCustomer(data[5], "", data[19]);
            // Create a reservation, remember that the id is automatically generated by the database
            Reservation reservation = new Reservation(-1, apartmentId, arrivalDate, departureDate, numberOfGuests, numberOfChildren, customerId, price, dateOfReservation);
            // Add the reservation to the database
            try {
                reservationDAO.insert(reservation);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

}
