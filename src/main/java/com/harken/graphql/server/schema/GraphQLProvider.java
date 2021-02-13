package com.harken.graphql.server.schema;

import com.harken.graphql.server.domain.scalar.LocalDateScalar;
import com.harken.graphql.server.resolvers.GraphQLDataFetchers;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {

    @Autowired
    private GraphQLDataFetchers graphQLDataFetchers;
    private GraphQL graphQL;

    @Value("classpath:graphql/**/*.graphqls")
    private Resource[] schemaResources;

    @PostConstruct
    public void init() throws IOException {
        final List<File> schemas = Arrays.stream(schemaResources).filter(Resource::isFile).map(resource -> {
            try {
                return resource.getFile();
            } catch (IOException ex) {
                throw new RuntimeException("Unable to load schema files");
            }
        }).collect(Collectors.toList());

        GraphQLSchema graphQLSchema = buildSchema(schemas);

        /**
         * Data loading WIP
         * There is a discrepancy in the documentation and the latest graphql-java library:
         *
         * DataLoaderRegistry registry = new DataLoaderRegistry();
         * registry.register("reports", reportsDataLoader);
         *
         * // Instrumentation and registry instantiation changed in graphql-java v10+
         * DataLoaderDispatcherInstrumentationOptions options = newOptions().includeStatistics(true);
         * DataLoaderDispatcherInstrumentation dispatcherInstrumentation = new DataLoaderDispatcherInstrumentation(options);
         * // Or
         * DataLoaderDispatcherInstrumentation dispatcherInstrumentation = new DataLoaderDispatcherInstrumentation(registry);
         *
         */

        // Async execution is actually the default
        // ExecutorServiceExecutionStrategy allows data fetchers to be executed asynchronously via an ExecutorService
        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .queryExecutionStrategy(new AsyncExecutionStrategy())
                .mutationExecutionStrategy(new AsyncSerialExecutionStrategy())
                .build();
    }

    private GraphQLSchema buildSchema(final List<File> schemas) {
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        for (final File schema:schemas) {
            typeRegistry.merge(schemaParser.parse(schema));
        }

        RuntimeWiring runtimeWiring = buildWiring();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(new LocalDateScalar())
                .type(newTypeWiring("Query")
                        .dataFetcher("users", graphQLDataFetchers.getUsersDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("userByEmail", graphQLDataFetchers.getUserByEmailDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("userById", graphQLDataFetchers.getUserByIdDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("reportById", graphQLDataFetchers.getReportByIdDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("reportsByUser", graphQLDataFetchers.getReportByUserDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("reportsIn", graphQLDataFetchers.getReportsInDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("cityById", graphQLDataFetchers.getCityByIdDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("city", graphQLDataFetchers.getCityDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("cities", graphQLDataFetchers.getCitiesDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("airQualityById", graphQLDataFetchers.getAQByIdDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("airQualitySummary", graphQLDataFetchers.getAQSummaryDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("airQualities", graphQLDataFetchers.getAirQualitiesDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("airQualityHistory", graphQLDataFetchers.getAQHistoryDataFetcher()))

                .type(newTypeWiring("Mutation")
                        .dataFetcher("createUser", graphQLDataFetchers.createUserMutationDataFetcher()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("updateUser", graphQLDataFetchers.updateUserMutationDataFetcher()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createReport", graphQLDataFetchers.createReportMutationDataFetcher()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("updateReport", graphQLDataFetchers.updateReportMutationDataFetcher()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createReports", graphQLDataFetchers.createReportsMutationDataFetcher()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createAQSummary", graphQLDataFetchers.createAQSummaryMutationDataFetcher()))

                .type(newTypeWiring("Address")
                        .dataFetcher("city", graphQLDataFetchers.getAddressCityDataFetcher()))
                .type(newTypeWiring("Level")
                        .dataFetcher("reports", graphQLDataFetchers.getAQReportsForLevelDataFetcher()))
                .type(newTypeWiring("User")
                        .dataFetcher("reports", graphQLDataFetchers.getUserReportsDataFetcher()))
                .type(newTypeWiring("Report")
                        .dataFetcher("city", graphQLDataFetchers.getReportCityDataFetcher()))
                .type(newTypeWiring("Report")
                        .dataFetcher("user", graphQLDataFetchers.getReportUserDataFetcher()))
                .type(newTypeWiring("Query")
                        .dataFetcher("reports", graphQLDataFetchers.getReportsDataFetcher()))
                .build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

}

