package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Section;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends MongoRepository<Section, String> {

    Optional<Section> findBySectionCode(String sectionCode);

    Optional<Section> findBySectionCodeAndActive(String sectionCode, Boolean active);

    List<Section> findByActiveTrue();

    List<Section> findByActiveTrueOrderByDisplayOrder();

    List<Section> findByActiveTrueOrderBySectionCode();

    @Query("{ 'maxCapacity': { $gt: '$currentEnrollment' }, 'active': true }")
    List<Section> findAvailableSections();

    @Query("{ 'currentEnrollment': { $gte: '$maxCapacity' }, 'active': true }")
    List<Section> findFullSections();

    boolean existsBySectionCode(String sectionCode);

    boolean existsBySectionCodeAndActive(String sectionCode, Boolean active);

    @Query("{ 'currentEnrollment': { $lt: ?0 }, 'active': true }")
    List<Section> findSectionsWithCapacityLessThan(Integer capacity);

    long countByActiveTrue();

    @Query("{ 'active': true }")
    long getTotalCapacity();

    @Query("{ 'active': true }")
    long getTotalEnrollment();
}
