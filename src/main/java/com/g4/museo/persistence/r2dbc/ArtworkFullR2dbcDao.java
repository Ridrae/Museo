package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.ArtworkFull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArtworkFullR2dbcDao extends R2dbcRepository<ArtworkFull, Integer> {
}
