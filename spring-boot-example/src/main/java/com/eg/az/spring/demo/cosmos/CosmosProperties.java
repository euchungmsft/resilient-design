// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.eg.az.spring.demo.cosmos;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.cosmos")
public class CosmosProperties {

	private String uri;

	private String key;

	private String secondaryKey;

	private boolean queryMetricsEnabled;

	private String database;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecondaryKey() {
		return secondaryKey;
	}

	public void setSecondaryKey(String secondaryKey) {
		this.secondaryKey = secondaryKey;
	}

	public boolean isQueryMetricsEnabled() {
		return queryMetricsEnabled;
	}

	public void setQueryMetricsEnabled(boolean enableQueryMetrics) {
		this.queryMetricsEnabled = enableQueryMetrics;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
}
