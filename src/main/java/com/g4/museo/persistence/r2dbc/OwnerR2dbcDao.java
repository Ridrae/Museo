package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.Owner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OwnerR2dbcDao extends R2dbcRepository<Owner, Integer> {
    @Query("SELECT idowner FROM owner WHERE organisation = :orgaName")
    Mono<Integer> findOwnerIdByOrga(@Param("orgaName") String name);

    @Query("SELECT idowner FROM owner WHERE firstname = :firstname AND lastname = :lastname")
    Mono<Integer> findOwnerIdByName(@Param("firstname") String firstname, @Param("lastname") String lastname);
}
