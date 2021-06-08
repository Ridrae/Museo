package com.g4.museo.persistence.converter;

import com.g4.museo.persistence.dto.ArtworkDetails;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

@WritingConverter
public class ArtworkDetailsWritingConverter implements Converter<ArtworkDetails, OutboundRow> {
    @Override
    public OutboundRow convert(ArtworkDetails artworkDetails) {
        var row = new OutboundRow();
        if(artworkDetails.getIddetails() != null) {
            row.put("iddetails", Parameter.from(artworkDetails.getIddetails()));
        }
        row.put("idartwork", Parameter.from(artworkDetails.getIdartwork()));
        row.put("width", Parameter.from(artworkDetails.getWidth()));
        row.put("height", Parameter.from(artworkDetails.getHeight()));
        row.put("perimeter", Parameter.from(artworkDetails.getPerimeter()));
        if(artworkDetails.getInsuranceNumber()!=null){
            row.put("insurance_number", Parameter.from(artworkDetails.getInsuranceNumber()));
        }
        if(artworkDetails.getMaterial()!=null){
            row.put("material", Parameter.from(artworkDetails.getMaterial()));
        }
        if(artworkDetails.getTechnic()!=null){
            row.put("technic", Parameter.from(artworkDetails.getTechnic()));
        }
        row.put("type", Parameter.from(artworkDetails.getType()));
        row.put("is_restored", Parameter.from(artworkDetails.isRestored()));
        return row;
    }
}
