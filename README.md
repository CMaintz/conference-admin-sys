# KAS: Conference Administration System

A JavaFX desktop app for managing conference bookings. I built it as an exam project in Java, and it was graded 10 (the Danish scale tops out at 12).

## What it does

KAS is for the person running a conference: participants, hotels, excursions and bookings, including pricing, companions, hotel add-ons and lecturer status.

- Create, edit and delete conferences, each with a date range and a daily price.
- Register participants, optionally with a company. A participant can be booked as a lecturer, which waives the conference fee.
- Attach hotels to a conference with single and double room prices, plus optional add-ons like WiFi or breakfast.
- Set up excursions per conference with their own prices. Excursions are booked for the participant's companion.
- Make a booking that ties together participant, hotel, room type, companion, excursions and add-ons, and get the total price calculated.
- Search for a participant by name and see their bookings.
- Bookings are listed alphabetically by participant name, sorted with a hand-written selection sort.
- Data is entered and edited through modal dialogs with input validation.

## Architecture

It's a layered design: GUI on top, a controller layer in the middle, and the domain model with in-memory storage underneath.

```
src/
├── application/
│   ├── model/          # Domain model (Conference, Hotel, Participant, etc.)
│   └── controller/     # Business logic (Controller.java)
├── gui/                # JavaFX panes and windows
└── storage/            # Static in-memory storage (Storage.java)
```

### Domain model

| Class | Description |
|---|---|
| `Conference` | A conference with hotels, excursions, bookings and a daily price |
| `ConferenceBooking` | Ties a participant to a conference, with hotel, companion and excursions |
| `Hotel` | Accommodation for a conference, with room prices and add-ons |
| `Participant` | A person attending, optionally with a company |
| `Companion` | A participant's companion, who can be booked onto excursions |
| `Excursion` | An optional trip belonging to one conference |
| `AddonPurchase` | An optional hotel service (e.g. WiFi, massage) |
| `RoomType` | Enum: `SINGLE` or `DOUBLE` |

### Pricing

A booking's total is:

```
Total = conference fee  (daily rate × days, both arrival and departure day count; waived for lecturers)
      + hotel           ((room rate + add-ons) × nights)
      + excursions      (sum of the companion's excursions)
```

## Tech stack

- Java with JavaFX for the GUI
- `java.time.LocalDate` for dates
- An IntelliJ IDEA project (`KAS.iml`)

## Running it

You'll need Java 17 or later, the JavaFX SDK (unless your JDK bundles it) and ideally IntelliJ IDEA.

1. Open the project in IntelliJ.
2. Set up the JavaFX SDK in the project settings if needed.
3. Run `src/gui/App.java`. It loads sample data and opens the GUI.

Storage is in-memory only, so everything resets each run. The sample participants, conferences, hotels and bookings come from `App.initStorage()`.

## Project structure

```
KAS/
├── src/
│   ├── application/
│   │   ├── controller/
│   │   │   ├── BookingController.java
│   │   │   ├── CompanionController.java
│   │   │   ├── ConferenceController.java
│   │   │   ├── Controller.java
│   │   │   ├── HotelController.java
│   │   │   └── ParticipantController.java
│   │   └── model/
│   │       ├── AddonPurchase.java
│   │       ├── Companion.java
│   │       ├── Conference.java
│   │       ├── ConferenceBooking.java
│   │       ├── Excursion.java
│   │       ├── ExcursionBooking.java
│   │       ├── Hotel.java
│   │       ├── HotelBooking.java
│   │       ├── Participant.java
│   │       └── RoomType.java
│   ├── gui/
│   │   ├── App.java
│   │   ├── StartWindow.java
│   │   ├── AdministrationPane.java
│   │   ├── ConferencePane.java
│   │   ├── ExcursionPane.java
│   │   ├── HotelPane.java
│   │   ├── BookingWindow.java
│   │   ├── CompanionWindow.java
│   │   ├── ConferenceWindow.java
│   │   ├── ExcursionWindow.java
│   │   ├── HotelWindow.java
│   │   ├── ParticipantWindow.java
│   │   ├── AddonWindow.java
│   │   └── SearchWindow.java
│   └── storage/
│       └── Storage.java
└── KAS.iml
```

The controller logic all lives in `Controller.java`; the other controller classes are empty placeholders.
