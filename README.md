# WebVoteServer

This repository contains REST Endpoints that uses Cassandra database for storing data. The application is build using Spring Framework and written in JAVA.

#### What?
The VoteApp allows user to Post Polls that they would like people to vote for either as Yes/No (the app will later allow users to provide multiple options to choose). The link to these Polls can then be shared with people from where they can register their votes for an active Poll. The Moderators can then check the result of these Polls once they log in. 

#### How?
To Post polls, user need to sign up to become a Moderator. The moderator can then create any number of polls he/she wishes to post and share the link with other people. Voters do not require to sign up to vote for a poll. The application does not restrict people to cast multiple votes.

#### Client - Web App
I have created a client for these services using angularJS framework whose source code is in the repository:
```sh
    https://github.com/sylvestor88/WebVoteClient.git
```

The client application should also be running live on cloud 9 instance and can be accessed using the below link:
```sh
http://angularclient-sylvestor88.c9users.io/index.html
```
#### Server Endpoints
Below is the list of endpoints that allow users to perform various actions.
A moderator record can be represented in a JSON hash like so:
```sh
{
    "moderator_id": "sylvestor88",
    "firstName": "Sylvestor",
    "lastName": "George",
    "email": "sylvestor.george88@gmail.com",
    "password": "mypassword"
}
```
A poll record can be represented in a JSON hash like so:
```sh
{
    "title": "What type of smartphone do you have?",
    "description": "Testing my Voting App",
    "started_at": "2015-02-23T13:00:00.000Z",
    "expired_at" : "2015-02-24T13:00:00.000Z"
}
```

The service provides following endpoints and semantics:

```sh
POST /voteapp/moderator
    Creates a new moderator record. 
```
```sh
GET /voteapp/moderator/{moderator_id}
    Returns the matching moderator record or 404 if none exist.
```
```sh
PUT /voteapp/moderator/{moderator_id}
    Updates an existing moderator record or 404 if none exist.
```
```sh
DELETE /voteapp/moderator/{moderator_id}
    Deletes an existing moderator and the polls associated with the moderator.
```
```sh
POST /voteapp/moderator/{moderator_id}/vote
    Creates a new poll for the moderator. 
```
```sh
GET /voteapp/{vote_id}
    Returns the matching vote record or 404 if none exist.
```
```sh
GET /voteapp/votes
    Returns all the existing votes.
```
```sh
PUT /voteapp/moderator/{moderator_id}/vote/{vote_id}
    Updates an existing poll record or 404 if moderator or poll does not exist.
```
```sh
DELETE /voteapp/moderator/{moderator_id}/vote/{vote_id}
    Deletes an existing poll or 404 if moderator or poll does not exist.
```
```sh
PUT /voteapp/vote/{vote_id}?choice=1
    Registers users vote for a given poll. If choice=1, vote is registered as 'yes', 'no' for choice=0. 404 for any other choice or invalid poll
```
The server application is currently running on EC2 instance at port 8080:
```sh
    ec2-52-27-91-149.us-west-2.compute.amazonaws.com
```
