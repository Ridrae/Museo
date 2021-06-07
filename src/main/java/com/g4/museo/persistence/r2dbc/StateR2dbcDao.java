package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.ArtworkState;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StateR2dbcDao extends R2dbcRepository<ArtworkState, Integer> {
    @Cacheable("states")
    @Query("SELECT * from artwork_state")
    Flux<ArtworkState> getAllStates();

    @Query("SELECT idstate FROM artwork_state WHERE state_name = :stateName")
    Mono<Integer> findStateIdByName(@Param("stateName") String name);
}