package com.eg.az.spring.demo;

public class Fruit {

	private Integer id;
	private String name;

	public Fruit() {
		this(-1, "NIL");
	}

	public Fruit(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
