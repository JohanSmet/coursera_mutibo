/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.controller;

import mutibo.data.User;
import mutibo.data.UserRole;
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
	public User createDefault()
	{
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		User user = new User();
		user.setId(1L);
		user.setUsername("admin");
		user.setPassword(encoder.encode("password"));
		user.grantRole(UserRole.ADMIN);
		user.grantRole(UserRole.USER);
		userRepository.save(user);

		user = new User();
		user.setId(2L);
		user.setUsername("player");
		user.setPassword(encoder.encode("password"));
		user.grantRole(UserRole.USER);
		userRepository.save(user);

		return user;
	}

	// member variables	
	@Autowired
	private UserRepository userRepository;
}
