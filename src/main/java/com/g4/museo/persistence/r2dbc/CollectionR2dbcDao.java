package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.Collection;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CollectionR2dbcDao extends R2dbcRepository<Collection, Integer> {
    @Query("SELECT idcollection FROM collection WHERE name = :collectionName")
    Mono<Integer> findCollectionIdByName(@Param("collectionName") String name);
}
