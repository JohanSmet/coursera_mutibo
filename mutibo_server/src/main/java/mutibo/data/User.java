package mutibo.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Redacted
 */

@Document(collection = "users")
public class User implements UserDetails 
{
	public User()
	{
	}

	public ObjectId getId()
	{
		return id;
	}

	protected void setId(ObjectId id)
	{
		this.id = id;
	}

	@Override
	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}

	@Override
	@JsonIgnore
	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	@JsonIgnore
	public String getGoogleId()
	{
		return googleId;
	}

	public void setGoogleId(String googleId)
	{
		this.googleId = googleId;
	}

	public String getFacebookId()
	{
		return facebookId;
	}

	public void setFacebookId(String facebookId)
	{
		this.facebookId = facebookId;
	}

	public long getExpires()
	{
		return expires;
	}

	public void setExpires(long expires)
	{
		this.expires = expires;
	}

	@Override
	@JsonIgnore
	public Set<UserAuthority> getAuthorities() 
	{
		return authorities;
	}

	// Use Roles as external API
	public Set<UserRole> getRoles() 
	{
		Set<UserRole> roles = EnumSet.noneOf(UserRole.class);

		if (authorities != null) 
		{
			for (UserAuthority authority : authorities) 
			{
				roles.add(UserRole.valueOf(authority));
			}
		}
	
		return roles;
	}

	public void setRoles(Set<UserRole> roles) 
	{
		for (UserRole role : roles) 
		{
			grantRole(role);
		}
	}

	public void grantRole(UserRole role) 
	{
		if (authorities == null) 
		{
			authorities = new HashSet<>();
		}
		
		authorities.add(role.asAuthorityFor(this));
	}

	public void revokeRole(UserRole role) 
	{
		if (authorities != null) 
		{
			authorities.remove(role.asAuthorityFor(this));
		}
	}
	
	public boolean hasRole(UserRole role) 
	{
		return authorities.contains(role.asAuthorityFor(this));
	}
	
	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() 
	{
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() 
	{
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled()
	{
		return true;
	}
	
	// Mongo converters
	static public class ReadConverter implements Converter<DBObject, User> 
	{
		@Override
  		public User convert(DBObject source) 
		{
			User u = new User();
			u.setId((ObjectId) source.get("_id"));
			u.setUsername((String) source.get("username"));
			u.setPassword((String) source.get("password"));
			u.setGoogleId((String) source.get("googleId"));
			u.setFacebookId((String) source.get("facebookId"));

			if (source.containsField("roles"))
			{
				BasicDBList roles = (BasicDBList) source.get("roles");
				
				for (Object role : roles)
				{
					u.grantRole(UserRole.toEnum((String) role));
				}
			}

    		return u;
  		}
	}
	
	static public class WriteConverter implements Converter<User, DBObject> 
	{
		@Override
  		public DBObject convert(User source) 
		{
    		DBObject dbo = new BasicDBObject();
    		dbo.put("_id", 			source.getId());
    		dbo.put("username", 	source.getUsername());
			dbo.put("password", 	source.getPassword());
			dbo.put("googleId", 	source.getGoogleId());
			dbo.put("facebookId", 	source.getFacebookId());

			if (source.authorities != null) 
			{
				BasicDBList roles = new BasicDBList();

				for (UserAuthority authority : source.authorities) 
				{
					roles.add(authority.getAuthority());
				}
				
				dbo.put("roles", roles);
			}

    		return dbo;
  		}
	}

	// member variables
	@Id
	private ObjectId			id;
	private String				username;
	private String				password;
	private String				googleId;
	private String				facebookId;
	private Set<UserAuthority> 	authorities;

	@Transient
	private long 				expires;
}
