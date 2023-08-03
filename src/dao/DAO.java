package dao;
import java.util.ArrayList;

public interface DAO<T, ID> {

    T get(ID id) throws Exception;

    ArrayList<T> getAll() throws Exception;

    void insert(T t) throws Exception;

    void update(T t) throws Exception;

    boolean delete(ID id) throws Exception;

    int getNextId() throws Exception;

}
