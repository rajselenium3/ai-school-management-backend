package com.eduai.schoolmanagement.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Attendance;
import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.AttendanceRepository;
import com.eduai.schoolmanagement.repository.CourseRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getAttendanceById(String id) {
        return attendanceRepository.findById(id);
    }

    public List<Attendance> getAttendanceByStudentId(String studentId) {
        return attendanceRepository.findByStudentStudentId(studentId);
    }

    public List<Attendance> getAttendanceByCourseId(String courseId) {
        return attendanceRepository.findByCourseCourseCode(courseId);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByDateBetween(startDate, endDate);
    }

    public List<Attendance> getStudentAttendanceByDateRange(String studentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentStudentIdAndDateBetween(studentId, startDate, endDate);
    }

    public List<Attendance> getAttendanceByCourseAndDate(String courseId, LocalDate date) {
        return attendanceRepository.findByCourseCourseCodeAndDate(courseId, date);
    }

    public List<Attendance> getAttendanceByStatus(String status) {
        return attendanceRepository.findByStatus(status);
    }

    public Attendance saveAttendance(Attendance attendance) {
        log.info("Saving attendance for student: {} on date: {}",
                attendance.getStudent().getStudentId(), attendance.getDate());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> saveBulkAttendance(List<Attendance> attendanceList) {
        log.info("Saving bulk attendance records: {} records", attendanceList.size());
        return attendanceRepository.saveAll(attendanceList);
    }

    public Attendance updateAttendance(String id, Attendance attendance) {
        attendance.setId(id);
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(String id) {
        log.info("Deleting attendance record with id: {}", id);
        attendanceRepository.deleteById(id);
    }

    public double getStudentAttendanceRate(String studentId) {
        List<Attendance> studentAttendance = getAttendanceByStudentId(studentId);
        if (studentAttendance.isEmpty()) {
            return 0.0;
        }

        long presentCount = studentAttendance.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();

        return (double) presentCount / studentAttendance.size() * 100;
    }

    public double getCourseAttendanceRate(String courseId) {
        List<Attendance> courseAttendance = getAttendanceByCourseId(courseId);
        if (courseAttendance.isEmpty()) {
            return 0.0;
        }

        long presentCount = courseAttendance.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();

        return (double) presentCount / courseAttendance.size() * 100;
    }

    public Map<String, Object> getStudentAttendanceSummary(String studentId) {
        List<Attendance> studentAttendance = getAttendanceByStudentId(studentId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSessions", studentAttendance.size());
        summary.put("presentCount", studentAttendance.stream()
                .filter(a -> "PRESENT".equals(a.getStatus())).count());
        summary.put("absentCount", studentAttendance.stream()
                .filter(a -> "ABSENT".equals(a.getStatus())).count());
        summary.put("lateCount", studentAttendance.stream()
                .filter(a -> "LATE".equals(a.getStatus())).count());
        summary.put("attendanceRate", getStudentAttendanceRate(studentId));

        // Recent attendance pattern (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Attendance> recentAttendance = studentAttendance.stream()
                .filter(a -> a.getDate().isAfter(thirtyDaysAgo))
                .collect(Collectors.toList());

        summary.put("recentAttendanceCount", recentAttendance.size());
        summary.put("recentPresentCount", recentAttendance.stream()
                .filter(a -> "PRESENT".equals(a.getStatus())).count());

        return summary;
    }

    public Map<String, Object> getCourseAttendanceSummary(String courseId) {
        List<Attendance> courseAttendance = getAttendanceByCourseId(courseId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRecords", courseAttendance.size());
        summary.put("averageAttendanceRate", getCourseAttendanceRate(courseId));

        // Group by student to get unique student count
        Set<String> uniqueStudents = courseAttendance.stream()
                .map(a -> a.getStudent().getStudentId())
                .collect(Collectors.toSet());
        summary.put("enrolledStudents", uniqueStudents.size());

        // Status breakdown
        Map<String, Long> statusCounts = courseAttendance.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));
        summary.put("statusBreakdown", statusCounts);

        return summary;
    }

    public Map<String, Object> getDailyAttendanceReport(LocalDate date) {
        List<Attendance> dailyAttendance = getAttendanceByDate(date);

        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalRecords", dailyAttendance.size());

        Map<String, Long> statusCounts = dailyAttendance.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));
        report.put("statusBreakdown", statusCounts);

        // Calculate overall attendance rate for the day
        long presentCount = statusCounts.getOrDefault("PRESENT", 0L);
        double attendanceRate = dailyAttendance.isEmpty() ? 0.0 :
                (double) presentCount / dailyAttendance.size() * 100;
        report.put("attendanceRate", attendanceRate);

        return report;
    }

    public Map<String, Object> getMonthlyAttendanceReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Attendance> monthlyAttendance = getAttendanceByDateRange(startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("month", month);
        report.put("totalRecords", monthlyAttendance.size());

        // Group by date to get daily statistics
        Map<LocalDate, List<Attendance>> dailyAttendance = monthlyAttendance.stream()
                .collect(Collectors.groupingBy(Attendance::getDate));

        Map<String, Object> dailyStats = new HashMap<>();
        for (Map.Entry<LocalDate, List<Attendance>> entry : dailyAttendance.entrySet()) {
            List<Attendance> dayAttendance = entry.getValue();
            long presentCount = dayAttendance.stream()
                    .filter(a -> "PRESENT".equals(a.getStatus())).count();
            double dayRate = (double) presentCount / dayAttendance.size() * 100;
            dailyStats.put(entry.getKey().toString(), dayRate);
        }
        report.put("dailyRates", dailyStats);

        // Overall monthly rate
        long totalPresent = monthlyAttendance.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();
        double monthlyRate = monthlyAttendance.isEmpty() ? 0.0 :
                (double) totalPresent / monthlyAttendance.size() * 100;
        report.put("monthlyAttendanceRate", monthlyRate);

        return report;
    }

    public Attendance markStudentPresent(String studentId, String courseId, LocalDate date) {
        return markAttendance(studentId, courseId, date, "PRESENT");
    }

    public Attendance markStudentAbsent(String studentId, String courseId, LocalDate date) {
        return markAttendance(studentId, courseId, date, "ABSENT");
    }

    private Attendance markAttendance(String studentId, String courseId, LocalDate date, String status) {
        // Check if attendance already exists
        List<Attendance> existing = attendanceRepository.findByStudentStudentIdAndCourseCourseCodeAndDate(
                studentId, courseId, date);

        if (!existing.isEmpty()) {
            // Update existing record
            Attendance attendance = existing.get(0);
            attendance.setStatus(status);
            attendance.setMarkedAt(java.time.LocalDateTime.now());
            return attendanceRepository.save(attendance);
        }

        // Create new attendance record
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        Optional<Course> courseOpt = courseRepository.findByCourseCode(courseId);

        if (studentOpt.isPresent() && courseOpt.isPresent()) {
            Attendance attendance = new Attendance();

            // Set student info
            Attendance.StudentInfo studentInfo = new Attendance.StudentInfo();
            Student student = studentOpt.get();
            studentInfo.setStudentId(student.getStudentId());
            studentInfo.setFirstName(student.getUser().getFirstName());
            studentInfo.setLastName(student.getUser().getLastName());
            studentInfo.setEmail(student.getUser().getEmail());
            studentInfo.setGrade(student.getGrade());
            studentInfo.setSection(student.getSection());
            attendance.setStudent(studentInfo);

            // Set course info
            Attendance.CourseInfo courseInfo = new Attendance.CourseInfo();
            Course course = courseOpt.get();
            courseInfo.setCourseCode(course.getCourseCode());
            courseInfo.setCourseName(course.getCourseName());
            courseInfo.setDepartment(course.getDepartment());
            attendance.setCourse(courseInfo);

            attendance.setDate(date);
            attendance.setStatus(status);
            attendance.setMarkedAt(java.time.LocalDateTime.now());

            return attendanceRepository.save(attendance);
        }

        throw new RuntimeException("Student or Course not found");
    }

    public Map<String, Object> getAttendanceTrends(String studentId, String courseId,
            LocalDate startDate, LocalDate endDate) {

        List<Attendance> attendanceRecords;

        // Set default date range if not provided (last 30 days)
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        // Filter based on provided parameters
        if (studentId != null && courseId != null) {
            attendanceRecords = attendanceRepository.findByStudentStudentIdAndCourseCourseCodeAndDateBetween(
                    studentId, courseId, startDate, endDate);
        } else if (studentId != null) {
            attendanceRecords = getStudentAttendanceByDateRange(studentId, startDate, endDate);
        } else if (courseId != null) {
            attendanceRecords = attendanceRepository.findByCourseCourseCodeAndDateBetween(
                    courseId, startDate, endDate);
        } else {
            attendanceRecords = getAttendanceByDateRange(startDate, endDate);
        }

        Map<String, Object> trends = new HashMap<>();
        trends.put("totalRecords", attendanceRecords.size());
        trends.put("dateRange", Map.of("start", startDate, "end", endDate));

        // Weekly trends
        Map<String, Double> weeklyTrends = new HashMap<>();
        Map<Integer, List<Attendance>> weeklyData = attendanceRecords.stream()
                .collect(Collectors.groupingBy(a -> a.getDate().getDayOfYear() / 7));

        for (Map.Entry<Integer, List<Attendance>> entry : weeklyData.entrySet()) {
            List<Attendance> weekData = entry.getValue();
            long presentCount = weekData.stream()
                    .filter(a -> "PRESENT".equals(a.getStatus())).count();
            double weekRate = weekData.isEmpty() ? 0.0 : (double) presentCount / weekData.size() * 100;
            weeklyTrends.put("Week " + entry.getKey(), weekRate);
        }
        trends.put("weeklyTrends", weeklyTrends);

        // Overall trend calculation
        long totalPresent = attendanceRecords.stream()
                .filter(a -> "PRESENT".equals(a.getStatus())).count();
        double overallRate = attendanceRecords.isEmpty() ? 0.0 :
                (double) totalPresent / attendanceRecords.size() * 100;
        trends.put("overallAttendanceRate", overallRate);

        return trends;
    }
}
