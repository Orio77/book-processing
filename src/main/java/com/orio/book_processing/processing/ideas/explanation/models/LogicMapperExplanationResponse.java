package com.orio.book_processing.processing.ideas.explanation.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogicMapperExplanationResponse {

    private String reasoning;
    private String explanation;
}
