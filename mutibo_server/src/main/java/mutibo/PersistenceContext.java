package mutibo;

import com.mongodb.Mongo;
import java.net.UnknownHostException;
import javax.annotation.Resource;
import mutibo.repository.MutiboMovieRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Database configuration
 * @author Redacted
 */
@Configuration
@EnableMongoRepositories(basePackages = "mutibo.repository",
 					     includeFilters = @ComponentScan.Filter(value = {MutiboMovieRepository.class}, type = FilterType.ASSIGNABLE_TYPE))
public class PersistenceContext
{
	private static final String PROPERTY_NAME_DATABASE_HOST = "db.host";

	@Resource
	private Environment environment;
	
	public @Bean MongoTemplate mongoTemplate(Mongo mongo)
	{
    	return new MongoTemplate(mongo, "mutibo");
  	}

  	public @Bean Mongo mongo() throws UnknownHostException
	{
    	return new Mongo(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_HOST));
  	}
}
