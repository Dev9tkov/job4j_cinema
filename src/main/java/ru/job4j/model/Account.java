package ru.job4j.model;

import java.util.Objects;

public class Account {
    private String name;
    private String telNumber;
    private Hall hall;

    public Account(String name, String telNumber, Hall hall) {
        this.name = name;
        this.telNumber = telNumber;
        this.hall = hall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(name, account.name) &&
                Objects.equals(telNumber, account.telNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, telNumber);
    }
}
