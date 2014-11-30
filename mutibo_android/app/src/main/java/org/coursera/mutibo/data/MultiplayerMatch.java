package org.coursera.mutibo.data;

public class MultiplayerMatch
{
    public MultiplayerMatch()
    {
    }

    public String getMatchId()
    {
        return matchId;
    }

    public void setMatchId(String matchId)
    {
        this.matchId = matchId;
    }

    public boolean isOpponentReady()
    {
        return opponentReady;
    }

    public void setOpponentReady(boolean opponentReady)
    {
        this.opponentReady = opponentReady;
    }

    public String getOpponentName()
    {
        return opponentName;
    }

    public void setOpponentName(String opponentName)
    {
        this.opponentName = opponentName;
    }

    public Long getSetId()
    {
        return setId;
    }

    public void setSetId(Long setId)
    {
        this.setId = setId;
    }

    // member variables
    private String matchId;
    private boolean opponentReady;
    private String  opponentName;
    private Long    setId;
}
