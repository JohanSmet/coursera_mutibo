package org.coursera.mutibo.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataStore
{
    private DataStore()
    {
        this.mDataLock = new ReentrantLock();
        this.mMovies = new HashMap<String, MutiboMovie>();
        this.mSets = new HashMap<Long, MutiboSet>();
        this.mDecks = new HashMap<Long, MutiboDeck>();
    }

    public void addDeck(MutiboDeck newDeck)
    {
        mDataLock.lock();
        try {
            this.mDecks.put(newDeck.getDeckId(), newDeck);
        } finally {
            mDataLock.unlock();
        }
    }

    public void addSets(Collection<MutiboSet> newSets)
    {
        mDataLock.lock();
        try {
            for (MutiboSet f_set : newSets)
            {
                this.mSets.put(f_set.getSetId(), f_set);
            }
        } finally {
            mDataLock.unlock();
        }
    }

    public int countSets()
    {
        return this.mSets.size();
    }

    public Set<Long> listSetKeys()
    {
        return this.mSets.keySet();
    }

    public MutiboSet getSetById(Long id)
    {
        return this.mSets.get(id);
    }

    public void addMovies(Collection<MutiboMovie> newMovies)
    {
        mDataLock.lock();
        try {
            for (MutiboMovie f_movie : newMovies)
            {
               mMovies.put(f_movie.getImdbId(), f_movie);
            }
        } finally {
            mDataLock.unlock();
        }
    }

    public MutiboMovie getMovieById(String id)
    {
        return mMovies.get(id);
    }

    public static DataStore getInstance()
    {
        if (instance == null)
        {
            instance = new DataStore();
        }

        return instance;
    }

    // member variables
    static DataStore                instance = null;

    Lock                            mDataLock;

    HashMap<Long,   MutiboDeck>     mDecks;
    HashMap<Long,   MutiboSet>      mSets;
    HashMap<String, MutiboMovie>    mMovies;
}
