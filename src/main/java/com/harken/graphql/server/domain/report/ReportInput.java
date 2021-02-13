package com.harken.graphql.server.domain.report;

import com.harken.graphql.server.domain.measure.MeasureInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportInput {

    private ObjectId userId;
    private ObjectId cityId;
    private MeasureInput measureInput;
}
