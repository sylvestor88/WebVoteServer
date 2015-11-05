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
	
	static Mapper<Moderator> moderatorMapper;
	static Mapper<Vote> voteMapper;
	static Mapper<VotesByModerator> getVotesByModerator;
	static PreparedStatement newModerator;
	static PreparedStatement newVote;
	static PreparedStatement registerYesVote;
	static PreparedStatement registerNoVote;
	static Cassandra cs = Cassandra.DB;
	
	public static void cachedStatements()
	{
		cs.connect();
		moderatorMapper = new MappingManager(cs.getSession()).mapper(Moderator.class);
		newModerator = cs.getSession().prepare(
				"INSERT INTO moderators " + "(moderator_id, first_name, last_name, email, password, created_date) " + 
		"VALUES (?, ?, ?, ?, ?, ?);");
		newVote = cs.getSession().prepare(
				"INSERT INTO votes_by_moderator " + "(moderator_id, vote_id, title, description, end_date, start_date) " + 
		"VALUES (?, ?, ?, ?, ?, ?);");
		voteMapper = new MappingManager(cs.getSession()).mapper(Vote.class);
		getVotesByModerator = new MappingManager(cs.getSession()).mapper(VotesByModerator.class);
		registerYesVote = cs.getSession().prepare("UPDATE votes_results SET yes_count = yes_count + 1 WHERE vote_id = ?;");
		registerNoVote = cs.getSession().prepare("UPDATE votes_results SET no_count = no_count + 1 WHERE vote_id = ?;");
	}
	
	public static void saveModerator(Moderator mod)
	{
		Session session = cs.getSession();
		BoundStatement boundStatement = new BoundStatement(newModerator);
		session.execute(boundStatement.bind(mod.getModerator_id(), mod.getFirstName(), mod.getLastName(), mod.getEmail(), mod.getPassword(), mod.getCreated_date()));
	}
	
	public static Moderator getModerator(String modId)
	{
		Moderator mod = moderatorMapper.get(modId);	
		return mod;
	}
	
	public static void deleteModerator(String modId)
	{
		moderatorMapper.deleteAsync(modId);
		Session session = cs.getSession();
		session.execute("DELETE FROM votes_by_moderator WHERE moderator_id = \'" + modId + "\';");
	}
	
	public static void saveVote(String modId, Vote vote)
	{
		voteMapper.save(vote);
		Session session = cs.getSession();
		BoundStatement boundStatement = new BoundStatement(newVote);
		session.execute(boundStatement.bind(modId, vote.getVote_id(), vote.getTitle(), vote.getDescription(), vote.getEnd_date(), vote.getStart_date()));
	}
	
	public static Result<VotesByModerator> getPollsByModerator(String modId)
	{
		Session session = cs.getSession();
		ResultSet results = session.execute("SELECT * FROM votes_by_moderator WHERE moderator_id = \'" + modId + "\';");
		Result<VotesByModerator> votes = getVotesByModerator.map(results);
		return votes;
	}
	
	public static Vote getVote(UUID voteId)
	{
		return voteMapper.get(voteId);	
	}
	
	public static Result<Vote> getAllVotes()
	{
		Session session = cs.getSession();
		ResultSet results = session.execute("SELECT * FROM votes;");
		Result<Vote> votes = voteMapper.map(results);
		return votes;
	}
	
	public static void deleteVote(String modId, UUID voteId)
	{
		Session session = cs.getSession();
		voteMapper.delete(voteId);
		session.execute("DELETE from votes_results WHERE vote_id = " + voteId + ";");
		session.execute("DELETE from votes_by_moderator WHERE moderator_id = \'" + modId + "\' " + "and vote_id = " + voteId + ";");	
	}
	
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
}
