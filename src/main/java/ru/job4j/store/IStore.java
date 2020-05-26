package ru.job4j.store;

import ru.job4j.model.Account;
import ru.job4j.model.Hall;

import java.util.List;

public interface IStore {
    List<Hall> places();

    Hall getPlace(Hall hall);

    boolean makePayment(Hall hall, Account account);
}
