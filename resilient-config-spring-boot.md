# resilient-config-spring-boot
Resiliency configuration guide for Spring Boot

## Intro

Spring Boot/Cloud configuration guide to achieve resiliency using patterns against faults and trasient problems from Azure Service components

## Azure Cache for Redis

Because per connection cost is higher with Azure Cache for Redis, connection reuse's recommendable by using connection pool. With Lettuce, Redis connections are designed to be long-lived and thread-safe, and if the connection is lost will reconnect until close() is called. Pending commands that have not timed out will be (re)sent after successful reconnection. You don't have to care of reconnection to Redis yourself

> Note: Jedis is another option to implement connection pool but Lettuce supports synchronous, asynchronous, and even reactive interfaces. Lettuce is in multi-threaded, event-based model that uses pipelining as a matter of course. Even when you use it synchronously, it’s asynchronous underneath

Consider these to add to your application.yaml

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

## Cosmos DB

### 1. Retry 


### 2. Fallback 


### 3. Timeouts 


### 4. Circuit breaker 


## Next Topics

- [Resilient Design](REAMD.md)
- [Resilient Config for SpringBoot](resilient-config-spring-boot.md)
- [Resilient Config for Generic Java](resilient-config-generic-java.md)
- [Resilient Config for Python](resilient-config-python.md)
- [Resilient Config for Node.js](resilient-config-nodejs.md)
- [Resilient Config for Golang](resilient-config-golang.md)
