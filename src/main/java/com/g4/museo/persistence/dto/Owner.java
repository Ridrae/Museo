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
public class Owner {
    @Id
    private int ownerID;
    private String firstname;
    private String lastname;
    private String organisation;
    private String adress;
}
