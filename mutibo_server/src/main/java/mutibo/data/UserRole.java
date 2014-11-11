package mutibo.data;

/**
 *
 * @author Redacted
 */

public enum UserRole 
{
	USER, ADMIN;

	public UserAuthority asAuthorityFor(final User user) 
	{
		final UserAuthority authority = new UserAuthority();
		authority.setAuthority("ROLE_" + toString());
		authority.setUser(user);
		return authority;
	}

	public static UserRole valueOf(final UserAuthority authority) 
	{
		return toEnum(authority.getAuthority());
	}

	public static UserRole toEnum(final String authority)
	{
		switch (authority)
		{
			case "ROLE_USER":
				return USER;
			case "ROLE_ADMIN":
				return ADMIN;
		}

		throw new IllegalArgumentException("No role defined for authority: " + authority);
	}
}