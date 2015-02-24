package mutibo.google;

import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 *
 * @author Redacted
 */

@Service
public class GoogleCloudMessenger
{
	@Autowired 
	public GoogleCloudMessenger(@Value("${google.gcm.key}") final String authorizationKey, @Value("${google.gcm.server}") final String gcmEndpoint)
	{
		gcmRestClient = new RestAdapter.Builder()
                                .setEndpoint(gcmEndpoint)
                                .setRequestInterceptor(new RequestInterceptor()
                                {
                                    @Override
                                    public void intercept(RequestFacade request)
                                    {
                                        request.addHeader("Authorization", "key=" + authorizationKey);
                                    }
                                })
                                // .setLogLevel(RestAdapter.LogLevel.FULL)
                                .build()
                                    .create(GcmRestClient.class)
                        ;
		
	}

	public void sendMessage(String[] regIds, String type, Map<String, String> payload)
	{
		payload.put("type", type);
		
		try {
			Response response = gcmRestClient.sendMessage(new GcmMessage(regIds, payload));
		} catch (RetrofitError e) {
			LoggerFactory.getLogger("GoogleCloudMessenger").warn(e.getMessage());
		}
	}

	private interface GcmRestClient
	{
		@POST("/gcm/send")
		Response sendMessage(@Body GcmMessage message);
	}

	private static class GcmMessage
	{
		public GcmMessage()
		{
		}

		public GcmMessage(String[] registration_ids, Map<String, String> payload)
		{
			this.registration_ids = registration_ids;
			this.data			  = payload;
		}

		public String[] getRegistration_ids()
		{
			return registration_ids;
		}

		public void setRegistration_ids(String[] registration_ids)
		{
			this.registration_ids = registration_ids;
		}

		public Map<String, String> getData()
		{
			return data;
		}

		public void setData(Map<String, String> data)
		{
			this.data = data;
		}

		String[]			registration_ids;
		Map<String, String>	data;
	}

	private GcmRestClient	gcmRestClient;
}
