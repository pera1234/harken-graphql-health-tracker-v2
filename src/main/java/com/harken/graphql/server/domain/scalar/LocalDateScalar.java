package com.harken.graphql.server.domain.scalar;

import graphql.language.StringValue;
import graphql.schema.*;

import java.time.ZonedDateTime;

public class LocalDateScalar extends GraphQLScalarType {

    public LocalDateScalar() {
        super("DateTime", "DateTime type",
                new Coercing<ZonedDateTime, String>() {

                    @Override
                    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof ZonedDateTime) {
                            return ((ZonedDateTime)dataFetcherResult).toString();
                        }
                        return null;
                    }

                    @Override
                    public ZonedDateTime parseValue(Object input) throws CoercingParseValueException {
                        if (input instanceof  String) {
                            return ZonedDateTime.parse((String)input);
                        }
                        return null;
                    }

                    @Override
                    public ZonedDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                        if (!(input instanceof StringValue)) {
                            return null;
                        }

                        return ZonedDateTime.parse(((StringValue)input).getValue());
                    }
                });
    }

}
