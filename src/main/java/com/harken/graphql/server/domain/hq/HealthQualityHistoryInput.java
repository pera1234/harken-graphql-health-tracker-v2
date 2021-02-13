package com.harken.graphql.server.domain.hq;

import com.harken.graphql.server.domain.measure.DiabetesIndicator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthQualityHistoryInput {

    private ObjectId city;
    private DiabetesIndicator diabetesIndicator;
}
