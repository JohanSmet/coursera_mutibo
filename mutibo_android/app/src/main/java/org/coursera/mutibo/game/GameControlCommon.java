package org.coursera.mutibo.game;

import android.os.Bundle;
import android.os.ResultReceiver;

public abstract class GameControlCommon implements GameControl
{
    public GameControlCommon(int playerCount)
    {
        this.mState       = GAME_STATE_FINISHED;

        this.mPlayerScore = new PlayerScore[playerCount];

        for (int idx = 0; idx < playerCount; ++idx)
            this.mPlayerScore[idx] = new PlayerScore();
    }

    //
    // state management
    //

    @Override
    public void registerStateCallback(ResultReceiver receiver)
    {
        stateCallback = receiver;
    }

    @Override
    public int currentGameState()
    {
        return mState;
    }

    protected void changeGameState(int newState)
    {
        changeGameState(newState, null);
    }

    protected void changeGameState(int newState, Bundle extras)
    {
        if (this.mState == newState)
            return;

        this.mState = newState;

        if (stateCallback != null)
            stateCallback.send(newState, extras);
    }

    //
    // events
    //

    protected void sendEvent(int eventCode)
    {
        sendEvent(eventCode, (Bundle) null);
    }

    protected void sendEvent(int eventCode, Bundle eventData)
    {
        if (stateCallback != null)
            stateCallback.send(eventCode, eventData);
    }

    //
    // player score
    //

    protected PlayerScore playerScoreByName(String playerName)
    {
        for (PlayerScore player : mPlayerScore)
        {
            if (player.mPlayerName.equals(playerName))
                return player;
        }

        return null;
    }

    public int playerCount()
    {
        return this.mPlayerScore.length;
    }

    public PlayerScore playerScore(int idx)
    {
        if (idx < 0 || idx >= this.mPlayerScore.length)
            return null;

        return this.mPlayerScore[idx];
    }

    protected void updatePlayerScore(String playerName, int score, int lives, int correct)
    {
        PlayerScore playerScore = playerScoreByName(playerName);

        playerScore.mScore    = score;
        playerScore.mLives    = lives;
        playerScore.mCorrect  = correct;
        playerScore.mUpToDate = true;
    }

    protected void resetPlayerScores()
    {
        for (PlayerScore player : mPlayerScore)
            player.mUpToDate = false;
    }


    //
    // member variables
    //

    protected ResultReceiver    stateCallback;
    private int                 mState;

    protected PlayerScore[]     mPlayerScore;
}
