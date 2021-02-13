package com.harken.graphql.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    private ObjectId id;
    private String firstname;
    private String lastname;
    private String avatar;
    private String email;
    private String password;
    private Role role;
    private Address address;
    private List<String> reports;
}
