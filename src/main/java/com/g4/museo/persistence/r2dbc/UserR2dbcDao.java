package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserR2dbcDao extends R2dbcRepository<User, String> {
    @Modifying
    @Query("INSERT INTO users (username, password, enabled) VALUES (:username, :password, :enabled)")
    Mono<Void> newUser(@Param("username") String username, @Param("password") String password, @Param("enabled") boolean enabled);

    @Modifying
    @Query("INSERT INTO authorities (username, authority) VALUES (:username, :authority)")
    Mono<Void> newAuthority(@Param("username") String username, @Param("authority") String authority);
}
