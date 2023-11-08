package com.sample.project.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "MAIL")
	private String mail;

	// Default constructor
	public Customer() {
	}

	// Constructor, getters, and setters
	public Customer(Long id, String name, String mail) {
		this.id = id;
		this.name = name;
		this.mail = mail;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	// toString method for debugging purposes
	@Override
	public String toString() {
		return "Customer{" + "id=" + id + ", name='" + name + '\'' + ", mail='" + mail + '\'' + '}';
	}
}
