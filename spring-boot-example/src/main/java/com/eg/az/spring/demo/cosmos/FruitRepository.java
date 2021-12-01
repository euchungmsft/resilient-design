package com.eg.az.spring.demo.cosmos;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.eg.az.spring.demo.Fruit;

@Repository
public interface FruitRepository extends CosmosRepository<Fruit, String> {

	Iterable<Fruit> findByName(String name);

	Fruit findByIdAndName(String id, String name);

	@Query(value = "SELECT * FROM c")
	List<Fruit> getAllFruits();

}
