package org.coursera.mutibo.data;

public class MutiboSetResult
{
    public MutiboSetResult()
    {
        this.score  = 0;
        this.rating = 0;
    }

    public MutiboSetResult(Long setId)
    {
        this.setId  = setId;
        this.score  = 0;
        this.rating = 0;
    }

    public Long getSetId()
    {
        return setId;
    }

    public void setSetId(Long setId)
    {
        this.setId = setId;
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

    // member variables
    private Long	setId;
    private long	msTimePlayed;
    private int		score;
    private int		rating;
}
