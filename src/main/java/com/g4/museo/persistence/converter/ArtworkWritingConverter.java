package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.Artwork;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

@WritingConverter
public class ArtworkWritingConverter implements Converter<Artwork, OutboundRow> {
    @Override
    public OutboundRow convert(Artwork artwork) {
        OutboundRow row = new OutboundRow();
        if(artwork.getIdartwork() != null) {
            row.put("idartwork", Parameter.from(artwork.getIdartwork()));
        }
        row.put("name", Parameter.from(artwork.getName()));
        row.put("author", Parameter.from(artwork.getAuthor()));
        final ByteBuffer[] picture = new ByteBuffer[1];
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
        row.put("description", Parameter.from(artwork.getDesc()));
        return row;
    }
}
