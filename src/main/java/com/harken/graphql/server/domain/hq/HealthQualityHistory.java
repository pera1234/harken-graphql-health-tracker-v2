package com.harken.graphql.server.domain.hq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthQualityHistory {

    private ObjectId city;
    private List<HistoryPoint> histories = new ArrayList<>();
}
