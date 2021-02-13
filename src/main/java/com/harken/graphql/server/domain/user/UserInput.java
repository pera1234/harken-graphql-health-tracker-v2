package com.harken.graphql.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private AddressInput addressInput;
}
