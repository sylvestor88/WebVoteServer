package com.sylvestor.voteapp;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "voteapp", name = "votes")
public class Vote {
	
	@PartitionKey
	UUID vote_id;
	Date end_date;
	Date start_date;
	String moderator_id;
	Date added_date;
	
	String title;
	String description;
	
	public UUID getVote_id() {
		return vote_id;
	}
	public void setVote_id(UUID vote_id) {
		this.vote_id = vote_id;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public String getModerator_id() {
		return moderator_id;
	}
	public void setModerator_id(String moderator_id) {
		this.moderator_id = moderator_id;
	}
	public Date getAdded_date() {
		return added_date;
	}
	public void setAdded_date(Date added_date) {
		this.added_date = added_date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
}
