package mutibo;

import com.mongodb.Mongo;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import mutibo.data.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Database configuration
 * @author Redacted
 */
@Configuration
@EnableMongoRepositories(basePackages = "mutibo.repository")
public class PersistenceContext extends AbstractMongoConfiguration
{
	@Value("${db.host}")
	private String confHost;

	@Value("${db.user}")
	private String confUser;

	@Value("${db.passwd}")
	private String confPasswd;

	@Value("${db.database}")
	private String confDatabase;

	@Override
  	public String getDatabaseName() 
	{
    	return confDatabase;
	}

	@Override
	public UserCredentials getUserCredentials()
	{
		return new UserCredentials(confUser, confPasswd);
	}

	@Override
  	public @Bean Mongo mongo() throws UnknownHostException
	{
    	return new Mongo(confHost);
  	}

	@Bean
	@Override
	public CustomConversions customConversions() 
	{
		List<Object>	converters = new ArrayList<>();
		converters.add(new User.ReadConverter());
		converters.add(new User.WriteConverter());
		return new CustomConversions(converters);
	}
}
