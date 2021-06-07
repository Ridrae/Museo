package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.ArtworkState;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StateReadingConverter implements Converter<Row, ArtworkState> {
    @Override
    public ArtworkState convert(Row source) {
        return ArtworkState.builder()
                .stateID(source.get("idstate", Integer.class))
                .stateName(source.get("state_name", String.class))
                .build();
    }
}
