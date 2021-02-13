package com.harken.graphql.server.repositories.hq;

import com.harken.graphql.server.domain.hq.HealthQuality;
import com.harken.graphql.server.domain.measure.DiabetesIndicator;
import com.harken.graphql.server.domain.report.Report;
import com.harken.graphql.server.repositories.BasicMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class HealthQualityRepository extends BasicMongoRepository<HealthQuality> {

    public HealthQualityRepository(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public List<HealthQuality> findAllHealthQualitySummaries() {
        return findAll(HealthQuality.class);
    }
    public HealthQuality findById(ObjectId id) {
        return findById(id, HealthQuality.class);
    }

    public HealthQuality findHealthQuality(ObjectId city, ZonedDateTime measureDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("city").is(city).and("measureDate").is(measureDate.toLocalDate()));
        return findOne(query, HealthQuality.class);
    }
    public List<HealthQuality> findHealthQuality(ObjectId city) {
        Query query = new Query();
        query.addCriteria(Criteria.where("city").is(city));
        return findAllByQuery(query, HealthQuality.class).orElse(null);
    }

    public HealthQuality saveHealthQualitySummary(HealthQuality healthQuality) {
        return save(healthQuality);
    }

    public HealthQuality updateHealthQualitySummary(Report report) {
        DiabetesIndicator diabetesIndicator = report.getMeasure().getDiabetesIndicator();

        Query query = new Query(Criteria.where("city").is(report.getCityId())
                .and("measureDate")
                .is(report.getMeasure().getMeasureDate().toLocalDate()));
        Update args = new Update();
        args.set("measureDate", report.getMeasure().getMeasureDate().toLocalDate())
                .addToSet(DiabetesIndicator.CHOLESTEROL == diabetesIndicator ? "cholesterolLevel.reports" : "bloodsugarLevel.reports",report.getId())
                .inc(DiabetesIndicator.CHOLESTEROL == diabetesIndicator ? "cholesterolLevel.count" : "bloodsugarLevel.count", 1)
                .inc(DiabetesIndicator.CHOLESTEROL == diabetesIndicator ? "cholesterolLevel.totalLevel" : "bloodsugarLevel.totalLevel", report.getMeasure().getLevel());

        HealthQuality healthQuality = mongoTemplate.findAndModify(
                query,
                args,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                HealthQuality.class);
        return healthQuality;
    }

    public HealthQuality updateReports(ObjectId aqId, ObjectId reportId) {
        Query query = new Query(Criteria.where("id").is(aqId));
        Update args = new Update();
        args.addToSet("reports",reportId);

        return mongoTemplate.findAndModify(
                query,
                args,
                FindAndModifyOptions.options().upsert(false).returnNew(true),
                HealthQuality.class);
    }
}
