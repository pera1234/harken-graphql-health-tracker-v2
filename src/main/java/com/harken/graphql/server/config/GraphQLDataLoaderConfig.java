//package com.harken.graphql.server.config;
//
//import com.harken.graphql.server.domain.report.Report;
//import com.harken.graphql.server.domain.user.User;
//import com.harken.graphql.server.resolvers.report.ReportResolver;
//import com.harken.graphql.server.resolvers.user.UserResolver;
//import org.bson.types.ObjectId;
//import org.dataloader.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//import java.util.stream.Collectors;
//
//@Configuration
//public class GraphQLDataLoaderConfig {
//
//    private UserResolver userResolver;
//    private ReportResolver reportResolver;
//
//    @Bean
//    public DataLoaderRegistry dataLoaderRegistry() {
//
//        final DataLoaderRegistry registry = new DataLoaderRegistry();
//        registry.register("", getHomeByIdDataLoader());
//
//        return registry;
//    }
//
//    private DataLoader<Object, List<Report>> getCustomerDataLoader() {
//        MappedBatchLoader<Object, List<Report>> customerMappedBatchLoader = reportIds -> CompletableFuture.supplyAsync(() -> {
//            List<Report> reports = reportResolver.reportsIn(reportIds);
//            Map<Object, List<Report>> groupByAccountId = reports.stream().collect(Collectors.groupingBy(report -> report.getId()));
//            return groupByAccountId;
//        });
//
//        return DataLoader.newMappedDataLoader(customerMappedBatchLoader);
//    }
//
//    BatchLoader<ObjectId, User> userBatchLoader = new BatchLoader<ObjectId, User>() {
//
//        @Override
//        public CompletionStage<List<User>> load(List<ObjectId> keys) {
//
////            @Override
////            public CompletionStage<User> load(ObjectId userId) {
////                return CompletableFuture.supplyAsync(() -> {
////                    return userResolver.userById(userId);
////                });
////            }
//
//            return null;
//        }
//    };
//
//    DataLoader<Long, User> userLoader = DataLoader.newDataLoader(userBatchLoader);
//
//    private DataLoader<ObjectId, Home> getHomeByIdDataLoader() {
//        final CacheMap cacheMap = new CaffeineCacheMap<>(500);
//
//        final DataLoaderOptions options = DataLoaderOptions.newOptions().setCacheMap(cacheMap);
//
////        final RedisCacheMap<String, ObjectId, Home> homeByObjectIdCacheMap = new RedisCacheMap<>(homeByObjectIdCacheMapRepository);
////        homeByObjectIdCacheMap.setRedisKey(HOMES_BY_OBJECT_IDS);
////        final DataLoaderOptions homeByObjectIdOptions = DataLoaderOptionsrOptions.newOptions().setCacheMap(homeByObjectIdCacheMap);
//
//        return DataLoader.newDataLoader(homeByIdBatchLoader(), homeByObjectIdOptions);
//    }
//
//    private BatchLoader<ObjectId, Home> homeByIdBatchLoader() {
//        return keys -> CompletableFuture.supplyAsync(() -> homeService.findAllHomesById(keys, true));
//    }
//
//}
