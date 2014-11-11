package mutibo.themoviedb;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 *
 * @author Redacted
 */
public interface TmdbImageClient
{
	@GET("/t/p/{resolution}/{image})")
	public Response moviePoster(@Path("resolution") String resolution, @Path("image") String image, @Query("api_key") String apiKey);
}
