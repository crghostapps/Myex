package lu.crghost.myex.dao;

import java.util.List;

/**
 * Created by CR on 24/12/2014.
 */
public interface DbDao<T> {
    long save(T type);
    void update(T type);
    void delete(T type);
    T get(long id);
    List<T> getAll(String selection, String[] selectionArgs);
    List<T> getAll(String selection, String[] selectionArgs, String order);
}
