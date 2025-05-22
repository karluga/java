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
│   └── booking.fxml
├── models/
│   ├── Reservation.java
│   └── Room.java

Dev notes:
25/04/2025
- in admin panel the reservations are not loaded initially but are added to the table itself 
- when searching the editable combo box after selecting a user before, there is a conversion error between the User obj and a string


HAVENT YET CHECKED: 
- Cancel reservation

WORKED ON LAST:
should add last insert id to RoomService.reserveRoom()

IN CASE OF EMERGENCY
`git reset --hard HEAD^`