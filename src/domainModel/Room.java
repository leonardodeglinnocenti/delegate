package domainModel;

import dao.RoomDAO;

public class Room extends Accommodation {
    public Room(int id, String description, int maxGuestsAllowed) {
        super(id, description, maxGuestsAllowed);
    }
    public Room(int id, String description, int maxGuestsAllowed, boolean hasPrivateBathroom, boolean hasKitchen) {
        super(id, description, maxGuestsAllowed);
        this.hasPrivateBathroom = hasPrivateBathroom;
        this.hasKitchen = hasKitchen;
    }

    private boolean hasPrivateBathroom;
    private boolean hasKitchen;

    public boolean getHasPrivateBathroom() {
        return hasPrivateBathroom;
    }

    public void setHasPrivateBathroom(boolean hasPrivateBathroom) {
        this.hasPrivateBathroom = hasPrivateBathroom;
    }

    public boolean getHasKitchen() {
        return hasKitchen;
    }

    public void setHasKitchen(boolean hasKitchen) {
        this.hasKitchen = hasKitchen;
    }

    public void printRoom() {
        System.out.println("Room ID: " + this.getId() + " " + this.getDescription() + " " + this.getMaxGuestsAllowed() + " " + this.getHasPrivateBathroom() + " " + this.getHasKitchen());
    }

}
