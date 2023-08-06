package dao;

import domainModel.LocalTax;

import java.time.LocalDate;
import java.util.ArrayList;

public interface LocalTaxDAO extends DAO<LocalTax, Integer>{

    public ArrayList<LocalTax> getLocalTaxesByTarget(String target, LocalDate startDate, LocalDate endDate) throws Exception;

}
