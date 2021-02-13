package com.harken.graphql.server.repositories.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harken.graphql.server.domain.measure.Measure;
import com.harken.graphql.server.domain.report.Report;
import com.harken.graphql.server.domain.report.ReportPageInput;
import com.harken.graphql.server.repositories.city.CityRepository;
import com.harken.graphql.server.domain.paging.FilterModel;
import com.harken.graphql.server.domain.paging.SortModel;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.JsonPath.using;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@Slf4j
@Repository
public class ReportPagingRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private CityRepository cityRepository;

    public Page<Report> findAll(ReportPageInput reportPageInput){
        FilterModel filterInput = reportPageInput.getFilter();
        if (StringUtils.isNotBlank(filterInput.getColumnField())) {
            String filterColumn = filterInput.getColumnField();
            String filterValue = filterInput.getValue();
            String operator = filterInput.getOperatorValue();
            if (!"contains".equals(operator)) {
                LOG.warn("Default to contains filter query");
            }
        }
        String sortField = "measure.measureDate";
        Sort.Direction direction = Sort.Direction.DESC;
        SortModel sortInput = reportPageInput.getSort();

        if (sortInput != null) {
            direction = "asc".equals(sortInput.getSort()) ? Sort.Direction.ASC :
                    Sort.Direction.DESC;
            sortField = sortInput.getField(); //get full jsonpath


        }

        Pageable pageable = PageRequest.of((reportPageInput.getPage() - 1), reportPageInput.getSize());

        LookupOperation lookupCities = LookupOperation.newLookup()
                .from("cities")
                .localField("cityId")
                .foreignField("_id")
                .as("reportCity");

        LookupOperation lookupUsers = LookupOperation.newLookup()
                .from("users")
                .localField("userId")
                .foreignField("_id")
                .as("reportUser");

        UnwindOperation unwindOperation = unwind("reportCity");
        ProjectionOperation projectCity = Aggregation.project("reportCity.city");

        Criteria cityCriteria = Criteria.where("reportCity.city")
                .regex(Pattern.compile("", Pattern.CASE_INSENSITIVE));

        MatchOperation matchOperation = match(cityCriteria);
        SkipOperation skipOperation = Aggregation.skip((reportPageInput.getPage() - 1) * (reportPageInput.getSize()));
        LimitOperation limitOperation = Aggregation.limit(reportPageInput.getSize());

        SortOperation sortOperation = Aggregation.sort(direction, "reportCity.city");
        //keep this
//        SortOperation sortOperation2 = Aggregation.sort(direction, transformedSortField(sortField));

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("cityId").ne(null)),
                lookupUsers, lookupCities,
                matchOperation,
                sortOperation,
                skipOperation,
                limitOperation,
                Aggregation.project("userId", "cityId", "measure"));
                //NOTE: For correct pagination skip then limit
                //unwindOperation, projectCity useful for join with other collection

        Aggregation aggregationForTotalCount = Aggregation.newAggregation(
                match(Criteria.where("cityId").ne(null)),
                lookupCities, matchOperation);

        List<Report> reportsCount = mongoTemplate.aggregate(aggregationForTotalCount,
                mongoTemplate.getCollectionName(Report.class), Report.class)
                .getMappedResults();

        List<Report> reports = mongoTemplate.aggregate(aggregation,
                mongoTemplate.getCollectionName(Report.class), Report.class)
                .getMappedResults();

        return PageableExecutionUtils.getPage(
                reports,
                pageable,
                reportsCount::size);
    }

    private String transformedSortField(String sortField) {
        String result = "";
        try {
            String json = objectMapper.writeValueAsString( Report.builder().measure(Measure.builder().build()).build() );
            Map<String, Object> map = objectMapper.readValue(json, Map.class);

            result = getKey(map.entrySet(),"measureDate", new StringBuilder(""));
            System.out.println();
        }catch (Exception e) {
            LOG.warn("Sort field could not be transformed {}, unable to sort on [{}]", e.getMessage(), sortField);
        }

        return result;
    }

    private String getKey(Set<Map.Entry<String,Object>> entries, String field, StringBuilder result) {
        //		entries.forEach((it) -> {
        //			if (it.getKey().equals(field)){
        //				result.append(it.getKey());
        //			}
        //
        //			if (it.getValue() instanceof Map) {
        //				getKey(((Map<String, Object>) it.getValue()).entrySet(), field, result.append(it.getKey()).append("."));
        //			}
        //		});
        //		return result.toString();
        return "";
    }
}
