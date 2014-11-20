package mutibo.data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Results of a played Mutibo Set
 * @author Redacted
 */

@Document(collection="set_results")
public class MutiboSetResult
{
	public MutiboSetResult()
	{
		this.isCumulated = false;
	}

	public MutiboSetResult(ObjectId sessionId, Long setId, ObjectId userId)
	{
		this.sessionId = sessionId;
		this.setId 	   = setId;
		this.userId    = userId;

		this.isCumulated = false;
	}

	public ObjectId getId()
	{
		return id;
	}

	public void setId(ObjectId id)
	{
		this.id = id;
	}

	public ObjectId getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(ObjectId sessionId)
	{
		this.sessionId = sessionId;
	}

	public Long getSetId()
	{
		return setId;
	}

	public void setSetId(Long setId)
	{
		this.setId = setId;
	}

	public ObjectId getUserId()
	{
		return userId;
	}

	public void setUserId(ObjectId userId)
	{
		this.userId = userId;
	}

	public long getMsTimePlayed()
	{
		return msTimePlayed;
	}

	public void setMsTimePlayed(long msTimePlayed)
	{
		this.msTimePlayed = msTimePlayed;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getRating()
	{
		return rating;
	}

	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public boolean isIsCumulated()
	{
		return isCumulated;
	}

	public void setIsCumulated(boolean isCumulated)
	{
		this.isCumulated = isCumulated;
	}

	// member variables
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId sessionId;

	private Long 	 setId;
	private ObjectId userId;
	private long	 msTimePlayed;
	private int		 score;
	private int		 rating;

	@Indexed
	private boolean	 isCumulated;
}
