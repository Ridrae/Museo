package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.*;
import io.r2dbc.spi.Blob;
import io.r2dbc.spi.Row;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDate;

@ReadingConverter
public class ArtworkFullReadingConverter implements Converter<Row, ArtworkFullDTO> {
    @Override
    public ArtworkFullDTO convert(Row source){
        Artwork artwork = Artwork.builder()
                .idartwork(source.get("idartwork", Integer.class))
                .name(source.get("name", String.class))
                .author(source.get("author", String.class))
                .picture(source.get("picture", Blob.class))
                .date(source.get("date", LocalDate.class))
                .certified(BooleanUtils.toBoolean(source.get("certified", Byte.class)))
                .storedLocation(source.get("stored_location", String.class))
                .collectionID(source.get("collection_id", Integer.class))
                .stateID(source.get("state_id", Integer.class))
                .borrowed(BooleanUtils.toBoolean(source.get("borrowed", Byte.class)))
                .desc(source.get("description", String.class))
                .build();

        ArtworkBorrow artworkBorrow = ArtworkBorrow.builder()
                .idartwork(source.get("idartwork", Integer.class))
                .idowner(source.get("idowner", Integer.class))
                .dateBorrowed(source.get("date_borrowed", LocalDate.class))
                .returnDate(source.get("date_return", LocalDate.class))
                .stored(BooleanUtils.toBoolean(source.get("is_stored", Byte.class)))
                .longTerm(BooleanUtils.toBoolean(source.get("is_long_term", Byte.class)))
                .build();

        ArtworkDetails artworkDetails = ArtworkDetails.builder()
                .idartwork(source.get("idartwork", Integer.class))
                .width(source.get("width", String.class))
                .height(source.get("height", String.class))
                .perimeter(source.get("perimeter", String.class))
                .insuranceNumber(source.get("insurance_number", String.class))
                .material(source.get("material", String.class))
                .technic(source.get("technic", String.class))
                .type(source.get("type", String.class))
                .restored(BooleanUtils.toBoolean(source.get("is_restored", Byte.class)))
                .build();

        Collection collection;
        try{
            collection = Collection.builder()
                    .collectionID(source.get("collection_id", Integer.class))
                    .collectionName(source.get("collection_name", String.class))
                    .build();
        } catch (NullPointerException e){
            collection = Collection.builder()
                    .collectionID(null)
                    .collectionName(null)
                    .build();
        }

        Owner owner = Owner.builder()
                .ownerID(source.get("idowner", Integer.class))
                .firstname(source.get("owner_firstname", String.class))
                .lastname(source.get("owner_lastname", String.class))
                .orga(source.get("owner_orga", String.class))
                .adress(source.get("owner_adress", String.class))
                .build();

        ArtworkState artworkState = ArtworkState.builder()
                .stateID(source.get("state_id", Integer.class))
                .stateName(source.get("state_name", String.class))
                .build();

        return ArtworkFullDTO.builder()
                .artwork(artwork)
                .artworkBorrow(artworkBorrow)
                .artworkDetails(artworkDetails)
                .collection(collection)
                .owner(owner)
                .artworkState(artworkState)
                .build();
    }
}
