//package com.harken.graphql.server.config;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.dataloader.CacheMap;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.CompletableFuture;
//
//@Component
//public final class DataLoaderCacheMap<V> implements CacheMap<Object, CompletableFuture<V>> {
//
//    private final Cache<Object, CompletableFuture<V>> cache;
//
//    public DataLoaderCacheMap(Integer cacheSize) {
//        this.cache = Caffeine.newBuilder().maximumSize(cacheSize).build();
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        return cache.asMap().containsKey(key);
//    }
//
//    @Override
//    public CompletableFuture<V> get(Object key) {
//        return cache.getIfPresent(key);
//    }
//
//    @Override
//    public CacheMap<Object, CompletableFuture<V>> set(Object key, CompletableFuture<V> value) {
//        cache.put(key, value);
//        return this;
//    }
//
//    @Override
//    public CacheMap<Object, CompletableFuture<V>> delete(Object key) {
//        cache.invalidate(key);
//        return this;
//    }
//
//    @Override
//    public CacheMap<Object, CompletableFuture<V>> clear() {
//        cache.invalidateAll();
//        return this;
//    }
//}