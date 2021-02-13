package com.harken.graphql.server.domain.hq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryPoint {
    private long measureDate;
    private int level;
    private List<String> reports;
}
