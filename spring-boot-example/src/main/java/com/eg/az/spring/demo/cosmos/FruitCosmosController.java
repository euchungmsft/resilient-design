package com.eg.az.spring.demo.cosmos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eg.az.spring.demo.Fruit;
import com.google.gson.Gson;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fruit-cosmos")
public class FruitCosmosController {
	
	private final Logger logger = LoggerFactory.getLogger(FruitCosmosController.class);

	@Autowired
	private FruitRepository repository;

	@Autowired
	private ReactiveFruitRepository reactiveFruitRepository;

	@PostMapping(consumes = "application/json", produces = "application/json")
	public Fruit newItem(@RequestBody Fruit newValue) {
		Fruit val = repository.save(newValue);
		return val;
	}

	@GetMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
	public Fruit findOne(@PathVariable int id) {

		Optional<Fruit> val;
		val = repository.findById("" + id);

		return val.get();
	}

	@GetMapping(consumes = "application/json", produces = "application/json")
	public Fruit[] listAll() {

		List<Fruit> lst = repository.getAllFruits();
		Fruit ret[] = new Fruit[lst.size()];
		int c = 0;
		for (Iterator<Fruit> i = lst.iterator(); i.hasNext(); c++) {
			ret[c] = i.next();
		}

		return ret;

	}

	@GetMapping(value = "/react", consumes = "application/json", produces = "application/json")
	public Object[] listAllReactive() {

		Flux<Fruit> lst = reactiveFruitRepository.getAllFruits();
		ArrayList<Fruit> ret = new ArrayList<>();
		Gson gson = new Gson();
		lst.map(f -> {
            logger.info("fruit is : {}", gson.toJson(f));
            ret.add(f);
            return f;
        }).subscribe();
        
		return ret.toArray();

	}

}
