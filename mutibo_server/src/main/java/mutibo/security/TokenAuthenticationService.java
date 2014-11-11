package mutibo.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import mutibo.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 *
 * @author Redacted
 */

@Service
public class TokenAuthenticationService 
{
	@Autowired
	public TokenAuthenticationService(@Value("${token.secret}") String secret) 
	{
		tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(secret));
	}

	public void addAuthentication(HttpServletResponse response, User user) 
	{
		user.setExpires(System.currentTimeMillis() + TEN_DAYS);
		response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
	}

	public Authentication getAuthentication(HttpServletRequest request) 
	{
		final String token = request.getHeader(AUTH_HEADER_NAME);
		
		if (token != null) 
		{
			final User user = tokenHandler.parseUserFromToken(token);

			if (user != null) 
			{
				return new UserAuthentication(user);
			} 
		}

		return null;
	}

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
	private static final long   TEN_DAYS = 1000 * 60 * 60 * 24 * 10;
	
	private final TokenHandler tokenHandler;
}
