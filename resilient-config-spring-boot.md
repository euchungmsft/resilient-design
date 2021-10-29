# resilient-config-spring-boot
Resiliency configuration guide for Spring Boot

## Intro

Spring Boot/Cloud configuration guide to achieve resiliency using patterns against faults and trasient problems from Azure Service components

## Azure Cache for Redis

Because per connection cost is higher with Azure Cache for Redis, connection reuse's recommendable by using connection pool. With Lettuce, Redis connections are designed to be long-lived and thread-safe, and if the connection is lost will reconnect until close() is called. Pending commands that have not timed out will be (re)sent after successful reconnection. You don't have to care of reconnection to Redis yourself

> Note: Jedis is another option to implement connection pool but Lettuce supports synchronous, asynchronous, and even reactive interfaces. Lettuce is in multi-threaded, event-based model that uses pipelining as a matter of course. Even when you use it synchronously, it’s asynchronous underneath

Consider these to add to your application.yaml (.properties)

```javascript
spring.redis.timeout
spring.redis.connect-timeout
spring.redis.lettuce.pool.max-active
spring.redis.lettuce.pool.max-idle
spring.redis.lettuce.pool.min-idle
spring.redis.lettuce.pool.max-wait
spring.redis.lettuce.cluster.refresh.adaptive
spring.redis.lettuce.cluster.refresh.dynamic-refresh-sources
spring.redis.lettuce.cluster.refresh.period
```

See [this](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#application-properties.data.spring.redis.timeout) for further details of each properties

You can find Azure best pratices from [here](
https://docs.microsoft.com/en-us/azure/azure-cache-for-redis/cache-best-practices-connection) on Connection resilience

### Timeouts

Timeouts are important. Default timeouts are way too long, which casues delays in problem detection and failovers and as well as unexpected system faults

spring.redis.timeout is read timeout (default 30 min), no bigger than 5 min. spring.redis.connect-timeout less than 1 sec

Another timeout you need to be careful with is command timeout. It depends on the data loaded in Redis but try not to be waiting forever. It can be set from the code like below

```Java
LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
  .commandTimeout(redisCommandTimeout) 
```

### Lettuce Pool

Try to align with your SKU of Redis with max-active, max-idle, min-idle. When the size of the pool is not big enough, it waits and blocks all connection pool requests for max-wait. Deafault is infinit (- 1ms). Try not to leave default

### Lettuce Cluster Refresh

These are very important especially when these cluster topology changes in your Redis cluster which could be caused by maintenance events and as well as actual faults in the back-ends. Try to set both of adaptive and dynamic-refresh-sources are true so that it follows the tology changes actively and passively. period needs to be set as short as 30 sec or less than that

## Cosmos DB (SDK v4)

You can configure the connection mode in the client builder using the directMode() or gatewayMode() methods, SDK default is direct. Azure Cosmos DB requests are made over REST(over HTTPS) when you use gateway mode. Direct mode is REST over TCP. See [this](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-sdk-connection-modes) for further details on connection mode

https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-sdk-connection-modes

Retry settings with RetryOptions of azure-documentdb

```Java
ConnectionPolicy policy = new ConnectionPolicy();
RetryOptions retryOptions = new RetryOptions();
retryOptions.setMaxRetryAttemptsOnThrottledRequests(0);
policy.setRetryOptions(retryOptions);
policy.setConnectionMode(cfg.getConnectionMode());
policy.setMaxPoolSize(cfg.getMaxConnectionPoolSize());

DocumentClient client = new DocumentClient(cfg.getServiceEndpoint(), cfg.getMasterKey(),
  policy, cfg.getConsistencyLevel());
```

2 attributes with getters
```
setMaxRetryWaitTimeInSeconds(int maxRetryWaitTimeInSeconds)
setMaxRetryAttemptsOnThrottledRequests(int maxRetryAttemptsOnThrottledRequests)
```

See [this](https://docs.microsoft.com/en-us/java/api/com.microsoft.azure.documentdb.retryoptions?view=azure-java-stable) for further details

Cosmos DB throttles the client, it returns an HTTP 429 error. Check the status code in the CosmosException class. 

RetryOptions oftenly retrieved from ConnectionPolicy by calling `getRetryOptions()`

CosmosDB SDK embeds connection pools on ConnectionPolicy

```
getMaxPoolSize()
setMaxPoolSize(int maxPoolSize)
```

And finally be careful of timeouts, you can find all timeouts from ConnectionPolicy, 

3 timeouts with getters
```
setRequestTimeout(int requestTimeout)
setDirectRequestTimeout(int directRequestTimeout)
setIdleConnectionTimeout(int idleConnectionTimeout)
```
request timeout here is read timeout while waiting for the response. 

Because Cosmos DB's optimized to its service, default values are not too long or not too short but for both of setRequestTimeout and setDirectRequestTimeout you need to try to set either of these accordingly to normal range of response time for the queries

See [this](https://docs.microsoft.com/en-us/java/api/com.microsoft.azure.documentdb.connectionpolicy?view=azure-java-stable) for further details

## For All Azure Databases with Hikari Connection Pool

Consider these to add to your application.yaml (.properties)

```javascript
spring.datasource.hikari.allow-pool-suspension
spring.datasource.hikari.auto-commit
spring.datasource.hikari.connection-init-sql
spring.datasource.hikari.connection-test-query
spring.datasource.hikari.connection-timeout
spring.datasource.hikari.health-check-properties
spring.datasource.hikari.health-check-registry
spring.datasource.hikari.idle-timeout
spring.datasource.hikari.initialization-fail-timeout
spring.datasource.hikari.isolate-internal-queries
spring.datasource.hikari.keepalive-time
spring.datasource.hikari.leak-detection-threshold
spring.datasource.hikari.login-timeout
spring.datasource.hikari.max-lifetime
spring.datasource.hikari.maximum-pool-size
spring.datasource.hikari.minimum-idle
```

[Here](https://github.com/brettwooldridge/HikariCP) you can find details of attributes

All settings on timeouts, pools are important for better resiliency of your application. Try not to skip connection-init-sql, connection-test-query, it proactively runs these queries for connection health checks and as well as connection liveness. Even when JDBC driver doesn't support timeouts and reconnections natively

## For All Azure Databases with DBCP2

Consider these to add to your application.yaml (.properties)

```javascript
spring.datasource.dbcp2.connection-init-sqls
spring.datasource.dbcp2.default-auto-commit
spring.datasource.dbcp2.default-query-timeout
spring.datasource.dbcp2.fast-fail-validation
spring.datasource.dbcp2.initial-size
spring.datasource.dbcp2.login-timeout
spring.datasource.dbcp2.max-conn-lifetime-millis
spring.datasource.dbcp2.max-idle
spring.datasource.dbcp2.max-open-prepared-statements
spring.datasource.dbcp2.max-total
spring.datasource.dbcp2.max-wait-millis
spring.datasource.dbcp2.min-evictable-idle-time-millis
spring.datasource.dbcp2.min-idle
spring.datasource.dbcp2.num-tests-per-eviction-run
spring.datasource.dbcp2.pool-prepared-statements
spring.datasource.dbcp2.remove-abandoned-on-borrow
spring.datasource.dbcp2.remove-abandoned-on-maintenance
spring.datasource.dbcp2.remove-abandoned-timeout
spring.datasource.dbcp2.soft-min-evictable-idle-time-millis
spring.datasource.dbcp2.test-on-borrow
spring.datasource.dbcp2.test-on-create
spring.datasource.dbcp2.test-on-return
spring.datasource.dbcp2.test-while-idle
spring.datasource.dbcp2.time-between-eviction-runs-millis
spring.datasource.dbcp2.validation-query
spring.datasource.dbcp2.validation-query-timeout
```

[Here](https://commons.apache.org/proper/commons-dbcp/configuration.html) you can find details of attributes

Careful with all settings on timeouts, pools for better resiliency of your application. Try not to skip connection-init-sqls, validation-query, it proactively runs these queries for connection health checks and as well as connection liveness. Even when JDBC driver doesn't support timeouts and reconnections natively

### Working with ORM(Object Relation Mappers) with Connection Pools

With Hibernate, it provides configuration attributes for both of Hikari and DBCP. Try to set `hibernate.connection.provider_class` with Hikari `com.zaxxer.hikari.hibernate.HikariConnectionProvider` or DBCP `org.hibernate.connection.DBCPConnectionProvider` and add above mentioned attributes accordingly

With MyBatis, all datasource with connection settings are in spring boot config (application.yaml/.properties) and you can explicitly load the datasources ono your purpose

<to-be-developed more>

## Next Topics

- [Resilient Config](REAMD.md)
- [Resilient Config for SpringBoot](resilient-config-spring-boot.md)
- [Resilient Config for Generic Java](resilient-config-generic-java.md)
- [Resilient Config for Python](resilient-config-python.md)
- [Resilient Config for Node.js](resilient-config-nodejs.md)
- [Resilient Config for Golang](resilient-config-golang.md)
