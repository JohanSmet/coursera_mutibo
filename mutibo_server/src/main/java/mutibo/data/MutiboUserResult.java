/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.data;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Redacted
 */

@Document(collection = "user_results")
public class MutiboUserResult
{
	public MutiboUserResult()
	{
	}

	public MutiboUserResult(Long id)
	{
		this.id = id;
		this.totalScore  = 0;
		this.bestScore   = 0;
		this.playedGames = 0;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getDateRegistered()
	{
		return dateRegistered;
	}

	public void setDateRegistered(Date dateRegistered)
	{
		this.dateRegistered = dateRegistered;
	}

	public Date getDateLastPlayed()
	{
		return dateLastPlayed;
	}

	public void setDateLastPlayed(Date dateLastPlayed)
	{
		this.dateLastPlayed = dateLastPlayed;
	}

	public long getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(long totalScore)
	{
		this.totalScore = totalScore;
	}

	public int getBestScore()
	{
		return bestScore;
	}

	public void setBestScore(int bestScore)
	{
		this.bestScore = bestScore;
	}

	public long getPlayedGames()
	{
		return playedGames;
	}

	public void setPlayedGames(long playedGames)
	{
		this.playedGames = playedGames;
	}

	public void addGameScore(int score)
	{
		++this.playedGames;
		this.totalScore += score;
		this.bestScore = Math.max(score, this.bestScore);
	}

	// member variables
	@Id
	private Long	id;

	private Date	dateRegistered;
	private Date	dateLastPlayed;

	private long	totalScore;
	private int		bestScore;
	private long	playedGames;
}
