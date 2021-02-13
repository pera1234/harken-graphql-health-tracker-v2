package com.harken.graphql.server.domain.report;

import com.harken.graphql.server.domain.paging.FilterModel;
import com.harken.graphql.server.domain.paging.SortModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * React query using Apollo client with variables for the pageable values:
    const GET_REPORTS = gql`
            query Reports($page: Int!, $size: Int!, $query: String, $sort: String) {
            reports(
            reportPageInput: {
            query: $query
            size: $size
            page: $page
            sort: $sort
            }
            ) {
            totalElements
            totalPages
            page
            content {
            id
            city {
            city

            }
            user {
            avatar
            }
            measure {
            measureDate
            level
            device
            pollutant
            pinLocation {
            latitude
            longitude
            }
            }
            }
            }
            }
            `;
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPageInput {

    private FilterModel filter;
    private int page;
    private int size;
    private SortModel sort;
}
