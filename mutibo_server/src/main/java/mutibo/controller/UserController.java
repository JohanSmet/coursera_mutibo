/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import java.util.Date;
import java.util.Random;
import mutibo.data.MutiboUserResult;
import mutibo.data.User;
import mutibo.data.UserRole;
import mutibo.repository.MutiboUserResultRepository;
import mutibo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Redacted
 */
@RestController
public class UserController
{
	@PreAuthorize ("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.GET, value="/user")
	public Iterable<User> listAll()
	{
		return userRepository.findAll();
	}

	@RequestMapping(method=RequestMethod.POST, value="/user/create-default")
	public void createDefault()
	{
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		User user = new User();
		user.setUsername("admin");
		user.setPassword(encoder.encode("password"));
		user.grantRole(UserRole.ADMIN);
		user.grantRole(UserRole.USER);
		user.setGoogleId("105510070566529232484");
		userRepository.save(user);

		user = new User();
		user.setUsername("player");
		user.setPassword(encoder.encode("password"));
		user.grantRole(UserRole.USER);
		userRepository.save(user);

		Random rand = new Random();

		for (int idx=100;idx<400;++idx)
		{
			user = new User();
			user.setUsername(String.format("demo%.3s", idx));
			user.setPassword("");
			user.grantRole(UserRole.USER);
			userRepository.save(user);
			
			MutiboUserResult userResult = new MutiboUserResult(user.getUserId(), user.getUsername());
			userResult.setDateRegistered(new Date());
			userResult.setDateLastPlayed(new Date());
			userResult.setPlayedGames(rand.nextInt(20));
			userResult.setTotalScore(rand.nextInt(300));
			userResult.setBestScore(rand.nextInt(30));
			mutiboUserSetRepository.save(userResult);
		}
	}

	// member variables	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MutiboUserResultRepository mutiboUserSetRepository;
}
