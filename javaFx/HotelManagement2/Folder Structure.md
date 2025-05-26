pom.xml
src/module-info.java
src/config.properties
src/application/
├── Main.java
├── Config.java
├── DBConnection.java
├── application.css
├── controllers/
│   ├── MainController.java
│   ├── RoomController.java
│   └── BookingController.java
├── views/
│   ├── login.fxml
│   ├── rooms.fxml
│   ├── booking.fxml
├── models/
│   ├── Reservation.java
│   ├── Room.java
│   └── User.java
│
├── utils/
│   └── PasswordUtil.java
├── ui/
│   └── RoomCardFactory.java

# Dev notes:
26/05/2025

(probably never touching this again)

## To-be improvements
- filtering reservations should be filtered by unpaid in default
- move statusLabel for successful room adding next to fields

## Known issues (severity top to bottom)
- floating point numbers with partial payments
- in RoomController reserved by column isn't showing the username (admin panel) 
- filtering reservations by name should have been by user's name
- scrolling rooms doesn't account for screen width (user view)

## Haven't checked
- Error handling (probably garbage)
- App will probably crash if theres nothing in the database

### IN CASE OF EMERGENCY
`git reset --hard HEAD^`