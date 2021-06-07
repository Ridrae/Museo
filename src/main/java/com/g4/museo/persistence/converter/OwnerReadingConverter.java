package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.Owner;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;

public class OwnerReadingConverter implements Converter<Row, Owner> {
    @Override
    public Owner convert(Row source) {
        return Owner.builder()
                .ownerID(source.get("idowner", Integer.class))
                .firstname(source.get("firstname", String.class))
                .lastname(source.get("lastname", String.class))
                .orga(source.get("organisation", String.class))
                .adress(source.get("adress", String.class))
                .build();
    }
}
