package com.g4.museo.persistence.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
public class ArtworkBorrow {
    @Id
    private Integer idborrow;
    private Integer idartwork;
    private Integer idowner;
    private LocalDate dateBorrowed;
    private LocalDate returnDate;
    private boolean stored;
    private boolean longTerm;
}
