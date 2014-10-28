package mutibo.themoviedb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.annotation.Resource;
import mutibo.data.MutiboMovie;
import org.springframework.core.env.Environment;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * @author Redacted
 */

public class TmdbApi
{
	public TmdbApi(String tmdbHost, String tmdbKey)
	{
		Gson gson = new GsonBuilder()
						.setDateFormat("yyyy-MM-dd")
						.create();
		
		restAdapter = new RestAdapter.Builder()
							.setEndpoint(tmdbHost)
							.setConverter(new GsonConverter(gson))
							.build();
		tmdbApiClient = restAdapter.create(TmdbApiClient.class);
		apiKey = tmdbKey;
	}

	public MutiboMovie findByImdbId(String imdbId)
	{
		TmdbFindResults f_results = tmdbApiClient.findByImdbId(imdbId, apiKey);
		
		// IMDB-results should be unique
		if (f_results.getMovie_results().size() != 1)
		{
			return null;
		}

		// work with the first movie
		TmdbSearchMovie f_movie = (TmdbSearchMovie) f_results.getMovie_results().toArray()[0];

		// retrieve more information about the movie
		TmdbMovie f_details = tmdbApiClient.findById(f_movie.getId(), apiKey);

		String	  f_plot = "";

		if (f_details != null)
			f_plot = f_details.getOverview();
		
		return new MutiboMovie(imdbId, f_movie.getTitle(), f_movie.getRelease_year(), f_plot);
	}

	// member variables
	private final RestAdapter		restAdapter;
	private final TmdbApiClient		tmdbApiClient;
	private final String			apiKey;

	@Resource
	private Environment environment;
	
}
