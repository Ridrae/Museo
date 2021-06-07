package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.ArtworkFullDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArtworkFullR2dbcDao extends R2dbcRepository<ArtworkFullDTO, Integer> {
    @Query("SELECT a.idartwork, a.name, a.picture, a.author, a.date, a.certified, a.stored_location, a.collection_id, a.state_id, a.borrowed, a.description, " +
            "c.name AS collection_name, " +
            "s.state_name, " +
            "b.date_borrowed, b.date_return, b.is_stored, b.is_long_term, " +
            "o.idowner, o.firstname AS owner_firstname, o.lastname AS owner_lastname, o.organisation AS owner_orga, o.adress AS owner_adress, " +
            "d.width, d.height, d.perimeter, d.insurance_number, d.material, d.technic, d.type, d.is_restored " +
            "FROM  artwork AS a " +
            "LEFT JOIN collection AS c ON a.collection_id = c.idcollection " +
            "JOIN artwork_state AS s ON a.state_id = s.idstate " +
            "JOIN artwork_borrow AS b ON a.idartwork = b.idartwork " +
            "JOIN owner AS o ON b.idowner = o.idowner " +
            "JOIN artwork_details AS d ON a.idartwork = d.idartwork")
    Flux<ArtworkFullDTO> findAllArtworks();
}
