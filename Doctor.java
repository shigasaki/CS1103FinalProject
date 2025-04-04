class Doctor {
    private int doctorID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String specialty;
    private double salary;

    public Doctor(int doctorID, String firstName, String lastName, String email, 
                 String phoneNumber, String specialty, double salary) {
        this.doctorID = doctorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.specialty = specialty;
        this.salary = salary;
    }

    // Getters and setters
    public int getDoctorID() { return doctorID; }
    public void setDoctorID(int doctorID) { this.doctorID = doctorID; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}