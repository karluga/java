package application.models;

public class Booking {
    private int roomId;
    private String customerName;
    private String startDate;
    private String endDate;
    private int numberOfPeople;

    public Booking(int roomId, String customerName, String startDate, String endDate, int numberOfPeople) {
        this.roomId = roomId;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfPeople = numberOfPeople;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "roomId=" + roomId +
                ", customerName='" + customerName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                '}';
    }
}