package com.sylvestor.voteapp;

import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

public class CassOperations {
	
	// Mapper and PreparedStatements
	static Mapper<Moderator> moderatorMapper;
	static Mapper<Vote> voteMapper;
	static Mapper<VotesByModerator> getVotesByModerator;
	static Mapper<VoteResult> voteResultMapper;
	static PreparedStatement newVote;
	static PreparedStatement registerYesVote;
	static PreparedStatement registerNoVote;
	static Cassandra cs = Cassandra.DB;
	
	// Initialize Mappers and PreparedStatement when Application starts
	public static void cachedStatements()
	{
		cs.connect();
		moderatorMapper = new MappingManager(cs.getSession()).mapper(Moderator.class);
		newVote = cs.getSession().prepare(
				"INSERT INTO votes_by_moderator " + "(moderator_id, vote_id, title, description, end_date, start_date) " + 
		"VALUES (?, ?, ?, ?, ?, ?);");
		voteMapper = new MappingManager(cs.getSession()).mapper(Vote.class);
		getVotesByModerator = new MappingManager(cs.getSession()).mapper(VotesByModerator.class);
		registerYesVote = cs.getSession().prepare("UPDATE votes_results SET yes_count = yes_count + 1 WHERE vote_id = ?;");
		registerNoVote = cs.getSession().prepare("UPDATE votes_results SET no_count = no_count + 1 WHERE vote_id = ?;");
		voteResultMapper = new MappingManager(cs.getSession()).mapper(VoteResult.class);
	}
	
	// Save moderator to the database via Moderator Mapper
	public static void saveModerator(Moderator mod)
	{
		moderatorMapper.saveAsync(mod);
	}
	
	// Retrieve single moderator from the database via Moderator Mapper
	public static Moderator getModerator(String modId)
	{
		Moderator mod = moderatorMapper.get(modId);	
		return mod;
	}
	
	// Delete Moderator and all votes posted by that moderayor
	public static void deleteModerator(String modId)
	{
		moderatorMapper.deleteAsync(modId);
		Session session = cs.getSession();
		session.execute("DELETE FROM votes_by_moderator WHERE moderator_id = \'" + modId + "\';");
	}
	
	// Save new vote in votes and votes_by_moderator table
	public static void saveVote(String modId, Vote vote)
	{
		voteMapper.save(vote);
		Session session = cs.getSession();
		BoundStatement boundStatement = new BoundStatement(newVote);
		session.execute(boundStatement.bind(modId, vote.getVote_id(), vote.getTitle(), vote.getDescription(), vote.getEnd_date(), vote.getStart_date()));
	}
	
	// Retrieve all votes for a given moderator
	public static Result<VotesByModerator> getPollsByModerator(String modId)
	{
		Session session = cs.getSession();
		ResultSet results = session.execute("SELECT * FROM votes_by_moderator WHERE moderator_id = \'" + modId + "\';");
		Result<VotesByModerator> votes = getVotesByModerator.map(results);
		return votes;
	}
	
	// Retrieve single vote by ID
	public static Vote getVote(UUID voteId)
	{
		return voteMapper.get(voteId);	
	}
	
	// Retrieve all votes
	public static Result<Vote> getAllVotes()
	{
		Session session = cs.getSession();
		ResultSet results = session.execute("SELECT * FROM votes;");
		Result<Vote> votes = voteMapper.map(results);
		return votes;
	}
	
	// Delete vote and all the references
	public static void deleteVote(String modId, UUID voteId)
	{
		Session session = cs.getSession();
		voteMapper.delete(voteId);
		session.execute("DELETE from votes_results WHERE vote_id = " + voteId + ";");
		session.execute("DELETE from votes_by_moderator WHERE moderator_id = \'" + modId + "\' " + "and vote_id = " + voteId + ";");	
	}
	
	// Register entry for a given vote
	public static void registerVote(int choice, UUID voteId)
	{
		Session session = cs.getSession();
		
		if(choice == 1)
		{
			BoundStatement boundStatement = new BoundStatement(registerYesVote);
			session.execute(boundStatement.bind(voteId));
		}
		else
		{
			BoundStatement boundStatement = new BoundStatement(registerNoVote);
			session.execute(boundStatement.bind(voteId));
		}
	}
	
	// Retrieve Vote Result
	public static VoteResult getVoteResult(UUID voteId)
	{
		return voteResultMapper.get(voteId);
	}
}
