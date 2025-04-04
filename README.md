# Hospital Management System - CLI Setup Guide

## Quick Setup Instructions

1. **Install MySQL** on your system if not already installed

2. **Run these SQL scripts** in order:
   - First run `DDL script.txt` to create the database structure
   - Then run `DML sample data script.txt` to add sample data (optional)

3. **Update database credentials** if needed:
   - Open `HospitalManagementCLI.java`
   - Find the `DatabaseManager` class
   - Change `USER` and `PASSWORD` constants if your MySQL setup uses different credentials

4. **Run the application**:
   - Compile and execute `HospitalManagementCLI.java`
   - The main menu will appear when the program starts

That's it! The system is ready to use. The CLI interface will guide you through all available operations.