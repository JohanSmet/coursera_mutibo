package org.coursera.mutibo.data;

import java.util.Collection;

public class MutiboSync
{
    public MutiboSync()
    {
    }

    public MutiboDeck getMutiboDeck()
    {
        return mutiboDeck;
    }

    public void setMutiboDeck(MutiboDeck mutiboDeck)
    {
        this.mutiboDeck = mutiboDeck;
    }

    public Collection<MutiboMovie> getMutiboMovies()
    {
        return mutiboMovies;
    }

    public void setMutiboMovies(Collection<MutiboMovie> mutiboMovies)
    {
        this.mutiboMovies = mutiboMovies;
    }

    public Collection<MutiboSet> getMutiboSets()
    {
        return mutiboSets;
    }

    public void setMutiboSets(Collection<MutiboSet> mutiboSets)
    {
        this.mutiboSets = mutiboSets;
    }

    // member variables
    private MutiboDeck				mutiboDeck;
    private Collection<MutiboMovie> mutiboMovies;
    private Collection<MutiboSet>	mutiboSets;
}
