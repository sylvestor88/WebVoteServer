package com.sylvestor.voteapp;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "voteapp", name = "votes_results")
public class VoteResult {
	
	@PartitionKey
	UUID vote_id;
	long yes_count;
	long no_count;
	
	public UUID getVote_id() {
		return vote_id;
	}
	public void setVote_id(UUID vote_id) {
		this.vote_id = vote_id;
	}
	public long getYes_count() {
		return yes_count;
	}
	public void setYes_count(long yes_count) {
		this.yes_count = yes_count;
	}
	public long getNo_count() {
		return no_count;
	}
	public void setNo_count(long no_count) {
		this.no_count = no_count;
	}

}
