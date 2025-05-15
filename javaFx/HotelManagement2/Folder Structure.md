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
│   ├── Booking.java
│   └── Room.java

Dev notes:
#save zip 5 ... 25/04/2025 10:19 
- in admin panel the reservations are not loaded initially but are added to the table itself 
- when searching the editable combo box after selecting a user before, there is a conversion error between the User obj and a string


IDEAS: 
- Add price and max people for rooms (admin)
- Cancel reservation
