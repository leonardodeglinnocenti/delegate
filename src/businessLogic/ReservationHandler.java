package businessLogic;

import dao.ReservationDAO;
import domainModel.Apartment;
import domainModel.Reservation;
import domainModel.Room;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Import Apache Commons for managing CSV files
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class ReservationHandler {

    // This class is a singleton
    private static ReservationHandler instance = null;
    private final ReservationDAO reservationDAO;
    private final CustomerBook customerBook;

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

    public int addReservation(int accommodationId, LocalDate startDate, LocalDate endDate, int numberOfGuests, int numberOfChildren, int customerId, double price, double cityTax) {
        // check if the accommodation is available for the given dates using ReservationDAO
        // this function returns the id of the reservation if the accommodation is available, -1 otherwise
        try {
            reservationDAO.checkAvailability(accommodationId, startDate, endDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }

        // Integrity check for the reservation object
        // Check whether the endDate is after the startDate
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return -1;
        }
        // Check whether the endDate is equal to the startDate
        if (startDate.isEqual(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return -1;
        }
        // Check whether the number of guests is greater than 0
        if (numberOfGuests <= 0) {
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return -1;
        }
        // Check whether the number of children is greater than the number of guests
        if (numberOfChildren > numberOfGuests) {
            System.err.println("ERROR: The number of children must be less than or equal to the number of guests.");
            return -1;
        }
        // Check whether the price is greater or equal to 0
        if (price < 0) {
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
        // The id passed as a parameter is ignored when passed to the DAO.
        Reservation reservation = new Reservation(-1, accommodationId, startDate, endDate, numberOfGuests, numberOfChildren, customerId, price, LocalDate.now(), cityTax);

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

    // The following method allows the user to import data from Airbnb
    public boolean importFromAirbnb(int apartmentId, String taxesFilePath, String reservationsFilePath, double localCityTax) throws FileNotFoundException, IOException {

        String confirmationCodeRecordTaxesFile = "Codice di conferma"; // taxes file
        String confirmationCodeRecordReservationsFile = "Codice di Conferma"; // reservations file

        String dateOfReservationRecord = "Prenotata"; // reservations file
        String dateOfReservationRecordFormat = "yyyy-MM-dd";

        String arrivalDateRecord = "Arrivo"; // taxes file
        String arrivalDateRecordFormat = "MM/dd/yyyy";

        String departureDateRecord = "";
        String departureDateRecordFormat = "MM/dd/yyyy";

        String numberOfAdultsRecord = "N. di adulti"; // reservations file
        String numberOfChildrenRecord = "N. di bambini"; // reservations file
        String numberOfInfantsRecord = "N. di neonati"; // reservations file
        String numberOfNightRecord = "N. di notti"; // reservations file
        String guestNameRecord = "Nome dell'ospite"; // reservations file
        String phoneNumberRecord = "Contatti"; // reservations file
        String priceRecord = "Guadagno lordo"; // taxes file
        String cityTaxRecord = "Tasse di Soggiorno"; // taxes file

        BufferedReader taxesFileReader;
        BufferedReader reservationsFileReader;

        try {
            taxesFileReader = new BufferedReader(new FileReader(taxesFilePath));
            reservationsFileReader = new BufferedReader(new FileReader(reservationsFilePath));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: The file does not exist.");
            return false;
        }

        // get the csv parser
        CSVParser csvParserTaxes = new CSVParser(taxesFileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        CSVParser csvParserReservations = new CSVParser(reservationsFileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        // Create a new temporary file to store the merged file
        File tempFile = new File("temp.csv");

        // Create a new writer
        CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(tempFile), CSVFormat.DEFAULT.withHeader("CODE", "RESERVATION_DATE", "ARRIVAL_DATE", "DEPARTURE_DATE", "NUMBER_OF_ADULTS", "NUMBER_OF_CHILDREN", "NUMBER_OF_INFANTS", "NUMBER_OF_NIGHTS", "GUEST_NAME", "PHONE_NUMBER", "PRICE", "CITY_TAX"));

        // Store all records in csvParserReservations inside a CSVRecord[]
        ArrayList<CSVRecord> csvReservationsRecords = new ArrayList<>();
        for (CSVRecord reservationRecord : csvParserReservations) {
            csvReservationsRecords.add(reservationRecord);
        }

        // Merge the files by matching the confirmation code
        for (CSVRecord taxesRecord : csvParserTaxes) {
            String confirmationCode = taxesRecord.get(confirmationCodeRecordTaxesFile);
            for (CSVRecord reservationsRecord : csvReservationsRecords) {
                if (reservationsRecord.get(confirmationCodeRecordReservationsFile).equals(confirmationCode)) {
                    // Evaluate departure date
                    String departureDate = LocalDate.parse(taxesRecord.get(arrivalDateRecord), DateTimeFormatter.ofPattern("MM/dd/yyyy")).plusDays(Integer.parseInt(reservationsRecord.get(numberOfNightRecord))).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    csvPrinter.printRecord(confirmationCode, reservationsRecord.get(dateOfReservationRecord), taxesRecord.get(arrivalDateRecord), departureDate, reservationsRecord.get(numberOfAdultsRecord), reservationsRecord.get(numberOfChildrenRecord), reservationsRecord.get(numberOfInfantsRecord), reservationsRecord.get(numberOfNightRecord), reservationsRecord.get(guestNameRecord), reservationsRecord.get(phoneNumberRecord), taxesRecord.get(priceRecord), taxesRecord.get(cityTaxRecord));
                    break;
                }
            }
        }
        // Close the parsers and the printer
        csvParserReservations.close();
        csvParserTaxes.close();
        csvPrinter.close();

        // Open the newly created file and store the reservations in the database
        BufferedReader tempFileReader = new BufferedReader(new FileReader(tempFile));
        CSVParser csvParserTempFile = new CSVParser(tempFileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        for (CSVRecord record : csvParserTempFile) {
            // Get the data from the record
            LocalDate reservationDate = LocalDate.parse(record.get("RESERVATION_DATE"), DateTimeFormatter.ofPattern(dateOfReservationRecordFormat));
            LocalDate arrivalDate = LocalDate.parse(record.get("ARRIVAL_DATE"), DateTimeFormatter.ofPattern(arrivalDateRecordFormat));
            LocalDate departureDate = LocalDate.parse(record.get("DEPARTURE_DATE"), DateTimeFormatter.ofPattern(departureDateRecordFormat));
            int numberOfAdults = Integer.parseInt(record.get("NUMBER_OF_ADULTS"));
            int numberOfChildren = Integer.parseInt(record.get("NUMBER_OF_CHILDREN")) + Integer.parseInt(record.get("NUMBER_OF_INFANTS"));
            int numberOfGuests = numberOfAdults + numberOfChildren;
            String guestName = record.get("GUEST_NAME");
            String phoneNumber = record.get("PHONE_NUMBER");
            double price = Double.parseDouble(record.get("PRICE"));
            double cityTaxAmount = Double.parseDouble(record.get("CITY_TAX"));

            // Create a new customer
            int customerId = customerBook.addCustomer(guestName, "", phoneNumber);

            // Create a new reservation
            Reservation reservation = new Reservation(-1, apartmentId, arrivalDate, departureDate, numberOfGuests, numberOfChildren, customerId, price, reservationDate, cityTaxAmount);
            // Add the reservation to the database
            try {
                reservationDAO.insert(reservation);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return false;
            }
        }

        // Delete the temporary file
        tempFile.delete();

        // Close the parser
        csvParserTempFile.close();

        return true;
    }

}
