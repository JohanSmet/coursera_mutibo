/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboDeck;
import mutibo.data.MutiboMatch;
import mutibo.data.MutiboSet;
import mutibo.data.User;
import mutibo.google.GoogleCloudMessenger;
import mutibo.repository.MutiboDeckRepository;
import mutibo.repository.MutiboMatchRepository;
import mutibo.repository.UserRepository;
import mutibo.security.UserAuthentication;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Redacted
 */

@RestController
public class MultiplayerController
{
	@RequestMapping(method=RequestMethod.POST, value="/multiplayer/challenge-random")
	MultiplayerMatch challengeRandom(@RequestParam("gcmRegistration") String regId, HttpServletResponse httpResponse)
	{
		// retrieve information about the current player	
		User currentUser = UserAuthentication.getLoggedInUser();

		if (currentUser == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}

		MultiplayerMatch match = new MultiplayerMatch();

		// check if anybody is waiting for an opponent
		Query query = Query.query(Criteria.where("state").is(MutiboMatch.STATE_WAITING).andOperator(Criteria.where("players[0]").ne(currentUser.getUserId())));
		MutiboMatch mutiboMatch = mongoTemplate.findAndModify(query, new Update().set("state", MutiboMatch.STATE_PLAYING), MutiboMatch.class);

		// --> open match available
		if (mutiboMatch != null)
		{
			// update the match information
			mutiboMatch.addSecondPlayer(currentUser.getUserId(), regId);

			// get more information abouth the first player		
			User firstPlayer = userRepository.findOne(mutiboMatch.getPlayers()[0]);

			match.opponentReady = true;
			match.opponentName  = firstPlayer.getUsername();

			// send a message to the players
			Map<String, String>	payload = new HashMap<>();
			payload.put("player_one", firstPlayer.getUsername());
			payload.put("player_two", currentUser.getUsername());
			payload.put("matchId",	  mutiboMatch.getId());
			googleCloudMessenger.sendMessage(mutiboMatch.getGcmRegIds(), "OPPONENT_READY", payload);
		}

		// --> create a new match (if necessary)
		if (mutiboMatch == null)
		{
			mutiboMatch = new MutiboMatch(currentUser.getUserId(), regId);
			mutiboMatch.setNextSet(chooseNextSet(mutiboMatch));

			match.opponentReady = false;
			match.opponentName  = null;
		}

		// save the updated match
		mutiboMatchRepository.save(mutiboMatch);

		match.matchId 		= mutiboMatch.getId();
		match.setId			= mutiboMatch.getNextSet();

		return match;
	}

	@RequestMapping(method=RequestMethod.POST, value="/multiplayer/game-cancel")
	void gameCancel(@RequestParam("matchId") String matchId, HttpServletResponse httpResponse)
	{
		// retrieve information about the current player	
		User currentUser = UserAuthentication.getLoggedInUser();

		if (currentUser == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// mark the match as cancelled
		Query query = Query.query(Criteria.where("id").is(matchId));
		MutiboMatch mutiboMatch = mongoTemplate.findAndModify(query, new Update().set("state", MutiboMatch.STATE_CANCELLED), MutiboMatch.class);

		if (mutiboMatch == null) {
			httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// notify the players of the cancellation
		Map<String, String>	payload = new HashMap<>();
		payload.put("player", 	currentUser.getUsername());
		payload.put("matchId",	matchId);
		googleCloudMessenger.sendMessage(mutiboMatch.getGcmRegIds(), "OPPONENT_QUIT", payload);
	}

	@RequestMapping(method=RequestMethod.POST, value="/multiplayer/game-update")
	Long gameUpdate(@RequestParam("matchId") String matchId, @RequestParam("setId") Long setId, @RequestParam("score") int score, HttpServletResponse httpResponse)
	{
		// retrieve information about the current player	
		User currentUser = UserAuthentication.getLoggedInUser();

		if (currentUser == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}

		// retrieve information about the match
		Query 		query = Query.query(Criteria.where("id").is(matchId));
		boolean		succeeded 		= false;
		boolean 	nextQuestion	= false;
		int			retries	 		= 0;
		MutiboMatch	mutiboMatch	= null;
		
		while (!succeeded && ++retries < 10) {

			try {
			
				mutiboMatch = mongoTemplate.findOne(query, MutiboMatch.class);

				if (mutiboMatch == null)
				{
					httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return null;
				}

				// update the match
				int playerIdx = mutiboMatch.addPlayerScore(currentUser.getUserId(), score);
		
				if (mutiboMatch.getNextSet().equals(setId))
				{
					mutiboMatch.getPlayedSets().add(setId);
					mutiboMatch.setNextSet(chooseNextSet(mutiboMatch));
				}

				if (mutiboMatch.getWaitingPlayers() == 0)
				{
					nextQuestion = true;
					mutiboMatch.setWaitingPlayers(2);
				}

				DBObject mongoObject = new BasicDBObject();
				mongoTemplate.getConverter().write(mutiboMatch, mongoObject);
				mongoTemplate.updateFirst(query, Update.fromDBObject(mongoObject), MutiboMatch.class);

				// send message to players with the score of the current player
				Map<String, String>	payload = new HashMap<>();
				payload.put("player", currentUser.getUsername());
				payload.put("score",  Integer.toString(mutiboMatch.getScore(playerIdx)));
				payload.put("lives",  Integer.toString(mutiboMatch.getLives(playerIdx)));
				payload.put("matchId",mutiboMatch.getId());
				googleCloudMessenger.sendMessage(mutiboMatch.getGcmRegIds(), "OPPONENT_SCORE", payload);
				
				// stop optimistic locking loop
				succeeded = true;
				
			} catch (OptimisticLockingFailureException e) {
				// retry
			}
		}

		// send message to all players to proceed to next set or to end the game
		if (succeeded && nextQuestion && mutiboMatch != null) {
			Map<String, String>	payload = new HashMap<>();

			payload.put("matchId",	  mutiboMatch.getId());

			if (mutiboMatch.isGameOver()) {
				payload.put("player_one", getPlayerName(mutiboMatch.getPlayers()[0]));
				payload.put("score_one",  Integer.toString(mutiboMatch.getScore(0)));
				payload.put("lives_one",  Integer.toString(mutiboMatch.getLives(0)));
				payload.put("player_two", getPlayerName(mutiboMatch.getPlayers()[1]));
				payload.put("score_two",  Integer.toString(mutiboMatch.getScore(1)));
				payload.put("lives_two",  Integer.toString(mutiboMatch.getLives(1)));
				googleCloudMessenger.sendMessage(mutiboMatch.getGcmRegIds(), "END_GAME", payload);
			} else {
				googleCloudMessenger.sendMessage(mutiboMatch.getGcmRegIds(), "CONTINUE_GAME", payload);
			}
		}

		if (succeeded && mutiboMatch != null)
			return mutiboMatch.getNextSet();
		else
			return 0L;
	}

	private Long chooseNextSet(MutiboMatch mutiboMatch)
	{
		// retrieve a list of all released decks
		List<Long> deckIds = new ArrayList<>();
		
		for (MutiboDeck deck : mutiboDeckRepository.findByReleased(true)) {
			deckIds.add(deck.getDeckId());
		}

		// retrieve a list of all sets
		List<Long> setIds = new ArrayList<>();

		Query setQuery = Query.query(Criteria.where("deckId").in(deckIds));
		for (MutiboSet set : mongoTemplate.find(setQuery, MutiboSet.class)) {
			setIds.add(set.getSetId());
		}

		// remove the already played sets
		setIds.removeAll(mutiboMatch.getPlayedSets());

		if (setIds.isEmpty())
			return 0L;

		// retrieve a random entry 
		Collections.shuffle(setIds);

		return setIds.get(0);
	}

	private String getPlayerName(ObjectId userId)
	{
		User user = userRepository.findOne(userId);

		if (user != null)
			return user.getUsername();
		else
			return "?";
	}

	private static class MultiplayerMatch
	{
		public MultiplayerMatch()
		{
		}
		
		@JsonProperty("matchId")
		private String    matchId;

		@JsonProperty("opponentReady")
		private boolean opponentReady;

		@JsonProperty("opponentName")
		private String  opponentName;

		@JsonProperty("setId")
		private Long    setId;
	};

	// member variables
	@Autowired
	MutiboMatchRepository	mutiboMatchRepository;

	@Autowired
	MutiboDeckRepository	mutiboDeckRepository;

	@Autowired
	UserRepository			userRepository;

	@Autowired
	MongoTemplate 			mongoTemplate;

	@Autowired
	GoogleCloudMessenger	googleCloudMessenger;
}
