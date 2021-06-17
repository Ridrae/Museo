package com.g4.museo.persistence.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Builder
@Getter
@Setter
@ToString
public class Collection {
    @Id
    private Integer idcollection;
    private String collectionName;
}
