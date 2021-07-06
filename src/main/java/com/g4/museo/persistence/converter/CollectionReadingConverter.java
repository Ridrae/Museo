package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.Collection;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class CollectionReadingConverter implements Converter<Row, Collection> {
    @Override
    public Collection convert(Row source) {
        return Collection.builder()
                .idcollection(source.get("idcollection", Integer.class))
                .collectionName(source.get("name", String.class))
                .build();
    }
}
