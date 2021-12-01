// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.eg.az.spring.demo.cosmos;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.cosmos.GatewayConnectionConfig;
import com.azure.cosmos.ThrottlingRetryOptions;
import com.azure.spring.data.cosmos.CosmosFactory;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.core.CosmosTemplate;
import com.azure.spring.data.cosmos.core.ResponseDiagnostics;
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor;
import com.azure.spring.data.cosmos.core.convert.MappingCosmosConverter;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.azure.spring.data.cosmos.repository.config.EnableReactiveCosmosRepositories;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;

import com.eg.az.spring.demo.YamlPropertySourceFactory;

@Configuration
@EnableConfigurationProperties(CosmosProperties.class)
@EnableCosmosRepositories
@EnableReactiveCosmosRepositories
@ConfigurationProperties(prefix = "cosmos")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class CosmosClientConfig extends AbstractCosmosConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(CosmosClientConfig.class);

	@Autowired
	private CosmosProperties properties;

	@Bean
	public CosmosClientBuilder cosmosClientBuilder() {
		DirectConnectionConfig directConnectionConfig = DirectConnectionConfig.getDefaultConfig();
		return new CosmosClientBuilder().endpoint(properties.getUri()).key(properties.getKey())
				.directMode(directConnectionConfig);
	}

	public CosmosClientBuilder cosmosClientBuilder_rewrite() {

		DirectConnectionConfig directConnectionConfig = DirectConnectionConfig.getDefaultConfig();
		directConnectionConfig.setConnectTimeout(Duration.ofMillis(100)); // .1 sec
		directConnectionConfig.setIdleConnectionTimeout(Duration.ofMillis(60000)); // 60 sec
		directConnectionConfig.setIdleEndpointTimeout(Duration.ofMillis(60000)); // 60 sec
		// directConnectionConfig.setMaxConnectionsPerEndpoint(130); // default
		// directConnectionConfig.setMaxRequestsPerConnection(30); // default
		directConnectionConfig.setNetworkRequestTimeout(Duration.ofMillis(5000)); // 5 sec

		GatewayConnectionConfig gatewayConnectionConfig = GatewayConnectionConfig.getDefaultConfig();
		gatewayConnectionConfig.setIdleConnectionTimeout(Duration.ofMillis(5000));
		gatewayConnectionConfig.setMaxConnectionPoolSize(2);

		ThrottlingRetryOptions retryOptions = new ThrottlingRetryOptions();
		retryOptions.setMaxRetryAttemptsOnThrottledRequests(5);
		retryOptions.setMaxRetryWaitTime(Duration.ofMillis(5000));

		CosmosClientBuilder builder;

		// Gateway mode
		builder = new CosmosClientBuilder().endpoint(properties.getUri()).key(properties.getKey())
				.throttlingRetryOptions(retryOptions).gatewayMode(gatewayConnectionConfig);

//		// Direct mode
//		builder = new CosmosClientBuilder().endpoint(properties.getUri()).key(properties.getKey())
//				.throttlingRetryOptions(retryOptions).directMode(directConnectionConfig, gatewayConnectionConfig);

		return builder;
	}

	@Bean
	public CosmosConfig cosmosConfig() {
		return CosmosConfig.builder()
				// .responseDiagnosticsProcessor(new
				// ResponseDiagnosticsProcessorImplementation())
				.enableQueryMetrics(properties.isQueryMetricsEnabled()).build();
	}

	@Override
	protected String getDatabaseName() {
		return properties.getDatabase();
	}

	private static class ResponseDiagnosticsProcessorImplementation implements ResponseDiagnosticsProcessor {
		@Override
		public void processResponseDiagnostics(@Nullable ResponseDiagnostics responseDiagnostics) {
			logger.info("Response Diagnostics {}", responseDiagnostics);
		}
	}

	@Override
	public CosmosFactory cosmosFactory(CosmosAsyncClient cosmosAsyncClient) {
		// TODO Auto-generated method stub
		return super.cosmosFactory(cosmosAsyncClient);
	}

	@Override
	public CosmosAsyncClient cosmosAsyncClient(CosmosClientBuilder cosmosClientBuilder) {
		// TODO Auto-generated method stub
		return super.cosmosAsyncClient(cosmosClientBuilder_rewrite());
	}

	@Override
	public CosmosTemplate cosmosTemplate(CosmosFactory cosmosFactory, CosmosConfig cosmosConfig,
			MappingCosmosConverter mappingCosmosConverter) {
		// TODO Auto-generated method stub
		return super.cosmosTemplate(cosmosFactory, cosmosConfig, mappingCosmosConverter);
	}
}
