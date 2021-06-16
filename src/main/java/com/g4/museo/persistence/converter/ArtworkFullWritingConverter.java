package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.ArtworkFull;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.ByteBuffer;

@WritingConverter
public class ArtworkFullWritingConverter implements Converter<ArtworkFull, OutboundRow> {
    @Override
    public OutboundRow convert(ArtworkFull artwork) {
        var row = new OutboundRow();
        if(artwork.getIdartwork() != null) {
            row.put("idartwork", Parameter.from(artwork.getIdartwork()));
        }
        row.put("name", Parameter.from(artwork.getName()));
        row.put("author", Parameter.from(artwork.getAuthor()));
        final var picture = new ByteBuffer[1];
        Subscriber<ByteBuffer> bytes = new Subscriber<ByteBuffer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBuffer byteBuffer) {
                picture[0] = byteBuffer;
            }

            @Override
            public void onError(Throwable throwable) {
                ErrorWindowFactory.create(new Exception(throwable));
            }

            @Override
            public void onComplete() {
                row.put("picture", Parameter.from(picture[0].array()));
                artwork.getPicture().discard();
            }
        } ;
        artwork.getPicture().stream().subscribe(bytes);
        row.put("date", Parameter.from(artwork.getDate()));
        row.put("certified", Parameter.from(artwork.isCertified()));
        if(artwork.getStoredLocation()!=null){
            row.put("stored_location", Parameter.from(artwork.getStoredLocation()));
        }
        if(artwork.getCollectionID()!=null){
            row.put("collection_id", Parameter.from(artwork.getCollectionID()));
        }
        row.put("state_id", Parameter.from(artwork.getStateID()));
        row.put("borrowed", Parameter.from(artwork.isBorrowed()));
        if(artwork.getDesc()!=null){
            row.put("description", Parameter.from(artwork.getDesc()));
        }
        row.put("last_updated", Parameter.from(SecurityContextHolder.getContext().getAuthentication().getName()));
        row.put("idowner", Parameter.from(artwork.getIdowner()));
        if(artwork.getDateBorrowed()!=null){
            row.put("date_borrowed", Parameter.from(artwork.getDateBorrowed()));
        }
        if(artwork.getReturnDate()!=null){
            row.put("date_return", Parameter.from(artwork.getReturnDate()));
        }
        row.put("is_stored", Parameter.from(artwork.isStored()));
        row.put("is_long_term", Parameter.from(artwork.isLongTerm()));
        row.put("width", Parameter.from(artwork.getWidth()));
        row.put("height", Parameter.from(artwork.getHeight()));
        row.put("perimeter", Parameter.from(artwork.getPerimeter()));
        if(artwork.getInsuranceNumber()!=null){
            row.put("insurance_number", Parameter.from(artwork.getInsuranceNumber()));
        }
        if(artwork.getMaterial()!=null){
            row.put("material", Parameter.from(artwork.getMaterial()));
        }
        if(artwork.getTechnic()!=null){
            row.put("technic", Parameter.from(artwork.getTechnic()));
        }
        row.put("type", Parameter.from(artwork.getType()));
        row.put("is_restored", Parameter.from(artwork.isRestored()));
        return row;
    }
}
