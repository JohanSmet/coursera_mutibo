package mutibo;

import com.mongodb.Mongo;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import mutibo.data.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Database configuration
 * @author Redacted
 */
@Configuration
@EnableMongoRepositories(basePackages = "mutibo.repository")
public class PersistenceContext extends AbstractMongoConfiguration
{
	private static final String PROPERTY_NAME_DATABASE_HOST = "db.host";

	@Resource
	private Environment environment;

	@Override
  	public String getDatabaseName() 
	{
    	return "mutibo";
	}
	/*public @Bean MongoTemplate mongoTemplate(Mongo mongo)
	{
    	return new MongoTemplate(mongo, "mutibo");
  	}*/

	@Override
  	public @Bean Mongo mongo() throws UnknownHostException
	{
    	return new Mongo(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_HOST));
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
