import java.sql.*;
import java.util.List;
import java.util.ArrayList;

class DatabaseManager {
    private static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/HospitalManagementSystem";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error disconnecting from database: " + e.getMessage());
        }
    }

    // Patient CRUD operations
    public static List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients ORDER BY LastName, FirstName";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(new Patient(
                    rs.getInt("PatientID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getDate("DOB").toLocalDate(),
                    rs.getString("Gender"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Address")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving patients: " + e.getMessage());
        }
        
        return patients;
    }

    public static Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM Patients WHERE PatientID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Patient(
                    rs.getInt("PatientID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getDate("DOB").toLocalDate(),
                    rs.getString("Gender"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Address")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving patient: " + e.getMessage());
        }
        
        return null;
    }

    public static boolean addPatient(Patient patient) {
        String sql = "INSERT INTO Patients (FirstName, LastName, DOB, Gender, Email, PhoneNumber, Address) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setDate(3, Date.valueOf(patient.getDob()));
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getEmail());
            pstmt.setString(6, patient.getPhoneNumber());
            pstmt.setString(7, patient.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setPatientID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean updatePatient(Patient patient) {
        String sql = "UPDATE Patients SET FirstName = ?, LastName = ?, DOB = ?, Gender = ?, " +
                     "Email = ?, PhoneNumber = ?, Address = ? WHERE PatientID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setDate(3, Date.valueOf(patient.getDob()));
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getEmail());
            pstmt.setString(6, patient.getPhoneNumber());
            pstmt.setString(7, patient.getAddress());
            pstmt.setInt(8, patient.getPatientID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean deletePatient(int patientId) {
        // First delete dependent records (billings and appointments)
        String deleteBillingsSql = "DELETE FROM Billings WHERE AppointmentID IN " +
                                 "(SELECT AppointmentID FROM Appointments WHERE PatientID = ?)";
        String deleteAppointmentsSql = "DELETE FROM Appointments WHERE PatientID = ?";
        String deletePatientSql = "DELETE FROM Patients WHERE PatientID = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt1 = connection.prepareStatement(deleteBillingsSql);
                 PreparedStatement pstmt2 = connection.prepareStatement(deleteAppointmentsSql);
                 PreparedStatement pstmt3 = connection.prepareStatement(deletePatientSql)) {
                
                pstmt1.setInt(1, patientId);
                pstmt1.executeUpdate();
                
                pstmt2.setInt(1, patientId);
                pstmt2.executeUpdate();
                
                pstmt3.setInt(1, patientId);
                int affectedRows = pstmt3.executeUpdate();
                
                connection.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("Error deleting patient: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
        
        return false;
    }

    // Doctor CRUD operations
    public static List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctors ORDER BY LastName, FirstName";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(new Doctor(
                    rs.getInt("DoctorID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Specialty"),
                    rs.getDouble("Salary")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctors: " + e.getMessage());
        }
        
        return doctors;
    }

    public static Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM Doctors WHERE DoctorID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Doctor(
                    rs.getInt("DoctorID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Specialty"),
                    rs.getDouble("Salary")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctor: " + e.getMessage());
        }
        
        return null;
    }

    public static boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO Doctors (FirstName, LastName, Email, PhoneNumber, Specialty, Salary) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, doctor.getFirstName());
            pstmt.setString(2, doctor.getLastName());
            pstmt.setString(3, doctor.getEmail());
            pstmt.setString(4, doctor.getPhoneNumber());
            pstmt.setString(5, doctor.getSpecialty());
            pstmt.setDouble(6, doctor.getSalary());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setDoctorID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding doctor: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE Doctors SET FirstName = ?, LastName = ?, Email = ?, " +
                     "PhoneNumber = ?, Specialty = ?, Salary = ? WHERE DoctorID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getFirstName());
            pstmt.setString(2, doctor.getLastName());
            pstmt.setString(3, doctor.getEmail());
            pstmt.setString(4, doctor.getPhoneNumber());
            pstmt.setString(5, doctor.getSpecialty());
            pstmt.setDouble(6, doctor.getSalary());
            pstmt.setInt(7, doctor.getDoctorID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating doctor: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean deleteDoctor(int doctorId) {
        // First delete dependent records (billings and appointments)
        String deleteBillingsSql = "DELETE FROM Billings WHERE AppointmentID IN " +
                                 "(SELECT AppointmentID FROM Appointments WHERE DoctorID = ?)";
        String deleteAppointmentsSql = "DELETE FROM Appointments WHERE DoctorID = ?";
        String deleteDoctorSql = "DELETE FROM Doctors WHERE DoctorID = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt1 = connection.prepareStatement(deleteBillingsSql);
                 PreparedStatement pstmt2 = connection.prepareStatement(deleteAppointmentsSql);
                 PreparedStatement pstmt3 = connection.prepareStatement(deleteDoctorSql)) {
                
                pstmt1.setInt(1, doctorId);
                pstmt1.executeUpdate();
                
                pstmt2.setInt(1, doctorId);
                pstmt2.executeUpdate();
                
                pstmt3.setInt(1, doctorId);
                int affectedRows = pstmt3.executeUpdate();
                
                connection.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("Error deleting doctor: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
        
        return false;
    }

    // Appointment CRUD operations
    public static List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointments ORDER BY AppointmentDate DESC, AppointmentTime DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("AppointmentID"),
                    rs.getInt("PatientID"),
                    rs.getInt("DoctorID"),
                    rs.getDate("AppointmentDate").toLocalDate(),
                    rs.getTime("AppointmentTime").toLocalTime(),
                    rs.getString("Status"),
                    rs.getString("Notes")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointments: " + e.getMessage());
        }
        
        return appointments;
    }

    public static Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT * FROM Appointments WHERE AppointmentID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Appointment(
                    rs.getInt("AppointmentID"),
                    rs.getInt("PatientID"),
                    rs.getInt("DoctorID"),
                    rs.getDate("AppointmentDate").toLocalDate(),
                    rs.getTime("AppointmentTime").toLocalTime(),
                    rs.getString("Status"),
                    rs.getString("Notes")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointment: " + e.getMessage());
        }
        
        return null;
    }

    public static List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointments WHERE PatientID = ? ORDER BY AppointmentDate DESC, AppointmentTime DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("AppointmentID"),
                    rs.getInt("PatientID"),
                    rs.getInt("DoctorID"),
                    rs.getDate("AppointmentDate").toLocalDate(),
                    rs.getTime("AppointmentTime").toLocalTime(),
                    rs.getString("Status"),
                    rs.getString("Notes")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointments: " + e.getMessage());
        }
        
        return appointments;
    }

    public static List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointments WHERE DoctorID = ? ORDER BY AppointmentDate DESC, AppointmentTime DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("AppointmentID"),
                    rs.getInt("PatientID"),
                    rs.getInt("DoctorID"),
                    rs.getDate("AppointmentDate").toLocalDate(),
                    rs.getTime("AppointmentTime").toLocalTime(),
                    rs.getString("Status"),
                    rs.getString("Notes")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointments: " + e.getMessage());
        }
        
        return appointments;
    }

    public static boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO Appointments (PatientID, DoctorID, AppointmentDate, AppointmentTime, Status, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, appointment.getPatientID());
            pstmt.setInt(2, appointment.getDoctorID());
            pstmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(5, appointment.getStatus());
            pstmt.setString(6, appointment.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setAppointmentID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding appointment: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE Appointments SET PatientID = ?, DoctorID = ?, AppointmentDate = ?, " +
                     "AppointmentTime = ?, Status = ?, Notes = ? WHERE AppointmentID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getPatientID());
            pstmt.setInt(2, appointment.getDoctorID());
            pstmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(5, appointment.getStatus());
            pstmt.setString(6, appointment.getNotes());
            pstmt.setInt(7, appointment.getAppointmentID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean deleteAppointment(int appointmentId) {
        // First delete dependent billing record if exists
        String deleteBillingSql = "DELETE FROM Billings WHERE AppointmentID = ?";
        String deleteAppointmentSql = "DELETE FROM Appointments WHERE AppointmentID = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt1 = connection.prepareStatement(deleteBillingSql);
                 PreparedStatement pstmt2 = connection.prepareStatement(deleteAppointmentSql)) {
                
                pstmt1.setInt(1, appointmentId);
                pstmt1.executeUpdate();
                
                pstmt2.setInt(1, appointmentId);
                int affectedRows = pstmt2.executeUpdate();
                
                connection.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("Error deleting appointment: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
        
        return false;
    }

    // Billing CRUD operations
    public static List<Billing> getAllBillings() {
        List<Billing> billings = new ArrayList<>();
        String sql = "SELECT * FROM Billings ORDER BY BillingDate DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                billings.add(new Billing(
                    rs.getInt("BillingID"),
                    rs.getInt("AppointmentID"),
                    rs.getDouble("Amount"),
                    rs.getDouble("Tax"),
                    rs.getDouble("TotalAmount"),
                    rs.getDate("BillingDate").toLocalDate(),
                    rs.getString("PaymentStatus")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving billings: " + e.getMessage());
        }
        
        return billings;
    }

    public static Billing getBillingById(int billingId) {
        String sql = "SELECT * FROM Billings WHERE BillingID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, billingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Billing(
                    rs.getInt("BillingID"),
                    rs.getInt("AppointmentID"),
                    rs.getDouble("Amount"),
                    rs.getDouble("Tax"),
                    rs.getDouble("TotalAmount"),
                    rs.getDate("BillingDate").toLocalDate(),
                    rs.getString("PaymentStatus")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving billing: " + e.getMessage());
        }
        
        return null;
    }

    public static Billing getBillingByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM Billings WHERE AppointmentID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Billing(
                    rs.getInt("BillingID"),
                    rs.getInt("AppointmentID"),
                    rs.getDouble("Amount"),
                    rs.getDouble("Tax"),
                    rs.getDouble("TotalAmount"),
                    rs.getDate("BillingDate").toLocalDate(),
                    rs.getString("PaymentStatus")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving billing: " + e.getMessage());
        }
        
        return null;
    }

    public static boolean addBilling(Billing billing) {
        String sql = "INSERT INTO Billings (AppointmentID, Amount, BillingDate, PaymentStatus) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, billing.getAppointmentID());
            pstmt.setDouble(2, billing.getAmount());
            pstmt.setDate(3, Date.valueOf(billing.getBillingDate()));
            pstmt.setString(4, billing.getPaymentStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        billing.setBillingID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding billing: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean updateBilling(Billing billing) {
        String sql = "UPDATE Billings SET AppointmentID = ?, Amount = ?, " +
                     "PaymentStatus = ? WHERE BillingID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, billing.getAppointmentID());
            pstmt.setDouble(2, billing.getAmount());
            pstmt.setString(3, billing.getPaymentStatus());
            pstmt.setInt(4, billing.getBillingID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating billing: " + e.getMessage());
        }
        
        return false;
    }

    public static boolean deleteBilling(int billingId) {
        String sql = "DELETE FROM Billings WHERE BillingID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, billingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting billing: " + e.getMessage());
        }
        
        return false;
    }
}