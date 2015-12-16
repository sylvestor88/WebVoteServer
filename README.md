#WebVoteServer

This repository contains REST Endpoints that uses Cassandra database for storing data. The application is build using Spring Framework and written in JAVA.

#### What?
The VoteApp allows users to Post Polls they want people to vote for either as Yes/No (the app will later allow users to provide multiple options to choose). The link to these Polls can then be shared with people for them to cast their votes for an active Poll. The Moderators can then check the result of these Polls once they log in. 

#### How?
To Post polls, users need to sign up to become a Moderator. The moderators can then create any number of polls they wish to post and share the link with people. Voters do not require to sign in to vote for on a poll. The application does not restrict people to cast multiple votes.

#### Server Endpoints

Below is the list of endpoints that allows users to perform actions:

The server application is currently running on EC2 instance. The client is running on:

```sh
http://angularclient-sylvestor88.c9users.io/index.html
```
