package org.coursera.mutibo.game;

import org.coursera.mutibo.data.MutiboMovie;

public interface GameControl
{
    public static final int GAME_STATE_STARTED  = 0;
    public static final int GAME_STATE_QUESTION = 1;
    public static final int GAME_STATE_ANSWERED = 2;
    public static final int GAME_STATE_FINISHED = 3;

    public void     startGame();
    public boolean  answerSet(int index);
    public void     continueGame();
    public int      currentGameState();

    public int      totalScore();
    public int      numCorrectQuestions();

    public int  remainingLives();

    public int         currentSetDifficulty();
    public int         currentSetPoints();
    public MutiboMovie currentSetMovie(int index);
    public String      currentSetReason();
    public boolean     currentSetSuccess();

}
