package com.harken.graphql.server.resolvers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harken.graphql.server.domain.hq.*;
import com.harken.graphql.server.domain.measure.DiabetesIndicator;
import com.harken.graphql.server.domain.report.Report;
import com.harken.graphql.server.domain.report.ReportInput;
import com.harken.graphql.server.domain.report.ReportPage;
import com.harken.graphql.server.domain.report.ReportPageInput;
import com.harken.graphql.server.domain.user.Address;
import com.harken.graphql.server.domain.user.User;
import com.harken.graphql.server.domain.user.UserInput;
import com.harken.graphql.server.repositories.hq.HealthQualityRepository;
import com.harken.graphql.server.repositories.city.CityRepository;
import com.harken.graphql.server.repositories.report.ReportPagingRepository;
import com.harken.graphql.server.repositories.report.ReportRepository;
import com.harken.graphql.server.repositories.user.UserRepository;
import graphql.schema.DataFetcher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class GraphQLDataFetchers {

    private UserRepository userRepository;
    private ReportRepository reportRepository;
    private ReportPagingRepository reportPagingRepository;
    private CityRepository cityRepository;
    private HealthQualityRepository healthQualityRepository;

    private Mutation mutation;
    private ObjectMapper objectMapper;


    public DataFetcher getUserByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String userId = dataFetchingEnvironment.getArgument("id");
            return userRepository.findById(new ObjectId(userId));
        };
    }

    public DataFetcher getUsersDataFetcher() {
        return dataFetchingEnvironment -> {
            return userRepository.findAllUsers();
        };
    }

    public DataFetcher getUserByEmailDataFetcher() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email");
            return userRepository.findOne(email);
        };
    }

    public DataFetcher getReportByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String reportId = dataFetchingEnvironment.getArgument("id");
            return reportRepository.findById(new ObjectId(reportId));
        };
    }

    public DataFetcher getReportByUserDataFetcher() {
        return dataFetchingEnvironment -> {
            String userId = dataFetchingEnvironment.getArgument("userId");
            return reportRepository.findReportsByUser(new ObjectId(userId));
        };
    }

    public DataFetcher getReportsInDataFetcher() {
        return dataFetchingEnvironment -> {
            List<String> reportIds = dataFetchingEnvironment.getArgument("reportIds");
            return reportRepository.findReports(reportIds);
        };
    }

    public DataFetcher getReportsDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("reportPageInput");
            ReportPageInput reportPageInput = objectMapper.convertValue(input, ReportPageInput.class);

            Page<Report> reportsPage = reportPagingRepository.findAll(reportPageInput);
            ReportPage reportPage = ReportPage.builder().totalElements(reportsPage.getTotalElements())
                    .totalPages(reportsPage.getTotalPages())
                    .page(reportsPage.getNumber())
                    .content(reportsPage.getContent()).build();

            return reportPage;
        };
    }

    public DataFetcher getCityByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String cityId = dataFetchingEnvironment.getArgument("id");
            return cityRepository.findById(new ObjectId(cityId));
        };
    }

    public DataFetcher getCityDataFetcher() {
        return dataFetchingEnvironment -> {
            String city = dataFetchingEnvironment.getArgument("city");
            return cityRepository.findOne(city);
        };
    }

    public DataFetcher getCitiesDataFetcher() {
        return dataFetchingEnvironment -> {
            return cityRepository.findAllCities();
        };
    }

    public DataFetcher getAQByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String aqId = dataFetchingEnvironment.getArgument("id");
            return healthQualityRepository.findById(new ObjectId(aqId));
        };
    }

    public DataFetcher getAQSummaryDataFetcher() {
        return dataFetchingEnvironment -> {
            String cityId = dataFetchingEnvironment.getArgument("city");
            ZonedDateTime measureDate = dataFetchingEnvironment.getArgument("measureDate");
            return healthQualityRepository.findHealthQuality(new ObjectId(cityId), measureDate);
        };
    }

    public DataFetcher getAirQualitiesDataFetcher() {
        return dataFetchingEnvironment -> {
            return healthQualityRepository.findAllHealthQualitySummaries();
        };
    }

    public DataFetcher getAQHistoryDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("aqHistoryInput");
            HealthQualityHistoryInput healthQualityHistoryInput = objectMapper.convertValue(input, HealthQualityHistoryInput.class);
            List<HealthQuality> airQualities = healthQualityRepository.findHealthQuality(healthQualityHistoryInput.getCity());

            return airQualityHistory(healthQualityHistoryInput, airQualities);
        };
    }

    public HealthQualityHistory airQualityHistory(HealthQualityHistoryInput healthQualityHistoryInput, List<HealthQuality> airQualities) {
        List<HistoryPoint> historySeries = new ArrayList<>();
        airQualities.forEach(hq -> {
            long measureDateEpoch = hq.getMeasureDate().toInstant().toEpochMilli();
            int dailyLevel = 0;
            List<String> reports = new ArrayList<>();

            if (DiabetesIndicator.CHOLESTEROL == healthQualityHistoryInput.getDiabetesIndicator()) {
                if (hq.getCholesterolLevel() != null) {
                    dailyLevel = Math.round(hq.getCholesterolLevel().getTotalLevel() / hq.getCholesterolLevel().getCount());
                    reports.addAll(hq.getCholesterolLevel().getReports());
                }
            } else {
                if (hq.getBloodsugarLevel() != null) {
                    dailyLevel = Math.round(hq.getBloodsugarLevel().getTotalLevel() / hq.getBloodsugarLevel().getCount());
                    reports.addAll(hq.getBloodsugarLevel().getReports());
                }
            }

            historySeries.add(HistoryPoint.builder().measureDate(measureDateEpoch).level(dailyLevel).reports(reports).build());
        });
        return HealthQualityHistory.builder().city(healthQualityHistoryInput.getCity()).histories(historySeries).build();
    }

    public DataFetcher createUserMutationDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("userInput");
            UserInput userInput = objectMapper.convertValue(input, UserInput.class);
            return mutation.createUser(userInput, dataFetchingEnvironment);
        };
    }

    public DataFetcher updateUserMutationDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("userUpdateInput");
            UserInput userInput = objectMapper.convertValue(input, UserInput.class);
            String userId = dataFetchingEnvironment.getArgument("id");

            return mutation.updateUser(new ObjectId(userId), userInput);
        };
    }

    public DataFetcher createReportMutationDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("reportInput");
            ReportInput reportInput = objectMapper.convertValue(input, ReportInput.class);
            return mutation.createReport(reportInput, dataFetchingEnvironment);
        };
    }

    public DataFetcher updateReportMutationDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("reportUpdateInput");
            ReportInput reportInput = objectMapper.convertValue(input, ReportInput.class);
            String reportId = dataFetchingEnvironment.getArgument("id");

            return mutation.updateReport(new ObjectId(reportId), reportInput);
        };
    }

    public DataFetcher createReportsMutationDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("reports");
            List<ReportInput> reportInputs = Arrays.asList(objectMapper.convertValue(input, ReportInput[].class));
            return mutation.createReports(reportInputs, dataFetchingEnvironment);
        };
    }

    public DataFetcher createAQSummaryMutationDataFetcher() {
        return dataFetchingEnvironment -> {
            Object input = dataFetchingEnvironment.getArgument("airQualityInput");
            HealthQualityInput healthQualityInput = objectMapper.convertValue(input, HealthQualityInput.class);
            return mutation.createAQSummary(healthQualityInput, dataFetchingEnvironment);
        };
    }

    public DataFetcher getAddressCityDataFetcher() {
        return dataFetchingEnvironment -> {
            Address address = dataFetchingEnvironment.getSource();
            ObjectId cityId = address.getCity();

            return cityRepository.findById(cityId);
        };
    }

    public DataFetcher getUserReportsDataFetcher() {
        return dataFetchingEnvironment -> {
            User user = dataFetchingEnvironment.getSource();
            List<String> reportIds = user.getReports();
            return reportRepository.findReports(reportIds).orElse(null);
        };
    }

    public DataFetcher getAQReportsForLevelDataFetcher() {
        return dataFetchingEnvironment -> {
            Level level = dataFetchingEnvironment.getSource();
            return reportRepository.findReports(level.getReports());
        };
    }

    public DataFetcher getReportCityDataFetcher() {
        return dataFetchingEnvironment -> {
            Report report = dataFetchingEnvironment.getSource();
            ObjectId cityId = report.getCityId();

            return cityRepository.findById(cityId);
        };
    }

    public DataFetcher getReportUserDataFetcher() {
        return dataFetchingEnvironment -> {
            Report report = dataFetchingEnvironment.getSource();
            ObjectId userId = report.getUserId();
            return userRepository.findById(userId);
        };
    }

}