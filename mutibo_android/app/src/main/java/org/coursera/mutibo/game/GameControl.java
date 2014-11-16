package org.coursera.mutibo.game;

import org.coursera.mutibo.data.MutiboGameResult;
import org.coursera.mutibo.data.MutiboMovie;

public interface GameControl
{
    public static final int GAME_STATE_STARTED  = 0;
    public static final int GAME_STATE_QUESTION = 1;
    public static final int GAME_STATE_ANSWERED = 2;
    public static final int GAME_STATE_FINISHED = 3;

    public enum SetSuccess
    {
        UNKNOWN,
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    public void     startGame();
    public void     endGame();
    public boolean  answerSet(int index);
    public void     timeoutSet();
    public void     continueGame(int rating);
    public int      currentGameState();

    public int      totalScore();
    public int      numCorrectQuestions();
    public int      remainingLives();

    public MutiboGameResult gameResult();

    public int         currentSetDifficulty();
    public int         currentSetPoints();
    public MutiboMovie currentSetMovie(int index);
    public String      currentSetReason();
    public SetSuccess  currentSetSuccess();

}
