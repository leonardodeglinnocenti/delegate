package businessLogic;

// Import Apache Commons for managing CSV files
import dao.ReservationDAO;
import dao.SQLiteLocalTaxDAO;
import dao.SQLiteReservationDAO;
import domainModel.Accommodation;
import domainModel.Reservation;
import domainModel.LocalTax;
import dao.LocalTaxDAO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AccountingHandler {
    private final ReservationHandler reservationHandler;
    private final LocalTaxDAO localTaxDAO = new SQLiteLocalTaxDAO();

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

    // Add a new local tax to the database
    public boolean addLocalTax(String name, double amount, String target, int daysThreshold, LocalDate startDate, LocalDate endDate) throws Exception {
        // Check if target is valid
        if (!target.equals("adults") && !target.equals("children") && !target.equals("infants")) {
            System.err.println("ERROR: Target must be one of the following: adults, children, infants");
            return false;
        }
        // Check data integrity
        if (startDate.isAfter(endDate)) {
            System.err.println("ERROR: Start date must be before end date");
            return false;
        }
        // Check if the amount is positive or zero
        if (amount < 0) {
            System.err.println("ERROR: Amount must be positive or zero");
            return false;
        }
        // Check if the days threshold is positive
        if (daysThreshold < 0) {
            System.err.println("ERROR: Days threshold must be positive");
            return false;
        }
        LocalTax localTax = new LocalTax(name, amount, target, daysThreshold, startDate, endDate);
        localTaxDAO.insert(localTax);
        return true;
    }

    public boolean deleteLocalTax(int id) throws Exception {
        return localTaxDAO.delete(id);
    }

    public void evaluateLocalTaxes(Reservation reservation) throws Exception {
        // Get the number of nights that the apartment was booked in the given month and year
        LocalDate start = reservation.getArrivalDate();
        LocalDate end = reservation.getDepartureDate();
        // Get the local taxes for the given period
        ArrayList<LocalTax> adultsLocalTaxes = localTaxDAO.getLocalTaxesByTarget("adults", start, end);
        ArrayList<LocalTax> childrenLocalTaxes = localTaxDAO.getLocalTaxesByTarget("children", start, end);
        ArrayList<LocalTax> infantsLocalTaxes = localTaxDAO.getLocalTaxesByTarget("infants", start, end);

        // Evaluate the local taxes for the given period
        // Evaluate the various amounts to pay according to the possible changes in the local taxes by
        // determining how many days of each local tax are in the given period
        double adultsAmount = 0;
        double childrenAmount = 0;
        double infantsAmount = 0;

        for (LocalTax localTax : adultsLocalTaxes) {
            // Get the minimum between the end date of the local tax and the departure date
            LocalDate localTaxEndDate = localTax.getEndDate().isBefore(end) ? localTax.getEndDate().plusDays(1) : end;
            // Get the maximum between the start date of the local tax and the arrival date
            LocalDate localTaxStartDate = localTax.getStartDate().isAfter(start) ? localTax.getStartDate() : start;

            long delta = (localTaxEndDate.toEpochDay() - localTaxStartDate.toEpochDay());

            adultsAmount += localTax.getAmount() * delta;
        }

        for (LocalTax localTax : childrenLocalTaxes) {
            // Get the minimum between the end date of the local tax and the departure date
            LocalDate localTaxEndDate = localTax.getEndDate().isBefore(end) ? localTax.getEndDate().plusDays(1) : end;
            // Get the maximum between the start date of the local tax and the arrival date
            LocalDate localTaxStartDate = localTax.getStartDate().isAfter(start) ? localTax.getStartDate() : start;

            long delta = (localTaxEndDate.toEpochDay() - localTaxStartDate.toEpochDay());

            childrenAmount += localTax.getAmount() * delta;
        }

        for (LocalTax localTax : infantsLocalTaxes) {
            // Get the minimum between the end date of the local tax and the departure date
            LocalDate localTaxEndDate = localTax.getEndDate().isBefore(end) ? localTax.getEndDate().plusDays(1) : end;
            // Get the maximum between the start date of the local tax and the arrival date
            LocalDate localTaxStartDate = localTax.getStartDate().isAfter(start) ? localTax.getStartDate() : start;

            long delta = (localTaxEndDate.toEpochDay() - localTaxStartDate.toEpochDay());

            infantsAmount += localTax.getAmount() * delta;
        }

        // Manually calculate the total amount to pay for the given month and year
        int numberOfAdults = reservation.getNumberOfGuests() - reservation.getNumberOfChildren() - reservation.getNumberOfInfants();
        double totalAmountEvaluated = (adultsAmount * numberOfAdults) + (childrenAmount * reservation.getNumberOfChildren()) + (infantsAmount * reservation.getNumberOfInfants());

        // Save in the database the amount to pay for the given local taxes
        reservation.setCityTaxAmount(totalAmountEvaluated);
        ReservationDAO reservationDAO = new SQLiteReservationDAO();
        reservationDAO.update(reservation);

    }

    // Evaluate the city tax for a given month and a given apartment
    public boolean evaluateCityTaxMonthlyDeclaration(Accommodation accommodation, int month, int year) throws Exception {
        // Check if the accommodation is valid
        if (accommodation == null) {
            System.err.println("ERROR: Accommodation is null");
            return false;
        }

        // Create a CSV file for the given month and year
        String fileName = "cityTax_" + accommodation.getId() + "_" + month + "_" + year + ".csv";

        // Get the reservations for the given month and year for the given apartment
        ArrayList<Reservation> reservations = reservationHandler.getAccommodationMonthReservations(accommodation, month, year);

        // Print all the collected reservations
        System.out.println("Reservations for the given month and year:");
        for (Reservation reservation : reservations) {
            reservation.printReservation();
        }

        // Get the total number of nights that the apartment was booked for the given month and year
        int totalNights = 0;
        // Get the total amount of money that Airbnb declared to pay for the city tax for the given month and year
        double totalCityTaxAmount = 0;

        // Get the total number of guests that stayed in the apartment for the given month and year
        int totalGuests = 0;
        LocalDate currentMonth = LocalDate.of(year, month, 1);
        double totalAmountEvaluated = 0;
        for (Reservation reservation : reservations) {
            // Get the number of nights that the apartment was booked in the given month and year
            LocalDate arrivalDate = reservation.getArrivalDate();
            LocalDate departureDate = reservation.getDepartureDate();
            // Get the minimum between the departure date and the last day of the month
            LocalDate end = departureDate.isBefore(currentMonth.plusMonths(1)) ? departureDate : currentMonth.plusMonths(1);
            // Get the maximum between the arrival date and the first day of the month
            LocalDate start = arrivalDate.isAfter(currentMonth.minusDays(1)) ? arrivalDate : currentMonth;

            // Get the local taxes for the given period
            ArrayList<LocalTax> adultsLocalTaxes = localTaxDAO.getLocalTaxesByTarget("adults", start, end);
            ArrayList<LocalTax> childrenLocalTaxes = localTaxDAO.getLocalTaxesByTarget("children", start, end);
            ArrayList<LocalTax> infantsLocalTaxes = localTaxDAO.getLocalTaxesByTarget("infants", start, end);

            // Evaluate the local taxes for the given period
            // Evaluate the various amounts to pay according to the possible changes in the local taxes by
            // determining how many days of each local tax are in the given period
            double adultsAmount = 0;
            double childrenAmount = 0;
            double infantsAmount = 0;

            // If there are multiple local taxes for the same target, the number of guests is counted only once
            boolean firstIteration = true;

            for (LocalTax localTax : adultsLocalTaxes) {
                // Get the minimum between the end date of the local tax and the departure date
                LocalDate localTaxEndDate = localTax.getEndDate().isBefore(end) ? localTax.getEndDate().plusDays(1) : end;
                // Get the maximum between the start date of the local tax and the arrival date
                LocalDate localTaxStartDate = localTax.getStartDate().isAfter(start) ? localTax.getStartDate() : start;
                // localTaxEndDate and localTaxStartDate should be both in the given month and year
                localTaxEndDate = localTaxEndDate.isBefore(currentMonth.plusMonths(1)) ? localTaxEndDate : currentMonth.plusMonths(1);
                localTaxStartDate = localTaxStartDate.isAfter(currentMonth.minusDays(1)) ? localTaxStartDate : currentMonth;
                // Get the number of days between the start date and the end date of the local tax

                long delta = (localTaxEndDate.toEpochDay() - localTaxStartDate.toEpochDay());

                // Get the number of days between currentMonth and arrivalDate
                // find number of days between currentMonth and arrivalDate
                long daysDifference = (currentMonth.toEpochDay() - arrivalDate.toEpochDay());
                delta = Math.min(Math.min(delta, localTax.getDaysThreshold()), (localTax.getDaysThreshold() - (int) daysDifference));

                // Evaluate if the reservation is eligible for the city tax for the given month and year
                if (delta <= 0) {
                    delta = 0;
                } else {
                    // Note that the totalGuests counter is incremented only if the reservation is eligible for the city tax
                    if (firstIteration) {
                        totalNights += delta * (reservation.getNumberOfGuests() - reservation.getNumberOfChildren() - reservation.getNumberOfInfants());
                        totalGuests += (reservation.getNumberOfGuests() - reservation.getNumberOfChildren() - reservation.getNumberOfInfants());
                        // The city tax amount harvested collecting data from the database is counted only once here
                        // Only get the city tax percentage related to the current month and year
                        int totalLength = Math.min((int) (reservation.getDepartureDate().toEpochDay() - reservation.getArrivalDate().toEpochDay()), localTax.getDaysThreshold());
                        daysDifference = (currentMonth.toEpochDay() - arrivalDate.toEpochDay());
                        if (daysDifference > 0 && daysDifference < localTax.getDaysThreshold()) {
                            int daysToPay = totalLength - (int)daysDifference;
                            totalCityTaxAmount += (reservation.getCityTaxAmount() * ((double) daysToPay / totalLength));
                        } else {
                            boolean check = departureDate.isAfter(currentMonth.plusMonths(1));
                            if (check && daysDifference < localTax.getDaysThreshold()) {
                                daysDifference = currentMonth.plusMonths(1).toEpochDay() - arrivalDate.toEpochDay();
                                totalCityTaxAmount += (reservation.getCityTaxAmount() * ((double) daysDifference / totalLength));
                            } else
                                totalCityTaxAmount += reservation.getCityTaxAmount();
                        }
                        // If there are multiple local taxes for the same period warn the user about the inaccuracy of the total city tax amount
                        if (localTaxDAO.getLocalTaxesByTarget("adults", arrivalDate, departureDate).size() > 1)
                            System.err.println("WARNING: The city tax amount has changed during the given month and year so Total city tax amount is not accurate");
                        firstIteration = false;
                    }
                }
                adultsAmount += localTax.getAmount() * delta;
            }

            firstIteration = true;

            for (LocalTax localTax : childrenLocalTaxes) {
                // Get the minimum between the end date of the local tax and the departure date
                LocalDate localTaxEndDate = localTax.getEndDate().isBefore(end) ? localTax.getEndDate().plusDays(1) : end;
                // Get the maximum between the start date of the local tax and the arrival date
                LocalDate localTaxStartDate = localTax.getStartDate().isAfter(start) ? localTax.getStartDate() : start;
                // localTaxEndDate and localTaxStartDate should be both in the given month and year
                localTaxEndDate = localTaxEndDate.isBefore(currentMonth.plusMonths(1)) ? localTaxEndDate : currentMonth.plusMonths(1);
                localTaxStartDate = localTaxStartDate.isAfter(currentMonth.minusDays(1)) ? localTaxStartDate : currentMonth;
                // Get the number of days between the start date and the end date of the local tax

                long delta = (localTaxEndDate.toEpochDay() - localTaxStartDate.toEpochDay());

                // Get the number of days between currentMonth and arrivalDate
                // find number of days between currentMonth and arrivalDate
                long daysDifference = (currentMonth.toEpochDay() - arrivalDate.toEpochDay());
                delta = Math.min(Math.min(delta, localTax.getDaysThreshold()), (localTax.getDaysThreshold() - (int) daysDifference));

                // Evaluate if the reservation is eligible for the city tax for the given month and year
                if (delta <= 0) {
                    delta = 0;
                } else {
                    // Note that the totalGuests counter is incremented only if the reservation is eligible for the city tax
                    if (firstIteration) {
                        totalNights += delta * reservation.getNumberOfChildren();
                        totalGuests += reservation.getNumberOfChildren();
                        firstIteration = false;
                    }
                }
                childrenAmount += localTax.getAmount() * delta;
            }

            firstIteration = true;

            for (LocalTax localTax : infantsLocalTaxes) {
                // Get the minimum between the end date of the local tax and the departure date
                LocalDate localTaxEndDate = localTax.getEndDate().isBefore(end) ? localTax.getEndDate().plusDays(1) : end;
                // Get the maximum between the start date of the local tax and the arrival date
                LocalDate localTaxStartDate = localTax.getStartDate().isAfter(start) ? localTax.getStartDate() : start;
                // localTaxEndDate and localTaxStartDate should be both in the given month and year
                localTaxEndDate = localTaxEndDate.isBefore(currentMonth.plusMonths(1)) ? localTaxEndDate : currentMonth.plusMonths(1);
                localTaxStartDate = localTaxStartDate.isAfter(currentMonth.minusDays(1)) ? localTaxStartDate : currentMonth;
                // Get the number of days between the start date and the end date of the local tax

                long delta = (localTaxEndDate.toEpochDay() - localTaxStartDate.toEpochDay());

                // Get the number of days between currentMonth and arrivalDate
                // find number of days between currentMonth and arrivalDate
                long daysDifference = (currentMonth.toEpochDay() - arrivalDate.toEpochDay());
                delta = Math.min(Math.min(delta, localTax.getDaysThreshold()), (localTax.getDaysThreshold() - (int) daysDifference));

                // Evaluate if the reservation is eligible for the city tax for the given month and year
                if (delta <= 0) {
                    delta = 0;
                } else {
                    // Note that the totalGuests counter is incremented only if the reservation is eligible for the city tax
                    if (firstIteration) {
                        totalNights += delta * reservation.getNumberOfInfants();
                        totalGuests += reservation.getNumberOfInfants();
                        firstIteration = false;
                    }
                }
                infantsAmount += localTax.getAmount() * delta;
            }

            // Manually calculate the total amount to pay for the given month and year
            int numberOfAdults = reservation.getNumberOfGuests() - reservation.getNumberOfChildren() - reservation.getNumberOfInfants();
            totalAmountEvaluated += (adultsAmount * numberOfAdults) + (childrenAmount * reservation.getNumberOfChildren()) + (infantsAmount * reservation.getNumberOfInfants());
        }

        // Write data to the CSV file
        try {
            CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(fileName), CSVFormat.DEFAULT.withHeader("TOTAL CITY TAX AMOUNT", "TOTAL GUESTS", "TOTAL NIGHTS", "EVALUATED CITY TAX"));
            csvPrinter.printRecord(totalCityTaxAmount, totalGuests, totalNights, totalAmountEvaluated);
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            System.err.println("ERROR: Could not write to CSV file.");
            return false;
        }

        if (totalAmountEvaluated != totalCityTaxAmount) {
            System.err.println("ERROR: There's a mismatch between the evaluated city tax and the city tax related to each reservation.");
        }

        // Print all the collected data
        System.out.println("Total city tax amount: " + totalCityTaxAmount);
        System.out.println("Total guests: " + totalGuests);
        System.out.println("Total nights: " + totalNights);
        System.out.println("City tax evaluated: " + totalAmountEvaluated);

        return true;

    }

}

