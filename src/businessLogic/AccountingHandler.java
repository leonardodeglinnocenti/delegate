package businessLogic;

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



}
