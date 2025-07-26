package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Semester;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends MongoRepository<Semester, String> {

    Optional<Semester> findBySemesterCode(String semesterCode);

    Optional<Semester> findBySemesterCodeAndActive(String semesterCode, Boolean active);

    List<Semester> findByActiveTrue();

    List<Semester> findByActiveTrueOrderByDisplayOrder();

    List<Semester> findByAcademicYear(Integer academicYear);

    List<Semester> findByAcademicYearAndActiveTrue(Integer academicYear);

    List<Semester> findBySemesterType(String semesterType);

    List<Semester> findBySemesterTypeAndActiveTrue(String semesterType);

    Optional<Semester> findByIsCurrentSemesterTrue();

    @Query("{ 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 }, 'active': true }")
    Optional<Semester> findActiveSemesterByDate(LocalDate date);

    @Query("{ 'registrationStartDate': { $lte: ?0 }, 'registrationEndDate': { $gte: ?0 }, 'active': true }")
    List<Semester> findSemestersWithOpenRegistration(LocalDate date);

    @Query("{ 'startDate': { $gt: ?0 }, 'active': true }")
    List<Semester> findUpcomingSemesters(LocalDate date);

    @Query("{ 'endDate': { $lt: ?0 }, 'active': true }")
    List<Semester> findCompletedSemesters(LocalDate date);

    boolean existsBySemesterCode(String semesterCode);

    boolean existsBySemesterCodeAndActive(String semesterCode, Boolean active);

    long countByActiveTrue();

    long countByAcademicYearAndActiveTrue(Integer academicYear);

    @Query("{ 'academicYear': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<Semester> findByAcademicYearRange(Integer startYear, Integer endYear);

    List<Semester> findByActiveTrueOrderByAcademicYearDescStartDateDesc();
}
