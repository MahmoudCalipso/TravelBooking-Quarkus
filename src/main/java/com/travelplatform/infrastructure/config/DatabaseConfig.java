package com.travelplatform.infrastructure.config;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration for the Travel Platform application.
 * This class provides configuration for Hibernate ORM, connection pooling,
 * and database-related utilities.
 */
@ApplicationScoped
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * Get the Hibernate SessionFactory from the EntityManagerFactory.
     * This is useful for accessing Hibernate-specific features.
     *
     * @param entityManagerFactory The EntityManagerFactory
     * @return The SessionFactory
     */
    @Produces
    @Singleton
    @Unremovable
    public SessionFactory sessionFactory(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(SessionFactory.class);
    }

    /**
     * Get database statistics if enabled.
     *
     * @param sessionFactory The SessionFactory
     * @return Database statistics or null if not enabled
     */
    public Statistics getStatistics(SessionFactory sessionFactory) {
        if (sessionFactory.getStatistics().isStatisticsEnabled()) {
            return sessionFactory.getStatistics();
        }
        return null;
    }

    /**
     * Get database connection information.
     *
     * @param dataSource The DataSource
     * @return Map containing database information
     */
    public Map<String, String> getDatabaseInfo(DataSource dataSource) {
        Map<String, String> info = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            info.put("databaseProductName", metaData.getDatabaseProductName());
            info.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            info.put("driverName", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            info.put("url", metaData.getURL());
            info.put("userName", metaData.getUserName());
            
        } catch (SQLException e) {
            log.error("Failed to get database information", e);
        }
        
        return info;
    }

    /**
     * Test database connection.
     *
     * @param dataSource The DataSource
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            log.error("Database connection test failed", e);
            return false;
        }
    }

    /**
     * Get connection pool statistics.
     *
     * @param dataSource The DataSource
     * @return Map containing pool statistics
     */
    public Map<String, Object> getPoolStatistics(DataSource dataSource) {
        Map<String, Object> stats = new HashMap<>();
        
        // Note: This is a placeholder. In production, you would use
        // HikariCP's specific API to get detailed pool statistics
        stats.put("status", "active");
        stats.put("note", "Detailed pool statistics require HikariCP-specific API");
        
        return stats;
    }

    /**
     * Clear all caches (first-level and second-level).
     *
     * @param entityManager The EntityManager
     * @param sessionFactory The SessionFactory
     */
    public void clearAllCaches(EntityManager entityManager, SessionFactory sessionFactory) {
        // Clear first-level cache
        entityManager.clear();
        
        // Clear second-level cache if enabled
        if (sessionFactory.getCache() != null) {
            sessionFactory.getCache().evictAll();
        }
        
        log.info("All caches cleared");
    }

    /**
     * Evict entity from second-level cache.
     *
     * @param sessionFactory The SessionFactory
     * @param entityClass The entity class to evict
     */
    public void evictEntityFromCache(SessionFactory sessionFactory, Class<?> entityClass) {
        if (sessionFactory.getCache() != null) {
            sessionFactory.getCache().evict(entityClass);
            log.debug("Evicted {} from second-level cache", entityClass.getSimpleName());
        }
    }

    /**
     * Evict all entities from second-level cache.
     *
     * @param sessionFactory The SessionFactory
     */
    public void evictAllEntitiesFromCache(SessionFactory sessionFactory) {
        if (sessionFactory.getCache() != null) {
            sessionFactory.getCache().evictAll();
            log.info("Evicted all entities from second-level cache");
        }
    }

    /**
     * Get cache statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing cache statistics
     */
    public Map<String, Object> getCacheStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        if (sessionFactory.getCache() != null) {
            stats.put("containsCollection", sessionFactory.getCache().containsCollection(null));
            stats.put("containsEntity", sessionFactory.getCache().containsEntity(null));
            stats.put("containsQuery", sessionFactory.getCache().containsQuery(null));
        }
        
        return stats;
    }

    /**
     * Get Hibernate configuration properties.
     *
     * @param sessionFactory The SessionFactory
     * @return Map of configuration properties
     */
    public Map<String, Object> getHibernateProperties(SessionFactory sessionFactory) {
        Map<String, Object> properties = new HashMap<>();
        
        // Get some key properties
        properties.put("dialect", sessionFactory.getDialect().toString());
        properties.put("defaultBatchFetchSize", sessionFactory.getSessionFactoryOptions().getDefaultBatchFetchSize());
        properties.put("defaultBatchSize", sessionFactory.getSessionFactoryOptions().getJdbcBatchSize());
        properties.put("fetchSize", sessionFactory.getSessionFactoryOptions().getDefaultFetchSize());
        
        return properties;
    }

    /**
     * Get query plan cache statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing query plan cache statistics
     */
    public Map<String, Object> getQueryPlanCacheStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("queryPlanCacheHitCount", statistics.getQueryPlanCacheHitCount());
            stats.put("queryPlanCacheMissCount", statistics.getQueryPlanCacheMissCount());
            stats.put("queryPlanCacheSize", statistics.getQueryPlanCacheSize());
        }
        
        return stats;
    }

    /**
     * Get entity statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing entity statistics
     */
    public Map<String, Object> getEntityStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("entityDeleteCount", statistics.getEntityDeleteCount());
            stats.put("entityInsertCount", statistics.getEntityInsertCount());
            stats.put("entityLoadCount", statistics.getEntityLoadCount());
            stats.put("entityFetchCount", statistics.getEntityFetchCount());
            stats.put("entityUpdateCount", statistics.getEntityUpdateCount());
        }
        
        return stats;
    }

    /**
     * Get collection statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing collection statistics
     */
    public Map<String, Object> getCollectionStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("collectionRemoveCount", statistics.getCollectionRemoveCount());
            stats.put("collectionRecreateCount", statistics.getCollectionRecreateCount());
            stats.put("collectionUpdateCount", statistics.getCollectionUpdateCount());
            stats.put("collectionLoadCount", statistics.getCollectionLoadCount());
            stats.put("collectionFetchCount", statistics.getCollectionFetchCount());
        }
        
        return stats;
    }

    /**
     * Get query execution statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing query execution statistics
     */
    public Map<String, Object> getQueryStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("queryExecutionCount", statistics.getQueryExecutionCount());
            stats.put("queryExecutionMaxTime", statistics.getQueryExecutionMaxTime());
            stats.put("queryExecutionMaxTimeQueryString", statistics.getQueryExecutionMaxTimeQueryString());
            stats.put("queryCacheHitCount", statistics.getQueryCacheHitCount());
            stats.put("queryCacheMissCount", statistics.getQueryCacheMissCount());
            stats.put("queryCachePutCount", statistics.getQueryCachePutCount());
        }
        
        return stats;
    }

    /**
     * Get transaction statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing transaction statistics
     */
    public Map<String, Object> getTransactionStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("transactionCount", statistics.getTransactionCount());
            stats.put("successfulTransactionCount", statistics.getSuccessfulTransactionCount());
            stats.put("optimisticFailureCount", statistics.getOptimisticFailureCount());
            stats.put("flushCount", statistics.getFlushCount());
            stats.put("connectCount", statistics.getConnectCount());
        }
        
        return stats;
    }

    /**
     * Get connection statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing connection statistics
     */
    public Map<String, Object> getConnectionStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("closeStatementCount", statistics.getCloseStatementCount());
            stats.put("prepareStatementCount", statistics.getPrepareStatementCount());
            stats.put("sessionOpenCount", statistics.getSessionOpenCount());
            stats.put("sessionCloseCount", statistics.getSessionCloseCount());
        }
        
        return stats;
    }

    /**
     * Get second-level cache statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing second-level cache statistics
     */
    public Map<String, Object> getSecondLevelCacheStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("secondLevelCacheHitCount", statistics.getSecondLevelCacheHitCount());
            stats.put("secondLevelCacheMissCount", statistics.getSecondLevelCacheMissCount());
            stats.put("secondLevelCachePutCount", statistics.getSecondLevelCachePutCount());
        }
        
        return stats;
    }

    /**
     * Get natural ID cache statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing natural ID cache statistics
     */
    public Map<String, Object> getNaturalIdCacheStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("naturalIdCacheHitCount", statistics.getNaturalIdCacheHitCount());
            stats.put("naturalIdCacheMissCount", statistics.getNaturalIdCacheMissCount());
            stats.put("naturalIdCachePutCount", statistics.getNaturalIdCachePutCount());
            stats.put("naturalIdQueryExecutionCount", statistics.getNaturalIdQueryExecutionCount());
        }
        
        return stats;
    }

    /**
     * Get update timestamp cache statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing update timestamp cache statistics
     */
    public Map<String, Object> getUpdateTimestampsCacheStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("updateTimestampsCacheHitCount", statistics.getUpdateTimestampsCacheHitCount());
            stats.put("updateTimestampsCacheMissCount", statistics.getUpdateTimestampsCacheMissCount());
            stats.put("updateTimestampsCachePutCount", statistics.getUpdateTimestampsCachePutCount());
        }
        
        return stats;
    }

    /**
     * Get query execution time statistics.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing query execution time statistics
     */
    public Map<String, Object> getQueryExecutionTimeStatistics(SessionFactory sessionFactory) {
        Map<String, Object> stats = new HashMap<>();
        
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics != null && statistics.isStatisticsEnabled()) {
            stats.put("queryExecutionMaxTime", statistics.getQueryExecutionMaxTime());
            stats.put("queryExecutionMaxTimeQueryString", statistics.getQueryExecutionMaxTimeQueryString());
        }
        
        return stats;
    }

    /**
     * Get all statistics combined.
     *
     * @param sessionFactory The SessionFactory
     * @return Map containing all statistics
     */
    public Map<String, Object> getAllStatistics(SessionFactory sessionFactory) {
        Map<String, Object> allStats = new HashMap<>();
        
        allStats.put("entity", getEntityStatistics(sessionFactory));
        allStats.put("collection", getCollectionStatistics(sessionFactory));
        allStats.put("query", getQueryStatistics(sessionFactory));
        allStats.put("transaction", getTransactionStatistics(sessionFactory));
        allStats.put("connection", getConnectionStatistics(sessionFactory));
        allStats.put("secondLevelCache", getSecondLevelCacheStatistics(sessionFactory));
        allStats.put("naturalIdCache", getNaturalIdCacheStatistics(sessionFactory));
        allStats.put("updateTimestampsCache", getUpdateTimestampsCacheStatistics(sessionFactory));
        allStats.put("queryPlanCache", getQueryPlanCacheStatistics(sessionFactory));
        allStats.put("queryExecutionTime", getQueryExecutionTimeStatistics(sessionFactory));
        
        return allStats;
    }
}
