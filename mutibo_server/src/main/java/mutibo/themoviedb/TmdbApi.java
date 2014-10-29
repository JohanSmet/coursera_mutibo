package mutibo.themoviedb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
						.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() 
							{
        						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

								@Override
								public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
										throws JsonParseException {
									try {
										return df.parse(json.getAsString());
									} catch (ParseException e) {
										return null;
									}
								}
							})
						// .setDateFormat("yyyy-MM-dd")
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

	public Collection<MutiboMovie> findByName(String pattern)
	{
		Collection<MutiboMovie> f_results = new ArrayList<>();

		TmdbSearchResults 		f_search 		= tmdbApiClient.findByName(pattern, 1, apiKey);
		boolean			  		f_continue		= f_search.getTotal_results() > 0;
		List<TmdbSearchMovie>	f_tmdb_results	= new ArrayList<>();

		// merge a few pages
		while (f_continue)
		{
			f_tmdb_results.addAll(f_search.getResults());

			// next page
			if (f_search.getPage() > f_search.getTotal_pages() || f_search.getPage() > 3)
				f_continue = false;
			else
				f_search   = tmdbApiClient.findByName(pattern, f_search.getPage() + 1, apiKey);
		}

		// sort the results
		Collections.sort(f_tmdb_results, TmdbSearchMovie.Comparators.POPULARITY_DESC);

		// covert them to something we can work with
		for (TmdbSearchMovie f_movie : f_tmdb_results)
		{
			f_results.add(new MutiboMovie(String.valueOf(f_movie.getId()), f_movie.getTitle(), f_movie.getRelease_year(), ""));
		}

		return f_results;
	}

	// member variables
	private final RestAdapter		restAdapter;
	private final TmdbApiClient		tmdbApiClient;
	private final String			apiKey;

	@Resource
	private Environment environment;
	
}
