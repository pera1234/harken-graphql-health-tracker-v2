package com.harken.graphql.server.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

public class ApplicationGraphQLExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public DataFetcherExceptionHandlerResult onException(DataFetcherExceptionHandlerParameters handlerParameters) {
        GraphQLError exception = (GraphQLError) handlerParameters.getException();

        GraphQLError error = GraphqlErrorBuilder
                .newError()
                .message(exception.getMessage())
                .extensions(exception.getExtensions())
                .build();

        return DataFetcherExceptionHandlerResult
                .newResult()
                .error(error)
                .build();
    }
}