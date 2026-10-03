package application.controller;

import application.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.Storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    private static final LocalDate START = LocalDate.of(2024, 4, 24);
    private static final LocalDate END = LocalDate.of(2024, 4, 26);

    @BeforeEach
    void clearStorage() {
        Storage.participants.clear();
        Storage.conferences.clear();
        Storage.hotels.clear();
    }

    @Test
    void createUpdateAndDeleteConference() {
        Conference c = Controller.createConference("Fight Club", "Basement", 150, START, END);
        assertEquals(List.of(c), Controller.getConferences());

        Controller.updateConference(c, "Book Club", "Library", 200, START.plusDays(1), END.plusDays(1));
        assertEquals("Book Club", c.getName());
        assertEquals("Library", c.getAddress());
        assertEquals(200, c.getDayPrice());
        assertEquals(START.plusDays(1), c.getStartDate());
        assertEquals(END.plusDays(1), c.getEndDate());

        Controller.deleteConference(c);
        assertTrue(Controller.getConferences().isEmpty());
    }

    @Test
    void createUpdateAndDeleteParticipant() {
        Participant p = Controller.createParticipant("Finn", "1234", "Here");
        assertEquals(List.of(p), Controller.getParticipants());

        Controller.updateParticipant(p, "Finn Madsen", "There", "5678");
        assertEquals("Finn Madsen", p.getName());
        assertEquals("There", p.getAddress());
        assertEquals("5678", p.getPhoneNumber());

        Controller.deleteParticipant(p);
        assertTrue(Controller.getParticipants().isEmpty());
    }

    @Test
    void deletingParticipantRemovesTheirBookingsFromConferences() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Participant p = Controller.createParticipant("Finn", "1", "a");
        Participant keep = Controller.createParticipant("Lone", "2", "b");
        Controller.createBooking(c, p, false, null, null, null, START, END);
        ConferenceBooking kept = Controller.createBooking(c, keep, false, null, null, null, START, END);

        Controller.deleteParticipant(p);

        assertEquals(List.of(kept), c.getBookings());
        assertTrue(p.getBookings().isEmpty());
    }

    @Test
    void createUpdateAndDeleteHotel() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Hotel h = Controller.createHotel(c, "Svanen", "Odense", "123", 500, 700);
        assertEquals(List.of(h), c.getHotels());
        assertEquals(List.of(h), Storage.getHotels());
        assertSame(c, h.getConference());

        Controller.updateHotel(c, h, "Phønix", "Aarhus", "456", 600, 800);
        assertEquals("Phønix", h.getName());
        assertEquals("Aarhus", h.getAddress());
        assertEquals("456", h.getPhoneNumber());
        assertEquals(600, h.getSingleRoomPrice());
        assertEquals(800, h.getDoubleRoomPrice());

        Controller.deleteHotel(h);
        assertTrue(c.getHotels().isEmpty());
        assertTrue(Storage.getHotels().isEmpty());
    }

    @Test
    void createAndDeleteHotelAddon() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Hotel h = Controller.createHotel(c, "Svanen", "Odense", "123", 500, 700);

        AddonPurchase wifi = Controller.createHotelAddon(h, "WiFi", 50);
        assertEquals(List.of(wifi), h.getAddonsOnOffer());

        Controller.deleteAddon(wifi);
        assertTrue(h.getAddonsOnOffer().isEmpty());
    }

    @Test
    void createUpdateAndDeleteExcursion() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Excursion e = Controller.createExcursion("Museum", "Kolding", c, START, 200);
        assertEquals(List.of(e), c.getExcursions());
        assertSame(c, e.getConference());

        Controller.updateExcursion(e, "Castle", "Egeskov", END, 75);
        assertEquals("Castle", e.getName());
        assertEquals(END, e.getDate());
        assertEquals(75, e.getPrice());

        Controller.deleteExcursion(e);
        assertTrue(c.getExcursions().isEmpty());
    }

    @Test
    void createUpdateAndDeleteCompanion() {
        Participant p = Controller.createParticipant("Finn", "1", "a");
        Companion comp = Controller.createCompanion("Mie", "22", "There", p);
        assertEquals(List.of(comp), p.getCompanions());
        assertSame(p, comp.getParticipant());

        Controller.updateCompanion(comp, "Mie Sommer", "Elsewhere", "33");
        assertEquals("Mie Sommer", comp.getName());
        assertEquals("Elsewhere", comp.getAddress());
        assertEquals("33", comp.getPhoneNumber());

        Controller.deleteCompanion(p, comp);
        assertTrue(p.getCompanions().isEmpty());
    }

    @Test
    void createBookingWiresParticipantConferenceAndHotel() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Hotel h = Controller.createHotel(c, "Svanen", "Odense", "123", 500, 700);
        Participant p = Controller.createParticipant("Finn", "1", "a");

        ConferenceBooking b = Controller.createBooking(c, p, false, null, h, RoomType.SINGLE, START, END);

        assertSame(c, b.getConference());
        assertSame(p, b.getParticipant());
        assertEquals(List.of(b), p.getBookings());
        assertEquals(List.of(b), c.getBookings());
        assertEquals(List.of(b), h.getBookings());
        assertEquals(300 + 1000, b.getTotalPrice());
    }

    @Test
    void addHotelAndAddonToExistingBooking() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Hotel h = Controller.createHotel(c, "Svanen", "Odense", "123", 500, 700);
        AddonPurchase wifi = Controller.createHotelAddon(h, "WiFi", 50);
        Participant p = Controller.createParticipant("Finn", "1", "a");
        ConferenceBooking b = Controller.createBooking(c, p, false, null, null, null, START, END);

        Controller.addHotelToBooking(b, h, RoomType.DOUBLE);
        Controller.addAddonToBooking(b, wifi);

        assertEquals((700 + 50) * 2, b.getHotelPrice());
    }

    @Test
    void addHotelToBookingIgnoresHotelsOfOtherConferences() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Conference other = Controller.createConference("Other", "y", 100, START, END);
        Hotel foreign = Controller.createHotel(other, "Far", "y", "1", 500, 700);
        ConferenceBooking b = Controller.createBooking(c, Controller.createParticipant("Finn", "1", "a"),
                false, null, null, null, START, END);

        Controller.addHotelToBooking(b, foreign, RoomType.SINGLE);

        assertEquals(0, b.getHotelPrice());
    }

    @Test
    void setExcursionsOrderedGoesThroughTheCompanion() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Excursion e = Controller.createExcursion("Museum", "Kolding", c, START, 200);
        Participant p = Controller.createParticipant("Finn", "1", "a");
        Companion comp = Controller.createCompanion("Mie", "22", "There", p);
        ConferenceBooking b = Controller.createBooking(c, p, true, comp, null, null, START, END);

        Controller.setExcursionsOrdered(b, new ArrayList<>(List.of(e)));

        assertEquals(List.of(e), comp.getExcursions());
        assertEquals(200, b.getTotalPrice());
    }

    @Test
    void selectionSortOrdersByParticipantNameIgnoringCase() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        ConferenceBooking peter = Controller.createBooking(c, Controller.createParticipant("peter", "1", "a"), false, null, null, null, START, END);
        ConferenceBooking anders = Controller.createBooking(c, Controller.createParticipant("Anders", "1", "a"), false, null, null, null, START, END);
        ConferenceBooking lone = Controller.createBooking(c, Controller.createParticipant("Lone", "1", "a"), false, null, null, null, START, END);
        ArrayList<ConferenceBooking> bookings = new ArrayList<>(List.of(peter, anders, lone));

        Controller.selectionSortBooking(bookings);

        assertEquals(List.of(anders, lone, peter), bookings);
    }

    @Test
    void selectionSortHandlesEmptyAndSingleLists() {
        ArrayList<ConferenceBooking> empty = new ArrayList<>();
        Controller.selectionSortBooking(empty);
        assertTrue(empty.isEmpty());

        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        ConferenceBooking only = Controller.createBooking(c, Controller.createParticipant("A", "1", "a"), false, null, null, null, START, END);
        ArrayList<ConferenceBooking> single = new ArrayList<>(List.of(only));
        Controller.selectionSortBooking(single);
        assertEquals(List.of(only), single);
    }

    @Test
    void storageReturnsCopies() {
        Controller.createParticipant("Finn", "1", "a");

        Controller.getParticipants().clear();

        assertEquals(1, Controller.getParticipants().size());
    }

    @Test
    void updatingExcursionAddressLeavesConferenceAlone() {
        Conference c = Controller.createConference("Conf", "Odense", 100, START, END);
        Excursion e = Controller.createExcursion("Museum", "Kolding", c, START, 200);
        assertEquals("Kolding", e.getAddress());

        Controller.updateExcursion(e, "Castle", "Egeskov", END, 75);

        assertEquals("Egeskov", e.getAddress());
        assertEquals("Odense", c.getAddress());
    }

    @Test
    void deletingParticipantRemovesThemFromHotelGuests() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Hotel h = Controller.createHotel(c, "Svanen", "Odense", "123", 500, 700);
        AddonPurchase wifi = Controller.createHotelAddon(h, "WiFi", 50);
        Participant p = Controller.createParticipant("Finn", "1", "a");
        ConferenceBooking b = Controller.createBooking(c, p, false, null, h, RoomType.SINGLE, START, END);
        Controller.addAddonToBooking(b, wifi);

        Controller.deleteParticipant(p);

        assertTrue(h.getBookings().isEmpty());
        assertTrue(h.getGuests().isEmpty());
        assertFalse(h.addonBooked(wifi));
    }

    @Test
    void excursionsWithoutCompanionAreIgnored() {
        Conference c = Controller.createConference("Conf", "x", 100, START, END);
        Excursion e = Controller.createExcursion("Museum", "Kolding", c, START, 200);
        ConferenceBooking b = Controller.createBooking(c, Controller.createParticipant("Finn", "1", "a"),
                false, null, null, null, START, END);

        Controller.setExcursionsOrdered(b, new ArrayList<>(List.of(e)));

        assertEquals(300, b.getTotalPrice());
        assertTrue(e.getParticipatingCompanions().isEmpty());
    }
}
