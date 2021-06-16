package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.*;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import io.r2dbc.spi.Blob;
import io.r2dbc.spi.Row;
import javafx.scene.image.Image;
import org.apache.commons.lang3.BooleanUtils;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.time.LocalDate;

@SuppressWarnings("java:S1192")
@ReadingConverter
public class ArtworkFullReadingConverter implements Converter<Row, ArtworkFull> {
    @Override
    public ArtworkFull convert(Row source){

        ArtworkFull.ArtworkFullBuilder artworkBuilder = ArtworkFull.builder()
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
                .lastUpdatedBy(source.get("last_updated", String.class))
                .idowner(source.get("idowner", Integer.class))
                .dateBorrowed(source.get("date_borrowed", LocalDate.class))
                .returnDate(source.get("date_return", LocalDate.class))
                .stored(BooleanUtils.toBoolean(source.get("is_stored", Byte.class)))
                .longTerm(BooleanUtils.toBoolean(source.get("is_long_term", Byte.class)))
                .width(source.get("width", String.class))
                .height(source.get("height", String.class))
                .perimeter(source.get("perimeter", String.class))
                .insuranceNumber(source.get("insurance_number", String.class))
                .material(source.get("material", String.class))
                .technic(source.get("technic", String.class))
                .type(source.get("type", String.class))
                .collectionName(source.get("collection_name", String.class))
                .ownerFirstname(source.get("owner_firstname", String.class))
                .ownerLastname(source.get("owner_lastname", String.class))
                .ownerOrga(source.get("owner_orga", String.class))
                .ownerAddress(source.get("owner_adress", String.class))
                .stateName(source.get("state_name", String.class));

        if(source.get("is_restored", Byte.class) != null){
            artworkBuilder.restored(BooleanUtils.toBoolean(source.get("is_restored", Byte.class)));
        }

        Blob picture = source.get("picture", Blob.class);
        final var image = new byte[1][1];
        Subscriber<ByteBuffer> bytes = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBuffer byteBuffer) {
                image[0] = byteBuffer.array();
            }

            @Override
            public void onError(Throwable throwable) {
                ErrorWindowFactory.create(new Exception(throwable));
            }

            @Override
            public void onComplete() {
                //No operation necessary upon completion
            }
        };
        picture.stream().subscribe(bytes);
        artworkBuilder.image(new Image(new ByteArrayInputStream(image[0])));

        return artworkBuilder.build();
    }
}
