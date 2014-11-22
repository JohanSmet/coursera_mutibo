package org.coursera.mutibo.data;

public class MutiboUserResult
{
    public MutiboUserResult()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
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

    public int getRanking()
    {
        return ranking;
    }

    public void setRanking(int ranking)
    {
        this.ranking = ranking;
    }

    // member variables
    private String	id;

    private String	nickName;

    private long	totalScore;
    private int		bestScore;
    private long	playedGames;
    private int 	ranking;
}
