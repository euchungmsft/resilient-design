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

See [this](https://docs.microsoft.com/en-us/java/api/com.microsoft.azure.documentdb.connectionpolicy?view=azure-java-stable) for further details


## Next Topics

- [Resilient Design](REAMD.md)
- [Resilient Config for SpringBoot](resilient-config-spring-boot.md)
- [Resilient Config for Generic Java](resilient-config-generic-java.md)
- [Resilient Config for Python](resilient-config-python.md)
- [Resilient Config for Node.js](resilient-config-nodejs.md)
- [Resilient Config for Golang](resilient-config-golang.md)
