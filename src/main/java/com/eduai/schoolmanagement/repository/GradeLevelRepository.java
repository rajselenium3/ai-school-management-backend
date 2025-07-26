package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.GradeLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeLevelRepository extends MongoRepository<GradeLevel, String> {

    Optional<GradeLevel> findByGradeCode(String gradeCode);

    Optional<GradeLevel> findByGradeCodeAndActive(String gradeCode, Boolean active);

    List<GradeLevel> findByActiveTrue();

    List<GradeLevel> findByActiveTrueOrderByDisplayOrder();

    List<GradeLevel> findByGradeLevelBetweenAndActiveTrue(Integer minLevel, Integer maxLevel);

    @Query("{ 'minimumAge': { $lte: ?0 }, 'maximumAge': { $gte: ?0 }, 'active': true }")
    List<GradeLevel> findByAgeRangeAndActive(Integer age);

    boolean existsByGradeCode(String gradeCode);

    boolean existsByGradeCodeAndActive(String gradeCode, Boolean active);

    @Query("{ 'gradeLevel': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<GradeLevel> findElementaryGrades(Integer minLevel, Integer maxLevel);

    @Query("{ 'gradeLevel': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<GradeLevel> findHighSchoolGrades(Integer minLevel, Integer maxLevel);

    long countByActiveTrue();
}
