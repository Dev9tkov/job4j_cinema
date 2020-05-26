package ru.job4j.store;

import ru.job4j.model.Hall;

/**
 * Класс для проверки запросов БД
 */
public class DbStoreMain {
    public static void main(String[] args) {
        IStore iStore = DbStore.instOf();
        for (Hall val : iStore.places()) {
            System.out.println(val);
        }
    }
}
