class Stop {
    private String name;
    private String departureTime;
    
    public Stop(String name, String departureTime) {
        this.name = name;
        this.departureTime = departureTime;
    }
    
    public String getName() { 
        return name; 
    }
    public String getDepartureTime() {
        return departureTime;
    }
    
    @Override
    public String toString() {
        return name + "(" + departureTime + ")";
    }
}
