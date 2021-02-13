package com.harken.graphql.server.domain.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.harken.graphql.server.domain.measure.Measure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Report {

    private ObjectId id;
    private ObjectId userId;
    private ObjectId cityId;
    private Measure measure;
}
