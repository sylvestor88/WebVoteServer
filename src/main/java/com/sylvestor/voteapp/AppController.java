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
	public @ResponseBody Moderator createModerator(
			@Valid @RequestBody Moderator mod) 
	{
		Date currentDate = new Date();
		mod.setCreated_date(currentDate);
		CassOperations.saveModerator(mod);
		return mod;
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
	
	//Create a Vote
	@RequestMapping(value = "moderator/{moderator_id}/vote", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody ResponseEntity<String> createVote(@PathVariable("moderator_id") String modId, @RequestBody Vote vote)
	{
		Moderator m = CassOperations.getModerator(modId);
		
		if(!(m == null))
		{
			UUID voteId = UUIDs.timeBased();
			vote.setVote_id(voteId);
			
			Date currentDate = new Date();
			vote.setAdded_date(currentDate);
			
			CassOperations.saveVote(modId, vote);
			
			return new ResponseEntity<String>("Vote added succesfully!!", HttpStatus.OK);
		}
		
		return new ResponseEntity<String>("Moderator Not Found!!!", HttpStatus.NOT_FOUND);
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
        	
        	if(!(voteList == null))
        		return new ResponseEntity<List<VotesByModerator>>(voteList, HttpStatus.OK);
        	else
        		return new ResponseEntity<List<VotesByModerator>>(HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<List<VotesByModerator>>(HttpStatus.NOT_FOUND);
    }
    
    // Delete a Vote
    @RequestMapping(value = "moderator/{moderator_id}/vote/{vote_id}", method = RequestMethod.DELETE, consumes = "application/json")
    public @ResponseBody ResponseEntity<String> deleteVote(
    		@PathVariable("moderator_id") String modId, @PathVariable("vote_id") UUID voteId)
    		{
    			Moderator m = CassOperations.getModerator(modId);
    			
    			if(!(m == null))
    			{
    					CassOperations.deleteVote(modId, voteId);
    					return new ResponseEntity<String>("Vote with Id : " + voteId + " deleted successfully!", HttpStatus.OK);
    			}
    			
    			return new ResponseEntity<String>("Moderator with Id : " + modId + " dnot found!", HttpStatus.NOT_FOUND);
    		}
    
    // Register a Vote
    
    @RequestMapping(value = "vote/{vote_id}", method = RequestMethod.PUT, consumes = "application/json")
    public @ResponseBody ResponseEntity<String> registerVote(
    		@PathVariable("vote_id") UUID voteId, @RequestParam(value = "choice") int choice)
    		{
    				if(choice == 0 || choice == 1)
    				{
    					CassOperations.registerVote(choice, voteId);
    					return new ResponseEntity<String>("Vote registerted succesfully!!", HttpStatus.OK);
    				}
    				else
    				{
    					return new ResponseEntity<String>("Invalid Input! Vote not registered.", HttpStatus.NOT_FOUND);
    				}
    		}
}

