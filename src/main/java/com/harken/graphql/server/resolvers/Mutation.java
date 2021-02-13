package com.harken.graphql.server.resolvers;

import com.harken.graphql.server.domain.hq.HealthQuality;
import com.harken.graphql.server.domain.hq.HealthQualityInput;
import com.harken.graphql.server.domain.hq.Level;
import com.harken.graphql.server.domain.measure.Measure;
import com.harken.graphql.server.domain.report.Report;
import com.harken.graphql.server.domain.report.ReportInput;
import com.harken.graphql.server.domain.user.Address;
import com.harken.graphql.server.domain.user.User;
import com.harken.graphql.server.domain.user.UserInput;
import com.harken.graphql.server.repositories.hq.HealthQualityRepository;
import com.harken.graphql.server.repositories.report.ReportRepository;
import com.harken.graphql.server.repositories.user.UserRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class Mutation {
    private UserRepository userRepository;
    private ReportRepository reportRepository;
    private HealthQualityRepository healthQualityRepository;

    @Transactional
    public Report createReport(ReportInput reportInput, DataFetchingEnvironment environment) {
        Report report = reportRepository.saveReport(
                Report.builder()
                        .userId(reportInput.getUserId())
                        .cityId(reportInput.getCityId())
                        .measure(Measure.builder()
                                .measureDate(reportInput.getMeasureInput().getMeasureDate().withZoneSameInstant(ZoneId.of("UTC")))
                                .diabetesIndicator(reportInput.getMeasureInput().getDiabetesIndicator())
                                .level(reportInput.getMeasureInput().getLevel())
                                .device(reportInput.getMeasureInput().getDevice())
                                .pinLocation(reportInput.getMeasureInput().getPinLocation())
                                .build())
                        .build());

        LOG.info("Report created {}, for user {}", report.getId(), reportInput.getUserId());
        User user = userRepository.updateReports(reportInput.getUserId(), report.getId());

        healthQualityRepository.updateHealthQualitySummary(report);

        List<String> reportIds = Optional.ofNullable(user.getReports()).orElse(Collections.emptyList());
        LOG.info("Reports created {}", reportIds.size());

        return report;
    }

    /**
     * Update the report measure data
     * City cannot be update as this report is linked to the users home city
     * For additional cities the user must pay for an upgraded subscription: REPORTER_PLUS
     * @param id
     * @param reportInput
     * @return
     */
    public Report updateReport(ObjectId id, ReportInput reportInput) {
        return reportRepository.updateReport(
                Report.builder()
                        .id(id)
                        .measure(Measure.builder()
                                .measureDate(reportInput.getMeasureInput().getMeasureDate().withZoneSameInstant(ZoneId.of("UTC")))
                                .diabetesIndicator(reportInput.getMeasureInput().getDiabetesIndicator())
                                .level(reportInput.getMeasureInput().getLevel())
                                .device(reportInput.getMeasureInput().getDevice())
                                .pinLocation(reportInput.getMeasureInput().getPinLocation())
                                .build())
                        .build());
    }

    public User createUser(UserInput userInput, DataFetchingEnvironment environment) {
        return userRepository.saveUser(
                User.builder()
                        .email(userInput.getEmail())
                        .password(userInput.getPassword())
                        .role(userInput.getRole())
                        .address(Address.builder()
                                .street(userInput.getAddressInput().getStreet())
                                .city(userInput.getAddressInput().getCity())
                                .postcode(userInput.getAddressInput().getPostcode())
                                .build())
                        .build());
    }

    /**
     * Bulk insert - use updateMany
     * @param reportInputs
     * @param environment
     * @return
     */
    public String createReports(List<ReportInput> reportInputs, DataFetchingEnvironment environment) {
        try {
            reportInputs.forEach(report -> {
                createReport(report,environment);
            });
            return "Report bulk update completed";
        } catch (Exception e) {
            LOG.error("Unable to bulk update: ", e);
            return "Report bulk update failed";
        }
    }

    public User updateUser(ObjectId id, UserInput userInput) {
        return userRepository.updateUser(
                User.builder()
                        .id(id)
                        .firstname(userInput.getFirstname())
                        .lastname((userInput.getLastname()))
                        .email(userInput.getEmail())
                        .password(userInput.getPassword())
                        .address(Address.builder()
                                .street(userInput.getAddressInput().getStreet())
                                .postcode(userInput.getAddressInput().getPostcode())
                                .build())
                        .build());
    }

    public User updateUserRole(ObjectId id, UserInput userInput) {
        return userRepository.updateUser(User.builder()
                .id(id)
                .role(userInput.getRole())
                .build());
    }

    public HealthQuality createAQSummary(HealthQualityInput healthQualityInput, DataFetchingEnvironment environment) {
        LocalDate now = LocalDate.now();
        return healthQualityRepository.saveHealthQualitySummary(
                HealthQuality.builder()
                        .city(healthQualityInput.getCity())
                        .measureDate(now.atStartOfDay(ZoneId.of("UTC")))
                        .bloodsugarLevel(Level.builder().totalLevel(healthQualityInput.getBloodsugarLevel().getTotalLevel())
                                .count(healthQualityInput.getBloodsugarLevel().getCount()).build())
                        .cholesterolLevel(Level.builder().totalLevel(healthQualityInput.getCholesterolLevel().getTotalLevel())
                                .count(healthQualityInput.getCholesterolLevel().getCount()).build())
                .build());
    }

}
