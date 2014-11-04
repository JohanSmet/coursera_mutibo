package org.coursera.mutibo.data;

public class MutiboSet
{
    public MutiboSet()
    {
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

    // member variables
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
