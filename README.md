# BookMyShow Movie Ticket Booking System — Low-Level Design (LLD)

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=apachemaven&logoColor=white)](pom.xml)
[![Tests](https://img.shields.io/badge/Tests-12%20Passed-success?style=for-the-badge&logo=junit5&logoColor=white)](src/test/java/com/bookmyshow)
[![Design Patterns](https://img.shields.io/badge/Design_Patterns-Strategy_%7C_Adapter_%7C_Observer_%7C_Facade-blueviolet?style=for-the-badge)](BookMyShowSchema.md)

A production-grade, object-oriented Low-Level Design (LLD) for **BookMyShow** in Java 21 LTS adhering to clean architecture, SOLID principles, and Gang of Four (GoF) design patterns.

---

## 1. System Requirements & Domain Feature Matrix

| # | Requirement | Implementation Details | Design Pattern / Component |
| :---: | :--- | :--- | :--- |
| **1** | **Multi-City & Multi-Theatre** | Geographic hierarchy: `City` $\to$ `List<Theatre>` $\to$ `List<Screen>`. | Domain Modeling |
| **2** | **Screen & Seat Categorization** | Screens contain multiple categorized seats (`PLATINUM`, `GOLD`, `SILVER`). | `Seat`, `SeatType` |
| **3** | **Show Scheduling & Features** | Shows mapped to Movies, Screens, timings, and supported features (`2D`, `3D`, `DOLBY_IMAX`, `DOLBY_VISION`, `4DX`, `DOLBY_ATMOS`). | `Show`, `Feature` validation |
| **4** | **Dynamic Day & Time Pricing** | Pricing calculated dynamically by seat type, weekend multiplier, prime-time multiplier, and format features. | `PricingStrategy`, `DynamicPricingStrategy` (Strategy Pattern) |
| **5** | **Multi-Seat Atomic Booking** | Concurrent seat selection with 10-minute TTL and transactional all-or-nothing rollback. | `SeatLockManager` (Thread-Safe Concurrency) |
| **6** | **Payment Gateway Integration** | Razorpay payment integration converting Rupees to Paise with order & signature verification. | `PaymentProcessor`, `RazorpayPaymentAdapter` (Adapter Pattern) |
| **7** | **Event-Driven Notifications** | Real-time event publisher broadcasting booking confirmations and cancellations. | `BookingEventPublisher`, `BookingEventListener` (Observer Pattern) |
| **8** | **Unified Entry Point** | Simplified platform facade orchestrating catalog search, booking, payment, and cancellation. | `BookMyShowPlatform` (Facade Pattern) |

---

## 2. Architecture Diagram

```mermaid
graph TD
    Client([User / Mobile App]) -->|Interacts with| BMS[BookMyShowPlatform Facade]
    
    subgraph Catalog & Geography
        BMS --> City[City]
        City --> Theatre[Theatre]
        Theatre --> Screen[Screen]
        Screen --> Seat[Seat]
        BMS --> Movie[Movie]
        Screen --> Show[Show]
        Show --> ShowSeat[ShowSeat Inventory]
    end
    
    subgraph Booking & Concurrency
        BMS --> BS[BookingService]
        BS --> SLM[SeatLockManager - Atomic 10m TTL]
        BS --> PS[PricingStrategy - Strategy Pattern]
        PS --> DPS[DynamicPricingStrategy]
        PS --> BSPS[BaseSeatPricingStrategy]
    end
    
    subgraph Payment Settlement
        BS --> PP[PaymentProcessor - Adapter Pattern]
        PP --> RPA[RazorpayPaymentAdapter]
        RPA --> ERC[ExternalRazorpayClient]
    end
    
    subgraph Observer Event Bus
        BS --> EP[BookingEventPublisher]
        EP --> NL[TicketNotificationListener - SMS / Email]
        EP --> AL[AnalyticsListener - Box Office Ledger]
    end
```

---

## 3. Test Verification
```bash
mvn clean test
```
- **12 unit & integration tests passing with 100% success**.
- High concurrency test verifying 20 threads competing for contested seats with zero double-booking.
