package com.harken.graphql.server.repositories.report;

import com.harken.graphql.server.domain.report.Report;
import com.harken.graphql.server.repositories.BasicMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReportRepository extends BasicMongoRepository<Report> {

    public ReportRepository(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public List<Report> findAllReports() {
        return findAll(Report.class);
    }
    public Report findById(ObjectId id) {
        return findById(id, Report.class);
    }

    public Optional<List<Report>> findReportsByUser(ObjectId userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return findAllByQuery(query, Report.class);
    }

    public Optional<List<Report>> findReports(List<String> reportIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(reportIds));
        return findAllByQuery(query, Report.class);
    }

    /**
     * @deprecated See ReportPagingRepository
     * @param query
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public Page<Report> search(String query, int page, int size, String sort) {
        LOG.info("search: page= " + (page - 1) + ",size=" +size);

        Pageable pageable = PageRequest.of((page - 1), size);
        Query pageableQuery = new Query().with(pageable);
        //add filter Criteria.where()

        List<Report> reports = mongoTemplate.find(pageableQuery, Report.class, "reports");
        LOG.info("reports paged = " + reports.size());

        Page<Report> reportPage = PageableExecutionUtils.getPage(
                reports,
                pageable,
                () -> mongoTemplate.count(pageableQuery, Report.class));
        return reportPage;
    }

    public Report saveReport(Report report) {
        return save(report);
    }

    public Report updateReport(Report report) {
        Query query = new Query(Criteria.where("id")
                .is(report.getId()));

        Update update = new Update()
                .set("measure", report.getMeasure());

        Report updatedReport = mongoTemplate.findAndModify(query, update,
                new FindAndModifyOptions().returnNew(true).upsert(false),
                Report.class);

        return updatedReport;
    }
}
