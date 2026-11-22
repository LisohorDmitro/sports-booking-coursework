package ua.edu.nupp.kpp.client;

public class Booking {
    private Long id;
    private long userId;
    private long timeSlotId;
    private BookingState state;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndex;

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(long timeSlotId) { this.timeSlotId = timeSlotId; }
    public BookingState getState() { return state; }
    public void setState(BookingState state) { this.state = state; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getIntegralIndex() { return integralIndex; }
    public void setIntegralIndex(double integralIndex) { this.integralIndex = integralIndex; }
}
