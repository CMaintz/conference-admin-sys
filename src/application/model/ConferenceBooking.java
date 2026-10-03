package application.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class ConferenceBooking {
    private final ArrayList<AddonPurchase> addonsOrdered = new ArrayList<>();
    private final ArrayList<Excursion> excursionsOrdered = new ArrayList<>();
    private final Conference conference;
    private final LocalDate arrivalDate;
    private final LocalDate departureDate;
    private final int numberOfDays;
    // TODO lav til en metode, ikke som attribut. Brug metode i constructor. Tjek attributter stemmer overens
    // TODO med klassediagram, og ret klassediagram ligeledes. Få fikset companion og excursion halløj, lav
    // TODO præbetingelser for delete metoder osv.
    private final boolean participantIsLecturer;
    private Participant participant;
    private int conferencePrice;
    private int hotelPrice;
    private int excursionsPrice;
    private int totalPrice;
    private RoomType roomType;
    private Hotel hotel;
    private Companion companion;
    private int bookingNumber;

    public ConferenceBooking(Conference conference, Participant participant, Boolean isLecturer, Companion companion, Hotel hotel, RoomType roomType, LocalDate arrivalDate, LocalDate departureDate) {
        this.participant = participant;
        this.conference = conference;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.participantIsLecturer = isLecturer;
        if (companion != null) {
            this.companion = companion;
        }
        if (hotel != null) {
            setHotel(hotel, roomType);
        }
        this.numberOfDays = (int) (departureDate.toEpochDay() - arrivalDate.toEpochDay());

    }

    //TODO split conferenceBooking op i forskellige klasser; en hotelBooking klasse,
    // conferenceBooking, udflugtBooking, som holdes styr på i en centralBooking klasse
//    public ArrayList<Excursion> getExcursionsOrdered() {
//        return new ArrayList<>(excursionsOrdered);
//    }

//    public ArrayList<Excursion> getExcursionsOrderedProper() {
//        ArrayList<Excursion> excursionsOrder = new ArrayList<>();
//        if (companion != null) {
//            if (companion.getExcursions().size() > 0 && conference.getExcursions().size() > 0) {
//                ArrayList<Excursion> companionExcursions = new ArrayList<>(companion.getExcursions());
//                ArrayList<Excursion> conferenceExcursions = new ArrayList<>(conference.getExcursions());
//
//                for (Excursion excursion : companionExcursions) {
//                    if (conferenceExcursions.contains(excursion)) {
//                        excursionsOrder.add(excursion);
//                    }
//                }
//            }
//        }
//        return excursionsOrder;
//    }



    public void setExcursionsOrdered(ArrayList<Excursion> excursionsOrdered) {
        if (companion == null) {
            return;
        }
        this.excursionsOrdered.addAll(excursionsOrdered);
        if (this.excursionsOrdered.size() > 0) {
            for (Excursion excursion : excursionsOrdered) {
                this.companion.addExcursion(excursion);
            }
        }
    }

    public int getConferencePrice() {
        int price = 0;
        price = conference.getDayPrice() * (numberOfDays + 1);

        conferencePrice = price;
        return conferencePrice;
    }

    public int getHotelPrice() {
        int price = 0;
        if (hotel != null) {
            price += hotel.calculateAccommodationPrice(roomType, addonsOrdered, numberOfDays);
        }
        hotelPrice = price;
        return hotelPrice;
    }

    public String getExcursionDeets() {
        StringBuilder sb = new StringBuilder();
        for (Excursion exc : excursionsOrdered) {
            sb.append("\n" + exc.toString());
        }
        return sb.toString();
    }

//    public int getExcursionPrice() {
//        int excursionTotal = 0;
//        if (excursionsOrdered.size() > 0) {
//            for (Excursion excursion : excursionsOrdered) {
//                excursionTotal += excursion.getPrice();
//            }
//            this.excursionsPrice = excursionTotal;
//        }
//        return excursionsPrice;
//    }

    //TODO place this inside companion;
    public int getExcursionPrice() {
        int excursionTotal = 0;
        if (companion != null) {
            if (companion.getExcursions().size() > 0 && conference.getExcursions().size() > 0) {
                for (Excursion excursion : companion.getExcursions()) {
                    if (conference.getExcursions().contains(excursion)) {
                        excursionTotal += excursion.getPrice();
                    }
                }
            }
        }
        this.excursionsPrice = excursionTotal;
        return excursionsPrice;
    }

    public int getTotalPrice() {
        int price = 0;
        if (hotel != null) {
            price += getHotelPrice();
        }
        if (this.companion != null && excursionsOrdered.size() > 0) {
            price += getExcursionPrice();
        }
        if (!participantIsLecturer) {
            price += getConferencePrice();
        }
        totalPrice = price;
        return price;
    }

    public Participant getParticipant() {
        return participant;
    }

//    public void setParticipant(Participant participant) {
//        if (this.participant != participant) {
//            this.participant = participant;
//            if (participant != null) {
//                participant.addBooking(this);
//            }
//        }
//    }

    public Conference getConference() {
        return conference;
    }

    public ArrayList<AddonPurchase> getAddonsOrdered() {
        return new ArrayList<>(addonsOrdered);
    }

    public void setAddonsOrdered(ArrayList<AddonPurchase> addonsOrdered) {
        if (addonsOrdered != null) {
            this.addonsOrdered.clear();
            this.addonsOrdered.addAll(addonsOrdered);
        }
    }

    public void addAddon(AddonPurchase addon) {
        if (hotel != null && !addonsOrdered.contains(addon) && hotel.getAddonsOnOffer().contains(addon)) {
            addonsOrdered.add(addon);
        }
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel, RoomType roomType) {
        if (hotel != null && conference.getHotels().contains(hotel)) {
            this.hotel = hotel;
            setRoomType(roomType);
            hotel.addBooking(this);
        }
    }

    public void setRoomType(RoomType roomType) {
        if (this.companion != null && roomType != RoomType.DOUBLE) {
            this.roomType = RoomType.DOUBLE;
        } else if (roomType != RoomType.SINGLE && roomType != RoomType.DOUBLE) {
            this.roomType = RoomType.SINGLE;
        } else {
            this.roomType = roomType;
        }
    }

//    public Companion getCompanion() {
//        return companion;
//    }

//    public void setCompanion(Companion companion) {
//        this.companion = companion;
//    }

//    public int getBookingNumber() {
//        return bookingNumber;
//    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    @Override
    public String toString() {
        return participant.getName() + " (booking #" + bookingNumber + ")\n";
    }

    public String getDetails() {
        String info = "";
        info += ("Booking #") + (bookingNumber) + ("\n");
        info += ("Name: ") + (participant.getName()) + ("\n");
        if (participantIsLecturer) {
            info += ("(Conference Lecturer)\n");
        }
        if (participant.getCompany().length() > 0) {
            info += ("Company: ") + (participant.getCompany()) + ("\n");
        }
        if (companion != null) {
            info += ("Companion: ") + (companion.getName()) + ("\n");
            if (excursionsOrdered.size() > 0) {
                info += ("Companion Excursion(s): ") + (getExcursionDeets()) + ("\n");
            }
        }
        if (hotel != null) {
            info += ("Hotel: ") + (hotel.getName()) + ("\n");
            if (roomType == RoomType.DOUBLE) {
                info += ("Roomtype: Double (" + hotel.getDoubleRoomPrice() + ",-)\n");
            } else if (roomType == RoomType.SINGLE) {
                info += ("Roomtype: Single (" + hotel.getSingleRoomPrice() + ",-)\n");
            }
        }
        info += ("Arrival: ") + (arrivalDate) + ("\n");
        info += ("Departure: ") + (departureDate) + ("\n");
        info += ("Number of days: ") + (numberOfDays + 1) + ("\n");
        info += ("Total price: ") + (getTotalPrice()) + (",-\n");
        return info;
    }

    public String getDetailsNoHotel() {
        String info = "";
        info += ("Booking #") + (bookingNumber) + ("\n");
        info += ("Name: ") + (participant.getName()) + ("\n");
        if (participant.getCompany().length() > 0) {
            info += ("Company: ") + (participant.getCompany()) + ("\n");
        }
        if (companion != null) {
            info += ("Companion: ") + (companion.getName()) + ("\n");
            if (excursionsOrdered.size() > 0) {
                info += ("Companion Excursion(s): ") + (getExcursionDeets()) + ("\n");
            }
        }
        info += ("Arrival: ") + (arrivalDate) + ("\n");
        info += ("Departure: ") + (departureDate) + ("\n");
        info += ("Number of nights: ") + (numberOfDays) + ("\n");
        info += ("Hotel booking price: ") + (getHotelPrice()) + ("\n");
        return info;
    }

    public String getSearchDetails() {
        String info = "";
        info += ("Conference: ") + (conference.getName()) + ("\n");
        info += ("Booking #") + (bookingNumber) + ("\n");
        info += ("Name: ") + (participant.getName()) + ("\n");
        if (participant.getCompany().length() > 0) {
            info += ("Company: ") + (participant.getCompany()) + ("\n");
        }
        if (companion != null) {
            info += ("Companion: ") + (companion.getName()) + ("\n");
            if (excursionsOrdered.size() > 0) {
                info += ("Companion Excursion(s): ") + (getExcursionDeets()) + ("\n");
            }
        }
        if (hotel != null) {
            info += ("Hotel: ") + (hotel.getName()) + ("\n");
            if (roomType == RoomType.DOUBLE) {
                info += ("Roomtype: Double (" + hotel.getDoubleRoomPrice() + ",-)\n");
            } else if (roomType == RoomType.SINGLE) {
                info += ("Roomtype: Single (" + hotel.getSingleRoomPrice() + ",-)\n");
            }
        }
        info += ("Arrival: ") + (arrivalDate) + ("\n");
        info += ("Departure: ") + (departureDate) + ("\n");
        info += ("Number of days: ") + (numberOfDays + 1) + ("\n");
        info += ("Total price: ") + (getTotalPrice()) + (",-\n");
        return info;
    }
}
