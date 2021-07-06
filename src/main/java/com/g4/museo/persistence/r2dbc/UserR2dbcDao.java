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

    @Modifying
    @Query("DELETE FROM users WHERE username=:username")
    Mono<Void> deleteUser(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM authorities WHERE username=:username")
    Mono<Void> deleteAuthorities(@Param("username") String username);

    @Modifying
    @Query("UPDATE users SET username=:newUsername, password=:password WHERE username=:username")
    Mono<Void> updateUser(@Param("username") String username, @Param("newUsername") String newUsername, @Param("password") String password);

}
