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
public class Level {
    private int totalLevel;
    private int count;
    private List<String> reports;
}
