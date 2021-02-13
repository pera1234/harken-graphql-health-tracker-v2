package com.harken.graphql.server.domain.measure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasureInput {

    private ZonedDateTime measureDate;
    private DiabetesIndicator diabetesIndicator;
    private PinLocation pinLocation;
    private String device;
    private int level;
}
