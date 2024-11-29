// OLD
public class Notification {
    public void sendEmai1(String message) {
        System.out.println(" Email:" + message);
    }
    public void sendSMS(String message) {
        System.out.println("SMS :" + message);
    }
}

// NEW
public class Email {
    public void send(String message) {
        System.out.println("Email:" + message);
    }
}
public class SMS {
    public void send(String message) {
        System.out.println("SMS :" + message);
    }
}
