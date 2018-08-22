package com.example.demoApp.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

@Entity
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private int version;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private User owner;

	@NotNull
	@Column
	private double balance = 0.0;

	@NotNull
	@Enumerated(STRING)
	@Column(name = "account_type", nullable = false)
	private AccountType type;

	@NotNull
	@Enumerated(STRING)
	@Column(name = "account_status", nullable = false)
	private AccountStatus status;

	public Account() {

	}

	public Account(Integer id, int version, @NotNull User owner, @NotNull double balance, @NotNull AccountType type,
				   @NotNull AccountStatus status) {
		this.id = id;
		this.version = version;
		this.owner = owner;
		this.balance = balance;
		this.type = type;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}
}
