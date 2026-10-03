package application.model;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private final ArrayList<Conference> conferences = new ArrayList<>();
    private final ArrayList<ConferenceBooking> bookings = new ArrayList<>();
    private final ArrayList<AddonPurchase> addonsOnOffer = new ArrayList<>();
    private Conference conference;
    private String location;
    private String name;
    private String phoneNumber;
    private RoomType roomType;
    private int roomTypeSinglePrice;
    private int roomTypeDoublePrice;


    public Hotel(String name, String location, String phoneNumber, int roomTypeSinglePrice, int roomTypeDoublePrice) {
        this.name = name;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.roomTypeSinglePrice = roomTypeSinglePrice;
        this.roomTypeDoublePrice = roomTypeDoublePrice;
    }

    public void addBooking(ConferenceBooking booking) {
        if (!bookings.contains(booking)) {
            bookings.add(booking);
        }
    }

    public void removeBooking(ConferenceBooking booking) {
        bookings.remove(booking);
    }

    public AddonPurchase createAddonPurchase(String name, int price) {
        AddonPurchase addonPurchase = new AddonPurchase(name, price, this);
        this.addonsOnOffer.add(addonPurchase);
        return addonPurchase;
    }

    public void removeAddon(AddonPurchase addon) {
        addonsOnOffer.remove(addon);
    }

    public ArrayList<AddonPurchase> getAddonsOnOffer() {
        return new ArrayList<>(addonsOnOffer);
    }

    public List<Participant> getGuests() {
        ArrayList<Participant> guests = new ArrayList<>();
        for (ConferenceBooking booking : bookings) {
            guests.add(booking.getParticipant());
        }
        return guests;
    }

    public boolean addonBooked(AddonPurchase addon) {
        for (ConferenceBooking booking : bookings) {
            if (booking.getAddonsOrdered().contains(addon)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getGuestBook() {
        ArrayList<String> guestBook = new ArrayList<>();
        List<Participant> derp = bookings.stream().filter(booking -> booking.getParticipant() != null).map(booking -> booking.getParticipant()).toList();
        for (Participant p : derp) {
            guestBook.add(p.getName());
        }
        return guestBook;
    }

    public ArrayList<ConferenceBooking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public String getBookingInfo() {
        String bookingInfo = "";
        for (ConferenceBooking booking : bookings) {
            bookingInfo += booking.getDetails();
        }
        return bookingInfo;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    // -----------------------------------------------

    public RoomType getRoomType() {
        return roomType;
    }

    public int calculateAccommodationPrice(RoomType roomType, ArrayList<AddonPurchase> chosenAddons, int days) {
        int accommodationPrice = 0;
        if (chosenAddons != null) {
            for (AddonPurchase addon : chosenAddons) {
                accommodationPrice += addon.getPrice();
            }
        }
        if (roomType == RoomType.SINGLE) {
            accommodationPrice += roomTypeSinglePrice;
        } else if (roomType == RoomType.DOUBLE) {
            accommodationPrice += roomTypeDoublePrice;
        }
        accommodationPrice = accommodationPrice * days;
        return accommodationPrice;
    }

    @Override
    public String toString() {
        return name + " (" + bookings.size() + " bookings)";
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public int getSingleRoomPrice() {
        return roomTypeSinglePrice;
    }

    public void setSingleRoomPrice(int singleRoomPrice) {
        this.roomTypeSinglePrice = singleRoomPrice;
    }

    public int getDoubleRoomPrice() {
        return roomTypeDoublePrice;
    }

    public void setDoubleRoomPrice(int doubleRoomPrice) {
        this.roomTypeDoublePrice = doubleRoomPrice;
    }
}
