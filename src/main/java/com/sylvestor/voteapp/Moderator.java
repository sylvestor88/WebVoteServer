package com.sylvestor.voteapp;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(keyspace = "voteapp", name = "moderators")
public class Moderator {
	
	@NotEmpty(message = "Please enter valid username")
	@Size(min = 6, message = "Your username must be greater than 6 characters")
	@PartitionKey
	String moderator_id;
	
	@Column (name = "first_name")
	String firstName;
	
	@Column (name = "last_name")
	String lastName;
	
	@NotEmpty(message = "Please enter valid email address")
	@Email
	@Column (name = "email")
	String email;
	
	@NotEmpty(message = "Please enter your password")
	@Size(min = 6, max = 12, message = "Your password must be between 6 and 12 characters")
	@Transient
	String password;
	
	@Column (name = "created_date")
	Date created_date;
	
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getModerator_id() {
		return moderator_id;
	}
	public void setModerator_id(String moderator_id) {
		this.moderator_id = moderator_id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}

