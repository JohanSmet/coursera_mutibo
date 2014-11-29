/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboMatch;
import mutibo.data.User;
import mutibo.google.GoogleCloudMessenger;
import mutibo.repository.MutiboMatchRepository;
import mutibo.repository.UserRepository;
import mutibo.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
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
		Query query = Query.query(Criteria.where("state").is(MutiboMatch.STATE_WAITING));
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
			payload.put("message_id", mutiboMatch.getUniqueId());
			googleCloudMessenger.sendMessage(mutiboMatch.getGcmRegIds(), "OPPONENT_READY", payload);
		}

		// --> create a new match (if necessary)
		if (mutiboMatch == null)
		{
			mutiboMatch = new MutiboMatch(currentUser.getUserId(), regId);

			match.opponentReady = false;
			match.opponentName  = null;
		}

		// save the updated match
		mutiboMatchRepository.save(mutiboMatch);

		match.matchId 		= mutiboMatch.getId();
		match.setId			= mutiboMatch.getNextSet();

		return match;
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
	UserRepository			userRepository;

	@Autowired
	MongoTemplate 			mongoTemplate;

	@Autowired
	GoogleCloudMessenger	googleCloudMessenger;
}
