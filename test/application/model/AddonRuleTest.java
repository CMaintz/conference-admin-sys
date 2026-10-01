package application.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddonRuleTest {
    private static final LocalDate ARRIVAL = LocalDate.of(2023, 12, 18);
    private static final LocalDate DEPARTURE = LocalDate.of(2023, 12, 20);

    private Conference conference;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        conference = new Conference("Conf", "Odense", 1500, ARRIVAL, DEPARTURE);
        hotel = new Hotel("Svanen", "Odense", "1", 1000, 1200);
        conference.addHotelToConference(hotel);
    }

    private ConferenceBooking book(String name) {
        return new Participant(name, "1", "a")
                .createConfBook(conference, false, null, hotel, RoomType.SINGLE, ARRIVAL, DEPARTURE);
    }

    @Test
    void onlyAddonsOfferedByTheBookedHotelCanBeAdded() {
        Hotel other = new Hotel("Other", "x", "2", 1, 1);
        AddonPurchase foreign = other.createAddonPurchase("Spa", 300);
        ConferenceBooking booking = book("a");

        booking.addAddon(foreign);

        assertTrue(booking.getAddonsOrdered().isEmpty());
    }

    @Test
    void addonIsOnlyAddedOnce() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        ConferenceBooking booking = book("a");

        booking.addAddon(wifi);
        booking.addAddon(wifi);

        assertEquals(List.of(wifi), booking.getAddonsOrdered());
    }

    @Test
    void addonKnowsItsHotel() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);

        assertSame(hotel, wifi.getHotel());
        assertEquals("WiFi (50,-)", wifi.toString());
    }

    @Test
    void unbookedAddonCanBeDeleted() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        book("a");

        assertFalse(hotel.addonBooked(wifi));
    }

    @Test
    void addonBookedOnTheLatestBookingIsReported() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        book("a");
        book("b").addAddon(wifi);

        assertTrue(hotel.addonBooked(wifi));
    }

    @Test
    void setAddonsOrderedReplacesTheList() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        AddonPurchase breakfast = hotel.createAddonPurchase("Breakfast", 100);
        ConferenceBooking booking = book("a");
        booking.addAddon(wifi);

        booking.setAddonsOrdered(new ArrayList<>(List.of(breakfast)));
        assertEquals(List.of(breakfast), booking.getAddonsOrdered());

        booking.setAddonsOrdered(null);
        assertEquals(List.of(breakfast), booking.getAddonsOrdered());
    }
}
