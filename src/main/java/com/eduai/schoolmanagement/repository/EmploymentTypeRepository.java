package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.EmploymentType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentTypeRepository extends MongoRepository<EmploymentType, String> {

    Optional<EmploymentType> findByTypeCode(String typeCode);

    Optional<EmploymentType> findByTypeCodeAndActive(String typeCode, Boolean active);

    List<EmploymentType> findByActiveTrue();

    List<EmploymentType> findByActiveTrueOrderByDisplayOrder();

    List<EmploymentType> findByActiveTrueOrderByTypeName();

    List<EmploymentType> findByEligibleForBenefitsAndActiveTrue(Boolean eligibleForBenefits);

    List<EmploymentType> findByRequiresContractAndActiveTrue(Boolean requiresContract);

    @Query("{ 'minHoursPerWeek': { $lte: ?0 }, 'maxHoursPerWeek': { $gte: ?0 }, 'active': true }")
    List<EmploymentType> findByHoursPerWeekRange(Integer hoursPerWeek);

    boolean existsByTypeCode(String typeCode);

    boolean existsByTypeCodeAndActive(String typeCode, Boolean active);

    long countByActiveTrue();

    @Query("{ 'typeCode': 'FULL_TIME', 'active': true }")
    Optional<EmploymentType> findFullTimeType();

    @Query("{ 'typeCode': 'PART_TIME', 'active': true }")
    Optional<EmploymentType> findPartTimeType();

    @Query("{ 'typeCode': 'CONTRACT', 'active': true }")
    Optional<EmploymentType> findContractType();

    @Query("{ 'typeCode': 'SUBSTITUTE', 'active': true }")
    Optional<EmploymentType> findSubstituteType();
}
