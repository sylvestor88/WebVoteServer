CREATE	keyspace IF NOT EXISTS voteapp with replication = { 'class': 'SimpleStrategy', 'replication_factor': 3};

CREATE TABLE IF NOT EXISTS voteapp.moderators (
	moderator_id text PRIMARY KEY,
	first_name text,
	last_name text,
	email text,
	password text,
	created_date timestamp
);


CREATE TABLE IF NOT EXISTS voteapp.votes (
	vote_id timeuuid,
	moderator_id text,
	end_date timestamp,
	start_date timestamp,
	added_date timestamp,
	title text,
	description text,
	PRIMARY KEY((vote_id), end_date, start_date)
)
WITH CLUSTERING ORDER BY (end_date DESC, start_date DESC);


CREATE TABLE IF NOT EXISTS voteapp.votes_by_moderator (
	moderator_id text,	
	vote_id timeuuid,
	title text,
	description text,
	end_date timestamp,
	start_date timestamp,
	PRIMARY KEY((moderator_id), vote_id)
)
WITH CLUSTERING ORDER BY (vote_id DESC);


CREATE TABLE IF NOT EXISTS voteapp.votes_results (		
	vote_id timeuuid,
	yes_count counter,
	no_count counter,
	PRIMARY KEY((vote_id))
);
