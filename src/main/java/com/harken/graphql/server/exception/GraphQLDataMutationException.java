package com.harken.graphql.server.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphQLDataMutationException extends RuntimeException implements GraphQLError {

    private final int errorCode;

    public GraphQLDataMutationException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> errorMap = new LinkedHashMap<>();

        errorMap.put("errorCode", this.errorCode);
        errorMap.put("errorMessage", this.getMessage());
        return errorMap;
    }
}