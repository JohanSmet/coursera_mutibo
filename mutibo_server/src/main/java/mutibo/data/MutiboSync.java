package mutibo.data;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import mutibo.HashUtils;
import mutibo.repository.MutiboMovieRepository;
import mutibo.repository.MutiboSetRepository;

/**
 * @author Redacted
 */

public class MutiboSync
{
	public MutiboSync(MutiboDeck mutiboDeck)
	{
		this.mutiboDeck 	= mutiboDeck;
		this.mutiboMovies	= new ArrayList<>();
		this.mutiboSets		= new ArrayList<>();
	}

	public MutiboDeck getMutiboDeck()
	{
		return mutiboDeck;
	}

	public Collection<MutiboMovie> getMutiboMovies()
	{
		return mutiboMovies;
	}

	public Collection<MutiboSet> getMutiboSets()
	{
		return mutiboSets;
	}
		
	public void addMutiboMovie(MutiboMovie movie)
	{
		if (!mutiboMovies.contains(movie))
			mutiboMovies.add(movie);
	}

	public void addMutiboSet(MutiboSet set)
	{
		if (!mutiboSets.contains(set))
			mutiboSets.add(set);
	}

	public void fill(MutiboMovieRepository movieRepository, MutiboSetRepository setRepository)
	{
		for (MutiboSet f_set : setRepository.findByDeckId(mutiboDeck.getDeckId()))
		{
			addMutiboSet(f_set);

			for (String f_movie : f_set.getGoodMovies())
			{
				addMutiboMovie(movieRepository.findOne(f_movie));
			}

			for (String f_movie : f_set.getBadMovies())
			{
				addMutiboMovie(movieRepository.findOne(f_movie));
			}
		}
	}

	public String computeHash()
	{
		Gson gson = new Gson();
		return HashUtils.MD5(gson.toJson(this));
	}

	// member variables
	private final MutiboDeck				mutiboDeck;
	private final Collection<MutiboMovie>	mutiboMovies;
	private final Collection<MutiboSet>		mutiboSets;
}
