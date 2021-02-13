package com.harken.graphql.server.domain.hq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "health-summaries")
public class HealthQuality {

    private ObjectId id;
    private ObjectId city;
    private ZonedDateTime measureDate;
    private Level cholesterolLevel;
    private Level bloodsugarLevel;
}
