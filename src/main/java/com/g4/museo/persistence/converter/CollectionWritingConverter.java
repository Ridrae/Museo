package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.Collection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

@WritingConverter
public class CollectionWritingConverter implements Converter<Collection, OutboundRow> {
    @Override
    public OutboundRow convert(Collection collection) {
        var row = new OutboundRow();
        if(collection.getIdcollection() != null) {
            row.put("idcollection", Parameter.from(collection.getIdcollection()));
        }
        row.put("name", Parameter.from(collection.getCollectionName()));
        return row;
    }
}
