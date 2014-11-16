/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * SET data element
 * @author Redacted
 */

@Document(collection = "sets")
public class MutiboSet
{
	/**
	 * Default constructor
	 */
	public MutiboSet()
	{
	}

	public MutiboSet(Long setId, Long deckId, String[] goodMovies, String[] badMovies, String reason, int points, int difficulty, int ratingTotal, int ratingCount)
	{
		this.setId = setId;
		this.deckId = deckId;
		this.goodMovies = goodMovies;
		this.badMovies = badMovies;
		this.reason = reason;
		this.points = points;
		this.difficulty = difficulty;
		this.ratingTotal = ratingTotal;
		this.ratingCount = ratingCount;
	}

	public Long getSetId()
	{
		return setId;
	}

	public void setSetId(Long setId)
	{
		this.setId = setId;
	}

	public Long getDeckId()
	{
		return deckId;
	}

	public void setDeckId(Long deckId)
	{
		this.deckId = deckId;
	}

	public String[] getGoodMovies()
	{
		return goodMovies;
	}

	public void setGoodMovies(String[] goodMovies)
	{
		this.goodMovies = goodMovies;
	}

	public String[] getBadMovies()
	{
		return badMovies;
	}

	public void setBadMovies(String[] badMovies)
	{
		this.badMovies = badMovies;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public int getPoints()
	{
		return points;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public int getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(int difficulty)
	{
		this.difficulty = difficulty;
	}

	public int getRatingTotal()
	{
		return ratingTotal;
	}

	public void setRatingTotal(int ratingTotal)
	{
		this.ratingTotal = ratingTotal;
	}

	public int getRatingCount()
	{
		return ratingCount;
	}

	public void setRatingCount(int ratingCount)
	{
		this.ratingCount = ratingCount;
	}

	public void addRating(int rating)
	{
		++this.ratingCount;
		this.ratingTotal += rating;
	}

	// member variables
	@Id
	private Long setId;
	private Long deckId;
	private String[] goodMovies;
	private String[] badMovies;
	private String reason;
	private int points;
	private int difficulty;
	private int ratingTotal;
	private int ratingCount;
}
