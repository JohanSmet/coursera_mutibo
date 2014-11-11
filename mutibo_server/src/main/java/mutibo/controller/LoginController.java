/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import javax.servlet.http.HttpServletResponse;
import mutibo.data.User;
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
	public void sync(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse httpResponse)
	{
		// find the user
		User user = userRepository.findByUsername(username);

		if (user == null)
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		// check the password
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(password, user.getPassword()))
		{
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		// add a token to the request
		tokenAuthenticationService.addAuthentication(httpResponse, user);
	}

	// member variables	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
}
