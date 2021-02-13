package com.harken.graphql.server.domain.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPage {

    private long totalElements;
    private int totalPages;
    private int page;
    private List<Report> content;
}
