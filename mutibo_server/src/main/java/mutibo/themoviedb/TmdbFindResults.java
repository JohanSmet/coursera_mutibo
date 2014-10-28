package mutibo.themoviedb;

import java.util.Collection;

/**
 *
 * @author Redacted
 */
public class TmdbFindResults
{
	public TmdbFindResults()
	{
	}

	public Collection<TmdbSearchMovie> getMovie_results()
	{
		return movie_results;
	}

	public void setMovie_results(Collection<TmdbSearchMovie> movie_results)
	{
		this.movie_results = movie_results;
	}
	
	private Collection<TmdbSearchMovie>	movie_results;
}
