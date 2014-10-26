package mutibo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Configuration of the WebApp part 
 * @author Redacted
 */

@Configuration
@ComponentScan(basePackages = {"mutibo.controller"})
@EnableWebMvc
public class WebAppContext
{
	
}
