-- Insert test data for Customer Contact API
-- Run this after creating the database and table

USE contactdb;

-- Clear existing data (for testing)
DELETE FROM contact;

-- Insert comprehensive test data
INSERT INTO contact (first_name, last_name, email, phone, primary_phone, address, primary_email) VALUES
('John', 'Doe', 'john.doe@example.com', '555-0001', '555-1001', '123 Main St, Anytown, USA', 'john.primary@example.com'),
('Jane', 'Smith', 'jane.smith@example.com', '555-0002', '555-1002', '456 Oak Ave, Springfield, USA', 'jane.primary@example.com'),
('Michael', 'Johnson', 'michael.johnson@example.com', '555-0003', '555-1003', '789 Pine Rd, Riverside, USA', 'michael.primary@example.com'),
('Emily', 'Davis', 'emily.davis@example.com', '555-0004', '555-1004', '321 Elm St, Lakewood, USA', 'emily.primary@example.com'),
('David', 'Wilson', 'david.wilson@example.com', '555-0005', '555-1005', '654 Maple Dr, Hillcrest, USA', 'david.primary@example.com'),
('Sarah', 'Brown', 'sarah.brown@example.com', '555-0006', '555-1006', '987 Cedar Ln, Greenfield, USA', 'sarah.primary@example.com'),
('Robert', 'Jones', 'robert.jones@example.com', '555-0007', '555-1007', '147 Birch Ct, Fairview, USA', 'robert.primary@example.com'),
('Lisa', 'Garcia', 'lisa.garcia@example.com', '555-0008', '555-1008', '258 Willow Way, Meadowbrook, USA', 'lisa.primary@example.com'),
('James', 'Martinez', 'james.martinez@example.com', '555-0009', '555-1009', '369 Aspen Blvd, Mountain View, USA', 'james.primary@example.com'),
('Jennifer', 'Anderson', 'jennifer.anderson@example.com', '555-0010', '555-1010', '741 Spruce St, Valley Heights, USA', 'jennifer.primary@example.com');

-- Additional test cases for edge cases
INSERT INTO contact (first_name, last_name, email, phone, address) VALUES
('Test', 'User1', 'test1@example.com', '555-9001', '100 Test St, Test City, USA'),
('Test', 'User2', 'test2@example.com', NULL, NULL),
('Long Name For Testing Purposes', 'Very Long Last Name That Tests Length Limits', 'longname@example.com', '555-9003', '999 Very Long Address Name That Tests The Maximum Length Limits Of The Address Field In The Database Table');

-- Show inserted data
SELECT COUNT(*) as 'Total Records' FROM contact;
SELECT * FROM contact ORDER BY id LIMIT 5;

-- Test queries for validation
SELECT 'Data validation:' as status;
SELECT COUNT(*) as 'Records with primary_phone' FROM contact WHERE primary_phone IS NOT NULL;
SELECT COUNT(*) as 'Records with primary_email' FROM contact WHERE primary_email IS NOT NULL;
SELECT COUNT(*) as 'Records with complete data' FROM contact WHERE primary_phone IS NOT NULL AND primary_email IS NOT NULL;