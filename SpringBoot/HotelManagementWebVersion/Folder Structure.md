pom.xml
src/main/java/
├── com/hotelmanagement/
│   ├── MainApplication.java
│   ├── config/
│   │   ├── AppConfig.java
│   │   └── DBConfig.java
│   ├── controllers/
│   │   ├── LoginController.java
│   │   ├── RoomController.java
│   │   └── BookingController.java
│   ├── models/
│   │   ├── Reservation.java
│   │   ├── Room.java
│   │   └── User.java
│   ├── services/
│   │   ├── ReservationService.java
│   │   ├── RoomService.java
│   │   └── UserService.java
│   ├── utils/
│   │   ├── PasswordUtil.java
│   │   └── ValidationUtil.java
│   └── ui/
│       └── RoomCardFactory.java
src/main/resources/
├── application.properties
├── static/
│   └── css/
│       └── application.css
├── templates/
│   ├── login.html
│   ├── rooms.html
│   └── booking.html

# Dev notes
- shit hit the fan in bookings view

## To-be improvements
- 

## Known issues (severity top to bottom)
- 

## Haven't checked
- any functionalities

### IN CASE OF EMERGENCY
`git reset --hard HEAD^`