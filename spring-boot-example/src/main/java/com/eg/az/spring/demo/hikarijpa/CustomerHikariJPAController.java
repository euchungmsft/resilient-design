package com.eg.az.spring.demo.hikarijpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerHikariJPAController {
	
	private static final Logger log = LoggerFactory.getLogger(CustomerHikariJPAController.class);
	
	@Autowired
	private CustomerRepository repository;


	@PostMapping(consumes = "application/json", produces = "application/json")
	public Customer newItem(@RequestBody Customer newValue) {
		Customer val = repository.save(newValue);
		return val;
	}

	@GetMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
	public Customer findOne(@PathVariable long id) {
		Customer val = repository.findById(id);
		return val;
	}

	@GetMapping(consumes = "application/json", produces = "application/json")
	public Object[] listAll() {

		Iterable<Customer> it = repository.findAll();
		ArrayList<Customer> ret = new ArrayList<Customer>();
		it.forEach(c -> {
			ret.add(c);
		});
		
		return ret.toArray();

	}	
	
}
