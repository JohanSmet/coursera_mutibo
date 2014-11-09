package org.coursera.mutibo.game;

import org.coursera.mutibo.data.DataStore;
import org.coursera.mutibo.data.MutiboMovie;
import org.coursera.mutibo.data.MutiboSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class GameControlSingle implements GameControl
{
    public GameControlSingle()
    {
        this.mScore      = 0;
        this.mNumCorrect = 0;
        this.mState      = GAME_STATE_FINISHED;
        this.mPlayedSets = new HashSet<Long>();
        this.mSetSeed    = null;
        this.mSetMovies  = new ArrayList<MutiboMovie>();
        this.mSuccess    = SetSuccess.UNKNOWN;
    }

    @Override
    public void startGame()
    {
        this.mScore      = 0;
        this.mNumCorrect = 0;
        this.mLives      = 3;
        this.mState      = GAME_STATE_STARTED;

        this.mPlayedSets.clear();
        this.mSetSeed = new Random();

        chooseNextSet();
    }

    public boolean answerSet(int index)
    {
        // check for success
        boolean correctGuess = false;

        for (String f_bad :  mCurrentSet.getBadMovies())
        {
            correctGuess = correctGuess || (f_bad.equals(mSetMovies.get(index).getImdbId()));
        }

        this.mSuccess = (correctGuess) ? SetSuccess.SUCCESS : SetSuccess.FAILURE;

        // consequences of a guess
        if (this.mSuccess != SetSuccess.SUCCESS)
        {
            --this.mLives;
        }
        else
        {
            ++this.mNumCorrect;
            this.mScore +=  this.mCurrentSet.getPoints();
        }

        // change state of the game
        updateGameState();

        return this.mSuccess == SetSuccess.SUCCESS;
    }

    @Override
    public void timeoutSet()
    {
        --this.mLives;
        this.mSuccess = SetSuccess.TIMEOUT;

        updateGameState();
    }

    @Override
    public void continueGame()
    {
        if (this.mState == GAME_STATE_ANSWERED)
            chooseNextSet();
    }

    @Override
    public int currentGameState()
    {
        return mState;
    }

    @Override
    public int  totalScore()
    {
        return this.mScore;
    }

    @Override
    public int  numCorrectQuestions()
    {
        return this.mNumCorrect;
    }

    @Override
    public int remainingLives()
    {
        return mLives;
    }

    @Override
    public int currentSetDifficulty()
    {
        if (mCurrentSet != null)
            return mCurrentSet.getDifficulty();
        else
            return 0;
    }

    @Override
    public int currentSetPoints()
    {
        if (mCurrentSet != null)
            return mCurrentSet.getPoints();
        else
            return 0;
    }

    @Override
    public MutiboMovie currentSetMovie(int index)
    {
        if (index >= 0 && index < mSetMovies.size())
            return mSetMovies.get(index);
        else
            return null;
    }

    @Override
    public String currentSetReason()
    {
        if (mCurrentSet != null)
            return mCurrentSet.getReason();
        else
            return null;
    }

    @Override
    public SetSuccess currentSetSuccess()
    {
        return this.mSuccess;
    }

    private void chooseNextSet()
    {
        // don't try if all sets have been played
        if (this.mPlayedSets.size() == mDataStore.countSets())
            return;

        // build a list of available sets (data can be added in the background - XXX an optimization would be to do this only when the data has changed)
        HashSet<Long> f_sets = new HashSet<Long>(mDataStore.listSetKeys());
        f_sets.removeAll(mPlayedSets);

        // choose a random set from the data store (XXX factor in ratings)
        int f_index = mSetSeed.nextInt(f_sets.size());
        mCurrentSet = mDataStore.getSetById((Long) f_sets.toArray()[f_index]);

        // put the movies of the set in a random order
        mSetMovies.clear();

        ArrayList<String> f_movies = new ArrayList<String>();
        Collections.addAll(f_movies, mCurrentSet.getGoodMovies());
        Collections.addAll(f_movies, mCurrentSet.getBadMovies());
        Collections.shuffle(f_movies);

        for (String imdbId : f_movies)
            mSetMovies.add(mDataStore.getMovieById(imdbId));

        mPlayedSets.add(mCurrentSet.getSetId());
        mState = GAME_STATE_QUESTION;
    }

    private void updateGameState()
    {
        if (this.mLives == 0 || mPlayedSets.size() == mDataStore.countSets())
            this.mState = GAME_STATE_FINISHED;
        else
            this.mState = GAME_STATE_ANSWERED;
    }

    // member variables
    private int                     mScore;
    private int                     mNumCorrect;
    private int                     mLives;
    private int                     mState;

    private MutiboSet               mCurrentSet;
    private HashSet<Long>           mPlayedSets;
    private Random                  mSetSeed;
    private ArrayList<MutiboMovie>  mSetMovies;
    private SetSuccess              mSuccess;

    private DataStore               mDataStore = DataStore.getInstance();
}
