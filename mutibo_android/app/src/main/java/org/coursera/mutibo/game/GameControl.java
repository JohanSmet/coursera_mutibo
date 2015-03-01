package org.coursera.mutibo.game;

import android.os.ResultReceiver;

import org.coursera.mutibo.data.MutiboGameResult;
import org.coursera.mutibo.data.MutiboMovie;

public interface GameControl
{
    public static final int GAME_STATE_AWAITING_OPPONENT    = 0;
    public static final int GAME_STATE_STARTED              = 1;
    public static final int GAME_STATE_QUESTION             = 2;
    public static final int GAME_STATE_ANSWERED             = 3;
    public static final int GAME_STATE_FINISHED             = 5;
    public static final int GAME_STATE_CANCELLED            = 6;

    public static final int GAME_EVENT_QUESTION_COUNTDOWN   = 101;
    public static final int GAME_EVENT_SCORE_UPDATE         = 102;

    public static final int PLAYER_ONE                      = 0;
    public static final int PLAYER_TWO                      = 1;


    public enum SetSuccess
    {
        UNKNOWN,
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    public class PlayerScore
    {
        public String      mPlayerName;
        public int         mScore;
        public int         mCorrect;
        public int         mLives;
        public boolean     mUpToDate;
    }

    public boolean  isMultiPlayer();

    public void     startGame();
    public void     endGame();
    public void     cancelGame();
    public boolean  answerSet(int index);
    public void     timeoutSet();
    public void     continueGame(int rating);
    public int      currentGameState();
    public int      playerAnswer();

    public int      totalScore();
    public int      numCorrectQuestions();
    public int      remainingLives();

    public PlayerScore playerScore(int idx);
    public int         playerCount();

    public int         currentSetDifficulty();
    public int         currentSetPoints();
    public int         currentSetCorrectAnswer();
    public MutiboMovie currentSetMovie(int index);
    public String      currentSetReason();
    public SetSuccess  currentSetSuccess();

    public void        registerStateCallback(ResultReceiver receiver);

}
