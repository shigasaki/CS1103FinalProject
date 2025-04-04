import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class HospitalManagementCLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean running = true;

    public static void main(String[] args) {
        DatabaseManager.connect();

        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> patientManagement();
                case 2 -> doctorManagement();
                case 3 -> appointmentManagement();
                case 4 -> billingManagement();
                case 5 -> {
                    System.out.println("Exiting the system...");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        DatabaseManager.disconnect();
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n=== HOSPITAL MANAGEMENT SYSTEM ===");
        System.out.println("1. Patient Management");
        System.out.println("2. Doctor Management");
        System.out.println("3. Appointment Management");
        System.out.println("4. Billing Management");
        System.out.println("5. Exit");
    }

    // Patient Management
    private static void patientManagement() {
        while (true) {
            System.out.println("\n=== PATIENT MANAGEMENT ===");
            System.out.println("1. View All Patients");
            System.out.println("2. View Patient Details");
            System.out.println("3. Add New Patient");
            System.out.println("4. Update Patient Information");
            System.out.println("5. Delete Patient");
            System.out.println("6. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> displayAllPatients();
                case 2 -> viewPatientDetails();
                case 3 -> addNewPatient();
                case 4 -> updatePatient();
                case 5 -> deletePatient();
                case 6 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayAllPatients() {
        List<Patient> patients = DatabaseManager.getAllPatients();
        System.out.println("\n=== ALL PATIENTS ===");
        System.out.printf("%-10s %-15s %-15s %-12s %-10s %-25s %-15s %-30s%n",
                "ID", "First Name", "Last Name", "DOB", "Gender", "Email", "Phone", "Address");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (Patient patient : patients) {
            System.out.printf("%-10d %-15s %-15s %-12s %-10s %-25s %-15s %-30s%n",
                    patient.getPatientID(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    patient.getDob(),
                    patient.getGender(),
                    patient.getEmail() != null ? patient.getEmail() : "N/A",
                    patient.getPhoneNumber(),
                    patient.getAddress() != null ? patient.getAddress() : "N/A");
        }
    }

    private static void viewPatientDetails() {
        int patientId = getIntInput("Enter Patient ID: ");
        Patient patient = DatabaseManager.getPatientById(patientId);
        
        if (patient != null) {
            System.out.println("\n=== PATIENT DETAILS ===");
            System.out.println("ID: " + patient.getPatientID());
            System.out.println("Name: " + patient.getFirstName() + " " + patient.getLastName());
            System.out.println("Date of Birth: " + patient.getDob());
            System.out.println("Gender: " + patient.getGender());
            System.out.println("Email: " + (patient.getEmail() != null ? patient.getEmail() : "N/A"));
            System.out.println("Phone: " + patient.getPhoneNumber());
            System.out.println("Address: " + (patient.getAddress() != null ? patient.getAddress() : "N/A"));
            
            // Show patient's appointments
            List<Appointment> appointments = DatabaseManager.getAppointmentsByPatientId(patientId);
            if (!appointments.isEmpty()) {
                System.out.println("\n=== APPOINTMENTS ===");
                System.out.printf("%-12s %-20s %-15s %-10s %-30s%n",
                        "Appt ID", "Date", "Time", "Doctor", "Status");
                System.out.println("------------------------------------------------------------------");
                for (Appointment appt : appointments) {
                    Doctor doctor = DatabaseManager.getDoctorById(appt.getDoctorID());
                    System.out.printf("%-12d %-20s %-15s %-10s %-30s%n",
                            appt.getAppointmentID(),
                            appt.getAppointmentDate(),
                            appt.getAppointmentTime(),
                            doctor != null ? doctor.getLastName() : "Unknown",
                            appt.getStatus());
                }
            }
        } else {
            System.out.println("Patient not found with ID: " + patientId);
        }
    }

    private static void addNewPatient() {
        System.out.println("\n=== ADD NEW PATIENT ===");
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        LocalDate dob = getDateInput("Date of Birth (YYYY-MM-DD): ");
        String gender = getGenderInput();
        String email = getOptionalStringInput("Email (press Enter to skip): ");
        String phone = getStringInput("Phone Number: ");
        String address = getOptionalStringInput("Address (press Enter to skip): ");

        Patient newPatient = new Patient(0, firstName, lastName, dob, gender, email, phone, address);
        boolean success = DatabaseManager.addPatient(newPatient);
        
        if (success) {
            System.out.println("Patient added successfully!");
        } else {
            System.out.println("Failed to add patient.");
        }
    }

    private static void updatePatient() {
        int patientId = getIntInput("Enter Patient ID to update: ");
        Patient patient = DatabaseManager.getPatientById(patientId);
        
        if (patient == null) {
            System.out.println("Patient not found with ID: " + patientId);
            return;
        }

        System.out.println("\nCurrent Patient Information:");
        System.out.println("1. First Name: " + patient.getFirstName());
        System.out.println("2. Last Name: " + patient.getLastName());
        System.out.println("3. Date of Birth: " + patient.getDob());
        System.out.println("4. Gender: " + patient.getGender());
        System.out.println("5. Email: " + (patient.getEmail() != null ? patient.getEmail() : "N/A"));
        System.out.println("6. Phone Number: " + patient.getPhoneNumber());
        System.out.println("7. Address: " + (patient.getAddress() != null ? patient.getAddress() : "N/A"));
        System.out.println("8. Cancel Update");

        int fieldToUpdate = getIntInput("Enter the number of the field to update (1-7) or 8 to cancel: ");
        
        if (fieldToUpdate == 8) {
            System.out.println("Update cancelled.");
            return;
        }

        switch (fieldToUpdate) {
            case 1 -> patient.setFirstName(getStringInput("Enter new First Name: "));
            case 2 -> patient.setLastName(getStringInput("Enter new Last Name: "));
            case 3 -> patient.setDob(getDateInput("Enter new Date of Birth (YYYY-MM-DD): "));
            case 4 -> patient.setGender(getGenderInput());
            case 5 -> patient.setEmail(getOptionalStringInput("Enter new Email (press Enter to clear): "));
            case 6 -> patient.setPhoneNumber(getStringInput("Enter new Phone Number: "));
            case 7 -> patient.setAddress(getOptionalStringInput("Enter new Address (press Enter to clear): "));
            default -> {
                System.out.println("Invalid field number.");
                return;
            }
        }

        boolean success = DatabaseManager.updatePatient(patient);
        if (success) {
            System.out.println("Patient information updated successfully!");
        } else {
            System.out.println("Failed to update patient information.");
        }
    }

    private static void deletePatient() {
        int patientId = getIntInput("Enter Patient ID to delete: ");
        System.out.println("WARNING: This will also delete all appointments and billing records for this patient.");
        String confirm = getStringInput("Are you sure you want to delete this patient? (yes/no): ");
        
        if (confirm.equalsIgnoreCase("yes")) {
            boolean success = DatabaseManager.deletePatient(patientId);
            if (success) {
                System.out.println("Patient deleted successfully.");
            } else {
                System.out.println("Failed to delete patient or patient not found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // Doctor Management
    private static void doctorManagement() {
        while (true) {
            System.out.println("\n=== DOCTOR MANAGEMENT ===");
            System.out.println("1. View All Doctors");
            System.out.println("2. View Doctor Details");
            System.out.println("3. Add New Doctor");
            System.out.println("4. Update Doctor Information");
            System.out.println("5. Delete Doctor");
            System.out.println("6. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> displayAllDoctors();
                case 2 -> viewDoctorDetails();
                case 3 -> addNewDoctor();
                case 4 -> updateDoctor();
                case 5 -> deleteDoctor();
                case 6 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayAllDoctors() {
        List<Doctor> doctors = DatabaseManager.getAllDoctors();
        System.out.println("\n=== ALL DOCTORS ===");
        System.out.printf("%-10s %-15s %-15s %-25s %-15s %-20s %-10s%n",
                "ID", "First Name", "Last Name", "Email", "Phone", "Specialty", "Salary");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Doctor doctor : doctors) {
            System.out.printf("%-10d %-15s %-15s %-25s %-15s %-20s $%-9.2f%n",
                    doctor.getDoctorID(),
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctor.getEmail() != null ? doctor.getEmail() : "N/A",
                    doctor.getPhoneNumber(),
                    doctor.getSpecialty(),
                    doctor.getSalary());
        }
    }

    private static void viewDoctorDetails() {
        int doctorId = getIntInput("Enter Doctor ID: ");
        Doctor doctor = DatabaseManager.getDoctorById(doctorId);
        
        if (doctor != null) {
            System.out.println("\n=== DOCTOR DETAILS ===");
            System.out.println("ID: " + doctor.getDoctorID());
            System.out.println("Name: " + doctor.getFirstName() + " " + doctor.getLastName());
            System.out.println("Email: " + (doctor.getEmail() != null ? doctor.getEmail() : "N/A"));
            System.out.println("Phone: " + doctor.getPhoneNumber());
            System.out.println("Specialty: " + doctor.getSpecialty());
            System.out.println("Salary: $" + doctor.getSalary());
            
            // Show doctor's appointments
            List<Appointment> appointments = DatabaseManager.getAppointmentsByDoctorId(doctorId);
            if (!appointments.isEmpty()) {
                System.out.println("\n=== UPCOMING APPOINTMENTS ===");
                System.out.printf("%-12s %-20s %-15s %-20s %-10s%n",
                        "Appt ID", "Date", "Time", "Patient", "Status");
                System.out.println("------------------------------------------------------------------");
                for (Appointment appt : appointments) {
                    Patient patient = DatabaseManager.getPatientById(appt.getPatientID());
                    System.out.printf("%-12d %-20s %-15s %-20s %-10s%n",
                            appt.getAppointmentID(),
                            appt.getAppointmentDate(),
                            appt.getAppointmentTime(),
                            patient != null ? patient.getLastName() : "Unknown",
                            appt.getStatus());
                }
            }
        } else {
            System.out.println("Doctor not found with ID: " + doctorId);
        }
    }

    private static void addNewDoctor() {
        System.out.println("\n=== ADD NEW DOCTOR ===");
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        String email = getOptionalStringInput("Email (press Enter to skip): ");
        String phone = getStringInput("Phone Number: ");
        String specialty = getStringInput("Specialty: ");
        double salary = getDoubleInput("Salary: $");

        Doctor newDoctor = new Doctor(0, firstName, lastName, email, phone, specialty, salary);
        boolean success = DatabaseManager.addDoctor(newDoctor);
        
        if (success) {
            System.out.println("Doctor added successfully!");
        } else {
            System.out.println("Failed to add doctor.");
        }
    }

    private static void updateDoctor() {
        int doctorId = getIntInput("Enter Doctor ID to update: ");
        Doctor doctor = DatabaseManager.getDoctorById(doctorId);
        
        if (doctor == null) {
            System.out.println("Doctor not found with ID: " + doctorId);
            return;
        }

        System.out.println("\nCurrent Doctor Information:");
        System.out.println("1. First Name: " + doctor.getFirstName());
        System.out.println("2. Last Name: " + doctor.getLastName());
        System.out.println("3. Email: " + (doctor.getEmail() != null ? doctor.getEmail() : "N/A"));
        System.out.println("4. Phone Number: " + doctor.getPhoneNumber());
        System.out.println("5. Specialty: " + doctor.getSpecialty());
        System.out.println("6. Salary: $" + doctor.getSalary());
        System.out.println("7. Cancel Update");

        int fieldToUpdate = getIntInput("Enter the number of the field to update (1-6) or 7 to cancel: ");
        
        if (fieldToUpdate == 7) {
            System.out.println("Update cancelled.");
            return;
        }

        switch (fieldToUpdate) {
            case 1 -> doctor.setFirstName(getStringInput("Enter new First Name: "));
            case 2 -> doctor.setLastName(getStringInput("Enter new Last Name: "));
            case 3 -> doctor.setEmail(getOptionalStringInput("Enter new Email (press Enter to clear): "));
            case 4 -> doctor.setPhoneNumber(getStringInput("Enter new Phone Number: "));
            case 5 -> doctor.setSpecialty(getStringInput("Enter new Specialty: "));
            case 6 -> doctor.setSalary(getDoubleInput("Enter new Salary: $"));
            default -> {
                System.out.println("Invalid field number.");
                return;
            }
        }

        boolean success = DatabaseManager.updateDoctor(doctor);
        if (success) {
            System.out.println("Doctor information updated successfully!");
        } else {
            System.out.println("Failed to update doctor information.");
        }
    }

    private static void deleteDoctor() {
        int doctorId = getIntInput("Enter Doctor ID to delete: ");
        System.out.println("WARNING: This will also delete all appointments and billing records for this doctor.");
        String confirm = getStringInput("Are you sure you want to delete this doctor? (yes/no): ");
        
        if (confirm.equalsIgnoreCase("yes")) {
            boolean success = DatabaseManager.deleteDoctor(doctorId);
            if (success) {
                System.out.println("Doctor deleted successfully.");
            } else {
                System.out.println("Failed to delete doctor or doctor not found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void scheduleAppointment() {
        System.out.println("\n=== SCHEDULE NEW APPOINTMENT ===");
        
        // Display patients for reference
        displayAllPatients();
        int patientId = getIntInput("Enter Patient ID: ");
        
        // Display doctors for reference
        displayAllDoctors();
        int doctorId = getIntInput("Enter Doctor ID: ");
        
        LocalDate date = getDateInput("Enter Appointment Date (YYYY-MM-DD): ");
        
        // Show available time slots for the doctor on this date
        List<LocalTime> availableSlots = DatabaseManager.getAvailableTimeSlots(doctorId, date);
        if (availableSlots.isEmpty()) {
            System.out.println("No available time slots for this doctor on " + date);
            return;
        }
        
        System.out.println("\nAvailable Time Slots:");
        for (int i = 0; i < availableSlots.size(); i++) {
            System.out.println((i + 1) + ". " + availableSlots.get(i));
        }
        
        int slotChoice = getIntInput("Choose a time slot (1-" + availableSlots.size() + "): ");
        if (slotChoice < 1 || slotChoice > availableSlots.size()) {
            System.out.println("Invalid time slot choice.");
            return;
        }
        
        LocalTime time = availableSlots.get(slotChoice - 1);
        String notes = getOptionalStringInput("Enter Notes (press Enter to skip): ");

        Appointment newAppointment = new Appointment(0, patientId, doctorId, date, time, "Scheduled", notes);
        boolean success = DatabaseManager.addAppointment(newAppointment);
        
        if (success) {
            System.out.println("Appointment scheduled successfully for " + date + " at " + time + "!");
        } else {
            System.out.println("Failed to schedule appointment.");
        }
    }

    // New method to check doctor availability
    private static void checkDoctorAvailability() {
        System.out.println("\n=== CHECK DOCTOR AVAILABILITY ===");
        displayAllDoctors();
        int doctorId = getIntInput("Enter Doctor ID: ");
        LocalDate date = getDateInput("Enter date to check availability (YYYY-MM-DD): ");
        
        List<LocalTime> availableSlots = DatabaseManager.getAvailableTimeSlots(doctorId, date);
        if (availableSlots.isEmpty()) {
            System.out.println("\nNo available time slots for this doctor on " + date);
        } else {
            System.out.println("\nAvailable Time Slots on " + date + ":");
            for (LocalTime slot : availableSlots) {
                System.out.println("- " + slot);
            }
        }
    }

    // Updated appointmentManagement menu
    private static void appointmentManagement() {
        while (true) {
            System.out.println("\n=== APPOINTMENT MANAGEMENT ===");
            System.out.println("1. View All Appointments");
            System.out.println("2. View Appointment Details");
            System.out.println("3. Schedule New Appointment");
            System.out.println("4. Update Appointment");
            System.out.println("5. Cancel Appointment");
            System.out.println("6. Mark Appointment as Completed");
            System.out.println("7. Check Doctor Availability");
            System.out.println("8. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> displayAllAppointments();
                case 2 -> viewAppointmentDetails();
                case 3 -> scheduleAppointment();
                case 4 -> updateAppointment();
                case 5 -> cancelAppointment();
                case 6 -> completeAppointment();
                case 7 -> checkDoctorAvailability();
                case 8 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayAllAppointments() {
        List<Appointment> appointments = DatabaseManager.getAllAppointments();
        System.out.println("\n=== ALL APPOINTMENTS ===");
        System.out.printf("%-12s %-20s %-15s %-20s %-20s %-10s%n",
                "Appt ID", "Date", "Time", "Patient", "Doctor", "Status");
        System.out.println("-------------------------------------------------------------------------------------");
        
        for (Appointment appt : appointments) {
            Patient patient = DatabaseManager.getPatientById(appt.getPatientID());
            Doctor doctor = DatabaseManager.getDoctorById(appt.getDoctorID());
            
            System.out.printf("%-12d %-20s %-15s %-20s %-20s %-10s%n",
                    appt.getAppointmentID(),
                    appt.getAppointmentDate(),
                    appt.getAppointmentTime(),
                    patient != null ? patient.getLastName() : "Unknown",
                    doctor != null ? doctor.getLastName() : "Unknown",
                    appt.getStatus());
        }
    }

    private static void viewAppointmentDetails() {
        int appointmentId = getIntInput("Enter Appointment ID: ");
        Appointment appointment = DatabaseManager.getAppointmentById(appointmentId);
        
        if (appointment != null) {
            System.out.println("\n=== APPOINTMENT DETAILS ===");
            System.out.println("Appointment ID: " + appointment.getAppointmentID());
            
            Patient patient = DatabaseManager.getPatientById(appointment.getPatientID());
            System.out.println("Patient: " + (patient != null ? 
                    patient.getFirstName() + " " + patient.getLastName() + " (ID: " + patient.getPatientID() + ")" : "Unknown"));
            
            Doctor doctor = DatabaseManager.getDoctorById(appointment.getDoctorID());
            System.out.println("Doctor: " + (doctor != null ? 
                    doctor.getFirstName() + " " + doctor.getLastName() + " (ID: " + doctor.getDoctorID() + ")" : "Unknown"));
            
            System.out.println("Date: " + appointment.getAppointmentDate());
            System.out.println("Time: " + appointment.getAppointmentTime());
            System.out.println("Status: " + appointment.getStatus());
            System.out.println("Notes: " + (appointment.getNotes() != null ? appointment.getNotes() : "N/A"));
            
            // Show billing information if exists
            Billing billing = DatabaseManager.getBillingByAppointmentId(appointmentId);
            if (billing != null) {
                System.out.println("\n=== BILLING INFORMATION ===");
                System.out.println("Billing ID: " + billing.getBillingID());
                System.out.println("Amount: $" + billing.getAmount());
                System.out.println("Tax (5%): $" + billing.getTax());
                System.out.println("Total Amount: $" + billing.getTotalAmount());
                System.out.println("Billing Date: " + billing.getBillingDate());
                System.out.println("Payment Status: " + billing.getPaymentStatus());
            }
        } else {
            System.out.println("Appointment not found with ID: " + appointmentId);
        }
    }

    private static void updateAppointment() {
        int appointmentId = getIntInput("Enter Appointment ID to update: ");
        Appointment appointment = DatabaseManager.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found with ID: " + appointmentId);
            return;
        }

        System.out.println("\nCurrent Appointment Information:");
        System.out.println("1. Patient ID: " + appointment.getPatientID());
        System.out.println("2. Doctor ID: " + appointment.getDoctorID());
        System.out.println("3. Date: " + appointment.getAppointmentDate());
        System.out.println("4. Time: " + appointment.getAppointmentTime());
        System.out.println("5. Notes: " + (appointment.getNotes() != null ? appointment.getNotes() : "N/A"));
        System.out.println("6. Cancel Update");

        int fieldToUpdate = getIntInput("Enter the number of the field to update (1-5) or 6 to cancel: ");
        
        if (fieldToUpdate == 6) {
            System.out.println("Update cancelled.");
            return;
        }

        switch (fieldToUpdate) {
            case 1 -> {
                displayAllPatients();
                appointment.setPatientID(getIntInput("Enter new Patient ID: "));
            }
            case 2 -> {
                displayAllDoctors();
                appointment.setDoctorID(getIntInput("Enter new Doctor ID: "));
            }
            case 3 -> appointment.setAppointmentDate(getDateInput("Enter new Date (YYYY-MM-DD): "));
            case 4 -> appointment.setAppointmentTime(getTimeInput("Enter new Time (HH:MM): "));
            case 5 -> appointment.setNotes(getOptionalStringInput("Enter new Notes (press Enter to clear): "));
            default -> {
                System.out.println("Invalid field number.");
                return;
            }
        }

        boolean success = DatabaseManager.updateAppointment(appointment);
        if (success) {
            System.out.println("Appointment updated successfully!");
        } else {
            System.out.println("Failed to update appointment.");
        }
    }

    private static void cancelAppointment() {
        int appointmentId = getIntInput("Enter Appointment ID to cancel: ");
        String confirm = getStringInput("Are you sure you want to cancel this appointment? (yes/no): ");
        
        if (confirm.equalsIgnoreCase("yes")) {
            boolean success = DatabaseManager.deleteAppointment(appointmentId);
            if (success) {
                System.out.println("Appointment cancelled successfully.");
            } else {
                System.out.println("Failed to cancel appointment or appointment not found.");
            }
        } else {
            System.out.println("Cancellation cancelled.");
        }
    }

    private static void completeAppointment() {
        int appointmentId = getIntInput("Enter Appointment ID to mark as completed: ");
        Appointment appointment = DatabaseManager.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found with ID: " + appointmentId);
            return;
        }

        if (appointment.getStatus().equals("Completed")) {
            System.out.println("This appointment is already marked as completed.");
            return;
        }

        appointment.setStatus("Completed");
        boolean success = DatabaseManager.updateAppointment(appointment);
        
        if (success) {
            System.out.println("Appointment marked as completed successfully!");
            
            // Create billing record
            double amount = getDoubleInput("Enter the amount for the appointment: $");
            Billing billing = new Billing(0, appointmentId, amount, 0, 0, LocalDate.now(), "Unpaid");
            DatabaseManager.addBilling(billing);
            System.out.println("Billing record created for this appointment.");
        } else {
            System.out.println("Failed to update appointment status.");
        }
    }

    // Billing Management
    private static void billingManagement() {
        while (true) {
            System.out.println("\n=== BILLING MANAGEMENT ===");
            System.out.println("1. View All Bills");
            System.out.println("2. View Bill Details");
            System.out.println("3. Create New Bill");
            System.out.println("4. Update Bill");
            System.out.println("5. Delete Bill");
            System.out.println("6. Mark Bill as Paid");
            System.out.println("7. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> displayAllBills();
                case 2 -> viewBillDetails();
                case 3 -> createNewBill();
                case 4 -> updateBill();
                case 5 -> deleteBill();
                case 6 -> markBillAsPaid();
                case 7 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayAllBills() {
        List<Billing> bills = DatabaseManager.getAllBillings();
        System.out.println("\n=== ALL BILLS ===");
        System.out.printf("%-10s %-12s %-20s %-10s %-10s %-15s %-10s%n",
                "Bill ID", "Appt ID", "Date", "Amount", "Tax", "Total", "Status");
        System.out.println("---------------------------------------------------------------------------");
        
        for (Billing bill : bills) {
            System.out.printf("%-10d %-12d %-20s $%-9.2f $%-9.2f $%-14.2f %-10s%n",
                    bill.getBillingID(),
                    bill.getAppointmentID(),
                    bill.getBillingDate(),
                    bill.getAmount(),
                    bill.getTax(),
                    bill.getTotalAmount(),
                    bill.getPaymentStatus());
        }
    }

    private static void viewBillDetails() {
        int billingId = getIntInput("Enter Billing ID: ");
        Billing billing = DatabaseManager.getBillingById(billingId);
        
        if (billing != null) {
            System.out.println("\n=== BILL DETAILS ===");
            System.out.println("Billing ID: " + billing.getBillingID());
            
            Appointment appointment = DatabaseManager.getAppointmentById(billing.getAppointmentID());
            if (appointment != null) {
                Patient patient = DatabaseManager.getPatientById(appointment.getPatientID());
                Doctor doctor = DatabaseManager.getDoctorById(appointment.getDoctorID());
                
                System.out.println("\n=== APPOINTMENT INFORMATION ===");
                System.out.println("Appointment ID: " + appointment.getAppointmentID());
                System.out.println("Patient: " + (patient != null ? 
                        patient.getFirstName() + " " + patient.getLastName() : "Unknown"));
                System.out.println("Doctor: " + (doctor != null ? 
                        doctor.getFirstName() + " " + doctor.getLastName() : "Unknown"));
                System.out.println("Date: " + appointment.getAppointmentDate());
                System.out.println("Time: " + appointment.getAppointmentTime());
            }
            
            System.out.println("\n=== BILLING INFORMATION ===");
            System.out.println("Amount: $" + billing.getAmount());
            System.out.println("Tax (5%): $" + billing.getTax());
            System.out.println("Total Amount: $" + billing.getTotalAmount());
            System.out.println("Billing Date: " + billing.getBillingDate());
            System.out.println("Payment Status: " + billing.getPaymentStatus());
        } else {
            System.out.println("Bill not found with ID: " + billingId);
        }
    }

    private static void createNewBill() {
        System.out.println("\n=== CREATE NEW BILL ===");
        
        // Display appointments for reference
        displayAllAppointments();
        int appointmentId = getIntInput("Enter Appointment ID: ");
        
        // Check if appointment exists and is completed
        Appointment appointment = DatabaseManager.getAppointmentById(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found with ID: " + appointmentId);
            return;
        }
        
        if (!appointment.getStatus().equals("Completed")) {
            System.out.println("Cannot create bill for an appointment that isn't completed.");
            return;
        }
        
        // Check if bill already exists for this appointment
        Billing existingBill = DatabaseManager.getBillingByAppointmentId(appointmentId);
        if (existingBill != null) {
            System.out.println("A bill already exists for this appointment (Bill ID: " + existingBill.getBillingID() + ")");
            return;
        }
        
        double amount = getDoubleInput("Enter the amount: $");
        Billing newBill = new Billing(0, appointmentId, amount, 0, 0, LocalDate.now(), "Unpaid");
        boolean success = DatabaseManager.addBilling(newBill);
        
        if (success) {
            System.out.println("Bill created successfully!");
        } else {
            System.out.println("Failed to create bill.");
        }
    }

    private static void updateBill() {
        int billingId = getIntInput("Enter Billing ID to update: ");
        Billing billing = DatabaseManager.getBillingById(billingId);
        
        if (billing == null) {
            System.out.println("Bill not found with ID: " + billingId);
            return;
        }

        System.out.println("\nCurrent Bill Information:");
        System.out.println("1. Appointment ID: " + billing.getAppointmentID());
        System.out.println("2. Amount: $" + billing.getAmount());
        System.out.println("3. Payment Status: " + billing.getPaymentStatus());
        System.out.println("4. Cancel Update");

        int fieldToUpdate = getIntInput("Enter the number of the field to update (1-3) or 4 to cancel: ");
        
        if (fieldToUpdate == 4) {
            System.out.println("Update cancelled.");
            return;
        }

        switch (fieldToUpdate) {
            case 1 -> {
                displayAllAppointments();
                int newApptId = getIntInput("Enter new Appointment ID: ");
                
                // Check if new appointment exists and is completed
                Appointment newAppointment = DatabaseManager.getAppointmentById(newApptId);
                if (newAppointment == null) {
                    System.out.println("Appointment not found with ID: " + newApptId);
                    return;
                }
                
                if (!newAppointment.getStatus().equals("Completed")) {
                    System.out.println("Cannot assign bill to an appointment that isn't completed.");
                    return;
                }
                
                billing.setAppointmentID(newApptId);
            }
            case 2 -> {
                double newAmount = getDoubleInput("Enter new Amount: $");
                billing.setAmount(newAmount);
                // Tax and TotalAmount are generated columns, they will update automatically
            }
            case 3 -> {
                String newStatus = billing.getPaymentStatus().equals("Paid") ? "Unpaid" : "Paid";
                billing.setPaymentStatus(newStatus);
            }
            default -> {
                System.out.println("Invalid field number.");
                return;
            }
        }

        boolean success = DatabaseManager.updateBilling(billing);
        if (success) {
            System.out.println("Bill updated successfully!");
        } else {
            System.out.println("Failed to update bill.");
        }
    }

    private static void deleteBill() {
        int billingId = getIntInput("Enter Billing ID to delete: ");
        String confirm = getStringInput("Are you sure you want to delete this bill? (yes/no): ");
        
        if (confirm.equalsIgnoreCase("yes")) {
            boolean success = DatabaseManager.deleteBilling(billingId);
            if (success) {
                System.out.println("Bill deleted successfully.");
            } else {
                System.out.println("Failed to delete bill or bill not found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void markBillAsPaid() {
        int billingId = getIntInput("Enter Billing ID to mark as paid: ");
        Billing billing = DatabaseManager.getBillingById(billingId);
        
        if (billing == null) {
            System.out.println("Bill not found with ID: " + billingId);
            return;
        }

        if (billing.getPaymentStatus().equals("Paid")) {
            System.out.println("This bill is already marked as paid.");
            return;
        }

        billing.setPaymentStatus("Paid");
        boolean success = DatabaseManager.updateBilling(billing);
        
        if (success) {
            System.out.println("Bill marked as paid successfully!");
        } else {
            System.out.println("Failed to update bill status.");
        }
    }

    // Helper methods for input
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static String getOptionalStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    private static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private static LocalTime getTimeInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalTime.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid time format. Please use HH:MM.");
            }
        }
    }

    private static String getGenderInput() {
        while (true) {
            System.out.print("Gender (Male/Female/Other): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Male") || input.equalsIgnoreCase("Female") || input.equalsIgnoreCase("Other")) {
                return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
            }
            System.out.println("Invalid gender. Please enter Male, Female, or Other.");
        }
    }
}