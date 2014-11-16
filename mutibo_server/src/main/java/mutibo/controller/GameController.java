/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboSession;
import mutibo.data.MutiboSetResult;
import mutibo.data.User;
import mutibo.repository.MutiboSessionRepository;
import mutibo.repository.MutiboSetResultRepository;
import mutibo.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Redacted
 */

@RestController
public class GameController
{
	@RequestMapping(method=RequestMethod.POST, value="/game/results")
	void postGameResults(@RequestBody GameResults gameResults, HttpServletResponse httpResponse)
	{
		// get the currently logged in user
		User currentUser = UserAuthentication.getLoggedInUser();

		if (currentUser == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// create the MutiboSession-record
		MutiboSession session = new MutiboSession(currentUser.getId());
		session.setTimeBegin(gameResults.startTime);
		session.setTimeEnd(gameResults.endTime);
		
		// process each played set
		for (SetResult set : gameResults.setResults)
		{
			MutiboSetResult  setResult = new MutiboSetResult(session.getSessionId(), set.setId, currentUser.getId());
			setResult.setMsTimePlayed(set.msTimePlayed);
			setResult.setScore(set.score);
			setResult.setRating(set.rating);
			session.addScore(set.score);
			mutiboSetResultsRepository.save(setResult);
		}

		// save the session
		mutiboSessionRepository.save(session);
	}

	// nested types
	public static class GameResults 
	{
		public GameResults()
		{
		}

		@JsonProperty("startTime")
		Date			startTime;

		@JsonProperty("endTime")
		Date			endTime;

		@JsonProperty("setResults")
		List<SetResult>	setResults;
	}

	public static class SetResult
	{
		public SetResult()
		{
		}

		@JsonProperty("setId")
		Long	setId;
		
		@JsonProperty("msTimePlayed")
		long	msTimePlayed;

		@JsonProperty("score")
		int		score;

		@JsonProperty("rating")
		int		rating;
	}

	// member variables
	@Autowired
	private MutiboSessionRepository mutiboSessionRepository;

	@Autowired
	private MutiboSetResultRepository mutiboSetResultsRepository;
}
