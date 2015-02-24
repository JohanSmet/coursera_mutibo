package org.coursera.mutibo.game;

import android.os.Bundle;
import android.os.ResultReceiver;

public abstract class GameControlCommon implements GameControl
{
    public GameControlCommon()
    {
        this.mState      = GAME_STATE_FINISHED;
    }

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
    // member variables
    //

    protected ResultReceiver    stateCallback;

    private int                 mState;
}
