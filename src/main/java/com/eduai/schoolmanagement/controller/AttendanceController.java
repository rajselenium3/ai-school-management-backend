package com.eduai.schoolmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.eduai.schoolmanagement.entity.Attendance;
import com.eduai.schoolmanagement.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "Attendance tracking and management operations")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @Operation(summary = "Get all attendance records")
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        List<Attendance> attendance = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attendance by ID")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable String id) {
        Optional<Attendance> attendance = attendanceService.getAttendanceById(id);
        return attendance.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get attendance by student ID")
    public ResponseEntity<List<Attendance>> getAttendanceByStudentId(@PathVariable String studentId) {
        List<Attendance> attendance = attendanceService.getAttendanceByStudentId(studentId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get attendance by course ID")
    public ResponseEntity<List<Attendance>> getAttendanceByCourseId(@PathVariable String courseId) {
        List<Attendance> attendance = attendanceService.getAttendanceByCourseId(courseId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get attendance by date")
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendance = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get attendance by date range")
    public ResponseEntity<List<Attendance>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Attendance> attendance = attendanceService.getAttendanceByDateRange(startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}/date-range")
    @Operation(summary = "Get student attendance by date range")
    public ResponseEntity<List<Attendance>> getStudentAttendanceByDateRange(
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Attendance> attendance = attendanceService.getStudentAttendanceByDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @Operation(summary = "Get attendance by course and date")
    public ResponseEntity<List<Attendance>> getAttendanceByCourseAndDate(
            @PathVariable String courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendance = attendanceService.getAttendanceByCourseAndDate(courseId, date);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get attendance by status")
    public ResponseEntity<List<Attendance>> getAttendanceByStatus(@PathVariable String status) {
        List<Attendance> attendance = attendanceService.getAttendanceByStatus(status);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping
    @Operation(summary = "Create new attendance record")
    public ResponseEntity<Attendance> createAttendance(@Valid @RequestBody Attendance attendance) {
        Attendance savedAttendance = attendanceService.saveAttendance(attendance);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple attendance records")
    public ResponseEntity<List<Attendance>> createBulkAttendance(@Valid @RequestBody List<Attendance> attendanceList) {
        List<Attendance> savedAttendance = attendanceService.saveBulkAttendance(attendanceList);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendance);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update attendance record")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable String id, @Valid @RequestBody Attendance attendance) {
        Attendance updatedAttendance = attendanceService.updateAttendance(id, attendance);
        return ResponseEntity.ok(updatedAttendance);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attendance record")
    public ResponseEntity<Void> deleteAttendance(@PathVariable String id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}/rate")
    @Operation(summary = "Get student attendance rate")
    public ResponseEntity<Double> getStudentAttendanceRate(@PathVariable String studentId) {
        double rate = attendanceService.getStudentAttendanceRate(studentId);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/course/{courseId}/rate")
    @Operation(summary = "Get course attendance rate")
    public ResponseEntity<Double> getCourseAttendanceRate(@PathVariable String courseId) {
        double rate = attendanceService.getCourseAttendanceRate(courseId);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/student/{studentId}/summary")
    @Operation(summary = "Get student attendance summary")
    public ResponseEntity<Map<String, Object>> getStudentAttendanceSummary(@PathVariable String studentId) {
        Map<String, Object> summary = attendanceService.getStudentAttendanceSummary(studentId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/course/{courseId}/summary")
    @Operation(summary = "Get course attendance summary")
    public ResponseEntity<Map<String, Object>> getCourseAttendanceSummary(@PathVariable String courseId) {
        Map<String, Object> summary = attendanceService.getCourseAttendanceSummary(courseId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/daily-report/{date}")
    @Operation(summary = "Get daily attendance report")
    public ResponseEntity<Map<String, Object>> getDailyAttendanceReport(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> report = attendanceService.getDailyAttendanceReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly-report")
    @Operation(summary = "Get monthly attendance report")
    public ResponseEntity<Map<String, Object>> getMonthlyAttendanceReport(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> report = attendanceService.getMonthlyAttendanceReport(year, month);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/mark-present/{studentId}/{courseId}")
    @Operation(summary = "Mark student as present")
    public ResponseEntity<Attendance> markStudentPresent(
            @PathVariable String studentId,
            @PathVariable String courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate attendanceDate = date != null ? date : LocalDate.now();
        Attendance attendance = attendanceService.markStudentPresent(studentId, courseId, attendanceDate);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/mark-absent/{studentId}/{courseId}")
    @Operation(summary = "Mark student as absent")
    public ResponseEntity<Attendance> markStudentAbsent(
            @PathVariable String studentId,
            @PathVariable String courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate attendanceDate = date != null ? date : LocalDate.now();
        Attendance attendance = attendanceService.markStudentAbsent(studentId, courseId, attendanceDate);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/analytics/trends")
    @Operation(summary = "Get attendance trends and analytics")
    public ResponseEntity<Map<String, Object>> getAttendanceTrends(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> trends = attendanceService.getAttendanceTrends(studentId, courseId, startDate, endDate);
        return ResponseEntity.ok(trends);
    }
}
