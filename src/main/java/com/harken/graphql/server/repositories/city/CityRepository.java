package com.harken.graphql.server.repositories.city;

import com.harken.graphql.server.domain.city.City;
import com.harken.graphql.server.repositories.BasicMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CityRepository extends BasicMongoRepository<City> {

    public CityRepository(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public List<City> findAllCities() {
        return findAll(City.class);
    }
    public City findById(ObjectId id) {
        return findById(id, City.class);
    }

    public City findOne(String city) {
        Query query = new Query();
        query.addCriteria(Criteria.where("city").is(city));
        return findOne(query, City.class);
    }
}
