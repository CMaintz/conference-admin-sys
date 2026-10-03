package application.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Excursion {
    private String location;
    private final Conference conference;
    private final ArrayList<Companion> participatingCompanions = new ArrayList<>();
    private String name;
    private LocalDate date;
    private int price;

    Excursion(String name, String location, Conference conference, LocalDate date, int price) {
        this.name = name;
        this.location = location;
        this.conference = conference;
        this.date = date;
        this.price = price;
    }

    public Conference getConference() {
        return conference;
    }

    public ArrayList<Companion> getParticipatingCompanions() {
        return new ArrayList<>(participatingCompanions);
    }

    public void addCompanionToExcursion(Companion companion) {
        if (!participatingCompanions.contains(companion)) {
            participatingCompanions.add(companion);
        }
    }

    public void removeCompanionFromExcursion(Companion companion) {
        participatingCompanions.remove(companion);
        companion.removeExcursion(this);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " (" + price + ",-)";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return location;
    }

    public void setAddress(String address) {
        this.location = address;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

