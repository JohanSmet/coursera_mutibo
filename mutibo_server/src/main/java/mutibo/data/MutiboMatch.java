package mutibo.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Redacted
 */

@Document(collection = "matches")
public class MutiboMatch
{
	public static final int STATE_WAITING 	= 0;
	public static final int STATE_PLAYING 	= 1;
	public static final int STATE_FINISHED 	= 2;
	public static final int STATE_CANCELLED = 3;

	public MutiboMatch()
	{
	}
	
	public MutiboMatch(ObjectId playerOne, String gcmRegId)
	{
		players 		= new ObjectId[] {playerOne, null};
		gcmRegIds		= new String[] {gcmRegId, null};
		score			= new int[] {0,0};
		lives			= new int[] {3,3};
		nextSet			= 0L;
		playedSets		= new ArrayList<>();
		state			= STATE_WAITING;
		waitingPlayers  = 2;
	}

	public void addSecondPlayer(ObjectId playerTwo, String gcmRegId)
	{
		players[1]		= playerTwo;
		gcmRegIds[1]	= gcmRegId;
		state			= STATE_PLAYING;
	}

	public int addPlayerScore(ObjectId player, int playerScore)
	{
		int playerIdx = playerIndex(player);

		if (playerIdx < 0)
			return playerIdx;

		// save score of this player
		score[playerIdx] += playerScore;
		--waitingPlayers;
		
		// decrement remaining lives if player didn't score on this question
		if (playerScore <= 0)
			--lives[playerIdx];

		// game ends when a player has no more lives left
		if (lives[playerIdx] <= 0)
			state = STATE_FINISHED;

		return playerIdx;
	}

	public boolean isGameOver()
	{
		return state == STATE_FINISHED;
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

	@JsonIgnore
	public Long getVersion()
	{
		return version;
	}

	@JsonIgnore
	public void setVersion(Long version)
	{
		this.version = version;
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

	public int getScore(int player)
	{
		return score[player];
	}

	public void setScore(int[] score)
	{
		this.score = score;
	}

	public int[] getLives()
	{
		return lives;
	}

	public int getLives(int player)
	{
		return lives[player];
	}

	public void setLives(int[] lives)
	{
		this.lives = lives;
	}

	public Long getNextSet()
	{
		return nextSet;
	}

	public void setNextSet(Long nextSet)
	{
		this.nextSet = nextSet;
	}

	public List<Long> getPlayedSets()
	{
		return playedSets;
	}

	public void setPlayedSets(List<Long> playedSets)
	{
		this.playedSets = playedSets;
	}

	public String[] getGcmRegIds()
	{
		return gcmRegIds;
	}

	public void setGcmRegIds(String[] gcmRegIds)
	{
		this.gcmRegIds = gcmRegIds;
	}

	public int getWaitingPlayers()
	{
		return waitingPlayers;
	}

	public void setWaitingPlayers(int waitingPlayers)
	{
		this.waitingPlayers = waitingPlayers;
	}

	private int playerIndex(ObjectId player)
	{
		for (int idx=0; idx < players.length; ++idx)
		{
			if (players[idx].equals(player))
				return idx;
		}

		return -1;
	}

	// member variables
	@Id
	private ObjectId		id;

	@Version
	private Long			version;

	@Indexed
	private int				state;
	private ObjectId[]		players;
	private String[]		gcmRegIds;
	private int[]			score;
	private int[]			lives;
	private Long			nextSet;
	private List<Long>		playedSets;
	private int				waitingPlayers;
}
