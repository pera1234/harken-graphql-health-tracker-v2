package com.harken.graphql.server.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BasicMongoRepository<E> implements MongoCrudRepository<E> {

    protected final MongoTemplate mongoTemplate;

    public E findOne(Query query, Class<E> model) {
        return mongoTemplate.findOne(query, model);
    }

    @Override
    public List<E> findAll(Class<E> model) {
        return mongoTemplate.findAll(model);
    }

    @Override
    public Optional<List<E>> findAllByQuery(Query query, Class<E> model) {
        List<E> result = mongoTemplate.find(query, model);
        return Optional.ofNullable(result);
    }

    @Override
    public E findById(ObjectId id, Class<E> model) {
        return mongoTemplate.findById(id, model);
    }

    public E save(E entity) {
        return mongoTemplate.save(entity);
    }
}
