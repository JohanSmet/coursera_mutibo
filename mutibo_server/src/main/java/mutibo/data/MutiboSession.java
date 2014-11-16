/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.data;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Results of a Mutibo Game Session
 * @author Redacted
 */

@Document(collection="sessions")
public class MutiboSession
{
	public MutiboSession()
	{
		this.isCumulated = false;
		this.totalScore  = 0;
	}

	public MutiboSession(Long userId)
	{
		this.userId 	 = userId;
		this.isCumulated = false;
		this.totalScore  = 0;
	}

	public ObjectId getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(ObjectId sessionId)
	{
		this.sessionId = sessionId;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Date getTimeBegin()
	{
		return timeBegin;
	}

	public void setTimeBegin(Date timeBegin)
	{
		this.timeBegin = timeBegin;
	}

	public Date getTimeEnd()
	{
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd)
	{
		this.timeEnd = timeEnd;
	}

	public int getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(int totalScore)
	{
		this.totalScore = totalScore;
	}

	public void addScore(int score)
	{
		this.totalScore += score;
	}

	public Boolean getIsCumulated()
	{
		return isCumulated;
	}

	public void setIsCumulated(Boolean isCumulated)
	{
		this.isCumulated = isCumulated;
	}

	// member variables
	@Id
	private ObjectId	sessionId;

	@Indexed
	private Long		userId;

	private Date		timeBegin;
	private Date		timeEnd;
	private int			totalScore;

	@Indexed
	private Boolean		isCumulated;
}