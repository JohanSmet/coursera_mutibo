package mutibo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Redacted
 */
@Configuration
@Import({WebAppContext.class, PersistenceContext.class})
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
public class Application 
{
	/**
	 * Main entry method of the application
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) 
	{
        SpringApplication.run(Application.class, args);
    }
}
