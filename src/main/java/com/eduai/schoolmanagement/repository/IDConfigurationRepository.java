package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.IDConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDConfigurationRepository extends MongoRepository<IDConfiguration, String> {

    Optional<IDConfiguration> findByIdTypeAndActive(String idType, Boolean active);

    List<IDConfiguration> findByActiveTrue();

    List<IDConfiguration> findByActiveFalse();

    @Query("{ 'idType': ?0 }")
    Optional<IDConfiguration> findByIdType(String idType);

    @Query("{ 'prefix': ?0, 'active': true }")
    Optional<IDConfiguration> findByPrefixAndActive(String prefix);

    boolean existsByIdType(String idType);

    boolean existsByPrefix(String prefix);

    @Query("{ 'format': { $regex: ?0, $options: 'i' } }")
    List<IDConfiguration> findByFormatContaining(String formatPattern);
}
