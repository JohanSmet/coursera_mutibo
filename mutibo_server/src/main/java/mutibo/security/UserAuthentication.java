/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutibo.security;

import java.util.Collection;
import mutibo.data.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Redacted
 */

public class UserAuthentication implements Authentication 
{
	private final User user;
	private boolean authenticated = true;

	public UserAuthentication(User user) 
	{
		this.user = user;
	}

	@Override
	public String getName() 
	{
		return user.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() 
	{
		return user.getAuthorities();
	}

	@Override
	public Object getCredentials() 
	{
		return user.getPassword();
	}

	@Override
	public User getDetails() 
	{
		return user;
	}

	@Override
	public Object getPrincipal() 
	{
		return user.getUsername();
	}

	@Override
	public boolean isAuthenticated() 
	{
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) 
	{
		this.authenticated = authenticated;
	}
}