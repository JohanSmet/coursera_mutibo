/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.servlet.http.HttpServletResponse;
import mutibo.data.User;
import mutibo.data.UserRole;
import mutibo.google.GoogleTokenChecker;
import mutibo.repository.UserRepository;
import mutibo.security.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Redacted
 */
@RestController
public class LoginController
{
	@RequestMapping(method=RequestMethod.POST, value="/login/login-password")
	public LoginInfo loginPassword(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse httpResponse)
	{
		// find the user
		User user = userRepository.findByUsername(username);

		if (user == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		
		// check the password
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(password, user.getPassword()))
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}

		// add a token to the request
		tokenAuthenticationService.addAuthentication(httpResponse, user);
		return new LoginInfo(user.getUsername(), "OK");
	}

	@RequestMapping(method=RequestMethod.POST, value="/login/login-google")
	public LoginInfo loginGoogle(@RequestParam("googleToken") String googleToken, @RequestParam("username") String username, HttpServletResponse httpResponse)
	{
		String googleId = googleTokenChecker.checkForId(googleToken);

		// stop if it's an invalid token
		if (googleId == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new LoginInfo("", "FAILED");
		}

		// check if the user is already known
		User 	user = userRepository.findByGoogleId(googleId);
		String	result = "OK";

		// create a new user if necessary
		if (user == null) 
		{
			user = new User();
			user.setId(makeUniqueUserId(Long.valueOf(googleId.substring(1, 9))));
			user.setUsername(username);
			user.grantRole(UserRole.USER);
			user.setGoogleId(googleId);
			userRepository.save(user);
			result = "NEW";
		}

		// add a token to the request
		tokenAuthenticationService.addAuthentication(httpResponse, user);
		return new LoginInfo(user.getUsername(), result);
	}

	private Long makeUniqueUserId(Long startId)
	{
		Long newId = startId;

		// XXX goe plan Guido
		while(userRepository.findOne(newId) != null)
			++newId;
		
		return newId;
	}

	public static class LoginInfo
	{
		public LoginInfo()
		{
		}

		public LoginInfo(String nickName, String status)
		{
			this.status = status;
			this.nickName = nickName;
		}

		@JsonProperty("status")
		String status;

		@JsonProperty("nickName")
		String nickName;
	}

	// member variables	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GoogleTokenChecker	googleTokenChecker;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
}
