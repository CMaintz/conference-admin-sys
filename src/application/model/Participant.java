package application.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Participant {
    private final ArrayList<ConferenceBooking> bookings = new ArrayList<>();
    private final ArrayList<Companion> companions = new ArrayList<>();
    private String name;
    private String phoneNr;
    private String address;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String company;

    public Participant(String name, String phoneNr, String address) {
        this.name = name;
        this.phoneNr = phoneNr;
        this.address = address;
        this.company = "";
    }

    public ConferenceBooking createConfBook(Conference conference, boolean isLecturer, Companion companion, Hotel hotel, RoomType roomType, LocalDate arrivalDate, LocalDate departureDate) {
        ConferenceBooking booking = new ConferenceBooking(conference, this, isLecturer, companion, hotel, roomType, arrivalDate, departureDate);
        conference.addBooking(booking);
        bookings.add(booking);
        if (companion != null && !companions.contains(companion)) {
            companions.add(companion);
        }
        return booking;
    }

    public void removeAllBookings() {
        for (ConferenceBooking booking : bookings) {
            booking.getConference().removeBooking(booking);
            if (booking.getHotel() != null) {
                booking.getHotel().removeBooking(booking);
            }
        }
        this.bookings.clear();
    }

    public Companion createCompanion(String navn, String tlf, String addresse) {
        Companion companion = new Companion(navn, tlf, addresse, this);
        companions.add(companion);
        return companion;
    }

    public void removeCompanion(Companion companion) {
        companions.remove(companion);
    }

    public ArrayList<ConferenceBooking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public ArrayList<Companion> getCompanions() {
        return new ArrayList<>(companions);
    }

    public void addBooking(ConferenceBooking booking) {
        bookings.add(booking);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNr;
    }

    public void setPhoneNumber(String trim) {
        this.phoneNr = trim;
    }

    public void setPhoneNr(String phoneNumber) {
        this.phoneNr = phoneNumber;
    }

    public String getDetails() {
        String deets = "";
        deets += "Name: " + name;
        deets += "\nAddress: " + address;
        deets += "\nPhone number: " + phoneNr;
        if (company.length() > 0) {
            deets += "\nCompany: " + company;
        }
        if (bookings.size() > 0) {
            deets += "\nBookings:";
            for (ConferenceBooking booking : bookings) {
                deets += "\n" + booking.getConference().getName();
            }
        }
        if (companions.size() > 0) {
            deets += "\nCompanions:";
            for (Companion companion : companions) {
                deets += "\n" + companion.getName();
            }
        }
        return deets;
    }
}
