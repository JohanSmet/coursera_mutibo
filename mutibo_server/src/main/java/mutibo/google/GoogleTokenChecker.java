package mutibo.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Redacted
 */

@Service
public class GoogleTokenChecker
{
	private final List<String> 			mClientIDs;
    private final String 				mAudience;
    private final GoogleIdTokenVerifier mVerifier;
    private final JsonFactory 			mJFactory;
    private String 						mProblem = "Verification failed. (Time-out?)";

	@Autowired
    public GoogleTokenChecker(@Value("${google.clientids}") String clientIDs, @Value("${google.audience}") String audience) 
	{
        mClientIDs = Arrays.asList(clientIDs.split(","));
        mAudience  = audience;

        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new JacksonFactory();
        mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
    }

    public String checkForId(String tokenString) 
	{
        GoogleIdToken.Payload payload = null;
		String				  id 	  = null;

        try {
            GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
            if (mVerifier.verify(token)) 
			{
                GoogleIdToken.Payload tempPayload = token.getPayload();
                if (!tempPayload.getAudience().equals(mAudience))
                    mProblem = "Audience mismatch";
                else if (!mClientIDs.contains(tempPayload.getAuthorizedParty()))
                    mProblem = "Client ID mismatch";
                else
                    payload = tempPayload;
            }
        } catch (GeneralSecurityException e) {
            mProblem = "Security issue: " + e.getLocalizedMessage();
        } catch (IOException e) {
            mProblem = "Network problem: " + e.getLocalizedMessage();
        }

		if (payload != null)
		{
			id = payload.getSubject();
		}

		return id;
    }

    public String problem() {
        return mProblem;
    }
}