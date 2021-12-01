package com.eg.az.spring.demo.redis;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eg.az.spring.demo.Fruit;

@RestController
@RequestMapping("/fruit-redis")
public class FruitRedisController {

	@Autowired
	private StringRedisTemplate template;

	@PostMapping(consumes = "application/json", produces = "application/json")
	public Fruit newItem(@RequestBody Fruit newValue) {
		try {
			ValueOperations<String, String> ops = this.template.opsForValue();
			ops.set("" + newValue.getId(), newValue.getName());
			return newValue;
		}
		catch(Throwable th) {
			th.printStackTrace();
		}
		return newValue;
	}

	@GetMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
	public Fruit findOne(@PathVariable int id) {
		// return repository.findById(id);

		try {

			ValueOperations<String, String> ops = this.template.opsForValue();
			String name = ops.get("" + id);

			if (name == null)
				return new Fruit();

			return new Fruit(id, name);

		} catch (Throwable th) {
			th.printStackTrace();
		}

		return null;

	}

	@GetMapping(consumes = "application/json", produces = "application/json")
	public Fruit[] listAll() {
		
		Set<String> lst = this.template.keys("*");
	
		ValueOperations<String, String> ops = this.template.opsForValue();
		String id;
		Fruit ret[] = new Fruit[lst.size()]; 
		int c = 0;
		for(Iterator<String> i = lst.iterator(); i.hasNext(); c++) {
			id = i.next();
			ret[c] = new Fruit(Integer.parseInt(id), ops.get(id));
		}
		
		return ret;
	}

}
