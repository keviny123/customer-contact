@echo off
echo ============================================
echo   MySQL Setup for Customer Contact API
echo ============================================
echo.

echo Step 1: Testing MySQL Service Status
sc query MySQL80
echo.

echo Step 2: Attempting to connect to MySQL
echo If you're prompted for a password, try:
echo   - (no password - just press Enter)
echo   - password
echo   - root
echo   - admin
echo.

echo Running MySQL command...
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "SHOW DATABASES;"

echo.
echo Step 3: If connection successful, the next commands will set up the database:
echo.

pause

echo Creating contactdb database...
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "CREATE DATABASE IF NOT EXISTS contactdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo.
echo Step 4: Creating user for the application...
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "CREATE USER IF NOT EXISTS 'contactapi'@'localhost' IDENTIFIED BY 'password';"
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "GRANT ALL PRIVILEGES ON contactdb.* TO 'contactapi'@'localhost';"
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "FLUSH PRIVILEGES;"

echo.
echo Step 5: Running database creation script...
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -D contactdb < sql\01_create_database.sql

echo.
echo Step 6: Inserting test data...
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -D contactdb < sql\02_insert_test_data.sql

echo.
echo ============================================
echo   MySQL Setup Complete!
echo ============================================
echo.
echo Your database is ready with:
echo - Database: contactdb
echo - User: contactapi / password
echo - Test data: 13 contact records
echo.
echo Next: Run the Spring Boot application
echo.
pause