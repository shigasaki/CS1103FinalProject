import java.time.LocalDate;

class Patient {
    private int patientID;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phoneNumber;
    private String address;

    public Patient(int patientID, String firstName, String lastName, LocalDate dob, 
                  String gender, String email, String phoneNumber, String address) {
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters and setters
    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}