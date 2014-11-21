package mutibo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.MutiboSession;
import mutibo.data.MutiboSetResult;
import mutibo.data.MutiboUserResult;
import mutibo.data.User;
import mutibo.repository.MutiboSessionRepository;
import mutibo.repository.MutiboSetResultRepository;
import mutibo.repository.MutiboUserResultRepository;
import mutibo.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
		MutiboSession session = new MutiboSession(currentUser.getUserId());
		session.setTimeBegin(gameResults.startTime);
		session.setTimeEnd(gameResults.endTime);
		
		// process each played set
		for (SetResult set : gameResults.setResults)
		{
			MutiboSetResult  setResult = new MutiboSetResult(session.getSessionId(), set.setId, currentUser.getUserId());
			setResult.setMsTimePlayed(set.msTimePlayed);
			setResult.setScore(set.score);
			setResult.setRating(set.rating);
			session.addScore(set.score);
			mutiboSetResultsRepository.save(setResult);
		}

		// save the session
		mutiboSessionRepository.save(session);
	}

	@RequestMapping(method=RequestMethod.GET, value="/game/leaderboard")
	List<MutiboUserResult> getLeaderboard(@RequestParam("from") int from, @RequestParam("count") int count, HttpServletResponse httpResponse)
	{
		httpResponse.setHeader("Cache-Control", "max-age=315360000");
		return mutiboUserResultRepository.findByRankingBetweenOrderByRankingAsc(from - 1, from + count);
	}

	@RequestMapping(method=RequestMethod.GET, value="/game/leaderboard-player")
	List<MutiboUserResult> getLeaderboardPlayer(@RequestParam("player") String nickName, @RequestParam("count") int count, HttpServletResponse httpResponse)
	{
		MutiboUserResult userResult = mutiboUserResultRepository.findByNickName(nickName);

		if (userResult == null)
			return null;
		
		int from = userResult.getRanking() - (count / 2);
		if (from <= 0)
			from = 1;

		httpResponse.setHeader("Cache-Control", "max-age=315360000");
		return mutiboUserResultRepository.findByRankingBetweenOrderByRankingAsc(from, from+count);
	}

	@RequestMapping(method=RequestMethod.GET, value="/game/score")
	MutiboUserResult getScore(HttpServletResponse httpResponse)
	{
		// get the currently logged in user
		User currentUser = UserAuthentication.getLoggedInUser();

		if (currentUser == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}

		return mutiboUserResultRepository.findOne(currentUser.getUserId());
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

	@Autowired
	private MutiboUserResultRepository mutiboUserResultRepository;
}
