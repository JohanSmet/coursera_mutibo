package mutibo;

import javax.annotation.Resource;
import mutibo.themoviedb.TmdbApi;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Redacted
 */
@Configuration
@Import({WebAppContext.class, PersistenceContext.class, SecurityContext.class})
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
@EnableScheduling
@ComponentScan(basePackages = {"mutibo.themoviedb", "mutibo.background"})
public class Application 
{
	private static final String PROPERTY_NAME_TMDB_HOST 		= "tmdb.host";
	private static final String PROPERTY_NAME_TMDB_IMAGE_HOST 	= "tmdb.imagehost";
	private static final String PROPERTY_NAME_TMDB_API_KEY  	= "tmdb.apikey";

	/**
	 * Main entry method of the application
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) 
	{
        SpringApplication.run(Application.class, args);
    }

	@Bean
	TmdbApi createTmdbApi()
	{
		 return new TmdbApi(environment.getRequiredProperty(PROPERTY_NAME_TMDB_HOST),
				 			environment.getRequiredProperty(PROPERTY_NAME_TMDB_IMAGE_HOST),
							environment.getRequiredProperty(PROPERTY_NAME_TMDB_API_KEY));
	}

	@Resource
	private Environment environment;
}
