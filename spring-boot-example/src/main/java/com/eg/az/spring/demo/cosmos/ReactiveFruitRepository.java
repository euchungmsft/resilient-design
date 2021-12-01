// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.eg.az.spring.demo.cosmos;

import com.azure.spring.data.cosmos.repository.Query;
import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import com.eg.az.spring.demo.Fruit;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveFruitRepository extends ReactiveCosmosRepository<Fruit, String> {

	@Query(value = "SELECT * FROM c")
	Flux<Fruit> getAllFruits();
	
}
