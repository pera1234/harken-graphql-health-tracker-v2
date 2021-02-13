package com.harken.graphql.server.domain.measure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinLocationInput {
    private long latitude;
    private long longitude;
}
