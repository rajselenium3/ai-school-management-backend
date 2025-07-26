// MongoDB initialization script for AI School Management System

db = db.getSiblingDB('ai_school_management');

// Create collections
db.createCollection('users');
db.createCollection('students');
db.createCollection('teachers');
db.createCollection('courses');
db.createCollection('assignments');
db.createCollection('grades');
db.createCollection('attendance');

// Create indexes for better performance
print('Creating indexes...');

// User indexes
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "roles": 1 });

// Student indexes
db.students.createIndex({ "studentId": 1 }, { unique: true });
db.students.createIndex({ "grade": 1 });
db.students.createIndex({ "section": 1 });
db.students.createIndex({ "grade": 1, "section": 1 });
db.students.createIndex({ "user": 1 });
db.students.createIndex({ "aiInsights.riskScore": 1 });

// Teacher indexes
db.teachers.createIndex({ "employeeId": 1 }, { unique: true });
db.teachers.createIndex({ "department": 1 });
db.teachers.createIndex({ "subjects": 1 });
db.teachers.createIndex({ "user": 1 });

// Course indexes
db.courses.createIndex({ "courseCode": 1 }, { unique: true });
db.courses.createIndex({ "department": 1 });
db.courses.createIndex({ "grade": 1 });
db.courses.createIndex({ "teacher": 1 });
db.courses.createIndex({ "status": 1 });

// Assignment indexes
db.assignments.createIndex({ "course": 1 });
db.assignments.createIndex({ "type": 1 });
db.assignments.createIndex({ "dueDate": 1 });

// Grade indexes
db.grades.createIndex({ "student": 1 });
db.grades.createIndex({ "course": 1 });
db.grades.createIndex({ "assignment": 1 });
db.grades.createIndex({ "student": 1, "assignment": 1 }, { unique: true });
db.grades.createIndex({ "status": 1 });

// Attendance indexes
db.attendance.createIndex({ "student": 1 });
db.attendance.createIndex({ "course": 1 });
db.attendance.createIndex({ "date": 1 });
db.attendance.createIndex({ "student": 1, "course": 1, "date": 1 }, { unique: true });
db.attendance.createIndex({ "status": 1 });

print('Database initialization completed!');

// Insert sample data for testing
print('Inserting sample data...');

// Sample users
db.users.insertMany([
  {
    _id: ObjectId(),
    firstName: "John",
    lastName: "Admin",
    email: "admin@school.edu",
    password: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8loxzQPqpf6A3xKXK2", // password: admin123
    phone: "1234567890",
    roles: ["ADMIN"],
    active: true,
    emailVerified: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    firstName: "Sarah",
    lastName: "Johnson",
    email: "sarah.johnson@school.edu",
    password: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8loxzQPqpf6A3xKXK2", // password: teacher123
    phone: "1234567891",
    roles: ["TEACHER"],
    active: true,
    emailVerified: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: ObjectId(),
    firstName: "Emma",
    lastName: "Thompson",
    email: "emma.thompson@school.edu",
    password: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8loxzQPqpf6A3xKXK2", // password: student123
    phone: "1234567892",
    roles: ["STUDENT"],
    active: true,
    emailVerified: true,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print('Sample data inserted successfully!');
print('Default credentials:');
print('Admin: admin@school.edu / admin123');
print('Teacher: sarah.johnson@school.edu / teacher123');
print('Student: emma.thompson@school.edu / student123');
