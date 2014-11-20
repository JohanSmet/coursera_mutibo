package mutibo.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 *
 * @author Redacted
 */

@Service
public class FacebookTokenChecker
{
	private interface RestClient
    {
		@GET("/{id}")
		public FbGraphUser getInfo(@Path("id") String id, @Query("access_token") String accessToken);
	}

	private class FbGraphUser
	{
		public FbGraphUser()
		{
		}

		// just the fields that could be useful to us
		@JsonProperty("id")
		String id;

		@JsonProperty("first_name")
		String first_name;
	}
	
	public FacebookTokenChecker()
	{
		restClient = new RestAdapter.Builder()
                                .setEndpoint("https://graph.facebook.com/")
                                .build()
                                    .create(RestClient.class)
                            ;
	}
	
	public boolean validateId(String id, String accessToken)
	{
		try {
			FbGraphUser fbUser = restClient.getInfo(id, accessToken);
			return fbUser != null && id.equals(fbUser.id);
		} catch (Exception e) {
			return false;
		}
	}

	RestClient	restClient;
}
