package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.ArtworkBorrow;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkBorrowR2dbcDao extends R2dbcRepository<ArtworkBorrow, Integer> {
}
