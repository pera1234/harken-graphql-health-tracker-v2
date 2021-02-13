package com.harken.graphql.server.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public interface MongoCrudRepository<E> {

    List<E> findAll(Class<E> model);
    Optional<List<E>> findAllByQuery(Query query, Class<E> model);
    E findById(ObjectId id, Class<E> model);
}
