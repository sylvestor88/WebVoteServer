package com.sylvestor.voteapp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Result;

@RestController
@ComponentScan
@EnableAutoConfiguration
@RequestMapping("/voteapp/")
public class AppController {

	// Create Moderator
	
	@RequestMapping(value = "moderator", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<Message> createModerator(
			@Valid @RequestBody Moderator mod) 
	{
		Moderator checkMod = CassOperations.getModerator(mod.getModerator_id());
		
		if(checkMod == null)
		{
			Date currentDate = new Date();
			mod.setCreated_date(currentDate);
			CassOperations.saveModerator(mod);
			Message msg = new Message("Moderator created successfully!!! You can now post a Poll.");
			return new ResponseEntity<Message>(msg, HttpStatus.CREATED);
		}
		else
		{
			Message msg = new Message("Moderator with given username already exists!!!");
			return new ResponseEntity<Message>(msg, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	// Find a Moderator
	
	@RequestMapping(value = "moderator/{moderator_id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<Moderator> getModerator(
			@PathVariable("moderator_id") String modId) 
		{
		
		Moderator mod = CassOperations.getModerator(modId);

		if(!(mod == null))
				{
			return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
				}
	
		return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);
	}
	
	// Update Moderator
	
	@RequestMapping(value = "moderator/{moderator_id}", method = RequestMethod.PUT, consumes = "application/json")
	public @ResponseBody ResponseEntity<Moderator> updateModerator(
			@PathVariable("moderator_id") String modId, @Valid @RequestBody Moderator mod)
			{
				Moderator m = CassOperations.getModerator(modId);
				
				if (!(m == null)) {
					
					mod.setCreated_date(m.getCreated_date());
					CassOperations.saveModerator(mod);
					return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
				}

				else {

					return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);
				}
			}
	
	// Delete a Moderator
	@RequestMapping(value = "moderator/{moderator_id}", method = RequestMethod.DELETE, consumes = "application/json")
	public @ResponseBody ResponseEntity<String> deleteModerator(
			@PathVariable("moderator_id") String modId)
			{
				Moderator m = CassOperations.getModerator(modId);
				
				if (!(m == null)) {
					
					CassOperations.deleteModerator(modId);
					return new ResponseEntity<String>("Moderator with Id : " + modId + " deleted successfully!", HttpStatus.OK);
				}

				else {

					return new ResponseEntity<String>("Moderator with Id : " + modId + " not found!", HttpStatus.NOT_FOUND);
				}
			}
	
	// Login Moderator
	@RequestMapping(value = "moderator/{moderator_id}/login", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<Moderator> loginModerator(
			@PathVariable("moderator_id") String modId)
			{
				Moderator mod = CassOperations.getModerator(modId);

				if(!(mod == null))
				{
					return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
				}
	
				return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);
			}
	
	//Create a Vote
	@RequestMapping(value = "moderator/{moderator_id}/vote", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<Message> createVote(@PathVariable("moderator_id") String modId, @RequestBody Vote vote)
	{
		Moderator m = CassOperations.getModerator(modId);
		
		if(!(m == null))
		{
			UUID voteId = UUIDs.random();
			vote.setVote_id(voteId);
			vote.setModerator_id(modId);
			Date currentDate = new Date();
			vote.setAdded_date(currentDate);
			
			CassOperations.saveVote(modId, vote);
			Message msg = new Message("Vote added succesfully!!");
			return new ResponseEntity<Message>(msg, HttpStatus.OK);
		}
		
		Message msg = new Message("Moderator Not Found!!! Sign Up to publish new vote.");
		return new ResponseEntity<Message>(msg, HttpStatus.NOT_FOUND);
	}
	
	// Show a Vote
	@RequestMapping(value = "vote/{vote_id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<Vote> getVote(
			@PathVariable("vote_id") UUID voteId)
			{
				Vote vote = CassOperations.getVote(voteId);
				if(!(vote == null))
				{	
					return new ResponseEntity<Vote>(vote, HttpStatus.OK);
				}
				
				return new ResponseEntity<Vote>(HttpStatus.NOT_FOUND);
			}
	
	
	// Show all Votes
	@RequestMapping(value = "votes", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<List<Vote>> getAllVotes() 
	{
		List<Vote> list = null;
		Result<Vote> results = CassOperations.getAllVotes();
		list = results.all();
		
		if(!(list == null))
		{
			return new ResponseEntity<List<Vote>>(list, HttpStatus.OK);
		}
		else
			return new ResponseEntity<List<Vote>>(HttpStatus.NOT_FOUND);
	}
	
	
	// Update a Vote
	@RequestMapping(value = "moderator/{moderator_id}/vote/{vote_id}", method = RequestMethod.PUT, consumes = "application/json")
	public @ResponseBody ResponseEntity<Vote> updateVote(
			@PathVariable("moderator_id") String modId, @PathVariable("vote_id") UUID voteId, 
			@RequestBody Vote vote)
			{
				Moderator m = CassOperations.getModerator(modId);
				
				if (!(m == null)) {
					
					Vote v = CassOperations.getVote(voteId);
					
					if(!(v == null))
					{
						vote.setAdded_date(v.getAdded_date());
						vote.setModerator_id(v.getModerator_id());
						vote.setVote_id(v.getVote_id());
						CassOperations.saveVote(modId, vote);
					}
		
					return new ResponseEntity<Vote>(vote, HttpStatus.OK);
				}

				else {

					return new ResponseEntity<Vote>(HttpStatus.NOT_FOUND);
				}
			}
	
	// Show all Votes for a Moderator

    @RequestMapping(value = "moderator/{moderator_id}/vote", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody ResponseEntity<List<VotesByModerator>> getAllVotes(
            @PathVariable("moderator_id") String modId) {

    	List<VotesByModerator> voteList = null;	
    	Moderator m = CassOperations.getModerator(modId);

        if (!(m == null)) {

        	Result<VotesByModerator> resultSet = CassOperations.getPollsByModerator(modId);
        	voteList = resultSet.all();
        	
        	return new ResponseEntity<List<VotesByModerator>>(voteList, HttpStatus.OK);
        }
        else{
        	
        		return new ResponseEntity<List<VotesByModerator>>(HttpStatus.NOT_FOUND);
        }

    }
    
    // Delete a Vote
    @RequestMapping(value = "moderator/{moderator_id}/vote/{vote_id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<Message> deleteVote(
    		@PathVariable("moderator_id") String modId, @PathVariable("vote_id") UUID voteId)
    		{
    			Moderator m = CassOperations.getModerator(modId);
    			
    			if(!(m == null))
    			{
    				Vote v = CassOperations.getVote(voteId);
    				    
    				if(!(v == null))
    				{
    				   CassOperations.deleteVote(modId, voteId);
        			   Message msg = new Message("Vote Deleted Successfully!!!");
        			   return new ResponseEntity<Message>(msg, HttpStatus.OK);
    				}
    				else
    				{
    				   Message msg = new Message("Wrong Vote Id. No vote with Id " + voteId + " exists");
    	    		   return new ResponseEntity<Message>(msg, HttpStatus.NOT_FOUND);
    				}
    			}
    			else
    			{
    				Message msg = new Message("Wrong Moderator Id. No user with Id " + modId + " exists");
    				return new ResponseEntity<Message>(msg, HttpStatus.NOT_FOUND);
    			}
    		}
    
    // Register a Vote
    
    @RequestMapping(value = "vote/{vote_id}", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody ResponseEntity<Message> registerVote(
    		@PathVariable("vote_id") UUID voteId, @RequestParam(value = "choice") int choice)
    		{
    				if(choice == 0 || choice == 1)
    				{
    					CassOperations.registerVote(choice, voteId);
    					Message msg = new Message("Vote registerted succesfully!!");
    					return new ResponseEntity<Message>(msg, HttpStatus.OK);
    				}
    				else
    				{
    					Message msg = new Message("Invalid Input! Vote not registered.");
    					return new ResponseEntity<Message>(msg, HttpStatus.NOT_FOUND);
    				}
    		}
    
    // Show Vote Result
    @RequestMapping(value = "vote/{vote_id}/result", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody ResponseEntity<VoteResult> getVoteResult(
    		@PathVariable("vote_id") UUID voteId)
    		{			
    			VoteResult voteResult = CassOperations.getVoteResult(voteId);
    			
    			if(!(voteResult == null))
    			{
    				return new ResponseEntity<VoteResult>(voteResult, HttpStatus.OK);
    			}
		
    			return new ResponseEntity<VoteResult>(HttpStatus.NOT_FOUND);
    		}
}

