# KAS — Conference Administration System

A desktop application for managing conference bookings, built in Java with a JavaFX graphical user interface.

## Overview

KAS allows administrators to manage all aspects of a conference: participants, hotels, excursions, and bookings — including pricing, companions, hotel add-ons, and lecturer status.

## Features

- **Conference management** — Create, update, and delete conferences with date ranges and daily pricing
- **Participant management** — Register participants with optional company affiliation; supports lecturer status (lecturers are exempt from the conference fee)
- **Hotel management** — Associate hotels with conferences, set single/double room prices, and manage optional add-on services (e.g. WiFi, breakfast)
- **Excursion management** — Define optional excursions per conference with individual pricing; booked through a participant's companion
- **Booking system** — Full conference bookings linking participant, hotel, room type, companion, excursions, and add-ons, with automatic total price calculation
- **Companion registration** — Each participant can have a companion who can be assigned to excursions
- **Search** — Search across bookings
- **Sorting** — Bookings sorted alphabetically by participant name (selection sort)
- **CRUD via GUI** — All entities support create, update, and delete through modal dialog windows with input validation

## Architecture

The project follows a layered **MVC (Model-View-Controller)** architecture:

```
src/
├── application/
│   ├── model/          # Domain model (Conference, Hotel, Participant, etc.)
│   └── controller/     # Business logic layer (Controller.java)
├── gui/                # JavaFX views and windows
└── storage/            # Static in-memory storage (Storage.java)
```

### Domain Model

| Class | Description |
|---|---|
| `Conference` | A conference with hotels, excursions, bookings, and daily price |
| `ConferenceBooking` | A booking tying a participant to a conference, with hotel, companion, and excursions |
| `Hotel` | Accommodation associated with a conference, with room pricing and add-ons |
| `Participant` | A person attending a conference, optionally affiliated with a company |
| `Companion` | A companion of a participant who can be booked onto excursions |
| `Excursion` | An optional trip linked to a specific conference |
| `AddonPurchase` | An optional hotel service (e.g. WiFi, massage) |
| `RoomType` | Enum: `SINGLE` or `DOUBLE` |

### Pricing Logic

The total booking price is calculated as:

```
Total = Conference price (days × daily rate, waived for lecturers)
      + Hotel price (room type × nights + add-ons)
      + Excursion price (sum of booked excursions via companion)
```

## Tech Stack

- **Java** — Core application language
- **JavaFX** — Desktop GUI framework (scenes, stages, layouts, controls)
- **IntelliJ IDEA** — IDE (`.iml` project file)
- **Java Time API** (`java.time.LocalDate`) — Date handling

## Getting Started

### Prerequisites

- Java 11 or later
- JavaFX SDK (if not bundled with your JDK)
- IntelliJ IDEA (recommended)

### Running the Application

1. Open the project in IntelliJ IDEA
2. Configure the JavaFX SDK in project settings if needed
3. Run `src/gui/App.java` — this initialises sample data and launches the GUI

> **Note:** The application uses in-memory storage only. All data is reset on each run. Sample data (participants, conferences, hotels, bookings) is pre-loaded via `App.initStorage()`.

## Project Structure

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
