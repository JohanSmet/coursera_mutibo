/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Redacted
 */

@Document(collection = "matches")
public class MutiboMatch
{
	public static final int STATE_WAITING = 0;
	public static final int STATE_PLAYING = 1;
	public static final int STATE_FINISHED = 2;

	public MutiboMatch()
	{
		messageCounter	= 0;
	}
	
	public MutiboMatch(ObjectId playerOne, String gcmRegId)
	{
		players 		= new ObjectId[] {playerOne, null};
		gcmRegIds		= new String[] {gcmRegId, null};
		score			= new int[] {0,0};
		nextSet			= chooseNextSet();
		state			= STATE_WAITING;
		messageCounter	= 0;
	}

	public void addSecondPlayer(ObjectId playerTwo, String gcmRegId)
	{
		players[1]		= playerTwo;
		gcmRegIds[1]	= gcmRegId;
		state			= STATE_PLAYING;
	}

	@JsonIgnore
	public ObjectId getMatchId()
	{
		return id;
	}

	@JsonIgnore
	public void setId(ObjectId id)
	{
		this.id = id;
	}

	@JsonProperty("id")
	public String getId()
	{
		return id.toString();
	}

	@JsonProperty("id")
	public void setId(String id)
	{
		this.id = new ObjectId(id);
	}

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public ObjectId[] getPlayers()
	{
		return players;
	}

	public void setPlayers(ObjectId[] players)
	{
		this.players = players;
	}

	public int[] getScore()
	{
		return score;
	}

	public void setScore(int[] score)
	{
		this.score = score;
	}

	public Long getNextSet()
	{
		return nextSet;
	}

	public void setNextSet(Long nextSet)
	{
		this.nextSet = nextSet;
	}

	public Long[] getPlayedSets()
	{
		return playedSets;
	}

	public void setPlayedSets(Long[] playedSets)
	{
		this.playedSets = playedSets;
	}

	private Long chooseNextSet()
	{
		return 101L;
	}

	public String[] getGcmRegIds()
	{
		return gcmRegIds;
	}

	public void setGcmRegIds(String[] gcmRegIds)
	{
		this.gcmRegIds = gcmRegIds;
	}

	public int getMessageCounter()
	{
		return messageCounter;
	}

	public String getUniqueId()
	{
		++messageCounter;
		return Integer.toString(messageCounter);
	}

	public void setMessageCounter(int messageCounter)
	{
		this.messageCounter = messageCounter;
	}

	// member variables
	@Id
	private ObjectId		id;

	@Indexed
	private int				state;
	private ObjectId[]		players;
	private String[]		gcmRegIds;
	private int[]			score;
	private Long			nextSet;
	private Long[]			playedSets;
	private int				messageCounter;
}
