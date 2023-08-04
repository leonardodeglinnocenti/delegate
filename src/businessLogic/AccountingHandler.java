package businessLogic;

// Import Apache Commons for managing CSV files
import domainModel.Accommodation;
import domainModel.Reservation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AccountingHandler {
    private final ReservationHandler reservationHandler;

    // This class is a singleton
    private static AccountingHandler instance = null;
    private AccountingHandler(ReservationHandler reservationHandler) {
        this.reservationHandler = reservationHandler;
    }
    public static AccountingHandler getInstance(ReservationHandler reservationHandler) {
        if (instance == null) {
            instance = new AccountingHandler(reservationHandler);
        }
        return instance;
    }

    // Evaluate the city tax for a given month and a given apartment
    public boolean evaluateCityTaxMonthlyDeclaration(Accommodation accommodation, int month, int year, double localCityTax, int localCityTaxDaysThreshold) {
        // Create a CSV file for the given month and year
        String fileName = "cityTax_" + accommodation.getId() + "_" + month + "_" + year + ".csv";

        // Get the reservations for the given month and year for the given apartment
        ArrayList<Reservation> reservations = reservationHandler.getAccommodationMonthReservations(accommodation, month, year);

        // Get the total number of nights that the apartment was booked for the given month and year
        int totalNights = 0;
        // Get the total amount of money that Airbnb declared to pay for the city tax for the given month and year
        double totalCityTaxAmount = 0;
        // Get the total number of guests that stayed in the apartment for the given month and year
        int totalGuests = 0;
        LocalDate arrivalDate;
        LocalDate departureDate;
        LocalDate currentMonth = LocalDate.of(year, month, 1);
        LocalDate start, end;
        long delta;
        for (Reservation reservation : reservations) {
            // Get the number of nights that the apartment was booked in the given month and year
            arrivalDate = reservation.getArrivalDate();
            departureDate = reservation.getDepartureDate();
            // Get the minimum between the departure date and the last day of the month
            end = departureDate.isBefore(currentMonth.plusMonths(1)) ? departureDate : currentMonth.plusMonths(1);
            // Get the maximum between the arrival date and the first day of the month
            start = arrivalDate.isAfter(currentMonth.minusDays(1)) ? arrivalDate : currentMonth;

            delta = (end.toEpochDay() - start.toEpochDay());

            // Get the number of days between currentMonth and arrivalDate
            // find number of days between currentMonth and arrivalDate
            long daysDifference = (currentMonth.toEpochDay() - arrivalDate.toEpochDay());
            delta = Math.min(Math.min(delta, localCityTaxDaysThreshold), (localCityTaxDaysThreshold - (int)daysDifference));

            // Evaluate if the reservation is eligible for the city tax for the given month and year
            if (delta < 0) {
                delta = 0;
            } else {
                // Note that the totalGuests counter is incremented only if the reservation is eligible for the city tax
                totalGuests += reservation.getNumberOfGuests();
                totalCityTaxAmount += reservation.getCityTaxAmount();
            }

            totalNights += delta * reservation.getNumberOfGuests();
        }

        // Manually calculate the city tax for the given month and year
        double cityTaxEvaluated = totalNights * localCityTax;

        // Write data to the CSV file
        try {
            CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(fileName), CSVFormat.DEFAULT.withHeader("TOTAL CITY TAX AMOUNT", "TOTAL GUESTS", "TOTAL NIGHTS", "EVALUATED CITY TAX"));
            csvPrinter.printRecord(totalCityTaxAmount, totalGuests, totalNights, cityTaxEvaluated);
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to CSV file.");
            return false;
        }

        if (cityTaxEvaluated != totalCityTaxAmount) {
            System.err.println("ERROR: There's a mismatch between the evaluated city tax and the city tax declared by Airbnb.");
        }

        // Print all the collected data
        System.out.println("Total city tax amount: " + totalCityTaxAmount);
        System.out.println("Total guests: " + totalGuests);
        System.out.println("Total nights: " + totalNights);
        System.out.println("City tax evaluated: " + cityTaxEvaluated);

        return true;

    }

}
