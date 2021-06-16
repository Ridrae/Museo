package com.g4.museo.persistence.r2dbc;

import com.g4.museo.persistence.dto.ArtworkFull;
import io.r2dbc.spi.Blob;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface ArtworkFullR2dbcDao extends R2dbcRepository<ArtworkFull, Integer> {
    @Modifying
    @Query("UPDATE artwork SET name=:name, author=:author, date=:date,certified=:certified, stored_location=:storedLocation, collection_id=:collectionId, state_id=:stateId, borrowed=:borrowed, description=:desc, last_updated=:lastUpdate WHERE idartwork=:artworkId")
    Mono<Void> updateArtwork(@Param("artworkId") Integer id, @Param("name") String name, @Param("author") String author, @Param("date") LocalDate date,
                             @Param("certified") boolean certified, @Param("storedLocation") String storedLocation, @Param("collectionId") Integer collectionId,
                             @Param("stateId") Integer stateId, @Param("borrowed") boolean borrowed, @Param("desc") String desc, @Param("lastUpdate") String lastUpdate);

    @Modifying
    @Query("UPDATE artwork SET picture=:picture WHERE idartwork=:artworkId")
    Mono<Void> updatePicture(@Param("artworkId") Integer id, @Param("picture") byte[] picture);

    @Modifying
    @Query("UPDATE artwork_details SET width=:width, height=:height, perimeter=:perimeter, insurance_number=:insurance, material=:material, technic=:technic, type=:type," +
            " is_restored=:restored WHERE idartwork=:artworkId")
    Mono<Void> updateDetails(@Param("artworkId") int id, @Param("width") String width, @Param("height") String height, @Param("perimeter") String perimeter,
                       @Param("insurance") String insurance, @Param("material") String material, @Param("technic") String technic, @Param("type") String type,
                       @Param("restored") boolean restored);

    @Modifying
    @Query("UPDATE artwork_borrow SET idowner=:ownerID, date_borrowed=:borrowDate, date_return=:returnDate, is_stored=:stored, is_long_term=:longTerm WHERE idartwork=:artworkId")
    Mono<Void> updateBorrow(@Param("artworkId") int id, @Param("ownerID") Integer ownerID , @Param("borrowDate") LocalDate borrowDate,
                      @Param("returnDate") LocalDate returnDate, @Param("stored") boolean stored, @Param("longTerm") boolean longTerm);
}
