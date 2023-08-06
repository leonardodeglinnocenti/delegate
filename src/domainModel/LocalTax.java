package domainModel;

import java.time.LocalDate;

public class LocalTax {
    // The id will be assigned by the database
    private int id = 0;
    private String description;
    private double amount;

    // Get type of target (between adults, children and infants)
    private String target;

    // Get the number of days that the target is eligible for the city tax
    private int daysThreshold;

    // Start date and end date of the city tax
    private LocalDate startDate;
    private LocalDate endDate;

    public LocalTax(String description, double amount, String target, int daysThreshold, LocalDate startDate, LocalDate endDate) {
        this.description = description;
        this.amount = amount;
        this.target = target;
        this.daysThreshold = daysThreshold;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getTarget() {
        return target;
    }

    public int getDaysThreshold() {
        return daysThreshold;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void printLocalCityTax() {
        System.out.println("Amount: " + amount);
        System.out.println("Target: " + target);
        System.out.println("Days threshold: " + daysThreshold);
    }

}
