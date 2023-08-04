package dao;

import domainModel.Accommodation;
import domainModel.Room;
import domainModel.Apartment;
import domainModel.Reservation;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ReservationDAO extends DAO<Reservation, Integer>{
    boolean checkAvailability(Accommodation accommodation, LocalDate startDate, LocalDate endDate) throws Exception;
    int getCurrentId() throws Exception;
    ArrayList<Apartment> getAvailableApartments(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws Exception;
    ArrayList<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws Exception;
    ArrayList<Reservation> getAccommodationReservations(int accommodationId) throws Exception;
    ArrayList<Reservation> getAccommodationMonthReservations(int accommodationId, int month, int year) throws Exception;
    Reservation findReservationByPeriod(Accommodation accommodation, LocalDate startDate, LocalDate endDate) throws Exception;
}