package com.harken.graphql.server.domain.hq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthQualityInput {

    private ObjectId city;
    private ZonedDateTime measureDate;
    private Level cholesterolLevel;
    private Level bloodsugarLevel;
}
