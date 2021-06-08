package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.ArtworkBorrow;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

@WritingConverter
public class ArtworkBorrowWritingConverter implements Converter<ArtworkBorrow, OutboundRow> {
    @Override
    public OutboundRow convert(ArtworkBorrow artworkBorrow) {
        var row = new OutboundRow();
        if(artworkBorrow.getIdborrow() != null) {
            row.put("idborrow", Parameter.from(artworkBorrow.getIdborrow()));
        }
        row.put("idartwork", Parameter.from(artworkBorrow.getIdartwork()));
        row.put("idowner", Parameter.from(artworkBorrow.getIdowner()));
        if(artworkBorrow.getDateBorrowed()!=null){
            row.put("date_borrowed", Parameter.from(artworkBorrow.getDateBorrowed()));
        }
        if(artworkBorrow.getReturnDate()!=null){
            row.put("date_return", Parameter.from(artworkBorrow.getReturnDate()));
        }
        row.put("is_stored", Parameter.from(artworkBorrow.isStored()));
        row.put("is_long_term", Parameter.from(artworkBorrow.isLongTerm()));
        return row;
    }
}
