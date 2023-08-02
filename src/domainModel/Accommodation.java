package domainModel;

import java.util.ArrayList;

public abstract class Accommodation {
    int id;
    // The id is automatically generated and incremented.
    String description;
    ArrayList<RefundPolicy> refundPolicies;
    private int maxGuestsAllowed;

    public Accommodation(int id, String description, int maxGuestsAllowed) {
        this.id = id;
        this.description = description;
        this.refundPolicies = new ArrayList<>();
        this.maxGuestsAllowed = maxGuestsAllowed;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String input) {
        description = input;
    }

    public int getMaxGuestsAllowed() {
        return maxGuestsAllowed;
    }

    public void setMaxGuestsAllowed(int input){
        maxGuestsAllowed = input;
    }

    public Boolean addRefundPolicy(int daysInAdvance, int percentage) {
        if (daysInAdvance < 0 || percentage < 0 || percentage > 100) {
            System.err.println("Invalid input.");
            return false;
        }
        // Check if the given daysInAdvance already exists in the refundPolicies list and update it if it does.
        for (RefundPolicy refundPolicy : refundPolicies) {
            if (refundPolicy.daysInAdvance == daysInAdvance) {
                refundPolicy.percentage = percentage;
                System.err.println("Refund policy already exists.");
                return false;
            }
        }
        // Otherwise, add a new domainModel.RefundPolicy to the refundPolicies list.
        RefundPolicy refundPolicy = new RefundPolicy();
        refundPolicy.daysInAdvance = daysInAdvance;
        refundPolicy.percentage = percentage;
        refundPolicies.add(refundPolicy);
        return true;
    }

    public Boolean removeRefundPolicy(int daysInAdvance) {
        // Check if the given daysInAdvance exists in the refundPolicies list and remove it if it does.
        for (RefundPolicy refundPolicy : refundPolicies) {
            if (refundPolicy.daysInAdvance == daysInAdvance) {
                refundPolicies.remove(refundPolicy);
                return true;
            }
        }
        System.err.println("Refund policy does not exist.");
        return false;
    }
}

class RefundPolicy {
    public int daysInAdvance;
    public int percentage;
}
