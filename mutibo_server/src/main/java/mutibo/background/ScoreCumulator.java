package mutibo.background;

import mutibo.data.MutiboSession;
import mutibo.data.MutiboSet;
import mutibo.data.MutiboSetResult;
import mutibo.data.MutiboUserResult;
import mutibo.repository.MutiboSessionRepository;
import mutibo.repository.MutiboSetRepository;
import mutibo.repository.MutiboSetResultRepository;
import mutibo.repository.MutiboUserResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Redacted
 */

@Component
public class ScoreCumulator
{
	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void cumulateUserScore()
	{
		// iterate over all new played sessions
		for (MutiboSession session : mutiboSessionRepository.findByIsCumulated(Boolean.FALSE))
		{
			// fetch the corresponding user-results record
			MutiboUserResult userResult = mutiboUserResultRepository.findOne(session.getUserId());

			if (userResult == null)
			{
				// new player - just create a new record
				userResult = new MutiboUserResult(session.getUserId());
				userResult.setDateRegistered(session.getTimeBegin());
			}
			
			// add the score to the user-results record
			userResult.addGameScore(session.getTotalScore());
			userResult.setDateLastPlayed(session.getTimeBegin());

			// store the user result
			mutiboUserResultRepository.save(userResult);

			// flag the session
			session.setIsCumulated(Boolean.TRUE);
			mutiboSessionRepository.save(session);
		}
	}

	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void cumulateSetScore()
	{
		// iterate over all new played sets
		for (MutiboSetResult setResult : mutiboSetResultRepository.findByIsCumulated(Boolean.FALSE))
		{
			// fetch the corresponding set
			MutiboSet set = mutiboSetRepository.findOne(setResult.getSetId());

			if (set != null && setResult.getRating() > 0)
			{
				set.addRating(setResult.getRating());
				mutiboSetRepository.save(set);
			}
				
			// flag the set-result
			setResult.setIsCumulated(Boolean.TRUE);
			mutiboSetResultRepository.save(setResult);
		}
	}

	@Autowired
	private MutiboSessionRepository mutiboSessionRepository;

	@Autowired
	private MutiboUserResultRepository mutiboUserResultRepository;

	@Autowired
	private MutiboSetRepository mutiboSetRepository;

	@Autowired
	private MutiboSetResultRepository mutiboSetResultRepository;

}
