package ru.job4j.model;

import java.util.Comparator;
import java.util.Objects;

public class Hall implements Comparable<Hall> {
    private int row;
    private int place;
    private int price;
    private boolean free;

    public Hall(int row, int place, int price, boolean free) {
        this.row = row;
        this.place = place;
        this.price = price;
        this.free = free;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hall hall = (Hall) o;
        return row == hall.row &&
                place == hall.place &&
                price == hall.price &&
                free == hall.free;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, place, price, free);
    }

    @Override
    public String toString() {
        return "Hall{" +
                "row=" + row +
                ", place=" + place +
                ", price=" + price +
                ", free=" + free +
                '}';
    }

    /**
     * Компаратор нужен для одинакового вывода списка мест до и после обновления БД
     */
    @Override
    public int compareTo(Hall o) {
        return Comparator.comparing(Hall::getRow)
                .thenComparing(Hall::getPlace)
                .compare(this, o);
    }
}
