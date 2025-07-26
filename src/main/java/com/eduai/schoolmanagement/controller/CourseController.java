package com.eduai.schoolmanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "Course management operations")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{courseCode}")
    @Operation(summary = "Get course by course code")
    public ResponseEntity<Course> getCourseByCourseCode(@PathVariable String courseCode) {
        Optional<Course> course = courseService.getCourseByCourseCode(courseCode);
        return course.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get courses by department")
    public ResponseEntity<List<Course>> getCoursesByDepartment(@PathVariable String department) {
        List<Course> courses = courseService.getCoursesByDepartment(department);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get courses by teacher ID")
    public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable String teacherId) {
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/grade/{grade}")
    @Operation(summary = "Get courses by grade")
    public ResponseEntity<List<Course>> getCoursesByGrade(@PathVariable String grade) {
        List<Course> courses = courseService.getCoursesByGrade(grade);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get courses by status")
    public ResponseEntity<List<Course>> getCoursesByStatus(@PathVariable String status) {
        List<Course> courses = courseService.getCoursesByStatus(status);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses by name")
    public ResponseEntity<Page<Course>> searchCoursesByName(
            @RequestParam String name, Pageable pageable) {
        Page<Course> courses = courseService.searchCoursesByName(name, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active courses")
    public ResponseEntity<List<Course>> getActiveCourses() {
        List<Course> courses = courseService.getActiveCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming courses")
    public ResponseEntity<List<Course>> getUpcomingCourses() {
        List<Course> courses = courseService.getUpcomingCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    @Operation(summary = "Create new course")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.saveCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @Valid @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    @Operation(summary = "Enroll student in course")
    public ResponseEntity<Course> enrollStudent(
            @PathVariable String courseId, @PathVariable String studentId) {
        Course updatedCourse = courseService.enrollStudent(courseId, studentId);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{courseId}/unenroll/{studentId}")
    @Operation(summary = "Unenroll student from course")
    public ResponseEntity<Course> unenrollStudent(
            @PathVariable String courseId, @PathVariable String studentId) {
        Course updatedCourse = courseService.unenrollStudent(courseId, studentId);
        return ResponseEntity.ok(updatedCourse);
    }

    @PutMapping("/{courseId}/analytics")
    @Operation(summary = "Update course analytics")
    public ResponseEntity<Void> updateCourseAnalytics(
            @PathVariable String courseId, @RequestBody Course.CourseAnalytics analytics) {
        courseService.updateCourseAnalytics(courseId, analytics);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/department/{department}")
    @Operation(summary = "Get course count by department")
    public ResponseEntity<Long> getCourseCountByDepartment(@PathVariable String department) {
        long count = courseService.getCourseCountByDepartment(department);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Get course count by status")
    public ResponseEntity<Long> getCourseCountByStatus(@PathVariable String status) {
        long count = courseService.getCourseCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/enrollment-stats/{courseId}")
    @Operation(summary = "Get enrollment statistics for course")
    public ResponseEntity<Course.CourseAnalytics> getCourseEnrollmentStats(@PathVariable String courseId) {
        Course.CourseAnalytics stats = courseService.getCourseEnrollmentStats(courseId);
        return ResponseEntity.ok(stats);
    }
}
