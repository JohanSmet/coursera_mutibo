package mutibo.themoviedb;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 *
 * @author Redacted
 */
public interface TmdbApiClient
{
	@GET("/3/find/{id}?external_source=imdb_id")
	public TmdbFindResults findByImdbId(@Path("id") String imdbId, @Query("api_key") String apiKey);

	@GET("/3/movie/{id}")
	public TmdbMovie findById(@Path("id") int tmdbId, @Query("api_key") String apiKey);

}
