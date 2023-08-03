package domainModel;

import java.util.ArrayList;

public abstract class Accommodation {
    int id;
    // The id is automatically generated and incremented.
    String description;
    private int maxGuestsAllowed;

    public Accommodation(int id, String description, int maxGuestsAllowed) {
        this.id = id;
        this.description = description;
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

    public void setMaxGuestsAllowed(int input) {
        maxGuestsAllowed = input;
    }

}
