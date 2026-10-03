package application.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingPriceTest {
    private static final LocalDate ARRIVAL = LocalDate.of(2023, 12, 18);
    private static final LocalDate DEPARTURE = LocalDate.of(2023, 12, 20); // 2 nights, 3 conference days

    private Conference conference;
    private Hotel hotel;
    private Participant participant;

    @BeforeEach
    void setUp() {
        conference = new Conference("Hav og himmel", "Odense", 1500, ARRIVAL, DEPARTURE);
        hotel = new Hotel("Den Hvide Svane", "Odense", "123", 1050, 1250);
        conference.addHotelToConference(hotel);
        participant = new Participant("Finn", "1234", "Here");
    }

    private ConferenceBooking book(boolean lecturer, Companion companion, Hotel hotel, RoomType roomType) {
        return participant.createConfBook(conference, lecturer, companion, hotel, roomType, ARRIVAL, DEPARTURE);
    }

    @Test
    void conferenceFeeIsDayPriceTimesDaysIncludingDepartureDay() {
        ConferenceBooking booking = book(false, null, null, null);

        assertEquals(4500, booking.getConferencePrice());
        assertEquals(0, booking.getHotelPrice());
        assertEquals(0, booking.getExcursionPrice());
        assertEquals(4500, booking.getTotalPrice());
    }

    @Test
    void lecturersDoNotPayTheConferenceFee() {
        ConferenceBooking booking = book(true, null, null, null);

        assertEquals(0, booking.getTotalPrice());
    }

    @Test
    void singleRoomIsChargedPerNight() {
        ConferenceBooking booking = book(false, null, hotel, RoomType.SINGLE);

        assertEquals(2100, booking.getHotelPrice());
        assertEquals(4500 + 2100, booking.getTotalPrice());
    }

    @Test
    void doubleRoomIsChargedPerNight() {
        ConferenceBooking booking = book(false, null, hotel, RoomType.DOUBLE);

        assertEquals(2500, booking.getHotelPrice());
    }

    @Test
    void addonsAreChargedPerNight() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        AddonPurchase breakfast = hotel.createAddonPurchase("Breakfast", 100);
        ConferenceBooking booking = book(false, null, hotel, RoomType.SINGLE);

        booking.addAddon(wifi);
        booking.addAddon(breakfast);

        assertEquals((1050 + 50 + 100) * 2, booking.getHotelPrice());
    }

    @Test
    void companionForcesDoubleRoom() {
        Companion companion = participant.createCompanion("Mie", "22", "There");
        ConferenceBooking booking = book(false, companion, hotel, RoomType.SINGLE);

        assertEquals(2500, booking.getHotelPrice());
    }

    @Test
    void missingRoomTypeDefaultsToSingle() {
        ConferenceBooking booking = book(false, null, hotel, null);

        assertEquals(2100, booking.getHotelPrice());
    }

    @Test
    void hotelFromAnotherConferenceIsIgnored() {
        Hotel elsewhere = new Hotel("Elsewhere", "Aarhus", "1", 400, 600);
        ConferenceBooking booking = book(false, null, elsewhere, RoomType.SINGLE);

        assertEquals(0, booking.getHotelPrice());
        assertTrue(elsewhere.getBookings().isEmpty());
        assertEquals(4500, booking.getTotalPrice());
    }

    @Test
    void bookingRegistersWithItsHotel() {
        ConferenceBooking booking = book(false, null, hotel, RoomType.SINGLE);

        assertEquals(List.of(booking), hotel.getBookings());
        assertEquals(List.of(participant), hotel.getGuests());
        assertEquals(List.of("Finn"), hotel.getGuestBook());
    }

    @Test
    void companionExcursionsAreAddedToTheTotal() {
        Excursion museum = conference.createExcursion("Museum", "Kolding", ARRIVAL, 200);
        Excursion tour = conference.createExcursion("Tour", "Egeskov", ARRIVAL, 75);
        Companion companion = participant.createCompanion("Mie", "22", "There");
        ConferenceBooking booking = book(false, companion, null, null);

        booking.setExcursionsOrdered(new ArrayList<>(List.of(museum, tour)));

        assertEquals(275, booking.getExcursionPrice());
        assertEquals(4500 + 275, booking.getTotalPrice());
        assertEquals(List.of(museum, tour), companion.getExcursions());
        assertEquals(List.of(companion), museum.getParticipatingCompanions());
    }

    @Test
    void excursionsFromAnotherConferenceAreNotCharged() {
        Conference other = new Conference("Other", "Aarhus", 100, ARRIVAL, DEPARTURE);
        Excursion foreign = other.createExcursion("Foreign", "Aarhus", ARRIVAL, 999);
        Excursion local = conference.createExcursion("Local", "Odense", ARRIVAL, 125);
        Companion companion = participant.createCompanion("Mie", "22", "There");
        ConferenceBooking booking = book(false, companion, null, null);

        booking.setExcursionsOrdered(new ArrayList<>(List.of(foreign, local)));

        assertEquals(125, booking.getExcursionPrice());
    }

    @Test
    void lecturerWithCompanionHotelAddonAndExcursions() {
        // Same shape as cb4 in App.initStorage: lecturer, companion, hotel, WiFi, two excursions
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        Excursion e1 = conference.createExcursion("Odense Byrundtur", "Odense", ARRIVAL, 125);
        Excursion e2 = conference.createExcursion("Guidetur i Egeskov", "Egeskov", ARRIVAL, 75);
        Companion companion = participant.createCompanion("Jan", "k", "e");
        ConferenceBooking booking = book(true, companion, hotel, RoomType.SINGLE);
        booking.addAddon(wifi);
        booking.setExcursionsOrdered(new ArrayList<>(List.of(e1, e2)));

        assertEquals((1250 + 50) * 2 + 125 + 75, booking.getTotalPrice());
    }

    @Test
    void sameDayBookingPaysOneConferenceDayAndNoNights() {
        ConferenceBooking booking = participant.createConfBook(conference, false, null, hotel, RoomType.SINGLE, ARRIVAL, ARRIVAL);

        assertEquals(1500, booking.getConferencePrice());
        assertEquals(0, booking.getHotelPrice());
    }

    @Test
    void priceFollowsLaterPriceChanges() {
        AddonPurchase wifi = hotel.createAddonPurchase("WiFi", 50);
        ConferenceBooking booking = book(false, null, hotel, RoomType.SINGLE);
        booking.addAddon(wifi);

        conference.setDayPrice(1000);
        hotel.setSingleRoomPrice(500);
        wifi.setPrice(0);

        assertEquals(3000 + 1000, booking.getTotalPrice());
    }

    @Test
    void calculateAccommodationPriceHandlesNoAddons() {
        assertEquals(1050 * 3, hotel.calculateAccommodationPrice(RoomType.SINGLE, null, 3));
        assertEquals(0, hotel.calculateAccommodationPrice(RoomType.DOUBLE, new ArrayList<>(), 0));
    }

    @Test
    void bookingNumbersArePerConference() {
        Participant other = new Participant("Lone", "341", "k");
        ConferenceBooking first = book(false, null, null, null);
        ConferenceBooking second = other.createConfBook(conference, false, null, null, null, ARRIVAL, DEPARTURE);

        assertTrue(first.toString().contains("#1"));
        assertTrue(second.toString().contains("#2"));
        assertEquals(List.of(participant, other), conference.getParticipants());
    }

    @Test
    void detailsShowTheTotal() {
        ConferenceBooking booking = book(true, null, hotel, RoomType.DOUBLE);

        String details = booking.getDetails();

        assertTrue(details.contains("(Conference Lecturer)"));
        assertTrue(details.contains("Roomtype: Double (1250,-)"));
        assertTrue(details.contains("Number of days: 3"));
        assertTrue(details.contains("Total price: 2500,-"));
    }
}
