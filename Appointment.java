import java.time.LocalDate;
import java.time.LocalTime;

class Appointment {
    private int appointmentID;
    private int patientID;
    private int doctorID;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String notes;

    public Appointment(int appointmentID, int patientID, int doctorID, LocalDate appointmentDate, 
                      LocalTime appointmentTime, String status, String notes) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    // Getters and setters
    public int getAppointmentID() { return appointmentID; }
    public void setAppointmentID(int appointmentID) { this.appointmentID = appointmentID; }
    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }
    public int getDoctorID() { return doctorID; }
    public void setDoctorID(int doctorID) { this.doctorID = doctorID; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}