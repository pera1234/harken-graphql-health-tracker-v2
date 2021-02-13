package com.harken.graphql.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "com.harken.graphql.server")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    /**
     * For local standalone single node/cluster
     * Run MongoDB with --replSet <name> parameter
     * Start 'mongo' shell and run rs.initiate()
     * @param dbFactory
     * @return
     */
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    public static List<Converter<?, ?>> getConvertersToRegister() {
        return List.of(ZonedDateTimeToDateConverter.INSTANCE, DateToZonedDateTimeConverter.INSTANCE);
    }

    private enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
        INSTANCE;
        public @Override Date convert(ZonedDateTime source) {
            return Date.from(source.toInstant());
        }
    }
    private enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        INSTANCE;
        public @Override ZonedDateTime convert(Date source) {
            return source.toInstant().atZone(ZoneOffset.UTC);
        }
    }

    @Override
    protected String getDatabaseName() {
        return "harken-react-app-data";
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(getConvertersToRegister());
    }

}
