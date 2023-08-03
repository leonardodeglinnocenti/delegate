package dao;

import domainModel.Room;
import domainModel.Apartment;
import domainModel.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface ReservationDAO extends DAO<Reservation, Integer>{
    boolean checkAvailability(int accommodationId, LocalDate startDate, LocalDate endDate) throws Exception;
    int getCurrentId() throws Exception;
    ArrayList<Apartment> getAvailableApartments(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws Exception;
    ArrayList<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate, int numberOfGuests) throws Exception;
    ArrayList<Reservation> getAccommodationReservations(int accommodationId) throws Exception;
}