package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserR2dbcDao extends R2dbcRepository<User, String> {
}
