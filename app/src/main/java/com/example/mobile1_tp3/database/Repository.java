package com.example.mobile1_tp3.database;

import java.util.List;

public interface Repository<T> {

    void create(T item);

    T readById(Long id);

    List<T> readAll();

    void update(T item);

    void delete(T item);

}
