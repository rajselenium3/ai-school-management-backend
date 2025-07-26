package com.eduai.schoolmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.CourseRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import com.eduai.schoolmanagement.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    public List<Course> getCoursesByTeacherId(String teacherId) {
        return courseRepository.findByTeacherTeacherId(teacherId);
    }

    public List<Course> getCoursesByGrade(String grade) {
        return courseRepository.findByGrade(grade);
    }

    public List<Course> getCoursesByStatus(String status) {
        return courseRepository.findByStatus(status);
    }

    public Page<Course> searchCoursesByName(String name, Pageable pageable) {
        return courseRepository.findByCourseNameContainingIgnoreCase(name, pageable);
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByStatus("ACTIVE");
    }

    public List<Course> getUpcomingCourses() {
        return courseRepository.findByStatus("UPCOMING");
    }

    public Course saveCourse(Course course) {
        log.info("Saving course: {}", course.getCourseCode());

        // Initialize analytics if not present
        if (course.getAnalytics() == null) {
            Course.CourseAnalytics analytics = new Course.CourseAnalytics();
            analytics.setAverageScore(0.0);
            analytics.setCompletionRate(0.0);
            analytics.setEnrollmentCount(0);
            analytics.setAiScore(0.0);
            analytics.setPerformanceTrend("STABLE");
            analytics.setLastUpdated(LocalDate.now());
            course.setAnalytics(analytics);
        }

        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course course) {
        course.setId(id);
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        log.info("Deleting course with id: {}", id);
        courseRepository.deleteById(id);
    }

    public Course enrollStudent(String courseId, String studentId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (courseOpt.isPresent() && studentOpt.isPresent()) {
            Course course = courseOpt.get();
            Student student = studentOpt.get();

            // Create student info for course enrollment
            Course.StudentInfo studentInfo = new Course.StudentInfo();
            studentInfo.setStudentId(student.getStudentId());
            studentInfo.setFirstName(student.getUser().getFirstName());
            studentInfo.setLastName(student.getUser().getLastName());
            studentInfo.setEmail(student.getUser().getEmail());
            studentInfo.setGrade(student.getGrade());
            studentInfo.setSection(student.getSection());
            studentInfo.setEnrollmentDate(LocalDate.now());

            // Add to enrolled students if not already enrolled
            if (course.getEnrolledStudents() != null) {
                boolean alreadyEnrolled = course.getEnrolledStudents().stream()
                    .anyMatch(s -> s.getStudentId().equals(student.getStudentId()));

                if (!alreadyEnrolled) {
                    course.getEnrolledStudents().add(studentInfo);
                }
            } else {
                course.setEnrolledStudents(List.of(studentInfo));
            }

            // Update analytics
            updateEnrollmentAnalytics(course);

            log.info("Enrolled student {} in course {}", studentId, courseId);
            return courseRepository.save(course);
        }

        throw new RuntimeException("Course or Student not found");
    }

    public Course unenrollStudent(String courseId, String studentId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();

            if (course.getEnrolledStudents() != null) {
                course.getEnrolledStudents().removeIf(s -> s.getStudentId().equals(studentId));

                // Update analytics
                updateEnrollmentAnalytics(course);

                log.info("Unenrolled student {} from course {}", studentId, courseId);
                return courseRepository.save(course);
            }
        }

        throw new RuntimeException("Course not found");
    }

    public void updateCourseAnalytics(String courseId, Course.CourseAnalytics analytics) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            analytics.setLastUpdated(LocalDate.now());
            course.setAnalytics(analytics);
            courseRepository.save(course);
            log.info("Updated analytics for course: {}", courseId);
        }
    }

    public long getCourseCountByDepartment(String department) {
        return courseRepository.countByDepartment(department);
    }

    public long getCourseCountByStatus(String status) {
        return courseRepository.countByStatus(status);
    }

    public Course.CourseAnalytics getCourseEnrollmentStats(String courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            return course.getAnalytics();
        }
        throw new RuntimeException("Course not found");
    }

    private void updateEnrollmentAnalytics(Course course) {
        if (course.getAnalytics() == null) {
            course.setAnalytics(new Course.CourseAnalytics());
        }

        Course.CourseAnalytics analytics = course.getAnalytics();
        int enrollmentCount = course.getEnrolledStudents() != null ? course.getEnrolledStudents().size() : 0;
        analytics.setEnrollmentCount(enrollmentCount);

        // Calculate completion rate if capacity is set
        if (course.getCapacity() > 0) {
            double completionRate = (double) enrollmentCount / course.getCapacity() * 100;
            analytics.setCompletionRate(Math.min(completionRate, 100.0));
        }

        analytics.setLastUpdated(LocalDate.now());
    }
}
