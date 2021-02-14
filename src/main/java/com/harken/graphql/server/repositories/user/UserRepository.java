package com.harken.graphql.server.repositories.user;

import com.harken.graphql.server.domain.user.User;
import com.harken.graphql.server.exception.GraphQLDataMutationException;
import com.harken.graphql.server.exception.GraphQLQueryException;
import com.harken.graphql.server.repositories.BasicMongoRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository extends BasicMongoRepository<User> {

    public UserRepository(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    public List<User> findAllUsers() {
        return findAll(User.class);
    }

    public User findById(ObjectId id) {
        User user = findById(id, User.class);
        if (user == null) {
            throw new GraphQLQueryException(400, "User not found", "userId");
        }
        return user;
    }

    public User findOne(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        User user = findOne(query, User.class);
        if (user == null) {
            throw new GraphQLQueryException(400, "User not found", "userId");
        }
        return user;
    }

    public User saveUser(User user) {
        return save(user);
    }

    public User updateUser(User user) {
        User updatedUser = null;
        Query query = new Query(Criteria.where("id").is(user.getId()));
        Update update = new Update()
                .set("firstname", user.getFirstname())
                .set("lastname", user.getLastname())
                .set("email", user.getEmail())
                .set("address.street", user.getAddress().getStreet())
                .set("address.postcode", user.getAddress().getPostcode());

        try {
            updatedUser = mongoTemplate.findAndModify(query, update,
                    new FindAndModifyOptions().returnNew(true).upsert(false),
                    User.class);
        } catch (Exception e) {
            throw new GraphQLDataMutationException(500, "User could not be updated: " + e.getMessage());
        }
        return updatedUser;
    }

    public User updateReports(ObjectId userId, ObjectId reportId) {
        Query query = new Query(Criteria.where("id").is(userId));
        Update args = new Update();
        args.addToSet("reports",reportId);

        return mongoTemplate.findAndModify(
                query,
                args,
                FindAndModifyOptions.options().upsert(false).returnNew(true),
                User.class);
    }

}
