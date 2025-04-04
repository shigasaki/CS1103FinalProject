import java.time.LocalDate;

class Billing {
    private int billingID;
    private int appointmentID;
    private double amount;
    private double tax;
    private double totalAmount;
    private LocalDate billingDate;
    private String paymentStatus;

    public Billing(int billingID, int appointmentID, double amount, double tax, 
                  double totalAmount, LocalDate billingDate, String paymentStatus) {
        this.billingID = billingID;
        this.appointmentID = appointmentID;
        this.amount = amount;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.billingDate = billingDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters and setters
    public int getBillingID() { return billingID; }
    public void setBillingID(int billingID) { this.billingID = billingID; }
    public int getAppointmentID() { return appointmentID; }
    public void setAppointmentID(int appointmentID) { this.appointmentID = appointmentID; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public LocalDate getBillingDate() { return billingDate; }
    public void setBillingDate(LocalDate billingDate) { this.billingDate = billingDate; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}