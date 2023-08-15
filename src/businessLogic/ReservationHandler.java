package businessLogic;

import dao.ReservationDAO;
import domainModel.*;

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
    private final Customer unavailabilityCustomer;

    private ReservationHandler(ReservationDAO reservationDAO, CustomerBook customerBook) {
        this.reservationDAO = reservationDAO;
        this.customerBook = customerBook;
        this.unavailabilityCustomer = customerBook.addCustomer("UNAVAILABLE", "", "");
    }

    public static ReservationHandler getInstance(ReservationDAO reservationDAO, CustomerBook customerBook) {
        if (instance == null) {
            instance = new ReservationHandler(reservationDAO, customerBook);
        }
        return instance;
    }

    public Reservation addReservation(Accommodation accommodation, LocalDate startDate, LocalDate endDate, int numberOfGuests, int numberOfChildren, int numberOfInfants, Customer customer, double price, double cityTax) {
        // check if the accommodation is available for the given dates using ReservationDAO

        // Integrity check for the reservation object
        // Check whether the endDate is after the startDate
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return null;
        }
        // Check whether the endDate is equal to the startDate
        if (startDate.isEqual(endDate)) {
            System.err.println("ERROR: The arrival date must be before the departure date.");
            return null;
        }
        // Check whether the number of guests is greater than 0
        if (numberOfGuests <= 0 && customer != unavailabilityCustomer) {
            // Check if the customer is the unavailabilityCustomer
            System.err.println("ERROR: The number of guests must be greater than 0.");
            return null;
        }
        // Check whether the number of children is greater than the number of guests
        if (numberOfChildren + numberOfInfants > numberOfGuests) {
            System.err.println("ERROR: The number of children must be less than or equal to the number of guests.");
            return null;
        }
        // Check whether the price is greater or equal to 0
        if (price < 0) {
            System.err.println("ERROR: The price must be greater or equal to 0.");
            return null;
        }
        // Check whether the apartment is available for the given dates using ReservationDAO
        // this function returns the id of the reservation if the apartment is available, -1 otherwise
        try {
            if (!reservationDAO.checkAvailability(accommodation, startDate, endDate)) {
                System.err.println("ERROR: The apartment is not available for the given dates.");
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        // The id passed as a parameter is ignored when passed to the DAO.
        Reservation reservation = new Reservation(-1, accommodation, startDate, endDate, numberOfGuests, numberOfChildren, numberOfInfants, customer, price, LocalDate.now(), cityTax);

        try {
            reservationDAO.insert(reservation);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        // return the effective reservation id present in the database
        try {
            return reservationDAO.get(reservationDAO.getCurrentId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean deleteReservation(Reservation reservation) {
        int reservationId = reservation.getId();
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

    public ArrayList<Reservation> getAccommodationReservations(Accommodation accommodation) {
        try {
            return reservationDAO.getAccommodationReservations(accommodation.getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Reservation> getAccommodationMonthReservations(Accommodation accommodation, int month, int year) {
        try {
            return reservationDAO.getAccommodationMonthReservations(accommodation.getId(), month, year);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean addUnavailableDates(Accommodation accommodation, LocalDate startDate, LocalDate endDate) {
        // Check if the accommodation is already unavailable for the given period of time
        if (addReservation(accommodation, startDate, endDate, 0, 0, 0, unavailabilityCustomer, 0, 0) == null) {
            System.err.println("ERROR: The accommodation is already unavailable for the given period of time.");
            return false;
        }
        return true;
    }

    // The following method allows the user to import data from Airbnb
    public void importFromAirbnb(Accommodation accommodation, String taxesFilePath, String reservationsFilePath) throws Exception {

        String confirmationCodeRecordTaxesFile = "Codice di Conferma"; // taxes file
        String confirmationCodeRecordReservationsFile = "Codice di conferma"; // reservations file

        String dateOfReservationRecord = "Prenotata"; // reservations file
        String dateOfReservationRecordFormat = "yyyy-MM-dd";

        String arrivalDateRecord = "Arrivo"; // taxes file
        String arrivalDateRecordFormat = "MM/dd/yyyy";

        String departureDateRecord = "";
        String departureDateRecordFormat = "MM/dd/yyyy";

        String numberOfAdultsRecord = "N. di adulti"; // reservations file
        String numberOfChildrenRecord = "N. di bambini"; // reservations file
        String numberOfInfantsRecord = "N. di neonati"; // reservations file
        String numberOfNightsRecord = "N. di notti"; // reservations file
        String guestNameRecord = "Nome dell'ospite"; // reservations file
        String phoneNumberRecord = "Contatti"; // reservations file
        String priceRecord = "Guadagno lordo"; // taxes file
        String cityTaxRecord = "Tasse di Soggiorno"; // taxes file

        BufferedReader taxesFileReader;
        BufferedReader reservationsFileReader;

        try {
            taxesFileReader = new BufferedReader(new FileReader(taxesFilePath));
        } catch (FileNotFoundException e) {
            throw new Exception("ERROR: The file " + taxesFilePath + " does not exist.", e);
        }

        try {
            reservationsFileReader = new BufferedReader(new FileReader(reservationsFilePath));
        } catch (FileNotFoundException e) {
            throw new Exception("ERROR: The file " + reservationsFilePath + " does not exist.", e);
        }

        // get the csv parser
        CSVParser csvParserTaxes = new CSVParser(taxesFileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        CSVParser csvParserReservations = new CSVParser(reservationsFileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        // Collect and check the headers of the files
        ArrayList<String> taxesFileHeader = new ArrayList<>(csvParserTaxes.getHeaderMap().keySet());
        ArrayList<String> reservationsFileHeader = new ArrayList<>(csvParserReservations.getHeaderMap().keySet());
        if (!taxesFileHeader.contains(confirmationCodeRecordTaxesFile) || !taxesFileHeader.contains(arrivalDateRecord) || !taxesFileHeader.contains(priceRecord) || !taxesFileHeader.contains(cityTaxRecord)) {
            String errorMessage = "ERROR: The file " + taxesFilePath + " does not contain all the required columns.";
            if (!reservationsFileHeader.contains(confirmationCodeRecordReservationsFile) || !reservationsFileHeader.contains(dateOfReservationRecord) || !reservationsFileHeader.contains(numberOfAdultsRecord) || !reservationsFileHeader.contains(numberOfChildrenRecord) || !reservationsFileHeader.contains(numberOfInfantsRecord) || !reservationsFileHeader.contains(numberOfNightsRecord) || !reservationsFileHeader.contains(guestNameRecord) || !reservationsFileHeader.contains(phoneNumberRecord)) {
                errorMessage += "\nERROR: The file " + reservationsFilePath + " does not contain all the required columns.";
            }
            // Properly close the readers
            taxesFileReader.close();
            reservationsFileReader.close();
            throw new Exception(errorMessage);
        }
        if (!reservationsFileHeader.contains(confirmationCodeRecordReservationsFile) || !reservationsFileHeader.contains(dateOfReservationRecord) || !reservationsFileHeader.contains(numberOfAdultsRecord) || !reservationsFileHeader.contains(numberOfChildrenRecord) || !reservationsFileHeader.contains(numberOfInfantsRecord) || !reservationsFileHeader.contains(numberOfNightsRecord) || !reservationsFileHeader.contains(guestNameRecord) || !reservationsFileHeader.contains(phoneNumberRecord)) {
            String errorMessageReservations = "ERROR: The file " + reservationsFilePath + " does not contain all the required columns.";
            // Properly close the readers
            taxesFileReader.close();
            reservationsFileReader.close();
            throw new Exception(errorMessageReservations);
        }

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
                    String departureDate = LocalDate.parse(taxesRecord.get(arrivalDateRecord), DateTimeFormatter.ofPattern("MM/dd/yyyy")).plusDays(Integer.parseInt(reservationsRecord.get(numberOfNightsRecord))).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    csvPrinter.printRecord(confirmationCode, reservationsRecord.get(dateOfReservationRecord), taxesRecord.get(arrivalDateRecord), departureDate, reservationsRecord.get(numberOfAdultsRecord), reservationsRecord.get(numberOfChildrenRecord), reservationsRecord.get(numberOfInfantsRecord), reservationsRecord.get(numberOfNightsRecord), reservationsRecord.get(guestNameRecord), reservationsRecord.get(phoneNumberRecord), taxesRecord.get(priceRecord), taxesRecord.get(cityTaxRecord));
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
            int numberOfChildren = Integer.parseInt(record.get("NUMBER_OF_CHILDREN"));
            int numberOfInfants = Integer.parseInt(record.get("NUMBER_OF_INFANTS"));
            int numberOfGuests = numberOfAdults + numberOfChildren;
            String guestName = record.get("GUEST_NAME");
            String phoneNumber = record.get("PHONE_NUMBER");
            double price = Double.parseDouble(record.get("PRICE"));
            double cityTaxAmount = Double.parseDouble(record.get("CITY_TAX"));

            // Create a new customer
            Customer customer = customerBook.addCustomer(guestName, "", phoneNumber);

            // Create a new reservation
            Reservation reservation = addReservation(accommodation, arrivalDate, departureDate, numberOfGuests, numberOfChildren, numberOfInfants, customer, price, cityTaxAmount);
            // If reservation is null, then the reservation probably already exists
            if (reservation != null) {
                // Set the date of reservation to the correct one
                reservation.setDateOfReservation(reservationDate);
                reservationDAO.update(reservation);
            } else {
                // Let's find out if the reservation already exists and update it
                reservation = reservationDAO.findReservationByPeriod(accommodation, arrivalDate, departureDate);
                if (reservation != null) {
                    // The reservation already exists, and the price and city tax amount must be summed to the existing ones
                    reservation.setPrice(reservation.getPrice() + price);
                    reservation.setCityTaxAmount(reservation.getCityTaxAmount() + cityTaxAmount);
                    // Update the reservation
                    try {
                        reservationDAO.update(reservation);
                    } catch (Exception e) {
                        throw new Exception(e);
                    }
                    // Warn the user that the error related to the unavailability of the accommodation can be ignored
                    System.err.println("WARNING: The reservation with confirmation code " + record.get("CODE") + " already exists. The price and city tax amount have been summed to the existing ones. Ignore the previous error.");
                } else {
                    throw new Exception("ERROR: Conflict between two reservations detected.");
                }
            }
        }

        // Delete the temporary file
        tempFile.delete();

        // Close the parser
        csvParserTempFile.close();
    }

}
